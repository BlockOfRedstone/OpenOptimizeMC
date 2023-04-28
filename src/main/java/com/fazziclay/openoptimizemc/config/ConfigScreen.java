package com.fazziclay.openoptimizemc.config;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.experemental.ExperimentalRenderer;
import com.fazziclay.openoptimizemc.util.Debug;
import com.fazziclay.openoptimizemc.util.UpdateChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ConfigScreen extends Screen {
    private static final boolean DEBUG_BUTTONS = OpenOptimizeMc.debug(true);
    private static final boolean DEBUG_TEXT = OpenOptimizeMc.debug(true);
    private final Screen parent;
    private final Config config;
    private final List<ButtonShape> buttons = new ArrayList<>();

    private ButtonWidget update;

    public ConfigScreen(Screen parent) {
        super(Text.of("OpenOptimizeMC"));
        this.parent = parent;
        this.config = OpenOptimizeMc.getConfig();
        this.client = MinecraftClient.getInstance();
    }

    @Override
    protected void init() {
        ButtonWidget cancel = ButtonWidget.builder(Text.translatable("openoptimizemc.close"), button -> this.close()).position(width - 60, height - 30).size(50, 20).build();
        addSelectableChild(cancel);
        addDrawable(cancel);

        addButton(10, 20, 65, 20, switchButton("feature.renderWorld.button", "feature.renderWorld.tooltip", config::isRenderLevel, config::toggleRenderLevel, null, () -> true));
        addButton(80, 20, 80, 20, switchButton("feature.renderEntities.button", "feature.renderEntities.tooltip", config::isRenderEntities, config::toggleRenderEntities, this::updateButtons, () -> true));
        addButton(165, 20, 95, 20, switchButton("feature.renderBlockEntities.button", "feature.renderBlockEntities.tooltip", config::isRenderBlockEntities, config::toggleRenderBlockEntities, null, () -> true));
        addButton(265, 20, 95, 20, switchButton("feature.chunksUpdates.button", "feature.chunksUpdates.tooltip", config::isUpdateChunks, config::toggleUpdateChunks, null, () -> true));

        addButton(10, 70, 70, 20, switchButton("feature.renderPlayers.button", "feature.renderPlayers.tooltip", config::isRenderPlayers, config::toggleRenderPlayers, this::updateButtons, config::isRenderEntities));
        addButton(85, 70, 105, 20, switchButton("feature.renderPlayersOnlyHeads.button", "feature.renderPlayersOnlyHeads.tooltip", config::isPlayersOnlyHeads, config::togglePlayersOnlyHeads, this::updateButtons, () -> (config.isRenderEntities() && config.isRenderPlayers())));
        addButton(195, 70, 105, 20, switchButton("feature.notApplyFeaturesForSelfPlayer.button", "feature.notApplyFeaturesForSelfPlayer.tooltip", config::isNotApplyFeaturesForSelfPlayer, config::toggleNotApplyFeaturesForSelfPlayer, null, () -> true));
        addButton(305, 70, 120, 20, getRendererButtonShape());

        addButton(10, 95, 80, 20, switchButton("feature.renderArmor.button", "feature.renderArmor.tooltip", config::isRenderArmor, config::toggleRenderArmor, null, () -> (config.isRenderEntities() && config.getRenderer() == RendererType.VANILLA)));
        addButton(95, 95, 75, 20, switchButton("feature.heldItem.button", "feature.heldItem.tooltip", config::isHeldItemFeature, config::toggleHeldItemFeature, null, () -> (config.isRenderEntities() && config.getRenderer() == RendererType.VANILLA)));
        addButton(175, 95, 75, 20, switchButton("feature.entityAlwaysRender.button", "feature.entityAlwaysRender.tooltip", config::isEntityAlwaysShouldRender, config::toggleEntityAlwaysShouldRender, null, config::isRenderEntities));
        addButton(260, 95, 80, 20, switchButton("feature.cacheItemStackEnchantments.button", "feature.cacheItemStackEnchantments.tooltip", config::isCacheItemStackEnchantments, config::toggleCacheItemStackEnchantments, null, () -> true));

        addButton(10, height - 30, 100, 20, switchButton("feature.advancedDebugProfiler.button", "feature.advancedDebugProfiler.tooltip", config::isAdvancedProfiler, config::toggleAdvancedProfiler, null, () -> true));
        addButton(120, height - 30, 100, 20, switchButton("feature.openoptimizemc.automatic.button", "feature.openoptimizemc.automatic.tooltip", config::isAIBehavior, config::toggleAIBehavior, null, () -> true));
        if (DEBUG_BUTTONS) {
            addButton(230, height - 30, 100, 20, staticButton("RELOAD SHADER", "RELAOD ExperimentalRenderer shader", ExperimentalRenderer.INSTANCE::init, null, () -> true));
            addButton(335, height - 30, 100, 20, staticButton("RELOAD RES", "Like as F3+T", () -> {
                MinecraftClient.getInstance().reloadResources();
            }, null, () -> true));
        }


        update = ButtonWidget.builder(Text.translatable("openoptimizemc.updateAvailable.button"), button -> {
            MinecraftClient.getInstance().setScreen(new ConfirmLinkScreen(b -> {
                if (b) {
                    Util.getOperatingSystem().open(UpdateChecker.getUpdateURL());
                } else {
                    MinecraftClient.getInstance().setScreen(ConfigScreen.this);
                }
            }, UpdateChecker.getUpdateURL(), true));
        }).position(10, height - 60).size(200, 20).build();
        update.visible = UpdateChecker.isUpdateAvailable();
        addSelectableChild(update);
        addDrawable(update);
    }

    private void addButton(int x, int y, int w, int h, ButtonShape buttonShape) {
        ButtonWidget button = ButtonWidget.builder(buttonShape.getText(), internalButton -> {
            buttonShape.next();
            buttonShape.update();
            buttonShape.toggled();
        }).size(w, h).position(x, y).tooltip(buttonShape.getTooltip()).build();
        buttonShape.setButton(button);
        button.active = buttonShape.isActive();
        addSelectableChild(button);
        addDrawable(button);
        buttons.add(buttonShape);
    }


    private void updateButtons() {
        for (ButtonShape button : buttons) {
            button.update();
        }
    }

    private int i = 0;
    private static final Formatting[] UPDATE_AVAILABLE_FORMATTINGS = {
            Formatting.YELLOW,
            Formatting.RED,
            Formatting.GREEN,
            Formatting.WHITE,
            Formatting.LIGHT_PURPLE
    };
    @Override
    public void tick() {
        if (UpdateChecker.isUpdateAvailable()) {
            i++;
            update.setMessage(Text.translatable("openoptimizemc.updateAvailable.button").formatted(Formatting.BOLD).formatted(UPDATE_AVAILABLE_FORMATTINGS[(i / 10) % (UPDATE_AVAILABLE_FORMATTINGS.length-1)]));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        assert client != null;
        if (client.world == null) drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, Color.WHITE.getRGB());
        if (DEBUG_TEXT) {
            Debug.renderText();
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    private Tooltip getButtonTooltip(String buttonTranslatable, boolean active, boolean vanilla, String tooltipTranslatable) {
        Text tooltip = Text.translatable("openoptimizemc.config.tooltip.composed", Text.translatable(buttonTranslatable), Text.translatable(active ? "openoptimizemc.on.colored" : "openoptimizemc.off.colored"), Text.translatable(vanilla ? "openoptimizemc.on" : "openoptimizemc.off"), Text.translatable(tooltipTranslatable));
        return Tooltip.of(tooltip);
    }

    private abstract static class ButtonShape {
        private ButtonWidget button;

        abstract Text getText();
        abstract void next();
        abstract Tooltip getTooltip();

        void toggled() {}

        public boolean isActive() {
            return true;
        }

        public void setButton(ButtonWidget button) {
            this.button = button;
        }

        public void update() {
            if (button != null) {
                button.setTooltip(this.getTooltip());
                button.setMessage(this.getText());
                button.active = this.isActive();
            }
        }
    }

    private ButtonShape switchButton(String textTranslate, String tooltipTranslate, BooleanSupplier currentValue, Runnable toggle, Runnable post, BooleanSupplier active) {
        return new ButtonShape() {
            @Override
            public Text getText() {
                return Text.translatable(currentValue.getAsBoolean() ? "openoptimizemc.on.composed.colored" : "openoptimizemc.off.composed.colored", Text.translatable(textTranslate));
            }

            @Override
            public void next() {
                toggle.run();
            }

            @Override
            public Tooltip getTooltip() {
                return getButtonTooltip(textTranslate, currentValue.getAsBoolean(), true, tooltipTranslate);
            }

            @Override
            void toggled() {
                if (post != null) post.run();
            }

            @Override
            public boolean isActive() {
                return active.getAsBoolean();
            }
        };
    }

    private ButtonShape staticButton(String textTranslate, String tooltipTranslate, Runnable toggle, Runnable post, BooleanSupplier active) {
        return new ButtonShape() {
            @Override
            public Text getText() {
                return Text.translatable(textTranslate);
            }

            @Override
            public void next() {
                toggle.run();
            }

            @Override
            public Tooltip getTooltip() {
                return Tooltip.of(Text.translatable(tooltipTranslate));
            }

            @Override
            void toggled() {
                if (post != null) post.run();
            }

            @Override
            public boolean isActive() {
                return active.getAsBoolean();
            }
        };
    }

    private ButtonShape getRendererButtonShape() {
        return new ButtonShape() {
            @Override
            Text getText() {
                return Text.translatable("openoptimizemc.custom.composed", Text.translatable("feature.playerRenderer.button"), Text.translatable(config.getRenderer().getName()));
            }

            @Override
            void next() {
                config.toggleRenderer();
            }

            @Override
            Tooltip getTooltip() {
                Text text = MutableText.of(TextContent.EMPTY).append(getText()).append("\n\n").append(Text.translatable(config.getRenderer().getDescription()));
                return Tooltip.of(text);
            }

            @Override
            public boolean isActive() {
                return config.isRenderEntities() && config.isRenderPlayers() && !config.isPlayersOnlyHeads();
            }

            @Override
            void toggled() {
                updateButtons();
            }
        };
    }

}
