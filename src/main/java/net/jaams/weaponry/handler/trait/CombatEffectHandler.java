package net.jaams.weaponry.handler.trait;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class CombatEffectHandler {

    public static void handleDisablingStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.DISABLING_STRIKE.get()) {
            return;
        }
        float chance = getDisablingStrikeChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int cooldown = getDisablingStrikeCooldown(stack, stack.getTag());
        
        
        if (target.isUsingItem()) {
            target.stopUsingItem();
        }
        
        if (target instanceof Player victim) {
            if (!victim.getMainHandItem().isEmpty()) {
                victim.getCooldowns().addCooldown(victim.getMainHandItem().getItem(), cooldown);
            }
            if (!victim.getOffhandItem().isEmpty()) {
                victim.getCooldowns().addCooldown(victim.getOffhandItem().getItem(), cooldown);
            }
        }
    }

    public static void handleDismount(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.DISMOUNT.get()) {
            return;
        }
        if (!target.isPassenger()) {
            return;
        }
        
        if (!canDismountEntity(target, stack)) {
            return;
        }
        float chance = getDismountChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        target.stopRiding();
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.5F);
    }

    public static void handleOverwhelmingStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.OVERWHELMING_STRIKE.get()) {
            return;
        }
        float chance = getOverwhelmingStrikeChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int duration = getOverwhelmingStrikeDuration(stack, stack.getTag());
        target.addEffect(new MobEffectInstance(ModMobEffects.KNOCKED_OUT.get(), duration, 0));
    }

    public static void handleSuppressingStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.SUPPRESSING_STRIKE.get()) {
            return;
        }
        if (!EffectsConfig.INCAPABLE.get()) {
            return;
        }
        float chance = getSuppressingStrikeChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        int duration = getSuppressingStrikeDuration(stack, stack.getTag());
        
        if (target.isUsingItem()) {
            target.stopUsingItem();
        }
        
        
        target.addEffect(new MobEffectInstance(ModMobEffects.INCAPABLE.get(), duration, 0));
    }

    public static void handleThroughStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.THROUGH_STRIKE.get()) {
            return;
        }
        float chance = getThroughStrikeChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        target.invulnerableTime = 0;
    }

    public static void handleSparringStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.SPARRING_STRIKE.get()) {
            return;
        }
        if (target instanceof Player || (target instanceof Mob mob && mob.getTarget() != attacker)) {
            ModUtils.cancelDamage(target, attacker);
        }
    }

    

    public static void handleCleansingStrike(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!TraitsConfig.CLEANSING_STRIKE.get() || !ModTraits.isCleansingStrikeItem(stack)) {
            return;
        }
        if (attacker.level().isClientSide) {
            return;
        }

        float chance = getCleansingStrikeChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance || target.getActiveEffects().isEmpty()) {
            return;
        }

        
        Set<String> blacklisted = getCleansingStrikeBlacklistedEffects(stack, stack.getTag());
        MobEffectInstance slowness = null;
        boolean hasBaneOfArthropods = stack.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS) > 0;
        if (hasBaneOfArthropods && target.getMobType() == MobType.ARTHROPOD) {
            slowness = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }

        
        List<MobEffectInstance> effectsToKeep = new ArrayList<>();
        for (MobEffectInstance effect : new ArrayList<>(target.getActiveEffects())) {
            String effectId = getEffectId(effect);
            if (blacklisted.contains(effectId)) {
                effectsToKeep.add(effect);
            } else if (slowness != null && effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN) {
                effectsToKeep.add(effect);
            }
        }

        
        target.removeAllEffects();
        for (MobEffectInstance effect : effectsToKeep) {
            target.addEffect(new MobEffectInstance(effect));
        }

        
        if (stack.is(ModTags.BROOMS)) {
            ModUtils.playSound(target, "jaams_weaponry:broom_clean");
        } else {
            ModUtils.playSound(target, "jaams_weaponry:swoosh_air");
        }

        
        Level level = target.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            spawnCleansingParticles(serverLevel, target, ParticleTypes.CLOUD, 5);
        }
    }

    

    private static float getDisablingStrikeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisablingStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("DisablingStrikeChance")));
        }
        return TraitModifierData.getDisablingStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISABLING_STRIKE_CHANCE.get().floatValue());
    }

    private static int getDisablingStrikeCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisablingStrikeCooldown")) {
            return Math.max(1, tag.getInt("DisablingStrikeCooldown"));
        }
        return TraitModifierData.getDisablingStrike(stack)
                .map((entry) -> entry.cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISABLING_STRIKE_COOLDOWN.get());
    }

    private static float getDismountChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DismountChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("DismountChance")));
        }
        return TraitModifierData.getDismount(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISMOUNT_CHANCE.get().floatValue());
    }

    private static boolean canDismountEntity(LivingEntity target, ItemStack stack) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (entityId == null) {
            return true;
        }
        Set<String> protectedEntities = getNonDismountableEntities(stack);
        return !protectedEntities.contains(entityId.toString());
    }

    private static Set<String> getNonDismountableEntities(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        
        if (tag != null && tag.contains("DismountNonDismountableEntities")) {
            ListTag list = tag.getList("DismountNonDismountableEntities", Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getDismount(stack)
                .map((entry) -> entry.non_dismountable_entities)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.DISMOUNT_NON_DISMOUNTABLE_ENTITIES.get());
    }

    private static float getOverwhelmingStrikeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("OverwhelmingStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("OverwhelmingStrikeChance")));
        }
        return TraitModifierData.getOverwhelmingStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.OVERWHELMING_STRIKE_CHANCE.get().floatValue());
    }

    private static float getSuppressingStrikeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SuppressingStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("SuppressingStrikeChance")));
        }
        return TraitModifierData.getSuppressingStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SUPPRESSING_STRIKE_CHANCE.get().floatValue());
    }

    private static int getSuppressingStrikeDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SuppressingStrikeDuration")) {
            return Math.max(1, tag.getInt("SuppressingStrikeDuration"));
        }
        return TraitModifierData.getSuppressingStrike(stack)
                .map((entry) -> entry.duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SUPPRESSING_STRIKE_DURATION.get());
    }

    private static int getOverwhelmingStrikeDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("OverwhelmingStrikeDuration")) {
            return Math.max(1, tag.getInt("OverwhelmingStrikeDuration"));
        }
        return TraitModifierData.getOverwhelmingStrike(stack)
                .map((entry) -> entry.duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.OVERWHELMING_STRIKE_DURATION.get());
    }

    private static float getThroughStrikeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ThroughStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("ThroughStrikeChance")));
        }
        return TraitModifierData.getThroughStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.THROUGH_STRIKE_CHANCE.get().floatValue());
    }

    

    private static float getCleansingStrikeChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("CleansingStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("CleansingStrikeChance")));
        }
        return TraitModifierData.getCleansingStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.CLEANSING_STRIKE_CHANCE.get().floatValue());
    }

    private static Set<String> getCleansingStrikeBlacklistedEffects(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("CleansingStrikeBlacklist")) {
            net.minecraft.nbt.ListTag list = tag.getList("CleansingStrikeBlacklist", net.minecraft.nbt.Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        Set<String> fromData = TraitModifierData.getCleansingStrike(stack)
                .map((entry) -> entry.blacklisted_effects)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        return new HashSet<>(TraitsConfig.CLEANSING_STRIKE_BLACKLISTED_EFFECTS.get());
    }

    private static String getEffectId(MobEffectInstance effect) {
        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect());
        return key != null ? key.toString() : "";
    }

    private static void spawnCleansingParticles(ServerLevel serverLevel, LivingEntity entity,
            ParticleOptions particleType, int particleCount) {
        RandomSource random = serverLevel.random;
        for (int i = 0; i < particleCount; i++) {
            double xOffset = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            double yOffset = entity.getY() + entity.getBbHeight() * 0.5
                    + (random.nextDouble() - 0.5) * entity.getBbHeight() * 0.5;
            double zOffset = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            serverLevel.sendParticles(particleType, xOffset, yOffset, zOffset, 1, 0.1, 0.1, 0.1, 0.05);
        }
    }
}
