package com.lowspec.client.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getAll() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == category) {
                result.add(m);
            }
        }
        return result;
    }

    public Optional<Module> getById(String id) {
        for (Module m : modules) {
            if (m.getId().equals(id)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public void toggle(String id) {
        getById(id).ifPresent(m -> m.setEnabled(!m.isEnabled()));
    }

    public void toggle(Module module) {
        module.setEnabled(!module.isEnabled());
    }

    public void enableAll() {
        for (Module m : modules) {
            m.setEnabled(m.isDefault());
        }
    }

    public List<Module> getEnabled() {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.isEnabled()) {
                result.add(m);
            }
        }
        return result;
    }
}