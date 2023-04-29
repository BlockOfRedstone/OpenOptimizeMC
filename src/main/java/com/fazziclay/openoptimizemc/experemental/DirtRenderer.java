package com.fazziclay.openoptimizemc.experemental;

import com.fazziclay.dirtrenderer.rendering.opengl.*;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorManager;
import com.fazziclay.openoptimizemc.util.Debug;
import com.fazziclay.openoptimizemc.util.ResourcesUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DirtRenderer {
    private static final String SHADER_KEY_VIEWMATRIX = "view_matrix";
    private static final String SHADER_KEY_PROJECTIONMATRIX = "projection_matrix";
    private static final String SHADERS_PATH_FORMAT = "assets/openoptimizemc/dirtrenderer/shaders/%s";

    public static final DirtRenderer INSTANCE = new DirtRenderer();
    private static final float[] VBO_DATA = {
            // VERTEX COORDS            COLOR            color_shift?? TODO: what is this?
            // FRONT
            -0.5f, -0.5f, -0.5f,    1.f,  1.f,  1.f,     0.5f, 0.f,              // 0
            -0.5f,  0.5f, -0.5f,    1.f,  1.f,  1.f,     0.5f, 0.f,              // 1
            -0.5f,  0.5f,  0.5f,    1.f,  1.f,  1.f,     0.5f, 0.f,              // 2
            -0.5f, -0.5f,  0.5f,    1.f,  1.f,  1.f,     0.7f, 0.f,              // 3

            // BACK
            0.5f, -0.5f, -0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 4
            0.5f,  0.5f, -0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 5
            0.5f,  0.5f,  0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 6
            0.5f, -0.5f,  0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 7

            // RIGHT
            -0.5f,  0.5f, -0.5f,    1.f,  1.f,  1.f,     1.f, 0.f,              // 8
            0.5f,  0.5f, -0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 9
            0.5f,  0.5f,  0.5f,     1.f,  1.f,  1.f,     1.f, 0.f,              // 10
            -0.5f,  0.5f,  0.5f,    1.f,  1.f,  1.f,     1.f, 0.f,              // 11

            // LEFT
            -0.5f, -0.5f, -0.5f,    1.f, 1.f,  1.f,     0.9f, 0.f,              // 12
            0.5f, -0.5f, -0.5f,     1.f, 1.f,  1.f,     0.5f, 0.f,              // 13
            0.5f, -0.5f,  0.5f,     1.f, 1.f,  1.f,     0.5f, 0.f,              // 14
            -0.5f, -0.5f,  0.5f,    1.f, 1.f,  1.f,     0.5f, 0.f,              // 15

            // TOP
            -0.5f, -0.5f,  0.5f,     1.f,  1.f,  1.f,    2.f, 0.f,              // 16
            -0.5f,  0.5f,  0.5f,     1.f,  1.f,  1.f,    2.f, 0.f,              // 17
            0.5f,  0.5f,  0.5f,     1.f,  1.f,  1.f,     2.f, 0.f,              // 18
            0.5f, -0.5f,  0.5f,     1.f,  1.f,  1.f,     2.f, 0.f,              // 19

            // BOTTOM
            -0.5f, -0.5f, -0.5f,   1.f,  1.f, 1.f,     1.f, 0.f,              // 20
            -0.5f,  0.5f, -0.5f,   1.f,  1.f, 1.f,     1.f, 0.f,              // 21
            0.5f,  0.5f, -0.5f,    1.f,  1.f, 1.f,     1.f, 0.f,              // 22
            0.5f, -0.5f, -0.5f,    1.f,  1.f, 1.f,     1.f, 0.f,              // 23
    };


    private static final int[] INDEXES = {
            0,   1,  2,  2,  3,  0, // front
            4,   5,  6,  6,  7,  4, // back
            8,   9, 10, 10, 11,  8, // right
            12, 13, 14, 14, 15, 12, // left
            16, 17, 18, 18, 19, 16, // top
            20, 21, 22, 22, 23, 20  // bottom
    };

    private final BehaviorManager behaviorManager = OpenOptimizeMc.getBehaviorManager();
    private boolean initialized = false;
    private VAO vao;
    private IndexBuffer indexBuffer;
    private ShaderProgram shaderProgram;
    private final HashMap<Entity, RenderEntity> entityList = new HashMap<>();

    private float globalK;
    private long lastCleanup = 0;

    private long debugShadersBinds = 0;
    private long debugShadersUnBinds = 0;
    private long debugGlDrawElementArrays = 0;
    private long debugSetModelMatrix = 0;
    private long debugTimeToRenderMs = 0;



    public void init() {
        OpenOptimizeMc.LOGGER.info("DirtRenderer init...");
        if (initialized) OpenOptimizeMc.LOGGER.warn("WARNING! Double DirtRenderer.init called!!");
        initialized = true;

        openglMCWrapper("init()", () -> {
            shaderProgram = program("main"); // rename to main
            vao = new VAO();
            indexBuffer = new IndexBuffer(INDEXES);
            vao.addVertexBuffer(new VBO(VBO_DATA, BufferLayout.create(BufferLayout.ShaderDataType.FLOAT3, BufferLayout.ShaderDataType.FLOAT3, BufferLayout.ShaderDataType.FLOAT2)));
            vao.setIndexBuffer(indexBuffer);
        });

        OpenOptimizeMc.LOGGER.info("DirtRenderer initialized!");
    }


    public void beforeEntities(WorldRenderContext context) {
        if (!initialized) init();

        debugShadersBinds = 0;
        debugShadersUnBinds = 0;
        debugGlDrawElementArrays = 0;
        debugSetModelMatrix = 0;
        debugTimeToRenderMs = 0;

        // remove non-contains in world
        List<Entity> toDelete = new ArrayList<>();
        for (Entity entity : entityList.keySet()) {
            if (MinecraftClient.getInstance().world.getEntityById(entity.getId()) == null) {
                toDelete.add(entity);
            } else if (!behaviorManager.getBehavior().dirtRenderer(entity)) {
                toDelete.add(entity);
            }
        }
        for (Entity entity : toDelete) {
            entityList.remove(entity);
        }
    }

    public void unload(Entity entity) {
        entityList.remove(entity);
    }

    public void load(Entity entity) {
        if (!behaviorManager.getBehavior().dirtRenderer(entity)) {
            return;
        }
        if (!entityList.containsKey(entity)) {
            if (!entity.getUuid().equals(MinecraftClient.getInstance().cameraEntity.getUuid())) {
                entityList.put(entity, new RenderEntity(entity));
            }
        }
        Debug.setExperimentalRendererEntityListCount(entityList.size());

        globalK = (float) ((double)System.currentTimeMillis() % 1000L);
        Debug.setExperimentalRendererGlobalK(globalK);
    }

    public void afterEntities(WorldRenderContext context) {
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        openglMCWrapper(null, () -> {
            vao.bind();
            indexBuffer.bind();
            shaderProgram.bind();
            debugShadersBinds++;

            Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
            shaderProgram.setMat4f(SHADER_KEY_PROJECTIONMATRIX, projectionMatrix);

            entityList.forEach((entity, renderEntity) -> {
                if (entity.isInvisible()) return;
                if (!renderEntity.isReadyToRender()) return;
                if (entity.getUuid().equals(MinecraftClient.getInstance().cameraEntity.getUuid()) && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
                    return;
                }
                shaderProgram.setMat4f(SHADER_KEY_VIEWMATRIX, renderEntity.getViewMatrix());
                newRender(renderEntity);
            });

            ShaderProgram.unbind();
            debugShadersUnBinds++;
        });
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();


        Debug.setExperimentalStatString(String.format("b=%s unb=%s draws=%s setViewMat=%s time=%sms", debugShadersBinds, debugShadersUnBinds, debugGlDrawElementArrays, debugSetModelMatrix, debugTimeToRenderMs));
    }

    private void newRender(RenderEntity renderEntity) {
        long startRender = System.currentTimeMillis();

        for (DirtCuboid part : PARTS) {
            if (renderEntity.getEntity().isSpectator() && part != HEAD) return;
            part.setupShaderProgram((AbstractClientPlayerEntity) renderEntity.getEntity(), renderEntity);

            glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, NULL);
            debugGlDrawElementArrays++;
        }

        debugTimeToRenderMs += (System.currentTimeMillis() - startRender);
    }


    public void minecraftRenderCall(Entity entity, MatrixStack matrices) {
        RenderEntity renderEntity = entityList.computeIfAbsent(entity, RenderEntity::new);
        renderEntity.renderTick(entity);
        Matrix4f viewMatrix = new Matrix4f(matrices.peek().getPositionMatrix()).mul(RenderSystem.getModelViewMatrix());
        renderEntity.setViewMatrix(viewMatrix);
    }

    private void openglMCWrapper(@Nullable String m, Runnable r) {
        int mcVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int mcVbo = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        int mcIbo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING); // IDO - IndexBufferArray = ElementArray

        r.run();

        glBindVertexArray(mcVao);
        glBindBuffer(GL_ARRAY_BUFFER, mcVbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mcIbo);
        if (m != null) OpenOptimizeMc.LOGGER.info("["+m+"] MC values: vao="+mcVao + " vbo=" + mcVbo + " ibo=" + mcIbo);
    }

    private void setModelMatrix(ShaderProgram program, Matrix4f mat) {
        program.setMat4f("model_matrix", mat);
        debugSetModelMatrix++;
    }

    private ShaderProgram program(String name) {
        OpenOptimizeMc.LOGGER.info("ShaderProgram '"+name+"' created!");
        ShaderProgram shaderProgram = new ShaderProgram(ResourcesUtil.getText(SHADERS_PATH_FORMAT.formatted(name + ".vert")), ResourcesUtil.getText(SHADERS_PATH_FORMAT.formatted(name + ".frag")));
        if (!shaderProgram.isCompile()) {
            OpenOptimizeMc.LOGGER.info("ExperimentalRenderer shader name='"+name+"' not compiled!\nShader log:\n" + shaderProgram.getCompileLog());
        }
        return shaderProgram;
    }

    private final DirtCuboid HEAD = new DirtCuboid("head") { // HEAD
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getHeadModelMatrix());
            //shaderProgram.setFloat("yaw", player.getYaw());
            //shaderProgram.setFloat("pitch", player.getPitch());
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorB(), renderEntity.getColorG(), renderEntity.getColorR()));
        }
    };

    private final DirtCuboid BODY = new DirtCuboid("body") { // BODY
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getBodyModelMatrix());
            //shaderProgram.setFloat("bodyYaw", player.bodyYaw);
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorR(), renderEntity.getColorG(), renderEntity.getColorB()));
        }
    };

    private final DirtCuboid LEG_LEFT = new DirtCuboid("left_leg") { // LEFT LEG
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getLeftLegModelMatrix());
            //shaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            //shaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            //shaderProgram.setFloat("_globalK", globalK);
            //shaderProgram.setFloat("z_shift", 0.14f);
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorR(), renderEntity.getColorG(), renderEntity.getColorB()));
        }
    };

    private final DirtCuboid LEG_RIGHT = new DirtCuboid("right_leg") { // RIGHT LEG
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getRightLegModelMatrix());
            //shaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            //shaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            //shaderProgram.setFloat("_globalK", globalK);
            //shaderProgram.setFloat("z_shift", -0.14f);
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorR(), renderEntity.getColorG(), renderEntity.getColorB()));
        }
    };

    private final DirtCuboid ARM_LEFT = new DirtCuboid("left_arm") { // LEFT ARM
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getLeftArmModelMatrix());
            //shaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            //shaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            //shaderProgram.setFloat("_globalK", globalK);
            //shaderProgram.setFloat("z_shift", 0.34f);
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorR(), renderEntity.getColorG(), renderEntity.getColorB()));

        }
    };

    private final DirtCuboid ARM_RIGHT = new DirtCuboid("right_arm") { // RIGHT ARM
        @Override
        public void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            setModelMatrix(shaderProgram, renderEntity.getRightArmModelMatrix());
            //shaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            //shaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            //shaderProgram.setFloat("_globalK", globalK);
            //shaderProgram.setFloat("z_shift", -0.34f);
            shaderProgram.setVec3f("partColorModifier", new Vector3f(renderEntity.getColorR(), renderEntity.getColorG(), renderEntity.getColorB()));
        }
    };
    private final List<DirtCuboid> PARTS = ImmutableList.of(HEAD, BODY, ARM_LEFT, ARM_RIGHT, LEG_LEFT, LEG_RIGHT);

    private abstract static class DirtCuboid {
        public String name;

        public DirtCuboid(String name) {
            this.name = name;
        }

        public abstract void setupShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity);
    }
}
