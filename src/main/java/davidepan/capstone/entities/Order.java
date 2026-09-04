package davidepan.capstone.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import davidepan.capstone.enums.OrderStatus;
import davidepan.capstone.enums.OrderType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private Integer tableNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private Integer coverCount;

    private BigDecimal coverPrice;

    public Order(Integer tableNumber, OrderStatus orderStatus, OrderType orderType, BigDecimal totalAmount, String notes, LocalDateTime createdAt, List<OrderItem> items, Integer coverCount, BigDecimal coverPrice) {
        this.tableNumber = tableNumber;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.createdAt = createdAt;
        this.items = items;
        this.coverCount = coverCount;
        this.coverPrice = coverPrice;
    }
}


