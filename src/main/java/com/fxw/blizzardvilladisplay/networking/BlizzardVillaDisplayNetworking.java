package com.fxw.blizzardvilladisplay.networking;

import com.fxw.blizzardvilladisplay.BlizzardVillaDisplay;
import com.fxw.blizzardvilladisplay.networking.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;


public class BlizzardVillaDisplayNetworking implements ModInitializer {
    public static final String MOD_ID = BlizzardVillaDisplay.MOD_ID;
    @Override
    public void onInitialize() {
        NetworkPayloads.initialize();
        HighlightSearch.initialize();

        //open custom texture screen test
//        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//            if (!world.isClientSide() && entity instanceof Interaction interaction) {
//                if (hand == InteractionHand.MAIN_HAND) {
//                    if (interaction.getTags().contains("test_tag")) {
//                        ServerPlayNetworking.send((ServerPlayer) player, new OpenCustomScreenTestS2CPayload(player.blockPosition()));
//                        return InteractionResult.SUCCESS;
//                    }
//                }
//            }
//            return InteractionResult.PASS;
//        });

        //send open choose charactor payload
        final Map<UUID, Long> INTERACTION_COOLDOWN = new HashMap<>();
        final List<String> ALL_CHAR_TEAMS = List.of(
                "ys", "ke", "ysbe", "flk", "tms", "sfy"
        );
        final Map<String, String> CHAR_TEAM_NAMES = Map.of(
                "ys", "亚瑟",
                "ke", "卡尔",
                "ysbe", "伊莎贝尔",
                "flk", "弗兰克",
                "tms", "托马斯",
                "sfy", "索菲亚"
        );
        final Set<UUID> READ_PLAYERS = new HashSet<>();
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof Interaction interaction) {
                if (hand == InteractionHand.MAIN_HAND) {
                    long currentTime = Util.getMillis();
                    long lastTime = INTERACTION_COOLDOWN.getOrDefault(player.getUUID(), 0L);
                    if (currentTime - lastTime < 200) {
                        return InteractionResult.PASS;
                    }
                    INTERACTION_COOLDOWN.put(player.getUUID(), currentTime);

                    //choose char detect
                    for (String tag : interaction.getTags()) {
                        if (tag.startsWith("choose_char_")) {
                            String charId = tag.replace("choose_char_", "");
                            Scoreboard scoreboard = world.getScoreboard();
                            PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                            if (playerTeam != null) {
                                player.displayClientMessage(Component.literal("你已经选择角色了!").withColor(0xDC143C), false);
                                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0f, 1.0f);
                                return InteractionResult.SUCCESS;
                            }
                            String targetTeamName = charId;
                            PlayerTeam targetTeam = scoreboard.getPlayerTeam(targetTeamName);
                            if (targetTeam != null && !targetTeam.getPlayers().isEmpty()) {
                                player.displayClientMessage(Component.literal("该角色已经被选择了!").withColor(0xDC143C), false);
                                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0f, 1.0f);
                                return InteractionResult.SUCCESS;
                            }
                            ServerPlayNetworking.send((ServerPlayer) player, new OpenChooseCharS2CPayload(charId, player.blockPosition()));
                            return InteractionResult.SUCCESS;
                        }
                    }

