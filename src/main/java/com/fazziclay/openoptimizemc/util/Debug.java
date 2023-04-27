package com.fazziclay.openoptimizemc.util;

/**
 * Static info collector for F3 debug menu
 */
public class Debug {
    private static int realFps = -1;
    private static int aiFps = -1;
    private static String aiState = "-";
    private static int experimentalRendererEntities;
    private static float globalK;

    public static String getText() {
        return "[OpenOptimizeMc] " + "Real/Mod: " + realFps + "/" + aiFps + " " + aiState + " | ERE: " + experimentalRendererEntities + " globalK: " + globalK;
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
}
