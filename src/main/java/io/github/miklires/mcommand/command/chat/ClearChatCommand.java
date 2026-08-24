package io.github.miklires.mcommand.command.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class ClearChatCommand extends AbstractMCommand {

    public ClearChatCommand(MCommand plugin) {
        super(plugin, "clearchat");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) p.sendMessage(Component.empty());
        }
        send(sender, "clearchat-done");
    }
}

