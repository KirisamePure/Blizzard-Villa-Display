package com.fxw.blizzardvilladisplay.client;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.client.rendering.screens.*;
import com.fxw.blizzardvilladisplay.networking.payload.OpenChooseCharS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenDetectiveS2CPayload;
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

		//receive open detective screen payload
		ClientPlayNetworking.registerGlobalReceiver(OpenDetectiveS2CPayload.ID, (payload, context) -> {
			ClientLevel level = context.client().level;
			if (level == null) {
				return;
			}
			ResourceLocation confirmTexture = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/choose_char_confirm.png");
			ResourceLocation cancelTexture = ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/choose_char_cancel.png");
			String status = payload.status();
			DetectiveConfig config = switch (status) {
				case "start" -> new DetectiveConfig(
						status,
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/detective_start.png"),
						confirmTexture,
						cancelTexture
				);
				case "search_1" -> new DetectiveConfig(
						status,
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/detective_search_1.png"),
						confirmTexture,
						cancelTexture
				);
				case "end_search" -> new DetectiveConfig(
						status,
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/detective_end_search.png"),
						confirmTexture,
						cancelTexture
				);
				case "search_2" -> new DetectiveConfig(
						status,
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/detective_search_2.png"),
						confirmTexture,
						cancelTexture
				);
				case "vote" -> new DetectiveConfig(
						status,
						ResourceLocation.fromNamespaceAndPath(BlizzardVillaDisplay.MOD_ID, "textures/gui/detective_vote.png"),
						confirmTexture,
						cancelTexture
				);
                default -> null;
			};
			context.client().execute(() -> {
				context.client().setScreen(new DetectiveYoNScreen(config));
			});
		});
	}
}