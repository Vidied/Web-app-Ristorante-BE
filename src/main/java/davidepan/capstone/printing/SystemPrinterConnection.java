package davidepan.capstone.printing;

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

    public SystemPrinterConnection(String printerName) {
        this.printerName = printerName;
    }

    @Override
    public void print(byte[] data) {
        PrintService targetService = findPrintService(printerName);

        if (targetService == null) {
            log.error("STAMPANTE NON TROVATA: '{}'. Stampanti rilevate in Windows: {}",
                    printerName, getAvailablePrinterNames());
            throw new RuntimeException("Stampante Windows non trovata: " + printerName);
        }

        try {
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

            log.info("Comanda inviata con successo alla stampante USB: {}", printerName);
        } catch (PrintException e) {
            log.error("Errore dello Spooler di Windows per la stampante {}", printerName, e);
            throw new RuntimeException("Errore di stampa su " + printerName, e);
        }
    }

    @Override
    public boolean isAvailable() {
        return findPrintService(printerName) != null;
    }

    private PrintService findPrintService(String name) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        return Arrays.stream(printServices)
                .filter(p -> p.getName().trim().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    private String getAvailablePrinterNames() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        return Arrays.stream(printServices)
                .map(PrintService::getName)
                .toList()
                .toString();
    }
}