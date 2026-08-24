package com.fxw.blizzardvilladisplay.networking;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.networking.payload.ChooseCharConfrimC2SPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenChooseCharS2CPayload;
import com.fxw.blizzardvilladisplay.networking.payload.OpenCustomScreenTestS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BlizzardVillaDisplayNetworking implements ModInitializer {
    public static final String MOD_ID = BlizzardVillaDisplay.MOD_ID;
    @Override
    public void onInitialize() {
        NetworkPayloads.initialize();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof Interaction interaction) {
                if (hand == InteractionHand.MAIN_HAND) {
                    if (interaction.getTags().contains("test_tag")) {
                        ServerPlayNetworking.send((ServerPlayer) player, new OpenCustomScreenTestS2CPayload(player.blockPosition()));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        //send open choose charactor payload
        final Map<UUID, Long> INTERACTION_COOLDOWN = new HashMap<>();
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof Interaction interaction) {
                if (hand == InteractionHand.MAIN_HAND) {
                    long currentTime = Util.getMillis();
                    long lastTime = INTERACTION_COOLDOWN.getOrDefault(player.getUUID(), 0L);
                    if (currentTime - lastTime < 200) {
                        return InteractionResult.SUCCESS;
                    }
                    INTERACTION_COOLDOWN.put(player.getUUID(), currentTime);
                    for (String tag : interaction.getTags()) {
                        if (tag.startsWith("choose_char_")) {
                            String charId = tag.replace("choose_char_", "");
                            Scoreboard scoreboard = world.getScoreboard();
                            PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                            if (playerTeam != null) {
                                player.displayClientMessage(Component.literal("你已经选择角色了!").withColor(0xDC143C), false);
                                return InteractionResult.SUCCESS;
                            }
                            String targetTeamName = charId;
                            PlayerTeam targetTeam = scoreboard.getPlayerTeam(targetTeamName);
                            if (targetTeam != null && !targetTeam.getPlayers().isEmpty()) {
                                player.displayClientMessage(Component.literal("该角色已经被选择了!").withColor(0xDC143C), false);
                                return InteractionResult.SUCCESS;
                            }
                            ServerPlayNetworking.send((ServerPlayer) player, new OpenChooseCharS2CPayload(charId, player.blockPosition()));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });

        //receive confirm choose ke payload
        ServerPlayNetworking.registerGlobalReceiver(
                ChooseCharConfrimC2SPayload.ID, (payload, context) -> {
                    context.server().execute(() -> {
                        var player = context.player();
                        var server = context.server();
                        Scoreboard scoreboard = server.getScoreboard();
                        String charId = payload.charId();
                        PlayerTeam team = scoreboard.getPlayersTeam(charId);
                        if (team == null) {
                            team = scoreboard.addPlayerTeam(charId);
                        }
                        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
                        Component text = Component.empty().append(player.getDisplayName()).append(" 已选择角色").withColor(0x00FF00);
                        server.getPlayerList().broadcastSystemMessage(text, false);
                    });
                }
        );
    }

    public static ResourceLocation getId(String input) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, input);
    }
}
