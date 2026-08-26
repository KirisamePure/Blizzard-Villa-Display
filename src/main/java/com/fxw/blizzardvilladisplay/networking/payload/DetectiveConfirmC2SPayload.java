package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record DetectiveConfirmC2SPayload(String status) implements CustomPacketPayload {
    public static final ResourceLocation DETECTIVE_CONFIRM_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "detective_confrim");
    public static final Type<DetectiveConfirmC2SPayload> ID = new Type<>(DETECTIVE_CONFIRM_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, DetectiveConfirmC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DetectiveConfirmC2SPayload::status, DetectiveConfirmC2SPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
