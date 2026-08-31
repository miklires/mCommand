package io.github.miklires.mcommand.command.moderation;

import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StaffCommand extends AbstractMCommand {
    public StaffCommand(MCommand plugin) { super(plugin, "staff"); }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        if (args.length != 1) { send(sender, "staff-usage"); return; }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found"); return; }
        plugin.getStaffMenu().open((Player) sender, target);
    }
}
