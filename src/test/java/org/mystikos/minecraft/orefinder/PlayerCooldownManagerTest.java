package org.mystikos.minecraft.orefinder;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PlayerCooldownManager}. The manager is pure logic
 * (no Bukkit dependencies), so these tests run without a mock server.
 */
public class PlayerCooldownManagerTest {

    @Test
    public void firstUseIsAllowed() {
        PlayerCooldownManager cooldowns = new PlayerCooldownManager();
        assertTrue(cooldowns.canUse(UUID.randomUUID()), "The first use for a player should be allowed");
    }

    @Test
    public void immediateSecondUseIsBlocked() {
        PlayerCooldownManager cooldowns = new PlayerCooldownManager();
        UUID playerId = UUID.randomUUID();
        cooldowns.canUse(playerId);
        assertFalse(cooldowns.canUse(playerId), "A second use within the same second should be blocked");
    }

    @Test
    public void cooldownsAreTrackedPerPlayer() {
        PlayerCooldownManager cooldowns = new PlayerCooldownManager();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        cooldowns.canUse(first);
        assertTrue(cooldowns.canUse(second), "A different player's cooldown must be independent");
        assertFalse(cooldowns.canUse(first), "The first player should still be on cooldown");
    }

    @Test
    public void removingPlayerResetsTheirCooldown() {
        PlayerCooldownManager cooldowns = new PlayerCooldownManager();
        UUID playerId = UUID.randomUUID();
        cooldowns.canUse(playerId);
        cooldowns.removePlayer(playerId);
        assertTrue(cooldowns.canUse(playerId), "After removal the player should be able to use the detector again");
    }

    @Test
    public void removingUnknownPlayerIsHarmless() {
        PlayerCooldownManager cooldowns = new PlayerCooldownManager();
        // Removing a player that was never recorded must not throw.
        cooldowns.removePlayer(UUID.randomUUID());
    }
}
