package com.leclowndu93150.atmosfera.mixin;

import com.leclowndu93150.atmosfera.Atmosfera;
import com.leclowndu93150.atmosfera.AtmosferaConfig;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeighedSoundEvents.class)
public class WeighedSoundEventsMixin {
    @Unique
    private Identifier atmosfera$id;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void atmosfera$captureIdentifier(Identifier id, String subtitle, CallbackInfo ci) {
        this.atmosfera$id = id;
    }

    @Inject(method = "getSubtitle", at = @At("HEAD"), cancellable = true)
    public void atmosfera$disableSubtitle(CallbackInfoReturnable<Component> cir) {
        if (atmosfera$id != null && Atmosfera.SOUND_DEFINITIONS.containsKey(atmosfera$id) && !AtmosferaConfig.showSubtitle(atmosfera$id)) {
            cir.setReturnValue(null);
        }
    }
}
