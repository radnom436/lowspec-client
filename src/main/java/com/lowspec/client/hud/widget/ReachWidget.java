package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class ReachWidget extends Widget {
    public ReachWidget() {
        super("reach");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("REACH  4.5");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Calculate last hit reach
        float reach = calculateLastHitReach(mc);

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        String text = String.format("REACH  %.1f", reach);
        g.drawString(font, text, x, y, color & 0xFFFFFF | 0xFF000000);
    }

    private float calculateLastHitReach(Minecraft mc) {
        // Simplified - actual implementation would track last hit entity
        Player target = mc.targetedEntity;
        if (target == null) return 0f;

        double dist = mc.player.distanceTo(target);
        return (float) dist;
    }
}