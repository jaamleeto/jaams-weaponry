package net.jaams.weaponry.handler.trait;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.jaams.weaponry.handler.trait.BonusDamageHandler;
import net.jaams.weaponry.handler.trait.CombatEffectHandler;
import net.jaams.weaponry.handler.trait.DisarmHandler;
import net.jaams.weaponry.handler.trait.ItemDamageHandler;
import net.jaams.weaponry.handler.trait.SelfEffectHandler;
import net.jaams.weaponry.handler.trait.MovementHandler;
import net.jaams.weaponry.handler.trait.DecapitationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.configuration.common.TraitsConfig;


@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobTraitEventsHandler {

    
    private static boolean processing = false;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        
        
        if (processing) {
            return;
        }
        
        if (!(event.getSource().getEntity() instanceof Mob)) {
            return;
        }
        Mob attacker = (Mob) event.getSource().getEntity();
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity target = (LivingEntity) event.getEntity();
        if (attacker.level().isClientSide) {
            return;
        }

        processing = true;
        try {

            ItemStack mainHand = attacker.getMainHandItem();
            if (mainHand.isEmpty()) {
                return;
            }

            
            float attackStrength = 1.0F;

            

            
            if (TraitsConfig.AQUATIC_GRUDGE.get() && ModTraits.isAquaticGrudgeItem(mainHand)) {
                BonusDamageHandler.handleAquaticGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.ARTHROPOD_GRUDGE.get() && ModTraits.isArthropodGrudgeItem(mainHand)) {
                BonusDamageHandler.handleArthropodGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.UNDEAD_GRUDGE.get() && ModTraits.isUndeadGrudgeItem(mainHand)) {
                BonusDamageHandler.handleUndeadGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.TRAITOR_GRUDGE.get() && ModTraits.isTraitorGrudgeItem(mainHand)) {
                BonusDamageHandler.handleTraitorGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.SNOUT_GRUDGE.get() && ModTraits.isSnoutGrudgeItem(mainHand)) {
                BonusDamageHandler.handleSnoutGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.BONE_GRUDGE.get() && ModTraits.isBoneGrudgeItem(mainHand)) {
                BonusDamageHandler.handleBoneGrudge(target, attacker, mainHand);
            }
            if (TraitsConfig.ROTTEN_GRUDGE.get() && ModTraits.isRottenGrudgeItem(mainHand)) {
                BonusDamageHandler.handleRottenGrudge(target, attacker, mainHand);
            }

            
            if (TraitsConfig.ANTI_AERIAL.get() && ModTraits.isAntiAerialItem(mainHand)) {
                BonusDamageHandler.handleAntiAerial(target, attacker, mainHand);
            }
            if (TraitsConfig.DUELIST.get() && ModTraits.isDuelistItem(mainHand)) {
                boolean requireCharged = getDuelistRequireCharged(mainHand);
                BonusDamageHandler.handleDuelist(target, attacker, mainHand, requireCharged, attackStrength);
            }
            if (TraitsConfig.PIERCER_STRIKE.get() && ModTraits.isPiercerStrikeItem(mainHand)) {
                boolean requireCharged = getPiercerStrikeRequireCharged(mainHand);
                BonusDamageHandler.handlePiercerStrike(target, attacker, mainHand, requireCharged, attackStrength);
            }
            if (TraitsConfig.REACH_ADVANTAGE.get() && ModTraits.isReachAdvantageItem(mainHand)) {
                boolean requireCharged = getReachAdvantageRequireCharged(mainHand);
                BonusDamageHandler.handleReachAdvantage(target, attacker, mainHand, requireCharged, attackStrength);
            }
            if (TraitsConfig.THREAT_RESPONSE.get() && ModTraits.isThreatResponseItem(mainHand)) {
                boolean requireCharged = getThreatResponseRequireCharged(mainHand);
                BonusDamageHandler.handleThreatResponse(target, attacker, mainHand, requireCharged, attackStrength);
            }
            if (TraitsConfig.BACKSTAB.get() && ModTraits.isBackstabItem(mainHand)) {
                BonusDamageHandler.handleBackstab(target, attacker, mainHand);
            }

            

            if (TraitsConfig.ACROBATIC_LUNGE.get() && ModTraits.isAcrobaticLungeItem(mainHand)) {
                float strength = getAcrobaticLungeStrength(mainHand);
                float maxDistance = getAcrobaticLungeMaxDistance(mainHand);
                float maxVerticalPull = getAcrobaticLungeMaxVerticalPull(mainHand);
                float distanceScaling = getAcrobaticLungeDistanceScaling(mainHand);
                MovementHandler.pullTowardsEnemy(target, attacker, attackStrength, mainHand,
                        strength, strength, maxDistance, 2.0, 1.0F, 0.1,
                        maxVerticalPull, 0.5F, distanceScaling);
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
            }
            if (TraitsConfig.DEXTEROUS_LUNGE.get() && ModTraits.isDexterousLungeItem(mainHand)) {
                float pullStrength = getDexterousLungePullStrength(mainHand);
                float attractStrength = getDexterousLungeAttractStrength(mainHand);
                float maxDistance = getDexterousLungeMaxDistance(mainHand);
                float maxVerticalPull = getDexterousLungeMaxVerticalPull(mainHand);
                float distanceScaling = getDexterousLungeDistanceScaling(mainHand);
                MovementHandler.pullTowardsEnemy(target, attacker, attackStrength, mainHand,
                        pullStrength, attractStrength, maxDistance, 2.0, 1.0F, 0.1,
                        maxVerticalPull, 0.5F, distanceScaling);
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
            }
            if (TraitsConfig.PULL_LUNGE.get() && ModTraits.isPullLungeItem(mainHand)) {
                float strength = getPullLungeStrength(mainHand);
                float maxDistance = getPullLungeMaxDistance(mainHand);
                float maxVerticalPull = getPullLungeMaxVerticalPull(mainHand);
                float distanceScaling = getPullLungeDistanceScaling(mainHand);
                MovementHandler.pullEnemyTowardsPlayer(target, attacker, attackStrength, mainHand,
                        strength, maxDistance, 2.0, 1.0F, 0.1,
                        maxVerticalPull, 0.5F, distanceScaling);
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
            }
            if (TraitsConfig.DISENGAGE.get() && ModTraits.isDisengageItem(mainHand)) {
                float strength = getDisengageStrength(mainHand);
                float maxDistance = getDisengageMaxDistance(mainHand);
                float maxVerticalPush = getDisengageMaxVerticalPush(mainHand);
                float distanceScaling = getDisengageDistanceScaling(mainHand);
                MovementHandler.pushAwayFromTarget(target, attacker, attackStrength, mainHand,
                        strength, maxDistance, 2.0, 1.0F, 0.1,
                        maxVerticalPush, 0.5F, distanceScaling);
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.7F);
            }

            

            if (TraitsConfig.DISARM.get() && ModTraits.isDisarmItem(mainHand)) {
                float chance = getDisarmChance(mainHand);
                if (attacker.getRandom().nextFloat() < chance) {
                    DisarmHandler.disarmEnemy(target, attacker);
                }
            }
            if (TraitsConfig.DISABLING_STRIKE.get() && ModTraits.isDisablingStrikeItem(mainHand)) {
                CombatEffectHandler.handleDisablingStrike(target, attacker, mainHand);
            }
            if (TraitsConfig.DISMOUNT.get() && ModTraits.isDismountItem(mainHand)) {
                CombatEffectHandler.handleDismount(target, attacker, mainHand);
            }
            if (TraitsConfig.OVERWHELMING_STRIKE.get() && ModTraits.isOverwhelmingStrikeItem(mainHand)) {
                CombatEffectHandler.handleOverwhelmingStrike(target, attacker, mainHand);
            }
            if (TraitsConfig.SUPPRESSING_STRIKE.get() && ModTraits.isSuppressingStrikeItem(mainHand)) {
                CombatEffectHandler.handleSuppressingStrike(target, attacker, mainHand);
            }
            if (TraitsConfig.THROUGH_STRIKE.get() && ModTraits.isThroughStrikeItem(mainHand)) {
                CombatEffectHandler.handleThroughStrike(target, attacker, mainHand);
            }
            if (TraitsConfig.SPARRING_STRIKE.get() && ModTraits.isSparringStrikeItem(mainHand)) {
                CombatEffectHandler.handleSparringStrike(target, attacker, mainHand);
            }
            if (TraitsConfig.CLEANSING_STRIKE.get() && ModTraits.isCleansingStrikeItem(mainHand)) {
                CombatEffectHandler.handleCleansingStrike(target, attacker, mainHand);
            }

            

            if (TraitsConfig.ARMOR_BREAKER.get() && ModTraits.isArmorBreakerItem(mainHand)) {
                ItemDamageHandler.handleArmorBreaker(target, attacker, mainHand);
            }
            if (TraitsConfig.BLADE_BREAKER.get() && ModTraits.isBladeBreakerItem(mainHand)) {
                ItemDamageHandler.handleBladeBreaker(target, attacker, mainHand);
            }

            

            if (TraitsConfig.BARBED_HANDLE.get() && ModTraits.isBarbedHandleItem(mainHand)) {
                SelfEffectHandler.handleBarbedHandle(target, attacker, mainHand);
            }
            if (TraitsConfig.BRITTLE_HANDLE.get() && ModTraits.isBrittleHandleItem(mainHand)) {
                SelfEffectHandler.handleBrittleHandle(target, attacker, mainHand);
            }
            if (TraitsConfig.DETONATING.get() && ModTraits.isDetonatingItem(mainHand)) {
                SelfEffectHandler.handleDetonating(target, attacker, mainHand);
            }
            if (TraitsConfig.EXHAUSTING.get() && ModTraits.isExhaustingItem(mainHand)) {
                SelfEffectHandler.handleExhausting(target, attacker, mainHand);
            }
            if (TraitsConfig.FRAGILITY.get() && ModTraits.isFragilityItem(mainHand)) {
                SelfEffectHandler.handleFragility(target, attacker, mainHand);
            }
            if (TraitsConfig.OVERSTRAIN.get() && ModTraits.isOverstrainItem(mainHand)) {
                SelfEffectHandler.handleOverstrain(target, attacker, mainHand);
            }
            if (TraitsConfig.SLIPPERY.get() && ModTraits.isSlipperyItem(mainHand)) {
                SelfEffectHandler.handleSlippery(target, attacker, mainHand);
            }
        } finally {
            processing = false;
        }
    }

    

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        
        
        if (!(event.getSource().getEntity() instanceof Mob attacker)) {
            return;
        }
        
        LivingEntity target = event.getEntity();
        if (attacker.level().isClientSide) {
            return;
        }
        ItemStack mainHand = attacker.getMainHandItem();
        if (mainHand.isEmpty()) {
            return;
        }
        if (!TraitsConfig.DECAPITATION.get() || !ModTraits.isDecapitationItem(mainHand)) {
            return;
        }
        Level world = target.level();
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();
        DecapitationHandler.handleDecapitation(world, x, y, z, target, attacker, mainHand);
    }

    

    private static boolean getDuelistRequireCharged(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("DuelistFullyCharged")) {
            return tag.getBoolean("DuelistFullyCharged");
        }
        return net.jaams.weaponry.data.TraitModifierData.getDuelist(stack)
                .map(e -> e.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DUELIST_REQUIRE_FULLY_CHARGED.get());
    }

    private static boolean getPiercerStrikeRequireCharged(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("PiercerStrikeFullyCharged")) {
            return tag.getBoolean("PiercerStrikeFullyCharged");
        }
        return net.jaams.weaponry.data.TraitModifierData.getPiercerStrike(stack)
                .map(e -> e.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCER_STRIKE_REQUIRE_FULLY_CHARGED.get());
    }

    private static boolean getReachAdvantageRequireCharged(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("ReachAdvantageFullyCharged")) {
            return tag.getBoolean("ReachAdvantageFullyCharged");
        }
        return net.jaams.weaponry.data.TraitModifierData.getReachAdvantage(stack)
                .map(e -> e.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_REQUIRE_FULLY_CHARGED.get());
    }

    private static boolean getThreatResponseRequireCharged(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("ThreatResponseFullyCharged")) {
            return tag.getBoolean("ThreatResponseFullyCharged");
        }
        return net.jaams.weaponry.data.TraitModifierData.getThreatResponse(stack)
                .map(e -> e.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.THREAT_RESPONSE_REQUIRE_FULLY_CHARGED.get());
    }

    

    private static float getAcrobaticLungeStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AcrobaticLungeStrength")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeStrength"));
        }
        return net.jaams.weaponry.data.TraitModifierData.getAcrobaticLunge(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ACROBATIC_LUNGE_STRENGTH.get().floatValue());
    }

    private static float getAcrobaticLungeMaxDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AcrobaticLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("AcrobaticLungeMaxDistance"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    private static float getAcrobaticLungeMaxVerticalPull(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AcrobaticLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeMaxVerticalPull"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    private static float getAcrobaticLungeDistanceScaling(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AcrobaticLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeDistanceScaling"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    private static int getAcrobaticLungeDurabilityCost(ItemStack stack) {
        return 0;
    }

    private static float getDexterousLungePullStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DexterousLungePullStrength")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungePullStrength"));
        }
        return net.jaams.weaponry.data.TraitModifierData.getDexterousLunge(stack)
                .map(entry -> entry.pull_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_PULL_STRENGTH.get().floatValue());
    }

    private static float getDexterousLungeAttractStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DexterousLungeAttractStrength")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeAttractStrength"));
        }
        return net.jaams.weaponry.data.TraitModifierData.getDexterousLunge(stack)
                .map(entry -> entry.attract_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_ATTRACT_STRENGTH.get().floatValue());
    }

    private static float getDexterousLungeMaxDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DexterousLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("DexterousLungeMaxDistance"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    private static float getDexterousLungeMaxVerticalPull(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DexterousLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeMaxVerticalPull"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    private static float getDexterousLungeDistanceScaling(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DexterousLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeDistanceScaling"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    private static int getDexterousLungeDurabilityCost(ItemStack stack) {
        return 0;
    }

    private static float getPullLungeStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PullLungeStrength")) {
            return Math.max(0.0F, tag.getFloat("PullLungeStrength"));
        }
        return net.jaams.weaponry.data.TraitModifierData.getPullLunge(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PULL_LUNGE_STRENGTH.get().floatValue());
    }

    private static float getPullLungeMaxDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PullLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("PullLungeMaxDistance"));
        }
        return TraitsConfig.PULL_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    private static float getPullLungeMaxVerticalPull(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PullLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("PullLungeMaxVerticalPull"));
        }
        return TraitsConfig.PULL_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    private static float getPullLungeDistanceScaling(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PullLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("PullLungeDistanceScaling"));
        }
        return TraitsConfig.PULL_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    private static int getPullLungeDurabilityCost(ItemStack stack) {
        return 0;
    }

    private static float getDisengageStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DisengageStrength")) {
            return Math.max(0.0F, tag.getFloat("DisengageStrength"));
        }
        return net.jaams.weaponry.data.TraitModifierData.getDisengage(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISENGAGE_STRENGTH.get().floatValue());
    }

    private static float getDisengageMaxDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DisengageMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("DisengageMaxDistance"));
        }
        return TraitsConfig.DISENGAGE_MAX_DISTANCE.get().floatValue();
    }

    private static float getDisengageMaxVerticalPush(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DisengageMaxVerticalPush")) {
            return Math.max(0.0F, tag.getFloat("DisengageMaxVerticalPush"));
        }
        return TraitsConfig.DISENGAGE_MAX_VERTICAL_PUSH.get().floatValue();
    }

    private static float getDisengageDistanceScaling(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DisengageDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("DisengageDistanceScaling"));
        }
        return TraitsConfig.DISENGAGE_DISTANCE_SCALING.get().floatValue();
    }

    private static int getDisengageDurabilityCost(ItemStack stack) {
        return 0;
    }

    private static float getDisarmChance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DisarmChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("DisarmChance")));
        }
        return net.jaams.weaponry.data.TraitModifierData.getDisarm(stack)
                .map(entry -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISARM_CHANCE.get().floatValue());
    }
}
