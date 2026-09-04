package davidepan.capstone.controllers;


import davidepan.capstone.entities.Ingredient;
import davidepan.capstone.payloads.IngredientDTO;
import davidepan.capstone.payloads.IngredientUpdateDTO;
import davidepan.capstone.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAll(){
        return ingredientService.findAll();
    }

    @GetMapping("/{id}")
    public Ingredient findById(@PathVariable Long id){
        return ingredientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ingredient create(@RequestBody @Validated IngredientDTO body){
        return ingredientService.save(body);
    }

    @PutMapping("/{id}")
    public Ingredient update(@PathVariable Long id, @RequestBody @Validated IngredientUpdateDTO body){
        return ingredientService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        ingredientService.delete(id);
    }

}
