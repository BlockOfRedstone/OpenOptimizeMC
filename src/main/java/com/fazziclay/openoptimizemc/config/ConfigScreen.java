package com.fazziclay.openoptimizemc.config;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.experemental.DirtRenderer;
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
    private static final boolean DEBUG_BUTTONS = OpenOptimizeMc.debug(false);
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
        addButton(width - 60, height - 30, 50, 20, staticButton("openoptimizemc.close", null, this::close, null, () -> true));

        addButton(10, 20, 65, 20, switchButton(true, "feature.renderWorld.button", "feature.renderWorld.tooltip", config::isRenderLevel, config::toggleRenderLevel, null, () -> isNotAutomatic()));
        addButton(80, 20, 80, 20, switchButton(true, "feature.renderEntities.button", "feature.renderEntities.tooltip", config::isRenderEntities, config::toggleRenderEntities, this::updateButtons, () -> isNotAutomatic()));
        addButton(165, 20, 95, 20, switchButton(true, "feature.renderBlockEntities.button", "feature.renderBlockEntities.tooltip", config::isRenderBlockEntities, config::toggleRenderBlockEntities, null, () -> isNotAutomatic()));
        addButton(265, 20, 95, 20, switchButton(true, "feature.chunksUpdates.button", "feature.chunksUpdates.tooltip", config::isUpdateChunks, config::toggleUpdateChunks, null, () -> (OpenOptimizeMc.isInWorld() && isNotAutomatic())));

        addButton(10, 70, 70, 20, switchButton(true, "feature.renderPlayers.button", "feature.renderPlayers.tooltip", config::isRenderPlayers, config::toggleRenderPlayers, this::updateButtons, () -> config.isRenderEntities() && isNotAutomatic()));
        addButton(85, 70, 105, 20, switchButton(false, "feature.renderPlayersOnlyHeads.button", "feature.renderPlayersOnlyHeads.tooltip", config::isPlayersOnlyHeads, config::togglePlayersOnlyHeads, this::updateButtons, () -> (isNotAutomatic() && config.isRenderEntities() && config.isRenderPlayers())));
        addButton(195, 70, 105, 20, switchButton(false, "feature.notApplyFeaturesForSelfPlayer.button", "feature.notApplyFeaturesForSelfPlayer.tooltip", config::isNotApplyFeaturesForSelfPlayer, config::toggleNotApplyFeaturesForSelfPlayer, null, () -> isNotAutomatic()));
        addButton(305, 70, 120, 20, getRendererButtonShape());

        addButton(10, 95, 80, 20, switchButton(true, "feature.renderArmor.button", "feature.renderArmor.tooltip", config::isRenderArmor, config::toggleRenderArmor, null, () -> (isNotAutomatic() && config.isRenderEntities() && config.getRenderer() == RendererType.VANILLA)));
        addButton(95, 95, 75, 20, switchButton(true, "feature.heldItem.button", "feature.heldItem.tooltip", config::isHeldItemFeature, config::toggleHeldItemFeature, null, () -> (isNotAutomatic() && config.isRenderEntities() && config.getRenderer() == RendererType.VANILLA)));
        addButton(175, 95, 75, 20, switchButton(false, "feature.entityAlwaysRender.button", "feature.entityAlwaysRender.tooltip", config::isEntityAlwaysShouldRender, config::toggleEntityAlwaysShouldRender, null, () -> isNotAutomatic() && config.isRenderEntities()));
        addButton(260, 95, 80, 20, switchButton(false, "feature.cacheItemStackEnchantments.button", "feature.cacheItemStackEnchantments.tooltip", config::isCacheItemStackEnchantments, config::toggleCacheItemStackEnchantments, null, () -> isNotAutomatic()));

        addButton(10, height - 30, 100, 20, switchButton(false, "feature.advancedDebugProfiler.button", "feature.advancedDebugProfiler.tooltip", config::isAdvancedProfiler, config::toggleAdvancedProfiler, null, () -> true));
        addButton(115, height - 30, 110, 20, switchButton(true,"feature.openoptimizemc.automatic.button", "feature.openoptimizemc.automatic.tooltip", config::isAIBehavior, config::toggleAIBehavior, this::updateButtons, () -> true));
        if (DEBUG_BUTTONS) {
            addButton(230, height - 30, 100, 20, staticButton("_RELOAD_SHADER", "Call DirtRenderer init();\n\nNOT CLICK THIS!!! YOU ARE BREAK MINECRAFT RENDERING", DirtRenderer.INSTANCE::init, null, () -> true));
            addButton(335, height - 30, 100, 20, staticButton("_RELOAD_RES", "Like as F3+T\n\nCall MinecraftClient.getInstance().reloadResources()", () -> MinecraftClient.getInstance().reloadResources(), null, () -> true));
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

    private boolean isNotAutomatic() {
        return !config.isAIBehavior();
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

    private Tooltip getSwitchButtonTooltip(String buttonTranslatable, boolean active, boolean vanilla, String tooltipTranslatable) {
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

    private ButtonShape switchButton(boolean vanilla, String textTranslate, String tooltipTranslate, BooleanSupplier currentValue, Runnable toggle, Runnable post, BooleanSupplier active) {
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
                return getSwitchButtonTooltip(textTranslate, currentValue.getAsBoolean(), vanilla, tooltipTranslate);
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

    private ButtonShape staticButton(String textTranslate, String tooltipTranslate, Runnable click, Runnable post, BooleanSupplier active) {
        return new ButtonShape() {
            @Override
            public Text getText() {
                return Text.translatable(textTranslate);
            }

            @Override
            public void next() {
                click.run();
            }

            @Override
            public Tooltip getTooltip() {
                if (tooltipTranslate == null) return null;
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
                return isNotAutomatic() && config.isRenderEntities() && config.isRenderPlayers() && !config.isPlayersOnlyHeads();
            }

            @Override
            void toggled() {
                updateButtons();
            }
        };
    }

}
