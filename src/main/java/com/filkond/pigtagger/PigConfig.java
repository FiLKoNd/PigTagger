package com.filkond.pigtagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

/**
 * Определенно не спизженно
 */
public class PigConfig {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.FINAL)
            .create();

    /**
     * Distance between badges
     */
    public static float xOffset = 0.5F;

    /**
     * Distance to badges above the player's head
     */
    public static float yOffset = 4F;

    /**
     * Badge's scale
     */
    public static float badgeScale = 0.01F;

    /**
     * If {@code true} your own tier badges should not be rendering
     */
    public static boolean ignoreSelf = true;

    /**
     * Kits whose badges should not be rendering
     */
    public static Set<Kit> ignoredKits = Set.of(Kit.SWORD);

    public static void load(@NotNull Path path) {
        try {
            Path file = path.resolve("pigtagger.json");

            if (!Files.isRegularFile(file)) {
                save(path);
                return;
            }

            String value = Files.readString(file);
            JsonObject json = GSON.fromJson(value, JsonObject.class);

            GSON.fromJson(json, PigConfig.class);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load PigTagger config.", t);
        }
    }

    public static void save(@NotNull Path path) {
        try {
            Path file = path.resolve("pigtagger.json");

            @SuppressWarnings("InstantiationOfUtilityClass")
            JsonObject json = (JsonObject) GSON.toJsonTree(new PigConfig());

            String value = GSON.toJson(json);

            Files.createDirectories(file.getParent());

            Files.writeString(file, value, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    StandardOpenOption.SYNC, StandardOpenOption.DSYNC);

        } catch (Throwable t) {
            throw new RuntimeException("Unable to save PigTagger config.", t);
        }
    }
}
