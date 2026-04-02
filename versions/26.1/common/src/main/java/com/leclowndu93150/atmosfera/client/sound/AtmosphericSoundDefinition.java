package com.leclowndu93150.atmosfera.client.sound;

import com.google.common.collect.ImmutableCollection;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.resources.Identifier;

public record AtmosphericSoundDefinition(Identifier id, Identifier soundId,
                                         EnvironmentContext.Shape shape, EnvironmentContext.Size size,
                                         int defaultVolume, boolean hasSubtitleByDefault,
                                         ImmutableCollection<AtmosphericSoundModifier.Factory> modifierFactories) {
}
