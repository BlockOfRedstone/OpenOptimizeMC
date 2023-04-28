package com.fazziclay.openoptimizemc.behavior;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.config.Config;
import com.fazziclay.openoptimizemc.config.RendererType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/**
 * Mod behavior. Directly from config
 */
public class ConfigBehavior implements Behavior {
    private final Config config;

    public ConfigBehavior(Config config) {
        this.config = config;
    }

    @Override
    public void tick(MinecraftClient client) {
        // do nothing
    }

    @Override
    public boolean renderWorld() {
        return config.isRenderLevel();
    }

    @Override
    public boolean renderEntities(Entity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isRenderEntities();
    }

    @Override
    public boolean renderEntityArmor(LivingEntity entity) {
        return config.isRenderArmor();
    }

    @Override
    public boolean renderEntityElytra(LivingEntity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isRenderArmor(); // TODO: 4/19/23 move to external
    }

    @Override
    public boolean renderPlayers(AbstractClientPlayerEntity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isRenderPlayers();
    }

    @Override
    public boolean renderEntityHeldItem(LivingEntity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isHeldItemFeature();
    }

    @Override
    public boolean renderEntityHeadItem(LivingEntity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isHeldItemFeature();
    }

    @Override
    public boolean renderPlayersStuckObjects(LivingEntity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return true;
        }
        return config.isHeldItemFeature(); // TODO: 4/19/23 add  external
    }

    @Override
    public boolean overrideEntityShouldRender(Entity entity) {
        return config.isEntityAlwaysShouldRender();
    }

    @Override
    public boolean onlyHeadPlayers(AbstractClientPlayerEntity player) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(player)) {
            return false;
        }
        return config.isPlayersOnlyHeads(); // TODO: 4/19/23 move extarnal
    }

    @Override
    public boolean cacheHasEnchantments() {
        return config.isCacheItemStackEnchantments();
    }

    @Override
    public boolean updateChunks() {
        return config.isUpdateChunks();
    }

    @Override
    public boolean renderBlockEntities(BlockEntity block) {
        return config.isRenderBlockEntities();
    }

    @Override
    public boolean renderPlayersCapes(AbstractClientPlayerEntity pla) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(pla)) {
            return true;
        }
        return renderEntityElytra(pla); // TODO: 4/19/23 move extarnal
    }

    @Override
    public boolean cubePrimitivePlayers(AbstractClientPlayerEntity player) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(player)) {
            return false;
        }
        return config.getRenderer() == RendererType.PRIMITIVE_CUBE;
    }

    @Override
    public boolean dirtRenderer(Entity entity) {
        if (config.isNotApplyFeaturesForSelfPlayer() && OpenOptimizeMc.isSelfPlayer(entity)) {
            return false;
        }
        return config.getRenderer() == RendererType.DIRT_RENDERER;
    }
}
