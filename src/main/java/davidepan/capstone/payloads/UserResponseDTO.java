package davidepan.capstone.payloads;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String surname,
        String email,
        Set<String> roles
) {
}
