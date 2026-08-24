package ru.mossheaven.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class GodCommand extends AbstractMCommand {

    public GodCommand(MCommand plugin) {
        super(plugin, "god");
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
        boolean newState = !target.isInvulnerable();
        target.setInvulnerable(newState);
        send(sender, newState ? "god-on" : "god-off", "player", target.getName());
        if (target != sender) {
            target.sendMessage(mm.deserialize(prefix() + msg(newState ? "god-on" : "god-off")
                    .replace("{player}", target.getName())));
        }
    }
}
