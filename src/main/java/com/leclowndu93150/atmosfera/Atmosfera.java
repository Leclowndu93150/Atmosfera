package com.leclowndu93150.atmosfera;

import com.leclowndu93150.atmosfera.client.sound.AtmosphericSoundDefinition;
import com.leclowndu93150.atmosfera.client.sound.SoundDefinitionsReloader;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Mod(Atmosfera.MODID)
public class Atmosfera {
    public static final String MODID = "atmosfera";
    public static final String MOD_NAME = "Atmosfera";
    private static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final Map<ResourceLocation, AtmosphericSoundDefinition> SOUND_DEFINITIONS = new HashMap<>();
    public static final Map<ResourceLocation, AtmosphericSoundDefinition> MUSIC_DEFINITIONS = new HashMap<>();

    public Atmosfera() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            modEventBus.addListener(this::registerReloadListeners);
            modEventBus.addListener(this::addPackFinders);

            EnvironmentContext.init();
            log("Finished initialization.");
        }
    }

    private void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SoundDefinitionsReloader());
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path resourcePath = ModList.get().getModFileById(MODID).getFile().findResource("resourcepacks/dungeons");
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        new ResourceLocation(MODID, "dungeons").toString(),
                        Component.literal("Sounds from Dungeons"),
                        true,
                        id -> new PathPackResources(id, resourcePath, true),
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.TOP,
                        PackSource.BUILT_IN
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
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
