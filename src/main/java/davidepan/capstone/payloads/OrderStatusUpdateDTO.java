package davidepan.capstone.payloads;

import davidepan.capstone.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(

        @NotNull(message = "Lo stato dell'ordine è obbligatorio")
        OrderStatus orderStatus
) {
}
