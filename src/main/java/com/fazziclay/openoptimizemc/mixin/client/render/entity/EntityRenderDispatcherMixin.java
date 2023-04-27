package com.fazziclay.openoptimizemc.mixin.client.render.entity;


import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.experemental.ExperimentalRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();
    private static final ExperimentalRenderer experimentalRenderer = ExperimentalRenderer.INSTANCE;

    @Inject(at = @At("HEAD"), method = "shouldRender", cancellable = true)
    public <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!behaviorManager.getBehavior().renderEntities(entity)) {
            cir.setReturnValue(false);
            return;
        }
        if (behaviorManager.getBehavior().overrideEntityShouldRender(entity)) {
            cir.setReturnValue(true);
            //return;
        }
    }

    @Inject(at = @At("RETURN"), method = "shouldRender", cancellable = true)
    public <E extends Entity> void shouldRender$return(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity) {
            if (!cir.getReturnValue()) { // if false
                experimentalRenderer.unload(entity);
            } else {
                experimentalRenderer.load(entity);
            }
        }
        if (entity instanceof ArmorStandEntity) {
            cir.setReturnValue(false);
        }
    }
}
