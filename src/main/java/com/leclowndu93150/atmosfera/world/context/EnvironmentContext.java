package com.leclowndu93150.atmosfera.world.context;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

public interface EnvironmentContext {
    float getBlockTypePercentage(Block block);

    float getBlockTagPercentage(TagKey<Block> blocks);

    float getBiomePercentage(Biome biome);

    float getBiomeTagPercentage(TagKey<Biome> biomes);

    float getAltitude();

    float getElevation();

    float getSkyVisibility();

    boolean isDaytime();

    boolean isRainy();

    boolean isStormy();

    Entity getVehicle();

    Collection<String> getBossBars();

    static void init() {
        ContextUtil.init();
    }

    enum Shape {
        UPPER_HEMISPHERE, LOWER_HEMISPHERE, SPHERE
    }

    enum Size {
        SMALL((byte) 4),
        MEDIUM((byte) 8),
        LARGE((byte) 16);

        public final byte radius;

        Size(byte radius) {
            this.radius = radius;
        }
    }
}
