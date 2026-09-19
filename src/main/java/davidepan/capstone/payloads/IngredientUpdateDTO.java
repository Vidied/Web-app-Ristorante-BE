package davidepan.capstone.payloads;

import java.math.BigDecimal;

public record IngredientUpdateDTO(
        String name,
        Boolean isAvailable,
        BigDecimal extraPrice
) {
}