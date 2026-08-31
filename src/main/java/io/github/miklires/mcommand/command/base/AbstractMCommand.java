package io.github.miklires.mcommand.command.base;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mcommand.MCommand;

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

    protected boolean requireOtherPermission(CommandSender sender, Player target) {
        if (sender == target || sender.hasPermission("mcommand.command." + commandKey + ".other")) return true;
        send(sender, "no-permission");
        return false;
    }

    protected void send(CommandSender sender, String key) {
        plugin.getMessageUtil().send(sender, key);
    }

    protected void send(CommandSender sender, String key, String... replacements) {
        plugin.getMessageUtil().send(sender, key, replacements);
    }

    protected String prefix() { return plugin.getMessageUtil().prefix(); }
    protected String msg(String key) { return plugin.getMessageUtil().get(key); }

    protected Component parseUserText(CommandSender sender, String input) {
        if (sender.hasPermission("mcommand.format.unsafe")) return mm.deserialize(input);
        MiniMessage safe = MiniMessage.builder().tags(TagResolver.resolver(
                StandardTags.color(), StandardTags.decorations(), StandardTags.reset())).build();
        return safe.deserialize(input);
    }
}

