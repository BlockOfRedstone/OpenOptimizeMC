package com.fazziclay.openoptimizemc;

import net.minecraft.client.MinecraftClient;

public class OP {
    private static final boolean IS_LOG = OpenOptimizeMc.debug(false);
    private static boolean isEnabled = false;
    private static Thread thread = null;
    private static int deep = 0;

    public static void push(String s) {
        if (!isEnabled) return;
        deep++;
        if (IS_LOG) OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] push " + s);
        if (thread != Thread.currentThread()) return;
        if (deep > 50) OpenOptimizeMc.LOGGER.error("[OP] deep > 50!: " +s);
        MinecraftClient.getInstance().getProfiler().push(s);
    }

    public static void swap(String s) {
        if (!isEnabled) return;
        if (IS_LOG) OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] swap " + s);
        if (thread != Thread.currentThread()) return;
        MinecraftClient.getInstance().getProfiler().swap(s);
    }

    public static void pop() {
        if (!isEnabled) return;
        deep--;
        if (IS_LOG) OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] pop");
        if (thread != Thread.currentThread()) return;
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void initThread(Thread thread) {
        if (OP.thread != null) throw new RuntimeException("Thread already initialized! (double client.run()?????)");
        OP.thread = thread;
    }

    public static void setEnabled(boolean b) {
        OP.isEnabled = b;
    }
}
