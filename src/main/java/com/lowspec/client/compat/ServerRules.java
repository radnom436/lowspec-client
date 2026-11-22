package com.lowspec.client.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServerRules {
    private final Map<String, Profile> serverProfiles = new HashMap<>();

    public ServerRules() {
        // Default profiles for popular servers
        // In production, this would load from a data file
    }

    public static class Profile {
        public String name;
        public String[] allowedFeatures;
        public String[] bannedFeatures;
        public String reason;

        public Profile() {}

        public Profile(String name, String[] allowed, String[] banned, String reason) {
            this.name = name;
            this.allowedFeatures = allowed;
            this.bannedFeatures = banned;
            this.reason = reason;
        }
    }

    public Profile getProfileForServer(String serverAddress) {
        // Simple matching logic
        if (serverAddress.contains("hypixel") || serverAddress.contains("play.hypixel")) {
            return new Profile("hypixel",
                    new String[]{"fps", "cps", "coords", "keystrokes", "armor", "zoom"},
                    new String[]{"sprint", "freelook", "fullbright"},
                    "Auto-sprint may be restricted on Hypixel");
        }
        if (serverAddress.contains("thehive") || serverAddress.contains("hive")) {
            return new Profile("the_hive",
                    new String[]{"fps", "cps", "coords", "keystrokes", "armor"},
                    new String[]{"freelook", "fullbright"},
                    "Freelook and fullbright are not allowed");
        }
        if (serverAddress.contains("pika") || serverAddress.contains("pikachu")) {
            return new Profile("pika",
                    new String[]{"fps", "cps", "coords", "keystrokes", "armor", "combo"},
                    new String[]{"freelook", "auto-sprint"},
                    "Check server rules for allowed features");
        }

        // Default: standard
        return new Profile("standard",
                new String[]{"fps", "cps", "coords", "keystrokes", "armor", "zoom"},
                new String[]{},
                "Using default safe profile");
    }

    public boolean isFeatureAllowed(String serverAddress, String featureId) {
        Profile profile = getProfileForServer(serverAddress);
        for (String allowed : profile.allowedFeatures) {
            if (allowed.equals(featureId)) return true;
        }
        for (String banned : profile.bannedFeatures) {
            if (banned.equals(featureId)) return false;
        }
        return true; // Default allow
    }
}