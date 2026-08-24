package io.github.miklires.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class HealCommand extends AbstractMCommand {

    public HealCommand(MCommand plugin) {
        super(plugin, "heal");
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
        plugin.runFor(target, () -> {
            var attribute = target.getAttribute(Attribute.MAX_HEALTH);
            double max = attribute == null ? 20 : attribute.getValue();
            target.setHealth(max); target.setFoodLevel(20); target.setSaturation(20); target.setFireTicks(0);
            send(sender, "heal-done", "player", target.getName());
        });
    }
}

