package io.github.miklires.mcommand.util;

import io.github.miklires.mcommand.MCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class MessageUtil {
    private final MCommand plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private YamlConfiguration messages;

    public MessageUtil(MCommand plugin) { this.plugin = plugin; reload(); }

    public void reload() {
        for (String locale : new String[]{"en_US", "ru_RU"}) {
            File file = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
            if (!file.exists()) plugin.saveResource("lang/" + locale + ".yml", false);
        }
        File selected = new File(plugin.getDataFolder(), "lang/" + safeLocale() + ".yml");
        messages = YamlConfiguration.loadConfiguration(selected);
        mergeBundled("lang/" + safeLocale() + ".yml");
        mergeBundled("lang/en_US.yml");
    }

    private void mergeBundled(String resource) {
        var stream = plugin.getResource(resource);
        if (stream == null) return;
        YamlConfiguration bundled = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
        bundled.getKeys(true).stream().filter(key -> !bundled.isConfigurationSection(key))
                .filter(key -> !messages.contains(key)).forEach(key -> messages.set(key, bundled.get(key)));
    }

    private String safeLocale() {
        String locale = plugin.getConfigManager().language();
        if (!locale.matches("[A-Za-z]{2}_[A-Za-z]{2}")) {
            plugin.getLogger().warning("Invalid language.default; using en_US");
            return "en_US";
        }
        File bundled = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!bundled.exists() && plugin.getResource("lang/" + locale + ".yml") == null) return "en_US";
        return locale;
    }

    public String get(String key) { return messages.getString(key, key); }
    public String prefix() { return get("prefix"); }
    public Component component(String key, String... replacements) {
        String template = prefix() + get(key);
        TagResolver.Builder resolver = TagResolver.builder();
        for (int index = 0; index + 1 < replacements.length; index += 2) {
            String name = replacements[index];
            template = template.replace("{" + name + "}", "<" + name + ">");
            resolver.resolver(Placeholder.unparsed(name, replacements[index + 1]));
        }
        return miniMessage.deserialize(template, resolver.build());
    }
    public void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(component(key, replacements));
    }
}
