package com.filkond.pigtagger.mixin;

import com.filkond.pigtagger.Kit;
import com.filkond.pigtagger.PigConfig;
import com.filkond.pigtagger.PigTagger;
import com.filkond.pigtagger.Tier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.filkond.pigtagger.PigTagger.BADGE_HEIGHT;
import static com.filkond.pigtagger.PigTagger.BADGE_WIDTH;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Unique
    private static boolean shouldRenderForPlayer(AbstractClientPlayer player) {
        return (!PigConfig.ignoreSelf || !player.isLocalPlayer()) && !player.isInvisibleTo(Minecraft.getInstance().player);
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void render(AbstractClientPlayer player, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        if (!PigConfig.ignoreSelf) {
            renderBadges(player, poseStack, light);
        }
    }

    @Inject(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void renderNameTag(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        if (shouldRenderForPlayer(player)) {
            renderBadges(player, poseStack, light);
        }
    }

    @Unique
    private void renderBadges(AbstractClientPlayer player, PoseStack poseStack, int light) {
        Map<Kit, Tier> tiers = PigTagger.getEnabledTiersByNickname(player.getName().getString());
        if (tiers.isEmpty()) return;

        float scale = PigConfig.badgeScale;

        poseStack.pushPose();

        poseStack.translate(0, player.getBbHeight() + PigConfig.yOffset, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(scale, scale, scale);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexConsumer = tesselator.getBuilder();

        float offset = PigConfig.xOffset;
        float maxX = BADGE_WIDTH * tiers.size() + offset * (tiers.size() - 1);

        float x = -maxX / 2;
        for (Map.Entry<Kit, Tier> entry : tiers.entrySet()) {
            var kit = entry.getKey();
            var tier = entry.getValue();

            vertexConsumer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableDepthTest();

            Matrix4f model = poseStack.last().pose();
            renderIcon(
                    -x,
                    kit.getIconForTier(tier),
                    vertexConsumer,
                    model,
                    light
            );
            tesselator.end();
            x += BADGE_WIDTH + offset;
        }

        poseStack.popPose();
    }

    @Unique
    private void renderIcon(float x, ResourceLocation icon, BufferBuilder vertexConsumer, Matrix4f model, int light) {
        float minU = 0F;
        float maxU = 1F;
        float minV = 0F;
        float maxV = 1F;

        RenderSystem.setShaderTexture(0, icon);

        drawVertex(model, vertexConsumer, x, 0F - BADGE_HEIGHT, minU, maxV, light);
        drawVertex(model, vertexConsumer, x - BADGE_WIDTH, 0F - BADGE_HEIGHT, maxU, maxV, light);
        drawVertex(model, vertexConsumer, x - BADGE_WIDTH, 0, maxU, minV, light);
        drawVertex(model, vertexConsumer, x, 0, minU, minV, light);
    }

    @Unique
    private void drawVertex(Matrix4f model, BufferBuilder vertexConsumer, float x, float y, float u, float v, int light) {
        vertexConsumer
                .vertex(model, x, y, 0F)
                .uv(u, v)
                .endVertex();
    }
}
