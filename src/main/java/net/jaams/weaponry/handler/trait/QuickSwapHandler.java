package net.jaams.weaponry.handler.trait;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.util.ModTraits;

import java.util.List;

public class QuickSwapHandler {

    public static void switchItem(LevelAccessor level, double x, double y, double z, Entity entity, Item originalItem,
            Item targetItem, int mainHandCooldown, int offHandCooldown, String soundEvent, List<Item> noCooldownItems,
            List<ResourceLocation> noCooldownTags) {
        switchItem(level, x, y, z, entity, originalItem, targetItem, mainHandCooldown, offHandCooldown, soundEvent,
                noCooldownItems, noCooldownTags, null);
    }

    public static void switchItem(LevelAccessor level, double x, double y, double z, Entity entity, Item originalItem,
            Item targetItem, int mainHandCooldown, int offHandCooldown, String soundEvent, List<Item> noCooldownItems,
            List<ResourceLocation> noCooldownTags, InteractionHand usedHand) {
        if (level.isClientSide() || entity == null || !(entity instanceof LivingEntity livingEntity)
                || originalItem == null || targetItem == null || targetItem == net.minecraft.world.item.Items.AIR) {
            return;
        }
        ItemStack mainHandItem = livingEntity.getMainHandItem();
        ItemStack offHandItem = livingEntity.getOffhandItem();
        boolean mainHandChanged = false;
        boolean offHandChanged = false;


        if (usedHand == null || usedHand == InteractionHand.MAIN_HAND) {
            if (canSwitchItem(mainHandItem, livingEntity, originalItem)) {
                mainHandChanged = true;
            }
        }
        if (usedHand == null || usedHand == InteractionHand.OFF_HAND) {
            if (canSwitchItem(offHandItem, livingEntity, originalItem)) {
                offHandChanged = true;
            }
        }
        if (mainHandChanged) {
            Item originalMainItem = mainHandItem.getItem();
            replaceItem(livingEntity, InteractionHand.MAIN_HAND, new ItemStack(targetItem), mainHandItem);
            playSoundOnServer((ServerLevel) level, x, y, z, soundEvent, entity);
            if (livingEntity.isUnderWater() && level instanceof ServerLevel serverLevel) {
                spawnBubbleParticles(serverLevel, livingEntity, InteractionHand.MAIN_HAND);
            }
            if (livingEntity instanceof Player player) {
                applyCooldown(player, originalMainItem, mainHandCooldown);
                applyCooldown(player, targetItem, mainHandCooldown);
            }
        }
        if (offHandChanged) {
            Item originalOffItem = offHandItem.getItem();
            replaceItem(livingEntity, InteractionHand.OFF_HAND, new ItemStack(targetItem), offHandItem);
            playSoundOnServer((ServerLevel) level, x, y, z, soundEvent, entity);
            if (livingEntity.isUnderWater() && level instanceof ServerLevel serverLevel) {
                spawnBubbleParticles(serverLevel, livingEntity, InteractionHand.OFF_HAND);
            }
            if (livingEntity instanceof Player player) {
                applyCooldown(player, originalOffItem, offHandCooldown);
                applyCooldown(player, targetItem, offHandCooldown);
            }
        }
        if (mainHandChanged || offHandChanged) {
            ItemStack otherHandItem = mainHandChanged ? offHandItem : mainHandItem;
            Item otherItem = otherHandItem.getItem();
            boolean applyCooldownToOther = true;
            if (!otherHandItem.isEmpty()) {
                if (noCooldownItems != null && !noCooldownItems.isEmpty()) {
                    for (Item item : noCooldownItems) {
                        if (otherItem == item) {
                            applyCooldownToOther = false;
                            break;
                        }
                    }
                }
                if (applyCooldownToOther && noCooldownTags != null && !noCooldownTags.isEmpty()) {
                    for (ResourceLocation tag : noCooldownTags) {
                        if (otherHandItem.is(ItemTags.create(tag))) {
                            applyCooldownToOther = false;
                            break;
                        }
                    }
                }
            }
            if (applyCooldownToOther && otherItem != targetItem && livingEntity instanceof Player player) {
                int otherCooldown = mainHandChanged ? offHandCooldown : mainHandCooldown;
                applyCooldown(player, otherItem, otherCooldown);
            }
        }
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.getInventory().setChanged();
        }
    }

    private static boolean canSwitchItem(ItemStack stack, LivingEntity entity, Item originalItem) {
        if (stack.isEmpty() || stack.getItem() != originalItem) {
            return false;
        }
        Item currentItem = stack.getItem();
        if (entity instanceof Player player) {
            return !player.getCooldowns().isOnCooldown(currentItem);
        }
        return true;
    }

    private static void replaceItem(LivingEntity entity, InteractionHand hand, ItemStack newItem, ItemStack oldItem) {
        // Transfer all data components (enchantments, custom name, durability,
        // attribute modifiers, mod data, etc.) from the original weapon to its
        // swapped form so no data is lost during the quick swap.
        newItem.applyComponentsAndValidate(oldItem.getComponentsPatch());
        newItem.setCount(oldItem.getCount());
        entity.setItemInHand(hand, newItem);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.getInventory().setChanged();
        }
    }

    private static void applyCooldown(Player player, Item item, int cooldownTicks) {
        if (player instanceof ServerPlayer serverPlayer && item != null && cooldownTicks > 0
                && !serverPlayer.getCooldowns().isOnCooldown(item)) {
            boolean globalCooldown = TraitsConfig.QUICK_SWAP_GLOBAL_COOLDOWN.get();
            if (globalCooldown) {
                for (ItemStack stack : serverPlayer.getInventory().items) {
                    if (!stack.isEmpty() && ModTraits.isQuickSwapItem(stack)) {
                        serverPlayer.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
                    }
                }
                ItemStack offHandStack = serverPlayer.getOffhandItem();
                if (!offHandStack.isEmpty() && ModTraits.isQuickSwapItem(offHandStack)) {
                    serverPlayer.getCooldowns().addCooldown(offHandStack.getItem(), cooldownTicks);
                }
                for (ItemStack stack : serverPlayer.getInventory().armor) {
                    if (!stack.isEmpty() && ModTraits.isQuickSwapItem(stack)) {
                        serverPlayer.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
                    }
                }
            } else {
                serverPlayer.getCooldowns().addCooldown(item, cooldownTicks);
            }
        }
    }

    private static void playSoundOnServer(ServerLevel level, double x, double y, double z, String soundEvent,
            Entity entity) {
        if (soundEvent == null || soundEvent.isEmpty())
            return;
        SoundEvent sound = SoundEvent.createVariableRangeEvent(ResourceLocation.parse(soundEvent));
        SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
        level.playSound(null, x, y, z, sound, source, 1.0F, 1.0F);
    }

    private static void spawnBubbleParticles(ServerLevel serverLevel, LivingEntity entity, InteractionHand hand) {
        if (serverLevel.isClientSide()) {
            return;
        }
        RandomSource random = serverLevel.random;
        Vec3 handPos = getHandPosition(entity, hand);
        int particleCount = 5;
        for (int i = 0; i < particleCount; i++) {
            serverLevel.sendParticles(ParticleTypes.BUBBLE, handPos.x + (random.nextDouble() - 0.5) * 0.2,
                    handPos.y + (random.nextDouble() - 0.5) * 0.2, handPos.z + (random.nextDouble() - 0.5) * 0.2, 1,
                    0.0, 0.0, 0.0, 0.0);
        }
    }

    private static Vec3 getHandPosition(LivingEntity entity, InteractionHand hand) {
        Vec3 lookVec = entity.getLookAngle().normalize();
        double offsetX = hand == InteractionHand.MAIN_HAND ? 0.4 : -0.4;
        Vec3 sideVec = lookVec.cross(new Vec3(0, 1, 0)).normalize().scale(offsetX);
        return new Vec3(entity.getX() + sideVec.x, entity.getY() + entity.getEyeHeight() * 0.5,
                entity.getZ() + sideVec.z);
    }
}
