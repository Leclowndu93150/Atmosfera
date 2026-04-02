package com.leclowndu93150.atmosfera.client.sound;

import com.google.common.collect.ImmutableList;
import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import com.leclowndu93150.atmosfera.mixin.SoundManagerAccessor;
import com.leclowndu93150.atmosfera.mixin.SoundEngineAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtmosphericSoundHandler {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<Identifier, Music> MUSIC_CACHE = new HashMap<>();

    private ImmutableList<AtmosphericSound> sounds;
    private ImmutableList<AtmosphericSound> musics;

    private final ClientLevel world;

    public AtmosphericSoundHandler(ClientLevel world) {
        this.world = world;
        reloadDefinitions();
    }

    public void reloadDefinitions() {
        this.sounds = getSoundsFromDefinitions(Atmosfera.SOUND_DEFINITIONS, world);
        this.musics = getSoundsFromDefinitions(Atmosfera.MUSIC_DEFINITIONS, world);
    }

    private static ImmutableList<AtmosphericSound> getSoundsFromDefinitions(Map<Identifier, AtmosphericSoundDefinition> definitions, ClientLevel world) {
        var sounds = ImmutableList.<AtmosphericSound>builder();

        for (var definition : definitions.values()) {
            var modifiers = ImmutableList.<AtmosphericSoundModifier>builder();
            for (var factory : definition.modifierFactories()) {
                modifiers.add(factory.create(world));
            }
            sounds.add(new AtmosphericSound(definition.id(), definition.soundId(), definition.shape(), definition.size(), modifiers.build()));
        }

        return sounds.build();
    }

    public void tick() {
        var data = ClientLevelData.get(world);
        if (data != null) {
            data.updateEnvironmentContext();
        }

        var client = Minecraft.getInstance();
        SoundManager soundManager = client.getSoundManager();
        List<TickableSoundInstance> tickingSounds = ((SoundEngineAccessor) ((SoundManagerAccessor) soundManager).getSoundEngine()).getTickingSounds();

        for (var sound : sounds) {
            if (tickingSounds.stream()
                    .filter(s -> s instanceof AtmosphericSoundInstance)
                    .map(AtmosphericSoundInstance.class::cast)
                    .anyMatch(s -> sound.soundId().equals(s.getIdentifier())))
                continue;

            float volume = sound.getVolume(world);

            if (volume >= 0.0125 && client.options.getSoundSourceVolume(SoundSource.AMBIENT) > 0) {
                soundManager.queueTickingSound(new AtmosphericSoundInstance(sound, 0.0001f));
                Atmosfera.debug("volume > 0: {} - {}", sound.id(), volume);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public Music getMusicSound(Music original) {
        var client = Minecraft.getInstance();
        var data = ClientLevelData.get(world);
        if (data == null || !data.isInitialized() || client.options.getSoundSourceVolume(SoundSource.MUSIC) == 0)
            return original;

        SoundManager soundManager = client.getSoundManager();
        WeighedSoundEvents originalEvents = soundManager.getSoundEvent(original.sound().value().location());
        float originalWeight = originalEvents != null ? originalEvents.getWeight() : 1;

        List<Pair<Float, Music>> candidates = new ArrayList<>();
        float total = 0;

        candidates.add(Pair.of(originalWeight, original));
        total += originalWeight;

        for (var music : musics) {
            float volume = music.getVolume(world);

            if (volume >= 0.0125) {
                WeighedSoundEvents events = soundManager.getSoundEvent(music.soundId());
                float weight = AtmosferaConfig.customMusicWeightScale() * (events != null ? events.getWeight() : 1);
                var musicSound = MUSIC_CACHE.computeIfAbsent(music.soundId(), id ->
                        new Music(Holder.direct(SoundEvent.createVariableRangeEvent(id)), 12000, 24000, false));

                candidates.add(Pair.of(weight, musicSound));
                total += weight;
            }
        }

        float i = total <= 0 ? 0 : RANDOM.nextFloat() * total;

        for (Pair<Float, Music> pair : candidates) {
            i -= pair.getFirst();
            if (i < 0) return pair.getSecond();
        }

        return candidates.get(candidates.size() - 1).getSecond();
    }
}
