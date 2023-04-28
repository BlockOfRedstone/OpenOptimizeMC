package com.fazziclay.openoptimizemc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

/**
 * Static info collector for F3 debug menu
 */
public class Debug {
    private static int realFps = -1;
    private static int aiFps = -1;
    private static String aiState = "-";
    private static int experimentalRendererEntities;
    private static float globalK;
    private static String experimentalStat = "";

    public static String getText() {
        return "[OpenOptimizeMc] " + "[AI] r=" + realFps + " s=" + aiFps + " " + aiState + "[ExR] list="+experimentalRendererEntities + " " + experimentalStat + " " + " k=" + globalK;
    }

    public static void setAiFps(int aiFps) {
        Debug.aiFps = aiFps;
    }

    public static void setRealFps(int realFps) {
        Debug.realFps = realFps;
    }

    public static void setAiState(String s) {
        aiState = s;
    }

    public static void setExperimentalRendererEntityListCount(int size) {
        Debug.experimentalRendererEntities = size;
    }

    public static void setExperimentalRendererGlobalK(float globalK) {
        Debug.globalK = globalK;
    }

    public static void renderText() {
        MatrixStack matrices = new MatrixStack();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.drawWithShadow(matrices, Text.literal(MinecraftClient.getInstance().fpsDebugString), 1, 1, Color.WHITE.getRGB());
        textRenderer.drawWithShadow(matrices, Text.literal(Debug.getText()), 1, 9, Color.WHITE.getRGB());
    }

    public static void setExperimentalStatString(String s) {
        experimentalStat = s;
    }
}
