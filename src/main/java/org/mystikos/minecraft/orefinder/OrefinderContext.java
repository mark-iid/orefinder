package org.mystikos.minecraft.orefinder;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Provides context and access to essential plugin resources for the Orefinder plugin.
 * <p>
 * This interface defines the contract for accessing the plugin's configuration and
 * logging capabilities. Implementations of this interface should provide thread-safe
 * access to these resources.
 * </p>
 */
interface OrefinderContext {
    /**
     * Retrieves the plugin's configuration.
     * <p>
     * This method provides access to the Bukkit configuration file which contains
     * plugin settings and customizable parameters.
     * </p>
     *
     * @return the {@link FileConfiguration} object containing the plugin's configuration
     */
    FileConfiguration getConfig();

    /**
     * Retrieves the plugin's logger.
     * <p>
     * This method provides access to the logging facility for recording messages,
     * warnings, and errors related to plugin operations.
     * </p>
     *
     * @return the {@link Logger} instance for this plugin
     */
    Logger getLogger();
}
