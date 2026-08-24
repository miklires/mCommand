package io.github.miklires.mcommand.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

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
        String template = plugin.getMessageUtil().get("broadcast-format");
        template = template.replace("{message}", text);
        Bukkit.broadcast(mm.deserialize(template));
    }
}

