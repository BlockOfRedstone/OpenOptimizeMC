package com.fazziclay.openoptimizemc.experemental;

import com.fazziclay.dirtrenderer.rendering.opengl.*;
import com.fazziclay.openoptimizemc.MathConst;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.util.ResourcesUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ExperimentalRenderer {

    private static final float[] positions_colors2 = {
            -0.5f, -0.5f, -0.5f,    -1.f,  0.f,  0.f,     0.f, 0.f,              // 0
            -0.5f,  0.5f, -0.5f,    -1.f,  0.f,  0.f,     1.f, 0.f,              // 1
            -0.5f,  0.5f,  0.5f,    -1.f,  0.f,  0.f,     1.f, 1.f,              // 2
            -0.5f, -0.5f,  0.5f,    -1.f,  0.f,  0.f,     0.f, 1.f,              // 3

            // BACK
            0.5f, -0.5f, -0.5f,     1.f,  0.f,  0.f,     1.f, 0.f,              // 4
            0.5f,  0.5f, -0.5f,     1.f,  0.f,  0.f,     0.f, 0.f,              // 5
            0.5f,  0.5f,  0.5f,     1.f,  0.f,  0.f,     0.f, 1.f,              // 6
            0.5f, -0.5f,  0.5f,     1.f,  0.f,  0.f,     1.f, 1.f,              // 7

            // RIGHT
            -0.5f,  0.5f, -0.5f,     0.f,  1.f,  0.f,     0.f, 0.f,              // 8
            0.5f,  0.5f, -0.5f,     0.f,  1.f,  0.f,     1.f, 0.f,              // 9
            0.5f,  0.5f,  0.5f,     0.f,  1.f,  0.f,     1.f, 1.f,              // 10
            -0.5f,  0.5f,  0.5f,     0.f,  1.f,  0.f,     0.f, 1.f,              // 11

            // LEFT
            -0.5f, -0.5f, -0.5f,     0.f, -1.f,  0.f,     1.f, 0.f,              // 12
            0.5f, -0.5f, -0.5f,     0.f, -1.f,  0.f,     0.f, 0.f,              // 13
            0.5f, -0.5f,  0.5f,     0.f, -1.f,  0.f,     0.f, 1.f,              // 14
            -0.5f, -0.5f,  0.5f,     0.f, -1.f,  0.f,     1.f, 1.f,              // 15

            // TOP
            -0.5f, -0.5f,  0.5f,     0.f,  0.f,  1.f,     0.f, 0.f,              // 16
            -0.5f,  0.5f,  0.5f,     0.f,  0.f,  1.f,     1.f, 0.f,              // 17
            0.5f,  0.5f,  0.5f,     0.f,  0.f,  1.f,     1.f, 1.f,              // 18
            0.5f, -0.5f,  0.5f,     0.f,  0.f,  1.f,     0.f, 1.f,              // 19

            // BOTTOM
            -0.5f, -0.5f, -0.5f,    0.f,  0.f, -1.f,     0.f, 1.f,              // 20
            -0.5f,  0.5f, -0.5f,    0.f,  0.f, -1.f,     1.f, 1.f,              // 21
            0.5f,  0.5f, -0.5f,    0.f,  0.f, -1.f,     1.f, 0.f,              // 22
            0.5f, -0.5f, -0.5f,    0.f,  0.f, -1.f,     0.f, 0.f,              // 23
    };


    private static final int[] indices = {
            0,   1,  2,  2,  3,  0, // front
            4,   5,  6,  6,  7,  4, // back
            8,   9, 10, 10, 11,  8, // right
            12, 13, 14, 14, 15, 12, // left
            16, 17, 18, 18, 19, 16, // top
            20, 21, 22, 22, 23, 20  // bottom
    };

    private boolean initialized = false;
    private VAO vao;
    private IndexBuffer indexBuffer;
    private ShaderProgram shaderProgram;

    public void render(AbstractClientPlayerEntity player, MatrixStack matrices) {
        if (!initialized) {
            init();
            initialized = true;
        }
        internalRender(player, matrices);
    }

    private void init() {
        OpenOptimizeMc.LOGGER.info("Experimental initialized!");

        shaderProgram = new ShaderProgram(ResourcesUtil.getText("assets/openoptimizemc/shaders/main.vert"), ResourcesUtil.getText("assets/openoptimizemc/shaders/main.frag"));
        if (!shaderProgram.isCompile()) {
            OpenOptimizeMc.LOGGER.info("Experimental shader not compiled!\nShader log:\n" + shaderProgram.getCompileLog());
        }

        indexBuffer = new IndexBuffer(indices);
        VBO cube_vbo = new VBO(positions_colors2, BufferLayout.create(BufferLayout.ShaderDataType.FLOAT3, BufferLayout.ShaderDataType.FLOAT3, BufferLayout.ShaderDataType.FLOAT2));
        vao = new VAO();
        vao.addVertexBuffer(cube_vbo);
        vao.setIndexBuffer(indexBuffer);
    }

    private static final DirtCuboid[] parts = new DirtCuboid[]{
            new DirtCuboid() { // HEAD
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.translate(0, 1.5f, 0);
                    mmm.rotateY(-player.getYaw() * MathConst.F_PI_D180);
                    mmm.rotateX(player.getPitch() * MathConst.F_PI_D180);
                    mmm.translate(0, 0.10f, 0);
                    mmm.scale(0.51f);
                }
            },
            new DirtCuboid() { // BODY
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.rotateY(-player.bodyYaw * MathConst.F_PI_D180);
                    mmm.scale(0.5f, 0.65f, 0.36f);
                    mmm.translate(0, 0.5f + 1f + 0.05f, 0);
                }
            },
            new DirtCuboid() { // LEFT LEG
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.rotateY(-player.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
                    mmm.translate(0, 0.35f, 0.14f);
                    mmm.scale(0.25f, 0.65f, 0.25f);

                    float f = player.limbAnimator.getPos();
                    float g = player.limbAnimator.getSpeed();
                    float k = System.currentTimeMillis() / 3f % 50;

                    float leftLegPitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g / k;
                    mmm.rotateZ((float) (leftLegPitch * Math.PI*2));
                }
            },
            new DirtCuboid() { // RIGHT LEG
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.rotateY(-player.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
                    mmm.translate(0, 0.35f, -0.14f);
                    mmm.scale(0.25f, 0.65f, 0.25f);

                    float f = player.limbAnimator.getPos();
                    float g = player.limbAnimator.getSpeed();
                    float k = System.currentTimeMillis() / 3f % 50;

                    float rightLegPitch = MathHelper.cos(f * 0.6662f) * 1.4f * g / k;
                    mmm.rotateZ((float) (rightLegPitch * Math.PI * 2));
                }
            },

            new DirtCuboid() { // LEFT ARM
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.rotateY(-player.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
                    mmm.translate(0, 1, 0.34f);
                    mmm.scale(0.20f, 0.65f, 0.20f);

                    float f = player.limbAnimator.getPos();
                    float g = player.limbAnimator.getSpeed();
                    float k = System.currentTimeMillis() / 3f % 50;

                    float leftArmPitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f / k;
                    mmm.rotateZ((float) (leftArmPitch * Math.PI));
                }
            },

            new DirtCuboid() { // RIGHT ARM
                @Override
                public void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player) {
                    mmm.rotateY(-player.bodyYaw * MathConst.F_PI_D180 + MathConst.F_PI_D2);
                    mmm.translate(0, 1, -0.34f);
                    mmm.scale(0.20f, 0.65f, 0.20f);

                    float f = player.limbAnimator.getPos();
                    float g = player.limbAnimator.getSpeed();
                    float k = System.currentTimeMillis() / 3f % 50;

                    float rightArmPitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f / k;
                    mmm.rotateZ((float) (rightArmPitch * Math.PI));
                }
            }
    };
    private void internalRender(AbstractClientPlayerEntity player, MatrixStack matrices) {
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        vao.bind();
        indexBuffer.bind();
        shaderProgram.bind();

        for (DirtCuboid part : parts) {
            Matrix4f modelMatrix = new Matrix4f(matrices.peek().getPositionMatrix());
            part.editModelMatrix(modelMatrix, player);

            Matrix4f viewMatrix = RenderSystem.getModelViewMatrix();
            Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();


            shaderProgram.setMat4f("model_matrix", modelMatrix);
            shaderProgram.setMat4f("view_matrix", viewMatrix);
            shaderProgram.setMat4f("projection_matrix", projectionMatrix);


            glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, NULL);
        }

        VAO.unbind();
        IndexBuffer.unbind();
        ShaderProgram.unbind();

    }

    private abstract static class DirtCuboid {
        public abstract void editModelMatrix(Matrix4f mmm, AbstractClientPlayerEntity player);
    }
}
