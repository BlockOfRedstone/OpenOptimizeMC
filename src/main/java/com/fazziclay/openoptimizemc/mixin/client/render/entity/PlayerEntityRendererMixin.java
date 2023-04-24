package com.fazziclay.openoptimizemc.mixin.client.render.entity;

import com.fazziclay.openoptimizemc.MathConst;
import com.fazziclay.openoptimizemc.OP;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<M> extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    @Shadow protected abstract void setModelPose(AbstractClientPlayerEntity player);

    private static final String OP_OPENOPTIMIZEMC_MIXIN = "OpenOptimizeMC mixin";
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();


    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    /**
     * @author FazziCLAY ( <a href="https://fazziclay.github.io">My site</a> )
     * @reason Add ONLY_HEADS, IS_RENDER_PlAYERS, IS_PLAYER_MODEL_POSE
     */
    @Overwrite
    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (!behaviorManager.getBehavior().renderPlayers()) {
            return;
        }
        OP.push(OP_OPENOPTIMIZEMC_MIXIN);
        if (behaviorManager.getBehavior().cubePrimitivePlayers(abstractClientPlayerEntity)) {
            renderCubePrimitivePlayer(abstractClientPlayerEntity, matrixStack, vertexConsumerProvider, light);
            OP.pop();
            return;
        }
        setModelPose(abstractClientPlayerEntity);
        if (behaviorManager.getBehavior().onlyHeadPlayers(abstractClientPlayerEntity)) {
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = getModel();
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        }
        OP.pop();
        super.render(abstractClientPlayerEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
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
