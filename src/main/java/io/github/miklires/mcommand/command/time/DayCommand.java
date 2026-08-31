package io.github.miklires.mcommand.command.time;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class DayCommand extends AbstractMCommand {

    public DayCommand(MCommand plugin) {
        super(plugin, "day");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        World world = sender instanceof Player p ? p.getWorld() : plugin.getServer().getWorlds().get(0);
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            world.setTime(1000);
            plugin.runFor(sender, () -> send(sender, "day-done"));
        });
    }
}

