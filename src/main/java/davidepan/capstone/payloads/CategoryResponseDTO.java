package davidepan.capstone.payloads;

import java.util.List;

public record CategoryResponseDTO(
        Long id,
        String name,
        Integer displayOrder,
        List<ProductResponseDTO> products
) {
}
