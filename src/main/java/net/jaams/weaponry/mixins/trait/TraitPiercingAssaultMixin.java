package net.jaams.weaponry.mixins.trait;

import java.util.List;
import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.jaams.weaponry.init.ModMobEffects;

@Mixin(ItemStack.class)
public class TraitPiercingAssaultMixin {



    @Unique
    private static final String CHARGE_TAG = "PiercingAssaultCharging";

    @Unique
    private static final String KEY_DEPLETION_CHANCE = "PiercingAssaultDepletionChance";
    @Unique
    private static final String KEY_DEPLETION_DURATION = "PiercingAssaultDepletionDuration";
    @Unique
    private static final String KEY_DEPLETION_LEVEL = "PiercingAssaultDepletionLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_LEVEL = "PiercingAssaultDepletionMaxLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_DURATION = "PiercingAssaultDepletionMaxDuration";
    @Unique
    private static final String KEY_ENABLE_DEPLETION = "PiercingAssaultEnableDepletion";



    @Unique
    private boolean isPiercingAssaultEnabled(ItemStack stack) {
        if (!TraitsConfig.PIERCING_ASSAULT.get()) {
            return false;
        }
        return ModTraits.isPiercingAssaultItem(stack);
    }

    @Unique
    private ModEnums.PiercingAssaultMode getActivationMode(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultMode")) {
            try {
                return ModEnums.PiercingAssaultMode
                        .valueOf(tag.getString("PiercingAssaultMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }

        ModEnums.PiercingAssaultMode jsonMode = TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.piercing_assault_mode)
                .filter(java.util.Objects::nonNull)
                .filter((m) -> !m.isEmpty())
                .map((m) -> {
                    try {
                        return ModEnums.PiercingAssaultMode.valueOf(m.toUpperCase(Locale.ROOT));
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .orElse(null);
        if (jsonMode != null) {
            return jsonMode;
        }

        return TraitsConfig.PIERCING_ASSAULT_ACTIVATION_MODE.get();
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
        return switch (getActivationMode(stack)) {
            case SPRINT_CLICK, INSTANT_ON_RIGHT_CLICK -> true;
            default -> false;
        };
    }

    @Unique
    private int getMinChargeTicks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultMinChargeTicks")) {
            return Math.max(0, tag.getInt("PiercingAssaultMinChargeTicks"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.min_charge_ticks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_MIN_CHARGE_TICKS.get());
    }



    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onPiercingAssaultUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isPiercingAssaultEnabled(stack)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        ModEnums.PiercingAssaultMode mode = getActivationMode(stack);

        if (isChargeMode(stack)) {
            player.startUsingItem(hand);
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putBoolean(CHARGE_TAG, true);
            nbt.putInt(CHARGE_TAG + "StartTick", player.tickCount);
        } else if (mode == ModEnums.PiercingAssaultMode.INSTANT_ON_RIGHT_CLICK) {

            if (level.isClientSide()) {
                if (TraitsConfig.PIERCING_ASSAULT_DASH_SWING.get()) {
                    player.swing(hand, true);
                }
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            activatePiercingAssault(level, player, stack, hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        } else {

            if (!player.isSprinting()) {
                return;
            }
            if (!player.onGround()) {
                return;
            }
            if (level.isClientSide()) {
                if (TraitsConfig.PIERCING_ASSAULT_DASH_SWING.get()) {
                    player.swing(hand, true);
                }
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            activatePiercingAssault(level, player, stack, hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        }
    }



    @Unique
    private int getUseDurationTicks(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultUseDurationTicks")) {
            return Math.max(1, tag.getInt("PiercingAssaultUseDurationTicks"));
        }

        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.use_duration_ticks)
                .filter(java.util.Objects::nonNull)
                .filter(d -> d >= 0)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_CHARGE_DURATION_TICKS.get());
    }

    @Unique
    private UseAnim getCurrentUseAnimation(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultUseAnimation")) {
            String anim = tag.getString("PiercingAssaultUseAnimation");
            if (anim != null && !anim.isEmpty()) {
                return parseUseAnimation(anim);
            }
        }

        String jsonAnim = TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.use_animation)
                .filter(java.util.Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .orElse(null);
        if (jsonAnim != null) {
            return parseUseAnimation(jsonAnim);
        }

        return TraitsConfig.PIERCING_ASSAULT_CHARGE_ANIMATION.get();
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
    private void jaams$piercingAssaultGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isPiercingAssaultEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getCurrentUseAnimation(stack));
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaams$piercingAssaultGetUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isPiercingAssaultEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getUseDurationTicks(stack));
        }
    }



    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaams$onPiercingAssaultRelease(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isPiercingAssaultEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.PiercingAssaultMode mode = getActivationMode(stack);
        if (mode != ModEnums.PiercingAssaultMode.CHARGE_AND_RELEASE
                && mode != ModEnums.PiercingAssaultMode.CHARGE_RELEASE_AND_FINISH) {
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
        activatePiercingAssault(level, player, stack, player.getUsedItemHand());
    }



    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaams$onPiercingAssaultFinishUsing(Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isPiercingAssaultEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.PiercingAssaultMode mode = getActivationMode(stack);
        if (mode != ModEnums.PiercingAssaultMode.CHARGE_AND_FINISH_USING
                && mode != ModEnums.PiercingAssaultMode.CHARGE_RELEASE_AND_FINISH) {
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
        activatePiercingAssault(level, player, stack, player.getUsedItemHand());
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
    private void activatePiercingAssault(Level level, Player player, ItemStack itemStack, InteractionHand hand) {
        applyDashMovement(player, itemStack, hand);
        boolean hasTarget = applyPiercingAttack(level, player, itemStack, hand);
        playDashSound(level, player);
        if (!player.isCreative()) {
            float exhaustion = 1.0F + player.getRandom().nextFloat() * 1.0F;
            player.getFoodData().addExhaustion(exhaustion);
        }
        if (hasTarget) {
            applyItemDamage(level, player, itemStack);
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            if (hand == InteractionHand.MAIN_HAND && offHandItem.is(ModTags.KATANAS)) {
                applyItemDamage(level, player, offHandItem);
            } else if (hand == InteractionHand.OFF_HAND && mainHandItem.is(ModTags.KATANAS)) {
                applyItemDamage(level, player, mainHandItem);
            }
            if (TraitsConfig.PIERCING_ASSAULT_ATTACK_SWING.get()) {
                startPiercingAnimation(player);
            }
        }
        applyCooldowns(player, hand, hasTarget, itemStack);
        applyDepletionEffect(level, player, itemStack);
    }

    @Unique
    private void applyDashMovement(Player player, ItemStack itemStack, InteractionHand hand) {
        double movementSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double knockbackResistance = Math.max(0.0,
                Math.min(1.0, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double baseMovementSpeed = 0.1;
        double movementMultiplier = Math.max(0.1, movementSpeed / baseMovementSpeed);
        double knockbackModifier = 1.0 - (knockbackResistance * 1.2);
        float speedMultiplier = (float) (movementMultiplier * knockbackModifier);
        speedMultiplier = Math.max(0.3f, speedMultiplier);
        float dashDistance = getDashDistance(itemStack) * speedMultiplier;
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(new Vec3(look.x * dashDistance, 0, look.z * dashDistance));
        player.hurtMarked = true;
        if (TraitsConfig.PIERCING_ASSAULT_DASH_SWING.get()) {
            player.swing(hand, true);
        }
    }

    @Unique
    private void playDashSound(Level level, Player player) {
        if (!level.isClientSide()) {
            ResourceLocation dashSoundId = new ResourceLocation("jaams_weaponry:dash");
            SoundEvent dashSound = ForgeRegistries.SOUND_EVENTS.getValue(dashSoundId);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    dashSound != null ? dashSound : SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Unique
    private boolean applyPiercingAttack(Level level, Player player, ItemStack itemStack, InteractionHand hand) {
        float range = getPierceRange(itemStack);
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position().add(0, player.getEyeHeight() * 0.5, 0);


        AABB hitBox = player.getBoundingBox().inflate(range, 1.0, range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, hitBox,
                e -> e != player && e.isAlive()
                        && (!(e instanceof Player) || !((Player) e).isCreative())
                        && !ModUtils.isAlliedEntity(player, e));

        LivingEntity target = null;
        double closestDotProduct = -1.0;
        double threshold = 0.65;

        for (LivingEntity entity : entities) {
            if (isEntityInPiercePath(entity, startPos, lookVec, range)) {
                double dotProduct = entity.getEyePosition().subtract(startPos).normalize().dot(lookVec);
                if (dotProduct > closestDotProduct && dotProduct > threshold) {
                    target = entity;
                    closestDotProduct = dotProduct;
                }
            }
        }

        if (target == null) {
            if (TraitsConfig.PIERCING_ASSAULT_ATTACK_SWING.get()) {
                startPiercingAnimation(player);
            } else {
                player.swing(hand, true);
            }
            return false;
        }

        if (!level.isClientSide()) {
            applyEntityDamage(level, player, itemStack, target);
            SoundEvent hitSound = ForgeRegistries.SOUND_EVENTS
                    .getValue(new ResourceLocation("jaams_weaponry:rapier_hit"));
            level.playSound(null, target.blockPosition(),
                    hitSound != null ? hitSound : SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);

            if (level instanceof ServerLevel serverLevel) {
                emitCriticalParticles(serverLevel, target);
            }
        }

        if (TraitsConfig.PIERCING_ASSAULT_ATTACK_SWING.get()) {
            player.swing(hand, true);
        }
        return true;
    }

    @Unique
    private boolean isEntityInPiercePath(LivingEntity entity, Vec3 startPos, Vec3 lookVec, double range) {
        Vec3 toEntity = entity.getEyePosition().subtract(startPos);
        double distance = toEntity.length();
        if (distance > range + 0.5) {
            return false;
        }
        Vec3 toEntityNormalized = toEntity.normalize();
        double dotProduct = toEntityNormalized.dot(lookVec);
        return dotProduct > 0.65;
    }

    @Unique
    private void emitCriticalParticles(ServerLevel serverLevel, LivingEntity entity) {
        int particleCount = 6;
        double spread = 0.35;
        double speed = 0.15;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (serverLevel.random.nextDouble() - 0.5) * spread;
            double offsetY = (serverLevel.random.nextDouble() - 0.5) * spread;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5) * spread;
            serverLevel.sendParticles(ParticleTypes.CRIT, entity.getX() + offsetX, entity.getY() + 1 + offsetY,
                    entity.getZ() + offsetZ, 1, spread, spread, spread, speed);
        }
    }

    @Unique
    private void applyEntityDamage(Level level, Player player, ItemStack itemStack, LivingEntity target) {
        if (ModUtils.isAlliedEntity(player, target) || !target.isAlive()) {
            return;
        }
        float baseDamage = calculateDamage(itemStack);
        float enchantmentDamage = calculateEnchantmentDamage(itemStack, target);
        float playerAttackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float damageMultiplier = 1.0F;
        float totalDamage = (baseDamage + playerAttackDamage / 2 + enchantmentDamage) * 0.6F - 2.0F;
        totalDamage *= damageMultiplier;

        float minDamage = getMinDamage(itemStack);
        if (totalDamage < minDamage) {
            totalDamage = minDamage;
        }
        if (totalDamage > 0) {
            target.hurt(createDamageSource(level, player), totalDamage);
            applySpecialEffects(level, target, itemStack);
            if (level instanceof ServerLevel serverLevel) {
                SoundEvent cutSound = ForgeRegistries.SOUND_EVENTS
                        .getValue(new ResourceLocation("jaams_weaponry:rapier_hit"));
                serverLevel.playSound(null, target.blockPosition(),
                        cutSound != null ? cutSound : SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
            }
            if (target instanceof Mob mob && !player.isCreative()) {
                mob.setTarget(player);
            }
        }
    }

    @Unique
    private float calculateEnchantmentDamage(ItemStack itemStack, LivingEntity target) {
        float damage = 0;
        int sharpnessLevel = itemStack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS);
        if (sharpnessLevel > 0) {
            damage += 1.0F + sharpnessLevel * 0.5F;
        }
        int smiteLevel = itemStack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SMITE);
        if (smiteLevel > 0 && target.getMobType() == net.minecraft.world.entity.MobType.UNDEAD) {
            damage += smiteLevel * 1.5F;
        }
        int baneLevel = itemStack
                .getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS);
        if (baneLevel > 0 && target.getMobType() == net.minecraft.world.entity.MobType.ARTHROPOD) {
            damage += baneLevel * 1.5F;
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        }
        return damage;
    }

    @Unique
    private void applyItemDamage(Level level, Player player, ItemStack itemStack) {
        if (!player.isCreative()) {
            int durabilityCost = getDurabilityCost(itemStack);
            if (itemStack.hurt(durabilityCost, player.getRandom(), null)) {
                itemStack.shrink(1);
                itemStack.setDamageValue(0);
                if (!level.isClientSide()) {
                    level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F,
                            1.0F);
                }
            }
        }
    }

    @Unique
    private void startPiercingAnimation(Player player) {
        player.swing(InteractionHand.MAIN_HAND, true);
    }

    @Unique
    private void applyCooldowns(Player player, InteractionHand hand, boolean hasTarget, ItemStack usedStack) {
        int cooldownTicks = hasTarget ? getPierceCooldown(usedStack) : getNoTargetCooldown(usedStack);
        player.getCooldowns().addCooldown(usedStack.getItem(), cooldownTicks);
        ItemStack otherHandStack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem()
                : player.getMainHandItem();
        if (otherHandStack.getItem() == usedStack.getItem()) {
            player.getCooldowns().addCooldown(otherHandStack.getItem(), cooldownTicks);
        }
    }

    @Unique
    private void applySpecialEffects(Level level, LivingEntity target, ItemStack itemStack) {
        int fireAspectLevel = itemStack
                .getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.FIRE_ASPECT);
        if (fireAspectLevel > 0) {
            target.setSecondsOnFire(10 + fireAspectLevel * 5);
        }
        int knockbackLevel = itemStack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.KNOCKBACK);
        if (knockbackLevel > 0) {
            double knockbackStrength = knockbackLevel * 0.5;
            Vec3 knockbackDirection = target.position().subtract(target.position()).normalize()
                    .scale(knockbackStrength);
            target.push(knockbackDirection.x, knockbackDirection.y + 0.1, knockbackDirection.z);
        }
    }

    @Unique
    private float calculateDamage(ItemStack itemStack) {
        java.util.Collection<net.minecraft.world.entity.ai.attributes.AttributeModifier> modifiers = itemStack
                .getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE);
        net.minecraft.world.entity.ai.attributes.AttributeModifier modifier = modifiers.stream().findFirst()
                .orElse(null);
        return modifier != null ? (float) modifier.getAmount() : 1.0F;
    }

