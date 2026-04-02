package com.leclowndu93150.atmosfera.client.sound;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.modifiers.AtmosphericSoundModifier;
import com.leclowndu93150.atmosfera.client.sound.modifiers.implementations.ConfigModifier;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Locale;
import java.util.Map;

public class SoundDefinitionsReloader extends SimplePreparableReloadListener<Void> {
    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void nothing, ResourceManager manager, ProfilerFiller profiler) {
        loadSoundDefinitions(manager, "sounds/ambient", Atmosfera.SOUND_DEFINITIONS);
        loadSoundDefinitions(manager, "sounds/music", Atmosfera.MUSIC_DEFINITIONS);
        AtmosferaConfig.loadedSoundDefinitions();

        var client = Minecraft.getInstance();
        if (client != null && client.level != null) {
            var data = ClientLevelData.get(client.level);
            if (data != null) {
                data.getSoundHandler().reloadDefinitions();
            }
        }
    }

    private static void loadSoundDefinitions(ResourceManager manager, String sourceFolder, Map<Identifier, AtmosphericSoundDefinition> destination) {
        destination.clear();

        Map<Identifier, Resource> resources = manager.listResources(sourceFolder + "/definitions", id -> id.getPath().endsWith(".json"));

        for (Identifier resource : resources.keySet()) {
            Identifier id = Identifier.fromNamespaceAndPath(
                    resource.getNamespace(),
                    resource.getPath().substring(
                            resource.getPath().indexOf("definitions/") + "definitions/".length(),
                            resource.getPath().indexOf(".json")
                    )
            );

            try (var reader = resources.get(resource).openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                Identifier soundId = Identifier.parse(GsonHelper.getAsString(json, "sound"));

                EnvironmentContext.Shape shape = getShape(json, id);
                EnvironmentContext.Size size = getSize(json, id);
                ImmutableCollection<AtmosphericSoundModifier.Factory> modifiers = getModifiers(json, id);
                int defaultVolume = GsonHelper.getAsInt(json, "default_volume", 100);
                boolean showSubtitlesByDefault = GsonHelper.getAsBoolean(json, "default_subtitle", true);

                destination.put(id, new AtmosphericSoundDefinition(id, soundId, shape, size, defaultVolume, showSubtitlesByDefault, modifiers));
            } catch (Exception e) {
                Atmosfera.error("Failed to load sound event '{}'", id, e);
            }
        }
    }

    private static EnvironmentContext.Shape getShape(JsonObject json, Identifier id) {
        if (json.has("shape")) {
            return EnvironmentContext.Shape.valueOf(json.getAsJsonPrimitive("shape").getAsString().toUpperCase(Locale.ROOT));
        } else {
            throw new RuntimeException("Sound definition '%s' is missing \"shape\" field.".formatted(id));
        }
    }

    private static EnvironmentContext.Size getSize(JsonObject json, Identifier id) {
        if (json.has("size")) {
            return EnvironmentContext.Size.valueOf(json.getAsJsonPrimitive("size").getAsString().toUpperCase(Locale.ROOT));
        } else {
            throw new RuntimeException("Sound definition '%s' is missing \"size\" field.".formatted(id));
        }
    }

    private static ImmutableCollection<AtmosphericSoundModifier.Factory> getModifiers(JsonObject json, Identifier id) {
        var modifiers = ImmutableList.<AtmosphericSoundModifier.Factory>builder();

        modifiers.add(new ConfigModifier(id));

        if (json.has("modifiers")) {
            for (JsonElement element : json.get("modifiers").getAsJsonArray()) {
                JsonObject modifierJson = element.getAsJsonObject();

                if (!modifierJson.has("type")) {
                    throw new RuntimeException("Modifier for sound definition '%s' is missing \"type\" field.".formatted(id));
                }

                String type = modifierJson.get("type").getAsString();
                var factory = AtmosphericSoundModifierRegistry.get(type);

                if (factory == null) {
                    Atmosfera.warn("Modifier type \"{}\" does not exist", type);
                } else {
                    modifiers.add(factory.deserialize(modifierJson));
                }
            }
        }

        return modifiers.build();
    }
}
