package davidepan.capstone.controllers;

import davidepan.capstone.payloads.ProductDTO;
import davidepan.capstone.payloads.ProductResponseDTO;
import davidepan.capstone.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ProductResponseDTO findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/available")
    public List<ProductResponseDTO> getAvailable() {
        return productService.findAvailable();
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getByCategoryId(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @PatchMapping("/{id}/availability")
    public ProductResponseDTO toggleAvailability(@PathVariable Long id) {
        return productService.toggleAvailability(id);
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO create(@RequestBody @Validated ProductDTO body) {
        return productService.save(body);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Long id, @RequestBody @Validated ProductDTO body) {
        return productService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}