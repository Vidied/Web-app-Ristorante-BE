package davidepan.capstone.services;

import davidepan.capstone.entities.Order;
import davidepan.capstone.entities.OrderItem;
import davidepan.capstone.enums.DestinationArea;
import davidepan.capstone.payloads.PrintResultDTO;
import davidepan.capstone.printing.OrderReceiptBuilder;
import davidepan.capstone.printing.PrinterConnection;
import davidepan.capstone.exceptions.PrinterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderPrintService {

    private static final Logger log = LoggerFactory.getLogger(OrderPrintService.class);
    private final PrinterConnection receiptPrinterConnection;
    private final Map<DestinationArea, PrinterConnection> printerConnections;
    private final OrderReceiptBuilder receiptBuilder;

    public OrderPrintService(Map<DestinationArea, PrinterConnection> printerConnections,
                             OrderReceiptBuilder receiptBuilder,
                             @Qualifier("receiptPrinterConnection") PrinterConnection receiptPrinterConnection) {
        this.printerConnections = printerConnections;
        this.receiptBuilder = receiptBuilder;
        this.receiptPrinterConnection = receiptPrinterConnection;
    }



    public PrintResultDTO printCustomerReceipt(Order order) {
        try {
            byte[] receipt = receiptBuilder.buildCustomerReceipt(order);
            receiptPrinterConnection.print(receipt);
            return new PrintResultDTO(null, true, null);
        } catch (PrinterException e) {
            log.warn("Stampa scontrino fallita per ordine {}: {}", order.getId(), e.getMessage());
            return new PrintResultDTO(null, false, e.getMessage());
        }
    }



    public List<PrintResultDTO> printFullOrder(Order order) {
        return printItems(order, order.getItems(), false);
    }

            public void printCancellation(Order order) {
                byte[] ticket = receiptBuilder.buildCancellation(order);
        Set<DestinationArea> involvedAreas = order.getItems().stream()
                .map(i -> i.getProduct().getDestinationArea())
                .collect(Collectors.toSet());

        for (DestinationArea area : involvedAreas) {
            PrinterConnection connection = printerConnections.get(area);
            if (connection == null) {
                continue;
            }
            try {
                connection.print(ticket);
            } catch (PrinterException e) {
                log.warn("Stampa annullamento fallita per ordine {} su {}: {}",
                        order.getId(), area, e.getMessage());
            }
        }
    }


    public List<PrintResultDTO> printItems(Order order, List<OrderItem> items, boolean isAddition) {
        Map<DestinationArea, List<OrderItem>> byArea = items.stream()
                .collect(Collectors.groupingBy(i -> i.getProduct().getDestinationArea()));

        List<PrintResultDTO> results = new ArrayList<>();

                for (Map.Entry<DestinationArea, List<OrderItem>> entry : byArea.entrySet()) {
            DestinationArea area = entry.getKey();
            PrinterConnection connection = printerConnections.get(area);

            if (connection == null) {
                results.add(new PrintResultDTO(area, false, "Nessuna stampante configurata per " + area));
                continue;
            }
            try {
                byte[] receipt = receiptBuilder.build(order, area, entry.getValue(), isAddition);
                connection.print(receipt);
                results.add(new PrintResultDTO(area, true, null));
            } catch (PrinterException e) {
                log.warn("Stampa fallita per ordine {} su {}: {}", order.getId(), area, e.getMessage());
                results.add(new PrintResultDTO(area, false, e.getMessage()));
            }
        }
        return results;
    }


}

