package com.lowspec.client.modules.gameplay;

import com.lowspec.client.core.Module;
import com.lowspec.client.core.Category;
import com.lowspec.client.core.ComplianceTag;

public class CrosshairModule extends Module {
    public enum Style {
        DEFAULT("Default"),
        DOT("Dot"),
        CROSS("Cross"),
        T_SHAPED("T-Shaped"),
        CIRCLE("Circle"),
        DYNAMIC("Dynamic (reacts to attack cooldown)");

        private final String label;

        Style(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private Style style = Style.DEFAULT;
    private int size = 8;
    private int gap = 2;
    private int color = 0xFFFFFFFF;
    private float opacity = 1.0f;

    public CrosshairModule() {
        super("crosshair", "Custom Crosshair", "Customizable crosshair with presets", Category.VISUAL, ComplianceTag.ALWAYS_SAFE, false);
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(50, size));
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = Math.max(0, Math.min(20, gap));
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
        this.opacity = Math.max(0f, Math.min(1f, opacity));
    }
}