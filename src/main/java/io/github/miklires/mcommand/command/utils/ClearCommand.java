package io.github.miklires.mcommand.command.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class ClearCommand extends AbstractMCommand {

    public ClearCommand(MCommand plugin) {
        super(plugin, "clear");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 1) {
            if (!sender.hasPermission("mcommand.command.clear.other")) { send(sender, "no-permission"); return; }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found"); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = (Player) sender;
        }
        target.getInventory().clear();
        send(sender, "clear-done", "player", target.getName());
    }
}

