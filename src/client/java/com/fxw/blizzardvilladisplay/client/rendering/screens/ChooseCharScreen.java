package com.fxw.blizzardvilladisplay.client.rendering.screens;

import com.fxw.blizzardvilladisplay.networking.payload.ChooseCharConfrimC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public class ChooseCharScreen extends Screen {
    public ChooseCharScreen (ChooseCharConfig config) {
        super(Component.empty());
        this.config = config;
    }
    private ChooseCharConfig config;
    private boolean closing = false;
    private long animationStart;
    private long animationDuration = 200;
    private ChooseCharWidget widgetConfirm;
    private ChooseCharWidget widgetCancel;
    @Override
    public void onClose() {
        startClosing();
    }
    @Override
    public void init() {
        int widgetXConfirm = (int) (this.width * 0.5 - this.width * 0.5 * 0.34);
        int widgetXCancel = (int) (this.width * 0.5 + this.width * 0.5 * 0.15);
        int widgetY = (int) (this.height * 0.5 + this.height * 0.5 * 0.21);
        animationStart = Util.getMillis();
        closing = false;
        int renderWidgetHeight = (int) (this.height * 0.5 * 0.175);
        int renderWidgetWidth = (int) (renderWidgetHeight * 256 / 126);
        widgetConfirm = new ChooseCharWidget(
                widgetXConfirm, widgetY,
                renderWidgetWidth, renderWidgetHeight,
                Component.empty(),
                () -> {
                    ClientPlayNetworking.send(new ChooseCharConfrimC2SPayload(config.charId()));
                    this.startClosing();
                });
        widgetConfirm.visible = false;
        widgetConfirm.active = false;
        addRenderableWidget(widgetConfirm);
        widgetCancel = new ChooseCharWidget(widgetXCancel, widgetY,
                renderWidgetWidth, renderWidgetHeight,
                Component.empty(),
                () -> {
                    this.startClosing();
                });
        widgetCancel.visible = false;
        widgetCancel.active = false;
        addRenderableWidget(widgetCancel);
    }
    @Override
    protected void renderMenuBackground(GuiGraphics context) {
        context.fill(0, 0, this.width, this.height, 0x00000000);
    }
    @Override
    protected void renderBlurredBackground(GuiGraphics guiGraphics) {
        return;
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            if (!closing) {
                startClosing();
            }
            return true;
        } else if (super.keyPressed(keyEvent)) {
            return true;
        } else {
            return super.keyPressed(keyEvent);
        }
    }
    private void startClosing() {
        if (closing) {
            return;
        }
        closing = true;
        long now = Util.getMillis();
        long elapsed = now - animationStart;
        if (elapsed < animationDuration) {
            animationStart = now - (animationDuration - elapsed);
        } else {
            animationStart = now;
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBgTexture(context, config);
        renderConfirmtexture(context, config);
        renderCanceltexture(context, config);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBgTexture(GuiGraphics context, ChooseCharConfig config) {
        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();
        int textureWidth = 1270;
        int textureHeight = 720;
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int renderHeight = (int) (screenHeight * 0.5f);
        int renderWidth = (int) (renderHeight * (127.0f / 72.0f));
        long now = Util.getMillis();
        long elapsed = now - animationStart;
        float progress = Math.min(1.0f, (float) elapsed / animationDuration);
        if (closing && progress >= 1.0f) {
            this.minecraft.setScreen(null);
            return;
        }
        float animationFactor = closing ? (1.0f - progress) : progress;
        float eased = 1.0f - (1.0f - animationFactor) * (1.0f - animationFactor) * (1.0f - animationFactor);
        float scale = 0.85f + 0.15f * eased;
        int alphaInt = (int) (eased * 255);
        int argbColor = 0xFFFFFF | (alphaInt << 24);
        Matrix3x2fStack matrices = context.pose();
        context.pose().pushMatrix();
        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                config.bgTexture(),
                - renderWidth / 2, - renderHeight / 2,
                0, 0,
                renderWidth, renderHeight,
                textureWidth,textureHeight,
                textureWidth, textureHeight,
                argbColor
        );
        matrices.popMatrix();
    }

    private void renderConfirmtexture(GuiGraphics context, ChooseCharConfig config) {
        long now = Util.getMillis();
        long elapsed = now - animationStart;
        long widgetStartTime = animationDuration - 70;
        if (elapsed < widgetStartTime || closing) {
            widgetConfirm.visible = false;
            widgetConfirm.active = false;
        } else {
            widgetConfirm.visible = true;
            long widgetElapsed = elapsed - widgetStartTime;
            float widgetProgress = Math.min(1.0f, (float) widgetElapsed / animationDuration);
            float easedWidget = 1.0f - (1.0f - widgetProgress) * (1.0f - widgetProgress) * (1.0f - widgetProgress);
            float widgetScale = 0.85f + 0.15f * easedWidget;
            float widgetAlpha = Math.clamp(widgetProgress, 0.0f, 1.0f);
            if (widgetProgress >= 1.0f) {
                widgetConfirm.active = true;
            }
            widgetConfirm.renderAnimatedWidget(context, widgetScale, widgetAlpha, config.confirmTexture());
        }
    }

    private void renderCanceltexture(GuiGraphics context, ChooseCharConfig config) {
        long now = Util.getMillis();
        long elapsed = now - animationStart;
        long widgetStartTime = animationDuration - 70 + 100;
        if (elapsed < widgetStartTime || closing) {
            widgetCancel.visible = false;
            widgetCancel.active = false;
        } else {
            widgetCancel.visible = true;
            long widgetElapsed = elapsed - widgetStartTime;
            float widgetProgress = Math.min(1.0f, (float) widgetElapsed / animationDuration);
            float easedWidget = 1.0f - (1.0f - widgetProgress) * (1.0f - widgetProgress) * (1.0f - widgetProgress);
            float widgetScale = 0.85f + 0.15f * easedWidget;
            float widgetAlpha = Math.clamp(widgetProgress, 0.0f, 1.0f);
            if (widgetProgress >= 1.0f) {
                widgetCancel.active = true;
            }
            widgetCancel.renderAnimatedWidget(context, widgetScale, widgetAlpha, config.cancelTexture());
        }
    }
}
