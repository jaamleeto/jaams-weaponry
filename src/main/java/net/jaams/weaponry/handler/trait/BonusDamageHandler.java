package net.jaams.weaponry.handler.trait;

import net.jaams.weaponry.init.ModEnchantments;

import net.minecraft.tags.EntityTypeTags;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class BonusDamageHandler {

    

    private static void applyBonusDamageWithCost(LivingEntity attacker, LivingEntity target, ItemStack stack,
            float bonusDamage, int durabilityCost) {
        if (bonusDamage <= 0)
            return;
        AmountProvider.get(attacker).ifPresent(amount -> {
            ModUtils.applyBonusDamage(attacker, target, stack, bonusDamage);
            if (durabilityCost > 0) {
                ModUtils.applyTraitDurabilityCost(stack, attacker, durabilityCost, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            }
        });
    }

    private static void playHitSound(LivingEntity attacker, LivingEntity target, float pitch) {
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, pitch);
    }

    

    
    public static void handleAntiAerial(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.ANTI_AERIAL.get())
            return;
        if (!matchesAntiAerialTarget(target))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getAntiAerialBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.4F);
        }
    }

    private static boolean matchesAntiAerialTarget(LivingEntity target) {
        if (target.getType().is(ModTags.ANTI_AERIAL_TARGETS))
            return true;
        if (target instanceof FlyingMob)
            return true;
        if (target.isFallFlying())
            return true;
        return !target.onGround();
    }

    private static float getAntiAerialBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AntiAerialBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("AntiAerialBonusDamage"));
        }
        return TraitModifierData.getAntiAerial(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ANTI_AERIAL_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleAquaticGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.AQUATIC_GRUDGE.get())
            return;
        if (!matchesAquaticTarget(target))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getAquaticGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static boolean matchesAquaticTarget(LivingEntity target) {
        if (target.getType().is(ModTags.AQUATIC_GRUDGE_TARGETS))
            return true;
        if (target.getType().is(EntityTypeTags.AQUATIC) || target instanceof WaterAnimal)
            return true;
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString().toLowerCase();
        return entityId.contains("water") || entityId.contains("aquatic") || entityId.contains("fish");
    }

    private static float getAquaticGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AquaticGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("AquaticGrudgeBonusDamage"));
        }
        return TraitModifierData.getAquaticGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AQUATIC_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleArthropodGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.ARTHROPOD_GRUDGE.get())
            return;
        if (!target.getType().is(ModTags.ARTHROPOD_GRUDGE_TARGETS) && !target.getType().is(EntityTypeTags.ARTHROPOD))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getArthropodGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getArthropodGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ArthropodGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("ArthropodGrudgeBonusDamage"));
        }
        return TraitModifierData.getArthropodGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ARTHROPOD_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleBoneGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.BONE_GRUDGE.get())
            return;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString().toLowerCase();
        if (!target.getType().is(ModTags.BONE_GRUDGE_TARGETS) && !entityId.contains("skeleton"))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getBoneGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getBoneGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BoneGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("BoneGrudgeBonusDamage"));
        }
        return TraitModifierData.getBoneGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BONE_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleDuelist(LivingEntity target, LivingEntity attacker, ItemStack stack,
            boolean requireFullyCharged, float attackStrength) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.DUELIST.get())
            return;
        if (requireFullyCharged && attackStrength < 0.9F)
            return;
        if (!ModUtils.isEntityArmed(target))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getDuelistBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 0.9F);
        }
    }

    private static float getDuelistBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DuelistBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("DuelistBonusDamage"));
        }
        return TraitModifierData.getDuelist(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DUELIST_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handlePiercerStrike(LivingEntity target, LivingEntity attacker, ItemStack stack,
            boolean requireFullyCharged, float attackStrength) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.PIERCER_STRIKE.get())
            return;
        if (requireFullyCharged && attackStrength < 0.9F)
            return;

        CompoundTag tag = ModComponents.get(stack);
        int minArmor = getPiercerStrikeMinArmor(stack, tag);
        if (target.getArmorValue() < minArmor)
            return;

        float bonusDamage = getPiercerStrikeBonusDamage(stack, tag);
        if (bonusDamage <= 0)
            return;

        AmountProvider.get(attacker).ifPresent(amount -> {
            ModUtils.applyPiercingDamage(attacker, target, stack, bonusDamage);
            playHitSound(attacker, target, 0.8F);
        });
    }

    private static int getPiercerStrikeMinArmor(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercerStrikeMinArmor")) {
            return Math.max(0, tag.getInt("PiercerStrikeMinArmor"));
        }
        return TraitModifierData.getPiercerStrike(stack)
                .map(entry -> entry.min_armor)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCER_STRIKE_MIN_ARMOR.get());
    }

    private static float getPiercerStrikeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercerStrikeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("PiercerStrikeBonusDamage"));
        }
        return TraitModifierData.getPiercerStrike(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCER_STRIKE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleReachAdvantage(LivingEntity target, LivingEntity attacker, ItemStack stack,
            boolean requireFullyCharged, float attackStrength) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.REACH_ADVANTAGE.get())
            return;
        if (requireFullyCharged && attackStrength < 0.9F)
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamagePerBlock = getReachAdvantageBonusDamage(stack, tag);
        if (bonusDamagePerBlock <= 0)
            return;

        float minDistance = getReachAdvantageMinDistance(stack, tag);
        float maxDistance = getReachAdvantageMaxDistance(stack, tag);
        float calculatedBonus = ModUtils.calculateDistanceBonus(attacker, target, false,
                maxDistance, minDistance, bonusDamagePerBlock, 1.0F);
        if (calculatedBonus > 0) {
            applyBonusDamageWithCost(attacker, target, stack, calculatedBonus, 0);
            playHitSound(attacker, target, 1.1F);
        }
    }

    private static float getReachAdvantageBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("ReachAdvantageBonusDamage"));
        }
        return TraitModifierData.getReachAdvantage(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_BONUS_DAMAGE.get().floatValue());
    }

    private static float getReachAdvantageMinDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageMinDistance")) {
            return Math.max(0.0F, tag.getFloat("ReachAdvantageMinDistance"));
        }
        return TraitModifierData.getReachAdvantage(stack)
                .map(entry -> entry.min_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_MIN_DISTANCE.get().floatValue());
    }

    private static float getReachAdvantageMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageMaxDistance")) {
            return Math.max(0.0F, tag.getFloat("ReachAdvantageMaxDistance"));
        }
        return TraitModifierData.getReachAdvantage(stack)
                .map(entry -> entry.max_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_MAX_DISTANCE.get().floatValue());
    }



    

    
    public static void handleRottenGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.ROTTEN_GRUDGE.get())
            return;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString().toLowerCase();
        if (!target.getType().is(ModTags.ROTTEN_GRUDGE_TARGETS) && !entityId.contains("zombie"))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getRottenGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getRottenGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("RottenGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("RottenGrudgeBonusDamage"));
        }
        return TraitModifierData.getRottenGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ROTTEN_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleSnoutGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.SNOUT_GRUDGE.get())
            return;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString().toLowerCase();
        if (!target.getType().is(ModTags.SNOUT_GRUDGE_TARGETS) && !entityId.contains("piglin")
                && !entityId.contains("hoglin"))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getSnoutGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getSnoutGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SnoutGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("SnoutGrudgeBonusDamage"));
        }
        return TraitModifierData.getSnoutGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SNOUT_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleThreatResponse(LivingEntity target, LivingEntity attacker, ItemStack stack,
            boolean requireFullyCharged, float attackStrength) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.THREAT_RESPONSE.get())
            return;
        if (requireFullyCharged && attackStrength < 0.9F)
            return;
        if (!(target instanceof Mob mob) || mob.getTarget() != attacker)
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getThreatResponseBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.0F);
        }
    }

    private static float getThreatResponseBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ThreatResponseBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("ThreatResponseBonusDamage"));
        }
        return TraitModifierData.getThreatResponse(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.THREAT_RESPONSE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleTraitorGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.TRAITOR_GRUDGE.get())
            return;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString().toLowerCase();
        if (!target.getType().is(ModTags.TRAITOR_GRUDGE_TARGETS) && !entityId.contains("pillager"))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getTraitorGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getTraitorGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("TraitorGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("TraitorGrudgeBonusDamage"));
        }
        return TraitModifierData.getTraitorGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.TRAITOR_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleUndeadGrudge(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (target == null || attacker == null)
            return;
        if (!TraitsConfig.UNDEAD_GRUDGE.get())
            return;
        if (!target.getType().is(ModTags.UNDEAD_GRUDGE_TARGETS) && !target.getType().is(EntityTypeTags.UNDEAD))
            return;

        CompoundTag tag = ModComponents.get(stack);
        float bonusDamage = getUndeadGrudgeBonusDamage(stack, tag);
        if (bonusDamage > 0) {
            applyBonusDamageWithCost(attacker, target, stack, bonusDamage, 0);
            playHitSound(attacker, target, 1.2F);
        }
    }

    private static float getUndeadGrudgeBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("UndeadGrudgeBonusDamage")) {
            return Math.max(0.0F, tag.getFloat("UndeadGrudgeBonusDamage"));
        }
        return TraitModifierData.getUndeadGrudge(stack)
                .map(entry -> entry.bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.UNDEAD_GRUDGE_BONUS_DAMAGE.get().floatValue());
    }



    

    
    public static void handleBackstab(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.BACKSTAB.get() || !ModTraits.isBackstabItem(stack)) {
            return;
        }
        if (target == null || attacker == null || attacker.level().isClientSide) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        double maxDistance = getBackstabMaxDistance(stack, tag);
        double maxAngle = getBackstabMaxAngle(stack, tag);
        if (isBackstab(attacker, target, maxDistance, maxAngle)) {
            float baseDamage = calculateBackstabBaseDamage(stack, attacker);
            applyBackstabEffects(stack, target, attacker, attacker.level(), baseDamage, tag);
        }
    }

    private static boolean isBackstab(LivingEntity attacker, LivingEntity target, double maxDistance, double maxAngle) {
        if (attacker == null || target == null || !target.isAlive() || !attacker.isAlive()) {
            return false;
        }
        double distance = attacker.distanceTo(target);
        if (distance > maxDistance) {
            return false;
        }
        if (!attacker.hasLineOfSight(target)) {
            return false;
        }
        Vec3 attackDirection = attacker.getLookAngle().normalize();
        Vec3 targetDirection = target.getLookAngle().normalize();
        double dotProduct = attackDirection.x * targetDirection.x + attackDirection.z * targetDirection.z;
        double angle = Math.acos(Mth.clamp(dotProduct, -1.0, 1.0));
        boolean withinAngle = angle < maxAngle;
        boolean withinGrace = angle < maxAngle * 1.5 && attacker.getDeltaMovement().lengthSqr() > 0.1;
        return withinAngle || withinGrace;
    }

    private static float calculateBackstabBaseDamage(ItemStack stack, LivingEntity attacker) {
        double dmgSum = net.jaams.weaponry.util.ModUtils.attackDamageModifierSum(stack, EquipmentSlot.MAINHAND);
        float baseDamage = dmgSum > 0 ? (float) dmgSum : 1.0f;
        return (baseDamage + (float) (attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0)) * 1.3F;
    }

    private static void applyBackstabEffects(ItemStack stack, LivingEntity target, LivingEntity attacker,
            Level level, float baseDamage, CompoundTag tag) {
        float durabilityPenalty = getBackstabDurabilityPenalty(stack, tag);
        float weaknessChance = getBackstabWeaknessChance(stack, tag);
        int weaknessDuration = getBackstabWeaknessDuration(stack, tag);
        int weaknessLevel = getBackstabWeaknessLevel(stack, tag);
        float multiplierNormal = getBackstabMultiplierNormal(stack, tag);
        float multiplierSneaking = getBackstabMultiplierSneaking(stack, tag);
        float multiplierInvisible = getBackstabMultiplierInvisible(stack, tag);
        float multiplierSneakingInvisible = getBackstabMultiplierSneakingInvisible(stack, tag);
        float darknessBonus = getBackstabDarknessBonus(stack, tag);
        float movingTargetPenalty = getBackstabMovingTargetPenalty(stack, tag);

        performBackstab(attacker, target, stack, level, baseDamage, durabilityPenalty, weaknessChance,
                weaknessDuration, weaknessLevel, multiplierNormal, multiplierSneaking, multiplierInvisible,
                multiplierSneakingInvisible, darknessBonus, movingTargetPenalty);
    }

    private static void performBackstab(LivingEntity attacker, LivingEntity target, ItemStack stack,
            Level level, float baseDamage, float durabilityPenalty, float weaknessChance, int weaknessDuration,
            int weaknessLevel, float multiplierNormal, float multiplierSneaking, float multiplierInvisible,
            float multiplierSneakingInvisible, float darknessBonus, float movingTargetPenalty) {
        float backstabMultiplier = calculateBackstabMultiplier(attacker, target, level,
                multiplierNormal, multiplierSneaking, multiplierInvisible, multiplierSneakingInvisible,
                darknessBonus, movingTargetPenalty);
        float enchantmentBonus = ModEnchantments.damageBonus(stack, target);
        float bonusDamage = baseDamage * (backstabMultiplier - 1);
        float additionalDamage = bonusDamage + enchantmentBonus;
        applyBackstabFinalEffects(attacker, target, stack, level, additionalDamage, durabilityPenalty,
                weaknessChance, weaknessDuration, weaknessLevel);
    }

    private static float calculateBackstabMultiplier(LivingEntity attacker, LivingEntity target, Level level,
            float multiplierNormal, float multiplierSneaking, float multiplierInvisible,
            float multiplierSneakingInvisible, float darknessBonus, float movingTargetPenalty) {
        float multiplier = multiplierNormal;
        boolean isSneaking = attacker.isCrouching();
        boolean isInvisible = attacker.hasEffect(MobEffects.INVISIBILITY);
        if (isSneaking && isInvisible) {
            multiplier = Math.max(multiplier, multiplierSneakingInvisible);
        } else if (isInvisible) {
            multiplier = Math.max(multiplier, multiplierInvisible);
        } else if (isSneaking) {
            multiplier = Math.max(multiplier, multiplierSneaking);
        }
        float lightLevel = level.getLightEmission(target.blockPosition());
        if (lightLevel < 7) {
            multiplier += darknessBonus * (1 - lightLevel / 15);
        }
        if (target.getDeltaMovement().lengthSqr() > 0.1) {
            multiplier = Math.max(1.0F, multiplier - movingTargetPenalty);
        }
        return Math.max(1.0F, multiplier);
    }

    private static void applyBackstabFinalEffects(LivingEntity attacker, LivingEntity target, ItemStack stack,
            Level level, float damage, float durabilityPenalty, float weaknessChance, int weaknessDuration,
            int weaknessLevel) {
        ModUtils.applyBackstabDamage(attacker, target, stack, damage);
        if (target.isAlive() && level.random.nextFloat() < weaknessChance) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel - 1));
        }
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1.0F, 1.0F);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            ModUtils.spawnCustomParticlesInFront(attacker, stack,
                    new CustomHitParticleData(1.0F, 1.0F, 1.0F, 0.5F),
                    1.0F, 1.0F, 1.0F, 0.5F, 1.0F, 1, false);
        }
        stack.hurtAndBreak((int) durabilityPenalty, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
    }

    

    private static double getBackstabMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMaxDistance")) {
            return Math.max(0.5, tag.getDouble("BackstabMaxDistance"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.max_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MAX_DISTANCE.get());
    }

    private static double getBackstabMaxAngle(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMaxAngle")) {
            return Math.toRadians(Math.max(0, Math.min(180, tag.getDouble("BackstabMaxAngle"))));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.max_angle)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> Math.toRadians(TraitsConfig.BACKSTAB_MAX_ANGLE.get()));
    }

    private static float getBackstabMultiplierNormal(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierNormal")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierNormal"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_normal)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_NORMAL.get().floatValue());
    }

    private static float getBackstabMultiplierSneaking(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierSneaking")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierSneaking"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_sneaking)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_SNEAKING.get().floatValue());
    }

    private static float getBackstabMultiplierInvisible(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierInvisible")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierInvisible"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_invisible)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_INVISIBLE.get().floatValue());
    }

    private static float getBackstabMultiplierSneakingInvisible(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierSneakingInvisible")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierSneakingInvisible"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_sneaking_invisible)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_SNEAKING_INVISIBLE.get().floatValue());
    }

    private static float getBackstabDarknessBonus(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabDarknessBonus")) {
            return Math.max(0.0F, tag.getFloat("BackstabDarknessBonus"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.darkness_bonus)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_DARKNESS_BONUS.get().floatValue());
    }

    private static float getBackstabMovingTargetPenalty(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMovingTargetPenalty")) {
            return Math.max(0.0F, tag.getFloat("BackstabMovingTargetPenalty"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.moving_target_penalty)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MOVING_TARGET_PENALTY.get().floatValue());
    }

    private static int getBackstabDurabilityPenalty(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabDurabilityPenalty")) {
            return Math.max(0, tag.getInt("BackstabDurabilityPenalty"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.durability_penalty)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_DURABILITY_PENALTY.get());
    }

    private static float getBackstabWeaknessChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("BackstabWeaknessChance")));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_CHANCE.get().floatValue());
    }

    private static int getBackstabWeaknessDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessDuration")) {
            return Math.max(0, tag.getInt("BackstabWeaknessDuration"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_DURATION.get());
    }

    private static int getBackstabWeaknessLevel(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessLevel")) {
            return Math.max(0, tag.getInt("BackstabWeaknessLevel"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_LEVEL.get());
    }
}
