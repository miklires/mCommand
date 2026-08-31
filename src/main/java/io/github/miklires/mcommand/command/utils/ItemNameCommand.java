package io.github.miklires.mcommand.command.utils;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import io.github.miklires.mcommand.util.CommandSafety;

public class ItemNameCommand extends AbstractMCommand {

    public ItemNameCommand(MCommand plugin) {
        super(plugin, "itemname");
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
        if (args.length == 0) {
            ItemMeta meta = stack.getItemMeta();
            meta.displayName(null);
            stack.setItemMeta(meta);
            send(sender, "itemname-cleared");
            return;
        }
        String text = String.join(" ", args);
        if (!CommandSafety.isLengthAllowed(text, plugin.getConfigManager().maxItemNameLength())) {
            send(sender, "input-too-long", "max", Integer.toString(plugin.getConfigManager().maxItemNameLength()));
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(parseUserText(sender, text).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        send(sender, "itemname-done");
    }
}

