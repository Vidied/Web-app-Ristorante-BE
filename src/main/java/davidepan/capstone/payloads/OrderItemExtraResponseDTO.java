package davidepan.capstone.payloads;

import java.math.BigDecimal;

public record OrderItemExtraResponseDTO(
        String ingredientName,
        BigDecimal price
) {
}