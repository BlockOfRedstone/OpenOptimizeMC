package com.fazziclay.openoptimizemc.behavior;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/**
 * Mod behavior interface
 */
public interface Behavior {
    void tick(MinecraftClient client);

    boolean renderWorld();
    boolean renderEntities(Entity entity);
    boolean renderBlockEntities(BlockEntity block);
    boolean renderEntityArmor(LivingEntity livingEntity);
    boolean renderEntityElytra(LivingEntity livingEntity);
    boolean renderPlayers(AbstractClientPlayerEntity abstractClientPlayerEntity);
    boolean renderPlayersCapes(AbstractClientPlayerEntity pla);

    boolean cubePrimitivePlayers(AbstractClientPlayerEntity player);
    boolean dirtRenderer(Entity entity);

    boolean renderEntityHeldItem(LivingEntity entity);

    boolean renderEntityHeadItem(LivingEntity entity);

    boolean renderPlayersStuckObjects(LivingEntity entity);

    boolean overrideEntityShouldRender(Entity entity);

    boolean onlyHeadPlayers(AbstractClientPlayerEntity player);
    boolean cacheHasEnchantments();

    boolean updateChunks();
}
