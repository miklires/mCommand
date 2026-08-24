package io.github.miklires.mcommand.util;

import io.github.miklires.mcommand.MCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class MessageUtil {
    private final MCommand plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private YamlConfiguration messages;

    public MessageUtil(MCommand plugin) { this.plugin = plugin; reload(); }

    public void reload() {
        for (String locale : new String[]{"en_US", "ru_RU"}) {
            File file = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
            if (!file.exists()) plugin.saveResource("lang/" + locale + ".yml", false);
        }
        File selected = new File(plugin.getDataFolder(), "lang/" + safeLocale() + ".yml");
        messages = YamlConfiguration.loadConfiguration(selected);
        var defaults = plugin.getResource("lang/en_US.yml");
        if (defaults != null) messages.setDefaults(YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaults, StandardCharsets.UTF_8)));
    }

    private String safeLocale() {
        String locale = plugin.getConfigManager().language();
        if (!locale.matches("[A-Za-z]{2}_[A-Za-z]{2}")) {
            plugin.getLogger().warning("Invalid language.default; using en_US");
            return "en_US";
        }
        File bundled = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!bundled.exists() && plugin.getResource("lang/" + locale + ".yml") == null) return "en_US";
        return locale;
    }

    public String get(String key) { return messages.getString(key, key); }
    public String prefix() { return get("prefix"); }
    public void send(CommandSender sender, String key) { sender.sendMessage(miniMessage.deserialize(prefix() + get(key))); }
}
