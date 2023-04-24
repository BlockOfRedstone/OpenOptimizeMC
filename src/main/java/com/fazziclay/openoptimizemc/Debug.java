package com.fazziclay.openoptimizemc;

public class Debug {
    private static int realFps = -1;
    private static int aiFps = -1;
    private static String aiState = "-";

    public static String getText() {
        return "[OpenOptimizeMc] " + "Real/Mod: " + realFps + "/" + aiFps + " " + aiState;
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
}
