package net.jaams.weaponry.tooltip.trait;

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

public class ArmorBreakerItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.ARMOR_BREAKER.get()) {
            return;
        }
        if (!ModTraits.isArmorBreakerItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.armor_breaker", "tooltip.jaams_weaponry.trait.armor_breaker.desc");
        if (!TooltipsConfig.TOOLTIP_ARMOR_BREAKER_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.armor_breaker", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "armor_breaker.chance", getChance(stack, tag) * 100.0);
        ModTooltips.addStatInt(stack, tooltip, "armor_breaker.durability_damage", getDurabilityDamage(stack, tag));
        String slots = String.join(", ", getSlots(stack, tag));
        tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.armor_breaker.affected_slots", slots).withStyle(ChatFormatting.GRAY));
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ArmorBreakerChance")) {
            return tag.getFloat("ArmorBreakerChance");
        }
        return TraitModifierData.getArmorBreaker(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.ARMOR_BREAKER_CHANCE.get().floatValue());
    }

    private static int getDurabilityDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ArmorBreakerDurabilityDamage")) {
            return tag.getInt("ArmorBreakerDurabilityDamage");
        }
        return TraitModifierData.getArmorBreaker(stack)
            .map((entry) -> entry.durability_damage)
            .orElseGet(() -> TraitsConfig.ARMOR_BREAKER_DURABILITY_DAMAGE.get());
    }

    private static List<String> getSlots(ItemStack stack, CompoundTag tag) {
        
        if (tag != null && tag.contains("ArmorBreakerSlots")) {
            String slotsStr = tag.getString("ArmorBreakerSlots");
            return List.of(slotsStr.split(","));
        }
        return TraitModifierData.getArmorBreaker(stack)
            .map((entry) -> entry.slots)
            .orElseGet(() -> List.copyOf(TraitsConfig.ARMOR_BREAKER_SLOTS.get()));
    }
}
