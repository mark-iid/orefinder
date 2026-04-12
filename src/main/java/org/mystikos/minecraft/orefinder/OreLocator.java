package org.mystikos.minecraft.orefinder;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Searches the world for the nearest block of a given type using an expanding cubic shell.
 */
class OreLocator {

    /**
     * Returns the Chebyshev distance to the nearest block matching {@code blockId},
     * or -1 if none is found within the 20-block search radius.
     *
     * @param loc     The origin of the search.
     * @param blockId The block type name to search for (case-insensitive).
     */
    int getBlockTypeDistance(Location loc, String blockId) {
        World world = loc.getWorld();
        if (world == null) return -1;

        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        for (int d = 0; d < 20; d++) {
            // Top and bottom faces of the cube shell at distance d
            for (int dx = -d; dx <= d; dx++) {
                for (int dz = -d; dz <= d; dz++) {
                    if (isMatchingBlock(world, x + dx, y + d, z + dz, blockId) ||
                        isMatchingBlock(world, x + dx, y - d, z + dz, blockId)) {
                        return d;
                    }
                }
            }
            // Front and back faces (excluding top/bottom edges already checked)
            for (int dx = -d; dx <= d; dx++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if (isMatchingBlock(world, x + dx, y + dy, z + d, blockId) ||
                        isMatchingBlock(world, x + dx, y + dy, z - d, blockId)) {
                        return d;
                    }
                }
            }
            // Left and right faces (excluding top/bottom/front/back edges already checked)
            for (int dz = -d + 1; dz <= d - 1; dz++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if (isMatchingBlock(world, x + d, y + dy, z + dz, blockId) ||
                        isMatchingBlock(world, x - d, y + dy, z + dz, blockId)) {
                        return d;
                    }
                }
            }
        }
        return -1;
    }

    private boolean isMatchingBlock(World world, double x, double y, double z, String blockId) {
        int iy = (int) y;
        if (iy < world.getMinHeight() || iy >= world.getMaxHeight()) return false;
        return world.getBlockAt((int) x, iy, (int) z).getType().toString().equalsIgnoreCase(blockId);
    }
}
