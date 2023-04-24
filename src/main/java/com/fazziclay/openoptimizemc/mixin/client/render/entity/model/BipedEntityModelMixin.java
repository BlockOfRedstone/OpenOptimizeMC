package com.fazziclay.openoptimizemc.mixin.client.render.entity.model;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BipedEntityModel.class)
public class BipedEntityModelMixin <T extends LivingEntity> {
}
