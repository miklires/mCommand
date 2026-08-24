package ru.mossheaven.mcommand.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.mossheaven.mcommand.MCommand;

public class BackTrackingListener implements Listener {

    private final MCommand plugin;

    public BackTrackingListener(MCommand plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND
                || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN
                || event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            plugin.getBackLocationManager().save(event.getPlayer());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getBackLocationManager().save(event.getEntity());
    }
}
