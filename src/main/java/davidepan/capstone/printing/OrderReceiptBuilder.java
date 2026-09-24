package davidepan.capstone.printing;

import davidepan.capstone.entities.Order;
import davidepan.capstone.entities.OrderItem;
import davidepan.capstone.entities.OrderItemExtra;
import davidepan.capstone.enums.DestinationArea;
import davidepan.capstone.enums.OrderType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderReceiptBuilder {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final int LINE_WIDTH = 48;
    private static final int HEADER_LINE_WIDTH = LINE_WIDTH / 2;
    private static final int PRICE_COL_WIDTH = 8;
    private static final int NAME_COL_WIDTH = LINE_WIDTH - PRICE_COL_WIDTH;
    private static final String SEPARATOR = "-".repeat(LINE_WIDTH);
    private static final int FEED_BEFORE_CUT = 6;
    private static final int MIN_TICKET_LINES = 10;

    public byte[] build(Order order, DestinationArea area, List<OrderItem> items, boolean isAddition) {
        EscPosCommandBuilder b = new EscPosCommandBuilder().init();

        String headerLabel = isAddition ? area.name() + " - AGGIUNTA" : area.name();
        b.alignCenter().characterSize(2, 2).bold(true)
                .text(centerWithDashes(headerLabel, 3, HEADER_LINE_WIDTH))
                .resetCharacterSize().bold(false).alignLeft().newLine();

        b.characterSize(2, 1).bold(true);
        if (order.getTableNumber() != null) {
            b.text("TAVOLO " + order.getTableNumber()).newLine();
        }
        b.text(order.getOrderType() != null ? order.getOrderType().name() : "");
        b.text(" - " + TIME_FORMAT.format(order.getCreatedAt()));
        b.newLine();
        b.resetCharacterSize().bold(false);

        b.text(SEPARATOR).newLine();

        if (order.getNotes() != null && !order.getNotes().isBlank()) {
            appendNotesBox(b, order.getNotes());
            b.text(SEPARATOR).newLine();
        }

        for (OrderItem item : items) {
            b.characterSize(2, 2).bold(true)
                    .text(item.getQuantity() + "x " + item.getProduct().getName())
                    .resetCharacterSize().bold(false).newLine();

            if (item.getExtras() != null) {
                for (OrderItemExtra extra : item.getExtras()) {
                    String sign = extra.getPrice().signum() < 0 ? "- " : "+ ";
                    b.text("   " + sign)
                            .bold(true).text(extra.getIngredientName()).bold(false)
                            .newLine();
                }
            }
            if (item.getNotes() != null && !item.getNotes().isBlank()) {
                b.text("   Nota: " + item.getNotes()).newLine();
            }
            if (item.getRemovedIngredients() != null) {
                for (String removed : item.getRemovedIngredients()) {
                    b.text("   NO ").bold(true).text(removed).bold(false).newLine();
                }
            }
        }

        b.beep(3, 2);

        b.padToMinimumLines(MIN_TICKET_LINES);
        b.feed(FEED_BEFORE_CUT).cut();
        return b.build();
    }

    public byte[] buildCustomerReceipt(Order order) {
        EscPosCommandBuilder b = new EscPosCommandBuilder().init();

        b.alignCenter().doubleHeight(true).bold(true)
                .text("SCONTRINO").newLine()
                .doubleHeight(false).bold(false).alignLeft();

       // b.text("Ordine #" + order.getId());
        if (order.getTableNumber() != null) {
            b.text("Tavolo " + order.getTableNumber());
        }
        b.newLine();
        b.text(order.getOrderType() != null ? order.getOrderType().name() : "").newLine();
        b.text(SEPARATOR).newLine();

        boolean isTakeaway = order.getOrderType() == OrderType.ASPORTO;

        for (OrderItem item : order.getItems()) {
            BigDecimal unitPrice = (isTakeaway
                    && item.getTakeawayUnitPrice() != null
                    && item.getTakeawayUnitPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? item.getTakeawayUnitPrice()
                    : item.getUnitPrice();

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            String label = item.getQuantity() + "x " + item.getProduct().getName();
            b.text(formatLine(label, lineTotal)).newLine();

            if (item.getExtras() != null) {
                for (OrderItemExtra extra : item.getExtras()) {
                    BigDecimal extraTotal = extra.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    b.text(formatLine("  + " + extra.getIngredientName(), extraTotal)).newLine();
                }
            }
            if (item.getRemovedIngredients() != null) {
                for (String removed : item.getRemovedIngredients()) {
                    b.text("  (senza " + removed + ")").newLine();
                }
            }
        }

        if (!isTakeaway && order.getCoverCount() != null && order.getCoverCount() > 0) {
            BigDecimal coverPrice = order.getCoverPrice() != null ? order.getCoverPrice() : BigDecimal.ZERO;
            BigDecimal coverTotal = coverPrice.multiply(BigDecimal.valueOf(order.getCoverCount()));

            b.text(SEPARATOR).newLine();
            b.text(formatLine("Coperto x" + order.getCoverCount(), coverTotal)).newLine();
        }

        b.text(SEPARATOR).newLine();
        b.bold(true);
        b.text(formatLine("TOTALE", order.getTotalAmount())).newLine();
        b.bold(false);

        b.text(SEPARATOR).newLine();
        b.newLine();
        b.alignCenter().bold(true);
        b.text("GRAZIE E ARRIVEDERCI").newLine();
        b.bold(false).alignLeft();


        b.padToMinimumLines(MIN_TICKET_LINES);


        b.beep(1, 1);
        b.feed(FEED_BEFORE_CUT).cut();
        return b.build();
    }

    public byte[] buildCancellation(Order order) {
        EscPosCommandBuilder b = new EscPosCommandBuilder().init();

        b.alignCenter().characterSize(2, 2).bold(true)
                .text(centerWithDashes("COMANDA CANCELLATA", 3, HEADER_LINE_WIDTH))
                .resetCharacterSize().bold(false).alignLeft().newLine();

        b.alignCenter().characterSize(2, 2).bold(true)
                .text("*** ANNULLATO ***")
                .resetCharacterSize().bold(false).alignLeft().newLine();

        b.characterSize(2, 1).bold(true);
        if (order.getTableNumber() != null) {
            b.text("TAVOLO " + order.getTableNumber()).newLine();
        }
        b.text(order.getOrderType() != null ? order.getOrderType().name() : "");
        b.text(" - " + TIME_FORMAT.format(order.getCreatedAt()));
        b.newLine();
        b.resetCharacterSize().bold(false);

        b.text(SEPARATOR).newLine();

        b.beep(3, 2);

        b.padToMinimumLines(MIN_TICKET_LINES);
        b.feed(FEED_BEFORE_CUT).cut();
        return b.build();
    }

    private String formatLine(String label, BigDecimal amount) {
        String truncatedLabel = truncate(label, NAME_COL_WIDTH);
        return String.format("%-" + NAME_COL_WIDTH + "s%" + PRICE_COL_WIDTH + ".2f",
                truncatedLabel, amount);
    }

    private String centerWithDashes(String label, int minDashesEachSide, int lineWidth) {
        int totalDashes = lineWidth - label.length() - 2;
        if (totalDashes < minDashesEachSide * 2) {
            totalDashes = minDashesEachSide * 2;
        }
        int left = totalDashes / 2;
        int right = totalDashes - left;
        return "-".repeat(left) + " " + label + " " + "-".repeat(right);
    }

    private void appendNotesBox(EscPosCommandBuilder b, String notes) {
        int innerWidth = LINE_WIDTH - 4;
        String border = "+" + "-".repeat(LINE_WIDTH - 2) + "+";

        b.text(border).newLine();
        b.bold(true).text(boxLine("NOTE ORDINE", innerWidth)).bold(false).newLine();
        b.text(border).newLine();
        for (String line : wrapText(notes, innerWidth)) {
            b.text(boxLine(line, innerWidth)).newLine();
        }
        b.text(border).newLine();
    }

    private String boxLine(String content, int innerWidth) {
        String padded = String.format("%-" + innerWidth + "s", truncate(content, innerWidth));
        return "| " + padded + " |";
    }

    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        String[] words = text.trim().split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.isEmpty()) {
                current.append(word);
            } else if (current.length() + 1 + word.length() <= width) {
                current.append(" ").append(word);
            } else {
                lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        List<String> safeLines = new ArrayList<>();
        for (String line : lines) {
            while (line.length() > width) {
                safeLines.add(line.substring(0, width));
                line = line.substring(width);
            }
            safeLines.add(line);
        }
        return safeLines;
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + ".";
    }
}