package com.fazziclay.openoptimizemc;

import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.behavior.BehaviorType;
import com.fazziclay.openoptimizemc.config.Config;
import com.fazziclay.openoptimizemc.config.ConfigKeyBinds;
import com.fazziclay.openoptimizemc.experemental.DirtRenderer;
import com.fazziclay.openoptimizemc.util.OP;
import com.fazziclay.openoptimizemc.util.UpdateChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * Main class of FabricMod.
 */
public class OpenOptimizeMc implements ClientModInitializer {
    public static final String ID = "openoptimizemc";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    private static Config config;
    private static final BehaviorManager behaviorManager = new BehaviorManager();
    private static final DirtRenderer DIRT_RENDERER = DirtRenderer.INSTANCE;

    @Override
    public void onInitializeClient() {
        LOGGER.info("OpenOptimizeMC (modID: "+ID+") initializing... Version: " + Version.NAME + " build " + Version.BUILD + " DEVELOPMENT="+Version.DEVELOPMENT);
        config = Config.load(new File(MinecraftClient.getInstance().runDirectory, "config/openoptimizemc.json"));
        config.setUpdateChunks(true); // Set to true for fix infinity loading screen
        OP.setEnabled(config.isAdvancedProfiler());
        behaviorManager._updateConfig(config);
        behaviorManager.setBehaviorType(config.isAIBehavior() ? BehaviorType.AI_AUTOMATIC : BehaviorType.CONFIG_DIRECTLY);

        ConfigKeyBinds.init();
        UpdateChecker.initialCheck();

        registerEvents();
        LOGGER.info("OpenOptimizeMC initialized successfully!");
    }

    private void registerEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(this::tickStart);
        ClientTickEvents.END_CLIENT_TICK.register(this::tickEnd);
        WorldRenderEvents.BEFORE_ENTITIES.register(OpenOptimizeMc.this::beforeEntities);
        WorldRenderEvents.AFTER_ENTITIES.register(OpenOptimizeMc.this::afterEntities);
    }

    private void afterEntities(WorldRenderContext context) {
        DIRT_RENDERER.afterEntities(context);
    }

    private void beforeEntities(WorldRenderContext context) {
        DIRT_RENDERER.beforeEntities(context);
    }

    private void tickStart(MinecraftClient client) {
        behaviorManager.tick(client); // Behavior tick (for Automatic behavior)
        if (!isInWorld()) {
            if (!config.isUpdateChunks()) config.setUpdateChunks(true);
        }
    }

    private void tickEnd(MinecraftClient client) {
        ConfigKeyBinds.tick(client); // Keybindings tick.
    }


    // == GETTERS AND SETTERS
    public static Config getConfig() {
        return config;
    }

    public static BehaviorManager getBehaviorManager() {
        return behaviorManager;
    }


    // == UTILS ==
    /**
     * wrapper of debug values
     */
    public static boolean debug(boolean b) {
        return Version.DEVELOPMENT && b;
    }

    /**
     * Check MinecraftInstance player uuid and received entity uuid.
     */
    public static boolean isSelfPlayer(Entity player) {
        if (player == null) {
            LOGGER.warn("isSelfPlayer player argument is null! Returned false.");
            return false;
        }
        if (MinecraftClient.getInstance().player != null) {
            return Objects.equals(player.getUuid(), MinecraftClient.getInstance().player.getUuid());
        }
        return false;
    }

    public static boolean isInWorld() {
        return MinecraftClient.getInstance().world != null;
    }
}
