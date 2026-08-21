package com.fxw.blizzardvilladisplay.client.rendering.screens;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2fStack;

public class CustomTextureScreenTest extends Screen {
    public CustomTextureScreenTest(Component component) {
        super(component);
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/test.png");
    private boolean closing = false;
    private long animationStart;
    private long animationDuration = 250;
    private static final long type_delay = 0L;
    private static final long typing_duration = 1000L;
    private static final long wait_duration = 400L;
    private final String fullText = "这是一段测试文字，为了检测长文本会怎么摆放我特地写了很长长长长长长长长长长长长长长长长长长长的文字";
    private long textStartTime = -1L;
    private CustomTextureWidgetTest widgetTest;
    @Override
    public void onClose() {
        startClosing();
    }
    @Override
    public void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        animationStart = Util.getMillis();
        closing = false;
        textStartTime = -1L;
        int widgetRenderHeight = (int) (this.height * 0.5 * 0.2);
        int widgetRenderWidth = widgetRenderHeight * 2;
        widgetTest = new CustomTextureWidgetTest(centerX, centerY + this.height / 10, widgetRenderWidth, widgetRenderHeight, Component.literal("test_widget"));
        widgetTest.active = false;
        widgetTest.visible = false;
        addRenderableWidget(widgetTest);
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
        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();
        int textureWidth = 1280;
        int textureHeight = 720;
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int renderHeight = (int) (screenHeight * 0.5f);
        int renderWidth = (int) (renderHeight * (16.0f / 9.0f));
        long now = Util.getMillis();
        long elapsed = now - animationStart;
        float progress = Math.min(1.0f, (float) elapsed / animationDuration);
        if(closing && progress >= 1.0f) {
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
                TEXTURE,
                - renderWidth / 2, - renderHeight / 2,
                0, 0,
                renderWidth, renderHeight,
                textureWidth,textureHeight,
                textureWidth, textureHeight,
                argbColor
        );
        matrices.popMatrix();

        if (!closing && progress >= 1.0f && textStartTime == -1L) {
            this.textStartTime = now;
        }
        if (textStartTime == -1L || now < textStartTime + type_delay) {
            super.render(context, mouseX, mouseY, delta);
            return;
        }
        long textElapsed = now - (textStartTime + type_delay);
        String textToDraw;
        if (closing) {
            textToDraw = "";
        } else if (textElapsed < typing_duration) {
            float typeProgress = (float) textElapsed / typing_duration;
            int charCount = (int) (this.fullText.length() * typeProgress);
            charCount = Math.clamp(charCount, 0, this.fullText.length());
            textToDraw = this.fullText.substring(0, charCount);
        } else {
            textToDraw = this.fullText;
        }
        context.drawCenteredString(this.font, textToDraw, centerX, centerY, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);

        long widgetStartTime = typing_duration + wait_duration + type_delay;
        if (elapsed < widgetStartTime) {
            widgetTest.visible = false;
            widgetTest.active = false;
        } else {
            widgetTest.visible = true;
            long widgetElapsed = elapsed - widgetStartTime;
            float widgetProgress = Math.min(1.0f, (float) widgetElapsed / animationDuration);
            float easedWidget = 1.0f - (1.0f - widgetProgress) * (1.0f - widgetProgress) * (1.0f - widgetProgress);
            float widgetScale = 0.85f + 0.15f * easedWidget;
            float widgetAlpha = Math.clamp(widgetProgress, 0.0f, 1.0f);
            if (widgetProgress >= 1.0f) {
                widgetTest.active = true;
            }
            widgetTest.renderAnimatedWidget(context, widgetScale, widgetAlpha);
        }
    }
}
