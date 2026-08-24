package io.github.miklires.mcommand.command.state;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

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

