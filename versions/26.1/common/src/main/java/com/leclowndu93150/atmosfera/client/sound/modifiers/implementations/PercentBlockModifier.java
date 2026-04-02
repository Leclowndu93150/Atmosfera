package com.leclowndu93150.atmosfera.client.sound.modifiers.implementations;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Bound;
import com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.Range;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getBound;
import static com.leclowndu93150.atmosfera.client.sound.modifiers.CommonAttributes.getRange;

public record PercentBlockModifier(Range range, Bound bound, ImmutableCollection<Block> blocks, ImmutableCollection<TagKey<Block>> blockTags) implements AtmosphericSoundModifier, AtmosphericSoundModifier.Factory {
    public PercentBlockModifier(Range range, Bound bound, ImmutableCollection<Block> blocks, ImmutableCollection<TagKey<Block>> blockTags) {
        var blocksBuilder = ImmutableList.<Block>builder();

        blocks:
        for (var block : blocks) {
            for (var tag : blockTags) {
                if (block.defaultBlockState().is(tag)) {
                    continue blocks;
                }
            }
            blocksBuilder.add(block);
        }

        this.blocks = blocksBuilder.build();
        this.blockTags = blockTags;
        this.range = range;
        this.bound = bound;
    }

    @Override
    public float getModifier(EnvironmentContext context) {
        float modifier = 0;

        for (var block : this.blocks) {
            modifier += context.getBlockTypePercentage(block);
        }

        for (var tag : this.blockTags) {
            modifier += context.getBlockTagPercentage(tag);
        }

        return range.apply(bound.apply(modifier));
    }

    public static PercentBlockModifier create(JsonObject object) {
        var blocks = ImmutableList.<Block>builder();
        var tags = ImmutableList.<TagKey<Block>>builder();

        GsonHelper.getAsJsonArray(object, "blocks").forEach(block -> {
            if (block.getAsString().startsWith("#")) {
                var tagId = Identifier.parse(block.getAsString().substring(1));
                tags.add(TagKey.create(Registries.BLOCK, tagId));
            } else {
                var blockId = Identifier.parse(block.getAsString());
                BuiltInRegistries.BLOCK.get(blockId).ifPresent(ref -> blocks.add(ref.value()));
            }
        });

        var range = getRange(object);
        var bound = getBound(object);

        return new PercentBlockModifier(range, bound, blocks.build(), tags.build());
    }

    @Override
    public AtmosphericSoundModifier create(Level world) {
        return this;
    }
}
