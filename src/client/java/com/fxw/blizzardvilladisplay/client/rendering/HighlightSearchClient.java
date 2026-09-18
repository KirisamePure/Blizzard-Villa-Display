package com.fxw.blizzardvilladisplay.client.rendering;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.networking.payload.HighlightDataS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.RequestHighlightC2SPayload;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class HighlightSearchClient implements ClientModInitializer {
    public KeyMapping highlightKeyMapping;
    KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "highlight_category")
    );
    private static long lastUseTime = 0;
    private static final long COOLDOWN_MS = 10000;

    private static final List<Integer> highlightedEntities = new ArrayList<>();
    private static final List<BlockPos> highlightedBlocks = new ArrayList<>();
    private static int highlightTicksRemaining = 0;
    @Override
    public void onInitializeClient() {
        highlightKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blizzard-villa-display.highlight", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (highlightTicksRemaining > 0) {
                highlightTicksRemaining--;
                if (highlightTicksRemaining == 0) {
                    highlightedEntities.clear();
                    highlightedBlocks.clear();
                }
            }
            while (highlightKeyMapping.consumeClick()) {
                long currTime = Util.getMillis();
                if (currTime - lastUseTime >= COOLDOWN_MS) {
                    lastUseTime = currTime;
                    ClientPlayNetworking.send(new RequestHighlightC2SPayload());
                    client.player.playNotifySound(SoundEvents.ENDER_CHEST_OPEN, SoundSource.MASTER, 1.0f, 1.0f);
                } else {
                    long remainingSeconds = (COOLDOWN_MS - (currTime - lastUseTime)) / 1000 + 1;
                    if (client.player != null) {
                        client.player.displayClientMessage(Component.literal("冷却中,剩余 " + remainingSeconds + " 秒").withColor(0xDC143C), true);
                        client.player.playNotifySound(SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.MASTER, 1.0f, 0.6f);
                    }
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(HighlightDataS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                highlightedEntities.clear();
                highlightedEntities.addAll(payload.entityIds());
                highlightedBlocks.clear();
                highlightedBlocks.addAll(payload.blockPositions());
                highlightTicksRemaining = 120;
            });
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (highlightTicksRemaining <= 0) return;
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            PoseStack poseStack = context.matrices();
            Vec3 cameraPos = context.gameRenderer().getMainCamera().getPosition();
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            MultiBufferSource consumers = context.consumers();
            if (consumers != null) {
                VertexConsumer buffer = consumers.getBuffer(FILLED_THROUGH_WALLS);
                for (BlockPos pos : highlightedBlocks) {
                    AABB box = new AABB(pos).inflate(0.02);
                    drawBoxOutline(poseStack, buffer, box, 1.0f, 0.84f, 0.0f, 1.0f);
                }
                for (Integer id : highlightedEntities) {
                    Entity entity = level.getEntity(id);
                    if (entity != null) {
                        AABB box = entity.getBoundingBox().inflate(0.02);
                        drawBoxOutline(poseStack, buffer, box, 0.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
                if (consumers instanceof MultiBufferSource.BufferSource bufferSource) {
                    bufferSource.endBatch(FILLED_THROUGH_WALLS);
                }
            }
            poseStack.popPose();
        });


    }

//    private static final RenderPipeline FILLED_THROUGH_WALLS_PIPELINE = RenderPipelines.register(RenderPipeline.builder()
//            .withLocation(ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "pipeline/debug_filled_box_through_walls"))
//            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
//            .withCull(false)
//            .build()
//    );

    public static final RenderType FILLED_THROUGH_WALLS = RenderType.create(
            "filled_through_walls",
            1536,
            false,
            false,
            MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL,
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(8.0D)))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .createCompositeState(false)
    );

    private static void drawBoxOutline(PoseStack poseStack, VertexConsumer buffer, AABB box, float r, float g, float b, float a) {
        PoseStack.Pose entry = poseStack.last();

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        drawLine(entry, buffer, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        drawLine(entry, buffer, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        drawLine(entry, buffer, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        drawLine(entry, buffer, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        drawLine(entry, buffer, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        drawLine(entry, buffer, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        drawLine(entry, buffer, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        drawLine(entry, buffer, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        drawLine(entry, buffer, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        drawLine(entry, buffer, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        drawLine(entry, buffer, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        drawLine(entry, buffer, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private static void drawLine(PoseStack.Pose entry, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        buffer.addVertex(entry, x1, y1, z1).setColor(r, g, b, a).setNormal(entry, 0.0f, 1.0f, 0.0f);
        buffer.addVertex(entry, x2, y2, z2).setColor(r, g, b, a).setNormal(entry, 0.0f, 1.0f, 0.0f);
    }
}
