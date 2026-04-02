package com.leclowndu93150.atmosfera.world.context;

import com.leclowndu93150.atmosfera.mixin.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Sphere extends AbstractEnvironmentContext {
    final Hemisphere upperHemisphere;
    final Hemisphere lowerHemisphere;

    public Sphere(EnvironmentContext.Size size) {
        this.upperHemisphere = new Hemisphere(ContextUtil.OFFSETS[EnvironmentContext.Shape.UPPER_HEMISPHERE.ordinal()][size.ordinal()], this);
        this.lowerHemisphere = new Hemisphere(ContextUtil.OFFSETS[EnvironmentContext.Shape.LOWER_HEMISPHERE.ordinal()][size.ordinal()], this);
        this.bossBars = new HashSet<>();
    }

    @Override
    public float getBlockTypePercentage(Block block) {
        return (upperHemisphere.getBlockTypePercentage(block) + lowerHemisphere.getBlockTypePercentage(block)) / 2F;
    }

    @Override
    public float getBlockTagPercentage(TagKey<Block> blocks) {
        return (upperHemisphere.getBlockTagPercentage(blocks) + lowerHemisphere.getBlockTagPercentage(blocks)) / 2F;
    }

    @Override
    public float getBiomePercentage(Biome biome) {
        return (upperHemisphere.getBiomePercentage(biome) + lowerHemisphere.getBiomePercentage(biome)) / 2F;
    }

    @Override
    public float getBiomeTagPercentage(TagKey<Biome> biomes) {
        return (upperHemisphere.getBiomeTagPercentage(biomes) + lowerHemisphere.getBiomeTagPercentage(biomes)) / 2F;
    }

    @Override
    public float getSkyVisibility() {
        return (upperHemisphere.getSkyVisibility() + lowerHemisphere.getSkyVisibility()) / 2F;
    }

    public void update(LocalPlayer player) {
        var world = player.level();
        var pos = player.blockPosition();

        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos().set(pos);

            int count = 0;
            while (world.getBlockState(mut).isAir() && mut.getY() > 0) {
                count += 1;
                mut.move(Direction.DOWN);
            }
            altitude = count;

            bossBars.clear();

            var bossOverlay = Minecraft.getInstance().gui.getBossOverlay();
            Map<UUID, LerpingBossEvent> bossBarMap = ((BossHealthOverlayAccessor) bossOverlay).getEvents();

            for (var bossBar : bossBarMap.values()) {
                String value = bossBar.getName().getContents() instanceof TranslatableContents translatable ? translatable.getKey() : bossBar.getName().toString();
                bossBars.add(value);
            }

            elevation = pos.getY();

            long timeOfDay = world.getOverworldClockTime() % 24000;
            isDay = 0 <= timeOfDay && timeOfDay < 13000;

            isRainy = world.isRaining();
            isStormy = world.isThundering();
            vehicle = player.getVehicle();

            ContextUtil.EXECUTOR.execute(() -> upperHemisphere.update(world, pos.above()));
            ContextUtil.EXECUTOR.execute(() -> lowerHemisphere.update(world, pos.below()));
        }
    }

    public EnvironmentContext getUpperHemisphere() {
        return upperHemisphere;
    }

    public EnvironmentContext getLowerHemisphere() {
        return lowerHemisphere;
    }
}
