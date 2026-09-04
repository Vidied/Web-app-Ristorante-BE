package davidepan.capstone.repositories;

import davidepan.capstone.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByDisplayOrder(Integer displayOrder);

    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.products p " +
            "LEFT JOIN FETCH p.ingredients " +
            "ORDER BY c.displayOrder ASC")
    List<Category> findAllWithDetails();

    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.products p " +
            "LEFT JOIN FETCH p.ingredients " +
            "WHERE c.id = :id")
    Optional<Category> findByIdWithDetails(@Param("id") Long id);

    Optional<Category> findByDisplayOrder(Integer displayOrder);

    @Query("SELECT MAX(c.displayOrder) FROM Category c")
    Optional<Integer> findMaxDisplayOrder();

    List<Category> findByDisplayOrderGreaterThanOrderByDisplayOrderAsc(Integer displayOrder);

}