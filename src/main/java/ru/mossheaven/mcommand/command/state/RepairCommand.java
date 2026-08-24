package ru.mossheaven.mcommand.command.state;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class RepairCommand extends AbstractMCommand {

    public RepairCommand(MCommand plugin) {
        super(plugin, "repair");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack == null || stack.getType().isAir()) {
            send(sender, "no-item-in-hand");
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof Damageable d)) {
            send(sender, "not-repairable");
            return;
        }
        d.setDamage(0);
        stack.setItemMeta(meta);
        send(sender, "repair-done");
    }
}
