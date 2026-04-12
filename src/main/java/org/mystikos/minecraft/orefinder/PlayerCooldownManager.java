package org.mystikos.minecraft.orefinder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player cooldowns for the Orefinder detector.
 * A player may use the detector at most once per second.
 */
class PlayerCooldownManager {

    private final Map<UUID, Long> lastUsed = new HashMap<>();

    /**
     * Returns true and records the current timestamp if the player's cooldown has elapsed,
     * false if the player used the detector within the current second.
     *
     * @param playerId The player's unique ID.
     */
    boolean canUse(UUID playerId) {
        long now = System.currentTimeMillis() / 1000L;
        Long last = lastUsed.get(playerId);
        if (last == null || now > last) {
            lastUsed.put(playerId, now);
            return true;
        }
        return false;
    }

    /**
     * Removes the cooldown record for a player (call on disconnect to free memory).
     *
     * @param playerId The player's unique ID.
     */
    void removePlayer(UUID playerId) {
        lastUsed.remove(playerId);
    }
}
