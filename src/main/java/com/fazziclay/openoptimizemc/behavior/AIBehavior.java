package com.fazziclay.openoptimizemc.behavior;

import com.fazziclay.openoptimizemc.util.Debug;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.util.m.FpsContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/**
 * Mod behavior. By current game states (fps, and etc...)
 * This is "Automatic" function in mod config
 */
public class AIBehavior implements Behavior {
    private static final boolean LOG = OpenOptimizeMc.debug(false);

    private int fps;
    private State state = State.DEAFAUT;

    @Override
    public void tick(MinecraftClient client) {
        FpsContainer fpsContainer = (FpsContainer) client;
        int currentFps = fpsContainer.getCurrentFps();
        fps = (int) (currentFps * state.modifier);
        if (LOG) {
            OpenOptimizeMc.LOGGER.info(state + " REAL: " + currentFps + "; INTERNAL: " + fps);
        }
        Debug.setRealFps(currentFps);
        Debug.setAiFps(fps);
        Debug.setAiState(state == null ? "null" : state.toString());
        recalculateState();
    }

    private void recalculateState() {
        if (fps > 60) {
            state = State.DEAFAUT;
            return;
        }
        if (fps > 30) {
            state = State.FPS60;
            return;
        }
        if (fps > 15) {
            state = State.FPS30;
            return;
        }
        if (fps > 10) {
            state = State.FPS15;
            return;
        }
        if (fps > 5) {
            state = State.FPS10;
            return;
        }
        if (fps > 0) {
            state = State.FPS5;
        }
    }

    enum State {
        DEAFAUT(1, 0),
        FPS60(0.9f, 0),
        FPS30(0.5f, -30),
        FPS15(0.30f, -45),
        FPS10(0.20f, -50),
        FPS5(0.15f, -55);

        final float modifier;
        final int shift;

        State(float modifier, int shift) {
            this.modifier = modifier;
            this.shift = shift;
        }

        @Override
        public String toString() {
            return "m="+modifier + " " + name();
        }
    }

    @Override
    public boolean renderWorld() {
        return fps > 0;
    }

    @Override
    public boolean renderEntities(Entity entity) {
        return fps > 0;
    }

    @Override
    public boolean renderBlockEntities(BlockEntity block) {
        return fps > 1;
    }

    @Override
    public boolean renderEntityArmor(LivingEntity livingEntity) {
        return fps >= 30;
    }

    @Override
    public boolean renderEntityElytra(LivingEntity livingEntity) {
        return fps >= 20;
    }

    @Override
    public boolean renderPlayers(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return fps > 0;
    }

    @Override
    public boolean renderPlayersCapes(AbstractClientPlayerEntity pla) {
        return fps >= 15;
    }

    @Override
    public boolean cubePrimitivePlayers(AbstractClientPlayerEntity player) {
        if (OpenOptimizeMc.isSelfPlayer(player)) return false;
        return fps < 2;
    }

    @Override
    public boolean dirtRenderer(Entity entity) {
        if (OpenOptimizeMc.isSelfPlayer(entity)) return fps <= 4;
        return fps < 30;
    }

    @Override
    public boolean renderEntityHeldItem(LivingEntity entity) {
        if (OpenOptimizeMc.isSelfPlayer(entity)) return fps <= 7;
        return fps > 15;
    }

    @Override
    public boolean renderEntityHeadItem(LivingEntity entity) {
        return fps > 20;
    }

    @Override
    public boolean renderPlayersStuckObjects(LivingEntity entity) {
        return fps > 20;
    }

    @Override
    public boolean overrideEntityShouldRender(Entity entity) {
        return false;
    }

    @Override
    public boolean onlyHeadPlayers(AbstractClientPlayerEntity player) {
        if (OpenOptimizeMc.isSelfPlayer(player)) return fps <= 6;
        return fps < 25;
    }

    @Override
    public boolean cacheHasEnchantments() {
        return fps < 120;
    }

    @Override
    public boolean updateChunks() {
        return fps > 2;
    }
}
