package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public record RidingCondition(ImmutableList<EntityType<?>> entityTypes) implements AtmosphericSoundModifier, AtmosphericSoundModifier.Factory {
    @Override
    public float getModifier(EnvironmentContext context) {
        Entity vehicle = context.getVehicle();

        if (vehicle != null) {
            for (var entityType : entityTypes) {
                if (vehicle.getType().equals(entityType)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public AtmosphericSoundModifier create(Level world) {
        return this;
    }

    public static Factory create(JsonObject object) {
        var entityTypes = ImmutableList.<EntityType<?>>builder();

        JsonElement value = object.get("value");

        if (value.isJsonPrimitive()) {
            ResourceLocation id = new ResourceLocation(value.getAsString());
            BuiltInRegistries.ENTITY_TYPE.getOptional(id).ifPresent(entityTypes::add);
        } else if (value.isJsonArray()) {
            for (JsonElement e : value.getAsJsonArray()) {
                ResourceLocation id = new ResourceLocation(e.getAsString());
                BuiltInRegistries.ENTITY_TYPE.getOptional(id).ifPresent(entityTypes::add);
            }
        }

        return new RidingCondition(entityTypes.build());
    }
}
