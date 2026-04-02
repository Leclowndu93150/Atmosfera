package com.leclowndu93150.atmosfera.mixin;

import com.leclowndu93150.atmosfera.AtmosferaConfig;
import com.leclowndu93150.atmosfera.client.sound.util.ClientLevelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public ClientLevel level;

    @Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true)
    private void atmosfera$getAmbientMusic(CallbackInfoReturnable<Music> cir) {
        if (!AtmosferaConfig.enableCustomMusic())
            return;

        Music original = cir.getReturnValue();
        if (original != null && original != Musics.MENU && original != Musics.CREDITS && level != null) {
            var data = ClientLevelData.get(level);
            if (data != null) {
                cir.setReturnValue(data.getSoundHandler().getMusicSound(original));
            }
        }
    }
}
