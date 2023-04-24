package com.fazziclay.openoptimizemc.mixin.client.render.entity.model;

import com.fazziclay.openoptimizemc.OP;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalModel.class)
public class AnimalModelMixin {
    private static final String PROFILER_RENDER = "AnimalModel:render";


    @Inject(at = @At("HEAD"), method = "render")
    private void profiler_push$render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        OP.push(PROFILER_RENDER);
    }

    @Inject(at = @At("RETURN"), method = "render")
    private void profiler_pop$render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        OP.pop();
    }
}
