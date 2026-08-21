package com.fxw.blizzardvilladisplay.client;

import com.fxw.blizzardvilladisplay.client.rendering.screens.CustomScreenTest;
import com.fxw.blizzardvilladisplay.client.rendering.screens.CustomTextureScreenTest;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

public class BlizzardVillaDisplayClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientPlayNetworking.registerGlobalReceiver(OpenCustomScreenTestS2CPayload.ID, (payload, context) -> {
			ClientLevel level = context.client().level;
			if (level == null) {
				return;
			}
			context.client().execute(() -> {
				context.client().setScreen(new CustomTextureScreenTest(Component.empty()));
			});
		});
	}
}