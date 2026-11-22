package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SpeedWidget extends Widget {
    public SpeedWidget() {
        super("speed");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("SPEED  0.0");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double speed = calculateSpeed(mc);

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        String text = String.format("SPEED  %.1f", speed);
        g.text(font, text, x, y, 0xFFFFFFFF, false);
    }

    private double calculateSpeed(Minecraft mc) {
        if (mc.player == null) return 0;
        return mc.player.getDeltaMovement().length() * 0.0625 * 20;
    }
}