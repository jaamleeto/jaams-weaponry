package net.jaams.weaponry.handler.trait;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;

import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.particle.MiniSweepParticleData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;

public class DisarmHandler {

    private static boolean isDisarmEnabled() {
        return TraitsConfig.DISARM.get();
    }

    
    public static boolean disarmEnemy(LivingEntity target, LivingEntity sourceEntity) {
        if (!isDisarmEnabled())
            return false;
        if (!canDisarmEntity(target, sourceEntity.getMainHandItem())) {
            return false;
        }
        for (EquipmentSlot slot : EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)) {
            if (disarmSlot(target, sourceEntity, slot)) {
                return true;
            }
        }
        return false;
    }

    
    public static boolean disarmSlot(LivingEntity target, LivingEntity sourceEntity, EquipmentSlot slot) {
        if (!isDisarmEnabled())
            return false;
        Level level = target.level();
        ItemStack itemStack = target.getItemBySlot(slot);
        if (itemStack.isEmpty() || !canBeDisarmed(itemStack, sourceEntity.getMainHandItem())) {
            return false;
        }
        ItemStack droppedItem = itemStack.copy();
        if (itemStack.getCount() > 1) {
            droppedItem.setCount(1);
            itemStack.shrink(1);
        } else {
            target.setItemSlot(slot, ItemStack.EMPTY);
        }
        if (!level.isClientSide) {
            
            ItemEntity itemEntity = new ItemEntity(level,
                    target.getX(), target.getY() + 0.5, target.getZ(),
                    droppedItem);
            itemEntity.setPickUpDelay(10);
            level.addFreshEntity(itemEntity);

            
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);

            
            if (level instanceof ServerLevel serverLevel) {
                spawnDisarmParticles(serverLevel, target, slot);
            }
        }
        return true;
    }

    
    
    

    
    public static boolean canDisarmEntity(LivingEntity target, ItemStack weaponStack) {
        if (!isDisarmEnabled())
            return false;
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        if (entityId == null) {
            return true;
        }
        String entityKey = entityId.toString();
        Set<String> protectedEntities = getNonDisarmableEntities(weaponStack);
        return !protectedEntities.contains(entityKey);
    }

    
    public static boolean canBeDisarmed(ItemStack stack, ItemStack weaponStack) {
        if (!isDisarmEnabled())
            return false;
        
        boolean hasSecureGripOrBindingCurse = ModEnchantments.level(stack, ModEnchantments.SECURE_GRIP) > 0
                || ModEnchantments.level(stack, Enchantments.BINDING_CURSE) > 0;
        if (hasSecureGripOrBindingCurse) {
            return false;
        }
        
        ResourceLocation itemID = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemID == null) {
            return true;
        }
        Set<String> nonDisarmableItems = getNonDisarmableItems(weaponStack);
        return !nonDisarmableItems.contains(itemID.toString());
    }

    
    
    

    
    private static Set<String> getNonDisarmableItems(ItemStack weaponStack) {
        CompoundTag tag = ModComponents.get(weaponStack);
        
        if (tag != null && tag.contains("DisarmNonDisarmableItems")) {
            ListTag list = tag.getList("DisarmNonDisarmableItems", Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getDisarm(weaponStack)
                .map(entry -> entry.non_disarmable_items)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.DISARM_NON_DISARMABLE_ITEMS.get());
    }

    
    private static Set<String> getNonDisarmableEntities(ItemStack weaponStack) {
        CompoundTag tag = ModComponents.get(weaponStack);
        
        if (tag != null && tag.contains("DisarmNonDisarmableEntities")) {
            ListTag list = tag.getList("DisarmNonDisarmableEntities", Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getDisarm(weaponStack)
                .map(entry -> entry.non_disarmable_entities)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.DISARM_NON_DISARMABLE_ENTITIES.get());
    }

    
    
    

    private static void spawnDisarmParticles(ServerLevel level, LivingEntity entity, EquipmentSlot slot) {
        RandomSource random = level.random;
        double x = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.getBbHeight() * 0.6;
        double z = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
        level.sendParticles(
                new MiniSweepParticleData(1.0F, 1.0F, 1.0F, 0.4F),
                x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }
}
