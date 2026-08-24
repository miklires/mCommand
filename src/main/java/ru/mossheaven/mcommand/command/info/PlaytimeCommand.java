package ru.mossheaven.mcommand.command.info;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mossheaven.mcommand.MCommand;
import ru.mossheaven.mcommand.command.base.AbstractMCommand;

public class PlaytimeCommand extends AbstractMCommand {

    public PlaytimeCommand(MCommand plugin) {
        super(plugin, "playtime");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if (args.length >= 1) {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                send(sender, "player-never-played");
                return;
            }
        } else {
            if (!requirePlayer(sender)) return;
            target = (Player) sender;
        }
        int ticks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20L;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        String formatted = days + "д " + hours + "ч " + minutes + "м";
        send(sender, "playtime-result",
                "player", target.getName(),
                "time", formatted);
    }
}
