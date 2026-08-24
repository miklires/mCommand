package ru.mossheaven.mcommand.command.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class WorkbenchCommand extends AbstractMCommand {

    public WorkbenchCommand(MCommand plugin) {
        super(plugin, "wb");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        ((Player) sender).openWorkbench(null, true);
        send(sender, "wb-done");
    }
}
