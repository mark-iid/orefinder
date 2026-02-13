package org.mystikos.minecraft.orefinder;

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

        // Initialize player interaction handler
        this.pInteract.init();

        // Log plugin enabled message
        getLogger().info("OreFinder enabled.");
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