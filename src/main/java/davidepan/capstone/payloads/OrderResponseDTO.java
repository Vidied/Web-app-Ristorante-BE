package davidepan.capstone.payloads;

import davidepan.capstone.enums.OrderStatus;
import davidepan.capstone.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Integer tableNumber,
        Integer coverCount,
        OrderType orderType,
        LocalDateTime createdAt,
        OrderStatus orderStatus,
        String notes,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items
) {}