package com.fazziclay.openoptimizemc.experemental;

import com.fazziclay.openoptimizemc.EasterEggs;
import com.fazziclay.openoptimizemc.util.MathConst;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.Random;

public class RenderEntity {
    private final LivingEntity entity;
    private final String entityName;
    private final boolean isModCreator;
    private final boolean isImportantContributor;
    private final Random random;
    private final float colorR;
    private final float colorG;
    private final float colorB;
    private Matrix4f viewMatrix;
    private boolean readyToRender = false;

    public RenderEntity(Entity entity) {
        // TODO: 4/27/23 what,
        this.entity = (LivingEntity) entity;
        this.entityName = entity.getEntityName();
        this.isModCreator = EasterEggs.isModCreatorNickname(entityName);
        if (!isModCreator) {
            this.isImportantContributor = EasterEggs.isImportantContributorNickname(entityName);
        } else {
            this.isImportantContributor = false;
        }
        random = new Random();
        if (isModCreator) {
            random.setSeed(EasterEggs.getModCreatorSeed(entityName));
            colorR = 0;
            colorG = 1;
            colorB = 0;
        } else if (isImportantContributor) {
            random.setSeed(EasterEggs.getContributorRandomSeedByNickname(entityName));
            colorR = 1;
            colorG = 0;
            colorB = 0;
        } else {
            random.setSeed(entityName.hashCode());
            colorR = random.nextFloat();
            colorG = random.nextFloat();
            colorB = random.nextFloat();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void renderTick(Entity player) {

    }

    public float getColorR() {
        return colorR;
    }

    public float getColorG() {
        return colorG;
    }

    public float getColorB() {
        return colorB;
    }

    public boolean isModCreator() {
        return isModCreator;
    }

    public boolean isImportantContributor() {
        return isImportantContributor;
    }

    private static final Matrix4f IDENTIFY = new Matrix4f();
    private static final boolean id = false;

    public Matrix4f getHeadModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();
        mmm.translate(0, 1.5f, 0);
        mmm.rotateY(-entity.getYaw() * MathConst.F_PI_D180);
        mmm.rotateX(entity.getPitch() * MathConst.F_PI_D180);
        mmm.translate(0, 0.10f, 0);
        mmm.scale(isModCreator ? 1 : 0.51f);
        return mmm;
    }

    public Matrix4f getBodyModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();
        mmm.rotateY(-entity.bodyYaw * MathConst.F_PI_D180);
        mmm.scale(0.5f, 0.65f, 0.36f);
        mmm.translate(0, 0.5f + 1f + 0.05f, 0);
        return mmm;
    }

    public Matrix4f getLeftLegModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();

        mmm.rotateY(-entity.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
        mmm.translate(0, 0.35f, 0.14f);
        mmm.scale(0.25f, 0.65f, 0.25f);

        float f = entity.limbAnimator.getPos();
        float g = entity.limbAnimator.getSpeed();
        float k = System.currentTimeMillis() / 3f % 50;

        float leftLegPitch = MathHelper.cos(f * 0.6662f + (isModCreator ? 0 : (float)Math.PI)) * 1.4f * g / k;
        mmm.rotateZ((float) (leftLegPitch * Math.PI*2));

        return mmm;
    }

    public Matrix4f getRightLegModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();
        mmm.rotateY(-entity.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
        mmm.translate(0, 0.35f, -0.14f);
        mmm.scale(0.25f, 0.65f, 0.25f);

        float f = entity.limbAnimator.getPos();
        float g = entity.limbAnimator.getSpeed();
        float k = System.currentTimeMillis() / 3f % 50;

        float rightLegPitch = MathHelper.cos(f * 0.6662f) * 1.4f * g / k;
        mmm.rotateZ((float) (rightLegPitch * Math.PI * 2));

        return mmm;
    }

    public Matrix4f getLeftArmModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();
        mmm.rotateY(-entity.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
        mmm.translate(0, 1, 0.34f);
        mmm.scale(0.20f, 0.65f, 0.20f);

        float f = entity.limbAnimator.getPos();
        float g = entity.limbAnimator.getSpeed();
        float k = System.currentTimeMillis() / 3f % 50;

        float leftArmPitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f / k;
        mmm.rotateZ((float) (leftArmPitch * Math.PI));
        return mmm;
    }

    public Matrix4f getRightArmModelMatrix() {
        if (id) return IDENTIFY;
        Matrix4f mmm = new Matrix4f();
        mmm.rotateY(-entity.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
        mmm.translate(0, 1, -0.34f);
        mmm.scale(0.20f, 0.65f + (isImportantContributor ? 0.1f : 0f), 0.20f);

        float f = entity.limbAnimator.getPos();
        float g = entity.limbAnimator.getSpeed();
        float k = System.currentTimeMillis() / 3f % 50;

        float rightArmPitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f / k;
        mmm.rotateZ((float) (rightArmPitch * Math.PI));
        return mmm;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
        this.readyToRender = true;
    }

    public Matrix4f getViewMatrix() {
        if (viewMatrix == null) {
            viewMatrix = new Matrix4f();
            viewMatrix.scale(0.25f);
        }
        if (isModCreator) {
            viewMatrix.translate(0, 0.25f, 0);
            viewMatrix.scale(0.75f);
        }
        return viewMatrix;
    }

    public boolean isReadyToRender() {
        return readyToRender;
    }
}
