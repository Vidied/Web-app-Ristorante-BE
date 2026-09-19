package davidepan.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
@NoArgsConstructor
@Getter
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(name = "extra_price", nullable = false, precision = 10, scale = 2,
            columnDefinition = "numeric(10,2) default 1.00")
    private BigDecimal extraPrice = new BigDecimal("1.00");

    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient(String name, BigDecimal extraPrice) {
        this.name = name;
        this.extraPrice = extraPrice;
    }
}