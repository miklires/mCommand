package io.github.miklires.mcommand;

import io.github.miklires.mcommand.command.BrigadierCommands;
import io.github.miklires.mcommand.command.ModernCommand;
import io.github.miklires.mcommand.config.ConfigManager;
import io.github.miklires.mcommand.data.BackLocationManager;
import io.github.miklires.mcommand.data.PlayerStateManager;
import io.github.miklires.mcommand.listener.BackTrackingListener;
import io.github.miklires.mcommand.listener.PlayerStateListener;
import io.github.miklires.mcommand.util.MessageUtil;
import io.github.miklires.mcommand.update.UpdateChecker;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

public final class MCommand extends JavaPlugin {
    private ConfigManager configManager;
    private BackLocationManager backLocationManager;
    private PlayerStateManager playerStateManager;
    private MessageUtil messageUtil;
    private ModernCommand modernCommand;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();
        messageUtil = new MessageUtil(this);
        backLocationManager = new BackLocationManager(this);
        playerStateManager = new PlayerStateManager(this);
        modernCommand = new ModernCommand(this);
        getServer().getPluginManager().registerEvents(new BackTrackingListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerStateListener(this), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> new BrigadierCommands(this).register(event.registrar()));
        if (configManager.isMetricsEnabled() && configManager.getBstatsId() > 0) {
            new org.bstats.bukkit.Metrics(this, configManager.getBstatsId());
        }
        getServer().getAsyncScheduler().runNow(this, task -> new UpdateChecker(this).check());
        getLogger().info("mCommand enabled");
    }

    @Override
    public void onDisable() {
        if (backLocationManager != null) backLocationManager.saveNow();
        if (playerStateManager != null) playerStateManager.saveNow();
        getLogger().info("mCommand disabled");
    }

    public void reloadPlugin() { configManager.reload(); messageUtil.reload(); }
    public ConfigManager getConfigManager() { return configManager; }
    public BackLocationManager getBackLocationManager() { return backLocationManager; }
    public PlayerStateManager getPlayerStateManager() { return playerStateManager; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public ModernCommand getModernCommand() { return modernCommand; }
    public void runFor(Player player, Runnable task) {
        player.getScheduler().execute(this, task, null, 1L);
    }
    public void runFor(CommandSender sender, Runnable task) {
        if (sender instanceof Player player) runFor(player, task);
        else getServer().getGlobalRegionScheduler().execute(this, task);
    }
}
