package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MemoryWidget extends Widget {
    public enum Mode {
        RAM_ONLY, RAM_AND_GPU
    }

    private Mode mode = Mode.RAM_ONLY;
    private boolean showMax = true;

    public MemoryWidget() {
        super("memory");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("MEM  0 / 0 MB");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();

        String text = String.format("MEM  %d / %d MB", used >> 20, max >> 20);

        int x = getAbsoluteX(0);
        int y = getAbsoluteY(0, font);

        g.text(font, text, x, y, 0xFFFFFFFF, false);
    }

    public long getUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public double getUsagePercent() {
        Runtime rt = Runtime.getRuntime();
        return (double)(rt.totalMemory() - rt.freeMemory()) / rt.maxMemory() * 100;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setShowMax(boolean showMax) {
        this.showMax = showMax;
    }

    public boolean isShowMax() {
        return showMax;
    }
}