package davidepan.capstone.printing;

import davidepan.capstone.exceptions.PrinterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class SystemPrinterConnection implements PrinterConnection {

    private static final Logger log = LoggerFactory.getLogger(SystemPrinterConnection.class);

    private final String printerName;
    private final int maxRetries;
    private final long retryDelayMillis;

    public SystemPrinterConnection(String printerName, int maxRetries, long retryDelayMillis) {
        this.printerName = printerName;
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public void print(byte[] data) throws PrinterException {
        PrinterException lastFailure = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            PrintService targetService = findPrintService();

            if (targetService != null) {
                try {
                    sendToPrinter(targetService, data);
                    if (attempt > 1) {
                        log.info("Stampante {} raggiunta al tentativo {}/{}", printerName, attempt, maxRetries);
                    }
                    return;
                } catch (PrintException e) {
                    lastFailure = new PrinterException("Errore dello spooler Windows su " + printerName, e);
                }
            } else {
                lastFailure = new PrinterException(
                        "Stampante non trovata: " + printerName
                                + " (tentativo " + attempt + "/" + maxRetries
                                + "). Stampanti rilevate in Windows: " + getAvailablePrinterNames());
            }

            log.warn(lastFailure.getMessage());

            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PrinterException("Stampa interrotta", ie);
                }
            }
        }

        throw new PrinterException(
                "Stampante " + printerName + " irraggiungibile dopo " + maxRetries + " tentativi", lastFailure);
    }

    private void sendToPrinter(PrintService targetService, byte[] data) throws PrintException {
        DocPrintJob job = targetService.createPrintJob();
        Doc doc;

        if (targetService.isDocFlavorSupported(DocFlavor.BYTE_ARRAY.AUTOSENSE)) {
            doc = new SimpleDoc(data, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        } else if (targetService.isDocFlavorSupported(DocFlavor.INPUT_STREAM.AUTOSENSE)) {
            InputStream is = new ByteArrayInputStream(data);
            doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        } else {
            log.warn("Formato AUTOSENSE non dichiarato per '{}', tentativo forzato...", printerName);
            doc = new SimpleDoc(data, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        }

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        job.print(doc, attributes);

        log.info("Stampa inviata con successo a: {}", printerName);
    }

    @Override
    public boolean isAvailable() {
        return findPrintService() != null;
    }


    private PrintService findPrintService() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        PrintService exactMatch = Arrays.stream(services)
                .filter(p -> p.getName().trim().equalsIgnoreCase(printerName.trim()))
                .findFirst()
                .orElse(null);

        if (exactMatch != null) {
            return exactMatch;
        }


        PrintService fuzzyMatch = Arrays.stream(services)
                .filter(p -> p.getName().toUpperCase().contains(printerName.trim().toUpperCase()))
                .findFirst()
                .orElse(null);

        if (fuzzyMatch != null) {
            log.warn("Trovata '{}' solo per corrispondenza parziale col nome configurato '{}' " +
                            "— probabile coda duplicata in Windows da ripulire.",
                    fuzzyMatch.getName(), printerName);
        }

        return fuzzyMatch;
    }

    private String getAvailablePrinterNames() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        return Arrays.stream(printServices)
                .map(PrintService::getName)
                .toList()
                .toString();
    }
}