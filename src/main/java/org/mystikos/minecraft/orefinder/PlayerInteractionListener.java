package org.mystikos.minecraft.orefinder;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

    private final OrefinderContext plugin;
    private final ItemConf ic;
    private final OreLocator oreLocator = new OreLocator();
    private final PlayerCooldownManager cooldowns = new PlayerCooldownManager();
    private final Random random = new Random();

    public PlayerInteractionListener(OrefinderContext plugin) {
        this.plugin = plugin;
        this.ic = new ItemConf(this.plugin);
    }

    /**
     * Delegates to {@link OreLocator} to find the Chebyshev distance to the nearest matching block.
     *
     * @param loc     The location to search from.
     * @param blockId The block type to search for.
     * @return Distance to the nearest matching block, or -1 if not found.
     */
    public int getBlockTypeDistance(Location loc, String blockId) {
        return oreLocator.getBlockTypeDistance(loc, blockId);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        cooldowns.removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("orefinder.use")) {
            ItemStack holding = player.getInventory().getItemInMainHand();
            String oreId = ic.getOreId(holding.getType().toString());
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && !oreId.isEmpty() && cooldowns.canUse(player.getUniqueId())) {
                if (event.getClickedBlock() != null) {
                    int distance = oreLocator.getBlockTypeDistance(event.getClickedBlock().getLocation(), oreId);
                    sendDistanceMessage(player, distance);
                    if (plugin.getConfig().getBoolean("functions.block_stealing")) {
                        handleBlockStealing(player, holding);
                    }
                }
            }
        }
    }

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

    private void sendMessage(Player player, String message, ChatColor color) {
        if (message != null) {
            player.sendMessage(color + message);
        }
    }

    private void handleBlockStealing(Player player, ItemStack holding) {
        if (random.nextInt(plugin.getConfig().getInt("chance.steal_block")) == 0) {
            player.damage(1);
            if (player.getGameMode() == GameMode.SURVIVAL) {
                int amount = holding.getAmount();
                if (amount > 1) {
                    holding.setAmount(amount - 1);
                    player.getInventory().setItemInMainHand(holding);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
            }
            player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("text.ender_steal")));
        }
    }

    /**
     * Initializes item configuration from the plugin config. Must be called during plugin enable.
     */
    public void init() {
        ic.init();
    }
}
