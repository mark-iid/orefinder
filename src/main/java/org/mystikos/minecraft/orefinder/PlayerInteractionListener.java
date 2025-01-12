package org.mystikos.minecraft.orefinder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

/**
 * Listener class for handling player interactions and events related to the Orefinder plugin.
 */
class PlayerInteractionListener implements Listener {

    private final Orefinder plugin;
    private final ItemConf ic;
    private PlayerCD pcdm;

    /**
     * Constructor for PlayerInteractionListener.
     *
     * @param plugin The Orefinder plugin instance.
     */
    public PlayerInteractionListener(Orefinder plugin) {
        this.plugin = plugin;
        this.ic = new ItemConf(this.plugin);
    }

    /**
     * Calculates the distance to the nearest block of a specified type.
     *
     * @param loc  The location to start the search from.
     * @param stid The block type to search for.
     * @return The distance to the nearest block of the specified type, or -1 if not found.
     */
    public int getBlockTypeDistance(Location loc, String stid) {
        World world = loc.getWorld();
        if (world == null) return -1;

        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        for (int d = 0; d < 20; d++) {
            for (int dx = -d; dx <= d; dx++) {
                for (int dz = -d; dz <= d; dz++) {
                    if (isMatchingBlock(world, x + dx, y + d, z + dz, stid) || isMatchingBlock(world, x + dx, y - d, z + dz, stid)) {
                        return d;
                    }
                }
            }
            for (int dx = -d; dx <= d; dx++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if (isMatchingBlock(world, x + dx, y + dy, z + d, stid) || isMatchingBlock(world, x + dx, y + dy, z - d, stid)) {
                        return d;
                    }
                }
            }
            for (int dz = -d + 1; dz <= d - 1; dz++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if (isMatchingBlock(world, x + d, y + dy, z + dz, stid) || isMatchingBlock(world, x - d, y + dy, z + dz, stid)) {
                        return d;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Checks if a block at the given coordinates matches the specified type.
     *
     * @param world The world where the block is located.
     * @param x     The x-coordinate of the block.
     * @param y     The y-coordinate of the block.
     * @param z     The z-coordinate of the block.
     * @param stid  The block type to check for.
     * @return True if the block matches the specified type, false otherwise.
     */
    private boolean isMatchingBlock(World world, double x, double y, double z, String stid) {
        return world.getBlockAt((int) x, (int) y, (int) z).getType().toString().equalsIgnoreCase(stid);
    }

    /**
     * Event handler for player quit events.
     *
     * @param event The PlayerQuitEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        pcdm.delRecord(event.getPlayer().getEntityId());
    }

    /**
     * Event handler for player interact events.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("orefinder.use")) {
            ItemStack holding = player.getInventory().getItemInMainHand();
            String oreId = ic.getOreId(holding.getType().toString());
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && !oreId.isEmpty() && pcdm.getUseRight(player.getEntityId())) {
                if (event.getClickedBlock() != null) {
                    int distance = getBlockTypeDistance(event.getClickedBlock().getLocation(), oreId);
                    sendDistanceMessage(player, distance);
                    if (plugin.getConfig().getBoolean("functions.block_stealing")) {
                        handleBlockStealing(player, holding);
                    }
                }
            }
        }
    }

    /**
     * Sends a distance message to the player based on the distance to the nearest block of the specified type.
     *
     * @param player   The player to send the message to.
     * @param distance The distance to the nearest block of the specified type.
     */
    private void sendDistanceMessage(Player player, int distance) {
        String message;
        if (distance == -1) {
            message = plugin.getConfig().getString("text.very_cold");
            sendMessage(player, message, ChatColor.BLUE);
        } else if (distance < 2) {
            message = plugin.getConfig().getString("text.oneblock_hot");
            sendMessage(player, message, ChatColor.DARK_RED);
        } else if (distance < 4) {
            message = plugin.getConfig().getString("text.very_hot");
            sendMessage(player, message, ChatColor.RED);
        } else if (distance < 6) {
            message = plugin.getConfig().getString("text.hot");
            sendMessage(player, message, ChatColor.RED);
        } else if (distance < 8) {
            message = plugin.getConfig().getString("text.warm");
            sendMessage(player, message, ChatColor.GOLD);
        } else if (distance < 15) {
            message = plugin.getConfig().getString("text.lukewarm");
            sendMessage(player, message, ChatColor.YELLOW);
        } else if (distance < 20) {
            message = plugin.getConfig().getString("text.cold");
            sendMessage(player, message, ChatColor.AQUA);
        }
    }

    /**
     * Sends a message to the player with the specified color.
     *
     * @param player  The player to send the message to.
     * @param message The message to send.
     * @param color   The color of the message.
     */
    private void sendMessage(Player player, String message, ChatColor color) {
        if (message != null) {
            player.sendMessage(color + message);
        }
    }

    /**
     * Handles the block stealing functionality.
     *
     * @param player  The player who is interacting with the block.
     * @param holding The item the player is holding.
     */
    private void handleBlockStealing(Player player, ItemStack holding) {
        Random random = new Random();
        if (random.nextInt(plugin.getConfig().getInt("chance.steal_block")) == 0) {
            player.damage(1);
            if (player.getGameMode() == GameMode.SURVIVAL) {
                int amount = holding.getAmount();
                if (amount > 1) {
                    holding.setAmount(amount - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
            }
            player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("text.ender_steal")));
        }
    }

    /**
     * Initializes the PlayerCD and ItemConf instances.
     */
    public void init() {
        pcdm = new PlayerCD(Bukkit.getMaxPlayers());
        ic.init();
    }

    /**
     * Class representing a player cooldown record.
     */
    static class PCDR {
        private final int pid;
        private int time;

        /**
         * Constructor for PCDR.
         *
         * @param pid The player ID.
         */
        PCDR(int pid) {
            this.pid = pid;
            this.time = 0;
        }

        /**
         * Gets the player ID.
         *
         * @return The player ID.
         */
        int getPid() {
            return pid;
        }

        /**
         * Gets the cooldown time.
         *
         * @return The cooldown time.
         */
        int getTime() {
            return time;
        }

        /**
         * Sets the cooldown time.
         *
         * @param time The cooldown time.
         */
        void setTime(int time) {
            this.time = time;
        }
    }

    /**
     * Class representing a collection of player cooldown records.
     */
    static class PlayerCD {
        private final PCDR[] pcdrs;
        private final int cap;
        private int count;

        /**
         * Constructor for PlayerCD.
         *
         * @param slots The maximum number of player cooldown records.
         */
        PlayerCD(int slots) {
            this.cap = slots;
            this.pcdrs = new PCDR[slots];
            this.count = 0;
        }

        /**
         * Gets the index of a player cooldown record by player ID.
         *
         * @param pid The player ID.
         * @return The index of the player cooldown record, or -1 if not found.
         */
        private int getRecordIndex(int pid) {
            for (int i = 0; i < count; i++) {
                if (pcdrs[i].getPid() == pid) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Deletes a player cooldown record by player ID.
         *
         * @param pid The player ID.
         */
        private void delRecord(int pid) {
            int index = getRecordIndex(pid);
            if (index != -1) {
                System.arraycopy(pcdrs, index + 1, pcdrs, index, count - index - 1);
                count--;
            }
        }

        /**
         * Checks if a player has the right to use the Orefinder functionality.
         *
         * @param pid The player ID.
         * @return True if the player has the right to use the functionality, false otherwise.
         */
        boolean getUseRight(int pid) {
            int index = getRecordIndex(pid);
            if (index == -1 && count < cap) {
                index = count;
                pcdrs[index] = new PCDR(pid);
                count++;
            }
            if (index != -1) {
                int currentTime = (int) (System.currentTimeMillis() / 1000L);
                if (currentTime > pcdrs[index].getTime()) {
                    pcdrs[index].setTime(currentTime);
                    return true;
                }
            }
            return false;
        }
    }
}