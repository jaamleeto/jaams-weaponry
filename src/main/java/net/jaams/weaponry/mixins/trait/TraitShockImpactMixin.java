package net.jaams.weaponry.mixins.trait;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.particle.CustomSmokeParticleData;
import java.util.Locale;

@Mixin(ItemStack.class)
public class TraitShockImpactMixin {

    @Unique
    private static final String CHARGE_TAG = "ShockImpactCharging";



    @Unique
    private static final String KEY_MAX_BONUS_DAMAGE = "ShockImpactMaxBonusDamage";
    @Unique
    private static final String KEY_MAX_RESIDUAL_DAMAGE = "ShockImpactMaxResidualDamage";
    @Unique
    private static final String KEY_SMASH_RADIUS = "ShockImpactSmashRadius";
    @Unique
    private static final String KEY_MIN_KNOCKBACK = "ShockImpactMinKnockbackStrength";
    @Unique
    private static final String KEY_MAX_KNOCKBACK = "ShockImpactMaxKnockbackStrength";
    @Unique
    private static final String KEY_KNOCKBACK_SCALING = "ShockImpactKnockbackScalingFactor";
    @Unique
    private static final String KEY_PARTICLE_COUNT = "ShockImpactParticleCount";
    @Unique
    private static final String KEY_SHAKE_INTENSITY = "ShockImpactShakeIntensity";
    @Unique
    private static final String KEY_SHAKE_RESET_DELAY = "ShockImpactShakeResetDelay";
    @Unique
    private static final String KEY_DURABILITY_BASE = "ShockImpactDurabilityDamageBase";
    @Unique
    private static final String KEY_MAX_DURABILITY = "ShockImpactMaxDurabilityDamage";
    @Unique
    private static final String KEY_EXHAUSTION = "ShockImpactExhaustion";
    @Unique
    private static final String KEY_DEPLETION_CHANCE = "ShockImpactDepletionChance";
    @Unique
    private static final String KEY_DEPLETION_DURATION = "ShockImpactDepletionDuration";
    @Unique
    private static final String KEY_DEPLETION_LEVEL = "ShockImpactDepletionLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_LEVEL = "ShockImpactDepletionMaxLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_DURATION = "ShockImpactDepletionMaxDuration";
    @Unique
    private static final String KEY_ENABLE_DEPLETION = "ShockImpactEnableDepletion";
    @Unique
    private static final String KEY_PLAYER_VERTICAL_IMPULSE = "ShockImpactPlayerVerticalImpulse";
    @Unique
    private static final String KEY_ENTITY_VERTICAL_IMPULSE = "ShockImpactEntityVerticalImpulse";
    @Unique
    private static final String KEY_COOLDOWN_TICKS = "ShockImpactCooldownTicks";
    @Unique
    private static final String KEY_BASE_DAMAGE_MULTIPLIER = "ShockImpactBaseDamageMultiplier";
    @Unique
    private static final String KEY_OFFHAND_COOLDOWN_MULTIPLIER = "ShockImpactOffhandCooldownMultiplier";
    @Unique
    private static final String KEY_OFFHAND_POWER_MULTIPLIER = "ShockImpactOffhandPowerMultiplier";



    @Unique
    private boolean isShockImpactEnabled(ItemStack stack) {
        if (!TraitsConfig.SHOCK_IMPACT.get()) {
            return false;
        }
        return ModTraits.isShockImpactItem(stack);
    }

    @Unique
    private ModEnums.ShockImpactMode getActivationMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ShockImpactMode")) {
            try {
                return ModEnums.ShockImpactMode.valueOf(tag.getString("ShockImpactMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        String jsonMode = TraitModifierData.getShockImpact(stack)
                .map(e -> e.shock_impact_mode)
                .filter(java.util.Objects::nonNull)
                .filter(m -> !m.isEmpty())
                .orElse(null);
        if (jsonMode != null) {
            try {
                return ModEnums.ShockImpactMode.valueOf(jsonMode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        return TraitsConfig.SHOCK_IMPACT_ACTIVATION_MODE.get();
    }

    @Unique
    private boolean isChargeMode(ItemStack stack) {
        return switch (getActivationMode(stack)) {
            case CHARGE_AND_RELEASE, CHARGE_AND_FINISH_USING, CHARGE_RELEASE_AND_FINISH -> true;
            default -> false;
        };
    }

    @Unique
    private boolean isInstantMode(ItemStack stack) {
        return getActivationMode(stack) == ModEnums.ShockImpactMode.INSTANT_ON_RIGHT_CLICK;
    }

    @Unique
    private int getMinChargeTicks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ShockImpactMinChargeTicks")) {
            return Math.max(0, tag.getInt("ShockImpactMinChargeTicks"));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.min_charge_ticks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MIN_CHARGE_TICKS.get());
    }



    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onShockImpactUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isShockImpactEnabled(stack)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }
        if (!player.onGround()) {
            return;
        }
        if (TraitsConfig.SHOCK_IMPACT_REQUIRE_CROUCHING.get() && !player.isCrouching()) {
            return;
        }

        ModEnums.ShockImpactMode mode = getActivationMode(stack);

        if (isChargeMode(stack)) {
            if (level.isClientSide()) {
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            player.startUsingItem(hand);
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putBoolean(CHARGE_TAG, true);
            nbt.putInt(CHARGE_TAG + "StartTick", player.tickCount);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        } else if (mode == ModEnums.ShockImpactMode.INSTANT_ON_RIGHT_CLICK) {
            if (level.isClientSide()) {
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            float attackStrength = player.getAttackStrengthScale(0.5F);
            if (attackStrength < 0.3F) {
                return;
            }
            activateShockImpact(level, player, stack, hand, player.blockPosition());
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        }
    }



    @Unique
    private int getUseDurationTicks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ShockImpactUseDurationTicks")) {
            return Math.max(1, tag.getInt("ShockImpactUseDurationTicks"));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.use_duration_ticks)
                .filter(java.util.Objects::nonNull)
                .filter(d -> d >= 0)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_CHARGE_DURATION_TICKS.get());
    }

