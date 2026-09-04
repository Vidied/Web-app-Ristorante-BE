package davidepan.capstone.services;

import davidepan.capstone.entities.Category;
import davidepan.capstone.entities.Ingredient;
import davidepan.capstone.entities.Product;
import davidepan.capstone.enums.DestinationArea;
import davidepan.capstone.exceptions.BadRequestException;
import davidepan.capstone.exceptions.NotFoundException;
import davidepan.capstone.payloads.ProductDTO;
import davidepan.capstone.payloads.ProductResponseDTO;
import davidepan.capstone.repositories.CategoryRepository;
import davidepan.capstone.repositories.IngredientRepository;
import davidepan.capstone.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Spring corretto

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prodotto con ID " + id + " non trovato"));
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Prodotto con ID " + id + " non trovato"));
        return ProductResponseDTO.fromEntity(product);
    }

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAllWithDetails().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    public List<ProductResponseDTO> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryIdWithDetails(categoryId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    public List<ProductResponseDTO> findAvailable() {
        return productRepository.findAvailableWithDetails().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public ProductResponseDTO save(ProductDTO body) {
        Category category = categoryRepository.findById(body.categoryId())
                .orElseThrow(() -> new NotFoundException("Categoria con ID " + body.categoryId() + " non trovata"));

        Set<Ingredient> ingredients = new HashSet<>();
        if (body.ingredientIds() != null && !body.ingredientIds().isEmpty()) {
            List<Ingredient> fetchedIngredients = ingredientRepository.findAllById(body.ingredientIds());
            if (fetchedIngredients.size() != body.ingredientIds().size()) {
                throw new NotFoundException("Uno o più ingredienti non sono stati trovati");
            }
            ingredients.addAll(fetchedIngredients);
        }

        boolean hasUnavailableIngredient = ingredients.stream()
                .anyMatch(ingredient -> Boolean.FALSE.equals(ingredient.getIsAvailable()));

        boolean isAvailable = !hasUnavailableIngredient && (body.isAvailable() != null ? body.isAvailable() : true);
        DestinationArea destination = body.destinationArea() != null ? body.destinationArea() : DestinationArea.SALA;

        Product product = new Product(
                body.name(),
                body.description(),
                body.price(),
                isAvailable,
                destination,
                category,
                ingredients
        );

        Product savedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductDTO body) {
        Product found = this.findEntityById(id);

        if (body.categoryId() != null) {
            Category category = categoryRepository.findById(body.categoryId())
                    .orElseThrow(() -> new NotFoundException("Categoria con ID " + body.categoryId() + " non trovata"));
            found.setCategory(category);
        }

        if (body.ingredientIds() != null) {
            Set<Ingredient> updatedIngredients = new HashSet<>();
            if (!body.ingredientIds().isEmpty()) {
                List<Ingredient> fetchedIngredients = ingredientRepository.findAllById(body.ingredientIds());
                if (fetchedIngredients.size() != body.ingredientIds().size()) {
                    throw new NotFoundException("Uno o più ingredienti non sono stati trovati");
                }
                updatedIngredients.addAll(fetchedIngredients);
            }
            found.getIngredients().clear();
            found.getIngredients().addAll(updatedIngredients);
        }

        boolean hasUnavailableIngredient = found.getIngredients().stream()
                .anyMatch(ingredient -> Boolean.FALSE.equals(ingredient.getIsAvailable()));


        Boolean targetAvailability = body.isAvailable() != null ? body.isAvailable() : found.getIsAvailable();
        if (Boolean.TRUE.equals(targetAvailability) && hasUnavailableIngredient) {
            throw new BadRequestException("Impossibile rendere disponibile il prodotto: contiene ingredienti non disponibili.");
        }

        if (body.name() != null) found.setName(body.name());
        if (body.description() != null) found.setDescription(body.description());
        if (body.price() != null) found.setPrice(body.price());
        if (body.destinationArea() != null) found.setDestinationArea(body.destinationArea());

        found.setIsAvailable(targetAvailability);

        Product updatedProduct = productRepository.save(found);
        return ProductResponseDTO.fromEntity(updatedProduct);
    }

    @Transactional
    public ProductResponseDTO toggleAvailability(Long id) {
        Product product = this.findEntityById(id);

        boolean hasUnavailableIngredient = product.getIngredients().stream()
                .anyMatch(ingredient -> Boolean.FALSE.equals(ingredient.getIsAvailable()));

        if (!product.getIsAvailable() && hasUnavailableIngredient) {
            throw new BadRequestException("Impossibile rendere disponibile il prodotto: contiene ingredienti non disponibili.");
        }

        product.setIsAvailable(!product.getIsAvailable());
        Product updatedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(updatedProduct);
    }

    public void delete(Long id) {
        Product found = this.findEntityById(id);
        productRepository.delete(found);
    }
}