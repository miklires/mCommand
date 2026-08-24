package ru.mossheaven.mcommand.command.utils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class TopCommand extends AbstractMCommand {

    public TopCommand(MCommand plugin) {
        super(plugin, "top");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;
        Location loc = player.getLocation();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int y = loc.getWorld().getHighestBlockYAt(x, z) + 1;
        Location top = new Location(loc.getWorld(), x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
        plugin.getBackLocationManager().save(player);
        player.teleport(top);
        send(sender, "top-done");
    }
}
