package com.filkond.pigtagger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PigTagger {
    public static final String MOD_ID = "pigtagger";
    public static final int BADGE_WIDTH = 35;
    public static final int BADGE_HEIGHT = 17;
    private static final Duration TIMEOUT = Duration.ofSeconds(Long.getLong("pigtagger.timeout", 15L));

    private static Path configFolder;

    public static void init(Path configFolder) {
        PigTagger.configFolder = configFolder;

        PigConfig.load(configFolder);
        PigTagger.updateTiers();
    }

    public static void updateTiers() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .executor(Runnable::run)
                    .build();
            for (Kit kit : Kit.values()) {
                kit.getUsers().clear();
                HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                        .uri(new URI("https://api.cistiers.ru/v1/get-table/" + kit.getId()))
                        .timeout(TIMEOUT)
                        .GET()
                        .build(), HttpResponse.BodyHandlers.ofString());

                JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    Tier tier = Tier.valueOf(entry.getKey().toUpperCase());
                    List<String> nicknames = entry.getValue().getAsJsonArray().asList().stream()
                            .map(JsonElement::getAsString)
                            .toList();
                    for (String nickname : nicknames) {
                        kit.getUsers().put(nickname, tier);
                    }
                }
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all the tiers that player have
     *
     * @param nickname Player's nickname
     * @return {@link Kit} collection that mapped to {@link Tier}
     */
    public static @NotNull Map<Kit, Tier> getTiersByNickname(@NotNull String nickname) {
        Map<Kit, Tier> out = new HashMap<>();
        for (Kit kit : Kit.values()) {
            Tier tier = kit.getTierByNickname(nickname);
            if (tier != null) {
                out.put(kit, tier);
            }
        }
        return out;
    }

    /**
     * Get all the tiers that player have, except for {@link PigConfig#ignoredKits}
     *
     * @param nickname Player's nickname
     * @return {@link Kit} collection that mapped to {@link Tier}
     */
    public static @NotNull Map<Kit, Tier> getEnabledTiersByNickname(@NotNull String nickname) {
        Map<Kit, Tier> out = getTiersByNickname(nickname);
        PigConfig.ignoredKits.forEach(out::remove);
        return out;
    }

    public static void saveConfig() {
        PigConfig.save(configFolder);
    }
}