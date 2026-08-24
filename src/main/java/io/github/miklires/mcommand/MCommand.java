package io.github.miklires.mcommand;

import io.github.miklires.mcommand.command.BrigadierCommands;
import io.github.miklires.mcommand.command.ModernCommand;
import io.github.miklires.mcommand.config.ConfigManager;
import io.github.miklires.mcommand.data.BackLocationManager;
import io.github.miklires.mcommand.listener.BackTrackingListener;
import io.github.miklires.mcommand.listener.PlayerStateListener;
import io.github.miklires.mcommand.util.MessageUtil;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCommand extends JavaPlugin {
    private ConfigManager configManager;
    private BackLocationManager backLocationManager;
    private MessageUtil messageUtil;
    private ModernCommand modernCommand;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();
        messageUtil = new MessageUtil(this);
        backLocationManager = new BackLocationManager(this);
        modernCommand = new ModernCommand(this);
        getServer().getPluginManager().registerEvents(new BackTrackingListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerStateListener(this), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> new BrigadierCommands(this).register(event.registrar()));
        if (configManager.isMetricsEnabled() && configManager.getBstatsId() > 0) {
            new org.bstats.bukkit.Metrics(this, configManager.getBstatsId());
        }
        getLogger().info("mCommand enabled");
    }

    @Override
    public void onDisable() {
        if (backLocationManager != null) backLocationManager.saveNow();
        getLogger().info("mCommand disabled");
    }

    public void reloadPlugin() { configManager.reload(); messageUtil.reload(); }
    public ConfigManager getConfigManager() { return configManager; }
    public BackLocationManager getBackLocationManager() { return backLocationManager; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public ModernCommand getModernCommand() { return modernCommand; }
}
