package davidepan.capstone.repositories;

import davidepan.capstone.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

 @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category " +
         "LEFT JOIN FETCH p.ingredients " +
         "WHERE p.id = :id")
 Optional<Product> findByIdWithDetails(@Param("id") Long id);

 @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category " +
         "LEFT JOIN FETCH p.ingredients " +
         "ORDER BY p.name ASC")
 List<Product> findAllWithDetails();

 @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category " +
         "LEFT JOIN FETCH p.ingredients " +
         "WHERE p.category.id = :categoryId " +
         "ORDER BY p.name ASC")
 List<Product> findByCategoryIdWithDetails(@Param("categoryId") Long categoryId);

 @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category " +
         "LEFT JOIN FETCH p.ingredients " +
         "WHERE p.isAvailable = true " +
         "ORDER BY p.name ASC")
 List<Product> findAvailableWithDetails();

 List<Product> findByIngredientsIsEmpty();

 List<Product> findByIngredientsId(Long ingredientId);

}