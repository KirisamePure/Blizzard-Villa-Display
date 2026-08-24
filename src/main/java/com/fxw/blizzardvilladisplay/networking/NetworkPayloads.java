package com.fxw.blizzardvilladisplay.networking;

import com.fxw.blizzardvilladisplay.networking.payload.ChooseCharConfrimC2SPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenChooseCharS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class NetworkPayloads {
    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> paclIdentifier, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(paclIdentifier, codec);
    }
    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> paclIdentifier, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(paclIdentifier, codec);
    }
    public static void initialize() {
        registerS2C(OpenCustomScreenTestS2CPayload.ID, OpenCustomScreenTestS2CPayload.CODEC);
        registerS2C(OpenChooseCharS2CPayload.ID, OpenChooseCharS2CPayload.CODEC);
        registerC2S(ChooseCharConfrimC2SPayload.ID, ChooseCharConfrimC2SPayload.CODEC);
    }
}
