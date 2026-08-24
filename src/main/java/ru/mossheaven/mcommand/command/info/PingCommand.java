package ru.mossheaven.mcommand.command.info;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class PingCommand extends AbstractMCommand {

    public PingCommand(MCommand plugin) {
        super(plugin, "ping");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found"); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = (Player) sender;
        }
        send(sender, "ping-result", "player", target.getName(), "ping", String.valueOf(target.getPing()));
    }
}
