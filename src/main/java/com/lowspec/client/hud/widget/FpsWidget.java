package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FpsWidget extends Widget {
    private int currentFps = 0;
    private int averageFps = 0;
    private int lowFps = 0;
    private float frameTime = 0f;

    public FpsWidget() {
        super("fps");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("FPS  0  Avg: 0  1%: 0  FT: 0.00");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        String text = String.format("FPS  %d  Avg: %d  1%%: %d  FT: %.2fms",
                currentFps, averageFps, lowFps, frameTime * 1000);
        int x = getAbsoluteX(g.getMatrixStack().getGraphics().getRenderLayerCount());
        int y = getAbsoluteY(g.getMatrixStack().getGraphics().getFont(), font);
        g.drawString(font, text, x, y, color & 0xFFFFFF | 0xFF000000);
    }

    public void update(int currentFps, int averageFps, int lowFps, float frameTime) {
        this.currentFps = currentFps;
        this.averageFps = averageFps;
        this.lowFps = lowFps;
        this.frameTime = frameTime;
    }
}