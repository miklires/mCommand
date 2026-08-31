package io.github.miklires.mcommand.config;

import io.github.miklires.mcommand.MCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConfigManager {
    private static final int VERSION = 2;
    private final MCommand plugin;
    public ConfigManager(MCommand plugin) { this.plugin = plugin; }

    public void load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        int version = config.getInt("config-version", 0);
        if (version < VERSION) {
            var stream = plugin.getResource("config.yml");
            if (stream != null) {
                YamlConfiguration bundled = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                bundled.getKeys(true).stream().filter(key -> !bundled.isConfigurationSection(key))
                        .filter(key -> !config.contains(key)).forEach(key -> config.set(key, bundled.get(key)));
            }
            config.set("config-version", VERSION);
            plugin.saveConfig();
        } else if (version > VERSION) plugin.getLogger().warning("config.yml is from a newer mCommand version");
    }

    public void reload() { plugin.reloadConfig(); load(); }
    public boolean isCommandEnabled(String key) {
        String module = moduleFor(key);
        return plugin.getConfig().getBoolean("modules." + module, true)
                && plugin.getConfig().getBoolean("commands." + key + ".enabled", true);
    }
    public String language() {
        String value = plugin.getConfig().getString("language.default", "en_US");
        return value != null && value.matches("[A-Za-z]{2}_[A-Za-z]{2}") ? value : "en_US";
    }
    public boolean isMetricsEnabled() { return plugin.getConfig().getBoolean("metrics.enabled", true); }
    public int getBstatsId() { return Math.max(0, plugin.getConfig().getInt("metrics.bstats-id", 33356)); }
    public boolean isUpdatesEnabled() { return plugin.getConfig().getBoolean("updates.enabled", true); }
    public String modrinthProjectId() { return plugin.getConfig().getString("updates.modrinth-project-id", "").trim(); }
    public int updateTimeoutMillis() { return (int) range("updates.timeout-millis", 8000, 500, 60000); }
    public double maxNearRadius() { return range("limits.near-max-radius", 200, 1, 5000); }
    public double maxTeleportCoordinate() { return range("limits.max-teleport-coordinate", 30_000_000, 1000, 30_000_000); }
    public Set<String> freezeAllowedCommands() {
        return plugin.getConfig().getStringList("moderation.freeze.allowed-commands").stream()
                .map(value -> value.toLowerCase(Locale.ROOT).replaceFirst("^/", ""))
                .filter(value -> !value.isBlank()).collect(Collectors.toUnmodifiableSet());
    }

    private double range(String path, double fallback, double min, double max) {
        double value = plugin.getConfig().getDouble(path, fallback);
        if (!Double.isFinite(value) || value < min || value > max) {
            plugin.getLogger().warning("Invalid " + path + "; using " + fallback);
            return fallback;
        }
        return value;
    }

    private String moduleFor(String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "fly", "god", "heal", "feed", "repair", "speed", "ext", "gm" -> "state";
            case "invsee", "endersee", "freeze", "unfreeze", "vanish", "sudo", "broadcast", "clearchat" -> "moderation";
            case "day", "night", "sun", "rain" -> "world";
            case "ping", "seen", "playtime", "near", "whois" -> "info";
            default -> "utils";
        };
    }
}
