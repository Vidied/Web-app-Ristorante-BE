package davidepan.capstone.printing;

import davidepan.capstone.exceptions.PrinterException;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.util.Arrays;

public class SystemPrinterConnection implements PrinterConnection {

    private final String printerName;

    public SystemPrinterConnection(String printerName) {
        this.printerName = printerName;
    }

    @Override
    public void print(byte[] data) throws PrinterException {
        PrintService service = findPrintService();
        if (service == null) {
            throw new PrinterException("Stampante di sistema non trovata: " + printerName);
        }
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(data, flavor, null);
        DocPrintJob job = service.createPrintJob();
        try {
            job.print(doc, new HashPrintRequestAttributeSet());
        } catch (PrintException e) {
            throw new PrinterException("Errore di stampa su " + printerName, e);
        }
    }

    @Override
    public boolean isAvailable() {
        return findPrintService() != null;
    }

    private PrintService findPrintService() {
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .filter(s -> s.getName().equalsIgnoreCase(printerName))
                .findFirst()
                .orElse(null);
    }
}