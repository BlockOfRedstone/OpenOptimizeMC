package com.fazziclay.openoptimizemc;

import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.behavior.BehaviorType;
import com.fazziclay.openoptimizemc.config.Config;
import com.fazziclay.openoptimizemc.config.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

public class OpenOptimizeMc implements ClientModInitializer {
    public static final String ID = "openoptimizemc";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    private static volatile OpenOptimizeMc instance;
    private static Config config;
    private static final BehaviorManager behaviorManager = new BehaviorManager();
    private static boolean isUpdateAvailable;


    public static OpenOptimizeMc getInstance() {
        return instance;
    }

    public static boolean debug(boolean b) {
        if (Version.DEVELOPMENT) {
            return b;
        }
        return false;
    }

    public static Config getConfig() {
        return config;
    }

    public static BehaviorManager getBehaviorManager() {
        return behaviorManager;
    }

    private boolean togglePlayers = false;
    private boolean toggleOnlyHeads = false;
    private boolean configOpen = false;

    public static boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public static void setIsUpdateAvailable(boolean isUpdateAvailable) {
        OpenOptimizeMc.isUpdateAvailable = isUpdateAvailable;
    }

    public static String getUpdateURL() {
        return "https://fazziclay.github.io/openoptimizemc?from_build="+Version.BUILD+"&from_name="+Version.NAME+"&from_dev="+Version.DEVELOPMENT;
    }

    public static boolean isSelfPlayer(Entity player) {
        if (MinecraftClient.getInstance().player != null) {
            return Objects.equals(player.getUuid(), MinecraftClient.getInstance().player.getUuid());
        }
        return false;
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        config = Config.load(new File(MinecraftClient.getInstance().runDirectory, "config/openoptimizemc.json"));
        OP.setEnabled(config.isAdvancedProfiler());
        behaviorManager._updateConfig(config);
        behaviorManager.setBehaviorType(config.isAIBehavior() ? BehaviorType.AI_AUTOMATIC : BehaviorType.CONFIG_DIRECTLY);
        ClientTickEvents.START_CLIENT_TICK.register(behaviorManager::tick);

        KeyBinding binding_togglePlayers = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.openoptimizemc.togglePlayers", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.openoptimizemc.keybinds.categary"));
        KeyBinding binding_toggleOnlyHeads = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.openoptimizemc.toggleOnlyHeads", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.openoptimizemc.keybinds.categary"));
        KeyBinding binding_config = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.openoptimizemc.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "key.openoptimizemc.keybinds.categary"));



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (binding_togglePlayers.isPressed()) {
                togglePlayers = true;
            } else if (togglePlayers) {
                runTogglePlayers(client);
                togglePlayers = false;
            }

            if (binding_toggleOnlyHeads.isPressed()) {
                toggleOnlyHeads = true;
            } else if (toggleOnlyHeads) {
                runToggleOnlyHeads(client);
                toggleOnlyHeads = false;
            }

            if (binding_config.isPressed()) {
                if (!configOpen) {
                    client.setScreen(new ConfigScreen(null));
                }
                configOpen = true;
            } else if (configOpen) {
                configOpen = false;
            }
        });

        UpdateChecker.check((build, name, pageUrl) -> {
            isUpdateAvailable = true;
        });
    }

    private void runToggleOnlyHeads(MinecraftClient client) {
        LOGGER.info("pressed");
        boolean c = !getConfig().isPlayersOnlyHeads();
        getConfig().setPlayersOnlyHeads(c);
        if (c) {
            client.player.sendMessage(Text.of("§a[client] Players only-head enabled"));
        } else {
            client.player.sendMessage(Text.of("§c[client] Players only-head disabled"));
        }
    }

    private void runTogglePlayers(MinecraftClient client) {
        LOGGER.info("pressed");
        boolean c = !getConfig().isRenderPlayers();
        if (c) {
            client.player.sendMessage(Text.of("§a[client] Players enabled"));
        } else {
            client.player.sendMessage(Text.of("§c[client] Players disabled"));
        }
        getConfig().setRenderPlayers(c);
    }
}
