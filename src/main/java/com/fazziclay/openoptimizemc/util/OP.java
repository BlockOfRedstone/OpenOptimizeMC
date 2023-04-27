package com.fazziclay.openoptimizemc.util;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import net.minecraft.client.MinecraftClient;

/**
 * Fast static wrapper of minecraft Profiler
 */
public class OP {
    private static final boolean IS_LOG = OpenOptimizeMc.debug(false);
    private static boolean isEnabled = false;
    private static Thread thread = null;
    private static int deep = 0;

    public static void push(String s) {
        if (!isEnabled) return;
        deep++;
        if (IS_LOG) {
            OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] push " + s);
            if (deep > 50) OpenOptimizeMc.LOGGER.error("[OP] deep > 50!: " +s);
        }
        if (checkBadThread()) return;
        MinecraftClient.getInstance().getProfiler().push(s);
    }

    public static void swap(String s) {
        if (!isEnabled) return;
        if (IS_LOG) OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] swap " + s);
        if (checkBadThread()) return;
        MinecraftClient.getInstance().getProfiler().swap(s);
    }

    public static void pop() {
        if (!isEnabled) return;
        deep--;
        if (IS_LOG) OpenOptimizeMc.LOGGER.info("[OP] ["+deep+"] pop");
        if (checkBadThread()) return;
        MinecraftClient.getInstance().getProfiler().pop();
    }

    private static boolean checkBadThread() {
        return thread != Thread.currentThread();
    }

    /**
     * {@link OP} need set main thread. Called once at HEAD in MinecraftClient::run
     * @param thread main thread for OP
     */
    public static void initThread(Thread thread) {
        if (OP.thread != null) throw new RuntimeException("Thread already initialized! (double client.run()?????)");
        OP.thread = thread;
    }

    public static void setEnabled(boolean b) {
        OP.isEnabled = b;
    }
}
