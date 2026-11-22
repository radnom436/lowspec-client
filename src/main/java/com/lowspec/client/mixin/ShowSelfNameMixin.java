package com.lowspec.client.mixin;

import com.lowspec.client.LowSpecClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class ShowSelfNameMixin {

    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true, require = 0)
    private void lowspec$showSelf(LivingEntity entity, double distanceSq, CallbackInfoReturnable<Boolean> cir) {
        if (!LowSpecClient.nameTagOn()) return;
        Minecraft mc = Minecraft.getInstance();
        if (entity == mc.player && !mc.options.getCameraType().isFirstPerson() && !entity.isInvisible()) {
            cir.setReturnValue(true);
        }
    }
}