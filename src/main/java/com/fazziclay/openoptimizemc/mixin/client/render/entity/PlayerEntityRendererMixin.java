package com.fazziclay.openoptimizemc.mixin.client.render.entity;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.experemental.ExperimentalRenderer;
import com.fazziclay.openoptimizemc.util.MathConst;
import com.fazziclay.openoptimizemc.util.OP;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<M> extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final String OP_OPENOPTIMIZEMC_MIXIN = "OpenOptimizeMC mixin";
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();
    private static final ExperimentalRenderer experimentalRenderer = ExperimentalRenderer.INSTANCE;


    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", cancellable = true)
    private void render$mixin(AbstractClientPlayerEntity abstractClientPlayerEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!behaviorManager.getBehavior().renderPlayers(abstractClientPlayerEntity)) {
            ci.cancel();
            return;
        }
        OP.push(OP_OPENOPTIMIZEMC_MIXIN);
        if (behaviorManager.getBehavior().dirtRenderer(abstractClientPlayerEntity)) {
            renderDirtRenderer(abstractClientPlayerEntity, matrixStack, vertexConsumerProvider, light);
            OP.pop();
            ci.cancel();
            return;
        }
        if (behaviorManager.getBehavior().cubePrimitivePlayers(abstractClientPlayerEntity)) {
            renderCubePrimitivePlayer(abstractClientPlayerEntity, matrixStack, vertexConsumerProvider, light);
            OP.pop();
            ci.cancel();
            return;
        }
        if (behaviorManager.getBehavior().onlyHeadPlayers(abstractClientPlayerEntity)) {
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = getModel();
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        }
        OP.pop();
    }

    @Inject(at = @At("HEAD"), method = "setModelPose", cancellable = true)
    private void setModelPose$mixin(AbstractClientPlayerEntity player, CallbackInfo ci) {
        if (behaviorManager.getBehavior().onlyHeadPlayers(player)) {
            ci.cancel();
            return;
        }
    }

    private void renderDirtRenderer(AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        experimentalRenderer.render(player, matrices);
    }

    public Random RANDOM = new Random();
    private void renderCubePrimitivePlayer(AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        boolean easterEgg = "FazziCLAY".equals(player.getEntityName());
        RANDOM.setSeed(player.hashCode());
        float f = 0.75f;
        double x1 = f * -0.5;
        double y1 = f * -0.5;
        double z1 = f * -0.5;
        double x2 = f * 0.5;
        double y2 = f * 0.5;
        double z2 = f * 0.5 + (easterEgg ? 0 : (RANDOM.nextFloat() / 2f));
        float r = easterEgg ? 0.0f : RANDOM.nextFloat();
        float g = easterEgg ? 1.0f : RANDOM.nextFloat();
        float b = easterEgg ? 0.0f : RANDOM.nextFloat();
        float a = 1f;
        matrices.push();
        MatrixStack matrix = RenderSystem.getModelViewStack();
        matrix.push();
        Matrix4f mmm = matrices.peek().getPositionMatrix();
        mmm.translate(0, 1, 0);
        mmm.rotateY(-player.getYaw() * MathConst.F_PI_D180);
        mmm.rotateX(player.getPitch() * MathConst.F_PI_D180);
        matrix.multiplyPositionMatrix(mmm);
        RenderSystem.applyModelViewMatrix();


        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR_LIGHT);

        drawBox(bufferBuilder, x1, y1, z1, x2, y2, z2, r, g, b, a, light);
        tessellator.draw();
        RenderSystem.disableDepthTest();


        matrix.pop();
        matrices.pop();
        RenderSystem.applyModelViewMatrix();
    }

    private void drawBox(VertexConsumer buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, int light) {
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).light(light).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).light(light).next();
    }
}
