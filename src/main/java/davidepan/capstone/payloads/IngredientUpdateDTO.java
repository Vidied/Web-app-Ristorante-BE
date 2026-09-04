package davidepan.capstone.payloads;

public record IngredientUpdateDTO(
        String name,
        Boolean isAvailable
) {
}
