package io.github.miklires.mcommand.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackLocationManager {

    private final Map<UUID, Location> lastLocation = new ConcurrentHashMap<>();

    public void save(Player player) {
        lastLocation.put(player.getUniqueId(), player.getLocation().clone());
    }

    public Location get(UUID uuid) {
        return lastLocation.get(uuid);
    }

    public void clear(UUID uuid) {
        lastLocation.remove(uuid);
    }
}

