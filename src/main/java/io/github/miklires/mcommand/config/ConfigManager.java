package io.github.miklires.mcommand.config;

import io.github.miklires.mcommand.MCommand;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Locale;

public final class ConfigManager {
    private static final int VERSION = 1;
    private final MCommand plugin;
    public ConfigManager(MCommand plugin) { this.plugin = plugin; }

    public void load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        int version = config.getInt("config-version", 0);
        if (version < VERSION) {
            config.options().copyDefaults(true);
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
    public String language() { return plugin.getConfig().getString("language.default", "en_US"); }
    public boolean isMetricsEnabled() { return plugin.getConfig().getBoolean("metrics.enabled", true); }
    public int getBstatsId() { return Math.max(0, plugin.getConfig().getInt("metrics.bstats-id", 33356)); }
    public boolean isUpdatesEnabled() { return plugin.getConfig().getBoolean("updates.enabled", true); }
    public double maxNearRadius() { return range("limits.near-max-radius", 200, 1, 5000); }
    public double maxTeleportCoordinate() { return range("limits.max-teleport-coordinate", 30_000_000, 1000, 30_000_000); }

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
