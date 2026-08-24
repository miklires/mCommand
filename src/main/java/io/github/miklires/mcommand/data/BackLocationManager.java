package io.github.miklires.mcommand.data;

import io.github.miklires.mcommand.MCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BackLocationManager {
    private final MCommand plugin;
    private final Path file;
    private final Map<UUID, StoredLocation> locations = new ConcurrentHashMap<>();

    public BackLocationManager(MCommand plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("back-locations.db");
        load();
    }

    public void save(Player player) {
        Location value = player.getLocation();
        locations.put(player.getUniqueId(), StoredLocation.from(value));
        Map<UUID, StoredLocation> snapshot = new HashMap<>(locations);
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> write(snapshot));
    }

    public Location get(UUID uuid) {
        StoredLocation stored = locations.get(uuid);
        if (stored == null) return null;
        World world = Bukkit.getWorld(stored.world());
        return world == null ? null : stored.toLocation(world);
    }

    public void clear(UUID uuid) { locations.remove(uuid); }
    public void saveNow() { write(new HashMap<>(locations)); }

    private void load() {
        if (!Files.isRegularFile(file)) return;
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] fields = line.split("\\|", -1);
                if (fields.length != 8) continue;
                try {
                    UUID player = UUID.fromString(fields[0]);
                    locations.put(player, new StoredLocation(UUID.fromString(fields[1]),
                            Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]),
                            Float.parseFloat(fields[5]), Float.parseFloat(fields[6])));
                } catch (IllegalArgumentException ignored) { }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Could not load back locations: " + e.getMessage());
        }
    }

    private synchronized void write(Map<UUID, StoredLocation> snapshot) {
        try {
            Files.createDirectories(file.getParent());
            Path temp = file.resolveSibling(file.getFileName() + ".tmp");
            StringBuilder output = new StringBuilder();
            snapshot.forEach((player, location) -> output.append(player).append('|').append(location.world())
                    .append('|').append(location.x()).append('|').append(location.y()).append('|').append(location.z())
                    .append('|').append(location.yaw()).append('|').append(location.pitch()).append("|1\n"));
            Files.writeString(temp, output, StandardCharsets.UTF_8);
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException unsupportedAtomicMove) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save back locations: " + e.getMessage());
        }
    }

    private record StoredLocation(UUID world, double x, double y, double z, float yaw, float pitch) {
        static StoredLocation from(Location value) {
            return new StoredLocation(value.getWorld().getUID(), value.getX(), value.getY(), value.getZ(),
                    value.getYaw(), value.getPitch());
        }
        Location toLocation(World value) { return new Location(value, x, y, z, yaw, pitch); }
    }
}
