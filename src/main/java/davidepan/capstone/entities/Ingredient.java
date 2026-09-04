package davidepan.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Ingredient(String name) {
        this.name = name;
    }
}
