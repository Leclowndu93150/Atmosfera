package com.leclowndu93150.atmosfera.client;

import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Atmosfera.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null) {
            var data = ClientLevelData.getOrCreate(client.level);
            data.getSoundHandler().tick();
        }
    }
}
