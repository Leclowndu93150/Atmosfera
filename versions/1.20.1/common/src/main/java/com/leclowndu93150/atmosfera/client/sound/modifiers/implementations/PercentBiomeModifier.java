package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Bound;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Range;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getBound;
import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getRange;

public record PercentBiomeModifier(Range range, Bound bound, ImmutableCollection<Holder<Biome>> biomes, ImmutableCollection<TagKey<Biome>> biomeTags) implements AtmosphericSoundModifier {
    public PercentBiomeModifier(Range range, Bound bound, ImmutableCollection<Holder<Biome>> biomes, ImmutableCollection<TagKey<Biome>> biomeTags) {
        var biomesBuilder = ImmutableList.<Holder<Biome>>builder();

        biomes:
        for (var biome : biomes) {
            for (var tag : biomeTags) {
                if (biome.is(tag)) {
                    continue biomes;
                }
            }
            biomesBuilder.add(biome);
        }

        this.biomes = biomesBuilder.build();
        this.biomeTags = biomeTags;
        this.range = range;
        this.bound = bound;
    }

    @Override
    public float getModifier(EnvironmentContext context) {
        float modifier = 0;

        for (var biomeEntry : this.biomes) {
            modifier += context.getBiomePercentage(biomeEntry.value());
        }

        for (var tag : this.biomeTags) {
            modifier += context.getBiomeTagPercentage(tag);
        }

        return range.apply(bound.apply(modifier));
    }

    public static AtmosphericSoundModifier.Factory create(JsonObject object) {
        var biomes = ImmutableList.<ResourceLocation>builder();
        var tags = ImmutableList.<ResourceLocation>builder();

        GsonHelper.getAsJsonArray(object, "biomes").forEach(biome -> {
            if (biome.getAsString().startsWith("#")) {
                tags.add(new ResourceLocation(biome.getAsString().substring(1)));
            } else {
                biomes.add(new ResourceLocation(biome.getAsString()));
            }
        });

        var range = getRange(object);
        var bound = getBound(object);

        return new PercentBiomeModifier.Factory(range, bound, biomes.build(), tags.build());
    }

    private record Factory(Range range, Bound bound, ImmutableCollection<ResourceLocation> biomes, ImmutableCollection<ResourceLocation> biomeTags) implements AtmosphericSoundModifier.Factory {
        @Override
        public AtmosphericSoundModifier create(Level world) {
            var biomes = ImmutableList.<Holder<Biome>>builder();
            var biomeRegistry = world.registryAccess().registryOrThrow(Registries.BIOME);

            for (var id : this.biomes) {
                biomeRegistry.getHolder(ResourceKey.create(Registries.BIOME, id)).ifPresent(biomes::add);
            }

            var tags = ImmutableList.<TagKey<Biome>>builder();
            for (var id : this.biomeTags) {
                tags.add(TagKey.create(Registries.BIOME, id));
            }

            return new PercentBiomeModifier(range, bound, biomes.build(), tags.build());
        }
    }
}
