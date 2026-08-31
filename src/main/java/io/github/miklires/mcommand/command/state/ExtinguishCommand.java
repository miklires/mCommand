package io.github.miklires.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class ExtinguishCommand extends AbstractMCommand {

    public ExtinguishCommand(MCommand plugin) {
        super(plugin, "ext");
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
            target.setFireTicks(0);
            plugin.runFor(sender, () -> send(sender, "ext-done", "player", target.getName()));
        });
    }
}

