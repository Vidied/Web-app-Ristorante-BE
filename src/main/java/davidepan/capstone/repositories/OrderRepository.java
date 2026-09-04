package davidepan.capstone.repositories;

import davidepan.capstone.entities.Order;
import davidepan.capstone.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.orderStatus = :status")
    List<Order> findByOrderStatusWithDetails(@Param("status") OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product")
    List<Order> findAllWithDetails();

    @Modifying
    @Query("DELETE FROM Order o WHERE o.orderStatus = :status")
    void deleteAllByOrderStatus(@Param("status") OrderStatus status);

    @Modifying
    @Query("DELETE FROM Order o WHERE o.id IN :ids AND o.orderStatus = 'COMPLETED'")
    void deleteAllCompletedByIds(@Param("ids") List<Long> ids);
}