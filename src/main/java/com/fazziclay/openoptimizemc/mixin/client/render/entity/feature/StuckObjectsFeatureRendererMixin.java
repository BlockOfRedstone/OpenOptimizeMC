package com.fazziclay.openoptimizemc.mixin.client.render.entity.feature;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckObjectsFeatureRenderer.class)
public class StuckObjectsFeatureRendererMixin {
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();


    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", cancellable = true)
    private <T extends LivingEntity> void profiler_push$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (!behaviorManager.getBehavior().renderPlayersStuckObjects(livingEntity)) {
            ci.cancel();
        }
    }
}
