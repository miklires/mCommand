package ru.mossheaven.mcommand.command.time;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class DayCommand extends AbstractMCommand {

    public DayCommand(MCommand plugin) {
        super(plugin, "day");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        World world = sender instanceof Player p ? p.getWorld() : plugin.getServer().getWorlds().get(0);
        world.setTime(1000);
        send(sender, "day-done");
    }
}
