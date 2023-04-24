package com.fazziclay.openoptimizemc.mixin.client;

import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.m.CachedHasEnchantments;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Optimization for ArmorFeatureRendererMixin
// Cache glint parametr and add disable functional
@Mixin(ItemStack.class)
public class ItemStackMixin implements CachedHasEnchantments {
    private static final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();


    private boolean isCached = false;
    private boolean cachedHasEnchantments;

    @Inject(at = @At("HEAD"), method = "hasEnchantments", cancellable = true)
    private void hasEnchantments$head(CallbackInfoReturnable<Boolean> cir) {
        if (isCacheEnabled()) {
            if (hasEnchantmentsIsCached()) {
                cir.setReturnValue(hasEnchantmentsGetCached());
            }
        } else {
            isCached = false;
        }
    }

    @Inject(at = @At("RETURN"), method = "hasEnchantments")
    private void hasEnchantments$return(CallbackInfoReturnable<Boolean> cir) {
        if (isCacheEnabled()) {
            hasEnchantmentsUpdate(cir.getReturnValueZ());
        }
    }

    private boolean isCacheEnabled() {
        return behaviorManager.getBehavior().cacheHasEnchantments();
    }

    @Override
    public boolean hasEnchantmentsGetCached() {
        return cachedHasEnchantments;
    }

    @Override
    public boolean hasEnchantmentsIsCached() {
        return isCached;
    }

    @Override
    public void hasEnchantmentsUpdate(boolean b) {
        isCached = true;
        cachedHasEnchantments = b;
    }
}
