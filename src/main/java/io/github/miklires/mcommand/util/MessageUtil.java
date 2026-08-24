package io.github.miklires.mcommand.util;

import org.bukkit.configuration.file.YamlConfiguration;
import io.github.miklires.mcommand.MCommand;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageUtil {

    private final MCommand plugin;
    private YamlConfiguration messages;

    public MessageUtil(MCommand plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
        var def = plugin.getResource("messages.yml");
        if (def != null) {
            messages.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(def, StandardCharsets.UTF_8)));
        }
    }

    public String get(String key) {
        String s = messages.getString(key);
        return s != null ? s : key;
    }

    public String prefix() {
        return get("prefix");
    }
}

