package ru.mossheaven.mcommand.command.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

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
