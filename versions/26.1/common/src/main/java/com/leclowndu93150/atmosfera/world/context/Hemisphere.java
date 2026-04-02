package com.leclowndu93150.atmosfera.world.context;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Hemisphere implements EnvironmentContext {
    private final byte[][] offsets;
    private final Sphere sphere;

    private final Map<Block, Integer> blockTypes = new ConcurrentHashMap<>();
    private final Map<Identifier, Integer> blockTags = new ConcurrentHashMap<>();
    private final Map<Biome, Integer> biomeTypes = new ConcurrentHashMap<>();
    private final Map<Identifier, Integer> biomeTags = new ConcurrentHashMap<>();

    private final AtomicInteger blockCount = new AtomicInteger();
    private final AtomicInteger skyVisibility = new AtomicInteger();

    Hemisphere(byte[][] offsets, Sphere sphere) {
        this.sphere = sphere;
        this.offsets = offsets;
    }

    @Override
    public float getBlockTypePercentage(Block block) {
        return blockTypes.getOrDefault(block, 0) / (float) blockCount.get();
    }

    @Override
    public float getBlockTagPercentage(TagKey<Block> blocks) {
        return blockTags.getOrDefault(blocks.location(), 0) / (float) blockCount.get();
    }

    @Override
    public float getBiomePercentage(Biome biome) {
        return biomeTypes.getOrDefault(biome, 0) / (float) blockCount.get();
    }

    @Override
    public float getBiomeTagPercentage(TagKey<Biome> biomes) {
        return biomeTags.getOrDefault(biomes.location(), 0) / (float) blockCount.get();
    }

    @Override
    public float getAltitude() {
        return sphere.altitude;
    }

    @Override
    public float getElevation() {
        return sphere.elevation;
    }

    @Override
    public float getSkyVisibility() {
        return skyVisibility.get() / (float) blockCount.get();
    }

    @Override
    public boolean isDaytime() {
        return sphere.isDay;
    }

    @Override
    public boolean isRainy() {
        return sphere.isRainy;
    }

    @Override
    public boolean isStormy() {
        return sphere.isStormy;
    }

    @Override
    public Entity getVehicle() {
        return sphere.vehicle;
    }

    @Override
    public Collection<String> getBossBars() {
        return sphere.bossBars;
    }

    private void clear() {
        blockCount.set(0);
        skyVisibility.set(0);
        blockTypes.replaceAll((block, integer) -> 0);
        blockTags.replaceAll((identifier, integer) -> 0);
        biomeTypes.replaceAll((biome, integer) -> 0);
        biomeTags.replaceAll((identifier, integer) -> 0);
    }

    @SuppressWarnings("deprecation")
    private void add(Level world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        blockTypes.merge(block, 1, Integer::sum);
        block.builtInRegistryHolder().tags().forEach(blockTag -> {
            blockTags.merge(blockTag.location(), 1, Integer::sum);
        });

        Holder<Biome> biomeEntry = world.getBiome(pos);
        Biome biome = biomeEntry.value();
        biomeEntry.tags().forEach(biomeTag -> {
            biomeTags.merge(biomeTag.location(), 1, Integer::sum);
        });

        biomeTypes.merge(biome, 1, Integer::sum);
        skyVisibility.addAndGet(world.getBrightness(LightLayer.SKY, pos) / 15);
        blockCount.incrementAndGet();
    }

    void update(Level world, BlockPos center) {
        clear();

        var mut = new BlockPos.MutableBlockPos();
        for (byte[] a : offsets) {
            mut.set(center.getX() + a[0], center.getY() + a[1], center.getZ() + a[2]);
            add(world, mut);
        }
    }
}
