package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.util.Mth;
import net.jaams.weaponry.particle.CustomHitParticleData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitBackstabMixin {

    @Unique
    private boolean isBackstabEnabled(ItemStack stack) {
        if (!TraitsConfig.BACKSTAB.get()) {
            return false;
        }
        return ModTraits.isBackstabItem(stack);
    }



    @Unique
    private double getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMaxDistance")) {
            return Math.max(0.5, tag.getDouble("BackstabMaxDistance"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.max_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MAX_DISTANCE.get());
    }

    @Unique
    private double getMaxAngle(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMaxAngle")) {
            return Math.toRadians(Math.max(0, Math.min(180, tag.getDouble("BackstabMaxAngle"))));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.max_angle)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> Math.toRadians(TraitsConfig.BACKSTAB_MAX_ANGLE.get()));
    }

    @Unique
    private float getMultiplierNormal(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierNormal")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierNormal"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_normal)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_NORMAL.get().floatValue());
    }

    @Unique
    private float getMultiplierSneaking(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierSneaking")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierSneaking"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_sneaking)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_SNEAKING.get().floatValue());
    }

    @Unique
    private float getMultiplierInvisible(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierInvisible")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierInvisible"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_invisible)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_INVISIBLE.get().floatValue());
    }

    @Unique
    private float getMultiplierSneakingInvisible(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierSneakingInvisible")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierSneakingInvisible"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_sneaking_invisible)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_SNEAKING_INVISIBLE.get().floatValue());
    }

    @Unique
    private float getDarknessBonus(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabDarknessBonus")) {
            return Math.max(0.0F, tag.getFloat("BackstabDarknessBonus"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.darkness_bonus)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_DARKNESS_BONUS.get().floatValue());
    }

    @Unique
    private float getMovingTargetPenalty(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMovingTargetPenalty")) {
            return Math.max(0.0F, tag.getFloat("BackstabMovingTargetPenalty"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.moving_target_penalty)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MOVING_TARGET_PENALTY.get().floatValue());
    }

    @Unique
    private int getDurabilityPenalty(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabDurabilityPenalty")) {
            return Math.max(0, tag.getInt("BackstabDurabilityPenalty"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.durability_penalty)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_DURABILITY_PENALTY.get());
    }

    @Unique
    private float getWeaknessChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("BackstabWeaknessChance")));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_CHANCE.get().floatValue());
    }

    @Unique
    private int getWeaknessDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessDuration")) {
            return Math.max(0, tag.getInt("BackstabWeaknessDuration"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_DURATION.get());
    }

    @Unique
    private int getWeaknessLevel(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabWeaknessLevel")) {
            return Math.max(0, tag.getInt("BackstabWeaknessLevel"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.weakness_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_WEAKNESS_LEVEL.get());
    }



    @Unique
    private boolean isBackstab(LivingEntity attacker, LivingEntity target, double maxDistance, double maxAngle) {
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

    @Unique
    private double getGracePeriodSeconds(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabGracePeriodSeconds")) {
            return Math.max(0.0, tag.getDouble("BackstabGracePeriodSeconds"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.grace_period_seconds)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_GRACE_PERIOD_SECONDS.get());
    }

    @Unique
    private float calculateBackstabMultiplier(LivingEntity attacker, LivingEntity target, Level level,
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

    @Unique
    private float calculateDamage(ItemStack itemStack) {
        var modifiers = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE);
        var modifier = modifiers.stream().findFirst().orElse(null);
        return modifier != null ? (float) modifier.getAmount() : 1.0f;
    }



    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onBackstabHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null)
            return;
        if (!isBackstabEnabled(stack))
            return;
        CompoundTag tag = stack.getTag();
        double maxDistance = getMaxDistance(stack, tag);
        double maxAngle = getMaxAngle(stack, tag);
        boolean isFullyCharged = attacker.getAttackStrengthScale(0.5F) >= 1.0F;
        if (isFullyCharged && isBackstab(attacker, target, maxDistance, maxAngle)) {
            float baseDamage = calculateDamage(stack)
                    + (float) (attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0);
            applyBackstabEffects(stack, target, attacker, attacker.level(), baseDamage, tag);
        }
    }

    @Unique
    private void applyBackstabEffects(ItemStack stack, LivingEntity entity, LivingEntity sourceentity, Level level,
            float baseDamage, CompoundTag tag) {
        float durabilityPenalty = getDurabilityPenalty(stack, tag);
        float weaknessChance = getWeaknessChance(stack, tag);
        int weaknessDuration = getWeaknessDuration(stack, tag);
        int weaknessLevel = getWeaknessLevel(stack, tag);
        float multiplierNormal = getMultiplierNormal(stack, tag);
        float multiplierSneaking = getMultiplierSneaking(stack, tag);
        float multiplierInvisible = getMultiplierInvisible(stack, tag);
        float multiplierSneakingInvisible = getMultiplierSneakingInvisible(stack, tag);
        float darknessBonus = getDarknessBonus(stack, tag);
        float movingTargetPenalty = getMovingTargetPenalty(stack, tag);

        performBackstab(sourceentity, entity, stack, level, baseDamage, durabilityPenalty, weaknessChance,
                weaknessDuration, weaknessLevel, multiplierNormal, multiplierSneaking, multiplierInvisible,
                multiplierSneakingInvisible, darknessBonus, movingTargetPenalty, tag);
    }

    @Unique
    private void performBackstab(LivingEntity attacker, LivingEntity entity, ItemStack itemstack, Level level,
            float baseDamage, float durabilityPenalty, float weaknessChance, int weaknessDuration, int weaknessLevel,
            float multiplierNormal, float multiplierSneaking, float multiplierInvisible,
            float multiplierSneakingInvisible, float darknessBonus, float movingTargetPenalty, CompoundTag tag) {
        baseDamage = baseDamage * 1.3F;
        float backstabMultiplier = calculateBackstabMultiplier(attacker, entity, level,
                multiplierNormal, multiplierSneaking, multiplierInvisible, multiplierSneakingInvisible,
                darknessBonus, movingTargetPenalty);
        float enchantmentBonus = EnchantmentHelper.getDamageBonus(itemstack, entity.getMobType());
        float bonusDamage = baseDamage * (backstabMultiplier - 1);
        float additionalDamage = bonusDamage + enchantmentBonus;
        applyLeftClickBackstabEffects(attacker, entity, itemstack, level, additionalDamage, durabilityPenalty,
                weaknessChance, weaknessDuration, weaknessLevel, tag);
    }

    @Unique
    private void applyLeftClickBackstabEffects(LivingEntity attacker, LivingEntity entity, ItemStack itemstack,
            Level level, float damage, float durabilityPenalty, float weaknessChance, int weaknessDuration,
            int weaknessLevel, CompoundTag tag) {
        ModUtils.applyBackstabDamage(attacker, entity, itemstack, damage);
        if (entity.isAlive() && level.random.nextFloat() < weaknessChance) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel - 1));
        }
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1.0F, 1.0F);
        if (!level.isClientSide) {
            ModUtils.spawnCustomParticlesInFront(attacker, itemstack,
                    new CustomHitParticleData(1.0F, 1.0F, 1.0F, 0.5F),
                    1.0F, 1.0F, 1.0F, 0.5F, 1.0F, 1, false);
        }
        itemstack.hurtAndBreak((int) durabilityPenalty, attacker, e -> e.broadcastBreakEvent(e.getUsedItemHand()));
    }



    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void jaams$onBackstabInteractLivingEntity(Player player, LivingEntity target, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (player == null || target == null)
            return;
        if (!isBackstabEnabled(stack))
            return;
        Level level = player.level();
        CompoundTag tag = stack.getTag();
        double maxDistance = getMaxDistance(stack, tag);
        double maxAngle = getMaxAngle(stack, tag);

        if (isBackstab(player, target, maxDistance, maxAngle)) {
            if (target instanceof Player targetPlayer && targetPlayer.isCreative()) {
                return;
            }
            if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                if (!level.isClientSide()) {
                    float baseDamage = calculateDamage(stack)
                            + (float) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0);
                    performRightClickBackstab(player, target, stack, level, baseDamage, tag);
                    int backstabCooldown = getRightClickCooldown(stack, tag);
                    boolean globalCooldown = TraitModifierData.getBackstab(stack)
                            .map(e -> e.global_cooldown)
                            .filter(java.util.Objects::nonNull)
                            .orElseGet(() -> TraitsConfig.BACKSTAB_GLOBAL_COOLDOWN.get());

                    if (globalCooldown) {
                        for (ItemStack invStack : player.getInventory().items) {
                            if (!invStack.isEmpty() && isBackstabEnabled(invStack)) {
                                player.getCooldowns().addCooldown(invStack.getItem(), backstabCooldown);
                            }
                        }
                        ItemStack offHandStack = player.getOffhandItem();
                        if (!offHandStack.isEmpty() && isBackstabEnabled(offHandStack)) {
                            player.getCooldowns().addCooldown(offHandStack.getItem(), backstabCooldown);
                        }
                        for (ItemStack armorStack : player.getInventory().armor) {
                            if (!armorStack.isEmpty() && isBackstabEnabled(armorStack)) {
                                player.getCooldowns().addCooldown(armorStack.getItem(), backstabCooldown);
                            }
                        }
                    } else {
                        player.getCooldowns().addCooldown(stack.getItem(), backstabCooldown);
                    }
                    player.swing(hand);
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
            }
        }
    }

    @Unique
    private int getRightClickCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabRightClickCooldown")) {
            return Math.max(0, tag.getInt("BackstabRightClickCooldown"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.right_click_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_RIGHT_CLICK_COOLDOWN.get());
    }

    @Unique
    private float getRightClickDamageBonus(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabRightClickDamageBonus")) {
            return Math.max(0.0F, tag.getFloat("BackstabRightClickDamageBonus"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.right_click_damage_bonus)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_RIGHT_CLICK_DAMAGE_BONUS.get().floatValue());
    }

    @Unique
    private float getRightClickDurabilityMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabRightClickDurabilityMultiplier")) {
            return Math.max(0.0F, tag.getFloat("BackstabRightClickDurabilityMultiplier"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.right_click_durability_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_RIGHT_CLICK_DURABILITY_MULTIPLIER.get().floatValue());
    }

    @Unique
    private float getRightClickForwardImpulse(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabRightClickForwardImpulse")) {
            return Math.max(0.0F, tag.getFloat("BackstabRightClickForwardImpulse"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.right_click_forward_impulse)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BACKSTAB_RIGHT_CLICK_FORWARD_IMPULSE.get().floatValue());
    }

    @Unique
    private void performRightClickBackstab(LivingEntity attacker, LivingEntity entity, ItemStack itemstack, Level level,
            float baseDamage, CompoundTag tag) {
        if (attacker == null || entity == null || !entity.isAlive())
            return;

        float durabilityPenalty = getDurabilityPenalty(itemstack, tag);
        float weaknessChance = getWeaknessChance(itemstack, tag);
        int weaknessDuration = getWeaknessDuration(itemstack, tag);
        int weaknessLevel = getWeaknessLevel(itemstack, tag);
        float multiplierNormal = getMultiplierNormal(itemstack, tag);
        float multiplierSneaking = getMultiplierSneaking(itemstack, tag);
        float multiplierInvisible = getMultiplierInvisible(itemstack, tag);
        float multiplierSneakingInvisible = getMultiplierSneakingInvisible(itemstack, tag);
        float darknessBonus = getDarknessBonus(itemstack, tag);
        float movingTargetPenalty = getMovingTargetPenalty(itemstack, tag);
        float rightClickDamageBonus = getRightClickDamageBonus(itemstack, tag);
        float rightClickDurabilityMultiplier = getRightClickDurabilityMultiplier(itemstack, tag);
        float rightClickForwardImpulse = getRightClickForwardImpulse(itemstack, tag);

        baseDamage = baseDamage / 2.0F;
        float backstabMultiplier = calculateBackstabMultiplier(attacker, entity, level,
                multiplierNormal, multiplierSneaking, multiplierInvisible, multiplierSneakingInvisible,
                darknessBonus, movingTargetPenalty);
        float enchantmentBonus = EnchantmentHelper.getDamageBonus(itemstack, entity.getMobType());
        float bonusDamage = baseDamage * (backstabMultiplier - 1);
        float totalDamage = baseDamage + bonusDamage + rightClickDamageBonus + enchantmentBonus;
        float finalDurabilityPenalty = durabilityPenalty * rightClickDurabilityMultiplier;

        DamageSource damageSource;
        try {
            damageSource = new DamageSource(
                    level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
                                    new ResourceLocation("jaams_weaponry:backstab"))),
                    attacker);
        } catch (Exception e) {
            damageSource = level.damageSources().mobAttack(attacker);
        }
        entity.hurt(damageSource, totalDamage);
        applyWeaponEnchantmentEffects(entity, attacker, itemstack);
        applyRightClickBackstabEffects(attacker, entity, itemstack, level, totalDamage, finalDurabilityPenalty,
                rightClickForwardImpulse, weaknessChance, weaknessDuration, weaknessLevel, tag);
    }

    @Unique
    private void applyRightClickBackstabEffects(LivingEntity attacker, LivingEntity entity, ItemStack itemstack,
            Level level, float damage, float durabilityPenalty, float rightClickForwardImpulse,
            float weaknessChance, int weaknessDuration, int weaknessLevel, CompoundTag tag) {
        if (entity.isAlive()) {
            if (level.random.nextFloat() < weaknessChance) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel - 1));
            }
            EnchantmentHelper.doPostDamageEffects(attacker, entity);
            EnchantmentHelper.doPostHurtEffects(entity, attacker);
        }
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1.0F, 1.0F);
        if (!level.isClientSide) {
            ModUtils.spawnCustomParticlesInFront(attacker, itemstack,
                    new CustomHitParticleData(1.0F, 1.0F, 1.0F, 0.5F),
                    1.0F, 1.0F, 1.0F, 0.5F, 1.0F, 1, false);
        }
        Vec3 lookDirection = attacker.getLookAngle().normalize().scale(rightClickForwardImpulse);
        attacker.setDeltaMovement(attacker.getDeltaMovement().add(lookDirection.x, 0.0, lookDirection.z));
        attacker.hurtMarked = true;
        itemstack.hurtAndBreak((int) durabilityPenalty, attacker, e -> e.broadcastBreakEvent(e.getUsedItemHand()));
    }

    @Unique
    private void applyWeaponEnchantmentEffects(LivingEntity nearby, LivingEntity attacker, ItemStack weaponItem) {
        int fireAspectLevel = weaponItem.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if (fireAspectLevel > 0) {
            nearby.setSecondsOnFire(fireAspectLevel * 4);
        }
        int baneOfArthropodsLevel = weaponItem.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS);
        if (baneOfArthropodsLevel > 0 && nearby.getMobType() == MobType.ARTHROPOD) {
            nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, baneOfArthropodsLevel * 50, 3));
        }
        int knockbackLevel = weaponItem.getEnchantmentLevel(Enchantments.KNOCKBACK);
        if (knockbackLevel > 0) {
            double knockbackStrength = knockbackLevel * 0.5;
            double motionX = nearby.getX() - attacker.getX();
            double motionZ = nearby.getZ() - attacker.getZ();
            double distance = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (distance > 0) {
                nearby.push(motionX / distance * knockbackStrength, 0.1D, motionZ / distance * knockbackStrength);
            }
        }
    }
}
