package com.fazziclay.openoptimizemc.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourcesUtil {
    public static String getText(String path) {
        InputStream is = ResourcesUtil.class.getClassLoader().getResourceAsStream(path);
        try {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Exception in ResourcesUtil.getText.", e);
        }
    }
}
