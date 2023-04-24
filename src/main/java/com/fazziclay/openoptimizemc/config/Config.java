package com.fazziclay.openoptimizemc.config;

import com.fazziclay.openoptimizemc.OP;
import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.behavior.BehaviorType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Config {
    private transient File file;
    private transient Gson gson;

    @SerializedName("renderPlayers")
    private boolean renderPlayers = true;
    @SerializedName("playersOnlyHeads")
    private boolean playersOnlyHeads = false;
    @SerializedName("playersModelPose")
    private boolean playersModelPose = true;
    @SerializedName("renderEntities")
    private boolean renderEntities = true;

    @SerializedName("renderLevel")
    private boolean renderLevel = true;

    @SerializedName("advancedProfilerForGameRendererEntities")
    private boolean advancedProfiler = false;
    @SerializedName("renderBlockEntities")
    private boolean renderBlockEntities = true;
    @SerializedName("renderArmor")

    private boolean renderArmor = true;
    @SerializedName("heldItemFeature")

    private boolean heldItemFeature = true;
    @SerializedName("entityAlwaysShouldRender")
    private boolean isEntityAlwaysShouldRender = false;
    @SerializedName("cacheItemStackEnchantments")

    private boolean isCacheItemStackEnchantments = true;
    @SerializedName("playersPrimitive")
    private boolean isPlayersPrimitive = false;
    @SerializedName("AIBehavior")
    private boolean isAIBehavior = true;
    @SerializedName("updateChunks")
    private boolean updateChunks;


    public void save() {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(gson.toJson(this, Config.class));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save OpenOptimizeMC config", e);
        }
    }

    public static Config load(File file) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!file.exists()) return new Config().setFile(file).setGson(gson);
        try {
            String jsonText = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Config config = gson.fromJson(jsonText, Config.class);
            config.file = file;
            config.gson = gson;
            return config;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load OpenOptimizeMC config", e);
        }
    }

    private Config setGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    private Config setFile(File file) {
        this.file = file;
        return this;
    }


    public boolean isUpdateChunks() {
        return updateChunks;
    }
    public void setUpdateChunks(boolean updateChunks) {
        this.updateChunks = updateChunks;
        save();
    }
    public boolean toggleUpdateChunks() {
        updateChunks = !updateChunks;
        save();
        return updateChunks;
    }


    // IS RENDER PLAYERS (personal for minecraft:player)
    public boolean isRenderPlayers() {
        return renderPlayers;
    }
    public void setRenderPlayers(boolean renderPlayers) {
        this.renderPlayers = renderPlayers;
        save();
    }
    public boolean toggleRenderPlayers() {
        renderPlayers = !renderPlayers;
        save();
        return renderPlayers;
    }



    // PLAYERS RENDER ONLY HEADS
    public boolean isPlayersOnlyHeads() {
        return playersOnlyHeads;
    }
    public void setPlayersOnlyHeads(boolean b) {
        playersOnlyHeads = b;
        save();
    }
    public boolean togglePlayersOnlyHeads() {
        playersOnlyHeads = !playersOnlyHeads;
        save();
        return playersOnlyHeads;
    }



    // PLAYERS MODEL POSE
    public boolean isPlayersModelPose() {return playersModelPose;}
    public void setPlayersModelPose(boolean playersModelPose) {
        this.playersModelPose = playersModelPose;
        save();
    }
    public boolean togglePlayersModelPose() {
        playersModelPose = !playersModelPose;
        save();
        return playersModelPose;
    }



    // RENDER ENTITIES
    public boolean isRenderEntities() {return renderEntities;}
    public void setRenderEntities(boolean renderEntities) {
        this.renderEntities = renderEntities;
        save();
    }
    public boolean toggleRenderEntities() {
        renderEntities = !renderEntities;
        save();
        return renderEntities;
    }



    // IS RENDER LEVEL (WORLD, MAP)
    public boolean isRenderLevel() {return renderLevel;}
    public void setRenderLevel(boolean b) {
        this.renderLevel = b;
        save();
    }
    public boolean toggleRenderLevel() {
        renderLevel = !renderLevel;
        save();
        return renderLevel;
    }



    // ADVANCED PROFILER
    public boolean isAdvancedProfiler() {return advancedProfiler;}
    public void setAdvancedProfiler(boolean b) {
        this.advancedProfiler = b;
        OP.setEnabled(b);
        save();
    }
    public boolean toggleAdvancedProfiler() {
        advancedProfiler = !advancedProfiler;
        OP.setEnabled(advancedProfiler);
        save();
        return advancedProfiler;
    }



    // RENDER BLOCK ENTITIES
    public boolean isRenderBlockEntities() {return renderBlockEntities;}
    public void setRenderBlockEntities(boolean b) {
        this.renderBlockEntities = b;
        save();
    }
    public boolean toggleRenderBlockEntities() {
        renderBlockEntities = !renderBlockEntities;
        save();
        return renderBlockEntities;
    }

    // IS RENDER ARMOR
    public boolean isRenderArmor() {
        return renderArmor;
    }

    public void setRenderArmor(boolean renderArmor) {
        this.renderArmor = renderArmor;
        save();
    }

    public boolean toggleRenderArmor() {
        renderArmor = !renderArmor;
        save();
        return renderArmor;
    }

    public boolean isHeldItemFeature() {
        return heldItemFeature;
    }

    public void setHeldItemFeature(boolean heldItemFeature) {
        this.heldItemFeature = heldItemFeature;
    }

    public boolean toggleHeldItemFeature() {
        heldItemFeature = !heldItemFeature;
        save();
        return heldItemFeature;
    }

    public boolean isEntityAlwaysShouldRender() {
        return isEntityAlwaysShouldRender;
    }

    public void setEntityAlwaysShouldRender(boolean b) {
        isEntityAlwaysShouldRender = b;
        save();
    }

    public boolean toggleEntityAlwaysShouldRender() {
        isEntityAlwaysShouldRender = !isEntityAlwaysShouldRender;
        save();
        return isEntityAlwaysShouldRender;
    }

    public boolean isCacheItemStackEnchantments() {
        return isCacheItemStackEnchantments;
    }

    public void setCacheItemStackEnchantments(boolean b) {
        isCacheItemStackEnchantments = b;
        save();
    }

    public boolean toggleCacheItemStackEnchantments() {
        isCacheItemStackEnchantments = !isCacheItemStackEnchantments;
        save();
        return isCacheItemStackEnchantments;
    }

    public boolean isPlayersPrimitive() {
        return isPlayersPrimitive;
    }

    public void setPlayersPrimitive(boolean playersPrimitive) {
        isPlayersPrimitive = playersPrimitive;
        save();
    }

    public boolean togglePlayersPrimitive() {
        isPlayersPrimitive = !isPlayersPrimitive;
        save();
        return isPlayersPrimitive;
    }

    public boolean isAIBehavior() {
        return isAIBehavior;
    }

    public void setAIBehavior(boolean b) {
        isAIBehavior = b;
        save();
    }

    public boolean toggleAIBehavior() {
        isAIBehavior = !isAIBehavior;
        OpenOptimizeMc.getBehaviorManager().setBehaviorType(isAIBehavior ? BehaviorType.AI_AUTOMATIC : BehaviorType.CONFIG_DIRECTLY);
        save();
        return isAIBehavior;
    }
}
