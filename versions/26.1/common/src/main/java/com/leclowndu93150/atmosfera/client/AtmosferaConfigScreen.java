package com.leclowndu93150.atmosfera.client;

import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.AtmosphericSoundDefinition;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class AtmosferaConfigScreen {
    public static Screen create(Screen parent) {
        var builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal(Atmosfera.MOD_NAME));

        builder.category(buildGeneralCategory());
        builder.category(buildVolumesCategory());
        builder.category(buildSubtitlesCategory());

        builder.save(AtmosferaConfig::write);

        return builder.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        var cat = ConfigCategory.createBuilder()
                .name(Component.translatable("config.category.atmosfera.general"));

        cat.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("config.value.atmosfera.enable_custom_music"))
                .description(OptionDescription.of(
                        Component.translatable("config.value.atmosfera.enable_custom_music.@Tooltip")))
                .binding(true,
                        AtmosferaConfig::enableCustomMusic,
                        AtmosferaConfig::setEnableCustomMusic)
                .controller(TickBoxControllerBuilder::create)
                .build());

        cat.option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.value.atmosfera.custom_music_weight_scale"))
                .description(OptionDescription.of(
                        Component.translatable("config.value.atmosfera.custom_music_weight_scale_explanation.@Tooltip")))
                .binding(250,
                        () -> (int) (AtmosferaConfig.customMusicWeightScale() * 100),
                        v -> AtmosferaConfig.setCustomMusicWeightScale(v / 100f))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 1000)
                        .step(1)
                        .formatValue(v -> Component.literal(v + "%")))
                .build());

        cat.option(LabelOption.create(
                Component.translatable("config.value.atmosfera.custom_music_weight_scale_explanation")));

        cat.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Print Debug Messages"))
                .binding(false,
                        AtmosferaConfig::printDebugMessages,
                        AtmosferaConfig::setPrintDebugMessages)
                .controller(TickBoxControllerBuilder::create)
                .build());

        return cat.build();
    }

    private static ConfigCategory buildVolumesCategory() {
        var cat = ConfigCategory.createBuilder()
                .name(Component.translatable("config.category.atmosfera.volumes"));

        var soundGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.subcategory.atmosfera.ambient_sound"))
                .collapsed(false);

        var musicGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.subcategory.atmosfera.music"))
                .collapsed(false);

        int soundCount = 0;
        int musicCount = 0;

        for (Map.Entry<Identifier, Integer> entry : AtmosferaConfig.getVolumeModifiers().entrySet()) {
            Identifier id = entry.getKey();

            if (Atmosfera.SOUND_DEFINITIONS.containsKey(id)) {
                AtmosphericSoundDefinition def = Atmosfera.SOUND_DEFINITIONS.get(id);
                String langId = id.toString().replace(':', '.');

                soundGroup.option(Option.<Integer>createBuilder()
                        .name(Component.translatable(langId))
                        .description(OptionDescription.of(
                                Component.literal(langId).withStyle(ChatFormatting.GRAY),
                                Component.translatable("subtitle." + langId),
                                Component.translatable("config.value.atmosfera.sound_tip.@Tooltip")))
                        .binding(def.defaultVolume(),
                                () -> AtmosferaConfig.getVolumeModifiers().getOrDefault(id, def.defaultVolume()),
                                v -> AtmosferaConfig.getVolumeModifiers().put(id, v))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 200)
                                .step(1)
                                .formatValue(v -> Component.literal(v + "%")))
                        .build());
                soundCount++;
            } else if (Atmosfera.MUSIC_DEFINITIONS.containsKey(id)) {
                AtmosphericSoundDefinition def = Atmosfera.MUSIC_DEFINITIONS.get(id);
                String langId = id.toString().replace(':', '.');

                musicGroup.option(Option.<Integer>createBuilder()
                        .name(Component.translatable(langId))
                        .description(OptionDescription.of(
                                Component.literal(langId).withStyle(ChatFormatting.GRAY),
                                Component.translatable("config.value.atmosfera.sound_tip.@Tooltip")))
                        .binding(def.defaultVolume(),
                                () -> AtmosferaConfig.getVolumeModifiers().getOrDefault(id, def.defaultVolume()),
                                v -> AtmosferaConfig.getVolumeModifiers().put(id, v))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 200)
                                .step(1)
                                .formatValue(v -> Component.literal(v + "%")))
                        .build());
                musicCount++;
            }
        }

        if (soundCount + musicCount == 0) {
            cat.option(LabelOption.create(
                    Component.translatable("config.atmosfera.resource_pack_warning").withStyle(ChatFormatting.RED)));
        } else {
            if (soundCount > 0) cat.group(soundGroup.build());
            if (musicCount > 0) cat.group(musicGroup.build());
        }

        return cat.build();
    }

    private static ConfigCategory buildSubtitlesCategory() {
        var cat = ConfigCategory.createBuilder()
                .name(Component.translatable("config.category.atmosfera.subtitles"));

        int count = 0;

        for (Map.Entry<Identifier, Boolean> entry : AtmosferaConfig.getSubtitleModifiers().entrySet()) {
            Identifier id = entry.getKey();

            if (Atmosfera.SOUND_DEFINITIONS.containsKey(id)) {
                AtmosphericSoundDefinition def = Atmosfera.SOUND_DEFINITIONS.get(id);
                String langId = id.toString().replace(':', '.');

                cat.option(Option.<Boolean>createBuilder()
                        .name(Component.translatable(langId))
                        .description(OptionDescription.of(
                                Component.literal(langId).withStyle(ChatFormatting.GRAY),
                                Component.translatable("subtitle." + langId)))
                        .binding(def.hasSubtitleByDefault(),
                                () -> AtmosferaConfig.getSubtitleModifiers().getOrDefault(id, def.hasSubtitleByDefault()),
                                v -> AtmosferaConfig.getSubtitleModifiers().put(id, v))
                        .controller(BooleanControllerBuilder::create)
                        .build());
                count++;
            }
        }

        if (count == 0) {
            cat.option(LabelOption.create(
                    Component.translatable("config.atmosfera.resource_pack_warning").withStyle(ChatFormatting.RED)));
        }

        return cat.build();
    }
}
