package com.fazziclay.openoptimizemc;

import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.behavior.BehaviorType;
import com.fazziclay.openoptimizemc.config.Config;
import com.fazziclay.openoptimizemc.config.ConfigKeyBinds;
import com.fazziclay.openoptimizemc.util.OP;
import com.fazziclay.openoptimizemc.util.UpdateChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
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

    @Override
    public void onInitializeClient() {
        LOGGER.info("OpenOptimizeMC (modID: "+ID+") initializing... Version: " + Version.NAME + " build " + Version.BUILD + " DEVELOPMENT="+Version.DEVELOPMENT);
        config = Config.load(new File(MinecraftClient.getInstance().runDirectory, "config/openoptimizemc.json"));
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
        ServerPlayConnectionEvents.JOIN.register(this::join);
    }

    private void tickStart(MinecraftClient client) {
        behaviorManager.tick(client); // Behavior tick (for Automatic behavior)
    }

    private void tickEnd(MinecraftClient client) {
        ConfigKeyBinds.tick(client); // Keybindings tick.
    }

    private void join(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        // TODO: 4/27/23 EDIT THIS
        handler.player.sendMessageToClient(Text.of("owo"), false);
        MinecraftClient.getInstance().player.sendMessage(Text.of("JOINED!!!!"));
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
        if (Version.DEVELOPMENT) {
            return b;
        }
        return false;
    }

    /**
     * Check MinecraftInstance player uuid and received entity uuid.
     */
    public static boolean isSelfPlayer(Entity player) {
        if (MinecraftClient.getInstance().player != null) {
            return Objects.equals(player.getUuid(), MinecraftClient.getInstance().player.getUuid());
        }
        return false;
    }
}
