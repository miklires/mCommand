# Changelog

## 1.1.0 - 2026-08-31

### Added

- permission-aware `/staff <player>` menu and `/serverinfo` performance diagnostics
- persistent freeze and vanish states with atomic storage
- configurable allowed commands for frozen players and input-size limits
- English and Russian messages for every command path, with missing-key fallback for existing installations

### Changed

- configuration schema upgraded to version 2 while preserving administrator values
- freeze now blocks movement, interaction, inventory actions, drops, pickups, damage, and unapproved commands
- vanish now persists, suppresses join and quit messages, blocks pickups and mob targeting, and restores visibility rules on join
- player and world mutations use entity/global schedulers for Paper and Folia ownership

### Security

- split self and other-player permissions for fly, god, heal, feed, and extinguish
- added sudo audit logging and global-region console execution
- bounded broadcast, sudo, item-name, and lore input using Unicode code points
- removed blocking lookups for unknown offline player names

## 1.0.1 - 2026-08-24

### Fixed

- release packaging for Linux-based CI and distribution

## 1.0.0 - 2026-08-24

### Added

- modular state, utility, moderation, world, and information command groups
- Brigadier command registration and suggestions
- nearby-player, whois, ender chest, teleport, freeze, vanish, and workstation commands
- persistent back locations, English and Russian messages, bStats, and update settings

### Security

- split sensitive permissions for sudo, inventories, teleportation, game mode, clearing, and IP display
- protected players from sudo, inventory inspection, and freezing
- restricted MiniMessage input and rejected sudo privilege escalation
