package org.mystikos.minecraft.orefinder;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.*;

public class OrefinderTest {

    private ServerMock server;
    private WorldMock world;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up server");
        server = MockBukkit.mock();
        server.setMaxPlayers(1);
        System.out.println("Loading plugin");
        Orefinder orefinder = MockBukkit.load(Orefinder.class);
        PlayerInteractionListener listener = new PlayerInteractionListener(orefinder);
        listener.init();
        System.out.println("Creating world");
        world = new WorldMock(Material.STONE, 100, 50);
        System.out.println("Adding player");
        player = server.addPlayer();
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
        server.getPluginManager().callEvent(event);

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
        server.getPluginManager().callEvent(event);
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
        server.getPluginManager().callEvent(event);
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
        server.getPluginManager().callEvent(event);
        String message = player.nextMessage();
        player.kick();
        // Verify no message is sent
        assertNull(message, "Message should be null");
    }


}