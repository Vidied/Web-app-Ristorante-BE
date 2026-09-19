package davidepan.capstone.printing;

import davidepan.capstone.exceptions.PrinterException;

public interface PrinterConnection {
    void print(byte[] data) throws PrinterException;
    boolean isAvailable();
}