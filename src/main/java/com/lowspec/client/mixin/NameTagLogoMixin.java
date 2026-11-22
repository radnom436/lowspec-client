package com.lowspec.client.mixin;

import com.lowspec.client.LowSpecClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class NameTagLogoMixin {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true, require = 0)
    private void lowspec$logo(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (!LowSpecClient.nameTagOn()) return;
        if (entity != Minecraft.getInstance().player) return;
        Component original = cir.getReturnValue();
        if (original != null) {
            cir.setReturnValue(LowSpecClient.withLogo(original));
        }
    }
}