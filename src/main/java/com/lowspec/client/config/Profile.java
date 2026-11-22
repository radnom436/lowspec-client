package com.lowspec.client.config;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    public String name;
    public String description;
    public boolean isDefault = false;
    public Map<String, ModuleSetting> modules = new HashMap<>();
    public String theme = "vanilla";
    public int version = 1;

    public Profile() {
    }

    public Profile(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ModuleSetting getModule(String moduleId) {
        return modules.computeIfAbsent(moduleId, id -> new ModuleSetting());
    }

    public void setModule(String moduleId, ModuleSetting setting) {
        modules.put(moduleId, setting);
    }
}