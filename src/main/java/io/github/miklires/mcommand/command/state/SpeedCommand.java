package io.github.miklires.mcommand.command.state;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.base.AbstractMCommand;

public class SpeedCommand extends AbstractMCommand {

    public SpeedCommand(MCommand plugin) {
        super(plugin, "speed");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        Player player = (Player) sender;

        if (args.length == 0) {
            send(sender, "speed-usage");
            return;
        }

        String mode = "walk";
        String valueArg;
        if (args.length >= 2 && (args[0].equalsIgnoreCase("walk") || args[0].equalsIgnoreCase("fly"))) {
            mode = args[0].toLowerCase();
            valueArg = args[1];
        } else {
            valueArg = args[0];
            if (player.isFlying()) mode = "fly";
        }

        float value;
        try {
            value = Float.parseFloat(valueArg);
        } catch (NumberFormatException e) {
            send(sender, "speed-invalid");
            return;
        }
        if (value < 0 || value > 10) {
            send(sender, "speed-range");
            return;
        }
        int requested = (int) Math.ceil(value);
        if (requested > 0 && !sender.hasPermission("mcommand.command.speed.max." + requested)) {
            send(sender, "no-permission");
            return;
        }
        float scaled = value / 10f;
        if (mode.equals("fly")) {
            player.setFlySpeed(Math.min(1f, scaled));
        } else {
            player.setWalkSpeed(Math.min(1f, scaled));
        }
        send(sender, "speed-set", "mode", mode, "value", String.valueOf(value));
    }
}

