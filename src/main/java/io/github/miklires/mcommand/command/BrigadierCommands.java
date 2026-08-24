package io.github.miklires.mcommand.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.miklires.mcommand.MCommand;
import io.github.miklires.mcommand.command.chat.BroadcastCommand;
import io.github.miklires.mcommand.command.chat.ClearChatCommand;
import io.github.miklires.mcommand.command.info.PingCommand;
import io.github.miklires.mcommand.command.info.PlaytimeCommand;
import io.github.miklires.mcommand.command.info.SeenCommand;
import io.github.miklires.mcommand.command.state.*;
import io.github.miklires.mcommand.command.time.*;
import io.github.miklires.mcommand.command.utils.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BrigadierCommands {
    private final MCommand plugin;
    public BrigadierCommands(MCommand plugin) { this.plugin = plugin; }

    public void register(Commands commands) {
        Map<String, CommandExecutor> legacy = new LinkedHashMap<>();
        legacy.put("fly", new FlyCommand(plugin)); legacy.put("god", new GodCommand(plugin));
        legacy.put("heal", new HealCommand(plugin)); legacy.put("feed", new FeedCommand(plugin));
        legacy.put("repair", new RepairCommand(plugin)); legacy.put("speed", new SpeedCommand(plugin));
        legacy.put("ext", new ExtinguishCommand(plugin)); legacy.put("invsee", new InvseeCommand(plugin));
        legacy.put("ec", new EnderChestCommand(plugin)); legacy.put("top", new TopCommand(plugin));
        legacy.put("back", new BackCommand(plugin)); legacy.put("hat", new HatCommand(plugin));
        legacy.put("anvil", new AnvilCommand(plugin)); legacy.put("wb", new WorkbenchCommand(plugin));
        legacy.put("clear", new ClearCommand(plugin)); legacy.put("skull", new SkullCommand(plugin));
        legacy.put("itemname", new ItemNameCommand(plugin)); legacy.put("lore", new LoreCommand(plugin));
        legacy.put("sudo", new SudoCommand(plugin)); legacy.put("day", new DayCommand(plugin));
        legacy.put("night", new NightCommand(plugin)); legacy.put("sun", new SunCommand(plugin));
        legacy.put("rain", new RainCommand(plugin)); legacy.put("ping", new PingCommand(plugin));
        legacy.put("seen", new SeenCommand(plugin)); legacy.put("playtime", new PlaytimeCommand(plugin));
        legacy.put("broadcast", new BroadcastCommand(plugin)); legacy.put("clearchat", new ClearChatCommand(plugin));
        legacy.put("gm", new GamemodeCommand(plugin));

        legacy.forEach((name, executor) -> register(commands, name, executor, aliases(name)));
        for (String name : List.of("near", "whois", "endersee", "tp", "tphere", "tppos", "tpall",
                "freeze", "unfreeze", "vanish", "grindstone", "cartography", "loom", "smithing")) {
            register(commands, name, plugin.getModernCommand(), aliases(name));
        }
        register(commands, "mcommand", new AdminCommand(plugin), List.of("mcmd"));
    }

    private void register(Commands commands, String name, CommandExecutor executor, List<String> aliases) {
        if (!name.equals("mcommand") && !plugin.getConfigManager().isCommandEnabled(name)) return;
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name)
                .executes(context -> invoke(executor, name, context));
        root.then(Commands.argument("arguments", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    String remaining = builder.getRemainingLowerCase();
                    plugin.getServer().getOnlinePlayers().stream().map(player -> player.getName())
                            .filter(value -> value.toLowerCase().startsWith(remaining)).forEach(builder::suggest);
                    for (String value : suggestions(name)) if (value.startsWith(remaining)) builder.suggest(value);
                    return builder.buildFuture();
                })
                .executes(context -> invoke(executor, name, context,
                        StringArgumentType.getString(context, "arguments").trim().split("\\s+"))));
        commands.register(root.build(), "mCommand: " + name, aliases);
    }

    private int invoke(CommandExecutor executor, String name, CommandContext<CommandSourceStack> context,
                       String... args) {
        return executor.onCommand(context.getSource().getSender(), new BridgeCommand(name), name, args) ? 1 : 0;
    }

    private List<String> aliases(String name) {
        return switch (name) {
            case "ext" -> List.of("extinguish"); case "wb" -> List.of("workbench", "craft");
            case "ec" -> List.of("enderchest"); case "broadcast" -> List.of("bc");
            case "clearchat" -> List.of("cc"); case "itemname" -> List.of("rename");
            default -> List.of();
        };
    }

    private List<String> suggestions(String name) {
        return switch (name) {
            case "gm" -> List.of("survival", "creative", "adventure", "spectator");
            case "speed" -> List.of("walk", "fly"); case "lore" -> List.of("add", "clear");
            case "tpall" -> List.of("confirm"); case "mcommand" -> List.of("reload");
            default -> List.of();
        };
    }

    private static final class BridgeCommand extends Command {
        BridgeCommand(String name) { super(name); }
        @Override public boolean execute(CommandSender sender, String label, String[] args) { return false; }
    }

    private static final class AdminCommand implements CommandExecutor {
        private final MCommand plugin;
        AdminCommand(MCommand plugin) { this.plugin = plugin; }
        @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0) {
                sender.sendMessage("mCommand " + plugin.getPluginMeta().getVersion());
                return true;
            }
            if (!sender.hasPermission("mcommand.admin")) { plugin.getMessageUtil().send(sender, "no-permission"); return true; }
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadPlugin(); plugin.getMessageUtil().send(sender, "reloaded"); return true;
            }
            plugin.getMessageUtil().send(sender, "usage-reload"); return true;
        }
    }
}
