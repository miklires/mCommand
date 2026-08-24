package ru.mossheaven.mcommand.command.state;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class EnderChestCommand extends AbstractMCommand {

    public EnderChestCommand(MCommand plugin) {
        super(plugin, "ec");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;
        player.openInventory(player.getEnderChest());
    }
}
