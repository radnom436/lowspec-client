package com.lowspec.client.hud.widget;

public enum Anchor {
    TOP_LEFT(0, 0),
    TOP_CENTER(0.5f, 0),
    TOP_RIGHT(1, 0),
    MIDDLE_LEFT(0, 0.5f),
    MIDDLE(0.5f, 0.5f),
    MIDDLE_RIGHT(1, 0.5f),
    BOTTOM_LEFT(0, 1),
    BOTTOM_CENTER(0.5f, 1),
    BOTTOM_RIGHT(1, 1);

    private final float xAnchor;
    private final float yAnchor;

    Anchor(float xAnchor, float yAnchor) {
        this.xAnchor = xAnchor;
        this.yAnchor = yAnchor;
    }

    public float getXAnchor() {
        return xAnchor;
    }

    public float getYAnchor() {
        return yAnchor;
    }
}