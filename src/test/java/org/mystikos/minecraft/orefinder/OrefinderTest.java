package org.mystikos.minecraft.orefinder;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class OrefinderTest {

    private ServerMock server;
    private WorldMock world;
    private PlayerMock player;
    private PlayerInteractionListener listener;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up server");
        server = MockBukkit.mock();
        server.setMaxPlayers(1);
        System.out.println("Loading plugin");
        OrefinderContext context = createTestContext();
        listener = new PlayerInteractionListener(context);
        listener.init();
        System.out.println("Creating world");
        world = new WorldMock(Material.STONE, 100, 50);
        System.out.println("Adding player");
        player = server.addPlayer();
        player.setOp(true);
    }

    private OrefinderContext createTestContext() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("text.oneblock_hot", "One block away!");
        config.set("text.very_hot", "Very hot!");
        config.set("text.hot", "Hot!");
        config.set("text.warm", "Warm!");
        config.set("text.lukewarm", "Lukewarm.");
        config.set("text.cold", "Cold.");
        config.set("text.very_cold", "Ice cold!");
        config.set("text.ender_steal", "An enderman stole your block!");
        config.set("chance.steal_block", 48);
        config.set("functions.block_stealing", false);
        config.set("indicate.inhand", List.of("diamond", "emerald", "ancient_debris"));
        config.set("indicate.lookfor", List.of("diamond_ore", "emerald_ore", "ancient_debris"));
        Logger logger = Logger.getLogger("OrefinderTest");
        return new OrefinderContext() {
            @Override
            public YamlConfiguration getConfig() {
                return config;
            }

            @Override
            public Logger getLogger() {
                return logger;
            }
        };
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testPlayerHitsStoneBlockWithNoDiamond() {
        System.out.println("testPlayerHitsStoneBlockWithNoDiamond");
        System.out.println("Player = " + player);
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        player.teleport(world.getSpawnLocation());
        // Player holds a diamond
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);

        // Simulate hitting a stone block
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond, world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);

        String expectedMessage = "Ice cold!";
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains(expectedMessage), "Expected message to contain: " + expectedMessage + " but contained: " + message);

    }

    @Test
    public void testPlayerHitsStoneBlockWithDiamond3BlocksAway() {
        System.out.println("testPlayerHitsStoneBlockWithDiamond3BlocksAway");
        System.out.println("Player = " + player);
        // Set the diamond ore block 3 blocks away
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(3, 0, 0)).setType(Material.DIAMOND_ORE);

        // Player holds a diamond
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);

        // Simulate hitting a stone block
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond, world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        // Verify the expected message
        String expectedMessage = "Very hot!";
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains(expectedMessage), "Expected message to contain: " + expectedMessage + " but contained: " + message);
    }

    @Test
    public void testPlayerHitsStoneBlockWithDiamondOre5BlocksAway() {
        System.out.println("testPlayerHitsStoneBlockWithDiamondOre5BlocksAway");
        System.out.println("Player = " + player);
        // Set the diamond ore block 5 blocks away
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(5, 0, 0)).setType(Material.DIAMOND_ORE);

        // Player holds a diamond
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);

        // Simulate hitting a stone block
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond, world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        // Verify the expected message
        String expectedMessage = "Hot!";
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains(expectedMessage), "Expected message to contain: " + expectedMessage + " but contained: " + message);
    }

    @Test
    public void testPlayerHitsBlockWithUnlistedItem() {
        System.out.println("testPlayerHitsBlockWithUnlistedItem");
        System.out.println("Player = " + player);
        // Set the diamond ore block 3 blocks away
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(3, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
        player.getInventory().setItemInMainHand(ironIngot);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, ironIngot, world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        String message = player.nextMessage();
        player.kick();
        // Verify no message is sent
        assertNull(message, "Message should be null");
    }

    // ===== DISTANCE THRESHOLD TESTS =====

    @Test
    public void testPlayerHitsBlockWithOre1BlockAway() {
        // Ore 1 block from the clicked block → "One block away!" (distance < 2)
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(2, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("One block away!"), "Expected 'One block away!' but got: " + message);
    }

    @Test
    public void testPlayerHitsBlockWithOre7BlocksAway() {
        // Ore 7 blocks from the clicked block → "Warm!" (distance < 8)
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(8, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("Warm!"), "Expected 'Warm!' but got: " + message);
    }

    @Test
    public void testPlayerHitsBlockWithOre10BlocksAway() {
        // Ore 10 blocks from the clicked block → "Lukewarm." (distance < 15)
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(11, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("Lukewarm."), "Expected 'Lukewarm.' but got: " + message);
    }

    @Test
    public void testPlayerHitsBlockWithOre16BlocksAway() {
        // Ore 16 blocks from the clicked block → "Cold." (distance < 20)
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(17, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        String message = player.nextMessage();
        player.kick();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("Cold."), "Expected 'Cold.' but got: " + message);
    }

    // ===== PERMISSION CHECK TEST =====

    @Test
    public void testPlayerWithoutPermissionReceivesNoMessage() {
        player.setOp(false);
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        world.getBlockAt(player.getLocation().add(3, 0, 0)).setType(Material.DIAMOND_ORE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        listener.onPlayerInteract(event);
        assertNull(player.nextMessage(), "Player without orefinder.use permission should receive no message");
    }

    // ===== COOLDOWN TESTS =====

    @Test
    public void testCooldownAllowsFirstUse() {
        PlayerInteractionListener.PlayerCD pcd = new PlayerInteractionListener.PlayerCD(10);
        assertTrue(pcd.getUseRight(1), "First use should be allowed");
    }

    @Test
    public void testCooldownPreventsImmediateSecondUse() {
        PlayerInteractionListener.PlayerCD pcd = new PlayerInteractionListener.PlayerCD(10);
        pcd.getUseRight(1);
        assertFalse(pcd.getUseRight(1), "Immediate second use should be blocked by the 1-second cooldown");
    }

    @Test
    public void testCooldownBlocksWhenAtCapacity() {
        // Only 1 slot; after player 1 fills it, player 2 cannot get a record
        PlayerInteractionListener.PlayerCD pcd = new PlayerInteractionListener.PlayerCD(1);
        pcd.getUseRight(1);
        assertFalse(pcd.getUseRight(2), "New player should be blocked when cooldown array is at capacity");
    }

    @Test
    public void testPlayerQuitRemovesCooldownRecord() {
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);

        listener.onPlayerInteract(interactEvent);
        player.nextMessage(); // consume the first message

        // Simulate the player quitting — this should clear their cooldown record
        PlayerQuitEvent quitEvent = new PlayerQuitEvent(player, Component.empty());
        listener.onPlayerQuitEvent(quitEvent);

        // After rejoining (new record), the player should be able to use the detector again
        listener.onPlayerInteract(interactEvent);
        assertNotNull(player.nextMessage(), "Use after player quit should succeed (cooldown record was cleared)");
    }

    // ===== BLOCK STEALING TESTS =====

    private PlayerInteractionListener createStealingListener() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("text.oneblock_hot", "One block away!");
        config.set("text.very_hot", "Very hot!");
        config.set("text.hot", "Hot!");
        config.set("text.warm", "Warm!");
        config.set("text.lukewarm", "Lukewarm.");
        config.set("text.cold", "Cold.");
        config.set("text.very_cold", "Ice cold!");
        config.set("text.ender_steal", "An enderman stole your block!");
        config.set("chance.steal_block", 1); // nextInt(1) always returns 0 → stealing always triggers
        config.set("functions.block_stealing", true);
        config.set("indicate.inhand", List.of("diamond", "emerald", "ancient_debris"));
        config.set("indicate.lookfor", List.of("diamond_ore", "emerald_ore", "ancient_debris"));
        Logger logger = Logger.getLogger("OrefinderTest");
        OrefinderContext ctx = new OrefinderContext() {
            @Override
            public YamlConfiguration getConfig() {
                return config;
            }

            @Override
            public Logger getLogger() {
                return logger;
            }
        };
        PlayerInteractionListener stealingListener = new PlayerInteractionListener(ctx);
        stealingListener.init();
        return stealingListener;
    }

    @Test
    public void testBlockStealingDamagesPlayer() {
        PlayerInteractionListener stealingListener = createStealingListener();
        player.setGameMode(GameMode.SURVIVAL);
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        player.getInventory().setItemInMainHand(diamond);
        double healthBefore = player.getHealth();
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        stealingListener.onPlayerInteract(event);
        assertTrue(player.getHealth() < healthBefore, "Player health should decrease after block stealing");
    }

    @Test
    public void testBlockStealingRemovesItemWhenAmountIsOne() {
        PlayerInteractionListener stealingListener = createStealingListener();
        player.setGameMode(GameMode.SURVIVAL);
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        stealingListener.onPlayerInteract(event);
        ItemStack inHand = player.getInventory().getItemInMainHand();
        assertTrue(inHand == null || inHand.getType() == Material.AIR,
                "Main hand should be empty after the last item is stolen");
    }

    @Test
    public void testBlockStealingDecrementsItemWhenAmountIsGreaterThanOne() {
        PlayerInteractionListener stealingListener = createStealingListener();
        player.setGameMode(GameMode.SURVIVAL);
        world.getBlockAt(player.getLocation().add(1, 0, 0)).setType(Material.STONE);
        ItemStack diamond = new ItemStack(Material.DIAMOND, 3);
        player.getInventory().setItemInMainHand(diamond);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, diamond,
                world.getBlockAt(player.getLocation().add(1, 0, 0)), null);
        stealingListener.onPlayerInteract(event);
        assertEquals(2, player.getInventory().getItemInMainHand().getAmount(), "Diamond stack should be decremented from 3 to 2 after stealing");
    }

    // ===== getBlockTypeDistance EDGE CASE TESTS =====

    @Test
    public void testGetBlockTypeDistanceReturnsNegativeOneWhenNullWorld() {
        Location loc = new Location(null, 0, 0, 0);
        assertEquals(-1, listener.getBlockTypeDistance(loc, "diamond_ore"),
                "Should return -1 immediately when the location's world is null");
    }

    @Test
    public void testGetBlockTypeDistanceFindsOreAlongYAxis() {
        Location loc = world.getSpawnLocation();
        world.getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).setType(Material.DIAMOND_ORE);
        assertEquals(1, listener.getBlockTypeDistance(loc, "diamond_ore"),
                "Should find ore exactly 1 block above the search origin");
    }

}