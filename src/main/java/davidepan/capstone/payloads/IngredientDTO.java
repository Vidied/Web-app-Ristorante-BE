package davidepan.capstone.payloads;

import jakarta.validation.constraints.NotBlank;

public record IngredientDTO(
        @NotBlank(message = "Il nome del'ingrediente è obbligatoria")
        String name,
        Boolean isAvailable
) {
}
