package io.github.miklires.mcommand.command.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class SudoCommand extends AbstractMCommand {

    public SudoCommand(MCommand plugin) {
        super(plugin, "sudo");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "sudo-usage");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            send(sender, "player-not-found");
            return;
        }
        String cmd = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        target.performCommand(cmd);
        send(sender, "sudo-done", "player", target.getName(), "command", cmd);
    }
}

