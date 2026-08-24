package io.github.miklires.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class FlyCommand extends AbstractMCommand {

    public FlyCommand(MCommand plugin) {
        super(plugin, "fly");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                send(sender, "player-not-found");
                return;
            }
        } else {
            if (!requirePlayer(sender)) return;
            target = (Player) sender;
        }
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        target.setFlying(newState);
        send(sender, newState ? "fly-on" : "fly-off", "player", target.getName());
        if (target != sender) {
            target.sendMessage(mm.deserialize(prefix() + msg(newState ? "fly-on" : "fly-off")
                    .replace("{player}", target.getName())));
        }
    }
}

