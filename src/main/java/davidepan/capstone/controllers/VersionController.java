package davidepan.capstone.controllers;

import davidepan.capstone.payloads.VersionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${app.version:unknown}")
    private String appVersion;

    @Value("${app.build-time:unknown}")
    private String appBuildTime;

    @Value("${app.git-commit:unknown}")
    private String appGitCommit;

    @GetMapping("/version")
    public VersionResponse version() {
        return new VersionResponse(
                resolve(appVersion),
                resolve(appBuildTime),
                resolve(appGitCommit)
        );
    }

    private static String resolve(String value) {
        if (value == null || value.isBlank()) return "unknown";
        if (value.startsWith("@") || value.startsWith("${")) return "unknown";
        return value;
    }
}
