package io.github.miklires.mcommand.command.info;

import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public final class ServerInfoCommand extends AbstractMCommand {
    public ServerInfoCommand(MCommand plugin) { super(plugin, "serverinfo"); }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1_048_576L;
        long maximum = runtime.maxMemory() / 1_048_576L;
        double[] tps = Bukkit.getTPS();
        send(sender, "serverinfo-header", "version", Bukkit.getMinecraftVersion());
        send(sender, "serverinfo-performance", "tps1", format(tps[0]), "tps5", format(tps[1]),
                "tps15", format(tps[2]), "mspt", format(Bukkit.getAverageTickTime()));
        send(sender, "serverinfo-runtime", "online", Integer.toString(Bukkit.getOnlinePlayers().size()),
                "max", Integer.toString(Bukkit.getMaxPlayers()), "worlds", Integer.toString(Bukkit.getWorlds().size()),
                "used", Long.toString(used), "memory", Long.toString(maximum));
    }

    private String format(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
