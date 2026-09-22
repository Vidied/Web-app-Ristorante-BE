package davidepan.capstone.payloads;

import java.util.List;

public record AppendItemsRequestDTO(
        List<OrderItemRequestDTO> items,
        Integer coverCount
) {
}