package com.filkond.pigtagger;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class PigConfigScreen extends Screen {
    private final Screen parent;
    private static final int OFFSET = 4;

    private EditBox xOffset;
    private EditBox yOffset;

    private EditBox badgeScale;
    private EditBox ignoredTiers;

    public PigConfigScreen(Screen parent) {
        super(Component.translatable("pigtagger.menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Checkbox box = Checkbox.builder(Component.translatable("pigtagger.config.ignoreSelf"), this.font)
                .pos(width / 3, height / 4)
                .selected(PigConfig.ignoreSelf)
                .onValueChange((cb, value) -> {
                    PigConfig.ignoreSelf = value;

                })
                .tooltip(Tooltip.create(Component.translatable("pigtagger.config.ignoreSelf.tooltip")))
                .build();
        box.setTooltipDelay(Duration.ofMillis(250L));
        this.addRenderableWidget(box);

        this.badgeScale = new EditBox(this.font, width / 2 + OFFSET, height / 4, width / 6 - OFFSET, 20, this.badgeScale, Component.translatable("pigtagger.config.badge.scale"));
        this.badgeScale.setHint(this.badgeScale.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.badgeScale.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.badge.scale.tooltip")));
        this.badgeScale.setTooltipDelay(Duration.ofMillis(250L));
        this.badgeScale.setMaxLength(128);
        this.badgeScale.setResponder(value -> {
            try {
                PigConfig.badgeScale = Float.parseFloat(value);
                this.badgeScale.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.badgeScale = 0.01F;
                this.badgeScale.setTextColor(0xFF_FF_00_00);
            }
        });
        this.badgeScale.setValue(PigConfig.badgeScale == 0.01F ? "" : String.valueOf(PigConfig.badgeScale));
        this.addRenderableWidget(this.badgeScale);

        this.xOffset = new EditBox(this.font, width / 3, height / 4 + 20 + OFFSET, width / 6, 20, this.xOffset, Component.translatable("pigtagger.config.xOffset"));
        this.xOffset.setHint(this.xOffset.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.xOffset.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.xOffset.tooltip")));
        this.xOffset.setTooltipDelay(Duration.ofMillis(250L));
        this.xOffset.setMaxLength(128);
        this.xOffset.setResponder(value -> {
            try {
                PigConfig.xOffset = Float.parseFloat(value);
                this.xOffset.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.xOffset = 0.5F;
                this.xOffset.setTextColor(0xFF_FF_00_00);
            }
        });
        this.xOffset.setValue(PigConfig.xOffset == 0.5F ? "" : String.valueOf(PigConfig.yOffset));
        this.addRenderableWidget(this.xOffset);


        this.yOffset = new EditBox(this.font, width / 2 + OFFSET, height / 4 + 20 + OFFSET, width / 6 - OFFSET, 20, this.yOffset, Component.translatable("pigtagger.config.yOffset"));
        this.yOffset.setHint(this.yOffset.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.yOffset.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.yOffset.tooltip")));
        this.yOffset.setTooltipDelay(Duration.ofMillis(250L));
        this.yOffset.setMaxLength(128);
        this.yOffset.setResponder(value -> {
            try {
                PigConfig.yOffset = Float.parseFloat(value);
                this.yOffset.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.yOffset = 0.4F;
                this.yOffset.setTextColor(0xFF_FF_00_00);
            }
        });
        this.yOffset.setValue(PigConfig.yOffset == 0.4F ? "" : String.valueOf(PigConfig.yOffset));
        this.addRenderableWidget(this.yOffset);

        this.ignoredTiers = new EditBox(this.font, width / 3, height / 4 + (20 + OFFSET) * 2, width / 3, 20, this.yOffset, Component.translatable("pigtagger.config.ignoredKits"));
        this.ignoredTiers.setHint(this.ignoredTiers.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.ignoredTiers.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.ignoredKits.tooltip")));
        this.ignoredTiers.setTooltipDelay(Duration.ofMillis(250L));
        this.ignoredTiers.setMaxLength(128);
        this.ignoredTiers.setResponder(value -> {
            Set<Kit> out = new HashSet<>();
            for (String s : value.split(",")) {
                try {
                    Kit kit = Kit.valueOf(s);
                    out.add(kit);
                } catch (IllegalArgumentException ignored) {
                }
            }
            PigConfig.ignoredKits = out;
        });
        this.ignoredTiers.setValue(
                String.join(",", PigConfig.ignoredKits.stream()
                        .map(Enum::name)
                        .toList()
                )
        );
        this.addRenderableWidget(this.ignoredTiers);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);

        PigTagger.saveConfig();
    }
}
