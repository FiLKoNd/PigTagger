package com.filkond.pigtagger.util;

import com.filkond.pigtagger.Kit;
import com.filkond.pigtagger.PigConfig;
import com.filkond.pigtagger.PigTagger;
import com.filkond.pigtagger.Tier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import java.util.Map;

import static com.filkond.pigtagger.PigTagger.BADGE_HEIGHT;
import static com.filkond.pigtagger.PigTagger.BADGE_WIDTH;

public class RenderUtil {
    public static void renderBadges(Player player, EntityRenderDispatcher dispatcher, PoseStack poseStack, int light) {
        if (!shouldRenderForPlayer(player)) {
            return;
        }

        Map<Kit, Tier> tiers = PigTagger.getEnabledTiersByNickname(player.getName().getString());
        if (tiers.isEmpty()) return;

        float scale = PigConfig.badgeScale;

        poseStack.pushPose();

        poseStack.translate(0, player.getBbHeight() + PigConfig.yOffset, 0);
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(-1, 1, 1);
        poseStack.scale(scale, scale, scale);

        Tesselator tesselator = Tesselator.getInstance();

        float offset = PigConfig.xOffset;
        float maxX = BADGE_WIDTH * tiers.size() + offset * (tiers.size() - 1);

        float x = -maxX / 2;
        for (Map.Entry<Kit, Tier> entry : tiers.entrySet()) {
            var kit = entry.getKey();
            var tier = entry.getValue();

            BufferBuilder vertexConsumer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            RenderSystem.setShader(CoreShaders.POSITION_TEX);
            RenderSystem.enableDepthTest();

            Matrix4f model = poseStack.last().pose();
            renderIcon(
                    -x,
                    kit.getIconForTier(tier),
                    vertexConsumer,
                    model,
                    light
            );
            BufferUploader.drawWithShader(vertexConsumer.buildOrThrow());
            x += BADGE_WIDTH + offset;
        }

        poseStack.popPose();
    }

    private static void renderIcon(float x, ResourceLocation icon, BufferBuilder vertexConsumer, Matrix4f model, int light) {
        float minU = 0;
        float maxU = 1;
        float minV = 0;
        float maxV = 1;
        RenderSystem.setShaderTexture(0, icon);

        drawVertex(model, vertexConsumer, x, 0F - BADGE_HEIGHT, minU, maxV, light);
        drawVertex(model, vertexConsumer, x - BADGE_WIDTH, 0F - BADGE_HEIGHT, maxU, maxV, light);
        drawVertex(model, vertexConsumer, x - BADGE_WIDTH, 0, maxU, minV, light);
        drawVertex(model, vertexConsumer, x, 0, minU, minV, light);
    }

    private static void drawVertex(Matrix4f model, BufferBuilder vertexConsumer, float x, float y, float u, float v, int light) {
        vertexConsumer
                .addVertex(model, x, y, 0F)
                .setUv(u, v);
    }


    private static boolean shouldRenderForPlayer(Entity entity) {
        if (entity instanceof Player abstractClientPlayerEntity) {
            return (!PigConfig.ignoreSelf || !abstractClientPlayerEntity.isLocalPlayer()) && !abstractClientPlayerEntity.isInvisibleTo(Minecraft.getInstance().player);
        }

        return false;
    }
}
