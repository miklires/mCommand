package io.github.miklires.mcommand.command;

import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.util.CommandSafety;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.time.Duration;
import java.util.*;

public final class ModernCommand implements CommandExecutor {
    private final MCommand plugin;
    private final Set<UUID> frozen = Collections.synchronizedSet(new HashSet<>());
    private final Set<UUID> vanished = Collections.synchronizedSet(new HashSet<>());

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
        if (args.length > 0) try { radius = Double.parseDouble(args[0]); } catch (NumberFormatException e) { error(sender, "Radius must be a number."); return true; }
        if (!Double.isFinite(radius) || radius < 1 || radius > plugin.getConfigManager().maxNearRadius()) {
            error(sender, "Radius must be between 1 and " + (int) plugin.getConfigManager().maxNearRadius() + '.'); return true;
        }
        double limit = radius * radius;
        List<String> values = player.getWorld().getPlayers().stream().filter(other -> other != player)
                .filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= limit)
                .filter(other -> player.canSee(other)).sorted(Comparator.comparingDouble(other -> other.getLocation().distanceSquared(player.getLocation())))
                .map(other -> other.getName() + " (" + Math.round(other.getLocation().distance(player.getLocation())) + "m)").toList();
        sender.sendMessage(Component.text(values.isEmpty() ? "No visible players nearby." : String.join(", ", values), NamedTextColor.GRAY));
        return true;
    }

    private boolean whois(CommandSender sender, String[] args) {
        if (args.length == 0) { error(sender, "Usage: /whois <player>"); return true; }
        OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (offline == null || !offline.hasPlayedBefore()) { message(sender, "player-never-played"); return true; }
        Player online = offline.getPlayer();
        sender.sendMessage(Component.text("Player: " + Objects.requireNonNullElse(offline.getName(), args[0]), NamedTextColor.GOLD));
        sender.sendMessage(Component.text("UUID: " + offline.getUniqueId(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("First joined: " + new Date(offline.getFirstPlayed()), NamedTextColor.GRAY));
        long ticks = offline.getStatistic(Statistic.PLAY_ONE_MINUTE);
        sender.sendMessage(Component.text("Playtime: " + Duration.ofSeconds(ticks / 20).toHours() + "h", NamedTextColor.GRAY));
        if (online != null) {
            sender.sendMessage(Component.text("Mode: " + online.getGameMode() + ", world: " + online.getWorld().getName()
                    + ", position: " + online.getLocation().getBlockX() + " " + online.getLocation().getBlockY() + " " + online.getLocation().getBlockZ(), NamedTextColor.GRAY));
            if (sender.hasPermission("mcommand.command.whois.ip") && online.getAddress() != null)
                sender.sendMessage(Component.text("IP: " + online.getAddress().getAddress().getHostAddress(), NamedTextColor.GRAY));
        }
        return true;
    }

    private boolean endersee(CommandSender sender, String[] args) {
        Player viewer = player(sender); if (viewer == null) return true;
        Player target = target(sender, args, 0); if (target == null) return true;
        if (target.hasPermission("mcommand.exempt.invsee") && !sender.hasPermission("mcommand.exempt.bypass")) { error(sender, "That player is protected."); return true; }
        viewer.openInventory(target.getEnderChest()); return true;
    }

    private boolean teleport(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 2) { error(sender, "Usage: /tp [player] <target>"); return true; }
        Player moving;
        Player destination;
        if (args.length == 1) { moving = player(sender); destination = target(sender, args, 0); }
        else {
            if (!sender.hasPermission("mcommand.command.tp.other")) { message(sender, "no-permission"); return true; }
            moving = target(sender, args, 0); destination = target(sender, args, 1);
        }
        if (moving != null && destination != null) move(moving, destination.getLocation(), sender);
        return true;
    }

    private boolean teleportHere(CommandSender sender, String[] args) {
        Player destination = player(sender); if (destination == null) return true;
        Player moving = target(sender, args, 0); if (moving != null) move(moving, destination.getLocation(), sender);
        return true;
    }

    private boolean teleportPosition(CommandSender sender, String[] args) {
        Player moving = player(sender); if (moving == null) return true;
        if (args.length != 3) { error(sender, "Usage: /tppos <x> <y> <z>"); return true; }
        try {
            double x = Double.parseDouble(args[0]), y = Double.parseDouble(args[1]), z = Double.parseDouble(args[2]);
            double max = plugin.getConfigManager().maxTeleportCoordinate();
            if (!CommandSafety.areCoordinatesAllowed(x, y, z, max,
                    moving.getWorld().getMinHeight(), moving.getWorld().getMaxHeight())) throw new NumberFormatException();
            move(moving, new Location(moving.getWorld(), x, y, z, moving.getYaw(), moving.getPitch()), sender);
        } catch (NumberFormatException e) { error(sender, "Coordinates are outside the allowed world bounds."); }
        return true;
    }

    private boolean teleportAll(CommandSender sender, String[] args) {
        Player destination = player(sender); if (destination == null) return true;
        if (args.length != 1 || !args[0].equalsIgnoreCase("confirm")) { error(sender, "Run /tpall confirm to teleport every online player."); return true; }
        Location location = destination.getLocation();
        for (Player moving : Bukkit.getOnlinePlayers()) if (moving != destination) move(moving, location, sender);
        return true;
    }

    private boolean freeze(CommandSender sender, String[] args, boolean state) {
        Player target = target(sender, args, 0); if (target == null) return true;
        if (target.hasPermission("mcommand.exempt.freeze") && !sender.hasPermission("mcommand.exempt.bypass")) { error(sender, "That player is protected."); return true; }
        if (state) frozen.add(target.getUniqueId()); else frozen.remove(target.getUniqueId());
        sender.sendMessage(Component.text(target.getName() + (state ? " frozen." : " unfrozen."), NamedTextColor.GREEN)); return true;
    }

    private boolean vanish(CommandSender sender, String[] args) {
        Player target = args.length == 0 ? player(sender) : target(sender, args, 0); if (target == null) return true;
        if (target != sender && !sender.hasPermission("mcommand.command.vanish.other")) { message(sender, "no-permission"); return true; }
        boolean enabled = vanished.add(target.getUniqueId()); if (!enabled) vanished.remove(target.getUniqueId());
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer == target || observer.hasPermission("mcommand.vanish.see")) continue;
            if (enabled) observer.hidePlayer(plugin, target); else observer.showPlayer(plugin, target);
        }
        sender.sendMessage(Component.text("Vanish " + (enabled ? "enabled" : "disabled") + " for " + target.getName() + '.', NamedTextColor.GREEN)); return true;
    }

    private boolean workstation(CommandSender sender, InventoryType type) {
        Player player = player(sender); if (player != null) player.openInventory(Bukkit.createInventory(player, type)); return true;
    }

    private void move(Player player, Location destination, CommandSender sender) {
        plugin.getBackLocationManager().save(player);
        player.teleportAsync(destination).thenAccept(success -> {
            if (!success) error(sender, "Teleport failed.");
        });
    }

    private Player player(CommandSender sender) {
        if (sender instanceof Player value) return value;
        message(sender, "player-only"); return null;
    }
    private Player target(CommandSender sender, String[] args, int index) {
        if (args.length <= index) { error(sender, "A player name is required."); return null; }
        Player value = Bukkit.getPlayerExact(args[index]);
        if (value == null) message(sender, "player-not-found");
        return value;
    }
    public boolean isFrozen(UUID uuid) { return frozen.contains(uuid); }
    public boolean isVanished(UUID uuid) { return vanished.contains(uuid); }
    private void message(CommandSender sender, String key) { plugin.getMessageUtil().send(sender, key); }
    private void error(CommandSender sender, String value) { sender.sendMessage(Component.text(value, NamedTextColor.RED)); }
}
