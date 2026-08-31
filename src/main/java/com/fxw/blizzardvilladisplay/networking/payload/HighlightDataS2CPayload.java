package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record HighlightDataS2CPayload(List<Integer> entityIds, List<BlockPos> blockPositions) implements CustomPacketPayload {
    public static final ResourceLocation HIGHLIGHT_DATA_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "highlight_data");
    public static final Type<HighlightDataS2CPayload> ID = new Type<>(HIGHLIGHT_DATA_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, HighlightDataS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), HighlightDataS2CPayload::entityIds,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), HighlightDataS2CPayload::blockPositions,
            HighlightDataS2CPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
