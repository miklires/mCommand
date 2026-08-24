package io.github.miklires.mcommand.listener;

import io.github.miklires.mcommand.MCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class PlayerStateListener implements Listener {
    private final MCommand plugin;
    public PlayerStateListener(MCommand plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId()) || !event.hasChangedPosition()) return;
        event.setCancelled(true);
        event.getPlayer().sendActionBar(Component.text("You are frozen.", NamedTextColor.RED));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joining = event.getPlayer();
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getModernCommand().isVanished(online.getUniqueId())
                    && !joining.hasPermission("mcommand.vanish.see")) joining.hidePlayer(plugin, online);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player target
                && event.getWhoClicked() != target
                && !event.getWhoClicked().hasPermission("mcommand.command.invsee.modify")) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Player target
                && event.getWhoClicked() != target
                && !event.getWhoClicked().hasPermission("mcommand.command.invsee.modify")) event.setCancelled(true);
    }
}
