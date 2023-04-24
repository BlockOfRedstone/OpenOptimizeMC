package com.fazziclay.openoptimizemc.behavior;

import com.fazziclay.openoptimizemc.Debug;
import com.fazziclay.openoptimizemc.config.Config;
import net.minecraft.client.MinecraftClient;

public class BehaviorManager {
    private Behavior behavior;
    private ConfigBehavior configBehavior;
    private final AIBehavior aiBehavior = new AIBehavior();

    public BehaviorManager() {
    }

    public void tick(MinecraftClient client) {
        if (behavior != null) behavior.tick(client);
    }

    public void _updateConfig(Config config) {
        this.configBehavior = new ConfigBehavior(config);
    }

    public void setBehaviorType(BehaviorType type) {
        switch (type) {
            case AI_AUTOMATIC -> behavior = aiBehavior;
            case CONFIG_DIRECTLY -> behavior = configBehavior;
        }
        Debug.setAiFps(-1);
        Debug.setRealFps(-1);
        Debug.setAiState("-");
    }

    public Behavior getBehavior() {
        return behavior;
    }
}
