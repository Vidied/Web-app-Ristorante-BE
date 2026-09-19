package davidepan.capstone.payloads;

import davidepan.capstone.enums.DestinationArea;

public record PrintResultDTO(
        DestinationArea destinationArea,
        boolean success,
        String errorMessage
) {}