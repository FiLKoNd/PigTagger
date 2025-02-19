package com.filkond.pigtagger.mixin;

import com.filkond.pigtagger.Kit;
import com.filkond.pigtagger.PigTagger;
import com.filkond.pigtagger.Tier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
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

import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void render(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        renderBadges(abstractClientPlayer, poseStack, i);
    }

    @Inject(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void renderNameTag(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        if (true) {
            return;
        }
        renderBadges(player, poseStack, light);
    }

    @Unique
    private void renderBadges(AbstractClientPlayer player, PoseStack poseStack, int light) {
        Map<Kit, Tier> tiers = PigTagger.getTiersByNickname(player.getName().getString());
        if (tiers.isEmpty()) return;

        float scale = 0.007F;

        poseStack.pushPose();

        poseStack.translate(0, player.getBbHeight() + 0.8f, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(scale, scale, scale);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexConsumer = tesselator.getBuilder();

        vertexConsumer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableDepthTest();

        Matrix4f model = poseStack.last().pose();

        float offset = 4F;
        float maxX = 128F * tiers.size() + offset * (tiers.size() - 1);

        float x = -maxX / 2;
        for (Map.Entry<Kit, Tier> entry : tiers.entrySet()) {
            var kit = entry.getKey();
            var tier = entry.getValue();

            renderIcon(
                    -x,
                    kit.getIconForTier(tier),
                    vertexConsumer,
                    model,
                    light
            );
            x += 128F;
            x += offset;
        }

        tesselator.end();
        poseStack.popPose();
    }

    @Unique
    private void renderIcon(float x, ResourceLocation icon, BufferBuilder vertexConsumer, Matrix4f model, int light) {
        float minU = 0F;
        float maxU = 1F;
        float minV = 0F;
        float maxV = 1F;

        float sizeX = 128;
        float sizeY = 128;
        RenderSystem.setShaderTexture(0, icon);

        vertexConsumer.vertex(model, x, 0F - sizeY, 0.0F).uv(minU, maxV).endVertex();
        vertexConsumer.vertex(model, x - sizeX, 0F - sizeY, 0.0F).uv(maxU, maxV).endVertex();
        vertexConsumer.vertex(model, x - sizeX, 0F, 0.0F).uv(maxU, minV).endVertex();
        vertexConsumer.vertex(model, x, 0F, 0.0F).uv(minU, minV).endVertex();
    }

    @Unique
    private static void drawBuffer(PoseStack matrixStack, BufferBuilder vertexConsumer) {
        BufferBuilder.RenderedBuffer builtBuffer;
        try {
            builtBuffer = vertexConsumer.endOrDiscardIfEmpty();
            if (builtBuffer != null) {
                BufferUploader.drawWithShader(builtBuffer);
                builtBuffer.release();
            }
        } catch (Exception ignored) {
        }
    }
}
