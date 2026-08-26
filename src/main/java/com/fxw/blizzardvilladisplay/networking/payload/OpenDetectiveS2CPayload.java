package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record OpenDetectiveS2CPayload(String status, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_DETECTIVE_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "open_detective");
    public static final Type<OpenDetectiveS2CPayload> ID = new Type<>(OPEN_DETECTIVE_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDetectiveS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenDetectiveS2CPayload::status, BlockPos.STREAM_CODEC, OpenDetectiveS2CPayload::pos, OpenDetectiveS2CPayload::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
