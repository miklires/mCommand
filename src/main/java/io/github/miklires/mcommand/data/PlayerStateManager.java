package io.github.miklires.mcommand.data;

import io.github.miklires.mcommand.MCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerStateManager {
    private final MCommand plugin;
    private final Path file;
    private final Set<UUID> frozen = ConcurrentHashMap.newKeySet();
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();

    public PlayerStateManager(MCommand plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("player-states.yml");
        load();
    }

    public boolean isFrozen(UUID uuid) { return frozen.contains(uuid); }
    public boolean isVanished(UUID uuid) { return vanished.contains(uuid); }

    public void setFrozen(UUID uuid, boolean value) {
        if (value ? frozen.add(uuid) : frozen.remove(uuid)) saveAsync();
    }

    public void setVanished(UUID uuid, boolean value) {
        if (value ? vanished.add(uuid) : vanished.remove(uuid)) saveAsync();
    }

    public void saveNow() { write(new HashSet<>(frozen), new HashSet<>(vanished)); }

    private void load() {
        if (!Files.isRegularFile(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
        readUuids(yaml.getStringList("frozen"), frozen);
        readUuids(yaml.getStringList("vanished"), vanished);
    }

    private void readUuids(Iterable<String> values, Set<UUID> target) {
        for (String value : values) {
            try { target.add(UUID.fromString(value)); }
            catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Ignored invalid UUID in player-states.yml: " + value);
            }
        }
    }

    private void saveAsync() {
        Set<UUID> frozenSnapshot = new HashSet<>(frozen);
        Set<UUID> vanishedSnapshot = new HashSet<>(vanished);
        plugin.getServer().getAsyncScheduler().runNow(plugin,
                task -> write(frozenSnapshot, vanishedSnapshot));
    }

    private synchronized void write(Set<UUID> frozenSnapshot, Set<UUID> vanishedSnapshot) {
        try {
            Files.createDirectories(file.getParent());
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("frozen", frozenSnapshot.stream().map(UUID::toString).sorted().toList());
            yaml.set("vanished", vanishedSnapshot.stream().map(UUID::toString).sorted().toList());
            yaml.save(temporary.toFile());
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException unsupportedAtomicMove) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not save player states: " + exception.getMessage());
        }
    }
}
