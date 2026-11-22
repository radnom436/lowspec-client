package com.lowspec.client.hud.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class Widget {
    protected String id;
    protected Anchor anchor = Anchor.TOP_LEFT;
    protected Point offset = new Point(6, 6);
    protected float scale = 1.0f;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected int color = 0xFFFFFFFF;
    protected float opacity = 1.0f;

    public Widget(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(int x, int y) {
        this.offset.set(x, y);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Math.max(0.1f, Math.min(3.0f, scale));
    }

    public boolean isVisible() {
        return visible && enabled;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    public abstract int getRenderWidth(Font font);
    public abstract int getRenderHeight(Font font);
    public abstract void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta);

    protected int getAbsoluteX(int screenWidth) {
        int width = getRenderWidth(null);
        return (int) (screenWidth * anchor.getXAnchor() + offset.x - (anchor == Anchor.TOP_LEFT || anchor == Anchor.MIDDLE_LEFT || anchor == Anchor.BOTTOM_LEFT ? 0 : width * (anchor.getXAnchor() > 0 ? 1 : 0)));
    }

    protected int getAbsoluteY(int screenHeight, Font font) {
        int height = getRenderHeight(font);
        return (int) (screenHeight * anchor.getYAnchor() + offset.y - (anchor == Anchor.TOP_LEFT || anchor == Anchor.TOP_CENTER || anchor == Anchor.TOP_RIGHT ? 0 : height * (anchor.getYAnchor() > 0 ? 1 : 0)));
    }
}