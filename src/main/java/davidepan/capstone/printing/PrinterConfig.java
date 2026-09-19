package davidepan.capstone.printing;

import davidepan.capstone.enums.DestinationArea;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PrinterConfig {

    @Bean
    public SerialPrinterConnection pizzeriaPrinterConnection(
            @Value("${printer.pizzeria.port}") String port,
            @Value("${printer.pizzeria.name-hint}") String nameHint,
            @Value("${printer.pizzeria.max-retries:3}") int maxRetries,
            @Value("${printer.pizzeria.retry-delay-ms:1500}") long retryDelayMillis) {
        return new SerialPrinterConnection(port, nameHint, maxRetries, retryDelayMillis);
    }

    @Bean
    public SystemPrinterConnection cucinaPrinterConnection(
            @Value("${printer.cucina.name}") String name) {
        return new SystemPrinterConnection(name);
    }

    @Bean
    public SystemPrinterConnection salaPrinterConnection(
            @Value("${printer.sala.name}") String name) {
        return new SystemPrinterConnection(name);
    }

    @Bean
    public Map<DestinationArea, PrinterConnection> printerConnections(
            SerialPrinterConnection pizzeriaPrinterConnection,
            SystemPrinterConnection cucinaPrinterConnection,
            SystemPrinterConnection salaPrinterConnection) {
        return Map.of(
                DestinationArea.PIZZERIA, pizzeriaPrinterConnection,
                DestinationArea.CUCINA, cucinaPrinterConnection,
                DestinationArea.SALA, salaPrinterConnection
        );
    }

    @Bean
    public SystemPrinterConnection receiptPrinterConnection(
            @Value("${printer.receipt.name}") String name) {
        return new SystemPrinterConnection(name);
    }
}