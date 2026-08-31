package io.github.miklires.mcommand.command;

import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.util.CommandSafety;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class ModernCommand implements CommandExecutor {
    private final MCommand plugin;
    public ModernCommand(MCommand plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase(Locale.ROOT);
        if (!sender.hasPermission("mcommand.command." + name)) { message(sender, "no-permission"); return true; }
        return switch (name) {
            case "near" -> near(sender, args);
            case "whois" -> whois(sender, args);
            case "endersee" -> endersee(sender, args);
            case "tp" -> teleport(sender, args);
            case "tphere" -> teleportHere(sender, args);
            case "tppos" -> teleportPosition(sender, args);
            case "tpall" -> teleportAll(sender, args);
            case "freeze", "unfreeze" -> freeze(sender, args, name.equals("freeze"));
            case "vanish" -> vanish(sender, args);
            case "grindstone" -> workstation(sender, InventoryType.GRINDSTONE);
            case "cartography" -> workstation(sender, InventoryType.CARTOGRAPHY);
            case "loom" -> workstation(sender, InventoryType.LOOM);
            case "smithing" -> workstation(sender, InventoryType.SMITHING);
            default -> false;
        };
    }

    private boolean near(CommandSender sender, String[] args) {
        Player player = player(sender); if (player == null) return true;
        double radius = 50;
        if (args.length > 0) try { radius = Double.parseDouble(args[0]); } catch (NumberFormatException e) { message(sender, "near-invalid"); return true; }
        if (!Double.isFinite(radius) || radius < 1 || radius > plugin.getConfigManager().maxNearRadius()) {
            message(sender, "near-range", "max", Integer.toString((int) plugin.getConfigManager().maxNearRadius())); return true;
        }
        double limit = radius * radius;
        List<String> values = player.getWorld().getPlayers().stream().filter(other -> other != player)
                .filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= limit)
                .filter(other -> player.canSee(other)).sorted(Comparator.comparingDouble(other -> other.getLocation().distanceSquared(player.getLocation())))
                .map(other -> other.getName() + " (" + Math.round(other.getLocation().distance(player.getLocation())) + "m)").toList();
        message(sender, values.isEmpty() ? "near-none" : "near-list", "players", String.join(", ", values));
        return true;
    }

    private boolean whois(CommandSender sender, String[] args) {
        if (args.length == 0) { message(sender, "whois-usage"); return true; }
        OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (offline == null || !offline.hasPlayedBefore()) { message(sender, "player-never-played"); return true; }
        Player online = offline.getPlayer();
        message(sender, "whois-player", "player", Objects.requireNonNullElse(offline.getName(), args[0]));
        message(sender, "whois-uuid", "uuid", offline.getUniqueId().toString());
        message(sender, "whois-first", "date", Instant.ofEpochMilli(offline.getFirstPlayed()).toString());
        long ticks = offline.getStatistic(Statistic.PLAY_ONE_MINUTE);
        message(sender, "whois-playtime", "hours", Long.toString(Duration.ofSeconds(ticks / 20).toHours()));
        if (online != null) {
            message(sender, "whois-location", "mode", online.getGameMode().name(), "world", online.getWorld().getName(),
                    "x", Integer.toString(online.getLocation().getBlockX()), "y", Integer.toString(online.getLocation().getBlockY()),
                    "z", Integer.toString(online.getLocation().getBlockZ()));
            if (sender.hasPermission("mcommand.command.whois.ip") && online.getAddress() != null)
                message(sender, "whois-ip", "ip", online.getAddress().getAddress().getHostAddress());
        }
        return true;
    }

    private boolean endersee(CommandSender sender, String[] args) {
        Player viewer = player(sender); if (viewer == null) return true;
        Player target = target(sender, args, 0); if (target == null) return true;
        if (target.hasPermission("mcommand.exempt.invsee") && !sender.hasPermission("mcommand.exempt.bypass")) { message(sender, "protected-player"); return true; }
        viewer.openInventory(target.getEnderChest()); return true;
    }

    private boolean teleport(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 2) { message(sender, "tp-usage"); return true; }
        Player moving;
        Player destination;
        if (args.length == 1) { moving = player(sender); destination = target(sender, args, 0); }
        else {
            if (!sender.hasPermission("mcommand.command.tp.other")) { message(sender, "no-permission"); return true; }
            moving = target(sender, args, 0); destination = target(sender, args, 1);
        }
        if (moving != null && destination != null) moveToPlayer(moving, destination, sender);
        return true;
    }

    private boolean teleportHere(CommandSender sender, String[] args) {
        Player destination = player(sender); if (destination == null) return true;
        Player moving = target(sender, args, 0); if (moving != null) move(moving, destination.getLocation(), sender);
        return true;
    }

    private boolean teleportPosition(CommandSender sender, String[] args) {
        Player moving = player(sender); if (moving == null) return true;
        if (args.length != 3) { message(sender, "tppos-usage"); return true; }
        try {
            double x = Double.parseDouble(args[0]), y = Double.parseDouble(args[1]), z = Double.parseDouble(args[2]);
            double max = plugin.getConfigManager().maxTeleportCoordinate();
            if (!CommandSafety.areCoordinatesAllowed(x, y, z, max,
                    moving.getWorld().getMinHeight(), moving.getWorld().getMaxHeight())) throw new NumberFormatException();
            move(moving, new Location(moving.getWorld(), x, y, z, moving.getYaw(), moving.getPitch()), sender);
        } catch (NumberFormatException e) { message(sender, "coordinates-invalid"); }
        return true;
    }

    private boolean teleportAll(CommandSender sender, String[] args) {
        Player destination = player(sender); if (destination == null) return true;
        if (args.length != 1 || !args[0].equalsIgnoreCase("confirm")) { message(sender, "tpall-confirm"); return true; }
        Location location = destination.getLocation();
        for (Player moving : Bukkit.getOnlinePlayers()) if (moving != destination) move(moving, location, sender);
        return true;
    }

    private boolean freeze(CommandSender sender, String[] args, boolean state) {
        Player target = target(sender, args, 0); if (target == null) return true;
        if (target.hasPermission("mcommand.exempt.freeze") && !sender.hasPermission("mcommand.exempt.bypass")) { message(sender, "protected-player"); return true; }
        plugin.getPlayerStateManager().setFrozen(target.getUniqueId(), state);
        message(sender, state ? "freeze-on" : "freeze-off", "player", target.getName()); return true;
    }

    private boolean vanish(CommandSender sender, String[] args) {
        Player target = args.length == 0 ? player(sender) : target(sender, args, 0); if (target == null) return true;
        if (target != sender && !sender.hasPermission("mcommand.command.vanish.other")) { message(sender, "no-permission"); return true; }
        boolean enabled = !plugin.getPlayerStateManager().isVanished(target.getUniqueId());
        plugin.getPlayerStateManager().setVanished(target.getUniqueId(), enabled);
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer == target || observer.hasPermission("mcommand.vanish.see")) continue;
            plugin.runFor(observer, () -> {
                if (enabled) observer.hidePlayer(plugin, target); else observer.showPlayer(plugin, target);
            });
        }
        message(sender, enabled ? "vanish-on" : "vanish-off", "player", target.getName()); return true;
    }

    private boolean workstation(CommandSender sender, InventoryType type) {
        Player player = player(sender); if (player != null) player.openInventory(Bukkit.createInventory(player, type)); return true;
    }

    private void move(Player player, Location destination, CommandSender sender) {
        plugin.runFor(player, () -> {
            plugin.getBackLocationManager().save(player);
            player.teleportAsync(destination).thenAccept(success ->
                    plugin.runFor(sender, () -> message(sender, success ? "teleport-done" : "teleport-failed")));
        });
    }

    private void moveToPlayer(Player moving, Player destination, CommandSender sender) {
        plugin.runFor(destination, () -> move(moving, destination.getLocation().clone(), sender));
    }

    private Player player(CommandSender sender) {
        if (sender instanceof Player value) return value;
        message(sender, "player-only"); return null;
    }
    private Player target(CommandSender sender, String[] args, int index) {
        if (args.length <= index) { message(sender, "player-required"); return null; }
        Player value = Bukkit.getPlayerExact(args[index]);
        if (value == null) message(sender, "player-not-found");
        return value;
    }
    public boolean isFrozen(UUID uuid) { return plugin.getPlayerStateManager().isFrozen(uuid); }
    public boolean isVanished(UUID uuid) { return plugin.getPlayerStateManager().isVanished(uuid); }
    private void message(CommandSender sender, String key, String... replacements) { plugin.getMessageUtil().send(sender, key, replacements); }
}
