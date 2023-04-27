package com.fazziclay.openoptimizemc.util;

import com.fazziclay.openoptimizemc.Version;

import java.io.IOException;

public class UpdateChecker {
    private static final String LATEST_BUILD_URL = "https://fazziclay.github.io/api/project_4/v1/latest_build";
    private static boolean hasNoUpdates = false;
    private static boolean isUpdateAvailable = false;

    public static int parseLatestBuild() {
        try {
            return Integer.parseInt(NetworkUtil.parseTextPage(LATEST_BUILD_URL).trim());
        } catch (IOException e) {
            new Exception("Failed to parse latest build of OpenOptimizeMC", e).printStackTrace();
        }
        return -1;
    }

    public static void check(Result result) {
        if (hasNoUpdates) return;
        Thread thread = new Thread(() -> {
            int latest = parseLatestBuild();
            if (latest > Version.BUILD) {
                result.available(latest, "TODO", "TODO");
            } else {
                hasNoUpdates = true;
            }
        });
        thread.setName("OpenOptimizeMc UpdateChecker");
        thread.setDaemon(true);
        thread.start();
    }

    public static String getUpdateURL() {
        return "https://fazziclay.github.io/openoptimizemc?from_build="+Version.BUILD+"&from_name="+Version.NAME+"&from_dev="+Version.DEVELOPMENT;
    }

    public static boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public static void initialCheck() {
        check((build, name, pageUrl) -> isUpdateAvailable = true);
    }

    public interface Result {
        void available(int build, String name, String pageUrl);
    }
}
