package net.jaams.weaponry.handler.trait;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class BreakingHandler {

    public static void damageArmorPieces(LivingEntity entity, int damageAmount, EquipmentSlot... slots) {
        if (entity == null || entity.level().isClientSide || damageAmount <= 0) {
            return;
        }
        if (BreakingHandler.isExemptFromDamage(entity)) {
            return;
        }
        for (EquipmentSlot slot : slots) {
            ItemStack armorPiece = entity.getItemBySlot(slot);
            if (armorPiece != null && armorPiece.isDamageableItem()) {
                armorPiece.hurtAndBreak(damageAmount, entity, slot);
            }
        }
    }

    public static void damageHeldItem(LivingEntity entity, int damageAmount) {
        if (entity == null || entity.level().isClientSide || damageAmount <= 0) {
            return;
        }
        if (BreakingHandler.isExemptFromDamage(entity)) {
            return;
        }
        ItemStack mainHandItem = entity.getMainHandItem();
        if (mainHandItem != null && mainHandItem.isDamageableItem()) {
            mainHandItem.hurtAndBreak(damageAmount, entity, EquipmentSlot.MAINHAND);
        }
        ItemStack offHandItem = entity.getOffhandItem();
        if (offHandItem != null && offHandItem.isDamageableItem()) {
            offHandItem.hurtAndBreak(damageAmount, entity, EquipmentSlot.OFFHAND);
        }
    }

    public static boolean isExemptFromDamage(LivingEntity entity) {
        ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entityKey != null && "dummmmmmy:target_dummy".equals(entityKey.toString());
    }
}
