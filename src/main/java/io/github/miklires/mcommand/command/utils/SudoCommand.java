package io.github.miklires.mcommand.command.utils;

import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import io.github.miklires.mcommand.util.CommandSafety;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Arrays;

public final class SudoCommand extends AbstractMCommand {
    public SudoCommand(MCommand plugin) { super(plugin, "sudo"); }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) { send(sender, "sudo-usage"); return; }
        String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replaceFirst("^/", "");
        if (!CommandSafety.isSingleCommand(command)
                || !CommandSafety.isLengthAllowed(command, plugin.getConfigManager().maxSudoLength())) {
            send(sender, "sudo-usage"); return;
        }
        if (args[0].equalsIgnoreCase("console")) {
            if (!sender.hasPermission("mcommand.command.sudo.console")) { send(sender, "no-permission"); return; }
            plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                plugin.getLogger().info(sender.getName() + " used sudo as console: /" + command);
                plugin.runFor(sender, () -> send(sender, "sudo-done", "player", "console", "command", command));
            });
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found"); return; }
        String scope = sender == target ? "self" : "other";
        if (!sender.hasPermission("mcommand.command.sudo." + scope)) { send(sender, "no-permission"); return; }
        if (target.hasPermission("mcommand.exempt.sudo") && !sender.hasPermission("mcommand.exempt.bypass")) {
            send(sender, "no-permission"); return;
        }
        if (sender != target && hasPermissionTargetDoesNotShare(sender, target)) {
            send(sender, "sudo-privilege-refused");
            return;
        }
        plugin.runFor(target, () -> {
            target.performCommand(command);
            plugin.getLogger().info(sender.getName() + " used sudo as " + target.getName() + ": /" + command);
            plugin.runFor(sender, () -> send(sender, "sudo-done", "player", target.getName(), "command", command));
        });
    }

    private boolean hasPermissionTargetDoesNotShare(CommandSender sender, Player target) {
        if (target.isOp() && !sender.isOp()) return true;
        for (PermissionAttachmentInfo permission : target.getEffectivePermissions()) {
            if (permission.getValue() && !sender.hasPermission(permission.getPermission())) return true;
        }
        return false;
    }
}
