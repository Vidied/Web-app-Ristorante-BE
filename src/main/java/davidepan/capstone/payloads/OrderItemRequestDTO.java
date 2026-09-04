package davidepan.capstone.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull(message = "L'ID del prodotto è obbligatorio")
        Long productId,

        @NotNull(message = "La quantità del prodotto è obbligatoria")
        @Min(value = 1, message = "Quantità minima 1")
        Integer quantity,

        String notes
) {
}
