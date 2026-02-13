package org.mystikos.minecraft.orefinder;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

interface OrefinderContext {
    FileConfiguration getConfig();

    Logger getLogger();
}
