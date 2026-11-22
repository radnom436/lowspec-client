package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DayTimeWidget extends Widget {
    public DayTimeWidget() {
        super("daytime");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("DAY  0  12:00");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        long time = mc.level.getDayTime();
        int day = (int)(time / 24000) + 1;
        int timeOfDay = (int)(time % 24000);
        int hour = (timeOfDay / 1000 + 6) % 24;
        int minute = (int)((timeOfDay % 1000) * 0.06);

        int W = mc.getWindow().getGuiScaledWidth();
        int x = 6;
        int y = 6;

        String text = String.format("DAY  %d  %02d:%02d", day, hour, minute);
        g.drawString(font, text, x, y, color & 0xFFFFFF | 0xFF000000);
    }
}