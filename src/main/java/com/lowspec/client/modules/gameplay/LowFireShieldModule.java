package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class LowFireShieldModule extends Module {
    private float fireOpacity = 1.0f;
    private float shieldOpacity = 1.0f;

    public LowFireShieldModule() {
        super("lowfire", "Low Fire/Shield", "Reduce fire and shield animation opacity", Category.VISUAL, ComplianceTag.SERVER_RESTRICTED, false);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft mc) {
        // Adjust through client settings
    }

    public float getFireOpacity() {
        return fireOpacity;
    }

    public void setFireOpacity(float opacity) {
        this.fireOpacity = Math.max(0f, Math.min(1f, opacity));
    }

    public float getShieldOpacity() {
        return shieldOpacity;
    }

    public void setShieldOpacity(float opacity) {
        this.shieldOpacity = Math.max(0f, Math.min(1f, opacity));
    }
}