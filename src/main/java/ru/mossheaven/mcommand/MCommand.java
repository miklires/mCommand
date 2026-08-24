package ru.mossheaven.mcommand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.mossheaven.mcommand.command.chat.BroadcastCommand;
import ru.mossheaven.mcommand.command.chat.ClearChatCommand;
import ru.mossheaven.mcommand.command.info.PingCommand;
import ru.mossheaven.mcommand.command.info.PlaytimeCommand;
import ru.mossheaven.mcommand.command.info.SeenCommand;
import ru.mossheaven.mcommand.command.state.EnderChestCommand;
import ru.mossheaven.mcommand.command.state.ExtinguishCommand;
import ru.mossheaven.mcommand.command.state.FeedCommand;
import ru.mossheaven.mcommand.command.state.FlyCommand;
import ru.mossheaven.mcommand.command.state.GodCommand;
import ru.mossheaven.mcommand.command.state.HealCommand;
import ru.mossheaven.mcommand.command.state.InvseeCommand;
import ru.mossheaven.mcommand.command.state.RepairCommand;
import ru.mossheaven.mcommand.command.state.SpeedCommand;
import ru.mossheaven.mcommand.command.time.DayCommand;
import ru.mossheaven.mcommand.command.time.NightCommand;
import ru.mossheaven.mcommand.command.time.RainCommand;
import ru.mossheaven.mcommand.command.time.SunCommand;
import ru.mossheaven.mcommand.command.utils.AnvilCommand;
import ru.mossheaven.mcommand.command.utils.BackCommand;
import ru.mossheaven.mcommand.command.utils.ClearCommand;
import ru.mossheaven.mcommand.command.utils.HatCommand;
import ru.mossheaven.mcommand.command.utils.ItemNameCommand;
import ru.mossheaven.mcommand.command.utils.LoreCommand;
import ru.mossheaven.mcommand.command.utils.SkullCommand;
import ru.mossheaven.mcommand.command.utils.SudoCommand;
import ru.mossheaven.mcommand.command.utils.TopCommand;
import ru.mossheaven.mcommand.command.utils.WorkbenchCommand;
import ru.mossheaven.mcommand.config.ConfigManager;
import ru.mossheaven.mcommand.data.BackLocationManager;
import ru.mossheaven.mcommand.listener.BackTrackingListener;

public class MCommand extends JavaPlugin {

    private ConfigManager configManager;
    private BackLocationManager backLocationManager;
    private ru.mossheaven.mcommand.util.MessageUtil messageUtil;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messageUtil = new ru.mossheaven.mcommand.util.MessageUtil(this);
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
        register("gm", new ru.mossheaven.mcommand.command.state.GamemodeCommand(this));

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
    public ru.mossheaven.mcommand.util.MessageUtil getMessageUtil() { return messageUtil; }

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
