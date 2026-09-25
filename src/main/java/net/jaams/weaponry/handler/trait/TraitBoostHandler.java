package net.jaams.weaponry.handler.trait;

import net.minecraft.resources.ResourceLocation;
import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber
public class TraitBoostHandler {

    private static final ResourceLocation RAPID_BOOST_ID = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "rapid_boost");
    private static final ResourceLocation POWER_BOOST_ID = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "power_boost");

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty())
            return;

        CompoundTag tag = ModComponents.get(stack);
        if (tag == null)
            return;

        if (TraitsConfig.RAPID_BOOST.get() && ModTraits.isRapidBoostItem(stack)) {
            int hits = tag.getInt("RapidBoostHits");
            if (hits > 0) {
                int maxHits = getRapidBoostMaxHits(stack);
                float increment = getRapidBoostIncrement(stack);
                float boost = Math.min(hits, maxHits) * increment;
                if (boost > 0) {
                    applyBoostToOriginalAttribute(event, Attributes.ATTACK_SPEED,
                            EquipmentSlotGroup.MAINHAND, boost, RAPID_BOOST_ID);
                }
            }
        }

        if (TraitsConfig.POWER_BOOST.get() && ModTraits.isPowerBoostItem(stack)) {
            int hits = tag.getInt("PowerBoostHits");
            if (hits > 0) {
                int maxHits = getPowerBoostMaxHits(stack);
                float increment = getPowerBoostIncrement(stack);
                float boost = Math.min(hits, maxHits) * increment;
                if (boost > 0) {
                    applyBoostToOriginalAttribute(event, Attributes.ATTACK_DAMAGE,
                            EquipmentSlotGroup.MAINHAND, boost, POWER_BOOST_ID);
                }
            }
        }
    }

    /**
     * Applies the boost directly onto the item's existing attribute modifier (the original
     * attribute) for the given attribute/slot, merging the amount into it instead of adding a
     * separate modifier, in the same way item modifiers (ItemModifierHandler) do. Falls back to
     * adding a new modifier when the item has no existing modifier for that attribute.
     */
    private static void applyBoostToOriginalAttribute(ItemAttributeModifierEvent event,
            Holder<Attribute> attribute, EquipmentSlotGroup slotGroup, double amount,
            ResourceLocation fallbackId) {
        for (ItemAttributeModifiers.Entry existing : List.copyOf(event.getModifiers())) {
            if (existing.attribute().is(attribute) && existing.slot() == slotGroup) {
                AttributeModifier original = existing.modifier();
                AttributeModifier merged = new AttributeModifier(
                        original.id(),
                        original.amount() + amount,
                        original.operation());
                event.replaceModifier(attribute, merged, existing.slot());
                return;
            }
        }
        AttributeModifier modifier = new AttributeModifier(
                fallbackId, amount, AttributeModifier.Operation.ADD_VALUE);
        event.addModifier(attribute, modifier, slotGroup);
    }

    private static int getRapidBoostMaxHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("RapidBoostMaxHits")) {
            return Math.max(1, tag.getInt("RapidBoostMaxHits"));
        }
        return TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_MAX_HITS.get());
    }

    private static float getRapidBoostIncrement(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("RapidBoostIncrement")) {
            return Math.max(0.0F, tag.getFloat("RapidBoostIncrement"));
        }
        return TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.increment)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_INCREMENT.get().floatValue());
    }

    private static int getPowerBoostMaxHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("PowerBoostMaxHits")) {
            return Math.max(1, tag.getInt("PowerBoostMaxHits"));
        }
        return TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_MAX_HITS.get());
    }

    private static float getPowerBoostIncrement(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("PowerBoostIncrement")) {
            return Math.max(0.0F, tag.getFloat("PowerBoostIncrement"));
        }
        return TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.increment)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_INCREMENT.get().floatValue());
    }
}
