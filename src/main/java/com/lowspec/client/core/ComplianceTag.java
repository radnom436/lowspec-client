package com.lowspec.client.core;

public enum ComplianceTag {
    ALWAYS_SAFE("Always safe", "Feature is always allowed on all servers"),
    USUALLY_ALLOWED("Usually allowed", "Allowed on most servers, check server rules"),
    SERVER_RESTRICTED("Server-restricted", "Only safe with explicit permission"),
    NEVER_SHIP("Never ship", "Feature disabled by default, use at own risk");

    private final String label;
    private final String description;

    ComplianceTag(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}