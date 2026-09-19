package davidepan.capstone.printing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrinterHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(PrinterHealthCheck.class);

    private final PrinterConnection pizzeriaConnection;
    private boolean lastKnownAvailable = true;

    public PrinterHealthCheck(@Qualifier("pizzeriaPrinterConnection") PrinterConnection pizzeriaConnection) {
        this.pizzeriaConnection = pizzeriaConnection;
    }

    @Scheduled(fixedRateString = "${printer.pizzeria.health-check-interval-ms:180000}")
    public void checkPizzeriaConnection() {
        boolean available = pizzeriaConnection.isAvailable();

        if (available && !lastKnownAvailable) {
            log.info("Stampante pizzeria tornata raggiungibile.");
        } else if (!available && lastKnownAvailable) {
            log.warn("Stampante pizzeria non raggiungibile (controllo periodico).");
        }

        lastKnownAvailable = available;
    }
}