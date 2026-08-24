package io.github.miklires.mcommand.command.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class HatCommand extends AbstractMCommand {

    public HatCommand(MCommand plugin) {
        super(plugin, "hat");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            send(sender, "no-item-in-hand");
            return;
        }
        ItemStack currentHat = player.getInventory().getHelmet();
        player.getInventory().setHelmet(hand);
        player.getInventory().setItemInMainHand(currentHat);
        send(sender, "hat-done");
    }
}

