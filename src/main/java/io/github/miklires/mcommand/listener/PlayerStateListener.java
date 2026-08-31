package io.github.miklires.mcommand.listener;

import io.github.miklires.mcommand.MCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class PlayerStateListener implements Listener {
    private final MCommand plugin;
    public PlayerStateListener(MCommand plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId()) || !event.hasChangedPosition()) return;
        event.setCancelled(true);
        event.getPlayer().sendActionBar(plugin.getMessageUtil().component("frozen-actionbar"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player
                && (plugin.getModernCommand().isFrozen(player.getUniqueId())
                || plugin.getModernCommand().isVanished(player.getUniqueId()))) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player
                && plugin.getModernCommand().isFrozen(player.getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player
                && plugin.getModernCommand().isVanished(player.getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getModernCommand().isFrozen(event.getPlayer().getUniqueId())) return;
        String command = event.getMessage().substring(1).split("\\s+", 2)[0].toLowerCase(java.util.Locale.ROOT);
        if (!plugin.getConfigManager().freezeAllowedCommands().contains(command)) {
            event.setCancelled(true);
            plugin.getMessageUtil().send(event.getPlayer(), "frozen-command-blocked");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joining = event.getPlayer();
        if (plugin.getModernCommand().isVanished(joining.getUniqueId())) {
            event.joinMessage(null);
            for (Player observer : plugin.getServer().getOnlinePlayers()) {
                if (observer != joining && !observer.hasPermission("mcommand.vanish.see")) {
                    plugin.runFor(observer, () -> observer.hidePlayer(plugin, joining));
                }
            }
        }
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getModernCommand().isVanished(online.getUniqueId())
                    && !joining.hasPermission("mcommand.vanish.see")) joining.hidePlayer(plugin, online);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getModernCommand().isVanished(event.getPlayer().getUniqueId())) event.quitMessage(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.getModernCommand().isFrozen(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (event.getInventory().getHolder() instanceof Player target
                && event.getWhoClicked() != target
                && !event.getWhoClicked().hasPermission("mcommand.command.invsee.modify")) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (plugin.getModernCommand().isFrozen(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (event.getInventory().getHolder() instanceof Player target
                && event.getWhoClicked() != target
                && !event.getWhoClicked().hasPermission("mcommand.command.invsee.modify")) event.setCancelled(true);
    }
}
