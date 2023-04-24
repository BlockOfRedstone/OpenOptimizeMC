package com.fazziclay.openoptimizemc.behavior;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface Behavior {
    void tick(MinecraftClient client);

    boolean renderWorld();
    boolean renderEntities(Entity entity);
    boolean renderBlockEntities(BlockEntity block);
    boolean renderEntityArmor(LivingEntity livingEntity);
    boolean renderEntityElytra(LivingEntity livingEntity);
    boolean renderPlayers();
    boolean renderPlayersCapes(AbstractClientPlayerEntity pla);

    boolean cubePrimitivePlayers(AbstractClientPlayerEntity player);

    boolean renderEntityHeldItem(LivingEntity entity);

    boolean renderEntityHeadItem(LivingEntity entity);

    boolean renderPlayersStuckObjects(LivingEntity entity);

    boolean overrideEntityShouldRender(Entity entity);

    boolean onlyHeadPlayers(AbstractClientPlayerEntity player);
    boolean cacheHasEnchantments();

    boolean updateChunks();
}
