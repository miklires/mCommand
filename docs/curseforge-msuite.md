# mSuite

mSuite is the single CurseForge distribution for the independently installable miklires server plugins. The bundle targets Minecraft 26.2, Java 25, Paper, Purpur, and Folia. Optional proxy modules target Velocity.

## Installation

1. Download the `mSuite-<version>.zip` file and extract it.
2. Copy only the JARs you want from `paper/` into the Paper, Purpur, or Folia `plugins/` directory.
3. When a selected plugin includes a matching proxy module, copy that JAR from `velocity/` into the Velocity `plugins/` directory.
4. Review each component's linked GitHub release for dependencies and configuration details.

The plugins are independent unless their own documentation lists a hard dependency. You do not need to install the whole suite.

## Included plugins

- **mAuction** — persistent player marketplace and guarded Vault transactions.
- **mAuth** — offline-mode and mixed-mode authentication with optional Discord, Telegram, and Velocity modules.
- **mBadges** — graphical player badges, collections, and resource-pack support.
- **mBans** — cross-server punishment management with optional Velocity enforcement.
- **mCases** — configurable reward cases with exact chances and recovery safeguards.
- **mChat** — chat channels, interactive messages, and moderation controls.
- **mClans** — persistent clans, relations, banks, homes, and rankings.
- **mCodes** — transactional promo-code and referral campaigns.
- **mColor** — player-name colors, gradients, presets, and network synchronization.
- **mCommand** — focused modular administration commands.
- **mCraft** — custom items and exact crafting recipes.
- **mEvents** — scheduled airdrops and contribution-based boss events.
- **mMotd** — cached, scheduled server-list MOTDs and maintenance controls.
- **mProfile** — private-by-design player profiles and social interactions.
- **mProtect** — exploit, lag-machine, item, packet, and event-spam protection.
- **mReports** — auditable player reports and moderation queues.
- **mReputation** — transparent, auditable community reputation.

## Source code and individual downloads

Every component remains available independently on GitHub and Modrinth. The bundle's `manifest.json` records the exact version and GitHub release URL used for every included component.

- Source organization: https://github.com/miklires
- Support: https://discord.gg/pes25cnWKy
