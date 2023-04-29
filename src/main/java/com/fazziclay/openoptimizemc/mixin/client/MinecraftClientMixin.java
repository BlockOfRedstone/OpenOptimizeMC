package com.fazziclay.openoptimizemc.mixin.client;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.util.OP;
import com.fazziclay.openoptimizemc.util.UpdateChecker;
import com.fazziclay.openoptimizemc.util.m.FpsContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements FpsContainer {
    @Shadow private static int currentFps;

    @Inject(at = @At("HEAD"), method = "run")
    private void run(CallbackInfo ci) {
        OP.initThread(Thread.currentThread());
    }

    @Inject(at = @At("HEAD"), method = "joinWorld")
    private void joinWorld(ClientWorld world, CallbackInfo ci) {
        if (!OpenOptimizeMc.getConfig().isUpdateChunks()) OpenOptimizeMc.getConfig().setUpdateChunks(true);
        if (UpdateChecker.isUpdateAvailable()) {
            new Thread(() -> {
                try {
                    Thread.sleep(2*1000);
                } catch (InterruptedException ignore) {}
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    String url = UpdateChecker.getUpdateURL();
                    MutableText text = Text.literal(url).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
                    player.sendMessage(Text.translatable("openoptimizemc.updateAvailable.chat", text));
                }
            }).start();
        }
    }

    @Override
    public int getCurrentFps() {
        return currentFps;
    }
}
