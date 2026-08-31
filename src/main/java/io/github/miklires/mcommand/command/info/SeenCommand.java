package io.github.miklires.mcommand.command.info;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SeenCommand extends AbstractMCommand {

    public SeenCommand(MCommand plugin) {
        super(plugin, "seen");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            send(sender, "seen-usage");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (target == null) { send(sender, "player-never-played"); return; }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            send(sender, "player-never-played");
            return;
        }
        if (target.isOnline()) {
            send(sender, "seen-online", "player", target.getName());
            return;
        }
        long lastSeen = target.getLastSeen();
        String fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(lastSeen));
        long diffMs = System.currentTimeMillis() - lastSeen;
        long days = diffMs / (1000L * 60 * 60 * 24);
        send(sender, "seen-offline",
                "player", target.getName(),
                "date", fmt,
                "days", String.valueOf(days));
    }
}

