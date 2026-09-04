package davidepan.capstone.payloads;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO (
        String message,
        LocalDateTime timestamp
){
}
