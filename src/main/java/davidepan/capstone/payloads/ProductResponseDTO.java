package davidepan.capstone.payloads;

import davidepan.capstone.entities.Ingredient;
import davidepan.capstone.entities.Product;
import davidepan.capstone.enums.DestinationArea;
import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal takeawayPrice,
        DestinationArea destinationArea,
        Boolean isAvailable,
        Long categoryId,
        String categoryName,
        List<String> ingredientNames
) {
    public static ProductResponseDTO fromEntity(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getTakeawayPrice(),
                product.getDestinationArea(),
                product.getIsAvailable(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getIngredients() != null
                        ? product.getIngredients().stream().map(Ingredient::getName).toList()
                        : List.of()
        );
    }
}