                    //detective screen detect
                    for (String tag : interaction.getTags()) {
                        if (tag.startsWith("detective_")) {
                            String status = tag.replace("detective_", "");
                            switch (status) {

                                case "start" -> {
                                    Scoreboard scoreboard = world.getScoreboard();
                                    boolean allTeamsFilled = ALL_CHAR_TEAMS.stream().allMatch(teamName -> {
                                        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
                                        return team != null && !team.getPlayers().isEmpty();
                                    });
                                    if (allTeamsFilled) {
                                        ServerPlayNetworking.send((ServerPlayer) player, new OpenDetectiveS2CPayload(status, player.blockPosition()));
                                    } else {
                                        String emptyTeamNames = ALL_CHAR_TEAMS.stream()
                                                .filter(teamName -> {
                                                    PlayerTeam team = scoreboard.getPlayerTeam(teamName);
                                                    return team == null || team.getPlayers().isEmpty();
                                                })
                                                .map(teamName ->
                                                        CHAR_TEAM_NAMES.getOrDefault(teamName, teamName)
                                                )
                                                .collect(Collectors.joining("、"));
                                        Component warningMsg = Component.literal("仍有角色未被选择！ 未选角色: " + emptyTeamNames)
                                                .withColor(0xFF5555);
                                        player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0f, 1.0f);
                                        if (world.getServer() != null) {
                                            world.getServer().getPlayerList().broadcastSystemMessage(warningMsg, false);
                                        }
                                    }
                                    return InteractionResult.SUCCESS;
                                }

                                case "read" -> {
                                    MinecraftServer server = world.getServer();
                                    if (server == null) {
                                        return InteractionResult.SUCCESS;
                                    }
                                    UUID playerUuid = player.getUUID();
                                    if (READ_PLAYERS.contains(playerUuid)) {
                                        player.displayClientMessage(
                                                Component.literal("你已经阅读完个人剧情，请耐心等待其他玩家......")
                                                        .withColor(0xFFFF00),
                                                false
                                        );
                                        player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0f, 1.0f);
                                        return InteractionResult.SUCCESS;
                                    }
                                    READ_PLAYERS.add(playerUuid);
                                    int currReadCount = READ_PLAYERS.size();
                                    Component broadcastMsg = Component.literal(
                                            player.getDisplayName().getString()
                                    ).append(
                                            " 已阅读完个人剧情"
                                    ).withColor(0x00FF00);
                                    server.getPlayerList().broadcastSystemMessage(broadcastMsg, false);
                                    if (currReadCount >= 6) {
                                        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                                        if (overworld != null) {
                                            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                                                serverPlayer.teleportTo(
                                                        overworld,
                                                        -2928.5, 279, 3092.5,
                                                        Set.of(),
                                                        serverPlayer.getYRot(),
                                                        serverPlayer.getXRot(),
                                                        true
                                                );
                                                serverPlayer.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                                Component title = Component.literal("自我介绍").withColor(0xDC143C);
                                                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                                                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
                                            }
                                            Component text = Component.literal("现在是自我介绍环节，下一阶段为一轮搜证。").withColor(0x7FFFAA);
                                            server.getPlayerList().broadcastSystemMessage(text, false);
                                        }
                                        READ_PLAYERS.clear();
                                    }
                                    return InteractionResult.SUCCESS;
                                }

                                case "search_1" -> {
                                    ServerPlayNetworking.send((ServerPlayer) player, new OpenDetectiveS2CPayload(status, player.blockPosition()));
                                    return InteractionResult.SUCCESS;
                                }

                                case "end_search" -> {
                                    ServerPlayNetworking.send((ServerPlayer) player, new OpenDetectiveS2CPayload(status, player.blockPosition()));
                                    return InteractionResult.SUCCESS;
                                }


                                case "search_2" -> {
                                    ServerPlayNetworking.send((ServerPlayer) player, new OpenDetectiveS2CPayload(status, player.blockPosition()));
                                    return InteractionResult.SUCCESS;
                                }

                                case "vote" -> {
                                    ServerPlayNetworking.send((ServerPlayer) player, new OpenDetectiveS2CPayload(status, player.blockPosition()));
                                    return InteractionResult.SUCCESS;
                                }
                                default -> {
                                    return InteractionResult.PASS;
                                }
                            }
                        }
                    }


                }
            }
            return InteractionResult.PASS;
        });

        //send open detective payload
