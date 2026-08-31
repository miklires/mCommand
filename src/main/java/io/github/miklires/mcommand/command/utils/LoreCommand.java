package io.github.miklires.mcommand.command.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import io.github.miklires.mcommand.util.CommandSafety;

import java.util.ArrayList;
import java.util.List;

public class LoreCommand extends AbstractMCommand {

    public LoreCommand(MCommand plugin) {
        super(plugin, "lore");
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
            send(sender, "lore-usage");
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        String sub = args[0].toLowerCase();

        switch (sub) {
            case "clear" -> {
                meta.lore(null);
                send(sender, "lore-cleared");
            }
            case "add" -> {
                if (args.length < 2) { send(sender, "lore-usage"); return; }
                String text = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                if (!CommandSafety.isLengthAllowed(text, plugin.getConfigManager().maxLoreLineLength())) {
                    send(sender, "input-too-long", "max", Integer.toString(plugin.getConfigManager().maxLoreLineLength()));
                    return;
                }
                if (lore.size() >= plugin.getConfigManager().maxLoreLines()) {
                    send(sender, "lore-too-many", "max", Integer.toString(plugin.getConfigManager().maxLoreLines()));
                    return;
                }
                lore.add(parseUserText(sender, text).decoration(TextDecoration.ITALIC, false));
                meta.lore(lore);
                send(sender, "lore-added");
            }
            default -> { send(sender, "lore-usage"); return; }
        }
        stack.setItemMeta(meta);
    }
}

