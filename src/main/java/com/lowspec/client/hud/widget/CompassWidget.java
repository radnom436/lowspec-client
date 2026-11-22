package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CompassWidget extends Widget {
    public CompassWidget() {
        super("compass");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("NORTH → 000°");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float yaw = mc.player.getYRot();
        if (yaw < 0) yaw += 360;

        float northDegrees = yaw;
        String dirs = "N";
        if (yaw >= 15 && yaw < 75) dirs = "NE";
        else if (yaw >= 75 && yaw < 105) dirs = "E";
        else if (yaw >= 105 && yaw < 255) dirs = "S";
        else if (yaw >= 255 && yaw < 285) dirs = "W";
        else if (yaw >= 285) dirs = "NW";

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        String text = String.format("NORTH %s  %d°", dirs, (int)northDegrees);
        g.drawString(font, text, x, y, color & 0xFFFFFF | 0xFF000000);
    }
}