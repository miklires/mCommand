package ru.mossheaven.mcommand.command.time;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class SunCommand extends AbstractMCommand {

    public SunCommand(MCommand plugin) {
        super(plugin, "sun");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        World world = sender instanceof Player p ? p.getWorld() : plugin.getServer().getWorlds().get(0);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(20 * 60 * 20);
        send(sender, "sun-done");
    }
}
