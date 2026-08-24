package ru.mossheaven.mcommand.command.state;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class FeedCommand extends AbstractMCommand {

    public FeedCommand(MCommand plugin) {
        super(plugin, "feed");
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
        target.setFoodLevel(20);
        target.setSaturation(20);
        send(sender, "feed-done", "player", target.getName());
    }
}
