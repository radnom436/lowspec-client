package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class FreelookModule extends Module {
    private float savedYaw;
    private float savedPitch;
    private boolean isActive = false;

    public FreelookModule() {
        super("freelook", "Freelook", "Separate camera rotation from body", Category.GAMEPLAY, ComplianceTag.SERVER_RESTRICTED, false);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void onDisable() {
        resetCamera();
    }

    private void onTick(Minecraft mc) {
        if (mc.player == null) return;

        // Key binding should be set up by LowSpecClient
        // This is a placeholder - actual key binding would be passed from main class
        boolean freelookKey = net.minecraft.client.KeyMapping.DEFAULT_KEYBOARD_BINDES.get("key.lowspec.freelook").isDown();

        if (freelookKey && !isActive) {
            isActive = true;
            savedYaw = mc.player.getYRot();
            savedPitch = mc.player.getXRot();
        } else if (!freelookKey && isActive) {
            isActive = false;
            resetCamera();
        }
    }

    private void resetCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.setYRot(savedYaw);
        mc.player.setXRot(savedPitch);
    }

    public boolean isActive() {
        return isActive;
    }
}