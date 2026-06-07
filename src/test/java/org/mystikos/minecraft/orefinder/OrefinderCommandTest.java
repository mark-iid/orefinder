package org.mystikos.minecraft.orefinder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@code /orefinder} command, exercised through a fully loaded
 * plugin instance so command registration from {@code plugin.yml} is covered too.
 */
public class OrefinderCommandTest {

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        MockBukkit.load(Orefinder.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void opCanReloadConfiguration() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        player.performCommand("orefinder reload");
        String message = player.nextMessage();
        assertNotNull(message, "Reload should produce a confirmation message");
        assertTrue(message.contains("reloaded"), "Op should see a reload confirmation but got: " + message);
    }

    @Test
    public void reloadIsDeniedWithoutPermission() {
        PlayerMock player = server.addPlayer();
        player.setOp(false);
        player.performCommand("orefinder reload");
        String message = player.nextMessage();
        assertNotNull(message, "A denied reload should still produce a message");
        assertTrue(message.contains("permission"),
                "Player without orefinder.reload should be denied but got: " + message);
    }

    @Test
    public void unknownSubcommandShowsUsage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        player.performCommand("orefinder bogus");
        String message = player.nextMessage();
        assertNotNull(message, "An unknown subcommand should produce a usage message");
        assertTrue(message.contains("Usage"), "Expected a usage hint but got: " + message);
    }
}
