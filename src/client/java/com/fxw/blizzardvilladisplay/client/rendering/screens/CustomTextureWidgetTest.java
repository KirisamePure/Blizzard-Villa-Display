package com.fxw.blizzardvilladisplay.client.rendering.screens;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomTextureWidgetTest extends AbstractWidget {
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/widget_test.png");
    private final int widgetWidth = 400;
    private final int widgetHeight= 200;
    public CustomTextureWidgetTest(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }
    public void renderAnimatedWidget(GuiGraphics guiGraphics, float scale, float animAlpha) {
        if (!this.visible) return;

        float finalAlpha = this.alpha * animAlpha;
        int alphaInt = (int) (finalAlpha * 255);
        int arbgColor = (alphaInt << 24) | 0xFFFFFFFF;

        if (this.isHovered()) {
            arbgColor = (alphaInt << 24) | 0xDDDDDDDD;
        }

        guiGraphics.pose().pushMatrix();

        float centerX = this.getX() + this.width / 2.0f;
        float centerY = this.getY() + this.height / 2.0f;

        guiGraphics.pose().translate(centerX, centerY);
        guiGraphics.pose().scale(scale, scale);

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                -this.width / 2, -this.height / 2,
                0, 0,
                this.width, this.height,
                widgetWidth, widgetHeight,
                widgetWidth, widgetHeight,
                arbgColor
        );

        guiGraphics.pose().popMatrix();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
     return;
    }
}
