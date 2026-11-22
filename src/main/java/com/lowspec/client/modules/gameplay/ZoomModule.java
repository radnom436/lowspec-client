package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class ZoomModule extends Module {
    private boolean zoomActive = false;
    private int zoomBase = 70;
    private float zoomCur = 70f;

    public ZoomModule() {
        super("zoom", "Zoom", "Smooth zoom with configurable FOV", Category.VISUAL, ComplianceTag.ALWAYS_SAFE, true);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft mc) {
        // Zoom handled in LowSpecClient
    }
}