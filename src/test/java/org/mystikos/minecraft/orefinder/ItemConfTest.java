package org.mystikos.minecraft.orefinder;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class ItemConfTest {

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private OrefinderContext createContext(List<String> inhand, List<String> lookfor) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("indicate.inhand", inhand);
        config.set("indicate.lookfor", lookfor);
        Logger logger = Logger.getLogger("ItemConfTest");
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

    @Test
    public void testGetOreIdReturnsCorrectOre() {
        OrefinderContext ctx = createContext(
                List.of("diamond", "emerald", "ancient_debris"),
                List.of("diamond_ore", "emerald_ore", "ancient_debris")
        );
        ItemConf ic = new ItemConf(ctx);
        ic.init();

        assertEquals("diamond_ore", ic.getOreId("diamond"));
        assertEquals("emerald_ore", ic.getOreId("emerald"));
        assertEquals("ancient_debris", ic.getOreId("ancient_debris"));
    }

    @Test
    public void testGetOreIdReturnsEmptyForUnknownItem() {
        OrefinderContext ctx = createContext(
                List.of("diamond"),
                List.of("diamond_ore")
        );
        ItemConf ic = new ItemConf(ctx);
        ic.init();

        assertEquals("", ic.getOreId("iron_ingot"));
        assertEquals("", ic.getOreId(""));
    }

    @Test
    public void testGetOreIdIsCaseInsensitive() {
        OrefinderContext ctx = createContext(
                List.of("diamond"),
                List.of("diamond_ore")
        );
        ItemConf ic = new ItemConf(ctx);
        ic.init();

        assertEquals("diamond_ore", ic.getOreId("DIAMOND"));
        assertEquals("diamond_ore", ic.getOreId("Diamond"));
        assertEquals("diamond_ore", ic.getOreId("dIaMoNd"));
    }

    @Test
    public void testInitThrowsWhenInhandListEmpty() {
        OrefinderContext ctx = createContext(List.of(), List.of("diamond_ore"));
        ItemConf ic = new ItemConf(ctx);
        assertThrows(IllegalArgumentException.class, ic::init);
    }

    @Test
    public void testInitThrowsWhenLookforListEmpty() {
        OrefinderContext ctx = createContext(List.of("diamond"), List.of());
        ItemConf ic = new ItemConf(ctx);
        assertThrows(IllegalArgumentException.class, ic::init);
    }

    @Test
    public void testInitUsesMinSizeOfBothLists() {
        // lookfor has fewer entries — only first 2 pairs should be loaded
        OrefinderContext ctx = createContext(
                List.of("diamond", "emerald", "ancient_debris"),
                List.of("diamond_ore", "emerald_ore")
        );
        ItemConf ic = new ItemConf(ctx);
        ic.init();

        assertEquals("diamond_ore", ic.getOreId("diamond"));
        assertEquals("emerald_ore", ic.getOreId("emerald"));
        assertEquals("", ic.getOreId("ancient_debris"),
                "Third entry is beyond the shorter lookfor list and should not be mapped");
    }
}
