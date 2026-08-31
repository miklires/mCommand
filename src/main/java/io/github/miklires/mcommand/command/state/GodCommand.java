package io.github.miklires.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class GodCommand extends AbstractMCommand {

    public GodCommand(MCommand plugin) {
        super(plugin, "god");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found"); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = (Player) sender;
        }
        if (!requireOtherPermission(sender, target)) return;
        plugin.runFor(target, () -> {
            boolean newState = !target.isInvulnerable();
            target.setInvulnerable(newState);
            plugin.runFor(sender, () -> send(sender, newState ? "god-on" : "god-off", "player", target.getName()));
            if (target != sender) plugin.getMessageUtil().send(target, newState ? "god-on" : "god-off", "player", target.getName());
        });
    }
}

