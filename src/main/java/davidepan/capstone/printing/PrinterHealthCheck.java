package davidepan.capstone.printing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PrinterHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(PrinterHealthCheck.class);

    private final Map<String, PrinterConnection> serialConnections;
    private final Map<String, Boolean> lastKnownAvailable = new ConcurrentHashMap<>();

    public PrinterHealthCheck(
            @Qualifier("pizzeriaPrinterConnection") PrinterConnection pizzeriaConnection,
            @Qualifier("cucinaPrinterConnection") PrinterConnection cucinaConnection) {
        this.serialConnections = Map.of(
                "pizzeria", pizzeriaConnection,
                "cucina", cucinaConnection
        );
        serialConnections.keySet().forEach(name -> lastKnownAvailable.put(name, true));
    }

    @Scheduled(fixedRateString = "${printer.health-check-interval-ms:180000}")
    public void checkConnections() {
        serialConnections.forEach((name, connection) -> {
            boolean available = connection.isAvailable();
            boolean wasAvailable = lastKnownAvailable.getOrDefault(name, true);

            if (available && !wasAvailable) {
                log.info("Stampante {} tornata raggiungibile.", name);
            } else if (!available && wasAvailable) {
                log.warn("Stampante {} non raggiungibile (controllo periodico).", name);
            }

            lastKnownAvailable.put(name, available);
        });
    }
}