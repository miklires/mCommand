package ru.mossheaven.mcommand.command.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class SkullCommand extends AbstractMCommand {

    public SkullCommand(MCommand plugin) {
        super(plugin, "skull");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        if (args.length == 0) {
            send(sender, "skull-usage");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(target);
        skull.setItemMeta(meta);
        ((Player) sender).getInventory().addItem(skull);
        send(sender, "skull-done", "player", args[0]);
    }
}
