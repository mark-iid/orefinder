package org.mystikos.minecraft.orefinder;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Orefinder class extends JavaPlugin and represents the main class for the OreFinder plugin.
 * It handles the enabling and disabling of the plugin, as well as initializing necessary components.
 */
public class Orefinder extends JavaPlugin implements OrefinderContext {
    /**
     * Instance of RCHPlayerInteract to handle player interactions.
     */
    private final PlayerInteractionListener pInteract = new PlayerInteractionListener(this);

    /**
     * Called when the plugin is enabled. This method initializes the plugin configuration,
     * registers event listeners, and performs any necessary setup.
     */
    @Override
    public void onEnable() {
        // Copy default configuration if it does not exist
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Register event listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.pInteract, this);

        // Register the /orefinder command
        if (getCommand("orefinder") != null) {
            getCommand("orefinder").setExecutor(this);
        }

        // Initialize player interaction handler
        this.pInteract.init();

        // Log plugin enabled message
        getLogger().info("OreFinder enabled.");
    }

    /**
     * Handles the {@code /orefinder} command. Currently supports the {@code reload}
     * subcommand, which reloads {@code config.yml} from disk and rebuilds the
     * item/ore mappings without requiring a server restart.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("orefinder")) {
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("orefinder.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to reload OreFinder.");
                return true;
            }
            try {
                reloadConfig();
                this.pInteract.init();
                sender.sendMessage(ChatColor.GREEN + "OreFinder configuration reloaded.");
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "OreFinder reload failed: " + e.getMessage());
            }
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Usage: /orefinder reload");
        return true;
    }

    /**
     * Called when the plugin is disabled. This method performs any necessary cleanup.
     */
    @Override
    public void onDisable() {
        // Log plugin disabled message
        getLogger().info("OreFinder disabled.");
    }
}