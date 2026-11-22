package com.lowspec.client.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final int VERSION = 1;
    public int version = VERSION;
    public String profile = "default";
    public List<String> profiles = new ArrayList<>();
    public Profile defaultProfile = new Profile("default", "Default configuration");
    public Profile pvpProfile = new Profile("pvp", "PvP optimized settings");
    public Profile survivalProfile = new Profile("survival", "Survival optimized settings");
    public Profile streamingProfile = new Profile("streaming", "Screen streaming optimized");

    public Config() {
        profiles.add("default");
        profiles.add("pvp");
        profiles.add("survival");
        profiles.add("streaming");
    }
}