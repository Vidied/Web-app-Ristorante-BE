package davidepan.capstone.payloads;

import java.math.BigDecimal;

public record IngredientResponseDTO(
        Long id,
        String name,
        Boolean isAvailable,
        BigDecimal extraPrice
) {
}