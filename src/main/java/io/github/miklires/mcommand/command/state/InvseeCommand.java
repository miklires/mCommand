package io.github.miklires.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class InvseeCommand extends AbstractMCommand {

    public InvseeCommand(MCommand plugin) {
        super(plugin, "invsee");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        if (args.length == 0) {
            send(sender, "invsee-usage");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            send(sender, "player-not-found");
            return;
        }
        ((Player) sender).openInventory(target.getInventory());
    }
}

