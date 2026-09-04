package davidepan.capstone.payloads;

public record IngredientResponseDTO(
        Long id,
        String name,
        Boolean isAvailable
) {
}
