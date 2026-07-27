package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BoneGrudgeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BONE_GRUDGE.get() || !ModTraits.isBoneGrudgeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.bone_grudge", "tooltip.jaams_weaponry.trait.bone_grudge.desc");
        if (!TooltipsConfig.TOOLTIP_BONE_GRUDGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.bone_grudge", ChatFormatting.GOLD);
        float bonusDamage = getBonusDamage(stack, tag);
        if (bonusDamage > 0.0F) {
            ModTooltips.addStat(stack, tooltip, "bone_grudge.bonus_damage", ModTooltips.roundToTwoDecimals(bonusDamage));
        }
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BoneGrudgeBonusDamage")) {
            return tag.getFloat("BoneGrudgeBonusDamage");
        }
        return TraitModifierData.getBoneGrudge(stack)
            .map((entry) -> entry.bonus_damage)
            .orElseGet(() -> TraitsConfig.BONE_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }
}
