package com.filkond.pigtagger;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Kit {
    UHC("uhc", "textures/kits/uhc/"),
    SMP("smp", "textures/kits/smp/"),
    SWORD("sword", "textures/kits/sword/"),
    VANILLA("vanilla", "textures/kits/vanilla/"),
    NETHPOT("netherite", "textures/kits/nethpot/"),
    ;

    private final String id;
    private final String iconPath;
    private final Map<String, Tier> users = new HashMap<>();
    private final Map<Tier, ResourceLocation> icons = new HashMap<>();

    Kit(String id, String iconPath) {
        this.id = id;
        this.iconPath = iconPath;
        for (Tier tier : Tier.values()) {
            String tierIconLocation = iconPath + tier.name().toLowerCase() + ".png";
            icons.put(tier, ResourceLocation.tryBuild(PigTagger.MOD_ID, tierIconLocation));
        }
    }

    public String getId() {
        return id;
    }

    public String getIconPath() {
        return iconPath;
    }

    public Map<String, Tier> getUsers() {
        return users;
    }

    public @Nullable Tier getTierByNickname(@NotNull String nickname) {
        return users.get(nickname);
    }

    public @Nullable ResourceLocation getIconForTier(@NotNull Tier tier) {
        return icons.get(tier);
    }
}