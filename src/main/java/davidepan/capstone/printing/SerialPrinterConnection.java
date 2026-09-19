package davidepan.capstone.printing;

import com.fazecast.jSerialComm.SerialPort;
import davidepan.capstone.exceptions.PrinterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class SerialPrinterConnection implements PrinterConnection {

    private static final Logger log = LoggerFactory.getLogger(SerialPrinterConnection.class);

    private final String configuredPort;
    private final String descriptiveNameHint;
    private final int maxRetries;
    private final long retryDelayMillis;

    public SerialPrinterConnection(String configuredPort, String descriptiveNameHint,
                                   int maxRetries, long retryDelayMillis) {
        this.configuredPort = configuredPort;
        this.descriptiveNameHint = descriptiveNameHint;
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public void print(byte[] data) throws PrinterException {
        SerialPort port = openWithRetry();
        try (OutputStream out = port.getOutputStream()) {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new PrinterException("Errore di scrittura sulla porta " + port.getSystemPortName(), e);
        } finally {
            port.closePort();
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            SerialPort port = openWithRetry();
            port.closePort();
            return true;
        } catch (PrinterException e) {
            return false;
        }
    }

    private SerialPort openWithRetry() throws PrinterException {
        PrinterException lastFailure = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            String portName = resolvePortName();
            SerialPort port = SerialPort.getCommPort(portName);
            port.setBaudRate(9600);
            port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 3000, 3000);

            if (port.openPort()) {
                if (attempt > 1) {
                    log.info("Stampante pizzeria raggiunta al tentativo {}/{} su {}", attempt, maxRetries, portName);
                }
                return port;
            }

            lastFailure = new PrinterException(
                    "Impossibile aprire la porta " + portName + " (tentativo " + attempt + "/" + maxRetries + ")");
            log.warn(lastFailure.getMessage());

            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PrinterException("Apertura porta interrotta", ie);
                }
            }
        }

        throw new PrinterException(
                "Stampante pizzeria offline dopo " + maxRetries + " tentativi", lastFailure);
    }

    private String resolvePortName() {
        boolean configuredExists = Arrays.stream(SerialPort.getCommPorts())
                .anyMatch(p -> p.getSystemPortName().equalsIgnoreCase(configuredPort));
        if (configuredExists) return configuredPort;

        return Arrays.stream(SerialPort.getCommPorts())
                .filter(p -> p.getDescriptivePortName().toUpperCase().contains(descriptiveNameHint.toUpperCase()))
                .findFirst()
                .map(SerialPort::getSystemPortName)
                .orElseThrow(() -> new PrinterException(
                        "Stampante pizzeria non trovata né su " + configuredPort + " né tramite ricerca automatica"));
    }
}