//package com.fxw.blizzardvilladisplay.client.rendering;
//
//import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
//import net.fabricmc.api.ClientModInitializer;
//import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
//import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//import org.joml.Matrix3x2fStack;
//
//public class BasicRendering implements ClientModInitializer {
//    public float totalTickProgress = 0F;
//    @Override
//    public void onInitializeClient() {
//        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "last_element"), hudLayer());
//    }
//
//    private HudElement hudLayer() {
//        return (drawContext, tickCounter) -> {
//            if (false) {
//                return;
//            }
//            Matrix3x2fStack matrices = drawContext.pose();
//            totalTickProgress += tickCounter.getGameTimeDeltaPartialTick(true);
//            matrices.pushMatrix();
//            float scaleAmount = Mth.sin(totalTickProgress / 10F) / 2F + 1.5F;
//            matrices.scale(scaleAmount, scaleAmount);
//            //matrices.scale(1 / scaleAmount, 1 / scaleAmount);
//            matrices.translate(60f, 60f);
//
//            float rotationAmount = totalTickProgress / 50F % 360;
//            matrices.rotate(rotationAmount);
//            matrices.translate(-20f, -40f);
//
//            drawContext.fillGradient(5, 20, 35, 60, 0xFF414141, 0xFF000000);
//            matrices.popMatrix();
//        };
//    }
//}
