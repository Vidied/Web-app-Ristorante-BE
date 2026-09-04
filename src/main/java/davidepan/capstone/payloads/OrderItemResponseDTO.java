package davidepan.capstone.payloads;

import davidepan.capstone.enums.DestinationArea;
import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        String notes,
        DestinationArea destinationArea
) {}