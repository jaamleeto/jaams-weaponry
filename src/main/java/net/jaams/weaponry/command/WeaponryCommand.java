package net.jaams.weaponry.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.PacketDistributor;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.network.PlayAnimationMessage;
import net.jaams.weaponry.util.ModAnimations;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;

@Mod.EventBusSubscriber
public class WeaponryCommand {

    private static String getModVersion() {
        try {
            return ModList.get().getModContainerById("jaams_weaponry")
                    .map(container -> container.getModInfo().getVersion().toString()).orElse("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("jaams").then(Commands.literal("weaponry")
                .then(Commands.literal("version").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.jaams.weaponry.version",
                            Component.literal(getModVersion()).withStyle(ChatFormatting.YELLOW)), false);
                    return 1;
                }))
                .then(Commands.literal("animate")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("animation", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            List<String> suggestions = new ArrayList<>();
                                            suggestions.add("stop");
                                            suggestions.addAll(AnimationAPI.animations.keySet());
                                            return SharedSuggestionProvider.suggest(suggestions, builder);
                                        })
                                        .executes(ctx -> animate(ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                StringArgumentType.getString(ctx, "animation"),
                                                false, false, false, 0, 1.0f))
                                        .then(Commands.argument("override", BoolArgumentType.bool())
                                                .executes(ctx -> animate(ctx.getSource(),
                                                        EntityArgument.getPlayers(ctx, "targets"),
                                                        StringArgumentType.getString(ctx, "animation"),
                                                        BoolArgumentType.getBool(ctx, "override"), false, false, 0,
                                                        1.0f))
                                                .then(Commands.argument("firstPerson", BoolArgumentType.bool())
                                                        .executes(ctx -> animate(ctx.getSource(),
                                                                EntityArgument.getPlayers(ctx, "targets"),
                                                                StringArgumentType.getString(ctx, "animation"),
                                                                BoolArgumentType.getBool(ctx, "override"),
                                                                BoolArgumentType.getBool(ctx, "firstPerson"), false, 0,
                                                                1.0f))
                                                        .then(Commands.argument("hideArms", BoolArgumentType.bool())
                                                                .executes(ctx -> animate(ctx.getSource(),
                                                                        EntityArgument.getPlayers(ctx, "targets"),
                                                                        StringArgumentType.getString(ctx, "animation"),
                                                                        BoolArgumentType.getBool(ctx, "override"),
                                                                        BoolArgumentType.getBool(ctx, "firstPerson"),
                                                                        BoolArgumentType.getBool(ctx, "hideArms"),
                                                                        0, 1.0f))
                                                                .then(Commands
                                                                        .argument("speed",
                                                                                FloatArgumentType.floatArg(0.01f,
                                                                                        100.0f))
                                                                        .executes(ctx -> animate(ctx.getSource(),
                                                                                EntityArgument.getPlayers(ctx,
                                                                                        "targets"),
                                                                                StringArgumentType.getString(ctx,
                                                                                        "animation"),
                                                                                BoolArgumentType.getBool(ctx,
                                                                                        "override"),
                                                                                BoolArgumentType.getBool(ctx,
                                                                                        "firstPerson"),
                                                                                BoolArgumentType.getBool(ctx,
                                                                                        "hideArms"),
                                                                                0,
                                                                                FloatArgumentType.getFloat(ctx,
                                                                                        "speed")))
                                                                        .then(Commands
                                                                                .argument("duration",
                                                                                        IntegerArgumentType.integer(0))
                                                                                .executes(ctx -> animate(
                                                                                        ctx.getSource(),
                                                                                        EntityArgument.getPlayers(ctx,
                                                                                                "targets"),
                                                                                        StringArgumentType.getString(
                                                                                                ctx, "animation"),
                                                                                        BoolArgumentType.getBool(ctx,
                                                                                                "override"),
                                                                                        BoolArgumentType.getBool(ctx,
                                                                                                "firstPerson"),
                                                                                        BoolArgumentType.getBool(ctx,
                                                                                                "hideArms"),
                                                                                        IntegerArgumentType.getInteger(
                                                                                                ctx, "duration"),
                                                                                        FloatArgumentType.getFloat(ctx,
                                                                                                "speed"))))))))))
                        .then(Commands.literal("on")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("animation", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    List<String> suggestions = new ArrayList<>();
                                                    suggestions.add("stop");
                                                    suggestions.addAll(AnimationAPI.animations.keySet());
                                                    return SharedSuggestionProvider.suggest(suggestions, builder);
                                                })
                                                .executes(ctx -> mobAnimate(ctx.getSource(),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        StringArgumentType.getString(ctx, "animation"),
                                                        false, 1.0f, 0))
                                                .then(Commands.argument("override", BoolArgumentType.bool())
                                                        .executes(ctx -> mobAnimate(ctx.getSource(),
                                                                EntityArgument.getEntities(ctx, "targets"),
                                                                StringArgumentType.getString(ctx, "animation"),
                                                                BoolArgumentType.getBool(ctx, "override"), 1.0f, 0))
                                                        .then(Commands.argument("speed",
                                                                FloatArgumentType.floatArg(0.01f, 100.0f))
                                                                .executes(ctx -> mobAnimate(ctx.getSource(),
                                                                        EntityArgument.getEntities(ctx, "targets"),
                                                                        StringArgumentType.getString(ctx, "animation"),
                                                                        BoolArgumentType.getBool(ctx, "override"),
                                                                        FloatArgumentType.getFloat(ctx, "speed"), 0))
                                                                .then(Commands.argument("duration",
                                                                        IntegerArgumentType.integer(0))
                                                                        .executes(ctx -> mobAnimate(ctx.getSource(),
                                                                                EntityArgument.getEntities(ctx,
                                                                                        "targets"),
                                                                                StringArgumentType.getString(ctx,
                                                                                        "animation"),
                                                                                BoolArgumentType.getBool(ctx,
                                                                                        "override"),
                                                                                FloatArgumentType.getFloat(ctx,
                                                                                        "speed"),
                                                                                IntegerArgumentType.getInteger(ctx,
                                                                                        "duration"))))))))))));
    }

    private static int animate(CommandSourceStack source, Collection<ServerPlayer> targets, String animation,
            boolean override, boolean firstPerson, boolean hideArms, int duration, float speed) {
        if (targets.isEmpty()) {
            source.sendFailure(Component.translatable("commands.jaams.weaponry.animate.no_targets"));
            return 0;
        }

        boolean isStop = animation.equalsIgnoreCase("stop");
        String displayName = isStop ? "stop" : animation;

        for (ServerPlayer target : targets) {
            if (isStop) {
                JaamsWeaponryMod.PACKET_HANDLER.send(
                        PacketDistributor.PLAYER.with(() -> target),
                        new PlayAnimationMessage(target.getId(), "", false, false));
            } else {
                JaamsWeaponryMod.PACKET_HANDLER.send(
                        PacketDistributor.PLAYER.with(() -> target),
                        new PlayAnimationMessage(target.getId(), animation, override, firstPerson, hideArms,
                                duration, speed));
            }
        }

        int count = targets.size();
        if (isStop) {
            source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.animate.stop.success",
                    count), true);
        } else {
            if (duration > 0 || speed != 1.0f) {
                source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.animate.success.details",
                        displayName, count,
                        Component.literal(firstPerson ? "enabled" : "disabled").withStyle(
                                firstPerson ? ChatFormatting.GREEN : ChatFormatting.RED),
                        duration, Component.literal(String.format("%.1f", speed)).withStyle(ChatFormatting.AQUA)),
                        true);
            } else {
                source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.animate.success",
                        displayName, count,
                        Component.literal(firstPerson ? "enabled" : "disabled").withStyle(
                                firstPerson ? ChatFormatting.GREEN : ChatFormatting.RED)),
                        true);
            }
        }
        return count;
    }

    private static int mobAnimate(CommandSourceStack source, Collection<? extends Entity> targets, String animation,
            boolean override, float speed, int duration) {
        if (targets.isEmpty()) {
            source.sendFailure(Component.translatable("commands.jaams.weaponry.animate.no_targets"));
            return 0;
        }

        boolean isStop = animation.equalsIgnoreCase("stop");
        String displayName = isStop ? "stop" : animation;
        int resultCount = 0;

        for (Entity target : targets) {
            if (!(target instanceof LivingEntity living))
                continue;
            resultCount++;
            if (isStop) {
                ModAnimations.stopMobAnimation(living);
            } else {
                ModAnimations.playMobAnimation(living, animation, override, speed, duration);
            }
        }

        final int finalCount = resultCount;
        if (finalCount == 0) {
            source.sendFailure(Component.translatable("commands.jaams.weaponry.mobanimate.no_living"));
            return 0;
        }

        if (isStop) {
            source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.mobanimate.stop.success",
                    finalCount), true);
        } else {
            if (duration > 0 || speed != 1.0f) {
                source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.animate.success.details",
                        displayName, finalCount,
                        Component.literal("N/A").withStyle(ChatFormatting.GRAY),
                        duration, Component.literal(String.format("%.1f", speed)).withStyle(ChatFormatting.AQUA)),
                        true);
            } else {
                source.sendSuccess(() -> Component.translatable("commands.jaams.weaponry.animate.success.entity",
                        displayName, finalCount,
                        Component.literal(String.format("%.1f", speed)).withStyle(ChatFormatting.AQUA)),
                        true);
            }
        }
        return finalCount;
    }
}
