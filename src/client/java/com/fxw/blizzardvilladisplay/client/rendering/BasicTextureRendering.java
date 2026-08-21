package com.fxw.blizzardvilladisplay.client.rendering;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2fStack;

public class BasicTextureRendering implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "before_chat"), BasicTextureRendering::render);
    }
    private static void render(GuiGraphics context, DeltaTracker tickCounter) {
        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();
        int textureWidth = 16;
        int textureHeight= 16;
        float centerX = screenWidth / 2F;
        float centerY = screenHeight/ 2F;
        double currTime = Util.getMillis() / 1000.0;
        float rotationAmount = (float) currTime * 2 % 360;
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/deepslate.png");
        matrices.translate(centerX, centerY);
        matrices.rotate(rotationAmount);
        context.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0, 0, 35, 35, 16, 16, textureWidth, textureHeight);
        matrices.popMatrix();
    }
}
