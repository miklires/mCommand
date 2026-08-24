package io.github.miklires.mcommand.command.utils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class BackCommand extends AbstractMCommand {

    public BackCommand(MCommand plugin) {
        super(plugin, "back");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;
        Location loc = plugin.getBackLocationManager().get(player.getUniqueId());
        if (loc == null) {
            send(sender, "back-none");
            return;
        }
        Location current = player.getLocation();
        plugin.getBackLocationManager().save(player);
        player.teleport(loc);
        send(sender, "back-done");
    }
}

