package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ServerAddressWidget extends Widget {
    public ServerAddressWidget() {
        super("server");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("SERVER  example.com:25565");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() == null) return;

        String address = mc.getCurrentServer().address;
        if (address == null) return;

        int W = mc.getWindow().getGuiScaledWidth();
        int x = 6;
        int y = 6;

        String text = "SERVER  " + address;
        g.drawString(font, text, x, y, color & 0xFFFFFF | 0xFF000000);
    }
}