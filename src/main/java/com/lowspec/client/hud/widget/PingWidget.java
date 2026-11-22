package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class PingWidget extends Widget {
    private int ping = -1;
    private long lastUpdate = 0;

    public PingWidget() {
        super("ping");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("PING  0ms");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.getConnection() == null) return;

        long now = System.currentTimeMillis();
        if (now - lastUpdate > 5000) {
            updatePing(mc);
            lastUpdate = now;
        }

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        String text;
        int col = 0xFFFFFFFF;
        if (ping < 0) {
            text = "PING  --";
        } else {
            text = String.format("PING  %dms", ping);
            if (ping < 100) col = 0xFF55FF55;
            else if (ping < 200) col = 0xFFFFFF55;
            else col = 0xFFFF5555;
        }

        g.text(font, text, x, y, col, false);
    }

    private void updatePing(Minecraft mc) {
        if (mc.getConnection() != null) {
            this.ping = (int)(mc.getConnection().getPing() + Math.random() * 10);
        }
    }
}