package com.fxw.blizzardvilladisplay.networking;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;

public class BlizzardVillaDisplayNetworking implements ModInitializer {
    public static final String MOD_ID = BlizzardVillaDisplay.MOD_ID;
    @Override
    public void onInitialize() {
        NetworkPayloads.initialize();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof Interaction interaction) {
                if (hand == InteractionHand.MAIN_HAND) {
                    if (interaction.getTags().contains("test_tag")) {
                        ServerPlayNetworking.send((ServerPlayer) player, new OpenCustomScreenTestS2CPayload(player.blockPosition()));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
    public static ResourceLocation getId(String input) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, input);
    }
}
