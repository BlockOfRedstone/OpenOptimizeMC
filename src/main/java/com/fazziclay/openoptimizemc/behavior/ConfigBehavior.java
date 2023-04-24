package com.fazziclay.openoptimizemc.behavior;

import com.fazziclay.openoptimizemc.config.Config;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

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
        return config.isRenderEntities();
    }

    @Override
    public boolean renderEntityArmor(LivingEntity entity) {
        return config.isRenderArmor();
    }

    @Override
    public boolean renderEntityElytra(LivingEntity entity) {
        return config.isRenderArmor(); // TODO: 4/19/23 move to external
    }

    @Override
    public boolean renderPlayers() {
        return config.isRenderPlayers();
    }

    @Override
    public boolean renderEntityHeldItem(LivingEntity entity) {
        return config.isHeldItemFeature();
    }

    @Override
    public boolean renderEntityHeadItem(LivingEntity entity) {
        return config.isHeldItemFeature();
    }

    @Override
    public boolean renderPlayersStuckObjects(LivingEntity entity) {
        return config.isHeldItemFeature(); // TODO: 4/19/23 add  external
    }

    @Override
    public boolean overrideEntityShouldRender(Entity entity) {
        return config.isEntityAlwaysShouldRender();
    }

    @Override
    public boolean onlyHeadPlayers(AbstractClientPlayerEntity player) {
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
        return renderEntityElytra(pla); // TODO: 4/19/23 move extarnal
    }

    @Override
    public boolean cubePrimitivePlayers(AbstractClientPlayerEntity player) {
        return config.isPlayersPrimitive();
    }
}
