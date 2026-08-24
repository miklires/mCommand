package ru.mossheaven.mcommand.command.state;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mossheaven.mcommand.MCommand;

public class GamemodeCommand implements CommandExecutor {

    private final MCommand plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public GamemodeCommand(MCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String prefix = plugin.getMessageUtil().prefix();

        if (!plugin.getConfigManager().isCommandEnabled("gm")) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("disabled")));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("player-only")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("gm-usage")));
            return true;
        }

        GameMode mode = parseMode(args[0]);
        if (mode == null) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("gm-invalid")));
            return true;
        }

        String perm = "mcommand.command.gm." + mode.name().toLowerCase();
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("no-permission")));
            return true;
        }

        player.setGameMode(mode);
        sender.sendMessage(mm.deserialize(prefix
                + plugin.getMessageUtil().get("gm-set").replace("{mode}", localizedName(mode))));
        return true;
    }

    private GameMode parseMode(String arg) {
        return switch (arg.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private String localizedName(GameMode m) {
        return switch (m) {
            case SURVIVAL -> "Survival";
            case CREATIVE -> "Creative";
            case ADVENTURE -> "Adventure";
            case SPECTATOR -> "Spectator";
        };
    }
}
