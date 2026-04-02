package com.leclowndu93150.atmosfera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leclowndu93150.atmosfera.client.sound.AtmosphericSoundDefinition;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class AtmosferaConfig {
    private static Path configPath;

    private static final TreeMap<ResourceLocation, Integer> VOLUME_MODIFIERS = new TreeMap<>(Comparator.comparing(ResourceLocation::toString));
    private static final TreeMap<ResourceLocation, Boolean> SUBTITLE_MODIFIERS = new TreeMap<>(Comparator.comparing(ResourceLocation::toString));
    private static boolean printDebugMessages = false;
    private static boolean enableCustomMusic = true;
    private static float customMusicWeightScale = 2.5f;

    public static void init() {
        configPath = Atmosfera.getConfigDir().resolve("atmosfera.json");
        if (!Files.exists(configPath)) {
            write();
        } else {
            try {
                read();
            } catch (Exception e) {
                Atmosfera.error("failed to read config! overwriting with default config...", e);
                write();
            }
        }
    }

    public static void read() throws IOException {
        if (!Files.exists(configPath))
            return;

        String jsonString = Files.readString(configPath);
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

        if (json.has("general")) {
            JsonObject general = json.getAsJsonObject("general");
            if (general.has("enable_custom_music")) {
                enableCustomMusic = general.get("enable_custom_music").getAsBoolean();
            }
            if (general.has("custom_music_weight_scale")) {
                customMusicWeightScale = general.get("custom_music_weight_scale").getAsFloat();
            }
        }

        if (json.has("volumes")) {
            for (var entry : json.get("volumes").getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    VOLUME_MODIFIERS.put(new ResourceLocation(entry.getKey()), entry.getValue().getAsInt());
                }
            }
        }

        if (json.has("subtitles")) {
            for (var entry : json.get("subtitles").getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    SUBTITLE_MODIFIERS.put(new ResourceLocation(entry.getKey()), entry.getValue().getAsBoolean());
                }
            }
        }

        if (json.has("debug")) {
            JsonObject debug = json.getAsJsonObject("debug");
            if (debug.has("print_debug_messages")) {
                printDebugMessages = debug.get("print_debug_messages").getAsBoolean();
            }
        }
    }

    public static void loadedSoundDefinitions() {
        for (AtmosphericSoundDefinition sound : Atmosfera.SOUND_DEFINITIONS.values()) {
            VOLUME_MODIFIERS.putIfAbsent(sound.id(), sound.defaultVolume());
            SUBTITLE_MODIFIERS.putIfAbsent(sound.id(), sound.hasSubtitleByDefault());
        }

        for (AtmosphericSoundDefinition sound : Atmosfera.MUSIC_DEFINITIONS.values()) {
            VOLUME_MODIFIERS.putIfAbsent(sound.id(), sound.defaultVolume());
        }

        write();
    }

    public static String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject general = new JsonObject();
        general.addProperty("enable_custom_music", enableCustomMusic);
        general.addProperty("custom_music_weight_scale", customMusicWeightScale);

        JsonObject debug = new JsonObject();
        debug.addProperty("print_debug_messages", printDebugMessages);

        JsonObject config = new JsonObject();
        config.add("general", general);
        config.add("volumes", gson.toJsonTree(VOLUME_MODIFIERS));
        config.add("subtitles", gson.toJsonTree(SUBTITLE_MODIFIERS));
        config.add("debug", debug);

        return gson.toJson(config);
    }

    public static void write() {
        try {
            Files.writeString(configPath, serialize());
        } catch (Exception e) {
            Atmosfera.error("could not write config file!", e);
        }
    }

    public static float volumeModifier(ResourceLocation soundId) {
        return VOLUME_MODIFIERS.getOrDefault(soundId, 100) / 100F;
    }

    public static boolean showSubtitle(ResourceLocation soundId) {
        return SUBTITLE_MODIFIERS.getOrDefault(soundId, true);
    }

    public static boolean printDebugMessages() {
        return printDebugMessages;
    }

    public static void setPrintDebugMessages(boolean value) {
        printDebugMessages = value;
    }

    public static boolean enableCustomMusic() {
        return enableCustomMusic;
    }

    public static void setEnableCustomMusic(boolean value) {
        enableCustomMusic = value;
    }

    public static float customMusicWeightScale() {
        return customMusicWeightScale;
    }

    public static void setCustomMusicWeightScale(float value) {
        customMusicWeightScale = value;
    }

    public static TreeMap<ResourceLocation, Integer> getVolumeModifiers() {
        return VOLUME_MODIFIERS;
    }

    public static TreeMap<ResourceLocation, Boolean> getSubtitleModifiers() {
        return SUBTITLE_MODIFIERS;
    }
}