    @Unique
    private UseAnim getCurrentUseAnimation(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ShockImpactUseAnimation")) {
            String anim = tag.getString("ShockImpactUseAnimation");
            if (anim != null && !anim.isEmpty()) {
                return parseUseAnimation(anim);
            }
        }
        String jsonAnim = TraitModifierData.getShockImpact(stack)
                .map(e -> e.use_animation)
                .filter(java.util.Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .orElse(null);
        if (jsonAnim != null) {
            return parseUseAnimation(jsonAnim);
        }
        return TraitsConfig.SHOCK_IMPACT_CHARGE_ANIMATION.get();
    }

    @Unique
    private UseAnim parseUseAnimation(String anim) {
        if (anim == null || anim.isEmpty())
            return UseAnim.NONE;
        return switch (anim.toUpperCase(Locale.ROOT).trim()) {
            case "BOW" -> UseAnim.BOW;
            case "CROSSBOW" -> UseAnim.CROSSBOW;
            case "SPEAR" -> UseAnim.SPEAR;
            case "NONE" -> UseAnim.NONE;
            case "EAT" -> UseAnim.EAT;
            case "DRINK" -> UseAnim.DRINK;
            case "BLOCK" -> UseAnim.BLOCK;
            case "SPYGLASS" -> UseAnim.SPYGLASS;
            default -> UseAnim.NONE;
        };
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaams$shockImpactGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isShockImpactEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getCurrentUseAnimation(stack));
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaams$shockImpactGetUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isShockImpactEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getUseDurationTicks(stack));
        }
    }



    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaams$onShockImpactRelease(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isShockImpactEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.ShockImpactMode mode = getActivationMode(stack);
        if (mode != ModEnums.ShockImpactMode.CHARGE_AND_RELEASE
                && mode != ModEnums.ShockImpactMode.CHARGE_RELEASE_AND_FINISH) {
            return;
        }
        if (!isCharging(stack)) {
            return;
        }
        int chargeTicks = getChargeTicks(stack, player);
        clearCharging(stack);
        ci.cancel();

        if (chargeTicks < getMinChargeTicks(stack)) {
            return;
        }
        if (level.isClientSide()) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }
        activateShockImpact(level, player, stack, player.getUsedItemHand(), player.blockPosition());
    }



    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaams$onShockImpactFinishUsing(Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isShockImpactEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.ShockImpactMode mode = getActivationMode(stack);
        if (mode != ModEnums.ShockImpactMode.CHARGE_AND_FINISH_USING
                && mode != ModEnums.ShockImpactMode.CHARGE_RELEASE_AND_FINISH) {
            return;
        }
        if (!isCharging(stack)) {
            return;
        }
        int chargeTicks = getChargeTicks(stack, player);
        clearCharging(stack);

        if (chargeTicks < getMinChargeTicks(stack)) {
            cir.setReturnValue(stack);
            return;
        }
        if (level.isClientSide()) {
            cir.setReturnValue(stack);
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            cir.setReturnValue(stack);
            return;
        }
        activateShockImpact(level, player, stack, player.getUsedItemHand(), player.blockPosition());
        cir.setReturnValue(stack);
    }



    @Unique
    private boolean isCharging(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(CHARGE_TAG);
    }

    @Unique
    private int getChargeTicks(ItemStack stack, Player player) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(CHARGE_TAG + "StartTick")) {
            return player.tickCount - tag.getInt(CHARGE_TAG + "StartTick");
        }
        return 0;
    }

    @Unique
    private void clearCharging(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(CHARGE_TAG);
            tag.remove(CHARGE_TAG + "StartTick");
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }



    @Unique
    private void activateShockImpact(Level level, Player player, ItemStack itemStack, InteractionHand hand,
            BlockPos impactPos) {
        if (level.isClientSide() || player == null || itemStack == null || impactPos == null) {
            return;
        }
        boolean isOffhand = hand == InteractionHand.OFF_HAND;
        float attackStrength = player.getAttackStrengthScale(0.5F);
        float intensityFactor = Math.max(0.3F, attackStrength);
        float powerMultiplier = isOffhand ? getOffhandPowerMultiplier(itemStack) : 1.0F;


        float baseDamageMultiplier = getBaseDamageMultiplier(itemStack) * intensityFactor * powerMultiplier;
        float maxBonusDamage = getMaxBonusDamage(itemStack) * intensityFactor * powerMultiplier;
        float maxResidualDamage = getMaxResidualDamage(itemStack) * intensityFactor * powerMultiplier;
        float smashRadius = getSmashRadius(itemStack) * powerMultiplier;
        float minKnockbackStrength = getMinKnockbackStrength(itemStack) * intensityFactor * powerMultiplier;
        float maxKnockbackStrength = getMaxKnockbackStrength(itemStack) * intensityFactor * powerMultiplier;
        float knockbackScalingFactor = getKnockbackScalingFactor(itemStack);
        int particleCount = Math.max(1, (int) (getParticleCount(itemStack) * intensityFactor * powerMultiplier));
        float shakeIntensity = getShakeIntensity(itemStack) * intensityFactor * powerMultiplier;
        int shakeResetDelay = getShakeResetDelay(itemStack);
        int durabilityDamageBase = getDurabilityDamageBase(itemStack);
        int maxDurabilityDamage = getMaxDurabilityDamage(itemStack);
        float exhaustion = getExhaustion(itemStack);
        float depletionChance = getDepletionChance(itemStack);
        int depletionLevel = getDepletionLevel(itemStack);
        int depletionDuration = getDepletionDuration(itemStack);
        int depletionMaxLevel = getDepletionMaxLevel(itemStack);
        int depletionMaxDuration = getDepletionMaxDuration(itemStack);
        boolean enableDepletion = getEnableDepletion(itemStack);
        float playerVerticalImpulse = getPlayerVerticalImpulse(itemStack) * intensityFactor * powerMultiplier;
        float entityVerticalImpulse = getEntityVerticalImpulse(itemStack) * intensityFactor * powerMultiplier;

        applyAreaAttack(player, level, itemStack, impactPos, attackStrength, baseDamageMultiplier, maxBonusDamage,
                maxResidualDamage, smashRadius, minKnockbackStrength, maxKnockbackStrength, knockbackScalingFactor,
                particleCount, shakeIntensity, shakeResetDelay, durabilityDamageBase, maxDurabilityDamage, exhaustion,
                depletionChance, depletionLevel, depletionDuration, depletionMaxLevel, depletionMaxDuration,
                enableDepletion, playerVerticalImpulse, entityVerticalImpulse);


        int cooldownTicks = isOffhand ? (int) (getCooldownTicks(itemStack) * getOffhandCooldownMultiplier(itemStack))
                : getCooldownTicks(itemStack);
        if (TraitsConfig.SHOCK_IMPACT_GLOBAL_COOLDOWN.get()) {

            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && isShockImpactEnabled(stack)) {
                    player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
                }
            }
            ItemStack offHandStack = player.getOffhandItem();
            if (!offHandStack.isEmpty() && isShockImpactEnabled(offHandStack)) {
                player.getCooldowns().addCooldown(offHandStack.getItem(), cooldownTicks);
            }
            for (ItemStack stack : player.getInventory().armor) {
                if (!stack.isEmpty() && isShockImpactEnabled(stack)) {
                    player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
                }
            }
        } else {

            player.getCooldowns().addCooldown(itemStack.getItem(), cooldownTicks);
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == itemStack.getItem()) {
                    player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
                }
            }
            ItemStack offHandStack = player.getOffhandItem();
            if (!offHandStack.isEmpty()) {
                player.getCooldowns().addCooldown(offHandStack.getItem(), cooldownTicks / 2);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            spawnAttackBlockParticles(serverLevel, impactPos, player);
        }


        player.resetAttackStrengthTicker();
        player.swing(hand, true);
    }



    @Unique
    private static final float DEBUFF_REDUCTION_PER_LEVEL = 0.10F;

    @Unique
    private float calculateDamage(ItemStack itemStack, Player player, boolean isArthropod, boolean isUndead) {
        float baseDamage = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
                .stream().findFirst()
                .map(modifier -> (float) modifier.getAmount())
                .orElse(1.0F);
        float playerAttackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float strengthMultiplier = 1.0F;
        if (player != null) {
            MobEffectInstance strength = player.getEffect(MobEffects.DAMAGE_BOOST);
            if (strength != null) {
                strengthMultiplier += 0.1F * (strength.getAmplifier() + 1);
            }
            MobEffectInstance warriorsGrace = player.getEffect(ModMobEffects.WARRIORS_GRACE.get());
            if (warriorsGrace != null) {
                strengthMultiplier += 0.05F * (warriorsGrace.getAmplifier() + 1);
            }
        }
        float totalDamage = (baseDamage + playerAttackDamage) * strengthMultiplier;
        if (isArthropod) {
            int baneLevel = itemStack.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS);
            if (baneLevel > 0) {
                totalDamage += baneLevel * 2.5F;
            }
        }
        if (isUndead) {
            int smiteLevel = itemStack.getEnchantmentLevel(Enchantments.SMITE);
            if (smiteLevel > 0) {
                totalDamage += smiteLevel * 2.5F;
            }
        }
        return totalDamage;
    }

    @Unique
    private float calculateDebuffReductionFactor(Player player) {
        float reductionFactor = 1.0F;
        if (player != null) {
            MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
            if (weakness != null) {
                reductionFactor -= DEBUFF_REDUCTION_PER_LEVEL * (weakness.getAmplifier() + 1);
            }
            MobEffectInstance depletion = player.getEffect(ModMobEffects.DEPLETION.get());
            if (depletion != null) {
                reductionFactor -= DEBUFF_REDUCTION_PER_LEVEL * (depletion.getAmplifier() + 1);
            }
        }
        return Math.max(0.1F, reductionFactor);
    }



    @Unique
    private void applyAreaAttack(Player player, Level level, ItemStack itemStack, BlockPos impactPos,
            float attackStrength, float baseDamageMultiplier, float maxBonusDamage, float maxResidualDamage,
            float smashRadius, float minKnockbackStrength, float maxKnockbackStrength, float knockbackScalingFactor,
            int particleCount, float shakeIntensity, int shakeResetDelay, int smashDurabilityDamageBase,
            int smashMaxDurabilityDamage, float exhaustion, float depletionChance, int depletionLevel,
            int depletionDuration, int depletionMaxLevel, int depletionMaxDuration, boolean enableDepletion,
            float playerVerticalImpulse, float entityVerticalImpulse) {
        if (level.isClientSide() || player == null || itemStack == null || impactPos == null) {
            return;
        }
        float debuffReductionFactor = calculateDebuffReductionFactor(player);
        float adjustedAttackStrength = attackStrength * debuffReductionFactor;
        float adjustedMinKnockbackStrength = minKnockbackStrength * debuffReductionFactor;
        float adjustedMaxKnockbackStrength = maxKnockbackStrength * debuffReductionFactor;
        int adjustedParticleCount = Math.max(1, (int) (particleCount * debuffReductionFactor));
        float adjustedPlayerVerticalImpulse = playerVerticalImpulse * debuffReductionFactor;
        float adjustedShakeIntensity = shakeIntensity * debuffReductionFactor;

        float weaponDamage = calculateDamage(itemStack, player, false, false);
        float baseSlamDamage = Math.min(weaponDamage * baseDamageMultiplier, maxBonusDamage) * adjustedAttackStrength;

        spawnBlockParticles(level, impactPos, adjustedParticleCount, smashRadius, player);
        playSlamSound(level, impactPos);
        applyShockwave(player, level, itemStack, impactPos, adjustedAttackStrength, smashRadius, baseSlamDamage,
                maxResidualDamage, adjustedMinKnockbackStrength, adjustedMaxKnockbackStrength, knockbackScalingFactor,
                entityVerticalImpulse);
        applyShakeEffects(player, level, impactPos, smashRadius, adjustedShakeIntensity, shakeResetDelay);
        applyPlayerVerticalImpulse(player, weaponDamage, adjustedPlayerVerticalImpulse);
        applyDurabilityDamage(player, itemStack, smashDurabilityDamageBase, smashMaxDurabilityDamage);
        if (!player.isCreative()) {
            player.causeFoodExhaustion(exhaustion);
        }
        applyDepletionEffect(level, player, depletionChance, depletionLevel, depletionDuration, depletionMaxLevel,
                depletionMaxDuration, enableDepletion);
    }



    @Unique
    private void applyShockwave(Player player, Level level, ItemStack itemStack, BlockPos impactPos,
            float attackStrength, float smashRadius, float baseSlamDamage, float maxResidualDamage,
            float minKnockbackStrength, float maxKnockbackStrength, float knockbackScalingFactor,
            float entityVerticalImpulse) {
        AABB affectedArea = new AABB(impactPos).inflate(smashRadius, 0.5D, smashRadius);
        Vec3 impactCenter = Vec3.atCenterOf(impactPos);
        float baseKnockback = calculateKnockback(minKnockbackStrength, maxKnockbackStrength, knockbackScalingFactor);
        DamageSource damageSource = new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
                                new ResourceLocation("jaams_weaponry:smash"))),
                player);
        int fireAspectLevel = itemStack.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        int knockbackLevel = itemStack.getEnchantmentLevel(Enchantments.KNOCKBACK);

        if (fireAspectLevel > 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LAVA, impactPos.getX() + 0.5, impactPos.getY() + 0.5,
                    impactPos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
        }

        level.getEntities(player, affectedArea, e -> e.isAlive() && !(e instanceof ItemEntity)).forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                boolean isArthropod = livingEntity.getMobType() == MobType.ARTHROPOD;
                boolean isUndead = livingEntity.getMobType() == MobType.UNDEAD;
                float weaponDamage = calculateDamage(itemStack, player, isArthropod, isUndead);
                double distance = entity.position().distanceTo(impactCenter);
                float distanceFactor = Math.max(0, 1.0F - (float) (distance / smashRadius));
                float effectiveDamage = Math.min(weaponDamage * 0.3F, maxResidualDamage) * attackStrength
                        * distanceFactor;
                if (effectiveDamage > 0) {
                    livingEntity.hurt(damageSource, effectiveDamage);
                    if (fireAspectLevel > 0) {
                        livingEntity.setSecondsOnFire(fireAspectLevel * 4);
                    }
                    if (!player.isCreative() && livingEntity instanceof Mob mob) {
                        mob.setTarget(player);
                    }
                }
                applyEntityKnockback(livingEntity, impactCenter, baseKnockback, attackStrength, distanceFactor,
                        entityVerticalImpulse, knockbackLevel);
            } else if (entity instanceof Projectile projectile) {
                double distance = entity.position().distanceTo(impactCenter);
                float distanceFactor = Math.max(0, 1.0F - (float) (distance / smashRadius));
                if (distanceFactor > 0) {
                    if (projectile instanceof AbstractArrow arrow && arrow.getDeltaMovement().lengthSqr() < 0.001) {
                        arrow.setNoPhysics(false);
                        arrow.setDeltaMovement(0, 0.2 * entityVerticalImpulse * distanceFactor, 0);
                    }
                    Vec3 currentMotion = projectile.getDeltaMovement();
                    Vec3 newMotion = currentMotion.scale(-0.1D * attackStrength * distanceFactor)
                            .add(0, 0.1 * entityVerticalImpulse * distanceFactor, 0);
                    projectile.setDeltaMovement(newMotion);
                    projectile.setYRot(projectile.getYRot() + 180.0F);
                    projectile.yRotO = projectile.getYRot();
                    projectile.hurtMarked = true;
                }
            } else if (entity.isPushable()) {
                double distance = entity.position().distanceTo(impactCenter);
                float distanceFactor = Math.max(0, 1.0F - (float) (distance / smashRadius));
                if (distanceFactor > 0) {
                    double knockbackResistance = 0.0;
                    double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 3.0);
                    double movementSpeed = entity instanceof net.minecraft.world.entity.vehicle.Boat
                            || entity instanceof net.minecraft.world.entity.vehicle.Minecart ? 0.1 : 0.4;
                    double movementSpeedMultiplier = Math.max(0.1, movementSpeed / 0.1);
                    float effectiveKnockback = baseKnockback * (1.0F - (float) knockbackReduction)
                            * attackStrength * distanceFactor * (float) movementSpeedMultiplier;
                    effectiveKnockback += knockbackLevel * 0.4F;
                    if (effectiveKnockback > 0) {
                        Vec3 knockbackVector = entity.position().subtract(impactCenter).normalize()
                                .scale(effectiveKnockback)
                                .add(0, Mth.clamp(0.2 * movementSpeedMultiplier, 0.1, entityVerticalImpulse), 0);
                        knockbackVector = new Vec3(Mth.clamp(knockbackVector.x, -1.2, 1.2),
                                Mth.clamp(knockbackVector.y, 0, entityVerticalImpulse),
                                Mth.clamp(knockbackVector.z, -1.2, 1.2));
                        entity.push(knockbackVector.x, knockbackVector.y, knockbackVector.z);
                        entity.hurtMarked = true;
                        if (entity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
                        } else if (!entity.level().isClientSide()) {
                            entity.level().getEntities(null, entity.getBoundingBox().inflate(10.0)).stream()
                                    .filter(e -> e instanceof ServerPlayer)
                                    .forEach(e -> ((ServerPlayer) e).connection
                                            .send(new ClientboundSetEntityMotionPacket(entity)));
                        }
                    }
                }
            }
        });
    }

    @Unique
    private void applyEntityKnockback(LivingEntity entity, Vec3 impactCenter, float baseKnockback,
            float attackStrength, float distanceFactor, float entityVerticalImpulse, int knockbackLevel) {
        double knockbackResistance = Math.max(0.0, Math.min(1.0,
                entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 3.0);
        double movementSpeed = entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double movementSpeedMultiplier = Math.max(0.1, movementSpeed / 0.1);
        float effectiveKnockback = baseKnockback * (1.0F - (float) knockbackReduction) * attackStrength
                * distanceFactor * (float) movementSpeedMultiplier;
        effectiveKnockback += knockbackLevel * 0.4F;
        if (effectiveKnockback > 0) {
            Vec3 knockbackVector = entity.position().subtract(impactCenter).normalize().scale(effectiveKnockback)
                    .add(0, Mth.clamp(0.2 * movementSpeedMultiplier, 0.1, entityVerticalImpulse), 0);
            knockbackVector = new Vec3(Mth.clamp(knockbackVector.x, -1.2, 1.2),
                    Mth.clamp(knockbackVector.y, 0, entityVerticalImpulse),
                    Mth.clamp(knockbackVector.z, -1.2, 1.2));
            entity.push(knockbackVector.x, knockbackVector.y, knockbackVector.z);
            entity.hurtMarked = true;
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
            } else if (!entity.level().isClientSide()) {
                entity.level().getEntities(null, entity.getBoundingBox().inflate(10.0)).stream()
                        .filter(e -> e instanceof ServerPlayer)
                        .forEach(e -> ((ServerPlayer) e).connection
                                .send(new ClientboundSetEntityMotionPacket(entity)));
            }
        }
    }

    @Unique
    private void applyPlayerVerticalImpulse(Player player, float weaponDamage, float playerVerticalImpulse) {
        double knockbackResistance = Math.max(0.0, Math.min(1.0,
                player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 3.0);
        double movementSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double movementSpeedMultiplier = Math.max(0.1, movementSpeed / 0.1);
        float damageScaling = Math.min(1.0F + weaponDamage / 10.0F, 2.0F);
        float effectiveImpulse = playerVerticalImpulse * damageScaling * (float) (1.0 - knockbackReduction)
                * (float) movementSpeedMultiplier;
        effectiveImpulse = Mth.clamp(effectiveImpulse, 0.3F, playerVerticalImpulse);
        if (effectiveImpulse > 0) {
            player.push(0, effectiveImpulse, 0);
            player.hurtMarked = true;
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }
    }



    @Unique
    private void spawnBlockParticles(Level level, BlockPos impactPos, int particleCount, float smashRadius,
            Player player) {
        if (!(level instanceof ServerLevel serverLevel) || impactPos == null || player == null) {
            return;
        }
        int radius = (int) Math.ceil(smashRadius);
        int particlesPerBlock = Math.max(1, particleCount / (radius * radius));
        int smokeParticlesPerBlock = Math.max(1, particlesPerBlock / 5);
        int bubbleParticlesPerBlock = Math.max(3, particlesPerBlock * 2);
        RandomSource random = level.random;
        boolean isUnderWater = player.isUnderWater();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSquared = x * x + z * z;
                if (distanceSquared > smashRadius * smashRadius)
                    continue;
                BlockPos pos = impactPos.offset(x, 0, z);
                BlockState state = level.getBlockState(pos);
                if (state.isAir())
                    continue;
                double yPos = pos.getY() + 1.0 + 0.05;
                for (int i = 0; i < particlesPerBlock; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double blockDistance = random.nextDouble() * 0.3;
                    double offsetX = Math.cos(angle) * blockDistance;
                    double offsetZ = Math.sin(angle) * blockDistance;
                    double velocityY = 0.15 + random.nextDouble() * 0.25;
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            pos.getX() + 0.5 + offsetX, yPos, pos.getZ() + 0.5 + offsetZ,
                            1, 0.1, velocityY, 0.1, 0.08);
                }
                if (isUnderWater) {
                    for (int i = 0; i < bubbleParticlesPerBlock; i++) {
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double maxDistance = 0.6 * (1 - Math.sqrt(distanceSquared) / smashRadius);
                        double distance = random.nextDouble() * maxDistance;
                        double offsetX = Math.cos(angle) * distance;
                        double offsetZ = Math.sin(angle) * distance;
                        double velocityY = 0.3 + random.nextDouble() * 0.4;
                        serverLevel.sendParticles(ParticleTypes.BUBBLE,
                                pos.getX() + 0.5 + offsetX, player.position().y + 0.3,
                                pos.getZ() + 0.5 + offsetZ, 1, 0.1, velocityY, 0.1, 0.1);
                    }
                } else {
                    for (int i = 0; i < smokeParticlesPerBlock; i++) {
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double maxDistance = 0.6 * (1 - Math.sqrt(distanceSquared) / smashRadius);
                        double distance = random.nextDouble() * maxDistance;
                        double offsetX = Math.cos(angle) * distance;
                        double offsetZ = Math.sin(angle) * distance;
                        float baseSize = 0.65F;
                        float sizeVariation = random.nextFloat() * 0.35F;
                        float grayScale = 0.5F + random.nextFloat() * 0.4F;
                        CustomSmokeParticleData smokeParticle = new CustomSmokeParticleData(grayScale, grayScale,
                                grayScale, Mth.clamp(baseSize + sizeVariation, 0.2F, 1.8F));
                        serverLevel.sendParticles(smokeParticle,
                                pos.getX() + 0.5 + offsetX, player.position().y + 0.3,
                                pos.getZ() + 0.5 + offsetZ, 1, 0.08, 0.15 + random.nextDouble() * 0.2, 0.08, 0.04);
                    }
                }
            }
        }
    }

    @Unique
    private void spawnAttackBlockParticles(ServerLevel serverLevel, BlockPos impactPos, LivingEntity entity) {
        if (impactPos == null || entity == null) {
            return;
        }
        boolean isInWater = entity.isUnderWater();
        BlockPos basePos = impactPos.below();
        int halfSize = 2;
        RandomSource random = serverLevel.random;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                BlockPos pos = basePos.offset(x, 0, z);
                BlockState state = serverLevel.getBlockState(pos);
                if (!state.isAir() && serverLevel.getBlockState(pos.above()).isAir()) {
                    ParticleOptions particle = isInWater ? ParticleTypes.BUBBLE
                            : new BlockParticleOption(ParticleTypes.BLOCK, state);
                    int particleCount = isInWater ? 6 : 3;
                    for (int i = 0; i < particleCount; i++) {
                        double offsetX = random.nextGaussian() * 0.3;
                        double offsetY = random.nextDouble() * 0.5 + 0.5;
                        double offsetZ = random.nextGaussian() * 0.3;
                        serverLevel.sendParticles(particle,
                                pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY,
                                pos.getZ() + 0.5 + offsetZ, 1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        }
    }



    @Unique
    private void playSlamSound(Level level, BlockPos impactPos) {
        level.playSound(null, impactPos, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,
                0.9F, 0.9F + level.random.nextFloat() * 0.2F);
        SoundEvent hammerSlam = net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS
                .getValue(new ResourceLocation("jaams_weaponry:hammer_slam"));
        if (hammerSlam != null) {
            level.playSound(null, impactPos, hammerSlam, SoundSource.PLAYERS,
                    0.9F, 0.9F + level.random.nextFloat() * 0.2F);
        }
    }



    @Unique
    private float calculateKnockback(float minKnockbackStrength, float maxKnockbackStrength,
            float knockbackScalingFactor) {
        return minKnockbackStrength + (maxKnockbackStrength - minKnockbackStrength) * knockbackScalingFactor;
    }



    @Unique
    private void applyShakeEffects(Player player, Level level, BlockPos impactPos, float smashRadius,
            float shakeIntensity, int shakeResetDelay) {
        ModUtils.applyShakeEffect(player, shakeIntensity * 0.8F, shakeResetDelay);
        AABB affectedArea = new AABB(impactPos).inflate(smashRadius, 0.5D, smashRadius);
        level.getEntitiesOfClass(Player.class, affectedArea, p -> p != player && p.isAlive())
                .forEach(p -> ModUtils.applyShakeEffect(p, shakeIntensity * 0.6F, shakeResetDelay));
    }



    @Unique
    private void applyDurabilityDamage(Player player, ItemStack itemStack, int smashDurabilityDamageBase,
            int smashMaxDurabilityDamage) {
        int durabilityDamage = Math.min(smashDurabilityDamageBase, smashMaxDurabilityDamage);
        itemStack.hurtAndBreak(durabilityDamage, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }



    @Unique
    private void applyDepletionEffect(Level level, Player player, float depletionChance, int depletionLevel,
            int depletionDuration, int depletionMaxLevel, int depletionMaxDuration, boolean enableDepletion) {
        if (!enableDepletion || level.isClientSide() || player == null) {
            return;
        }
        if (level.random.nextFloat() < depletionChance) {
            int calculatedDepletionLevel = Math.min(depletionLevel, depletionMaxLevel);
            int calculatedDepletionDuration = Math.min(depletionDuration, depletionMaxDuration);
            MobEffectInstance depletionEffect = new MobEffectInstance(
                    ModMobEffects.DEPLETION.get(), calculatedDepletionDuration,
                    calculatedDepletionLevel - 1, false, false, true);
            player.addEffect(depletionEffect);
        }
    }





    @Unique
    private float getMaxBonusDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MAX_BONUS_DAMAGE)) {
            return Math.max(0.0F, tag.getFloat(KEY_MAX_BONUS_DAMAGE));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.max_bonus_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MAX_BONUS_DAMAGE.get().floatValue());
    }

    @Unique
    private float getMaxResidualDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MAX_RESIDUAL_DAMAGE)) {
            return Math.max(0.0F, tag.getFloat(KEY_MAX_RESIDUAL_DAMAGE));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.max_residual_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MAX_RESIDUAL_DAMAGE.get().floatValue());
    }

    @Unique
    private float getSmashRadius(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_SMASH_RADIUS)) {
            return Math.max(0.1F, tag.getFloat(KEY_SMASH_RADIUS));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.smash_radius)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_SMASH_RADIUS.get().floatValue());
    }

    @Unique
    private float getMinKnockbackStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MIN_KNOCKBACK)) {
            return Math.max(0.0F, tag.getFloat(KEY_MIN_KNOCKBACK));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.min_knockback_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MIN_KNOCKBACK_STRENGTH.get().floatValue());
    }

    @Unique
    private float getMaxKnockbackStrength(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MAX_KNOCKBACK)) {
            return Math.max(0.0F, tag.getFloat(KEY_MAX_KNOCKBACK));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.max_knockback_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MAX_KNOCKBACK_STRENGTH.get().floatValue());
    }

    @Unique
    private float getKnockbackScalingFactor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_KNOCKBACK_SCALING)) {
            return Math.max(0.0F, tag.getFloat(KEY_KNOCKBACK_SCALING));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.knockback_scaling_factor)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_KNOCKBACK_SCALING_FACTOR.get().floatValue());
    }

    @Unique
    private int getParticleCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_PARTICLE_COUNT)) {
            return Math.max(0, tag.getInt(KEY_PARTICLE_COUNT));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.particle_count)
                .filter(java.util.Objects::nonNull)
                .orElse(35);
    }

    @Unique
    private float getShakeIntensity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_SHAKE_INTENSITY)) {
            return Math.max(0.0F, tag.getFloat(KEY_SHAKE_INTENSITY));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.shake_intensity)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_SHAKE_INTENSITY.get().floatValue());
    }

    @Unique
    private int getShakeResetDelay(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_SHAKE_RESET_DELAY)) {
            return Math.max(0, tag.getInt(KEY_SHAKE_RESET_DELAY));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.shake_reset_delay)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_SHAKE_RESET_DELAY.get());
    }

    @Unique
    private int getDurabilityDamageBase(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DURABILITY_BASE)) {
            return Math.max(0, tag.getInt(KEY_DURABILITY_BASE));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.durability_damage_base)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DURABILITY_DAMAGE_BASE.get());
    }

    @Unique
    private int getMaxDurabilityDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_MAX_DURABILITY)) {
            return Math.max(0, tag.getInt(KEY_MAX_DURABILITY));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.max_durability_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_MAX_DURABILITY_DAMAGE.get());
    }

    @Unique
    private float getExhaustion(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_EXHAUSTION)) {
            return Math.max(0.0F, tag.getFloat(KEY_EXHAUSTION));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.exhaustion)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_EXHAUSTION.get().floatValue());
    }

    @Unique
    private float getDepletionChance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_CHANCE)) {
            return (float) Mth.clamp(tag.getFloat(KEY_DEPLETION_CHANCE), 0.0, 1.0);
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.depletion_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DEPLETION_CHANCE.get().floatValue());
    }

    @Unique
    private int getDepletionDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_DURATION));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.depletion_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DEPLETION_DURATION.get());
    }

    @Unique
    private int getDepletionLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_LEVEL));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.depletion_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DEPLETION_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_LEVEL));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.depletion_max_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DEPLETION_MAX_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_DURATION));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.depletion_max_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_DEPLETION_MAX_DURATION.get());
    }

    @Unique
    private boolean getEnableDepletion(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_ENABLE_DEPLETION)) {
            return tag.getBoolean(KEY_ENABLE_DEPLETION);
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.enable_depletion)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_ENABLE_DEPLETION.get());
    }

    @Unique
    private float getPlayerVerticalImpulse(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_PLAYER_VERTICAL_IMPULSE)) {
            return Math.max(0.0F, tag.getFloat(KEY_PLAYER_VERTICAL_IMPULSE));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.player_vertical_impulse)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_PLAYER_VERTICAL_IMPULSE.get().floatValue());
    }

    @Unique
    private float getEntityVerticalImpulse(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_ENTITY_VERTICAL_IMPULSE)) {
            return Math.max(0.0F, tag.getFloat(KEY_ENTITY_VERTICAL_IMPULSE));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.entity_vertical_impulse)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_ENTITY_VERTICAL_IMPULSE.get().floatValue());
    }

    @Unique
    private int getCooldownTicks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_COOLDOWN_TICKS)) {
            return Math.max(0, tag.getInt(KEY_COOLDOWN_TICKS));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.cooldown_ticks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_COOLDOWN_TICKS.get());
    }

    @Unique
    private float getBaseDamageMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_BASE_DAMAGE_MULTIPLIER)) {
            return Math.max(0.0F, tag.getFloat(KEY_BASE_DAMAGE_MULTIPLIER));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.base_damage_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_BASE_DAMAGE_MULTIPLIER.get().floatValue());
    }

    @Unique
    private float getOffhandCooldownMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_OFFHAND_COOLDOWN_MULTIPLIER)) {
            return Math.max(0.1F, tag.getFloat(KEY_OFFHAND_COOLDOWN_MULTIPLIER));
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.offhand_cooldown_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_OFFHAND_COOLDOWN_MULTIPLIER.get().floatValue());
    }

    @Unique
    private float getOffhandPowerMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_OFFHAND_POWER_MULTIPLIER)) {
            return (float) Mth.clamp(tag.getFloat(KEY_OFFHAND_POWER_MULTIPLIER), 0.0, 1.0);
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.offhand_power_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_OFFHAND_POWER_MULTIPLIER.get().floatValue());
    }
}
