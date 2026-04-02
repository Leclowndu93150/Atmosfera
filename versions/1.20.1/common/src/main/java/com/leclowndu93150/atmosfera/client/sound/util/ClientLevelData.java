package com.leclowndu93150.atmosfera.client.sound.util;

import com.leclowndu93150.atmosfera.client.sound.AtmosphericSoundHandler;
import com.leclowndu93150.atmosfera.world.context.ContextUtil;
import com.leclowndu93150.atmosfera.world.context.EnvironmentContext;
import com.leclowndu93150.atmosfera.world.context.Sphere;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.EnumMap;
import java.util.Objects;
import java.util.WeakHashMap;

public class ClientLevelData {
    private static final WeakHashMap<ClientLevel, ClientLevelData> DATA = new WeakHashMap<>();

    private final AtmosphericSoundHandler soundHandler;
    private EnumMap<EnvironmentContext.Size, Sphere> environmentContexts;
    private boolean initialized;
    private int updateTimer = 0;

    private ClientLevelData(ClientLevel level) {
        this.soundHandler = new AtmosphericSoundHandler(level);
    }

    public static ClientLevelData getOrCreate(ClientLevel level) {
        return DATA.computeIfAbsent(level, ClientLevelData::new);
    }

    public static ClientLevelData get(ClientLevel level) {
        return DATA.get(level);
    }

    public AtmosphericSoundHandler getSoundHandler() {
        return soundHandler;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public EnvironmentContext getEnvironmentContext(EnvironmentContext.Size size, EnvironmentContext.Shape shape) {
        if (!initialized) return null;
        return switch (shape) {
            case UPPER_HEMISPHERE -> environmentContexts.get(size).getUpperHemisphere();
            case LOWER_HEMISPHERE -> environmentContexts.get(size).getLowerHemisphere();
            case SPHERE -> environmentContexts.get(size);
        };
    }

    public void updateEnvironmentContext() {
        if (!initialized) {
            environmentContexts = new EnumMap<>(EnvironmentContext.Size.class);
            environmentContexts.put(EnvironmentContext.Size.SMALL, new Sphere(EnvironmentContext.Size.SMALL));
            environmentContexts.put(EnvironmentContext.Size.MEDIUM, new Sphere(EnvironmentContext.Size.MEDIUM));
            environmentContexts.put(EnvironmentContext.Size.LARGE, new Sphere(EnvironmentContext.Size.LARGE));
            initialized = true;
        }

        if (--updateTimer <= 0 && ContextUtil.EXECUTOR.getQueue().isEmpty()) {
            var player = Objects.requireNonNull(Minecraft.getInstance().player);
            environmentContexts.get(EnvironmentContext.Size.SMALL).update(player);
            environmentContexts.get(EnvironmentContext.Size.MEDIUM).update(player);
            environmentContexts.get(EnvironmentContext.Size.LARGE).update(player);
            updateTimer = 20;
        }
    }
}
