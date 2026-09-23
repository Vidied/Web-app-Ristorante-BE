package davidepan.capstone.printing;

import davidepan.capstone.enums.DestinationArea;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public SerialPrinterConnection cucinaPrinterConnection(
            @Value("${printer.cucina.port}") String port,
            @Value("${printer.cucina.name-hint}") String nameHint,
            @Value("${printer.cucina.max-retries:3}") int maxRetries,
            @Value("${printer.cucina.retry-delay-ms:1500}") long retryDelayMillis) {
        return new SerialPrinterConnection(port, nameHint, maxRetries, retryDelayMillis);
    }

    @Bean
    public SystemPrinterConnection salaPrinterConnection(
            @Value("${printer.sala.name}") String name,
            @Value("${printer.sala.max-retries:3}") int maxRetries,
            @Value("${printer.sala.retry-delay-ms:1500}") long retryDelayMillis) {
        return new SystemPrinterConnection(name, maxRetries, retryDelayMillis);
    }

    @Bean
    public Map<DestinationArea, PrinterConnection> printerConnections(
            @Qualifier("pizzeriaPrinterConnection") SerialPrinterConnection pizzeriaPrinterConnection,
            @Qualifier("cucinaPrinterConnection") SerialPrinterConnection cucinaPrinterConnection,
            SystemPrinterConnection salaPrinterConnection) {
        return Map.of(
                DestinationArea.PIZZERIA, pizzeriaPrinterConnection,
                DestinationArea.CUCINA, cucinaPrinterConnection,
                DestinationArea.SALA, salaPrinterConnection
        );
    }

    @Bean
    public SystemPrinterConnection receiptPrinterConnection(
            @Value("${printer.receipt.name}") String name,
            @Value("${printer.receipt.max-retries:3}") int maxRetries,
            @Value("${printer.receipt.retry-delay-ms:1500}") long retryDelayMillis) {
        return new SystemPrinterConnection(name, maxRetries, retryDelayMillis);
    }
}