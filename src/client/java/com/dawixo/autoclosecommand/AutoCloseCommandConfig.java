package com.dawixo.autoclosecommand;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Persistent client settings for the independent AutoSell feature. */
final class AutoCloseCommandConfig {
    private static final Path CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("autoclosecommand.properties");

    private static final boolean DEFAULT_AUTOSELL_ENABLED = false;
    private static final int DEFAULT_INTERVAL_SECONDS = 60;
    private static final int MIN_INTERVAL_SECONDS = 1;
    private static final int MAX_INTERVAL_SECONDS = 86_400;

    private static boolean autoSellEnabled = DEFAULT_AUTOSELL_ENABLED;
    private static int autoSellIntervalSeconds = DEFAULT_INTERVAL_SECONDS;

    private AutoCloseCommandConfig() {
    }

    static void load() {
        Properties properties = new Properties();
        if (Files.isRegularFile(CONFIG_FILE)) {
            try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
                properties.load(input);
            } catch (IOException exception) {
                System.err.println("[AutoCloseCommand] Nie udało się odczytać konfiguracji: " + exception.getMessage());
            }
        }

        autoSellEnabled = Boolean.parseBoolean(properties.getProperty(
                "autoSellEnabled", Boolean.toString(DEFAULT_AUTOSELL_ENABLED)));

        try {
            int configuredInterval = Integer.parseInt(properties.getProperty(
                    "autoSellIntervalSeconds", Integer.toString(DEFAULT_INTERVAL_SECONDS)).trim());
            if (configuredInterval >= MIN_INTERVAL_SECONDS && configuredInterval <= MAX_INTERVAL_SECONDS) {
                autoSellIntervalSeconds = configuredInterval;
            } else {
                autoSellIntervalSeconds = DEFAULT_INTERVAL_SECONDS;
            }
        } catch (NumberFormatException exception) {
            autoSellIntervalSeconds = DEFAULT_INTERVAL_SECONDS;
        }

        // Create the file on first run and normalize invalid/missing values.
        save();
    }

    static boolean isAutoSellEnabled() {
        return autoSellEnabled;
    }

    static int getAutoSellIntervalSeconds() {
        return autoSellIntervalSeconds;
    }

    static void setAutoSellEnabled(boolean enabled) {
        autoSellEnabled = enabled;
        save();
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("autoSellEnabled", Boolean.toString(autoSellEnabled));
        properties.setProperty("autoSellIntervalSeconds", Integer.toString(autoSellIntervalSeconds));
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (OutputStream output = Files.newOutputStream(CONFIG_FILE)) {
                properties.store(output, "AutoCloseCommand client settings");
            }
        } catch (IOException exception) {
            System.err.println("[AutoCloseCommand] Nie udało się zapisać konfiguracji: " + exception.getMessage());
        }
    }
}
