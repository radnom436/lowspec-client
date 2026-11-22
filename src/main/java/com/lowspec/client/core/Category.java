package com.lowspec.client.core;

public enum Category {
    HUD("HUD", "Heads-up display widgets"),
    GAMEPLAY("Gameplay", "Quality of life and gameplay enhancements"),
    VISUAL("Visual", "Visual effects and appearance"),
    PERFORMANCE("Performance", "Frame rate and optimization settings"),
    SERVER_SAFETY("Server Safety", "Compliance and safety features"),
    COMPAT("Compatibility", "Third-party mod integration");

    private final String id;
    private final String label;
    private final String description;

    Category(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
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
}