package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record OpenChooseCharS2CPayload(String charId, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_CHOOSE_CHAR_KE_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "open_choose_char_ke");
    public static final Type<OpenChooseCharS2CPayload> ID = new Type<>(OPEN_CHOOSE_CHAR_KE_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenChooseCharS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenChooseCharS2CPayload::charId, BlockPos.STREAM_CODEC, OpenChooseCharS2CPayload::pos, OpenChooseCharS2CPayload::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
