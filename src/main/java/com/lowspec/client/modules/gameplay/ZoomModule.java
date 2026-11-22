package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
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
        // Zoom key is handled in LowSpecClient
        boolean want = LowSpecClient.isEnabled("zoom") && LowSpecClient.isZoomKeyDown();

        if (want) {
            if (!zoomActive) {
                zoomActive = true;
                zoomBase = mc.options.fov().get();
                zoomCur = zoomBase;
            }
            zoomCur += (30f - zoomCur) * 0.4f;
            mc.options.fov().set(Math.round(zoomCur));
        } else if (zoomActive) {
            zoomCur += (zoomBase - zoomCur) * 0.4f;
            if (Math.abs(zoomBase - zoomCur) < 0.8f) {
                mc.options.fov().set(zoomBase);
                zoomActive = false;
            } else {
                mc.options.fov().set(Math.round(zoomCur));
            }
        }
    }

    public float getTargetFov() {
        return 30f;
    }
}