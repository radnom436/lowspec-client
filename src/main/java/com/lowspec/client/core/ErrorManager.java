package com.lowspec.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorManager {
    private static final Logger LOGGER = Logger.getLogger("LowSpec");
    private final List<ErrorEvent> errors = new ArrayList<>();

    private ErrorManager() {
    }

    public static ErrorManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void logError(String moduleId, String message, Throwable error) {
        ErrorEvent event = new ErrorEvent(moduleId, message, error);
        errors.add(event);
        LOGGER.log(Level.WARNING, "[" + moduleId + "] " + message, error);
    }

    public void logMixinError(String mixinId, String target, Throwable error) {
        logError("mixin:" + mixinId, "Failed to inject into " + target, error);
    }

    public List<ErrorEvent> getErrors() {
        return new ArrayList<>(errors);
    }

    public void clearErrors() {
        errors.clear();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static class ErrorEvent {
        public final String moduleId;
        public final String message;
        public final String stackTrace;
        public final long timestamp;

        public ErrorEvent(String moduleId, String message, Throwable error) {
            this.moduleId = moduleId;
            this.message = message;
            this.stackTrace = error != null ? error.toString() : "Unknown error";
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static class InstanceHolder {
        private static final ErrorManager INSTANCE = new ErrorManager();
    }
}