    @Unique
    private DamageSource createDamageSource(Level level, Player player) {
        ResourceKey<DamageType> damageTypeKey = ResourceKey.create(Registries.DAMAGE_TYPE,
                new ResourceLocation("jaams_weaponry:piercing"));
        Holder<DamageType> damageTypeHolder = level
                .registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(damageTypeKey)
                .orElseThrow(() -> new IllegalStateException("Damage type not found: " + damageTypeKey.location()));
        return new DamageSource(damageTypeHolder, player);
    }



    @Unique
    private float getMinDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultMinDamage")) {
            return Math.max(0.0F, tag.getFloat("PiercingAssaultMinDamage"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.min_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_MIN_DAMAGE.get().floatValue());
    }

    @Unique
    private float getDashDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultDashDistance")) {
            return Math.max(0.1F, tag.getFloat("PiercingAssaultDashDistance"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.dash_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DASH_DISTANCE.get().floatValue());
    }

    @Unique
    private float getPierceRange(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultPierceRange")) {
            return Math.max(0.1F, tag.getFloat("PiercingAssaultPierceRange"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.pierce_range)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_RANGE.get().floatValue());
    }

    @Unique
    private int getPierceCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultPierceCooldown")) {
            return Math.max(1, tag.getInt("PiercingAssaultPierceCooldown"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.pierce_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_PIERCE_COOLDOWN.get());
    }

