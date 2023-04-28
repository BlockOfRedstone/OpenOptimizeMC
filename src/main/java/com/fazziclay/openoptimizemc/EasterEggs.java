package com.fazziclay.openoptimizemc;

public class EasterEggs {
    public static boolean isModCreatorNickname(String s) {
        return "FazziCLAY".equalsIgnoreCase(s);
    }

    public static boolean isImportantContributorNickname(String s) {
        // TODO: 5opka not a contributor.
        // TODO: Tyrbabyrik not a contributor
        // TODO: THIS NICKNAMES ADDED FOR TESTS!11!!!11!!!
        return "5opka".equals(s) || "Tyrbabyrik".equals(s);
    }

    public static long getContributorRandomSeedByNickname(String name) {
        return 40150L + name.length();
    }

    public static long getModCreatorSeed(String s) {
        return 2332200L;
    }
}
