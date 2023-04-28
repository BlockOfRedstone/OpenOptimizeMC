package com.fazziclay.openoptimizemc.experemental;

import com.fazziclay.dirtrenderer.rendering.opengl.*;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.util.Debug;
import com.fazziclay.openoptimizemc.util.OP;
import com.fazziclay.openoptimizemc.util.ResourcesUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ExperimentalRenderer {

    public static final ExperimentalRenderer INSTANCE = new ExperimentalRenderer();
    private static float[] positions_colors2 = {
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


    private static int[] indices = {
            0,   1,  2,  2,  3,  0, // front
            4,   5,  6,  6,  7,  4, // back
            8,   9, 10, 10, 11,  8, // right
            12, 13, 14, 14, 15, 12, // left
            16, 17, 18, 18, 19, 16, // top
            20, 21, 22, 22, 23, 20  // bottom
    };

    private boolean initialized = false;
    private VAO vao;
    private VAO chunchVao;
    private IndexBuffer indexBuffer;
    private ShaderProgram headShaderProgram;
    private ShaderProgram bodyShaderProgram;
    private ShaderProgram armsShaderProgram;
    private ShaderProgram legsShaderProgram;
    private final HashMap<Entity, RenderEntity> entityList = new HashMap<>();

    private float globalK;
    private long lastCleanup = 0;

    public void init() {
        if (initialized) {
            OpenOptimizeMc.LOGGER.info("WARNING! Double init called!!");
            headShaderProgram = program("head");
            return;
        }
        initialized = true;
        OP.push("ExperimentalRenderer:init");
        OpenOptimizeMc.LOGGER.info("Experimental initialized!");

        headShaderProgram = program("head");
        bodyShaderProgram = program("body");
        armsShaderProgram = program("arms");
        legsShaderProgram = program("legs");

        Obj obj;
        try {
            obj = ObjUtils.convertToRenderable(ObjReader.read(ResourcesUtil.getInputStream("assets/openoptimizemc/obj/base_human.obj")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //positions_colors2 = ObjData.getVerticesArray(obj);
        //indices = ObjData.getFaceVertexIndicesArray(obj);

        indexBuffer = new IndexBuffer(indices);
        VBO cube_vbo = new VBO(positions_colors2, BufferLayout.create(BufferLayout.ShaderDataType.FLOAT3)); // , BufferLayout.ShaderDataType.FLOAT3, BufferLayout.ShaderDataType.FLOAT2
        vao = new VAO();
        vao.addVertexBuffer(cube_vbo);
        vao.setIndexBuffer(indexBuffer);

        chunchVao = new VAO();

        if (!OpenOptimizeMc.debug(true)) {
            indices = null;
            positions_colors2 = null;
        }
        OP.pop();
    }

    public void unload(Entity entity) {
        entityList.remove(entity);
    }

    public void load(Entity entity) {
        if (entityList.size() > entity.world.getPlayers().size()) {
            long t = System.currentTimeMillis() - lastCleanup;
            if (t > 30 * 1000) {
                lastCleanup = System.currentTimeMillis();
                entityList.clear();
            }
        }
        entityList.put(entity, new RenderEntity(entity));
        Debug.setExperimentalRendererEntityListCount(entityList.size());

        globalK = (float) (((double)System.currentTimeMillis() % 10000L) / 10L);
        Debug.setExperimentalRendererGlobalK(globalK);
        if (!initialized) {
            init();
            initialized = true;
        }
    }

    public void render(AbstractClientPlayerEntity player, MatrixStack matrices) {
        OP.push("ExperimentalRenderer:render");

        OP.push("check init");
        if (!initialized) {
            init();
            initialized = true;
        }

        OP.swap("findings");
        RenderEntity renderEntity = entityList.get(player);
        if (renderEntity == null) {
            renderEntity = new RenderEntity(player);
            entityList.put(player, renderEntity);
        }

        OP.swap("Misc");
        renderEntity.renderTick(player);
        if (player.isInvisible()) return;
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();


        OP.swap("vao bind");
        vao.bind();
        OP.swap("indexBuffer bind");
        indexBuffer.bind();

        OP.swap("viewMatrix mul(*)");
        Matrix4f viewMatrix = new Matrix4f(matrices.peek().getPositionMatrix()).mul(RenderSystem.getModelViewMatrix());

        OP.swap("RenderSystem.getProjectionMatrix");
        Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();

        OP.swap("for cycle");
        for (DirtCuboid part : PARTS) { // TODO: 4/28/23 fix
            OP.push("Part " + part.name);
            if (player.isSpectator() && part != HEAD) return;

            OP.push("getShaderProgram");
            ShaderProgram currentShaderProgram = part.getShaderProgram(player, renderEntity);
            OP.swap("set ShaderProgram mat4");
            currentShaderProgram.setMat4f("view_matrix", viewMatrix);
            currentShaderProgram.setMat4f("projection_matrix", projectionMatrix);
            OP.pop();

            OP.swap("Part " + part.name + " glDrawElements");
            glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, NULL);
            OP.pop();
        }
        OP.swap("unbinds");


        BufferRenderer.reset(); //VAO.unbind() functional in minecraft-system
        IndexBuffer.unbind();
        ShaderProgram.unbind();

        OP.swap("Crunch vbo bind");
        //chunchVao.bind();
        //GameRenderer.getPositionColorProgram().bind();
        OP.pop();
        OP.pop();
    }

    private void setModelMatrix(ShaderProgram program, Matrix4f mat) {
        //mat.scale(0.05f);
        //mat.translate(0, -0.6f, 0);
        //mat.rotateY((float) Math.PI);
        //mat.scale(0.125f);
        //mat.scale(1, 0.4f, 1);
        program.setMat4f("model_matrix", mat);
    }


    private ShaderProgram program(String name) {
        OpenOptimizeMc.LOGGER.info("ShaderProgram '"+name+"' created!");
        ShaderProgram shaderProgram = new ShaderProgram(ResourcesUtil.getText("assets/openoptimizemc/shaders/"+name+".vert"), ResourcesUtil.getText("assets/openoptimizemc/shaders/"+name+".frag"));
        if (!shaderProgram.isCompile()) {
            OpenOptimizeMc.LOGGER.info("Experimental shader name='"+name+"' not compiled!\nShader log:\n" + shaderProgram.getCompileLog());
        }
        return shaderProgram;
    }

    private final DirtCuboid HEAD = new DirtCuboid("head") { // HEAD
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            headShaderProgram.bind();
            setModelMatrix(headShaderProgram, renderEntity.getHeadModelMatrix());
            headShaderProgram.setFloat("yaw", player.getYaw());
            headShaderProgram.setFloat("pitch", player.getPitch());
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return headShaderProgram;
        }
    };

    private final DirtCuboid BODY = new DirtCuboid("body") { // BODY
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            bodyShaderProgram.bind();
            setModelMatrix(bodyShaderProgram, renderEntity.getBodyModelMatrix());
            bodyShaderProgram.setFloat("bodyYaw", player.bodyYaw);
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return bodyShaderProgram;
        }
    };

    private final DirtCuboid LEG_LEFT = new DirtCuboid("left_leg") { // LEFT LEG
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            legsShaderProgram.bind();
            setModelMatrix(legsShaderProgram, renderEntity.getLeftLegModelMatrix());
            legsShaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            legsShaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            legsShaderProgram.setFloat("_globalK", globalK);
            legsShaderProgram.setFloat("z_shift", 0.14f);
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return legsShaderProgram;
        }
    };

    private final DirtCuboid LEG_RIGHT = new DirtCuboid("right_leg") { // RIGHT LEG
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            legsShaderProgram.bind();
            setModelMatrix(legsShaderProgram, renderEntity.getRightLegModelMatrix());
            legsShaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            legsShaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            legsShaderProgram.setFloat("_globalK", globalK);
            legsShaderProgram.setFloat("z_shift", -0.14f);
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return legsShaderProgram;
        }
    };

    private final DirtCuboid ARM_LEFT = new DirtCuboid("left_arm") { // LEFT ARM
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            armsShaderProgram.bind();
            setModelMatrix(armsShaderProgram, renderEntity.getLeftArmModelMatrix());
            armsShaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            armsShaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            armsShaderProgram.setFloat("_globalK", globalK);
            armsShaderProgram.setFloat("z_shift", 0.34f);
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return armsShaderProgram;
        }
    };

    private final DirtCuboid ARM_RIGHT = new DirtCuboid("right_arm") { // RIGHT ARM
        @Override
        public ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity) {
            armsShaderProgram.bind();
            setModelMatrix(armsShaderProgram, renderEntity.getRightArmModelMatrix());
            armsShaderProgram.setFloat("limb_pos", player.limbAnimator.getPos());
            armsShaderProgram.setFloat("limb_speed", player.limbAnimator.getSpeed());
            armsShaderProgram.setFloat("_globalK", globalK);
            armsShaderProgram.setFloat("z_shift", -0.34f);
            headShaderProgram.setFloat("entityColorR", renderEntity.getColorR());
            headShaderProgram.setFloat("entityColorG", renderEntity.getColorG());
            headShaderProgram.setFloat("entityColorB", renderEntity.getColorB());
            return armsShaderProgram;
        }
    };

    private final List<DirtCuboid> PARTS = ImmutableList.of(HEAD, BODY, ARM_LEFT, ARM_RIGHT, LEG_LEFT, LEG_RIGHT);

    private abstract static class DirtCuboid {
        public String name;

        public DirtCuboid(String name) {
            this.name = name;
        }

        public abstract ShaderProgram getShaderProgram(AbstractClientPlayerEntity player, RenderEntity renderEntity);
    }
}
