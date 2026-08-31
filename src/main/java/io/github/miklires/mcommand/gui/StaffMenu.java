package io.github.miklires.mcommand.gui;

import io.github.miklires.mcommand.MCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public final class StaffMenu implements Listener {
    private final MCommand plugin;

    public StaffMenu(MCommand plugin) { this.plugin = plugin; }

    public void open(Player viewer, Player target) {
        StaffHolder holder = new StaffHolder(target.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, 27,
                plugin.getMessageUtil().body("staff-title", "player", target.getName()));
        holder.inventory = inventory;
        inventory.setItem(10, head(target));
        inventory.setItem(12, item(Material.CHEST, "staff-inventory", "staff-inventory-lore"));
        inventory.setItem(13, item(Material.ENDER_CHEST, "staff-ender", "staff-ender-lore"));
        inventory.setItem(14, item(plugin.getPlayerStateManager().isFrozen(target.getUniqueId())
                ? Material.MAGMA_CREAM : Material.PACKED_ICE,
                plugin.getPlayerStateManager().isFrozen(target.getUniqueId()) ? "staff-unfreeze" : "staff-freeze",
                "staff-freeze-lore"));
        inventory.setItem(15, item(Material.ENDER_EYE,
                plugin.getPlayerStateManager().isVanished(target.getUniqueId()) ? "staff-unvanish" : "staff-vanish",
                "staff-vanish-lore"));
        inventory.setItem(16, item(Material.COMPASS, "staff-teleport", "staff-teleport-lore"));
        viewer.openInventory(inventory);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StaffHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player viewer) || event.getClickedInventory() != event.getInventory()) return;
        Player target = Bukkit.getPlayer(holder.target);
        if (target == null) {
            viewer.closeInventory();
            plugin.getMessageUtil().send(viewer, "player-not-found");
            return;
        }
        String command = switch (event.getRawSlot()) {
            case 12 -> "invsee " + target.getName();
            case 13 -> "endersee " + target.getName();
            case 14 -> (plugin.getPlayerStateManager().isFrozen(target.getUniqueId()) ? "unfreeze " : "freeze ") + target.getName();
            case 15 -> "vanish " + target.getName();
            case 16 -> "tp " + target.getName();
            default -> null;
        };
        if (command == null) return;
        viewer.closeInventory();
        viewer.performCommand(command);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof StaffHolder) event.setCancelled(true);
    }

    private ItemStack head(Player target) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(plugin.getMessageUtil().body("staff-player", "player", target.getName()));
        meta.lore(List.of(plugin.getMessageUtil().body("staff-player-lore", "uuid", target.getUniqueId().toString())));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack item(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(plugin.getMessageUtil().body(name));
        meta.lore(List.of(plugin.getMessageUtil().body(lore)));
        item.setItemMeta(meta);
        return item;
    }

    private static final class StaffHolder implements InventoryHolder {
        private final UUID target;
        private Inventory inventory;
        private StaffHolder(UUID target) { this.target = target; }
        @Override public Inventory getInventory() { return inventory; }
    }
}
