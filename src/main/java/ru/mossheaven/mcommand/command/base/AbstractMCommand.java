package ru.mossheaven.mcommand.command.base;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mossheaven.mcommand.MCommand;

public abstract class AbstractMCommand implements CommandExecutor {

    protected final MCommand plugin;
    protected final String commandKey;
    protected final MiniMessage mm = MiniMessage.miniMessage();

    public AbstractMCommand(MCommand plugin, String commandKey) {
        this.plugin = plugin;
        this.commandKey = commandKey;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!plugin.getConfigManager().isCommandEnabled(commandKey)) {
            sender.sendMessage(mm.deserialize(prefix() + msg("disabled")));
            return true;
        }
        String perm = "mcommand.command." + commandKey;
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(mm.deserialize(prefix() + msg("no-permission")));
            return true;
        }
        execute(sender, args);
        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);

    protected boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(mm.deserialize(prefix() + msg("player-only")));
            return false;
        }
        return true;
    }

    protected void send(CommandSender sender, String key) {
        sender.sendMessage(mm.deserialize(prefix() + msg(key)));
    }

    protected void send(CommandSender sender, String key, String... replacements) {
        String s = msg(key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            s = s.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        sender.sendMessage(mm.deserialize(prefix() + s));
    }

    protected String prefix() { return plugin.getMessageUtil().prefix(); }
    protected String msg(String key) { return plugin.getMessageUtil().get(key); }
}
