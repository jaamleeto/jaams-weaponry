package net.jaams.weaponry.handler.behavior.item;

import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.data.ThrowableTypeData;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.loader.ThrowableModifierLoader;
import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobThrowableBehaviorHandler {

    private static final double MAX_THROW_DISTANCE_SQ = 25.0 * 25.0;
    private static final double FOV_DOT_PRODUCT = 0.5;

    private static final Map<UUID, State> STATES = new HashMap<>();

    public static boolean tryExecute(Mob mob, long tick) {
        if (!MobBehaviorConfig.THROWABLE_MOBS_THROW.get()) return false;
        if (ModUtils.hasRestrictedEffect(mob)) return false;

        LivingEntity target = mob.getTarget();
        State state = STATES.get(mob.getUUID());
        if (state == null) {
            state = new State();
            STATES.put(mob.getUUID(), state);
        }

        if (target == null || !target.isAlive() || target == mob) {
            state.targetAcquiredTime = 0;
            return false;
        }
        if (mob.distanceToSqr(target) > MAX_THROW_DISTANCE_SQ) return false;

        if (MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_CHECK_CLEAR_SHOT.get()) {
            if (!hasClearShot(mob, target)) return false;
        } else if (!mob.getSensing().hasLineOfSight(target)) {
            return false;
        }

        if (!hasThrowableInEitherHand(mob)) return false;

        if (state.targetAcquiredTime == 0) state.targetAcquiredTime = tick;

        long timeSinceAcquired = tick - state.targetAcquiredTime;
        int initialCooldownMin = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS.get();
        int initialCooldownMax = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS.get();
        if (initialCooldownMax < initialCooldownMin) initialCooldownMax = initialCooldownMin;
        long initialCooldown = initialCooldownMin
                + mob.level().getRandom().nextInt(Math.max(1, initialCooldownMax - initialCooldownMin + 1));
        if (timeSinceAcquired < initialCooldown) return false;

        int cooldownMin = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS.get();
        int cooldownMax = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS.get();
        if (cooldownMax < cooldownMin) cooldownMax = cooldownMin;
        int baseCooldown = cooldownMin + mob.level().getRandom().nextInt(Math.max(1, cooldownMax - cooldownMin + 1));
        long totalCooldown = (long) (baseCooldown * getCooldownMultiplier(mob));

        if (tick - state.lastThrowTime < totalCooldown) return false;

        InteractionHand hand = getThrowableHand(mob);
        if (hand == null) return false;

        ItemStack throwableStack = mob.getItemInHand(hand);
        if (throwableStack.isEmpty()) return false;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceSq = mob.distanceToSqr(target);
        double meleeDist = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_MELEE_DISTANCE.get();
        double minThrowDist = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_MIN_THROW_DISTANCE.get();

        if (distanceSq <= meleeDist * meleeDist) {
            mob.doHurtTarget(target);
            mob.swing(hand);
        } else if (distanceSq >= minThrowDist * minThrowDist
                && distanceSq <= MAX_THROW_DISTANCE_SQ
                && isTargetInFront(mob, target)) {
            performThrow(mob, throwableStack, hand);
            mob.swing(hand);
        }

        state.lastThrowTime = tick;
        return true;
    }

    public static void removeState(UUID uuid) {
        STATES.remove(uuid);
    }

    private static void performThrow(Mob mob, ItemStack throwableStack, InteractionHand hand) {
        Level level = mob.level();
        if (level.isClientSide()) return;

        String projectileType = resolveProjectileType(throwableStack);
        if (projectileType == null || projectileType.isEmpty()) return;

        float speed = getThrowSpeed(throwableStack);
        float inaccuracy = getThrowInaccuracy(throwableStack);

        net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity projectile =
                createProjectileEntity(projectileType, level, mob, throwableStack.copy());
        if (projectile == null) return;

        projectile.pickup = net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity.Pickup.CREATIVE_ONLY;

        LivingEntity target = mob.getTarget();
        Vec3 aimDir = calculateAimDirection(mob, target);
        if (aimDir != null) {
            projectile.shoot(aimDir.x, aimDir.y, aimDir.z, speed, inaccuracy);
        } else {
            projectile.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0F, speed, inaccuracy);
        }
        projectile.setWeaponItem(throwableStack.copy());
        level.addFreshEntity(projectile);

        SoundEvent throwSound = getThrowSound(throwableStack);
        level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), throwSound, SoundSource.HOSTILE, 1.0F, 1.0F);

        double critChance = MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_CRITICAL_CHANCE.get();
        if (critChance > 0.0 && mob.getRandom().nextDouble() < critChance) {
            projectile.setCritical(true);
        }

        if (projectile instanceof net.jaams.weaponry.component.projectile.BaseReturningProjectileEntity returning) {
            double minRange = ModProjectiles.getThrowbackMinRange(throwableStack,
                    ThrowableTypeData.getThrowbackMinRangeDefault(projectileType));
            float maxRange = ModProjectiles.getThrowbackMaxRange(throwableStack,
                    ThrowableTypeData.getThrowbackMaxRangeDefault(projectileType));
            double returnSpeed = ModProjectiles.getThrowbackReturnSpeed(throwableStack,
                    ThrowableTypeData.getThrowbackReturnSpeedDefault(projectileType));
            float finalRange = (float) Math.min(minRange + 20.0, maxRange);
            returning.setWeaponRange(finalRange);
            returning.setReturnSpeed(returnSpeed);
        }

        if ("DYNAMITE".equals(projectileType)) {
            Vec3 lookVec = mob.getLookAngle().normalize();
            double recoilStrength = 0.8;
            mob.setDeltaMovement(
                    mob.getDeltaMovement().x - lookVec.x * recoilStrength,
                    mob.getDeltaMovement().y - lookVec.y * recoilStrength * 0.3,
                    mob.getDeltaMovement().z - lookVec.z * recoilStrength);
            mob.hurtMarked = true;
        }

        throwableStack.shrink(1);
        if (throwableStack.isEmpty()) mob.setItemInHand(hand, ItemStack.EMPTY);
    }

    private static String resolveProjectileType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableProjectileType")) {
            String nbtType = tag.getString("ThrowableProjectileType");
            if (nbtType != null && !nbtType.isEmpty()) return nbtType;
        }
        java.util.Optional<ThrowableItemData> optData = ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
        if (optData.isPresent()) {
            String jsonProjectile = optData.get().throwable.projectile;
            if (jsonProjectile != null && !jsonProjectile.isEmpty()) return jsonProjectile;
        }
        ThrowableTypeData legacy = ThrowableTypeData.getType(stack);
        return legacy != null ? legacy.name : null;
    }

    private static net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity createProjectileEntity(
            String type, Level level, LivingEntity shooter, ItemStack projectileStack) {
        if (type == null || type.isEmpty()) return null;
        try {
            String upperType = type.toUpperCase(java.util.Locale.ROOT).trim();
            return switch (upperType) {
                case "AXE" -> new net.jaams.weaponry.entity.AxeProjectileEntity(level, shooter, projectileStack);
                case "CLEAVER" -> new net.jaams.weaponry.entity.CleaverProjectileEntity(level, shooter, projectileStack);
                case "ROYAL_AXE" -> new net.jaams.weaponry.entity.RoyalAxeProjectileEntity(level, shooter, projectileStack);
                case "ROYAL_SPEAR" -> new net.jaams.weaponry.entity.RoyalSpearProjectileEntity(level, shooter, projectileStack);
                case "GIANT_SHURIKEN" -> new net.jaams.weaponry.entity.GiantShurikenProjectileEntity(level, shooter, projectileStack);
                case "SHURIKEN" -> new net.jaams.weaponry.entity.ShurikenProjectileEntity(level, shooter, projectileStack);
                case "KUNAI" -> new net.jaams.weaponry.entity.KunaiProjectileEntity(level, shooter, projectileStack);
                case "PRONGED_KUNAI" -> new net.jaams.weaponry.entity.ProngedKunaiProjectileEntity(level, shooter, projectileStack);
                case "SHARP_STONE" -> new net.jaams.weaponry.entity.SharpStoneProjectileEntity(level, shooter, projectileStack);
                case "SPEAR" -> new net.jaams.weaponry.entity.SpearProjectileEntity(level, shooter, projectileStack);
                case "TRIDENT" -> new net.jaams.weaponry.entity.TridentProjectileEntity(level, shooter, projectileStack);
                case "HUNTERS_BOOMERANG", "BOOMERANG" -> new net.jaams.weaponry.entity.HuntersBoomerangProjectileEntity(level, shooter, projectileStack);
                case "RING" -> new net.jaams.weaponry.entity.RingProjectileEntity(level, shooter, projectileStack);
                case "BROOM" -> new net.jaams.weaponry.entity.BroomProjectileEntity(level, shooter, projectileStack);
                case "DYNAMITE" -> new net.jaams.weaponry.entity.DynamiteProjectileEntity(level, shooter, projectileStack);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static SoundEvent getThrowSound(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableThrowSound", net.minecraft.nbt.Tag.TAG_STRING)) {
            try {
                ResourceLocation resLoc = ResourceLocation.tryParse(tag.getString("ThrowableThrowSound"));
                if (resLoc != null) {
                    SoundEvent customSound = ForgeRegistries.SOUND_EVENTS.getValue(resLoc);
                    if (customSound != null) return customSound;
                }
            } catch (Exception ignored) {
            }
        }
        java.util.Optional<ThrowableItemData> optData = ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
        if (optData.isPresent()) {
            ThrowableItemData.ThrowableEntry entry = optData.get().throwable;
            if (entry.throw_sound != null && !entry.throw_sound.isEmpty()) {
                try {
                    ResourceLocation loc = ResourceLocation.tryParse(entry.throw_sound);
                    if (loc != null) {
                        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(loc);
                        if (sound != null) return sound;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        ThrowableTypeData legacy = ThrowableTypeData.getType(stack);
        if (legacy != null) {
            SoundEvent legacySound = ThrowableTypeData.getShootSound(legacy.name);
            if (legacySound != null) return legacySound;
        }
        return ModSounds.PROJECTILE_THROW.get();
    }

    private static float getThrowSpeed(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableProjectileSpeed", net.minecraft.nbt.Tag.TAG_FLOAT))
            return tag.getFloat("ThrowableProjectileSpeed");
        java.util.Optional<ThrowableItemData> optData = ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
        if (optData.isPresent()) {
            ThrowableItemData.ThrowableEntry entry = optData.get().throwable;
            if (entry.max_speed != null) return entry.max_speed;
            if (entry.min_speed != null) return entry.min_speed;
        }
        ThrowableTypeData legacy = ThrowableTypeData.getType(stack);
        if (legacy != null) return ThrowableTypeData.getMaxSpeed(legacy.name);
        return 1.5f;
    }

    private static float getThrowInaccuracy(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableProjectileInaccuracy", net.minecraft.nbt.Tag.TAG_FLOAT))
            return (float) (tag.getFloat("ThrowableProjectileInaccuracy")
                    * MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER.get());
        java.util.Optional<ThrowableItemData> optData = ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
        if (optData.isPresent()) {
            ThrowableItemData.ThrowableEntry entry = optData.get().throwable;
            if (entry.inaccuracy != null)
                return (float) (entry.inaccuracy * MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER.get());
        }
        ThrowableTypeData legacy = ThrowableTypeData.getType(stack);
        if (legacy != null)
            return (float) (ThrowableTypeData.getInaccuracy(legacy.name)
                    * MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER.get());
        return (float) (1.0f * MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER.get());
    }

    private static Vec3 calculateAimDirection(Mob mob, LivingEntity target) {
        if (target == null) return null;
        return target.getBoundingBox().getCenter().subtract(mob.getEyePosition(1.0F)).normalize();
    }

    private static boolean hasClearShot(Mob mob, LivingEntity target) {
        Vec3 start = mob.getEyePosition(1.0F);
        Vec3 end = target.getBoundingBox().getCenter();
        ClipContext context = new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob);
        return mob.level().clip(context).getType() == HitResult.Type.MISS;
    }

    private static boolean isTargetInFront(Mob mob, LivingEntity target) {
        Vec3 lookVec = mob.getLookAngle().normalize();
        Vec3 toTarget = new Vec3(
                target.getX() - mob.getX(),
                target.getEyeY() - mob.getEyeY(),
                target.getZ() - mob.getZ()).normalize();
        return lookVec.dot(toTarget) >= FOV_DOT_PRODUCT;
    }

    private static boolean hasThrowableInEitherHand(Mob mob) {
        return hasThrowableInHand(mob, InteractionHand.MAIN_HAND)
                || hasThrowableInHand(mob, InteractionHand.OFF_HAND);
    }

    private static InteractionHand getThrowableHand(Mob mob) {
        if (hasThrowableInHand(mob, InteractionHand.MAIN_HAND)) return InteractionHand.MAIN_HAND;
        if (hasThrowableInHand(mob, InteractionHand.OFF_HAND)) return InteractionHand.OFF_HAND;
        return null;
    }

    private static boolean hasThrowableInHand(Mob mob, InteractionHand hand) {
        ItemStack stack = mob.getItemInHand(hand);
        return !stack.isEmpty() && isThrowable(stack);
    }

    private static boolean isThrowable(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableTrait")) {
            if (!tag.getBoolean("ThrowableTrait")) return false;
            return true;
        }
        java.util.Optional<ThrowableItemData> optData = ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
        if (optData.isPresent()) {
            ThrowableItemData.ThrowableEntry entry = optData.get().throwable;
            return entry.throw_enabled != null && entry.throw_enabled;
        }
        ThrowableTypeData legacy = ThrowableTypeData.getType(stack);
        return legacy != null && ThrowableTypeData.isEnabled(legacy.name);
    }

    private static double getCooldownMultiplier(Mob mob) {
        Difficulty diff = mob.level().getDifficulty();
        if (diff == Difficulty.HARD) return MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER.get();
        if (diff == Difficulty.NORMAL) return MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER.get();
        return MobBehaviorConfig.THROWABLE_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER.get();
    }

    private static class State {
        long lastThrowTime;
        long targetAcquiredTime;
    }
}
