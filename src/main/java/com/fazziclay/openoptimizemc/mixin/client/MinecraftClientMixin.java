package com.fazziclay.openoptimizemc.mixin.client;

import com.fazziclay.openoptimizemc.m.FpsContainer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fazziclay.openoptimizemc.OP;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements FpsContainer {
    @Shadow private static int currentFps;

    @Inject(at = @At("HEAD"), method = "run")
    private void run(CallbackInfo ci) {
        OP.initThread(Thread.currentThread());
    }

    @Override
    public int getCurrentFps() {
        return currentFps;
    }
}
