package com.leclowndu93150.atmosfera.neoforge;

import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.SoundDefinitionsReloader;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(Atmosfera.MODID)
public class AtmosferaNeoForge {
    public AtmosferaNeoForge(IEventBus modBus, ModContainer container) {
        Atmosfera.setConfigDir(FMLPaths.CONFIGDIR.get());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            AtmosferaConfig.init();

            modBus.addListener(this::registerReloadListeners);
            modBus.addListener(this::addPackFinders);

            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, parent) -> com.leclowndu93150.atmosfera.client.AtmosferaConfigScreen.create(parent));

            NeoForge.EVENT_BUS.addListener(this::onClientTick);

            EnvironmentContext.init();
            Atmosfera.log("Finished initialization.");
        }
    }

    private void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SoundDefinitionsReloader());
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addPackFinders(
                    ResourceLocation.fromNamespaceAndPath(Atmosfera.MODID, "resourcepacks/dungeons"),
                    PackType.CLIENT_RESOURCES,
                    Component.literal("Sounds from Dungeons"),
                    PackSource.BUILT_IN,
                    true,
                    Pack.Position.TOP
            );
        }
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null) {
            var data = ClientLevelData.getOrCreate(client.level);
            data.getSoundHandler().tick();
        }
    }
}
