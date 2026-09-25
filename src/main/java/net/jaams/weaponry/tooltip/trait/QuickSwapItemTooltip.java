package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.List;
import java.util.Locale;

public class QuickSwapItemTooltip {

    private static final String[] SUFFIXES = { "_reverse", "_unfolded", "_folded" };

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.QUICK_SWAP.get()) {
            return;
        }
        if (!ModTraits.isQuickSwapItem(stack)) {
            return;
        }
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.quick_swap",
                "tooltip.jaams_weaponry.trait.quick_swap.desc");
        if (!TooltipsConfig.TOOLTIP_QUICK_SWAP_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.quick_swap", ChatFormatting.GOLD);
        addTargetItemLine(stack, tooltip);
        addCooldownLines(stack, tooltip);
        addSwapModeIfEnabled(stack, tooltip);
    }

    private static void addSwapModeIfEnabled(ItemStack stack, List<Component> tooltip) {
        if (!TooltipsConfig.TOOLTIP_QUICK_SWAP_MODE.get())
            return;
        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.quick_swap_mode",
                ChatFormatting.GOLD);
        String modeKey = switch (mode) {
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.quick_swap_mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.quick_swap_mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.quick_swap_mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.quick_swap_mode.charge_hybrid";
        };
        tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static ModEnums.QuickSwapMode getQuickSwapMode(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("QuickSwapMode")) {
            try {
                return ModEnums.QuickSwapMode.valueOf(tag.getString("QuickSwapMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ModEnums.QuickSwapMode jsonMode = TraitModifierData.getQuickSwap(stack)
                .map(entry -> entry.activation_mode)
                .filter(java.util.Objects::nonNull)
                .map(m -> {
                    try {
                        return ModEnums.QuickSwapMode.valueOf(m.toUpperCase(Locale.ROOT));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
        if (jsonMode != null)
            return jsonMode;
        return TraitsConfig.QUICK_SWAP_ACTIVATION_MODE.get();
    }

    private static void addTargetItemLine(ItemStack stack, List<Component> tooltip) {
        String targetItemId = getTargetItemId(stack);
        if (targetItemId != null && !targetItemId.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(targetItemId);
            Component targetName;
            if (loc != null) {
                net.minecraft.world.item.Item targetItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(loc);
                targetName = targetItem != null
                        ? Component.translatable(targetItem.getDescriptionId())
                        : Component.literal(targetItemId);
            } else {
                targetName = Component.literal(targetItemId);
            }
            Component targetComponent = Component
                    .translatable("tooltip.jaams_weaponry.properties.quick_swap_target", targetName)
                    .withStyle(ChatFormatting.GRAY);
            tooltip.add(targetComponent);
        }
    }

    private static void addCooldownLines(ItemStack stack, List<Component> tooltip) {
        int mainHandCooldown = getMainHandCooldown(stack);
        int offHandCooldown = getOffHandCooldown(stack);
        if (mainHandCooldown > 0) {
            ModTooltips.addStat(stack, tooltip, "main_hand_cooldown", mainHandCooldown / 20.0);
        }
        if (offHandCooldown > 0) {
            ModTooltips.addStat(stack, tooltip, "off_hand_cooldown", offHandCooldown / 20.0);
        }
    }

    private static String getTargetItemId(ItemStack stack) {
        
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("QuickSwapTargetItem")) {
            return tag.getString("QuickSwapTargetItem");
        }
        
        String jsonTarget = TraitModifierData.getQuickSwap(stack)
                .map(entry -> entry.target_item)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonTarget != null) {
            return jsonTarget;
        }
        
        ResourceLocation registryKey = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (registryKey == null)
            return null;
        String namespace = registryKey.getNamespace();
        String path = registryKey.getPath();
        
        for (String suffix : SUFFIXES) {
            if (path.endsWith(suffix)) {
                String basePath = path.substring(0, path.length() - suffix.length());
                ResourceLocation baseLoc = ResourceLocation.fromNamespaceAndPath(namespace, basePath);
                net.minecraft.world.item.Item baseItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(baseLoc);
                if (baseItem != null && baseItem != net.minecraft.world.item.Items.AIR) {
                    return baseLoc.toString();
                }
            }
        }
        
        for (String suffix : SUFFIXES) {
            String candidatePath = path + suffix;
            ResourceLocation candidateLoc = ResourceLocation.fromNamespaceAndPath(namespace, candidatePath);
            net.minecraft.world.item.Item candidateItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(candidateLoc);
            if (candidateItem != null && candidateItem != net.minecraft.world.item.Items.AIR) {
                return candidateLoc.toString();
            }
        }
        return null;
    }

    private static int getMainHandCooldown(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("QuickSwapMainHandCooldown")) {
            return Math.max(0, tag.getInt("QuickSwapMainHandCooldown"));
        }
        return TraitModifierData.getQuickSwap(stack)
                .map(entry -> entry.main_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_MAIN_HAND_COOLDOWN.get());
    }

    private static int getOffHandCooldown(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("QuickSwapOffHandCooldown")) {
            return Math.max(0, tag.getInt("QuickSwapOffHandCooldown"));
        }
        return TraitModifierData.getQuickSwap(stack)
                .map(entry -> entry.off_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_OFF_HAND_COOLDOWN.get());
    }
}
