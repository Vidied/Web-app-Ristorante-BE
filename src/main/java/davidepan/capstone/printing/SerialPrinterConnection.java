package davidepan.capstone.printing;

import com.fazecast.jSerialComm.SerialPort;
import davidepan.capstone.exceptions.PrinterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SerialPrinterConnection implements PrinterConnection {

    private static final Logger log = LoggerFactory.getLogger(SerialPrinterConnection.class);

    private static final String MAC_HINT_MESSAGE =
            "Se il MAC configurato nel .env non è quello giusto, aggiornalo con uno dei MAC elencati nel dump sopra.";

    private final String label;
    private final String configuredPort;
    private final String descriptiveNameHint;
    private final String macAddress;
    private final int maxRetries;
    private final long retryDelayMillis;

    public SerialPrinterConnection(String label, String configuredPort, String descriptiveNameHint,
                                   int maxRetries, long retryDelayMillis) {
        this(label, configuredPort, descriptiveNameHint, null, maxRetries, retryDelayMillis);
    }

    public SerialPrinterConnection(String label, String configuredPort, String descriptiveNameHint,
                                   String macAddress, int maxRetries, long retryDelayMillis) {
        this.label = label;
        this.configuredPort = configuredPort == null ? "" : configuredPort.trim();
        this.descriptiveNameHint = descriptiveNameHint == null ? "" : descriptiveNameHint.trim();
        this.macAddress = normalizeMac(macAddress);
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    private static String normalizeMac(String mac) {
        if (mac == null) return "";
        return mac.trim().replace(":", "").replace("-", "").toUpperCase(Locale.ROOT);
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

    public static String dumpAvailablePorts() {
        Map<String, String> comToMac = ComPortMacLookup.getComToMac();
        SerialPort[] ports = SerialPort.getCommPorts();
        StringBuilder dump = new StringBuilder();
        if (ports.length == 0) {
            dump.append("  nessuna porta seriale rilevata");
        } else {
            for (SerialPort port : ports) {
                if (dump.length() > 0) dump.append('\n');
                String mac = comToMac.get(port.getSystemPortName());
                dump.append("  - SystemPortName=").append(port.getSystemPortName())
                        .append(" | MAC=").append(mac != null ? mac : "unknown")
                        .append(" | DescriptivePortName=").append(safe(port.getDescriptivePortName()))
                        .append(" | PortDescription=").append(safe(port.getPortDescription()))
                        .append(" | PortLocation=").append(safe(port.getPortLocation()));
            }
        }
        return dump.toString();
    }

    private SerialPort openWithRetry() throws PrinterException {
        PrinterException lastFailure = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            List<String> candidates = resolvePortCandidates();

            if (candidates.isEmpty()) {
                log.warn("Stampante {}: nessuna porta candidata rilevata.", label);
                log.warn("Porte visibili in questo momento:\n{}", dumpAvailablePorts());
                log.warn(MAC_HINT_MESSAGE);
                lastFailure = new PrinterException("Stampante " + label
                        + ": nessuna porta candidata rilevata (porta configurata " + configuredPort + ")");
            } else {
                for (String portName : candidates) {
                    SerialPort port = preparePort(portName);

                    if (port.openPort()) {
                        if (attempt > 1) {
                            log.info("Stampante {} raggiunta al tentativo {}/{} su {}",
                                    label, attempt, maxRetries, portName);
                        }
                        return port;
                    }

                    ComPortMacLookup.invalidateCache();
                    lastFailure = new PrinterException("Impossibile aprire la porta " + portName
                            + " (tentativo " + attempt + "/" + maxRetries + ")");
                    log.warn(lastFailure.getMessage());
                }

                log.warn("Stampante {}: nessuna delle porte candidate si è aperta.", label);
                log.warn("Porte visibili in questo momento:\n{}", dumpAvailablePorts());
                log.warn(MAC_HINT_MESSAGE);
            }

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
                "Stampante " + label + " offline dopo " + maxRetries + " tentativi", lastFailure);
    }

    private List<String> resolvePortCandidates() {
        List<String> matchesMac = new ArrayList<>();
        List<String> matchesOutgoing = new ArrayList<>();
        List<String> matchesAny = new ArrayList<>();
        boolean configuredExists = false;
        String hint = descriptiveNameHint.toLowerCase(Locale.ROOT);
        Map<String, String> comToMac = macAddress.isEmpty() ? Map.of() : ComPortMacLookup.getComToMac();

        for (SerialPort port : SerialPort.getCommPorts()) {
            String systemPortName = port.getSystemPortName();

            if (!configuredPort.isEmpty() && configuredPort.equalsIgnoreCase(systemPortName)) {
                configuredExists = true;
            }

            if (!macAddress.isEmpty()) {
                String portMac = comToMac.get(systemPortName);
                if (macAddress.equalsIgnoreCase(portMac) && !matchesMac.contains(systemPortName)) {
                    matchesMac.add(systemPortName);
                }
            }

            if (hint.isEmpty()) continue;

            String description = descriptiveText(port).toLowerCase(Locale.ROOT);
            if (!description.contains(hint)) continue;

            if (!matchesAny.contains(systemPortName)) matchesAny.add(systemPortName);

            boolean incoming = description.contains("incoming") || description.contains("in ingresso");
            if (!incoming && !matchesOutgoing.contains(systemPortName)) matchesOutgoing.add(systemPortName);
        }

        List<String> candidates = new ArrayList<>(matchesMac);
        for (String portName : matchesOutgoing) {
            if (!candidates.contains(portName)) candidates.add(portName);
        }
        for (String portName : matchesAny) {
            if (!candidates.contains(portName)) candidates.add(portName);
        }
        if (configuredExists && !candidates.contains(configuredPort)) {
            candidates.add(configuredPort);
        }
        return candidates;
    }

    private SerialPort preparePort(String portName) {
        SerialPort port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 3000, 3000);
        return port;
    }

    private static String descriptiveText(SerialPort port) {
        return safe(port.getDescriptivePortName()) + " " + safe(port.getPortDescription())
                + " " + safe(port.getPortLocation());
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
