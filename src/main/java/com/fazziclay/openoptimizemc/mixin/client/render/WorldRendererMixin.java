package com.fazziclay.openoptimizemc.mixin.client.render;

import com.fazziclay.openoptimizemc.OP;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();


    @Inject(at = @At("HEAD"), method = "renderEntity")
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (info.isCancelled()) return;
        OP.push(EntityType.getId(entity.getType()).toString());
    }

    @Inject(at = @At("RETURN"), method = "renderEntity")
    private void renderEntity_profilerPop(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (info.isCancelled()) return;
        OP.pop();
    }

    @Inject(at = @At("HEAD"), method = "updateChunks", cancellable = true)
    private void updateChunks(Camera camera, CallbackInfo info) {
        if (!behaviorManager.getBehavior().updateChunks()) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "renderLayer", cancellable = true)
    private void renderLayer(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo info) {
        if (!behaviorManager.getBehavior().renderWorld()) {
            info.cancel();
            return;
        }
        if (info.isCancelled()) return;
        OP.push("renderLayer " + renderLayer.toString());
    }

    @Inject(at = @At("RETURN"), method = "renderLayer")
    private void renderLayer_tail(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo info) {
        if (info.isCancelled()) return;
        OP.pop();
    }
}
