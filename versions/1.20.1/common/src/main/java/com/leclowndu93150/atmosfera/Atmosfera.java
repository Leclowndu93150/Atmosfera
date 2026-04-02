package com.leclowndu93150.atmosfera;

import com.leclowndu93150.atmosfera.client.sound.AtmosphericSoundDefinition;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Atmosfera {
    public static final String MODID = "atmosfera";
    public static final String MOD_NAME = "Atmosfera";
    private static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final Map<ResourceLocation, AtmosphericSoundDefinition> SOUND_DEFINITIONS = new HashMap<>();
    public static final Map<ResourceLocation, AtmosphericSoundDefinition> MUSIC_DEFINITIONS = new HashMap<>();

    private static Path configDir;

    public static void setConfigDir(Path dir) {
        configDir = dir;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static void debug(String message, Object... args) {
        if (AtmosferaConfig.printDebugMessages()) {
            LOG.info("[" + MOD_NAME + "] " + message, args);
        }
    }

    public static void log(String message, Object... args) {
        LOG.info("[" + MOD_NAME + "] " + message, args);
    }

    public static void warn(String message, Object... args) {
        LOG.warn("[" + MOD_NAME + "] " + message, args);
    }

    public static void error(String message, Object... args) {
        LOG.error("[" + MOD_NAME + "] " + message, args);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
