package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Bound;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Range;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.world.level.Level;

import java.util.function.Function;

import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getBound;
import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getRange;

public record SimpleBoundedCondition(Range range, Bound bound, Function<EnvironmentContext, Number> valueGetter) implements AtmosphericSoundModifier, AtmosphericSoundModifier.Factory {
    @Override
    public float getModifier(EnvironmentContext context) {
        float value = valueGetter.apply(context).floatValue();
        return range.apply(bound.apply(value));
    }

    @Override
    public AtmosphericSoundModifier create(Level world) {
        return this;
    }

    public static SimpleBoundedCondition altitude(JsonObject object) {
        return create(object, EnvironmentContext::getAltitude);
    }

    public static SimpleBoundedCondition elevation(JsonObject object) {
        return create(object, EnvironmentContext::getElevation);
    }

    public static SimpleBoundedCondition skyVisibility(JsonObject object) {
        return create(object, EnvironmentContext::getSkyVisibility);
    }

    public static SimpleBoundedCondition create(JsonObject object, Function<EnvironmentContext, Number> valueGetter) {
        var range = getRange(object);
        var bound = getBound(object);
        return new SimpleBoundedCondition(range, bound, valueGetter);
    }
}
