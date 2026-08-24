package io.github.miklires.mcommand.command.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

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

