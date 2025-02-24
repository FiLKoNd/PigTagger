package com.filkond.pigtagger;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashSet;
import java.util.Set;

public class PigConfigScreen extends Screen {
    private static final int OFFSET = 4;
    private final Screen parent;
    private EditBox xOffset;
    private EditBox yOffset;

    private EditBox badgeScale;
    private EditBox ignoredTiers;
    private Checkbox box;

    public PigConfigScreen(Screen parent) {
        super(Component.translatable("pigtagger.menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        MutableComponent component = Component.translatable("pigtagger.config.ignoreSelf");
        box = new Checkbox(
                width / 3,
                height / 4,
                9 + 8 + 4 + font.width(component),
                9 + 8,
                component,
                PigConfig.ignoreSelf
        );
//        Checkbox box = Checkbox.builder(component, this.font)
//                .pos(width / 3, height / 4)
//                .selected(PigConfig.ignoreSelf)
//                .onValueChange((cb, value) -> {
//                    PigConfig.ignoreSelf = value;
//
//                })
//                .tooltip(Tooltip.create(Component.translatable("pigtagger.config.ignoreSelf.tooltip")))
//                .build();
        box.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.ignoreSelf.tooltip")));
        box.setTooltipDelay(250);
        this.addRenderableWidget(box);

        this.badgeScale = new EditBox(this.font, width / 2 + OFFSET, height / 4, width / 6 - OFFSET, 20, this.badgeScale, Component.translatable("pigtagger.config.badge.scale"));
        this.badgeScale.setHint(this.badgeScale.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.badgeScale.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.badge.scale.tooltip")));
        this.badgeScale.setTooltipDelay(250);
        this.badgeScale.setMaxLength(128);
        this.badgeScale.setResponder(value -> {
            try {
                PigConfig.badgeScale = Float.parseFloat(value);
                this.badgeScale.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.badgeScale = PigConfig.DEFAULT_BADGE_SCALE;
                this.badgeScale.setTextColor(0xFF_FF_00_00);
            }
        });
        this.badgeScale.setValue(PigConfig.badgeScale == PigConfig.DEFAULT_BADGE_SCALE ? "" : String.valueOf(PigConfig.badgeScale));
        this.addRenderableWidget(this.badgeScale);

        this.xOffset = new EditBox(this.font, width / 3, height / 4 + 20 + OFFSET, width / 6, 20, this.xOffset, Component.translatable("pigtagger.config.xOffset"));
        this.xOffset.setHint(this.xOffset.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.xOffset.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.xOffset.tooltip")));
        this.xOffset.setTooltipDelay(250);
        this.xOffset.setMaxLength(128);
        this.xOffset.setResponder(value -> {
            try {
                PigConfig.xOffset = Float.parseFloat(value);
                this.xOffset.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.xOffset = PigConfig.DEFAULT_X_OFFSET;
                this.xOffset.setTextColor(0xFF_FF_00_00);
            }
        });
        this.xOffset.setValue(PigConfig.xOffset == PigConfig.DEFAULT_X_OFFSET ? "" : String.valueOf(PigConfig.yOffset));
        this.addRenderableWidget(this.xOffset);


        this.yOffset = new EditBox(this.font, width / 2 + OFFSET, height / 4 + 20 + OFFSET, width / 6 - OFFSET, 20, this.yOffset, Component.translatable("pigtagger.config.yOffset"));
        this.yOffset.setHint(this.yOffset.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.yOffset.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.yOffset.tooltip")));
        this.yOffset.setTooltipDelay(250);
        this.yOffset.setMaxLength(128);
        this.yOffset.setResponder(value -> {
            try {
                PigConfig.yOffset = Float.parseFloat(value);
                this.yOffset.setTextColor(0xFF_E0_E0_E0);
            } catch (NumberFormatException e) {
                PigConfig.yOffset = PigConfig.DEFAULT_Y_OFFSET;
                this.yOffset.setTextColor(0xFF_FF_00_00);
            }
        });
        this.yOffset.setValue(PigConfig.yOffset == PigConfig.DEFAULT_Y_OFFSET ? "" : String.valueOf(PigConfig.yOffset));
        this.addRenderableWidget(this.yOffset);

        this.ignoredTiers = new EditBox(this.font, width / 3, height / 4 + (20 + OFFSET) * 2, width / 3, 20, this.ignoredTiers, Component.translatable("pigtagger.config.ignoredKits"));
        this.ignoredTiers.setHint(this.ignoredTiers.getMessage().copy().withStyle(ChatFormatting.DARK_GRAY));
        this.ignoredTiers.setTooltip(Tooltip.create(Component.translatable("pigtagger.config.ignoredKits.tooltip")));
        this.ignoredTiers.setTooltipDelay(250);
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
    public void render(PoseStack poseStack, int i, int j, float f) {
        renderBackground(poseStack);
        super.render(poseStack, i, j, f);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
        PigConfig.ignoreSelf = box.selected();

        PigTagger.saveConfig();
    }
}
