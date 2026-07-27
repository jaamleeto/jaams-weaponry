package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class HeavyHandedItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.HEAVY_HANDED.get()) {
            return;
        }
        if (!ModTraits.isHeavyHandedItem(stack)) {
            return;
        }
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.heavy_handed",
                "tooltip.jaams_weaponry.trait.heavy_handed.desc");
        if (!TooltipsConfig.TOOLTIP_HEAVY_HANDED_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.heavy_handed", ChatFormatting.RED);

        
        double movReduction = calculateMovementReduction(stack);
        double atkSpeedReduction = calculateAttackSpeedReduction(stack);
        double atkDmgReduction = calculateAttackDamageReduction(stack);

        ModTooltips.addStat(stack, tooltip, "heavy_handed.movement_speed", movReduction * 100.0);
        ModTooltips.addStat(stack, tooltip, "heavy_handed.attack_speed", atkSpeedReduction * 100.0);
        ModTooltips.addStat(stack, tooltip, "heavy_handed.attack_damage", atkDmgReduction * 100.0);
    }

    public static double calculateMovementReduction(ItemStack stack) {
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_MOVEMENT_SPEED_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.movement_speed_reduction).orElse(null),
                1.0);
    }

    public static double calculateMovementReduction(ItemStack stack, ItemStack otherStack) {
        double mult = getDualWieldMultiplier(otherStack);
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_MOVEMENT_SPEED_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.movement_speed_reduction).orElse(null),
                mult);
    }

    public static double calculateAttackSpeedReduction(ItemStack stack) {
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_ATTACK_SPEED_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.attack_speed_reduction).orElse(null),
                1.0);
    }

    public static double calculateAttackSpeedReduction(ItemStack stack, ItemStack otherStack) {
        double mult = getDualWieldMultiplier(otherStack);
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_ATTACK_SPEED_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.attack_speed_reduction).orElse(null),
                mult);
    }

    public static double calculateAttackDamageReduction(ItemStack stack) {
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_ATTACK_DAMAGE_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.attack_damage_reduction).orElse(null),
                1.0);
    }

    public static double calculateAttackDamageReduction(ItemStack stack, ItemStack otherStack) {
        double mult = getDualWieldMultiplier(otherStack);
        return calculateTotalReduction(stack,
                TraitsConfig.HEAVY_HANDED_ATTACK_DAMAGE_REDUCTION,
                TraitModifierData.getHeavyHanded(stack).map(e -> e.attack_damage_reduction).orElse(null),
                mult);
    }

    private static double calculateTotalReduction(ItemStack stack,
            net.neoforged.neoforge.common.ModConfigSpec.DoubleValue configBase, Double dataBase, double dualWieldMult) {
        double baseReduction = dataBase != null ? dataBase : configBase.get();
        double durabilityFactor = getDurabilityFactor(stack);
        double damageFactor = getDamageFactor(stack);
        double maxReduction = getMaxReduction(stack);

        double total = (baseReduction + durabilityFactor + damageFactor) * dualWieldMult;
        return Math.min(total, maxReduction);
    }

    
    public static double getDualWieldMultiplier(ItemStack otherStack) {
        if (otherStack.isEmpty()) {
            return 0.0;
        }

        
        if (ModTraits.isHeavyHandedItem(otherStack)) {
            return 2.0;
        }

        
        if (otherStack.getMaxDamage() <= 0) {
            return 0.5;
        }

        
        
        double durability = otherStack.getMaxDamage();
        double mult = Math.min(0.5 + (durability / 1500.0), 1.2);
        return mult;
    }

    private static double getDurabilityFactor(ItemStack stack) {
        double factor = TraitModifierData.getHeavyHanded(stack)
                .map(e -> e.durability_factor)
                .orElseGet(() -> TraitsConfig.HEAVY_HANDED_DURABILITY_FACTOR.get());
        if (factor <= 0.0 || stack.getMaxDamage() <= 0)
            return 0.0;
        return (stack.getMaxDamage() / 100.0) * factor;
    }

    private static double getDamageFactor(ItemStack stack) {
        double factor = TraitModifierData.getHeavyHanded(stack)
                .map(e -> e.damage_factor)
                .orElseGet(() -> TraitsConfig.HEAVY_HANDED_DAMAGE_FACTOR.get());
        if (factor <= 0.0)
            return 0.0;
        double damage = getItemAttackDamage(stack);
        return damage * factor;
    }

    
    public static double getMaxReduction(ItemStack stack) {
        return TraitModifierData.getHeavyHanded(stack)
                .map(e -> e.max_reduction)
                .orElseGet(() -> TraitsConfig.HEAVY_HANDED_MAX_REDUCTION.get());
    }

    private static double getItemAttackDamage(ItemStack stack) {
        if (stack.isEmpty())
            return 0.0;
        return net.jaams.weaponry.util.ModUtils.attackDamageModifierSum(stack, EquipmentSlot.MAINHAND);
    }
}
