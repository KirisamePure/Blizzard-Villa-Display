package com.fxw.blizzardvilladisplay.networking.payload;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record ChooseCharConfrimC2SPayload(String charId) implements CustomPacketPayload {
    public static final ResourceLocation CHOOSE_CHAR_KE_CONFIRM_ID = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "choose_char_ke_confirm");
    public static final Type<ChooseCharConfrimC2SPayload> ID = new Type<>(CHOOSE_CHAR_KE_CONFIRM_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseCharConfrimC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ChooseCharConfrimC2SPayload::charId,ChooseCharConfrimC2SPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
