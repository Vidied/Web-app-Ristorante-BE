package davidepan.capstone.payloads;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorsResponseDTO (
        String message,
        List<String> errorsList,
        LocalDateTime timestamp
) {

}
