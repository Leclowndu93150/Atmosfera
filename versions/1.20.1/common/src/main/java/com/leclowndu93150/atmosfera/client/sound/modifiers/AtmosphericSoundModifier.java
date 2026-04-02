package com.leclowndu93150.atmosfera.client.sound.modifiers;

import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.world.level.Level;

public interface AtmosphericSoundModifier {
    float getModifier(EnvironmentContext context);

    interface Factory {
        AtmosphericSoundModifier create(Level world);
    }

    interface FactoryDeserializer {
        Factory deserialize(JsonObject object);
    }
}
