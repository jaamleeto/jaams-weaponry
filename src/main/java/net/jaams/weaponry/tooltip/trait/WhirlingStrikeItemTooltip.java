package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import java.util.Objects;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class WhirlingStrikeItemTooltip {

    public static void add(ItemStack stack, List<net.minecraft.network.chat.Component> tooltip) {
        if (!TraitsConfig.WHIRLING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isWhirlingStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.whirling_strike",
                "tooltip.jaams_weaponry.trait.whirling_strike.desc");
        if (!TooltipsConfig.TOOLTIP_WHIRLING_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.whirling_strike",
                ChatFormatting.GOLD);
        addPropertiesLines(stack, tag, tooltip);
    }

    private static void addPropertiesLines(ItemStack stack, CompoundTag tag,
            List<net.minecraft.network.chat.Component> tooltip) {
        float baseDamage = getBaseDamage(stack, tag);
        ModTooltips.addStat(stack, tooltip, "whirling_strike.base_damage", ModTooltips.roundToTwoDecimals(baseDamage));

        double attackRange = getBaseAttackRange(stack, tag);
        ModTooltips.addStat(stack, tooltip, "whirling_strike.attack_range", attackRange);

        int attackInterval = getAttackInterval(stack, tag);
        ModTooltips.addStat(stack, tooltip, "whirling_strike.attack_interval", attackInterval / 20.0);

        float maxDamageCap = getMaxDamageCap(stack, tag);
        ModTooltips.addStat(stack, tooltip, "whirling_strike.max_damage_cap",
                ModTooltips.roundToTwoDecimals(maxDamageCap));

        double dualWieldMultiplier = getDualWieldDamageMultiplier(stack, tag);
        ModTooltips.addStat(stack, tooltip, "whirling_strike.dual_wield_multiplier", dualWieldMultiplier);
    }

    private static float getBaseDamage(ItemStack stack, CompoundTag tag) {
        
        if (tag != null && tag.contains("WhirlingStrikeBaseDamage")) {
            return tag.getFloat("WhirlingStrikeBaseDamage");
        }
        
        var jsonDamage = TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.base_damage)
                .filter(Objects::nonNull);
        if (jsonDamage.isPresent()) {
            return jsonDamage.get();
        }
        
        float weaponDamage = getWeaponAttackDamage(stack);
        float multiplier = getDamageMultiplier(stack, tag);
        return weaponDamage * multiplier;
    }

    private static float getWeaponAttackDamage(ItemStack stack) {
        var modifiers = stack.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY);
        if (modifiers.modifiers().isEmpty()) {
            modifiers = stack.getItem().getDefaultAttributeModifiers(stack);
        }
        double damage = 1.0;
        for (var entry : modifiers.modifiers()) {
            if (entry.attribute().is(Attributes.ATTACK_DAMAGE)
                    && entry.slot().test(EquipmentSlot.MAINHAND)
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                damage += entry.modifier().amount();
            }
        }
        return (float) damage;
    }

    private static float getDamageMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WhirlingStrikeDamageMultiplier")) {
            return tag.getFloat("WhirlingStrikeDamageMultiplier");
        }
        return TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.damage_multiplier)
                .filter(Objects::nonNull)
                .orElse(0.5f);
    }

    private static double getBaseAttackRange(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WhirlingStrikeBaseAttackRange")) {
            return tag.getDouble("WhirlingStrikeBaseAttackRange");
        }
        return TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.base_attack_range)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WHIRLING_STRIKE_BASE_ATTACK_RANGE.get());
    }

    private static int getAttackInterval(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WhirlingStrikeAttackInterval")) {
            return Math.max(1, tag.getInt("WhirlingStrikeAttackInterval"));
        }
        return TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.attack_interval)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WHIRLING_STRIKE_ATTACK_INTERVAL.get());
    }

    private static float getMaxDamageCap(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WhirlingStrikeMaxDamageCap")) {
            return tag.getFloat("WhirlingStrikeMaxDamageCap");
        }
        return TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.max_damage_cap)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WHIRLING_STRIKE_MAX_DAMAGE_CAP.get().floatValue());
    }

    private static double getDualWieldDamageMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WhirlingStrikeDualWieldDamageMultiplier")) {
            return tag.getDouble("WhirlingStrikeDualWieldDamageMultiplier");
        }
        Float jsonValue = TraitModifierData.getWhirlingStrike(stack)
                .map((e) -> e.dual_wield_damage_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonValue != null) {
            return jsonValue.doubleValue();
        }
        return TraitsConfig.WHIRLING_STRIKE_DUAL_WIELD_DAMAGE_MULTIPLIER.get();
    }
}
