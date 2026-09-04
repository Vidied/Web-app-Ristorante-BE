package davidepan.capstone.services;

import davidepan.capstone.entities.Category;
import davidepan.capstone.exceptions.BadRequestException;
import davidepan.capstone.exceptions.NotFoundException;
import davidepan.capstone.payloads.CategoryDTO;
import davidepan.capstone.payloads.CategoryResponseDTO;
import davidepan.capstone.payloads.ProductResponseDTO;
import davidepan.capstone.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria con ID " + id + " non trovata"));
    }

    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Categoria con ID " + id + " non trovata"));
        return convertToResponseDTO(category);
    }

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAllWithDetails().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO save(CategoryDTO body) {
        Integer assignedOrder = body.displayOrder();

        if (assignedOrder == null) {
            assignedOrder = categoryRepository.findMaxDisplayOrder()
                    .map(max -> max + 1)
                    .orElse(1);
        } else {
            if (categoryRepository.existsByDisplayOrder(assignedOrder)) {
                throw new BadRequestException("Il display order " + assignedOrder + " è già in uso");
            }
        }

        Category category = new Category(body.name(), assignedOrder);
        Category savedCategory = categoryRepository.save(category);
        return convertToResponseDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryDTO body) {
        Category categoryToUpdate = this.findEntityById(id);

        if (body.name() != null) {
            categoryToUpdate.setName(body.name());
        }

        Integer newOrder = body.displayOrder();
        Integer currentOrder = categoryToUpdate.getDisplayOrder();

        if (newOrder != null && !newOrder.equals(currentOrder)) {
            Optional<Category> existingCategoryWithNewOrder = categoryRepository.findByDisplayOrder(newOrder);

            if (existingCategoryWithNewOrder.isPresent()) {
                Category otherCategory = existingCategoryWithNewOrder.get();
                otherCategory.setDisplayOrder(currentOrder);
                categoryRepository.save(otherCategory);
            }

            categoryToUpdate.setDisplayOrder(newOrder);
        }

        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return convertToResponseDTO(updatedCategory);
    }

    @Transactional
    public void delete(Long id) {
        Category categoryToDelete = this.findEntityById(id);
        Integer deletedOrder = categoryToDelete.getDisplayOrder();

        categoryRepository.delete(categoryToDelete);
        if (deletedOrder != null) {
            List<Category> categoriesToShift = categoryRepository
                    .findByDisplayOrderGreaterThanOrderByDisplayOrderAsc(deletedOrder);

            for (Category category : categoriesToShift) {
                category.setDisplayOrder(category.getDisplayOrder() - 1);
            }
            categoryRepository.saveAll(categoriesToShift);
        }
    }

    public CategoryResponseDTO convertToResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDisplayOrder(),
                category.getProducts() != null
                        ? category.getProducts().stream()
                        .map(ProductResponseDTO::fromEntity)
                        .toList()
                        : List.of()
        );
    }
}