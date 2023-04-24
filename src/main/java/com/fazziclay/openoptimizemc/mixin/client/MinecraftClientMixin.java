package com.fazziclay.openoptimizemc.mixin.client;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.m.FpsContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
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

    @Inject(at = @At("HEAD"), method = "joinWorld")
    private void joinWorld(ClientWorld world, CallbackInfo ci) {
        if (OpenOptimizeMc.isUpdateAvailable()) {
            new Thread(() -> {
                try {
                    Thread.sleep(6*1000);
                } catch (InterruptedException ignore) {}
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.sendMessage(Text.translatable("openoptimizemc.updateAvailable.chat"));
                }
            }).start();
        }
    }

    @Override
    public int getCurrentFps() {
        return currentFps;
    }
}
