package net.jaams.weaponry.handler.trait;

import net.jaams.weaponry.util.ModComponents;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemDamageHandler {

    public static void handleArmorBreaker(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.ARMOR_BREAKER.get()) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        float chance = getArmorBreakerChance(stack, tag);
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int damage = getArmorBreakerDurabilityDamage(stack, tag);
        List<EquipmentSlot> slots = getArmorBreakerAffectedSlots(stack, tag);
        if (slots.isEmpty()) {
            slots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
        }
        if (target instanceof Player playerTarget && playerTarget.getAbilities().instabuild) {
            return;
        }
        Set<String> immuneItems = getArmorBreakerImmuneItems(stack);
        for (EquipmentSlot slot : slots) {
            ItemStack armorPiece = target.getItemBySlot(slot);
            if (armorPiece.isEmpty() || !armorPiece.isDamageableItem()) {
                continue;
            }
            if (isItemImmune(armorPiece, immuneItems)) {
                continue;
            }
            armorPiece.hurtAndBreak(damage, target, slot);
        }
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    public static void handleBladeBreaker(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.BLADE_BREAKER.get()) {
            return;
        }
        if (!hasWeaponInHands(target)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        float chance = getBladeBreakerChance(stack, tag);
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int damage = getBladeBreakerDurabilityDamage(stack, tag);
        if (target instanceof Player playerTarget && playerTarget.getAbilities().instabuild) {
            return;
        }
        Set<String> immuneItems = getBladeBreakerImmuneItems(stack);
        
        ItemStack mainHand = target.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.isDamageableItem() && !isItemImmune(mainHand, immuneItems)) {
            mainHand.hurtAndBreak(damage, target, EquipmentSlot.MAINHAND);
        }
        
        ItemStack offHand = target.getOffhandItem();
        if (!offHand.isEmpty() && offHand.isDamageableItem() && !isItemImmune(offHand, immuneItems)) {
            offHand.hurtAndBreak(damage, target, EquipmentSlot.OFFHAND);
        }
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.4F);
    }

    private static boolean hasWeaponInHands(LivingEntity target) {
        return isWeaponItem(target.getMainHandItem()) || isWeaponItem(target.getOffhandItem());
    }

    private static boolean isWeaponItem(ItemStack stack) {
        return net.jaams.weaponry.util.ModUtils.attackDamageModifierSum(stack, EquipmentSlot.MAINHAND) > 0;
    }

    private static boolean isItemImmune(ItemStack itemStack, Set<String> immuneItems) {
        if (immuneItems == null || immuneItems.isEmpty()) {
            return false;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        return itemId != null && immuneItems.contains(itemId.toString());
    }

    private static Set<String> getArmorBreakerImmuneItems(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        
        if (tag != null && tag.contains("ArmorBreakerImmuneItems")) {
            ListTag list = tag.getList("ArmorBreakerImmuneItems", Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getArmorBreaker(stack)
                .map((entry) -> entry.immune_items)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.ARMOR_BREAKER_IMMUNE_ITEMS.get());
    }

    private static Set<String> getBladeBreakerImmuneItems(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        
        if (tag != null && tag.contains("BladeBreakerImmuneItems")) {
            ListTag list = tag.getList("BladeBreakerImmuneItems", Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getBladeBreaker(stack)
                .map((entry) -> entry.immune_items)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.BLADE_BREAKER_IMMUNE_ITEMS.get());
    }

    private static float getArmorBreakerChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ArmorBreakerChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("ArmorBreakerChance")));
        }
        return TraitModifierData.getArmorBreaker(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ARMOR_BREAKER_CHANCE.get().floatValue());
    }

    private static int getArmorBreakerDurabilityDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ArmorBreakerDurabilityDamage")) {
            return Math.max(1, tag.getInt("ArmorBreakerDurabilityDamage"));
        }
        return TraitModifierData.getArmorBreaker(stack)
                .map((entry) -> entry.durability_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ARMOR_BREAKER_DURABILITY_DAMAGE.get());
    }

    private static List<EquipmentSlot> getArmorBreakerAffectedSlots(ItemStack stack, CompoundTag tag) {
        List<? extends String> slotNames;
        if (tag != null && tag.contains("ArmorBreakerSlots")) {
            String slotsStr = tag.getString("ArmorBreakerSlots");
            slotNames = List.of(slotsStr.split(","));
        } else if (TraitModifierData.getArmorBreaker(stack)
                .map((entry) -> entry.slots)
                .filter(java.util.Objects::nonNull)
                .isPresent()) {
            slotNames = TraitModifierData.getArmorBreaker(stack).get().slots;
        } else {
            slotNames = TraitsConfig.ARMOR_BREAKER_SLOTS.get();
        }
        return slotNames
                .stream()
                .map(
                        (name) -> switch (name.toLowerCase(Locale.ROOT)) {
                            case "head", "helmet" -> EquipmentSlot.HEAD;
                            case "chest", "chestplate" -> EquipmentSlot.CHEST;
                            case "legs", "leggings" -> EquipmentSlot.LEGS;
                            case "feet", "boots" -> EquipmentSlot.FEET;
                            case "mainhand" -> EquipmentSlot.MAINHAND;
                            case "offhand" -> EquipmentSlot.OFFHAND;
                            default -> null;
                        })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private static float getBladeBreakerChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BladeBreakerChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("BladeBreakerChance")));
        }
        return TraitModifierData.getBladeBreaker(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BLADE_BREAKER_CHANCE.get().floatValue());
    }

    private static int getBladeBreakerDurabilityDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BladeBreakerDurabilityDamage")) {
            return Math.max(1, tag.getInt("BladeBreakerDurabilityDamage"));
        }
        return TraitModifierData.getBladeBreaker(stack)
                .map((entry) -> entry.durability_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BLADE_BREAKER_DURABILITY_DAMAGE.get());
    }
}
