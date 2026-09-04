# Orefinder Plugin

[![Java CI with Maven](https://github.com/mark-iid/orefinder/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/mark-iid/orefinder/actions/workflows/maven.yml)
[![CodeQL](https://github.com/mark-iid/orefinder/actions/workflows/github-code-scanning/codeql/badge.svg?branch=main)](https://github.com/mark-iid/orefinder/security/code-scanning)
[![Latest release](https://img.shields.io/github/v/release/mark-iid/orefinder)](https://github.com/mark-iid/orefinder/releases)
[![License: GPL-3.0](https://img.shields.io/github/license/mark-iid/orefinder)](LICENSE.md)
[![Java 25](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/projects/jdk/25/)
[![Minecraft 26.2](https://img.shields.io/badge/Minecraft-26.2-brightgreen)](https://www.minecraft.net/)

Orefinder is a Minecraft plugin that helps players locate ores by providing distance-based clues when they hit blocks while holding specific items. Hold a diamond and punch a wall — the plugin tells you how close the nearest diamond ore is.

Orefinder was forked from https://www.curseforge.com/minecraft/bukkit-plugins/orefinder-bukkit which appears to have been abandoned.

## Features

- Reports the distance to the nearest configured ore block as a "hot/cold" chat message.
- Configurable item-to-ore pairings — any held item can point at any block type.
- Per-player cooldown of one lookup per second, so the search cannot be spammed.
- Optional block-stealing "enderman" mechanic with a configurable chance.
- `/orefinder reload` to reload `config.yml` at runtime without restarting the server.

## How it works

When a player left-clicks a block while holding a configured item, the plugin searches
outward from the clicked block in expanding cubic shells and reports the
[Chebyshev distance](https://en.wikipedia.org/wiki/Chebyshev_distance) to the nearest
matching block, up to a radius of **20 blocks**. Blocks outside the world's build height
are skipped, and block-type matching is case-insensitive.

The distance is mapped to a message from `config.yml`:

| Distance  | Message key         | Default text    | Colour   |
| --------- | ------------------- | --------------- | -------- |
| 0–1       | `text.oneblock_hot` | One block away! | dark red |
| 2–3       | `text.very_hot`     | Very hot!       | red      |
| 4–5       | `text.hot`          | Hot!            | red      |
| 6–7       | `text.warm`         | Warm!           | gold     |
| 8–14      | `text.lukewarm`     | Lukewarm.       | yellow   |
| 15–19     | `text.cold`         | Cold.           | aqua     |
| not found | `text.very_cold`    | Ice cold!       | blue     |

## Requirements

- A Minecraft **26.2** server running Bukkit, Spigot, or Paper (`api-version: '26.2'`)
- **Java 25** or higher
- Maven (to build from source)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/mark-iid/orefinder.git
    cd orefinder
    ```

2. Build the project using Maven:
    ```sh
    mvn clean package
    ```

3. Copy the generated `target/Orefinder-<version>.jar` (e.g. `Orefinder-1.4.jar`) to your
   server's `plugins` directory.

4. Start or restart your Minecraft server. A default `config.yml` is written to
   `plugins/OreFinder/` on first run.

Prebuilt jars are also attached to the [releases](https://github.com/mark-iid/orefinder/releases),
and every CI build uploads a `Package` artifact.

## Configuration

`config.yml` is created on first startup and can be reloaded in-game with
`/orefinder reload`.

```yaml
# Messages to players
text:
    oneblock_hot: One block away!
    very_hot: Very hot!
    hot: Hot!
    warm: Warm!
    lukewarm: Lukewarm.
    cold: Cold.
    very_cold: Ice cold!
    ender_steal: An enderman stole your block!

# 48 means a 1/48 chance per lookup
chance:
    steal_block: 48

# Turn on/off optional functions
functions:
    block_stealing: false

# Item/Block id
indicate:
  inhand:
  - diamond
  - emerald
  - ancient_debris
  lookfor:
  - diamond_ore
  - emerald_ore
  - ancient_debris
```

| Setting | Description |
| ------- | ----------- |
| `text.*` | Messages sent to the player for each distance band (see the table above). |
| `chance.steal_block` | Denominator of the steal chance — `48` means a 1-in-48 chance on each lookup. |
| `functions.block_stealing` | Enables the enderman mechanic: on a successful roll the player takes 1 damage and, in survival mode, loses one of the held items. |
| `indicate.inhand` | Items that activate the detector when held in the main hand. |
| `indicate.lookfor` | Block types to search for. |

`indicate.inhand` and `indicate.lookfor` are paired **by position**: the first item in
`inhand` looks for the first block in `lookfor`, and so on. If the lists are different
lengths the extra entries are ignored, and the plugin fails to start if either list is
empty. Names are Bukkit `Material` names and are matched case-insensitively.

## Usage

1. Ensure you have the `orefinder.use` permission (granted to everyone by default).
2. Hold one of the configured items (e.g. a diamond) in your main hand.
3. Left-click any block to receive a distance-based clue about the paired ore.

## Commands

- `/orefinder reload`: Reloads `config.yml` from disk and rebuilds the item/ore mappings
  without restarting the server. Requires the `orefinder.reload` permission.

## Permissions

| Permission | Default | Description |
| ---------- | ------- | ----------- |
| `orefinder.use` | everyone | Allows the player to use the Orefinder detector. |
| `orefinder.reload` | operators | Allows reloading the configuration via `/orefinder reload`. |
| `orefinder.*` | — | Grants both of the above. |

## Development

### Prerequisites

- Java 25 (the build targets release 25; newer JDKs may fail the MockBukkit-based tests)
- Maven

### Running Tests

```sh
mvn test
```

The suite has 39 tests running against [MockBukkit](https://github.com/MockBukkit/MockBukkit),
which boots a mock server, loads the plugin, and drives real `PlayerInteractEvent`s.

### Code Structure

```
src/main/java/org/mystikos/minecraft/orefinder/
├── Orefinder.java                  # JavaPlugin entry point; /orefinder command executor
├── OrefinderContext.java           # Config + logger interface, keeps logic testable
├── PlayerInteractionListener.java  # Event handling, messaging, block stealing
├── OreLocator.java                 # Expanding cubic-shell nearest-block search
├── ItemConf.java                   # Loads and pairs the inhand/lookfor lists
└── PlayerCooldownManager.java      # Per-player one-lookup-per-second cooldown

src/main/resources/                 # plugin.yml and config.yml (Maven-filtered)
src/test/java/org/mystikos/minecraft/orefinder/
                                    # Unit and MockBukkit integration tests
```

### Dependencies

Only `spigot-api` is `provided` — nothing is shaded into the shipped jar. `paper-api` and
`mockbukkit-v26.2` are test-scoped and must stay on the same Minecraft line as each other,
since the MockBukkit module bundles registry data for exactly its version. A few
transitive dependencies are pinned in `pom.xml` for CVE remediation; the comments there
explain why each pin exists and when it can be removed.

Dependabot watches Maven and GitHub Actions dependencies weekly, with the
Minecraft-version-locked artifacts grouped separately so they don't block self-contained
updates (see `.github/dependabot.yml`).

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.

CI runs `mvn -B package` on JDK 25 for every push and pull request to `main`, and CodeQL
scans the codebase for security issues.

## License

This project is licensed under the GNU General Public License v3.0. See the `LICENSE.md`
file for details.
