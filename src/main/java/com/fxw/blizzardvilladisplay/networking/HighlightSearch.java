package com.fxw.blizzardvilladisplay.networking;

import com.fxw.blizzardvilladisplay.networking.payload.HighlightDataS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.RequestHighlightC2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HighlightSearch {
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(RequestHighlightC2SPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            ServerLevel level = player.level();
            context.server().execute(() -> {
                Vec3 pos = player.position();
                AABB searchBox = new AABB(pos.subtract(20, 20, 20), pos.add(20, 20, 20));

                List<Integer> entityIds = level.getEntitiesOfClass(
                        ItemFrame.class, searchBox,
                        e -> e.distanceToSqr(player) <= 200 && e.getTags().contains("clue_frame")
                ).stream().map(Entity::getId).toList();
                List<BlockPos> blockPositions = new ArrayList<>();
                BlockPos playerPos = player.blockPosition();
                for (BlockPos bPos : BlockPos.betweenClosed(playerPos.subtract(new Vec3i(20, 20, 20)), playerPos.offset(20, 20, 20))) {
                    if (bPos.distSqr(playerPos) <= 200) {
                        if (level.getBlockState(bPos).is(Blocks.CHISELED_BOOKSHELF)) {
                            if (level.getBlockEntity(bPos) instanceof ChiseledBookShelfBlockEntity be && !be.isEmpty()) {
                                blockPositions.add(bPos.immutable());
                            }
                        }
                    }
                }

                ServerPlayNetworking.send(player, new HighlightDataS2CPayload(entityIds, blockPositions));
            });
        });
    }
}
