package com.lowspec.client.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.RenderLayers;

public abstract class Module {
    protected final String id;
    protected final String label;
    protected final String description;
    protected final Category category;
    protected final ComplianceTag compliance;
    protected boolean enabled;
    protected boolean defaultConfig;

    protected Module(String id, String label, String description, Category category, ComplianceTag compliance, boolean defaultConfig) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.category = category;
        this.compliance = compliance;
        this.defaultConfig = defaultConfig;
        this.enabled = defaultConfig;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public ComplianceTag getCompliance() {
        return compliance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public boolean isDefault() {
        return defaultConfig;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onRender(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
    }

    public void onTick() {
    }

    @Override
    public String toString() {
        return label + " [" + (enabled ? "on" : "off") + "]";
    }
}