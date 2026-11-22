package com.lowspec.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowspec.client.LowSpecClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger("LowSpec");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("lowspec.json");
    private static final Path BACKUP_PATH = FabricLoader.getInstance().getConfigDir().resolve("lowspec.json.bak");

    private Config config;
    private final Map<String, Profile> profileMap = new HashMap<>();

    public ConfigManager() {
        load();
    }

    public Config get() {
        return config;
    }

    public Profile getCurrentProfile() {
        return profileMap.get(config.profile);
    }

    public void setProfile(String name) {
        if (profileMap.containsKey(name)) {
            config.profile = name;
            save();
        }
    }

    public Map<String, Profile> getProfiles() {
        return profileMap;
    }

    public void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
                return;
            }

            String content = Files.readString(CONFIG_PATH);
            config = GSON.fromJson(content, Config.class);

            if (config.version < Config.VERSION) {
                migrate(config.version);
            }

            buildProfileMap();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load config, restoring from backup", e);
            restoreBackup();
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        config = new Config();
        buildProfileMap();
        save();
    }

    private void buildProfileMap() {
        profileMap.clear();
        profileMap.put(config.defaultProfile.name, config.defaultProfile);
        for (Profile p : new Profile[]{config.pvpProfile, config.survivalProfile, config.streamingProfile}) {
            if (p != null) {
                profileMap.put(p.name, p);
            }
        }
    }

    public void save() {
        try {
            Path backup = CONFIG_PATH;
            if (Files.exists(backup)) {
                Files.copy(backup, BACKUP_PATH);
            }

            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save config", e);
        }
    }

    private void restoreBackup() {
        try {
            if (Files.exists(BACKUP_PATH)) {
                Files.copy(BACKUP_PATH, CONFIG_PATH);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to restore config backup", e);
        }
    }

    private void migrate(int fromVersion) {
        LOGGER.info("Migrating config from version " + fromVersion + " to " + Config.VERSION);
        // Migration logic for future versions
        if (fromVersion < 1) {
            // First version, no migration needed
        }
        config.version = Config.VERSION;
    }

    public String exportProfile() {
        Profile current = getCurrentProfile();
        return GSON.toJson(current);
    }

    public void importProfile(String json) {
        try {
            Profile imported = GSON.fromJson(json, Profile.class);
            if (imported != null && imported.name != null) {
                profileMap.put(imported.name, imported);
                config.profiles.add(imported.name);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to import profile", e);
        }
    }
}