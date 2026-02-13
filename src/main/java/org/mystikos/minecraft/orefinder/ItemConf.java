package org.mystikos.minecraft.orefinder;

import java.util.List;


/**
 * The ItemConf class handles the configuration for items in the OreFinder plugin.
 * It initializes the configuration and provides methods to retrieve ore IDs based on items in hand.
 */
class ItemConf {

    /**
     * Reference to the main plugin class.
     */
    private final OrefinderContext plugin;

    /**
     * Array of item IDs that the player can hold in hand.
     */
    private String[] inHand;

    /**
     * Array of ore IDs that the plugin will look for.
     */
    private String[] lookFor;

    /**
     * The size of the configuration lists.
     */
    private int size;

    /**
     * Constructor for the ItemConf class.
     *
     * @param plugin The main plugin instance.
     */
    ItemConf(OrefinderContext plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes the configuration by loading the item and ore lists from the plugin configuration.
     * Throws an IllegalArgumentException if the configuration lists are empty.
     */
    void init() {
        List<String> inHandList = plugin.getConfig().getStringList("indicate.inhand");
        List<String> lookForList = plugin.getConfig().getStringList("indicate.lookfor");
        if (inHandList.isEmpty()) {
            throw new IllegalArgumentException("Configuration list 'indicate.inhand' cannot be null or empty");
        }
        if (lookForList.isEmpty()) {
            throw new IllegalArgumentException("Configuration list 'indicate.lookfor' cannot be null or empty");
        }

        // Use the smaller size of the two lists to avoid IndexOutOfBoundsException
        size = Math.min(inHandList.size(), lookForList.size());
        inHand = new String[size];
        lookFor = new String[size];
        for (int i = 0; i < size; i++) {
            inHand[i] = inHandList.get(i);
            lookFor[i] = lookForList.get(i);
        }
    }

    /**
     * Retrieves the ore ID corresponding to the given item ID.
     *
     * @param tid The item ID to look up.
     * @return The corresponding ore ID, or an empty string if not found.
     */
    String getOreId(String tid) {
        for (int i = 0; i < size; i++) {
            if (inHand[i].equalsIgnoreCase(tid)) {
                return lookFor[i];
            }
        }
        return "";
    }
}