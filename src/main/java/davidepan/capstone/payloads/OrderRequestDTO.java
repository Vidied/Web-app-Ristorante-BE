package davidepan.capstone.payloads;

import davidepan.capstone.enums.OrderStatus;
import davidepan.capstone.enums.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDTO(

        @Min(value = 0, message = "Il numero del tavolo non può essere negativo")
        Integer tableNumber,

        @Min(value = 0, message = "Il numero dei coperti non può essere negativo")
        Integer coverCount,

        BigDecimal coverPrice,
        OrderStatus orderStatus,
        OrderType orderType,
        String notes,

        @NotEmpty(message = "La comanda deve avere almeno un prodotto")
        List<OrderItemRequestDTO> items
) {
}