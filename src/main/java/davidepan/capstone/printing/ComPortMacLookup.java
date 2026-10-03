package davidepan.capstone.printing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ComPortMacLookup {

    private static final Logger log = LoggerFactory.getLogger(ComPortMacLookup.class);

    private static final long CACHE_TTL_MILLIS = 5 * 60 * 1000L;
    private static final long PROCESS_TIMEOUT_SECONDS = 20;
    private static final Pattern OUTPUT_LINE = Pattern.compile("^COM\\d+\\|([0-9A-Fa-f]{12})$");
    private static final Pattern NON_HEX = Pattern.compile("[^0-9A-Fa-f]");

    private static Map<String, String> cachedComToMac = Map.of();
    private static long cacheExpiresAtMillis = 0L;

    private ComPortMacLookup() {
    }

    public static synchronized Map<String, String> getComToMac() {
        if (System.currentTimeMillis() >= cacheExpiresAtMillis) {
            cachedComToMac = lookupComToMac();
            cacheExpiresAtMillis = System.currentTimeMillis() + CACHE_TTL_MILLIS;
        }
        return cachedComToMac;
    }

    public static synchronized void invalidateCache() {
        cacheExpiresAtMillis = 0L;
    }

    private static Map<String, String> lookupComToMac() {
        String script = "Get-PnpDevice -Class Ports -Status OK | ForEach-Object { "
                + "$id = $_.InstanceId; "
                + "$fn = $_.FriendlyName; "
                + "$mac = if ($id -match '_([0-9A-Fa-f]{12})_') { $matches[1] } else { '' }; "
                + "$com = if ($fn -match '\\(COM(\\d+)\\)') { 'COM' + $matches[1] } else { '' }; "
                + "if ($com -and $mac) { Write-Output ($com + '|' + $mac) } "
                + "}";

        Map<String, String> result = new HashMap<>();
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "powershell.exe", "-NoProfile", "-NonInteractive", "-Command", script)
                    .redirectErrorStream(true);
            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = OUTPUT_LINE.matcher(line.trim());
                    if (matcher.matches()) {
                        result.put(matcher.group(1), normalizeMac(matcher.group(2)));
                    }
                }
            }

            if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)) {
                process.destroyForcibly();
                log.warn("Lookup MAC/WMI delle porte seriali scaduta dopo {} secondi.", PROCESS_TIMEOUT_SECONDS);
                return result;
            }
        } catch (IOException e) {
            log.warn("Lookup MAC/WMI delle porte seriali non disponibile: {}", e.getMessage());
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (process != null) process.destroyForcibly();
            return result;
        }
        return result;
    }

    private static String normalizeMac(String mac) {
        return NON_HEX.matcher(mac).replaceAll("").toUpperCase(java.util.Locale.ROOT);
    }
}
