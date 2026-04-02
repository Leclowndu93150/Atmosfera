package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Objects;

public record DimensionModifier(Identifier id) implements AtmosphericSoundModifier, AtmosphericSoundModifier.Factory {
    @Override
    public float getModifier(EnvironmentContext context) {
        return Objects.requireNonNull(Minecraft.getInstance().level).dimension().identifier().equals(id) ? 1 : 0;
    }

    @Override
    public AtmosphericSoundModifier create(Level world) {
        return this;
    }

    public static Factory create(JsonObject object) {
        return new DimensionModifier(Identifier.parse(object.get("id").getAsString()));
    }
}
