package com.fazziclay.openoptimizemc.config;

import com.fazziclay.openoptimizemc.OpenOptimizeMc;
import com.fazziclay.openoptimizemc.UpdateChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.function.BooleanSupplier;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;

    private ButtonWidget update;
    private boolean updateAvailable = false;

    public ConfigScreen(Screen parent) {
        super(Text.of("OpenOptimizeMC"));
        this.parent = parent;
        this.config = OpenOptimizeMc.getConfig();
        this.client = MinecraftClient.getInstance();
    }

    private void addButton(int x, int y, int w, int h, String text, BooleanSupplier o, BooleanSupplier o1, String... tooltip) {
        ButtonWidget button = ButtonWidget.builder(Text.of(text), ignore -> {
            stateButton(ignore, text, o1.getAsBoolean());
        }).size(w, h).position(x, y).tooltip(tooltip.length == 0 ? null : Tooltip.of(Text.of(tooltip[0]))).build();
        stateButton(button, text, o.getAsBoolean());
        addSelectableChild(button);
        addDrawable(button);
    }

    @Override
    protected void init() {
        ButtonWidget cancel = ButtonWidget.builder(Text.of("Close"), button -> this.close()).position(width - 60, height - 30).size(50, 20).build();
        addSelectableChild(cancel);
        addDrawable(cancel);

        addButton(10, 20, 110, 20, "Render world", config::isRenderLevel, config::toggleRenderLevel, "Render chunks. §c(Not use this as X-ray in servers!)§r");
        addButton(130, 20, 110, 20, "Render entities", config::isRenderEntities, config::toggleRenderEntities, "Fully control entities rendering (include shadow)");
        addButton(250, 20, 150, 20, "Render block-entities", config::isRenderBlockEntities, config::toggleRenderBlockEntities, "Render block-entities (signs, chests, bell...)");
        addButton(410, 20, 150, 20, "Update chunks", config::isUpdateChunks, config::toggleUpdateChunks, "Update chunks states");

        addButton(10, 70, 100, 20, "Render players", config::isRenderPlayers, config::toggleRenderPlayers, "Enable/Disable fully players rendering (no shadow control)");
        addButton(120, 70, 130, 20, "Players only-head", config::isPlayersOnlyHeads, config::togglePlayersOnlyHeads, "Render only head of §8minecraft:player");
        addButton(260, 70, 130, 20, "Players ModelPose", config::isPlayersModelPose, config::togglePlayersModelPose);
        addButton(400, 70, 130, 20, "Primitive players", config::isPlayersPrimitive, config::togglePlayersPrimitive);

        addButton(10, 95, 130, 20, "Render Armor", config::isRenderArmor, config::toggleRenderArmor, "Control armor rendering (25% of §8minecraft:player§r performance)");
        addButton(150, 95, 130, 20, "HeldFeature", config::isHeldItemFeature, config::toggleHeldItemFeature, "Control rendering item on hand");
        addButton(290, 95, 130, 20, "shouldRender(): true", config::isEntityAlwaysShouldRender, config::toggleEntityAlwaysShouldRender, "Always return true for EntityRenderDispatcher::shouldRender");
        addButton(430, 95, 145, 20, "cache hasEnchantments", config::isCacheItemStackEnchantments, config::toggleCacheItemStackEnchantments, "Cache `hasEnchantments` for ItemStack");

        addButton(10, height - 30, 200, 20, "Advanced Profiler [Shift + F3]", config::isAdvancedProfiler, config::toggleAdvancedProfiler, "Advanced call push() and pop() for more performance information (Debug Pie in Shift+F3 menu)");
        addButton(220, height - 30, 200, 20, "§2Automatic", config::isAIBehavior, config::toggleAIBehavior, "§aAutomatic manage all mod features by current performance");


        update =  ButtonWidget.builder(Text.of("§c(!)§a Update Available! (click to open URL)"), button -> {
            MinecraftClient.getInstance().setScreen(new ConfirmLinkScreen(b -> {
                if (b) {
                    Util.getOperatingSystem().open("https://fazziclay.github.io/openoptimizemc");
                } else {
                    MinecraftClient.getInstance().setScreen(ConfigScreen.this);
                }
            }, "https://fazziclay.github.io/openoptimizemc", true));
        }).position(10, height - 60).size(200, 20).build();
        update.visible = false;
        addSelectableChild(update);
        addDrawable(update);

        UpdateChecker.check((build, name, pageUrl) -> {
            update.visible = true;
            updateAvailable = true;
        });
    }

    private void stateButton(ButtonWidget button, String s, boolean b) {
        button.setMessage(Text.of(s + ": " + (b ? "§aON" : "§cOFF")));
    }

    int i = 0;
    @Override
    public void tick() {
        if (updateAvailable) {
            i++;
            update.visible = i % 30 != 0;
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
