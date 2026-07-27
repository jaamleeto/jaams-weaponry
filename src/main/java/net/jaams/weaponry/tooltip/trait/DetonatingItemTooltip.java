package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.List;

public class DetonatingItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DETONATING.get()) {
            return;
        }
        if (!ModTraits.isDetonatingItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.detonating", "tooltip.jaams_weaponry.trait.detonating.desc");
        if (!TooltipsConfig.TOOLTIP_DETONATING_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.detonating", ChatFormatting.RED);
        double chance = ModTooltips.roundToTwoDecimals(getExplodeChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "detonating.explode_chance", chance);
        ModTooltips.addStat(stack, tooltip, "detonating.explosion_power", ModTooltips.roundToTwoDecimals(getExplosionPower(stack, tag)));
    }

    private static float getExplodeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DetonatingExplodeChance")) {
            return tag.getFloat("DetonatingExplodeChance");
        }
        return TraitModifierData.getDetonating(stack).map(entry -> entry.explode_chance)
                .orElseGet(() -> TraitsConfig.DETONATING_EXPLODE_CHANCE.get().floatValue());
    }

    private static float getExplosionPower(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DetonatingExplosionPower")) {
            return tag.getFloat("DetonatingExplosionPower");
        }
        return TraitModifierData.getDetonating(stack).map(entry -> entry.explosion_power)
                .orElseGet(() -> TraitsConfig.DETONATING_EXPLOSION_POWER.get().floatValue());
    }
}
