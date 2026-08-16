package net.jaams.weaponry.handler.trait;

import net.jaams.weaponry.capability.amount.AmountProvider;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModUtils;

public class SelfEffectHandler {



    public static void handleBarbedHandle(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.BARBED_HANDLE.get()) {
            return;
        }
        float factor = getBarbedHandleDamageReturnFactor(stack);
        if (factor <= 0.0F)
            return;
        float dealtDamage = AmountProvider.get(attacker)
                .map(amount -> amount.getDamage()).orElse(0.0F);
        if (dealtDamage <= 0.0F)
            return;
        float returnDamage = dealtDamage * factor;
        if (returnDamage > 0.0F) {
            attacker.hurt(attacker.damageSources().generic(), returnDamage);
        }
    }

    private static float getBarbedHandleDamageReturnFactor(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BarbedHandleDamageReturnFactor")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("BarbedHandleDamageReturnFactor")));
        }
        return TraitModifierData.getBarbedHandle(stack)
                .map(entry -> entry.damage_return_factor)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BARBED_HANDLE_DAMAGE_RETURN_FACTOR.get().floatValue());
    }



    public static void handleBrittleHandle(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.BRITTLE_HANDLE.get()) {
            return;
        }
        int extra = getBrittleHandleExtraDurability(stack);
        if (extra > 0) {
            ModUtils.applyTraitDurabilityCost(stack, attacker, extra, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
    }

    private static int getBrittleHandleExtraDurability(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BrittleHandleExtraDurability")) {
            return Math.max(0, tag.getInt("BrittleHandleExtraDurability"));
        }
        return TraitModifierData.getBrittleHandle(stack)
                .map(entry -> entry.extra_durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BRITTLE_HANDLE_EXTRA_DURABILITY.get());
    }



    public static void handleDetonating(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.DETONATING.get()) {
            return;
        }
        InteractionHand hand = getHandHoldingStack(attacker, stack);
        if (hand == null) {
            return;
        }
        float chance = getDetonatingExplodeChance(stack);
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        float explosionPower = getDetonatingExplosionPower(stack);
        boolean breakBlocks = getDetonatingBreakBlocks(stack);
        Level.ExplosionInteraction interaction = breakBlocks
                ? Level.ExplosionInteraction.BLOCK
                : Level.ExplosionInteraction.NONE;

        ItemStack stackForParticles = stack.copy();

        boolean isCreative = attacker instanceof Player player && player.getAbilities().instabuild;
        if (!isCreative) {



            stack.shrink(1);
            if (stack.isEmpty()) {
                attacker.setItemInHand(hand, ItemStack.EMPTY);
            }
        }

        boolean damageOwner = getDetonatingDamageOwner(stack);
        Entity explosionSource = damageOwner ? null : attacker;
        Vec3 explosionPos = attacker.getEyePosition().add(attacker.getLookAngle().scale(0.5));
        attacker.level().explode(explosionSource, explosionPos.x,
                explosionPos.y,
                explosionPos.z, explosionPower, false, interaction);

        Level level = attacker.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 look = attacker.getLookAngle();
            Vec3 particlePos = attacker.getEyePosition().add(look.scale(1.2));
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stackForParticles),
                    particlePos.x, particlePos.y, particlePos.z,
                    12, 0.5, 0.5, 0.5, 0.2);
        }
    }

    private static float getDetonatingExplodeChance(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("DetonatingExplodeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("DetonatingExplodeChance")));
        }
        return TraitModifierData.getDetonating(stack)
                .map(entry -> entry.explode_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DETONATING_EXPLODE_CHANCE.get().floatValue());
    }

    private static float getDetonatingExplosionPower(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("DetonatingExplosionPower")) {
            return Math.max(0.0F, tag.getFloat("DetonatingExplosionPower"));
        }
        return TraitModifierData.getDetonating(stack)
                .map(entry -> entry.explosion_power)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DETONATING_EXPLOSION_POWER.get().floatValue());
    }

    private static boolean getDetonatingBreakBlocks(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("DetonatingBreakBlocks")) {
            return tag.getBoolean("DetonatingBreakBlocks");
        }
        return TraitModifierData.getDetonating(stack)
                .map(entry -> entry.break_blocks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DETONATING_BREAK_BLOCKS.get());
    }

    private static boolean getDetonatingDamageOwner(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("DetonatingDamageOwner")) {
            return tag.getBoolean("DetonatingDamageOwner");
        }
        return TraitModifierData.getDetonating(stack)
                .map(entry -> entry.damage_owner)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DETONATING_DAMAGE_OWNER.get());
    }



    public static void handleExhausting(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.EXHAUSTING.get()) {
            return;
        }
        float exhaustion = getExhaustingExhaustion(stack);
        if (attacker instanceof Player player) {
            player.getFoodData().addExhaustion(exhaustion);
        }
    }

    private static float getExhaustingExhaustion(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("ExhaustingExhaustion")) {
            return Math.max(0.0F, tag.getFloat("ExhaustingExhaustion"));
        }
        return TraitModifierData.getExhausting(stack)
                .map(entry -> entry.exhaustion_amount)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.EXHAUSTING_EXHAUSTION.get().floatValue());
    }



    public static void handleFragility(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.FRAGILITY.get()) {
            return;
        }

        if (attacker instanceof Player player && player.getAbilities().instabuild) {
            return;
        }
        InteractionHand hand = getHandHoldingStack(attacker, stack);
        if (hand == null) {
            return;
        }

        
        float minThreshold = getFragilityMinDurabilityThreshold(stack);
        if (minThreshold > 0.0F) {
            int maxDamage = stack.getMaxDamage();
            if (maxDamage <= 0) return;
            float currentDurabilityRatio = 1.0F - (float) stack.getDamageValue() / (float) maxDamage;
            if (currentDurabilityRatio > minThreshold) {
                return;
            }
        }

        float breakChance = getFragilityBreakChance(stack);
        if (attacker.getRandom().nextFloat() >= breakChance) {
            return;
        }

        
        ItemStack remainingItemStack = getFragilityRemainingItemStack(stack);

        ItemStack stackForParticles = stack.copy();

        stack.shrink(1);
        if (stack.isEmpty()) {
            attacker.setItemInHand(hand, ItemStack.EMPTY);
        }

        Level level = attacker.level();
        if (!level.isClientSide) {
            Vec3 look = attacker.getLookAngle();
            Vec3 pos = attacker.getEyePosition().add(look.scale(1.2));

            
            if (!remainingItemStack.isEmpty()) {
                float remainingChance = getFragilityRemainingItemChance(stack);
                if (attacker.getRandom().nextFloat() < remainingChance) {
                    ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y - 0.2, pos.z, remainingItemStack.copy());
                    itemEntity.setPickUpDelay(10);
                    itemEntity.setDeltaMovement(
                            (level.random.nextDouble() - 0.5) * 0.1,
                            level.random.nextDouble() * 0.1 + 0.1,
                            (level.random.nextDouble() - 0.5) * 0.1
                    );
                    level.addFreshEntity(itemEntity);
                }
            }

            level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stackForParticles),
                        pos.x, pos.y, pos.z,
                        8, 0.4, 0.4, 0.4, 0.1);
            }
        }
    }

    private static float getFragilityBreakChance(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("FragilityBreakChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("FragilityBreakChance")));
        }
        return TraitModifierData.getFragility(stack)
                .map(entry -> entry.break_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.FRAGILITY_BREAK_CHANCE.get().floatValue());
    }

    private static float getFragilityMinDurabilityThreshold(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("FragilityMinDurabilityThreshold")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("FragilityMinDurabilityThreshold")));
        }
        return TraitModifierData.getFragility(stack)
                .map(entry -> entry.min_durability_threshold)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.FRAGILITY_MIN_DURABILITY_THRESHOLD.get().floatValue());
    }

    private static ItemStack getFragilityRemainingItemStack(ItemStack stack) {
        
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("FragilityRemainingItem")) {
            String itemId = tag.getString("FragilityRemainingItem");
            if (!itemId.isEmpty()) {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemId));
                if (item != null && item != Items.AIR) {
                    int count = 1;
                    if (tag.contains("FragilityRemainingItemCount")) {
                        count = Math.max(1, tag.getInt("FragilityRemainingItemCount"));
                    }
                    ItemStack result = new ItemStack(item);
                    result.setCount(count);
                    return result;
                }
            }
        }
        
        return TraitModifierData.getFragility(stack)
                .map(entry -> entry.remaining_item)
                .filter(java.util.Objects::nonNull)
                .map(ri -> {
                    if (ri.item != null && !ri.item.isEmpty()) {
                        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(ri.item));
                        if (item != null && item != Items.AIR) {
                            int count = (ri.count != null && ri.count > 0) ? ri.count : 1;
                            ItemStack result = new ItemStack(item);
                            result.setCount(count);
                            return result;
                        }
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    private static float getFragilityRemainingItemChance(ItemStack stack) {
        
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("FragilityRemainingItemChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("FragilityRemainingItemChance")));
        }
        
        Float jsonChance = TraitModifierData.getFragility(stack)
                .map(entry -> entry.remaining_item)
                .filter(java.util.Objects::nonNull)
                .map(ri -> ri.chance)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonChance != null) {
            return Math.max(0.0F, Math.min(1.0F, jsonChance));
        }
        
        return TraitsConfig.FRAGILITY_REMAINING_ITEM_CHANCE.get().floatValue();
    }



    public static void handleOverstrain(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.OVERSTRAIN.get() || !EffectsConfig.DEPLETION.get()) {
            return;
        }
        float chance = getOverstrainChance(stack);
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int duration = getOverstrainEffectDuration(stack);
        int amplifier = getOverstrainEffectAmplifier(stack);
        attacker.addEffect(new MobEffectInstance(ModMobEffects.DEPLETION, duration, amplifier));
    }

    private static float getOverstrainChance(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("OverstrainChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("OverstrainChance")));
        }
        return TraitModifierData.getOverstrain(stack)
                .map(entry -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.OVERSTRAIN_CHANCE.get().floatValue());
    }

    private static int getOverstrainEffectDuration(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("OverstrainEffectDuration")) {
            return Math.max(1, tag.getInt("OverstrainEffectDuration"));
        }
        return TraitModifierData.getOverstrain(stack)
                .map(entry -> entry.effect_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.OVERSTRAIN_EFFECT_DURATION.get());
    }

    private static int getOverstrainEffectAmplifier(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("OverstrainEffectAmplifier")) {
            return Math.max(0, tag.getInt("OverstrainEffectAmplifier"));
        }
        return TraitModifierData.getOverstrain(stack)
                .map(entry -> entry.effect_amplifier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.OVERSTRAIN_EFFECT_AMPLIFIER.get());
    }



    public static void handleSlippery(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.SLIPPERY.get()) {
            return;
        }
        if (TraitsConfig.SLIPPERY_SECURE_GRIP_CANCELS.get() && ModEnchantments.level(stack, ModEnchantments.SECURE_GRIP) > 0) {
            return;
        }
        InteractionHand hand = getHandHoldingStack(attacker, stack);
        if (hand == null) {
            return;
        }
        float disarmChance = getSlipperyDisarmChance(stack);
        if (attacker.getRandom().nextFloat() >= disarmChance) {
            return;
        }
        ItemStack dropped = stack.copy();
        attacker.setItemInHand(hand, ItemStack.EMPTY);
        Level level = attacker.level();
        if (!dropped.isEmpty()) {

            Vec3 look = attacker.getLookAngle();
            double throwDistance = getSlipperyThrowDistance(stack);
            Vec3 dropPos = attacker.getEyePosition().add(look.scale(throwDistance));
            ItemEntity itemEntity = new ItemEntity(level, dropPos.x, dropPos.y - 0.2, dropPos.z, dropped);
            itemEntity.setPickUpDelay(40);

            double velocityScale = Math.min(throwDistance * 0.33, 1.0);
            itemEntity.setDeltaMovement(look.x * velocityScale, 0.15, look.z * velocityScale);
            level.addFreshEntity(itemEntity);
        }

        attacker.swing(hand);


        if (!level.isClientSide) {
            Vec3 handPos = attacker.position().add(0, attacker.getBbHeight() * 0.6, 0);
            Vec3 look = attacker.getLookAngle();
            Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
            double side = hand == InteractionHand.MAIN_HAND ? 0.3 : -0.3;
            handPos = handPos.add(right.scale(side)).add(look.scale(0.8));

            level.playSound(null, handPos.x, handPos.y, handPos.z,
                    SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    private static float getSlipperyDisarmChance(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("SlipperyChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("SlipperyChance")));
        }
        return TraitModifierData.getSlippery(stack)
                .map(entry -> entry.disarm_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLIPPERY_CHANCE.get().floatValue());
    }

    private static float getSlipperyThrowDistance(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("SlipperyThrowDistance")) {
            return (float) Math.max(0.0, tag.getDouble("SlipperyThrowDistance"));
        }
        return TraitModifierData.getSlippery(stack)
                .map(entry -> entry.throw_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLIPPERY_THROW_DISTANCE.get().floatValue());
    }



    private static InteractionHand getHandHoldingStack(LivingEntity entity, ItemStack stack) {
        if (ItemStack.isSameItemSameComponents(entity.getMainHandItem(), stack)) {
            return InteractionHand.MAIN_HAND;
        }
        if (ItemStack.isSameItemSameComponents(entity.getOffhandItem(), stack)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }
}
