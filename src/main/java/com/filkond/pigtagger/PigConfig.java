package com.filkond.pigtagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;

/**
 * Определенно не спизженно
 */
public class PigConfig {
    // cringe
    public static final float DEFAULT_X_OFFSET = 0.5F;
    public static final float DEFAULT_Y_OFFSET = 0.8F;
    public static final float DEFAULT_BADGE_SCALE = 0.015F;
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.FINAL)
            .create();
    /**
     * Distance between badges
     */
    public static float xOffset = DEFAULT_X_OFFSET;

    /**
     * Distance to badges above the player's head
     */
    public static float yOffset = DEFAULT_Y_OFFSET;

    /**
     * Badge's scale
     */
    public static float badgeScale = DEFAULT_BADGE_SCALE;

    /**
     * If {@code true} your own tier badges should not be rendering
     */
    public static boolean ignoreSelf = true;

    /**
     * Kits whose badges should not be rendering
     */
    public static Set<Kit> ignoredKits = Collections.emptySet();

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
