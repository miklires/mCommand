package io.github.miklires.mcommand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mcommand.command.chat.BroadcastCommand;
import io.github.miklires.mcommand.command.chat.ClearChatCommand;
import io.github.miklires.mcommand.command.info.PingCommand;
import io.github.miklires.mcommand.command.info.PlaytimeCommand;
import io.github.miklires.mcommand.command.info.SeenCommand;
import io.github.miklires.mcommand.command.state.EnderChestCommand;
import io.github.miklires.mcommand.command.state.ExtinguishCommand;
import io.github.miklires.mcommand.command.state.FeedCommand;
import io.github.miklires.mcommand.command.state.FlyCommand;
import io.github.miklires.mcommand.command.state.GodCommand;
import io.github.miklires.mcommand.command.state.HealCommand;
import io.github.miklires.mcommand.command.state.InvseeCommand;
import io.github.miklires.mcommand.command.state.RepairCommand;
import io.github.miklires.mcommand.command.state.SpeedCommand;
import io.github.miklires.mcommand.command.time.DayCommand;
import io.github.miklires.mcommand.command.time.NightCommand;
import io.github.miklires.mcommand.command.time.RainCommand;
import io.github.miklires.mcommand.command.time.SunCommand;
import io.github.miklires.mcommand.command.utils.AnvilCommand;
import io.github.miklires.mcommand.command.utils.BackCommand;
import io.github.miklires.mcommand.command.utils.ClearCommand;
import io.github.miklires.mcommand.command.utils.HatCommand;
import io.github.miklires.mcommand.command.utils.ItemNameCommand;
import io.github.miklires.mcommand.command.utils.LoreCommand;
import io.github.miklires.mcommand.command.utils.SkullCommand;
import io.github.miklires.mcommand.command.utils.SudoCommand;
import io.github.miklires.mcommand.command.utils.TopCommand;
import io.github.miklires.mcommand.command.utils.WorkbenchCommand;
import io.github.miklires.mcommand.config.ConfigManager;
import io.github.miklires.mcommand.data.BackLocationManager;
import io.github.miklires.mcommand.listener.BackTrackingListener;

public class MCommand extends JavaPlugin {

    private ConfigManager configManager;
    private BackLocationManager backLocationManager;
    private io.github.miklires.mcommand.util.MessageUtil messageUtil;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messageUtil = new io.github.miklires.mcommand.util.MessageUtil(this);
        configManager = new ConfigManager(this);
        backLocationManager = new BackLocationManager();

        getServer().getPluginManager().registerEvents(new BackTrackingListener(this), this);

        register("fly", new FlyCommand(this));
        register("god", new GodCommand(this));
        register("heal", new HealCommand(this));
        register("feed", new FeedCommand(this));
        register("repair", new RepairCommand(this));
        register("speed", new SpeedCommand(this));
        register("ext", new ExtinguishCommand(this));
        register("invsee", new InvseeCommand(this));
        register("ec", new EnderChestCommand(this));

        register("top", new TopCommand(this));
        register("back", new BackCommand(this));
        register("hat", new HatCommand(this));
        register("anvil", new AnvilCommand(this));
        register("wb", new WorkbenchCommand(this));
        register("clear", new ClearCommand(this));
        register("skull", new SkullCommand(this));
        register("itemname", new ItemNameCommand(this));
        register("lore", new LoreCommand(this));
        register("sudo", new SudoCommand(this));

        register("day", new DayCommand(this));
        register("night", new NightCommand(this));
        register("sun", new SunCommand(this));
        register("rain", new RainCommand(this));

        register("ping", new PingCommand(this));
        register("seen", new SeenCommand(this));
        register("playtime", new PlaytimeCommand(this));

        register("broadcast", new BroadcastCommand(this));
        register("clearchat", new ClearChatCommand(this));
        register("gm", new io.github.miklires.mcommand.command.state.GamemodeCommand(this));

        getCommand("mcommand").setExecutor(new AdminExecutor());

        getLogger().info("mCommand включён.");
    }

    private void register(String name, CommandExecutor executor) {
        var cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Команда не зарегистрирована в plugin.yml: " + name);
            return;
        }
        cmd.setExecutor(executor);
    }

    public ConfigManager getConfigManager() { return configManager; }
    public BackLocationManager getBackLocationManager() { return backLocationManager; }
    public io.github.miklires.mcommand.util.MessageUtil getMessageUtil() { return messageUtil; }

    private class AdminExecutor implements CommandExecutor {
        private final MiniMessage mm = MiniMessage.miniMessage();

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String[] args) {
            String prefix = messageUtil.prefix();
            if (args.length == 0) {
                sender.sendMessage(mm.deserialize(prefix
                        + "<gray>Версия: <white>" + getDescription().getVersion()));
                sender.sendMessage(mm.deserialize(prefix
                        + "<yellow>/mcommand reload</yellow> <gray>— перезагрузить конфиги"));
                return true;
            }
            if (!sender.hasPermission("mcommand.admin")) {
                sender.sendMessage(mm.deserialize(prefix + messageUtil.get("no-permission")));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                configManager.reload();
                messageUtil.reload();
                sender.sendMessage(mm.deserialize(prefix + messageUtil.get("reloaded")));
                return true;
            }
            sender.sendMessage(mm.deserialize(prefix + messageUtil.get("usage-reload")));
            return true;
        }
    }
}

