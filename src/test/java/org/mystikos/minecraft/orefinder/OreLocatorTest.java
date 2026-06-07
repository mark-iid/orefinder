package org.mystikos.minecraft.orefinder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link OreLocator}, which performs the expanding cubic-shell
 * search and returns the Chebyshev distance to the nearest matching block.
 */
public class OreLocatorTest {

    private WorldMock world;
    private final OreLocator locator = new OreLocator();

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        world = new WorldMock(Material.STONE, 100, 50);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private Location origin() {
        return world.getSpawnLocation();
    }

    private void setBlock(int dx, int dy, int dz, Material material) {
        Location loc = origin();
        world.getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz).setType(material);
    }

    @Test
    public void returnsNegativeOneWhenWorldIsNull() {
        Location loc = new Location(null, 0, 0, 0);
        assertEquals(-1, locator.getBlockTypeDistance(loc, "diamond_ore"),
                "A null world should short-circuit to -1");
    }

    @Test
    public void returnsZeroWhenOriginBlockMatches() {
        setBlock(0, 0, 0, Material.DIAMOND_ORE);
        assertEquals(0, locator.getBlockTypeDistance(origin(), "diamond_ore"),
                "A match at the origin should be distance 0");
    }

    @Test
    public void findsOreOneBlockAbove() {
        setBlock(0, 1, 0, Material.DIAMOND_ORE);
        assertEquals(1, locator.getBlockTypeDistance(origin(), "diamond_ore"),
                "Ore directly above should be distance 1");
    }

    @Test
    public void usesChebyshevDistanceAlongAnAxis() {
        setBlock(5, 0, 0, Material.DIAMOND_ORE);
        assertEquals(5, locator.getBlockTypeDistance(origin(), "diamond_ore"),
                "Ore 5 blocks east should be distance 5");
    }

    @Test
    public void usesChebyshevDistanceForDiagonals() {
        // A block offset by (3,0,3) is Chebyshev distance 3, not 6.
        setBlock(3, 0, 3, Material.DIAMOND_ORE);
        assertEquals(3, locator.getBlockTypeDistance(origin(), "diamond_ore"),
                "Diagonal offset (3,0,3) should be Chebyshev distance 3");
    }

    @Test
    public void matchingIsCaseInsensitive() {
        setBlock(2, 0, 0, Material.DIAMOND_ORE);
        assertEquals(2, locator.getBlockTypeDistance(origin(), "DiAmOnD_OrE"),
                "Block matching should ignore case");
    }

    @Test
    public void returnsNegativeOneWhenNoMatchWithinRadius() {
        // World is solid stone; no diamond ore exists anywhere.
        assertEquals(-1, locator.getBlockTypeDistance(origin(), "diamond_ore"),
                "No match within the 20-block radius should return -1");
    }
}
