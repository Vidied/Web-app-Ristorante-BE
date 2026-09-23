package davidepan.capstone.payloads;

import davidepan.capstone.enums.Allergen;
import davidepan.capstone.enums.DestinationArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductDTO(
        @NotBlank(message = "Il nome del prodotto è obbligatorio!")
        String name,
        String description,
        @NotNull(message = "Il prezzo del prodotto è obbligatorio!")
        BigDecimal price,
        BigDecimal takeawayPrice,
        Boolean isAvailable,
        DestinationArea destinationArea,
        @NotNull(message = "Id della categoria è obbligatoria")
        Long categoryId,
        List<Long> ingredientIds,
        Set<Allergen> allergens
) {
}