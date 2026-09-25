package net.jaams.weaponry.command;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.ChatFormatting;

@EventBusSubscriber
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
                }))));
    }
}
