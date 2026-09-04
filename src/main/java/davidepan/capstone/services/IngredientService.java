package davidepan.capstone.services;

import davidepan.capstone.entities.Ingredient;
import davidepan.capstone.entities.Product;
import davidepan.capstone.exceptions.NotFoundException;
import davidepan.capstone.payloads.IngredientDTO;
import davidepan.capstone.payloads.IngredientUpdateDTO;
import davidepan.capstone.repositories.IngredientRepository;
import davidepan.capstone.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

     @Autowired
     private ProductRepository productRepository;

    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }

    public Ingredient findById(Long id){
        return ingredientRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Ingrediente con ID " + id + " non trovato"));
    }

    public Ingredient save(IngredientDTO body){
        Ingredient ingredient = new Ingredient(body.name());

        if (body.isAvailable() != null){
            ingredient.setIsAvailable(body.isAvailable());
        }

        return ingredientRepository.save(ingredient);
    }
    @Transactional
    public Ingredient update(Long id, IngredientUpdateDTO body){
        Ingredient found = this.findById(id);

        if(body.name() != null && !body.name().isBlank()){
            found.setName(body.name());
        }

        if(body.isAvailable() != null) {
            found.setIsAvailable(body.isAvailable());
        }

        Ingredient savedIngredient = ingredientRepository.save(found);

        List<Product> associatedProducts = productRepository.findByIngredientsId(id);

        for (Product product : associatedProducts) {
            boolean hasUnavailableIngredient = product.getIngredients().stream()
                    .anyMatch(ingredient -> Boolean.FALSE.equals(ingredient.getIsAvailable()));
            product.setIsAvailable(!hasUnavailableIngredient);
        }

        productRepository.saveAll(associatedProducts);

        return savedIngredient;
    }

    public void delete(Long id){
        Ingredient found = this.findById(id);
        ingredientRepository.delete(found);
    }
}
