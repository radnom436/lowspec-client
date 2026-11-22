package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class ToggleSprintModule extends Module {
    private boolean sprintForced = false;

    public ToggleSprintModule() {
        super("sprint", "Toggle Sprint", "Toggle sprint on/off with a key", Category.GAMEPLAY, ComplianceTag.SERVER_RESTRICTED, false);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void onDisable() {
        if (sprintForced) {
            Minecraft mc = Minecraft.getInstance();
            mc.options.keySprint.setDown(false);
            sprintForced = false;
        }
    }

    private void onTick(Minecraft mc) {
        if (mc.player == null) return;

        boolean forward = mc.options.keyUp.isDown();
        boolean shouldSprint = forward && !mc.player.isUsingItem();

        if (shouldSprint && !mc.options.keySprint.isDown()) {
            mc.options.keySprint.setDown(true);
            sprintForced = true;
        } else if (!shouldSprint && sprintForced) {
            mc.options.keySprint.setDown(false);
            sprintForced = false;
        }
    }
}