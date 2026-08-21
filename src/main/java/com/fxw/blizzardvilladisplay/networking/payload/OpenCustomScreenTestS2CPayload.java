package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record OpenCustomScreenTestS2CPayload(BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_CUSTOM_SCREEN_TEST_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "open_custom_screen_test");
    public static final CustomPacketPayload.Type<OpenCustomScreenTestS2CPayload> ID = new CustomPacketPayload.Type<>(OPEN_CUSTOM_SCREEN_TEST_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCustomScreenTestS2CPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, OpenCustomScreenTestS2CPayload::pos, OpenCustomScreenTestS2CPayload::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
