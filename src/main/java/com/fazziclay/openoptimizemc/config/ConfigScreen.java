package com.fazziclay.openoptimizemc.config;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.UpdateChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.function.BooleanSupplier;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;

    private ButtonWidget update;

    public ConfigScreen(Screen parent) {
        super(Text.of("OpenOptimizeMC"));
        this.parent = parent;
        this.config = OpenOptimizeMc.getConfig();
        this.client = MinecraftClient.getInstance();
    }

    private void addButton(int x, int y, int w, int h, String text, BooleanSupplier o, BooleanSupplier o1, String... tooltip) {
        addButton(x, y, w, h, text, o, o1, true, tooltip);
    }

    private void addButton(int x, int y, int w, int h, String text, BooleanSupplier o, BooleanSupplier o1, boolean active, String... tooltip) {
        addButton(x, y, w, h, text, o, o1,null, active, tooltip);
    }

    private void addButton(int x, int y, int w, int h, String text, BooleanSupplier o, BooleanSupplier o1, Runnable after, boolean active, String... tooltip) {
        ButtonWidget button = ButtonWidget.builder(Text.translatable(text), ignore -> {
            stateButton(ignore, text, o1.getAsBoolean());
            if (after != null) after.run();
        }).size(w, h).position(x, y).tooltip(tooltip.length == 0 ? null : Tooltip.of(Text.translatable(tooltip[0]))).build();
        stateButton(button, text, o.getAsBoolean());
        button.active = active;
        addSelectableChild(button);
        addDrawable(button);
    }

    @Override
    protected void init() {
        ButtonWidget cancel = ButtonWidget.builder(Text.translatable("openoptimizemc.close"), button -> this.close()).position(width - 60, height - 30).size(50, 20).build();
        addSelectableChild(cancel);
        addDrawable(cancel);

        addButton(10, 20, 70, 20, "feature.renderWorld.button", config::isRenderLevel, config::toggleRenderLevel, "feature.renderWorld.tooltip");
        addButton(90, 20, 70, 20, "feature.renderEntities.button", config::isRenderEntities, config::toggleRenderEntities, this::recreateScreen, true, "feature.renderEntities.tooltip");
        addButton(170, 20, 90, 20, "feature.renderBlockEntities.button", config::isRenderBlockEntities, config::toggleRenderBlockEntities, "feature.renderBlockEntities.tooltip");
        addButton(270, 20, 90, 20, "feature.chunksUpdates.button", config::isUpdateChunks, config::toggleUpdateChunks, "feature.chunksUpdates.tooltip");

        addButton(10, 70, 70, 20, "feature.renderPlayers.button", config::isRenderPlayers, config::toggleRenderPlayers, this::recreateScreen, (config.isRenderEntities()), "feature.renderPlayers.tooltip");
        addButton(90, 70, 70, 20, "feature.renderPlayersOnlyHeads.button", config::isPlayersOnlyHeads, config::togglePlayersOnlyHeads, (config.isRenderEntities() && config.isRenderPlayers()), "feature.renderPlayersOnlyHeads.tooltip");
        addButton(170, 70, 70, 20, "feature.notApplyFeaturesForSelfPlayer.button", config::isNotApplyFeaturesForSelfPlayer, config::toggleNotApplyFeaturesForSelfPlayer, true, "feature.notApplyFeaturesForSelfPlayer.tooltip");
        addButton(250, 70, 70, 20, "feature.renderPlayersPrimitive.button", config::isPlayersPrimitive, config::togglePlayersPrimitive, (config.isRenderEntities() && config.isRenderPlayers()), "feature.renderPlayersPrimitive.tooltip");

        addButton(10, 95, 80, 20, "feature.renderArmor.button", config::isRenderArmor, config::toggleRenderArmor, (config.isRenderEntities()), "feature.renderArmor.tooltip");
        addButton(100, 95, 70, 20, "feature.heldItem.button", config::isHeldItemFeature, config::toggleHeldItemFeature, (config.isRenderEntities()), "feature.heldItem.tooltip");
        addButton(180, 95, 70, 20, "feature.entityAlwaysRender.button", config::isEntityAlwaysShouldRender, config::toggleEntityAlwaysShouldRender, (config.isRenderEntities()), "feature.entityAlwaysRender.button");
        addButton(260, 95, 80, 20, "feature.cacheItemStackEnchantments.button", config::isCacheItemStackEnchantments, config::toggleCacheItemStackEnchantments, "feature.cacheItemStackEnchantments.tooltip");

        addButton(10, height - 30, 100, 20, "feature.advancedDebugProfiler.button", config::isAdvancedProfiler, config::toggleAdvancedProfiler, "feature.advancedDebugProfiler.tooltip");
        addButton(120, height - 30, 100, 20, "feature.openoptimizemc.automatic.button", config::isAIBehavior, config::toggleAIBehavior, "feature.openoptimizemc.automatic.tooltip");


        update = ButtonWidget.builder(Text.translatable("openoptimizemc.updateAvailable.button"), button -> {
            MinecraftClient.getInstance().setScreen(new ConfirmLinkScreen(b -> {
                if (b) {
                    Util.getOperatingSystem().open(OpenOptimizeMc.getUpdateURL());
                } else {
                    MinecraftClient.getInstance().setScreen(ConfigScreen.this);
                }
            }, OpenOptimizeMc.getUpdateURL(), true));
        }).position(10, height - 60).size(200, 20).build();
        update.visible = OpenOptimizeMc.isUpdateAvailable();
        addSelectableChild(update);
        addDrawable(update);
    }

    private void recreateScreen() {
        client.setScreen(new ConfigScreen(parent));
    }

    private void stateButton(ButtonWidget button, String s, boolean b) {
        if (b) {
            button.setMessage(Text.translatable("openoptimizemc.on.composed", Text.translatable(s)));
        } else {
            button.setMessage(Text.translatable("openoptimizemc.off.composed", Text.translatable(s)));
        }
    }

    int i = 0;
    private static final Formatting[] UPDATE_AVAILABLE_FORMATTINGS = {
            Formatting.YELLOW,
            Formatting.RED,
            Formatting.GREEN,
            Formatting.WHITE,
            Formatting.LIGHT_PURPLE
    };
    @Override
    public void tick() {
        if (OpenOptimizeMc.isUpdateAvailable()) {
            i++;
            update.setMessage(Text.translatable("openoptimizemc.updateAvailable.button").formatted(Formatting.BOLD).formatted(UPDATE_AVAILABLE_FORMATTINGS[(i / 10) % (UPDATE_AVAILABLE_FORMATTINGS.length-1)]));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        assert client != null;
        if (client.world == null) drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, Color.WHITE.getRGB());
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
