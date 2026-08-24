package io.github.miklires.mcommand.update;

import io.github.miklires.mcommand.MCommand;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateChecker {
    private static final Pattern VERSION = Pattern.compile("\\\"version_number\\\":\\\"([^\\\"]+)\\\"");
    private final MCommand plugin;
    public UpdateChecker(MCommand plugin) { this.plugin = plugin; }

    public void check() {
        String project = plugin.getConfigManager().modrinthProjectId();
        if (!plugin.getConfigManager().isUpdatesEnabled() || project.isBlank()) return;
        try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            String id = URLEncoder.encode(project, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.modrinth.com/v2/project/" + id + "/version"))
                    .timeout(Duration.ofMillis(plugin.getConfigManager().updateTimeoutMillis()))
                    .header("User-Agent", "miklires/mCommand/" + plugin.getPluginMeta().getVersion()).GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) { plugin.getLogger().warning("Update check returned " + response.statusCode()); return; }
            String current = plugin.getPluginMeta().getVersion();
            String latest = current;
            Matcher matcher = VERSION.matcher(response.body());
            while (matcher.find()) if (isNewer(matcher.group(1), latest)) latest = matcher.group(1);
            if (isNewer(latest, current)) plugin.getLogger().info("mCommand " + latest + " is available on Modrinth");
        } catch (Exception e) {
            plugin.getLogger().warning("Update check failed: " + e.getMessage());
        }
    }

    static boolean isNewer(String candidate, String current) {
        long[] left = numbers(candidate), right = numbers(current);
        if (left == null || right == null) return false;
        for (int i = 0; i < 3; i++) if (left[i] != right[i]) return left[i] > right[i];
        boolean leftStable = !candidate.contains("-"), rightStable = !current.contains("-");
        return leftStable && !rightStable;
    }

    private static long[] numbers(String value) {
        Matcher matcher = Pattern.compile("^v?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-+].*)?$").matcher(value.trim());
        if (!matcher.matches()) return null;
        try {
            return new long[]{Long.parseLong(matcher.group(1)), number(matcher.group(2)), number(matcher.group(3))};
        } catch (NumberFormatException e) { return null; }
    }
    private static long number(String value) { return value == null ? 0 : Long.parseLong(value); }
}
