# Orefinder Plugin

Orefinder is a Minecraft plugin that helps players locate ores by providing distance-based messages when they interact with blocks while holding specific items.

## Features

- Detects the distance to the nearest specified ore block.
- Sends distance-based messages to the player.
- Includes a block stealing functionality with a configurable chance.

## Requirements

- Minecraft server with Bukkit/Spigot/Paper
- Java 8 or higher
- Maven

## Installation

1. Clone the repository:
    ```sh
    git clone <repository-url>
    cd orefinder
    ```

2. Build the project using Maven:
    ```sh
    mvn clean package
    ```

3. Copy the generated `orefinder.jar` file from the `target` directory to your server's `plugins` directory.

4. Start or restart your Minecraft server.

## Configuration

The plugin provides a configuration file `config.yml` where you can customize various settings such as messages and block stealing chance.

## Usage

1. Ensure you have the necessary permissions (`orefinder.use`) to use the plugin.
2. Hold a specified item (e.g., diamond) in your main hand.
3. Interact with blocks to receive distance-based messages about nearby ores.

## Commands

No commands are provided by this plugin.

## Permissions

- `orefinder.use`: Allows the player to use the Orefinder functionality.

## Development

### Prerequisites

- Java 8 or higher
- Maven

### Running Tests

1. Ensure you have MockBukkit for testing.
2. Run the tests using Maven:
    ```sh
    mvn test
    ```

### Code Structure

- `src/main/java/org/mystikos/minecraft/orefinder/`: Main plugin code.
- `src/test/java/org/mystikos/minecraft/orefinder/`: Unit tests.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.

## License

This project is licensed under the GNU General Public License. See the `LICENSE.md` file for details.