package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record ConfigModifier(ResourceLocation soundId) implements AtmosphericSoundModifier, AtmosphericSoundModifier.Factory {
    @Override
    public float getModifier(EnvironmentContext context) {
        return AtmosferaConfig.volumeModifier(soundId);
    }

    @Override
    public AtmosphericSoundModifier create(Level world) {
        return this;
    }
}
