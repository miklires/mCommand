package io.github.miklires.mcommand.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import io.github.miklires.mcommand.util.CommandSafety;

public class BroadcastCommand extends AbstractMCommand {

    public BroadcastCommand(MCommand plugin) {
        super(plugin, "broadcast");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            send(sender, "broadcast-usage");
            return;
        }
        String text = String.join(" ", args);
        if (!CommandSafety.isLengthAllowed(text, plugin.getConfigManager().maxBroadcastLength())) {
            send(sender, "input-too-long", "max", Integer.toString(plugin.getConfigManager().maxBroadcastLength()));
            return;
        }
        String template = plugin.getMessageUtil().get("broadcast-format").replace("{message}", "<message>");
        Bukkit.broadcast(mm.deserialize(template, Placeholder.component("message", parseUserText(sender, text))));
    }
}

