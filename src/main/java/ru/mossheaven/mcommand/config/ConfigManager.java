package ru.mossheaven.mcommand.config;

import ru.mossheaven.mcommand.MCommand;

public class ConfigManager {

    private final MCommand plugin;

    public ConfigManager(MCommand plugin) {
        this.plugin = plugin;
    }

    public boolean isCommandEnabled(String key) {
        return plugin.getConfig().getBoolean("commands." + key + ".enabled", true);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
