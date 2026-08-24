package com.fxw.blizzardvilladisplay.client;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.client.rendering.screens.ChooseCharConfig;
import com.fxw.blizzardvilladisplay.client.rendering.screens.ChooseCharScreen;
import com.fxw.blizzardvilladisplay.client.rendering.screens.CustomTextureScreenTest;
import com.fxw.blizzardvilladisplay.networking.payload.OpenChooseCharS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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

		//receive open choose charactor ke screen payload
		ClientPlayNetworking.registerGlobalReceiver(OpenChooseCharS2CPayload.ID, (payload, context) -> {
			ClientLevel level = context.client().level;
			if (level == null) {
				return;
			}
			ResourceLocation confirmTexture = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/choose_char_confirm.png");
			ResourceLocation cancelTexture = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/choose_char_cancel.png");
			String charId = payload.charId();
			ChooseCharConfig config = switch (charId) {
				case "ke" -> new ChooseCharConfig(
						"ke",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/ke_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
				case "ysbe" -> new ChooseCharConfig(
						"ysbe",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/ysbe_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
				case "flk" -> new ChooseCharConfig(
						"flk",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/flk_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
				case "tms" -> new ChooseCharConfig(
						"tms",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/tms_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
				case "sfy" -> new ChooseCharConfig(
						"sfy",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/sfy_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
				default -> new ChooseCharConfig(
						"ys",
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/ys_choose_char.png"),
						confirmTexture,
						cancelTexture
				);
			};
			context.client().execute(() -> {
				context.client().setScreen(new ChooseCharScreen(config));
			});
		});
	}
}