//        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//            if (!world.isClientSide() && entity instanceof Interaction interaction) {
//                if (hand == InteractionHand.MAIN_HAND) {
//                    System.out.println("TAGS = " + interaction.getTags());
//                    long currentTime = Util.getMillis();
//                    long lastTime = INTERACTION_COOLDOWN.getOrDefault(player.getUUID(), 0L);
//                    if (currentTime - lastTime < 200) {
//                        return InteractionResult.SUCCESS;
//                    }
//                    INTERACTION_COOLDOWN.put(player.getUUID(), currentTime);
//                }
//            }
//            return InteractionResult.PASS;
//        });

        //receive confirm choose char payload
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

        //receive detective confirm payload
        final Map<String, BlockPos> START_POS = Map.of(
                "ys", new BlockPos(-2911, 273, 3072),
                "ke", new BlockPos(-2924, 273, 3072),
                "ysbe", new BlockPos(-2937, 273, 3072),
                "flk", new BlockPos(-2910, 262, 3072),
                "tms", new BlockPos(-2924, 261, 3072),
                "sfy", new BlockPos(-2937, 261, 3072)
        );
        final Map<String, BlockPos> VOTE_POS = Map.of(
                "ys", new BlockPos(-2956, 239, 3074),
                "ke", new BlockPos(-2976, 239, 3073),
                "ysbe", new BlockPos(-2996, 239, 3073),
                "flk", new BlockPos(-2957, 239, 3058),
                "tms", new BlockPos(-2977, 239, 3057),
                "sfy", new BlockPos(-2997, 239, 3056)
        );
        ServerPlayNetworking.registerGlobalReceiver(
                DetectiveConfirmC2SPayload.ID, (payload, context) -> {
                    context.server().execute(() -> {
                        var server = context.server();
                        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                        Scoreboard scoreboard = server.getScoreboard();
                        switch (payload.status()) {
                            case "start" -> {
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
                                    if (team != null) {
                                        String teamName = team.getName();
                                        BlockPos targetPos = START_POS.get(teamName);
                                        if (targetPos != null) {
                                            player.teleportTo(
                                                    overworld,
                                                    targetPos.getX() + 0.5,
                                                    targetPos.getY(),
                                                    targetPos.getZ() + 0.5,
                                                    Set.of(),
                                                    player.getYRot(),
                                                    player.getXRot(),
                                                    true);
                                            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                        }
                                    }
                                }
                            }

                            case "search_1" -> {
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    player.teleportTo(
                                            overworld,
                                            -2717.5, 191, 3495.5,
                                            Set.of(),
                                            player.getYRot(),
                                            player.getXRot(),
                                            true
                                             );
                                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                    Component title = Component.literal("一轮搜证开始!").withColor(0xDC143C);
                                    player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                                    player.connection.send(new ClientboundSetTitleTextPacket(title));
                                    player.experienceLevel = 1800;
                                    player.experienceProgress = 0.0F;
                                    player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                                }
                                server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack(),
                                        "function clues:set_clues_1"
                                );
                            }

                            case "end_search" -> {
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    player.experienceProgress = 0.0F;
                                    player.experienceLevel = 0;
                                    player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                }
                            }


                            case "search_2" -> {
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    player.teleportTo(
                                            overworld,
                                            -2717.5, 191, 3495.5,
                                            Set.of(),
                                            player.getYRot(),
                                            player.getXRot(),
                                            true
                                    );
                                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                    Component title = Component.literal("二轮搜证开始!").withColor(0xDC143C);
                                    player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                                    player.connection.send(new ClientboundSetTitleTextPacket(title));
                                    player.experienceLevel = 1800;
                                    player.experienceProgress = 0.0F;
                                    player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                                }
                                server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack(),
                                        "function clues:set_clues_2"
                                );
                                server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack(),
                                        "kill @e[type=minecraft:interaction,tag=detective_search_2]"
                                );
                                server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack(),
                                        "summon minecraft:interaction -2962 281 3089 {Tags:[\"detective_vote\"],height:2,width:0.6}"
                                );
                            }

                            case "vote" -> {
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
                                    if (team != null) {
                                        String teamName = team.getName();
                                        BlockPos targetPos = VOTE_POS.get(teamName);
                                        if (targetPos != null) {
                                            player.teleportTo(
                                                    overworld,
                                                    targetPos.getX() + 0.5,
                                                    targetPos.getY(),
                                                    targetPos.getZ() + 0.5,
                                                    Set.of(),
                                                    player.getYRot(),
                                                    player.getXRot(),
                                                    true);
                                            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 1.0f);
                                            Component title = Component.literal("最终投票").withColor(0x800000);
                                            player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                                            player.connection.send(new ClientboundSetTitleTextPacket(title));
                                        }
                                    }
                                }
                                Component text = Component.literal("开始最终投票，请选择你认为的凶手").withColor(0xF0E68C);
                                server.getPlayerList().broadcastSystemMessage(text, false);
                            }

                            default -> {}
                        }
                    });
                }
        );
    }

    public static ResourceLocation getId(String input) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, input);
    }
}
