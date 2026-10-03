package davidepan.capstone.payloads;

public record VersionResponse(
        String version,
        String buildTime,
        String gitCommit
) {}
