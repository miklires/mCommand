<div align="center">
  <h1>mCommand</h1>
  <p>Lightweight modular administration commands without economy, homes, kits, or legacy baggage.</p>

  <p>
    <a href="https://papermc.io/software/paper"><img alt="Paper" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg"></a>
    <a href="https://purpurmc.org"><img alt="Purpur" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg"></a>
    <a href="https://papermc.io/software/folia"><img alt="Folia" height="56" src="https://raw.githubusercontent.com/miklires/mCommand/main/docs/assets/folia-available.png"></a>
  </p>

  <p>
    <a href="https://github.com/miklires/mCommand"><img alt="GitHub" src="https://tr7zw.github.io/uikit/social_buttons_icon/Github-Button-64.png"></a>
    <a href="https://modrinth.com/project/mcommand"><img alt="Modrinth" src="https://tr7zw.github.io/uikit/social_buttons_icon/Modrinth-Button-64.png"></a>
    <a href="https://discord.gg/pes25cnWKy"><img alt="Discord" src="https://tr7zw.github.io/uikit/social_buttons_icon/Discord-Button-64.png"></a>
  </p>

  <p>
    <a href="https://bstats.org/plugin/bukkit/mCommand/33356"><img alt="bStats" src="https://img.shields.io/badge/bStats-33356-2F9BE6?style=for-the-badge"></a>
    <a href="https://github.com/miklires/mCommand/releases"><img alt="Release" src="https://img.shields.io/github/v/release/miklires/mCommand?style=for-the-badge"></a>
    <img alt="Java 25" src="https://img.shields.io/badge/Java-25-5382A1?style=for-the-badge">
  </p>
</div>

mCommand is designed for servers that only need a focused administration toolkit. Every command is registered through Brigadier, and whole modules can be disabled to avoid conflicts with EssentialsX or another command suite.

## What it does

- state tools: flight, god mode, heal, feed, repair, speed, extinguish, and game mode
- moderation: permission-aware staff GUI, inventory and ender chest inspection, persistent freeze and vanish, audited sudo, broadcast, and chat clearing
- teleportation: back, top, player teleport, teleport-here, coordinates, and confirmed teleport-all
- information: ping, last seen, playtime, nearby players, and privacy-aware whois
- portable workstations: crafting table, anvil, grindstone, cartography table, loom, and smithing table
- safe item names and lore with restricted MiniMessage tags unless an elevated formatting permission is granted
- persistent `/back` locations written outside the server tick
- `/serverinfo` TPS, MSPT, player, world, and memory diagnostics
- configurable input limits for broadcasts, sudo commands, item names, and lore

## Requirements

- Java 25
- Paper, Purpur, or Folia 26.2 (Minecraft 1.21.11)

Velocity is not required and no proxy artifact is included.

## Install

1. Put `mCommand-1.1.0.jar` in the server's `plugins` directory.
2. Start the server once.
3. Edit `plugins/mCommand/config.yml` and restart when changing command modules.

English is the default language. Set `language.default: ru_RU` for the bundled Russian translation.

## Configuration

The `state`, `utils`, `moderation`, `world`, and `info` modules can be toggled independently. A disabled module does not register its commands, so those literals and suggestions disappear and another plugin can own them. Individual commands have a second `commands.<name>.enabled` switch.

`limits.near-max-radius` caps `/near`; `limits.max-teleport-coordinate` validates `/tppos`. Length limits protect broadcast, sudo, item-name, and lore input. `moderation.freeze.allowed-commands` keeps help and reporting available to frozen players. `/mcommand reload` safely reloads messages and limits, but command registration changes require a restart.

## Commands

- state: `/fly`, `/god`, `/heal`, `/feed`, `/repair`, `/speed`, `/ext`, `/gm`
- moderation: `/staff <player>`, `/invsee`, `/endersee`, `/freeze`, `/unfreeze`, `/vanish`, `/sudo`, `/broadcast`, `/clearchat`
- teleport and utilities: `/tp`, `/tphere`, `/tppos`, `/tpall confirm`, `/back`, `/top`, `/hat`, `/clear`, `/skull`, `/itemname`, `/lore`
- workstations: `/wb`, `/anvil`, `/grindstone`, `/cartography`, `/loom`, `/smithing`
- information: `/ping`, `/seen`, `/playtime`, `/near [radius]`, `/whois <player>`, `/serverinfo`
- optional world module: `/day`, `/night`, `/sun`, `/rain`
- administration: `/mcommand reload`

Brigadier supplies command syntax and online-player suggestions. Commands that accept no player are player-only unless their behavior is meaningful from the console.

## Permissions

`mcommand.commands` grants the base command set and `mcommand.admin` adds sensitive capabilities. Give elevated nodes deliberately:

- `mcommand.command.sudo.self`, `.other`, `.console`; `mcommand.exempt.sudo`
- `mcommand.command.invsee.modify`, `.offline`; `mcommand.exempt.invsee`
- `mcommand.command.gm.other`, `mcommand.command.clear.other`, `mcommand.command.tp.other`
- `mcommand.command.fly.other`, `.god.other`, `.heal.other`, `.feed.other`, `.ext.other`
- `mcommand.command.vanish.other`, `mcommand.vanish.see`
- `mcommand.command.whois.ip` for full IP addresses
- `mcommand.command.speed.max.<1-10>` for speed ceilings
- `mcommand.format.unsafe` for interactive MiniMessage tags
- `mcommand.exempt.bypass` to override protected-player nodes

Base command permissions use `mcommand.command.<command>`. Exemptions default to false; administrative permissions default to operators.

## Telemetry and updates

mCommand uses [bStats plugin ID 33356](https://bstats.org/plugin/bukkit/mCommand/33356) for anonymous usage statistics. Disable it with `metrics.enabled: false`.

The update setting is separate from telemetry. Disable update discovery with `updates.enabled: false`; no JAR is downloaded or replaced automatically.

## Build

```bash
./gradlew clean build
```

The release JAR is written to `build/libs/mCommand-1.1.0.jar`. The project is licensed under the MIT License.
