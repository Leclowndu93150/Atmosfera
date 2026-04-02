package com.leclowndu93150.atmosfera.client.sound;

import com.google.common.collect.ImmutableCollection;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

public record AtmosphericSound(ResourceLocation id, ResourceLocation soundId,
                               EnvironmentContext.Shape shape, EnvironmentContext.Size size,
                               ImmutableCollection<AtmosphericSoundModifier> modifiers) {
    public float getVolume(ClientLevel world) {
        var data = ClientLevelData.get(world);
        if (data == null) return 0;
        var context = data.getEnvironmentContext(size, shape);
        if (context == null) return 0;

        float volume = 1;
        for (var modifier : modifiers) {
            volume *= modifier.getModifier(context);
        }
        return volume;
    }
}
