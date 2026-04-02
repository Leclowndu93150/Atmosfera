package com.leclowndu93150.atmosfera.client.sound;

import com.google.common.collect.ImmutableCollection;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.resources.ResourceLocation;

public record AtmosphericSoundDefinition(ResourceLocation id, ResourceLocation soundId,
                                         EnvironmentContext.Shape shape, EnvironmentContext.Size size,
                                         int defaultVolume, boolean hasSubtitleByDefault,
                                         ImmutableCollection<AtmosphericSoundModifier.Factory> modifierFactories) {
}
