package com.fazziclay.openoptimizemc.config;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ConfigKeyBinds {
    private static boolean configOpen = false;
    private static KeyBinding configKeyBinding;

    public static void init() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.openoptimizemc.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "key.openoptimizemc.keybinds.categary"));
    }

    public static void tick(MinecraftClient client) {
        if (configKeyBinding.isPressed()) {
            if (!configOpen) {
                client.setScreen(new ConfigScreen(null));
            }
            configOpen = true;
        } else if (configOpen) {
            configOpen = false;
        }
    }
}
