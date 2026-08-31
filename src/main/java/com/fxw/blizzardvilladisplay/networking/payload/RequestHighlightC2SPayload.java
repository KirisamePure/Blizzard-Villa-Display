package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestHighlightC2SPayload() implements CustomPacketPayload {
    public static final ResourceLocation REQUEST_HIGHLIGHT_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID,"request_highlight");
    public static final Type<RequestHighlightC2SPayload> ID = new Type<>(REQUEST_HIGHLIGHT_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestHighlightC2SPayload> CODEC = StreamCodec.unit(
            new RequestHighlightC2SPayload()
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
