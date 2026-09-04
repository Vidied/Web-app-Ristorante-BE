package davidepan.capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @Setter
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    public Category(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }
}