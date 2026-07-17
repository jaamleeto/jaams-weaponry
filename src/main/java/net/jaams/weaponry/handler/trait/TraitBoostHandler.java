package net.jaams.weaponry.handler.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TraitBoostHandler {

    private static final UUID RAPID_BOOST_UUID = UUID.fromString("9a6a6e7c-8b3c-4f2e-9d1a-5b7c3f8e6d2a");
    private static final UUID POWER_BOOST_UUID = UUID.fromString("1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e");

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty())
            return;
        EquipmentSlot slot = event.getSlotType();
        if (slot != EquipmentSlot.MAINHAND)
            return;

        CompoundTag tag = stack.getTag();
        if (tag == null)
            return;

        
        if (TraitsConfig.RAPID_BOOST.get() && ModTraits.isRapidBoostItem(stack)) {
            int hits = tag.getInt("RapidBoostHits");
            if (hits > 0) {
                int maxHits = getRapidBoostMaxHits(stack);
                float increment = getRapidBoostIncrement(stack);
                float boost = Math.min(hits, maxHits) * increment;
                if (boost > 0) {
                    Attribute attribute = Attributes.ATTACK_SPEED;
                    AttributeModifier modifier = new AttributeModifier(
                            RAPID_BOOST_UUID, "Rapid Boost", boost, AttributeModifier.Operation.ADDITION);
                    event.addModifier(attribute, modifier);
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
                    Attribute attribute = Attributes.ATTACK_DAMAGE;
                    AttributeModifier modifier = new AttributeModifier(
                            POWER_BOOST_UUID, "Power Boost", boost, AttributeModifier.Operation.ADDITION);
                    event.addModifier(attribute, modifier);
                }
            }
        }
    }

    private static int getRapidBoostMaxHits(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("RapidBoostMaxHits")) {
            return Math.max(1, tag.getInt("RapidBoostMaxHits"));
        }
        return TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_MAX_HITS.get());
    }

    private static float getRapidBoostIncrement(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("RapidBoostIncrement")) {
            return Math.max(0.0F, tag.getFloat("RapidBoostIncrement"));
        }
        return TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.increment)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_INCREMENT.get().floatValue());
    }

    private static int getPowerBoostMaxHits(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PowerBoostMaxHits")) {
            return Math.max(1, tag.getInt("PowerBoostMaxHits"));
        }
        return TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_MAX_HITS.get());
    }

    private static float getPowerBoostIncrement(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PowerBoostIncrement")) {
            return Math.max(0.0F, tag.getFloat("PowerBoostIncrement"));
        }
        return TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.increment)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_INCREMENT.get().floatValue());
    }
}
