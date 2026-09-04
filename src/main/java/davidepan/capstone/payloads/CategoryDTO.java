package davidepan.capstone.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryDTO(
        @NotBlank(message = "Il nome non può essere vuoto")
        String name,
        @Min(value = 1, message = "L'ordine di visualizzazione deve essere almeno 1")
        Integer displayOrder
) {
}