    @Unique
    private int getNoTargetCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultNoTargetCooldown")) {
            return Math.max(1, tag.getInt("PiercingAssaultNoTargetCooldown"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.no_target_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_NO_TARGET_COOLDOWN.get());
    }

    @Unique
    private int getDurabilityCost(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercingAssaultDurabilityCost")) {
            return Math.max(0, tag.getInt("PiercingAssaultDurabilityCost"));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DURABILITY_COST.get());
    }

    @Unique
    private void applyDepletionEffect(Level level, Player player, ItemStack stack) {
        if (!getEnableDepletion(stack) || level.isClientSide() || player == null) {
            return;
        }
        float depletionChance = getDepletionChance(stack);
        if (level.random.nextFloat() < depletionChance) {
            int depletionLevel = getDepletionLevel(stack);
            int depletionMaxLevel = getDepletionMaxLevel(stack);
            int depletionDuration = getDepletionDuration(stack);
            int depletionMaxDuration = getDepletionMaxDuration(stack);
            int calculatedDepletionLevel = Math.min(depletionLevel, depletionMaxLevel);
            int calculatedDepletionDuration = Math.min(depletionDuration, depletionMaxDuration);
            MobEffectInstance depletionEffect = new MobEffectInstance(
                    ModMobEffects.DEPLETION.get(), calculatedDepletionDuration,
                    calculatedDepletionLevel - 1, false, false, true);
            player.addEffect(depletionEffect);
        }
    }

    @Unique
    private float getDepletionChance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_CHANCE)) {
            return (float) Mth.clamp(tag.getFloat(KEY_DEPLETION_CHANCE), 0.0, 1.0);
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.depletion_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DEPLETION_CHANCE.get().floatValue());
    }

    @Unique
    private int getDepletionDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_DURATION));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.depletion_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DEPLETION_DURATION.get());
    }

    @Unique
    private int getDepletionLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_LEVEL));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.depletion_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DEPLETION_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_LEVEL));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.depletion_max_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DEPLETION_MAX_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_DURATION));
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.depletion_max_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DEPLETION_MAX_DURATION.get());
    }

    @Unique
    private boolean getEnableDepletion(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_ENABLE_DEPLETION)) {
            return tag.getBoolean(KEY_ENABLE_DEPLETION);
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map(e -> e.enable_depletion)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_ENABLE_DEPLETION.get());
    }

}
