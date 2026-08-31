package io.github.miklires.mcommand.command.time;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class RainCommand extends AbstractMCommand {

    public RainCommand(MCommand plugin) {
        super(plugin, "rain");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        World world = sender instanceof Player p ? p.getWorld() : plugin.getServer().getWorlds().get(0);
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            world.setStorm(true);
            world.setWeatherDuration(20 * 60 * 10);
            plugin.runFor(sender, () -> send(sender, "rain-done"));
        });
    }
}

