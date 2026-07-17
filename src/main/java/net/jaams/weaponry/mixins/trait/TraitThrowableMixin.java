package net.jaams.weaponry.mixins.trait;

import java.util.List;
import java.util.Locale;
import net.jaams.weaponry.component.projectile.BaseReturningProjectileEntity;
import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.data.ThrowableTypeData;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.loader.ThrowableModifierLoader;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class TraitThrowableMixin {

    @Unique
    private List<ThrowableItemData> getJsonEntries(ItemStack stack) {
        return ThrowableModifierLoader.INSTANCE.getForItem(stack.getItem());
    }

    @Unique
    private boolean isThrowableEnabled(ItemStack stack) {
        Boolean nbt = getBooleanNBT(stack, "ThrowableTrait");
        if (nbt != null)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.throw_enabled != null) {
            return TraitsConfig.THROWABLE.get() && json.throw_enabled;
        }
        ThrowableTypeData legacy = getLegacyType(stack);
        if (legacy != null) {
            return TraitsConfig.THROWABLE.get() && ThrowableTypeData.isEnabled(legacy.name);
        }
        return false;
    }

    @Unique
    private ThrowableItemData getHighestJsonEntry(ItemStack stack) {
        List<ThrowableItemData> entries = getJsonEntries(stack);
        if (entries.isEmpty())
            return null;
        for (ThrowableItemData entry : entries) {
            if (ThrowableModifierLoader.INSTANCE.evaluateConditions(entry, stack)) {
                return entry;
            }
        }
        return null;
    }

    @Unique
    private ThrowableItemData.ThrowableEntry getJsonData(ItemStack stack) {
        ThrowableItemData entry = getHighestJsonEntry(stack);
        return entry != null ? entry.throwable : null;
    }

    @Unique
    private ThrowableTypeData getLegacyType(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            for (ThrowableTypeData type : ThrowableTypeData.ALL_TYPES) {
                if (tag.getBoolean(type.forceNbtKey)) {
                    return type;
                }
            }
        }
        return ThrowableTypeData.getType(stack);
    }

    @Unique
    private boolean isAnyThrowable(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("ThrowableTrait")
                && !stack.getTag().getBoolean("ThrowableTrait")) {
            return false;
        }
        return getJsonData(stack) != null || getLegacyType(stack) != null;
    }

    @Unique
    private boolean shouldApplyCustomThrowableLogic(ItemStack stack) {
        if (stack.is(Items.TRIDENT) && !ItemFeaturesConfig.TRIDENT_USE_CUSTOM_THROW.get()) {
            return false;
        }
        return isAnyThrowable(stack) && isThrowableEnabled(stack);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaam$onThrowableUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        if (hand == InteractionHand.MAIN_HAND && player.isCrouching()
                && ModUtils.isBlockableItem(player.getItemInHand(InteractionHand.OFF_HAND)))
            return;
        if (hand == InteractionHand.MAIN_HAND
                && ModUtils.isBlockableItem(player.getItemInHand(InteractionHand.OFF_HAND))
                && (Boolean.TRUE.equals(getBooleanNBT(stack, "PrioritizeShield"))
                        || stack.is(ModTags.PRIORITIZE_SHIELD)))
            return;
        if (!canThrowWithDurabilityCheck(stack)) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
            return;
        }
        ModEnums.ThrowMode mode = getCurrentThrowMode(stack);
        if (isRiptideTrident(stack, player) && !player.isInWaterOrRain()) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
            return;
        }
        if (mode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK) {
            throwInstant(level, player, hand, stack);
            if (!level.isClientSide)
                player.swing(hand, true);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
            return;
        }
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaam$modifyThrowableUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        cir.setReturnValue(getUseDurationTicks(stack));
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaam$changeThrowableUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        cir.setReturnValue(getCurrentUseAnimation(stack));
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaam$onReleaseThrowable(Level level, LivingEntity entity, int durationUsed, CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;
        ItemStack stack = (ItemStack) (Object) this;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        ModEnums.ThrowMode mode = getCurrentThrowMode(stack);
        if (mode != ModEnums.ThrowMode.CHARGE_AND_RELEASE && mode != ModEnums.ThrowMode.CHARGE_RELEASE_AND_FINISH)
            return;
        int remaining = stack.getUseDuration() - durationUsed;
        if (remaining < getMinChargeTicks(stack))
            return;
        float power = ModUtils.getPowerForTime(remaining, getMinChargeTicks(stack), getMaxChargeTicks(stack));
        throwCharged(level, player, player.getUsedItemHand(), stack, power);
        ci.cancel();
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaam$onFinishUsing(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (!(entity instanceof Player player))
            return;
        ItemStack stack = (ItemStack) (Object) this;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        ModEnums.ThrowMode mode = getCurrentThrowMode(stack);
        if (mode != ModEnums.ThrowMode.CHARGE_AND_FINISH_USING && mode != ModEnums.ThrowMode.CHARGE_RELEASE_AND_FINISH)
            return;
        throwCharged(level, player, player.getUsedItemHand(), stack, 1.0f);
        cir.setReturnValue(stack);
    }

    @Unique
    private ModEnums.ThrowMode getCurrentThrowMode(ItemStack stack) {
        String nbt = getStringNBT(stack, "ThrowableThrowMode");
        if (nbt != null && !nbt.isEmpty()) {
            try {
                return ModEnums.ThrowMode.valueOf(nbt.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.throw_mode != null && !json.throw_mode.isEmpty()) {
            try {
                return ModEnums.ThrowMode.valueOf(json.throw_mode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getThrowMode(legacy.name) : ModEnums.ThrowMode.CHARGE_AND_RELEASE;
    }

    @Unique
    private int getUseDurationTicks(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableUseDurationTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.use_duration_ticks != null && json.use_duration_ticks >= 0)
            return json.use_duration_ticks;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getUseDurationTicks(legacy.name) : 72000;
    }

    @Unique
    private int getMinChargeTicks(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableMinChargeTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.min_charge_ticks != null && json.min_charge_ticks >= 0)
            return json.min_charge_ticks;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMinChargeTicks(legacy.name) : 0;
    }

    @Unique
    private int getMaxChargeTicks(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableMaxChargeTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.max_charge_ticks != null && json.max_charge_ticks >= 0)
            return json.max_charge_ticks;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMaxChargeTicks(legacy.name) : 20;
    }

    @Unique
    private int getInstantCooldownTicks(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableInstantCooldownTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.instant_cooldown_ticks != null && json.instant_cooldown_ticks >= 0)
            return json.instant_cooldown_ticks;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getInstantCooldownTicks(legacy.name) : 0;
    }

    @Unique
    private float getMinSpeed(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableMinSpeed");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.min_speed != null && json.min_speed >= 0)
            return json.min_speed;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMinSpeed(legacy.name) : 1.0f;
    }

    @Unique
    private float getMaxSpeed(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableMaxSpeed");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.max_speed != null && json.max_speed >= 0)
            return json.max_speed;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMaxSpeed(legacy.name) : 2.2f;
    }

    @Unique
    private float getInaccuracy(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableInaccuracy");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.inaccuracy != null && json.inaccuracy >= 0)
            return json.inaccuracy;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getInaccuracy(legacy.name) : 1.0f;
    }

    @Unique
    private float getCriticalPowerThreshold(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableCriticalPowerThreshold");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.critical_power_threshold != null && json.critical_power_threshold >= 0)
            return json.critical_power_threshold;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getCriticalPowerThreshold(legacy.name) : 0.8f;
    }

    @Unique
    private int getDamageOnThrow(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableDamageOnThrow");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.damage_on_throw != null && json.damage_on_throw >= 0)
            return json.damage_on_throw;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getDamageOnThrow(legacy.name) : 1;
    }

    @Unique
    private int getMinRemainingDurability(ItemStack stack) {
        Integer nbt = getIntNBT(stack, "ThrowableMinRemainingDurability");
        if (nbt != null)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.min_remaining_durability != null)
            return json.min_remaining_durability;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMinRemainingDurability(legacy.name) : -1;
    }

    @Unique
    private boolean getAllowMultishot(ItemStack stack) {
        Boolean nbt = getBooleanNBT(stack, "ThrowableAllowMultishot");
        if (nbt != null)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.allow_multishot != null)
            return json.allow_multishot;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getAllowMultishot(legacy.name) : false;
    }

    @Unique
    private float getMultishotSpreadAngle(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableMultishotSpreadAngle");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.multishot_spread_angle != null && json.multishot_spread_angle >= 0)
            return json.multishot_spread_angle;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getMultishotSpreadAngle(legacy.name) : 10.0f;
    }

    @Unique
    private boolean getRememberSlot(ItemStack stack) {
        Boolean nbt = getBooleanNBT(stack, "ThrowableRememberSlot");
        if (nbt != null)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.remember_slot != null)
            return json.remember_slot;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getRememberSlot(legacy.name) : false;
    }

    @Unique
    private float getRecoil(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableRecoil");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.recoil != null && json.recoil >= 0)
            return json.recoil;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getRecoil(legacy.name) : 0.0f;
    }

    @Unique
    private boolean getRecoilOnlyFullyCharged(ItemStack stack) {
        Boolean nbt = getBooleanNBT(stack, "ThrowableRecoilOnlyFullyCharged");
        if (nbt != null)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.recoil_only_fully_charged != null)
            return json.recoil_only_fully_charged;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null && ThrowableTypeData.getRecoilOnlyFullyCharged(legacy.name);
    }

    @Unique
    private float getRecoilCrouchReduction(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableRecoilCrouchReduction");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.recoil_crouch_reduction != null && json.recoil_crouch_reduction >= 0)
            return json.recoil_crouch_reduction;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getRecoilCrouchReduction(legacy.name) : 0.4f;
    }

    @Unique
    private float getRecoilVerticalMultiplier(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableRecoilVerticalMultiplier");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.recoil_vertical_multiplier != null && json.recoil_vertical_multiplier >= 0)
            return json.recoil_vertical_multiplier;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getRecoilVerticalMultiplier(legacy.name) : 1.0f;
    }

    @Unique
    private float getRecoilPitchKick(ItemStack stack) {
        Float nbt = getFloatNBT(stack, "ThrowableRecoilPitchKick");
        if (nbt != null && nbt >= 0)
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.recoil_pitch_kick != null && json.recoil_pitch_kick >= 0)
            return json.recoil_pitch_kick;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getRecoilPitchKick(legacy.name) : 8.0f;
    }

    @Unique
    private UseAnim getCurrentUseAnimation(ItemStack stack) {
        String nbtValue = getStringNBT(stack, "ThrowableUseAnimation");
        if (nbtValue != null && !nbtValue.isEmpty()) {
            return parseUseAnimation(nbtValue);
        }
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.use_animation != null && !json.use_animation.isEmpty()) {
            return parseUseAnimation(json.use_animation);
        }
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? ThrowableTypeData.getUseAnimation(legacy.name) : UseAnim.SPEAR;
    }

    @Unique
    private UseAnim parseUseAnimation(String animationStr) {
        if (animationStr == null || animationStr.isEmpty()) {
            return UseAnim.SPEAR;
        }
        String upper = animationStr.toUpperCase(Locale.ROOT).trim();
        return switch (upper) {
            case "BOW" -> UseAnim.BOW;
            case "CROSSBOW" -> UseAnim.CROSSBOW;
            case "SPEAR" -> UseAnim.SPEAR;
            case "NONE" -> UseAnim.NONE;
            case "EAT" -> UseAnim.EAT;
            case "DRINK" -> UseAnim.DRINK;
            case "BLOCK" -> UseAnim.BLOCK;
            case "SPYGLASS" -> UseAnim.SPYGLASS;
            default -> UseAnim.SPEAR;
        };
    }

    @Unique
    private String getProjectileType(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;
        String nbt = getStringNBT(stack, "ThrowableProjectileType");
        if (nbt != null && !nbt.isEmpty())
            return nbt;
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.projectile != null && !json.projectile.isEmpty())
            return json.projectile;
        ThrowableTypeData legacy = getLegacyType(stack);
        return legacy != null ? legacy.name : null;
    }

    @Unique
    private void throwInstant(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!canThrowWithDurabilityCheck(stack))
            return;
        boolean success = performThrowWithDamageCheck(level, player, hand, stack, 1.0f);
        if (success && !level.isClientSide) {
            int cooldown = getInstantCooldownTicks(stack);
            if (cooldown > 0) {
                String currentType = getProjectileType(stack);
                if (currentType != null && !currentType.isEmpty()) {
                    for (ItemStack invStack : player.getInventory().items) {
                        if (!invStack.isEmpty() && currentType.equals(getProjectileType(invStack))) {
                            player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
                        }
                    }
                    for (ItemStack invStack : player.getInventory().armor) {
                        if (!invStack.isEmpty() && currentType.equals(getProjectileType(invStack))) {
                            player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
                        }
                    }
                    for (ItemStack invStack : player.getInventory().offhand) {
                        if (!invStack.isEmpty() && currentType.equals(getProjectileType(invStack))) {
                            player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
                        }
                    }
                } else {
                    player.getCooldowns().addCooldown(stack.getItem(), cooldown);
                }
            }
            boolean isCreative = player.getAbilities().instabuild;
            if (!isCreative) {
                stack.shrink(1);
                if (stack.isEmpty())
                    player.setItemInHand(hand, ItemStack.EMPTY);
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
    }

    @Unique
    private void throwCharged(Level level, Player player, InteractionHand hand, ItemStack stack, float power) {
        if (!canThrowWithDurabilityCheck(stack))
            return;
        boolean success = performThrowWithDamageCheck(level, player, hand, stack, power);
        if (success && !level.isClientSide) {
            boolean isCreative = player.getAbilities().instabuild;
            if (!isCreative && !isRiptideTrident(stack, player)) {
                stack.shrink(1);
                if (stack.isEmpty())
                    player.setItemInHand(hand, ItemStack.EMPTY);
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
    }

    @Unique
    private boolean performThrowWithDamageCheck(Level level, Player player, InteractionHand hand, ItemStack stack,
            float power) {
        boolean isCreative = player.getAbilities().instabuild;
        int damageAmount = getDamageOnThrow(stack);
        if (!isCreative && !level.isClientSide && ModUtils.hasDurability(stack)) {
            if (stack.getDamageValue() + damageAmount >= stack.getMaxDamage()) {
                ModUtils.applyTraitDurabilityCost(stack, player, damageAmount, (p) -> p.broadcastBreakEvent(hand));
                return false;
            }
            ModUtils.applyTraitDurabilityCost(stack, player, damageAmount, (p) -> p.broadcastBreakEvent(hand));
        }
        performThrow(level, player, hand, stack, power);
        return true;
    }

    @Unique
    private boolean canThrowWithDurabilityCheck(ItemStack stack) {
        int minRemaining = getMinRemainingDurability(stack);
        if (!ModUtils.hasDurability(stack))
            return true;
        int currentRemaining = stack.getMaxDamage() - stack.getDamageValue();
        if (minRemaining == 0)
            return true;
        if (minRemaining == -1)
            return currentRemaining >= 2;
        return currentRemaining >= minRemaining;
    }

    @Unique
    private void performThrow(Level level, Player player, InteractionHand hand, ItemStack stack, float power) {
        if (level.isClientSide)
            return;
        if (!shouldApplyCustomThrowableLogic(stack))
            return;
        if (isRiptideTrident(stack, player)) {
            int riptideLevel = EnchantmentHelper.getRiptide(stack);
            applyRiptideImpulse(player, riptideLevel, level, stack);
            return;
        }
        float minSpeed = getMinSpeed(stack);
        float maxSpeed = getMaxSpeed(stack);
        float speed = minSpeed + (maxSpeed - minSpeed) * power * power;
        float inaccuracy = getInaccuracy(stack);
        boolean allowMultishot = getAllowMultishot(stack);
        boolean rememberSlot = getRememberSlot(stack);
        float criticalThreshold = getCriticalPowerThreshold(stack);
        boolean isCreative = player.getAbilities().instabuild;
        int multishotLevel = allowMultishot ? EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT, stack)
                : 0;
        int projectileCount = multishotLevel > 0 ? 3 : 1;
        float spreadAngle = getMultishotSpreadAngle(stack);
        float[] shotPitches = ModUtils.generateShotPitches(level.getRandom(), projectileCount);
        boolean isInstant = getCurrentThrowMode(stack) == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK;
        int flameLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack);
        boolean isFlaming = flameLevel > 0;
        for (int i = 0; i < projectileCount; i++) {
            ItemStack projectileStack = stack.copy();
            BaseWeaponProjectileEntity projectile = createProjectile(level, player, projectileStack, stack);
            if (projectile == null)
                continue;
            if (isFlaming)
                projectile.setSecondsOnFire(100);
            if (rememberSlot) {
                int slotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
                projectile.setOriginalSlotIndex(slotIndex);
            }
            boolean shouldBeCritical = isInstant ? ModUtils.isProjectileCritical(player, power)
                    : (power >= criticalThreshold && ModUtils.isProjectileCritical(player, power));
            if (shouldBeCritical)
                projectile.setCritical(true);
            if (multishotLevel > 0) {
                projectile.pickup = isCreative ? AbstractArrow.Pickup.CREATIVE_ONLY
                        : (i == 0 ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED);
                if (i != 0)
                    projectile.getPersistentData().putBoolean("IsMultishotClone", true);
                float rotation = (i == 0) ? 0.0F : (i == 1) ? -spreadAngle : spreadAngle;
                Vec3 upVector = player.getUpVector(1.0F);
                Quaternionf rotationQuaternion = new Quaternionf().setAngleAxis((rotation * (float) Math.PI) / 180F,
                        (float) upVector.x, (float) upVector.y, (float) upVector.z);
                Vec3 viewVector = player.getViewVector(1.0F);
                Vector3f rotatedVector = new Vector3f((float) viewVector.x, (float) viewVector.y, (float) viewVector.z)
                        .rotate(rotationQuaternion);
                if (i == 0) {
                    Vec3 offset = viewVector.scale(1.0F);
                    projectile.setPos(player.getX() + offset.x, player.getEyeY() + offset.y, player.getZ() + offset.z);
                }
                projectile.shoot(rotatedVector.x(), rotatedVector.y(), rotatedVector.z(), speed, inaccuracy);
            } else {
                projectile.pickup = isCreative ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);
            }
            applyReturningProjectileSettings(projectile, stack, power);
            projectile.setWeaponItem(stack.copy());
            level.addFreshEntity(projectile);
            SoundEvent shootSound = getShootSound(stack);
            level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), shootSound,
                    SoundSource.PLAYERS, 1.0F, shotPitches[i]);
        }
        boolean recoilOnlyFullyCharged = getRecoilOnlyFullyCharged(stack);
        boolean shouldApplyRecoil = !recoilOnlyFullyCharged || power >= 0.99f;
        if (shouldApplyRecoil) {
            float recoilDistance = getRecoil(stack) * power;
            float crouchReduction = getRecoilCrouchReduction(stack);
            float verticalMult = getRecoilVerticalMultiplier(stack);
            ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMult);
        } else {
            float pitchChange = getRecoilPitchKick(stack) * power;
            player.setXRot(player.getXRot() - pitchChange);
        }
    }

    @Unique
    private boolean isRiptideTrident(ItemStack stack, Player player) {
        return EnchantmentHelper.getRiptide(stack) > 0;
    }

    @Unique
    private void applyRiptideImpulse(Player player, int riptideLevel, Level level, ItemStack stack) {
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        float xMotion = -Mth.sin(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
        float yMotion = -Mth.sin(xRot * ((float) Math.PI / 180F));
        float zMotion = Mth.cos(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
        float magnitude = Mth.sqrt(xMotion * xMotion + yMotion * yMotion + zMotion * zMotion);
        float riptideSpeed = 3.0F * ((1.0F + (float) riptideLevel) / 4.0F);
        if (magnitude > 0.0F) {
            xMotion *= riptideSpeed / magnitude;
            yMotion *= riptideSpeed / magnitude;
            zMotion *= riptideSpeed / magnitude;
        }
        player.setDeltaMovement(xMotion, yMotion, zMotion);
        player.hurtMarked = true;
        player.startAutoSpinAttack(20);
        if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999D, 0.0D));
        }
        SoundEvent riptideSound = switch (riptideLevel) {
            case 3 -> SoundEvents.TRIDENT_RIPTIDE_3;
            case 2 -> SoundEvents.TRIDENT_RIPTIDE_2;
            default -> SoundEvents.TRIDENT_RIPTIDE_1;
        };
        level.playSound(null, player.getX(), player.getY(), player.getZ(), riptideSound, SoundSource.PLAYERS, 1.0F,
                1.0F);
    }

    @Unique
    private void applyReturningProjectileSettings(BaseWeaponProjectileEntity projectile, ItemStack stack, float power) {
        if (!(projectile instanceof BaseReturningProjectileEntity returning))
            return;
        String type = getProjectileType(stack);
        double minRange = ModProjectiles.getThrowbackMinRange(stack,
                ThrowableTypeData.getThrowbackMinRangeDefault(type));
        float maxRange = ModProjectiles.getThrowbackMaxRange(stack,
                ThrowableTypeData.getThrowbackMaxRangeDefault(type));
        double returnSpeed = ModProjectiles.getThrowbackReturnSpeed(stack,
                ThrowableTypeData.getThrowbackReturnSpeedDefault(type));
        double weaponRangeCalc = minRange + (power * 20.0);
        float finalRange = (float) Math.min(weaponRangeCalc, maxRange);
        returning.setWeaponRange(finalRange);
        returning.setReturnSpeed(returnSpeed);
    }

    @Unique
    private BaseWeaponProjectileEntity createProjectile(Level level, Player player, ItemStack projectileStack,
            ItemStack weaponStack) {
        String type = getProjectileType(weaponStack);
        if (type == null || type.isEmpty())
            return null;
        return ThrowableTypeData.createProjectileEntity(type, level, player, projectileStack);
    }

    @Unique
    private SoundEvent getShootSound(ItemStack stack) {
        if (stack.hasTag()) {
            try {
                CompoundTag tag = stack.getTag();
                if (tag.contains("ThrowableThrowSound", Tag.TAG_STRING)) {
                    String soundId = tag.getString("ThrowableThrowSound");
                    ResourceLocation resLoc = ResourceLocation.tryParse(soundId);
                    SoundEvent customSound = ForgeRegistries.SOUND_EVENTS.getValue(resLoc);
                    if (customSound != null)
                        return customSound;
                }
            } catch (Exception ignored) {
            }
        }
        ThrowableItemData.ThrowableEntry json = getJsonData(stack);
        if (json != null && json.throw_sound != null && !json.throw_sound.isEmpty()) {
            try {
                ResourceLocation loc = ResourceLocation.tryParse(json.throw_sound);
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(loc);
                if (sound != null)
                    return sound;
            } catch (Exception ignored) {
            }
        }
        ThrowableTypeData legacy = getLegacyType(stack);
        if (legacy != null) {
            return ThrowableTypeData.getShootSound(legacy.name);
        }
        return ModSounds.PROJECTILE_THROW.get();
    }

    @Unique
    private Integer getIntNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_INT)) ? tag.getInt(key) : null;
    }

    @Unique
    private Float getFloatNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_FLOAT)) ? tag.getFloat(key) : null;
    }

    @Unique
    private Boolean getBooleanNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_BYTE)) ? tag.getBoolean(key) : null;
    }

    @Unique
    private String getStringNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_STRING)) ? tag.getString(key) : null;
    }
}
