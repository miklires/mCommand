package io.github.miklires.mcommand.command.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class AnvilCommand extends AbstractMCommand {

    public AnvilCommand(MCommand plugin) {
        super(plugin, "anvil");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        ((Player) sender).openAnvil(null, true);
        send(sender, "anvil-done");
    }
}

