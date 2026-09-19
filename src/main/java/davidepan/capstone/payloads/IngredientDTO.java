package davidepan.capstone.payloads;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record IngredientDTO(
        @NotBlank(message = "Il nome del'ingrediente è obbligatoria")
        String name,
        Boolean isAvailable,
        BigDecimal extraPrice
) {
}