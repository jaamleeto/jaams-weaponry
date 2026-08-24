package net.jaams.weaponry.mixins.trait;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.jaams.weaponry.init.ModMobEffects;
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
import net.minecraft.world.entity.Entity;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitSlashAssaultMixin {



    @Unique
    private static final String CHARGE_TAG = "SlashAssaultCharging";

    @Unique
    private static final String KEY_DEPLETION_CHANCE = "SlashAssaultDepletionChance";
    @Unique
    private static final String KEY_DEPLETION_DURATION = "SlashAssaultDepletionDuration";
    @Unique
    private static final String KEY_DEPLETION_LEVEL = "SlashAssaultDepletionLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_LEVEL = "SlashAssaultDepletionMaxLevel";
    @Unique
    private static final String KEY_DEPLETION_MAX_DURATION = "SlashAssaultDepletionMaxDuration";
    @Unique
    private static final String KEY_ENABLE_DEPLETION = "SlashAssaultEnableDepletion";



    @Unique
    private boolean isSlashAssaultEnabled(ItemStack stack) {
        if (!TraitsConfig.SLASH_ASSAULT.get()) {
            return false;
        }
        return ModTraits.isSlashAssaultItem(stack);
    }

    @Unique
    private ModEnums.SlashAssaultMode getActivationMode(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultMode")) {
            try {
                return ModEnums.SlashAssaultMode.valueOf(tag.getString("SlashAssaultMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }

        ModEnums.SlashAssaultMode jsonMode = TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_assault_mode)
                .filter(java.util.Objects::nonNull)
                .filter((m) -> !m.isEmpty())
                .map((m) -> {
                    try {
                        return ModEnums.SlashAssaultMode.valueOf(m.toUpperCase(Locale.ROOT));
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .orElse(null);
        if (jsonMode != null) {
            return jsonMode;
        }

        return TraitsConfig.SLASH_ASSAULT_ACTIVATION_MODE.get();
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
        if (tag != null && tag.contains("SlashAssaultMinChargeTicks")) {
            return Math.max(0, tag.getInt("SlashAssaultMinChargeTicks"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.min_charge_ticks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_MIN_CHARGE_TICKS.get());
    }



    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onSlashAssaultUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isSlashAssaultEnabled(stack)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        ModEnums.SlashAssaultMode mode = getActivationMode(stack);

        if (isChargeMode(stack)) {
            player.startUsingItem(hand);
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putBoolean(CHARGE_TAG, true);
            nbt.putInt(CHARGE_TAG + "StartTick", player.tickCount);
        } else if (mode == ModEnums.SlashAssaultMode.INSTANT_ON_RIGHT_CLICK) {

            if (level.isClientSide()) {
                if (TraitsConfig.SLASH_ASSAULT_DASH_SWING.get()) {
                    player.swing(hand, true);
                }
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            activateSlashAssault(level, player, stack, hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        } else {

            if (!player.isSprinting()) {
                return;
            }
            if (!player.onGround()) {
                return;
            }
            if (level.isClientSide()) {
                if (TraitsConfig.SLASH_ASSAULT_DASH_SWING.get()) {
                    player.swing(hand, true);
                }
                cir.setReturnValue(InteractionResultHolder.consume(stack));
                return;
            }
            activateSlashAssault(level, player, stack, hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        }
    }



    @Unique
    private int getUseDurationTicks(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultUseDurationTicks")) {
            return Math.max(1, tag.getInt("SlashAssaultUseDurationTicks"));
        }

        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.use_duration_ticks)
                .filter(java.util.Objects::nonNull)
                .filter(d -> d >= 0)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_CHARGE_DURATION_TICKS.get());
    }

    @Unique
    private UseAnim getCurrentUseAnimation(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultUseAnimation")) {
            String anim = tag.getString("SlashAssaultUseAnimation");
            if (anim != null && !anim.isEmpty()) {
                return parseUseAnimation(anim);
            }
        }

        String jsonAnim = TraitModifierData.getSlashAssault(stack)
                .map(e -> e.use_animation)
                .filter(java.util.Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .orElse(null);
        if (jsonAnim != null) {
            return parseUseAnimation(jsonAnim);
        }

        return TraitsConfig.SLASH_ASSAULT_CHARGE_ANIMATION.get();
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
    private void jaams$slashAssaultGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isSlashAssaultEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getCurrentUseAnimation(stack));
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaams$slashAssaultGetUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isSlashAssaultEnabled(stack) && isChargeMode(stack) && isCharging(stack)) {
            cir.setReturnValue(getUseDurationTicks(stack));
        }
    }



    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaams$onSlashAssaultRelease(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isSlashAssaultEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.SlashAssaultMode mode = getActivationMode(stack);
        if (mode != ModEnums.SlashAssaultMode.CHARGE_AND_RELEASE
                && mode != ModEnums.SlashAssaultMode.CHARGE_RELEASE_AND_FINISH) {
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
        activateSlashAssault(level, player, stack, player.getUsedItemHand());
    }



    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaams$onSlashAssaultFinishUsing(Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isSlashAssaultEnabled(stack) || !(entity instanceof Player player)) {
            return;
        }
        ModEnums.SlashAssaultMode mode = getActivationMode(stack);
        if (mode != ModEnums.SlashAssaultMode.CHARGE_AND_FINISH_USING
                && mode != ModEnums.SlashAssaultMode.CHARGE_RELEASE_AND_FINISH) {
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
        activateSlashAssault(level, player, stack, player.getUsedItemHand());
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
    private void activateSlashAssault(Level level, Player player, ItemStack itemStack, InteractionHand hand) {
        Vec3 startPos = player.position();
        applyDashMovement(player, itemStack, hand);
        Vec3 endPos = player.position();
        boolean hasTargets = applySlashAttack(level, player, itemStack, hand, startPos, endPos);
        playDashSound(level, player);
        if (!player.isCreative()) {
            float exhaustion = 1.0F + player.getRandom().nextFloat() * 1.0F;
            player.getFoodData().addExhaustion(exhaustion);
        }
        if (hasTargets) {
            applyItemDamage(level, player, itemStack);
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            if (hand == InteractionHand.MAIN_HAND && offHandItem.is(ModTags.KATANAS)) {
                applyItemDamage(level, player, offHandItem);
            } else if (hand == InteractionHand.OFF_HAND && mainHandItem.is(ModTags.KATANAS)) {
                applyItemDamage(level, player, mainHandItem);
            }
            if (TraitsConfig.SLASH_ASSAULT_ATTACK_SWING.get()) {
                startSlashAnimation(player);
            }
        }
        applyCooldowns(player, hand, hasTargets, itemStack);
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
        if (TraitsConfig.SLASH_ASSAULT_DASH_SWING.get()) {
            player.swing(hand, true);
        }
    }

    @Unique
    private void playDashSound(Level level, Player player) {
        if (!level.isClientSide()) {
            ResourceLocation dashSoundId = ResourceLocation.parse("jaams_weaponry:dash");
            SoundEvent dashSound = ForgeRegistries.SOUND_EVENTS.getValue(dashSoundId);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    dashSound != null ? dashSound : SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Unique
    private boolean applySlashAttack(Level level, Player player, ItemStack itemStack, InteractionHand hand,
            Vec3 startPos, Vec3 endPos) {
        double baseRange = getSlashRange(itemStack);
        double range = baseRange
                + itemStack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE) * 0.5;
        AABB hitBox = new AABB(startPos, endPos).inflate(range, range * 0.5, range);
        List<Entity> entities = level
                .getEntitiesOfClass(
                        Entity.class,
                        hitBox,
                        (e) -> e != player && e instanceof LivingEntity && ((LivingEntity) e).isAlive()
                                && (!(e instanceof Player) || !((Player) e).isCreative())
                                && !ModUtils.isAlliedEntity(player, e)
                                && e.position().distanceToSqr(startPos) <= range * range * 2)
                .stream()
                .sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(player.position())))
                .toList();
        if (entities.isEmpty()) {
            if (TraitsConfig.SLASH_ASSAULT_ATTACK_SWING.get()) {
                startSlashAnimation(player);
            } else {
                player.swing(hand, true);
            }
            return false;
        }
        itemStack.getOrCreateTag().putBoolean("katanaslash", true);
        if (!level.isClientSide()) {
            SoundEvent slashSound = ForgeRegistries.SOUND_EVENTS
                    .getValue(ResourceLocation.parse("jaams_weaponry:katana_slash"));
            level.playSound(null, player.blockPosition(),
                    slashSound != null ? slashSound : SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.5F, 1.0F);
        }
        for (Entity target : entities) {
            net.jaams.weaponry.JaamsWeaponryMod.queueServerWork(20,
                    () -> applyEntityDamage(level, player, itemStack, target));
        }
        net.jaams.weaponry.JaamsWeaponryMod.queueServerWork(20,
                () -> itemStack.getOrCreateTag().putBoolean("katanaslash", false));
        return true;
    }

    @Unique
    private void applyEntityDamage(Level level, Player player, ItemStack itemStack, Entity target) {
        if (!(target instanceof LivingEntity livingTarget) || ModUtils.isAlliedEntity(player, livingTarget)
                || !livingTarget.isAlive()) {
            return;
        }
        float baseDamage = calculateDamage(itemStack);
        float enchantmentDamage = calculateEnchantmentDamage(itemStack, livingTarget);
        float playerAttackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        float damageMultiplier = 1.0F;
        float totalDamage = (baseDamage + playerAttackDamage / 2 + enchantmentDamage) * 0.6F - 2.0F;
        totalDamage *= damageMultiplier;

        float minDamage = getMinDamage(itemStack);
        if (totalDamage < minDamage) {
            totalDamage = minDamage;
        }
        if (totalDamage > 0) {
            livingTarget.hurt(createDamageSource(level, player), totalDamage);
            applySpecialEffects(level, livingTarget, itemStack);
            if (level instanceof ServerLevel serverLevel) {
                float r = 1.0F,
                        g = 1.0F,
                        b = 1.0F;
                float size = 1.0F;
                double yOffset = serverLevel.random.nextFloat() * 0.5 - 0.25;
                double particleY = livingTarget.getY() + 1.0 + yOffset;
                serverLevel.sendParticles(new CustomSweepParticleData(r, g, b, size), livingTarget.getX(), particleY,
                        livingTarget.getZ(), 1, 0, 0, 0, 0);
                SoundEvent cutSound = ForgeRegistries.SOUND_EVENTS
                        .getValue(ResourceLocation.parse("jaams_weaponry:katana_slash_cut"));
                serverLevel.playSound(null, livingTarget.blockPosition(),
                        cutSound != null ? cutSound : SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
            }
            if (livingTarget instanceof Mob mob && !player.isCreative()) {
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
    private void startSlashAnimation(Player player) {
        player.swing(InteractionHand.MAIN_HAND, true);
    }

    @Unique
    private void applyCooldowns(Player player, InteractionHand hand, boolean hasTargets, ItemStack usedStack) {
        int cooldownTicks = hasTargets ? getSlashCooldown(usedStack) : getNoTargetCooldown(usedStack);
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
                ResourceLocation.parse("jaams_weaponry:slash"));
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
        if (tag != null && tag.contains("SlashAssaultMinDamage")) {
            return Math.max(0.0F, tag.getFloat("SlashAssaultMinDamage"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.min_damage)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_MIN_DAMAGE.get().floatValue());
    }

    @Unique
    private float getDashDistance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultDashDistance")) {
            return Math.max(0.1F, tag.getFloat("SlashAssaultDashDistance"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.dash_distance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DASH_DISTANCE.get().floatValue());
    }

    @Unique
    private float getSlashRange(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultSlashRange")) {
            return Math.max(0.1F, tag.getFloat("SlashAssaultSlashRange"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_range)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_RANGE.get().floatValue());
    }

    @Unique
    private int getSlashCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultSlashCooldown")) {
            return Math.max(1, tag.getInt("SlashAssaultSlashCooldown"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_SLASH_COOLDOWN.get());
    }

    @Unique
    private int getNoTargetCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultNoTargetCooldown")) {
            return Math.max(1, tag.getInt("SlashAssaultNoTargetCooldown"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.no_target_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_NO_TARGET_COOLDOWN.get());
    }

    @Unique
    private int getDurabilityCost(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlashAssaultDurabilityCost")) {
            return Math.max(0, tag.getInt("SlashAssaultDurabilityCost"));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DURABILITY_COST.get());
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
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.depletion_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DEPLETION_CHANCE.get().floatValue());
    }

    @Unique
    private int getDepletionDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_DURATION));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.depletion_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DEPLETION_DURATION.get());
    }

    @Unique
    private int getDepletionLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_LEVEL));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.depletion_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DEPLETION_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_LEVEL)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_LEVEL));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.depletion_max_level)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DEPLETION_MAX_LEVEL.get());
    }

    @Unique
    private int getDepletionMaxDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_DEPLETION_MAX_DURATION)) {
            return Math.max(0, tag.getInt(KEY_DEPLETION_MAX_DURATION));
        }
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.depletion_max_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DEPLETION_MAX_DURATION.get());
    }

    @Unique
    private boolean getEnableDepletion(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_ENABLE_DEPLETION)) {
            return tag.getBoolean(KEY_ENABLE_DEPLETION);
        }
        return TraitModifierData.getSlashAssault(stack)
                .map(e -> e.enable_depletion)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_ENABLE_DEPLETION.get());
    }

}
