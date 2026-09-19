package davidepan.capstone.payloads;

import davidepan.capstone.enums.DestinationArea;
import java.math.BigDecimal;
import java.util.List;

public record OrderItemResponseDTO(
        Long id,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal takeawayUnitPrice,
        String notes,
        DestinationArea destinationArea,
        List<OrderItemExtraResponseDTO> extras
) {}