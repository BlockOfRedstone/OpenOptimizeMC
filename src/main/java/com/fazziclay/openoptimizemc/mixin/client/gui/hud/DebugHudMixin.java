package com.fazziclay.openoptimizemc.mixin.client.gui.hud;

import com.fazziclay.openoptimizemc.Debug;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    private static final boolean DEBUG = OpenOptimizeMc.debug(true);

    @Inject(at = @At("RETURN"), method = "getLeftText")
    private void getLeftText(CallbackInfoReturnable<List<String>> info) {
        List<String> r = info.getReturnValue();
        if (DEBUG)r.add(Debug.getText());
    }
}
