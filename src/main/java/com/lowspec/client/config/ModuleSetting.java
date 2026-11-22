package com.lowspec.client.config;

import com.google.gson.annotations.SerializedName;

public class ModuleSetting {
    public boolean enabled = true;
    public int x = 6;
    public int y = 6;
    public float scale = 1.0f;
    public int color = 0xFFFFFFFF;
    public float opacity = 1.0f;
    public boolean visible = true;

    public ModuleSetting() {
    }

    public ModuleSetting(boolean enabled, int x, int y, float scale, int color, float opacity) {
        this.enabled = enabled;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.color = color;
        this.opacity = opacity;
    }
}