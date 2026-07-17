package net.jaams.weaponry.handler.behavior.item;

import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.gun.shoot.DefaultShoot;
import net.jaams.weaponry.gun.shoot.PistolShoot;
import net.jaams.weaponry.gun.shoot.ScattergunShoot;
import net.jaams.weaponry.gun.shoot.ShotgunShoot;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobGunShootBehaviorHandler {

    private static final double MAX_SHOOT_DISTANCE_SQ = 25.0 * 25.0;
    private static final double FOV_DOT_PRODUCT = 0.5;

    private static final Map<UUID, State> STATES = new HashMap<>();

    public static boolean tryExecute(Mob mob, long tick) {
        if (!MobBehaviorConfig.GUN_MOBS_SHOOT.get()) return false;
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
        if (mob.distanceToSqr(target) > MAX_SHOOT_DISTANCE_SQ) return false;

        if (MobBehaviorConfig.GUN_MOB_BEHAVIOR_CHECK_CLEAR_SHOT.get()) {
            if (!hasClearShot(mob, target)) return false;
        } else if (!mob.getSensing().hasLineOfSight(target)) {
            return false;
        }

        if (!hasGunInEitherHand(mob)) return false;

        if (state.targetAcquiredTime == 0) state.targetAcquiredTime = tick;

        long timeSinceAcquired = tick - state.targetAcquiredTime;
        int initialCooldownMin = MobBehaviorConfig.GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS.get();
        int initialCooldownMax = MobBehaviorConfig.GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS.get();
        if (initialCooldownMax < initialCooldownMin) initialCooldownMax = initialCooldownMin;
        long initialCooldown = initialCooldownMin
                + mob.level().getRandom().nextInt(Math.max(1, initialCooldownMax - initialCooldownMin + 1));
        if (timeSinceAcquired < initialCooldown) return false;

        long totalCooldown;
        if (MobBehaviorConfig.GUN_MOB_BEHAVIOR_USE_GUN_BASE_COOLDOWN.get()) {
            double gunBaseMultiplier = MobBehaviorConfig.GUN_MOB_BEHAVIOR_GUN_BASE_COOLDOWN_MULTIPLIER.get();
            totalCooldown = (long) (getGunBaseCooldown(mob) * gunBaseMultiplier * getCooldownMultiplier(mob));
        } else {
            int cooldownMin = MobBehaviorConfig.GUN_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS.get();
            int cooldownMax = MobBehaviorConfig.GUN_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS.get();
            if (cooldownMax < cooldownMin) cooldownMax = cooldownMin;
            int baseCooldown = cooldownMin + mob.level().getRandom().nextInt(Math.max(1, cooldownMax - cooldownMin + 1));
            totalCooldown = (long) (baseCooldown * getCooldownMultiplier(mob));
        }

        if (tick - state.lastShootTime < totalCooldown) return false;

        InteractionHand hand = getGunHand(mob);
        if (hand == null) return false;

        ItemStack gunStack = mob.getItemInHand(hand);
        if (gunStack.isEmpty() || ModGuns.getGunType(gunStack) == null) return false;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceSq = mob.distanceToSqr(target);
        double meleeDist = MobBehaviorConfig.GUN_MOB_BEHAVIOR_MELEE_DISTANCE.get();
        double minShootDist = MobBehaviorConfig.GUN_MOB_BEHAVIOR_MIN_SHOOT_DISTANCE.get();

        if (distanceSq <= meleeDist * meleeDist) {
            mob.doHurtTarget(target);
            mob.swing(hand);
        } else if (distanceSq >= minShootDist * minShootDist
                && distanceSq <= MAX_SHOOT_DISTANCE_SQ
                && isTargetInFront(mob, target)) {
            shoot(mob, gunStack, hand);
            mob.swing(hand);
        }

        state.lastShootTime = tick;
        return true;
    }

    public static void removeState(UUID uuid) {
        STATES.remove(uuid);
    }

    private static void shoot(Mob mob, ItemStack gunStack, InteractionHand hand) {
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        if (type == null) return;
        switch (type) {
            case PISTOL -> PistolShoot.shoot(mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob, gunStack);
            case SCATTERGUN -> ScattergunShoot.shoot(mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob, gunStack);
            case SHOTGUN -> ShotgunShoot.shoot(mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob, gunStack);
            default -> DefaultShoot.shoot(mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob, gunStack);
        }
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

    private static boolean hasGunInEitherHand(Mob mob) {
        return !ModUtils.getItemInEitherHand(mob, stack -> !stack.isEmpty() && ModGuns.isGun(stack)).isEmpty();
    }

    private static InteractionHand getGunHand(Mob mob) {
        if (isGun(mob.getItemInHand(InteractionHand.MAIN_HAND))) return InteractionHand.MAIN_HAND;
        if (isGun(mob.getItemInHand(InteractionHand.OFF_HAND))) return InteractionHand.OFF_HAND;
        return null;
    }

    private static boolean isGun(ItemStack stack) {
        return !stack.isEmpty() && ModGuns.isGun(stack);
    }

    private static double getCooldownMultiplier(Mob mob) {
        Difficulty diff = mob.level().getDifficulty();
        if (diff == Difficulty.HARD) return MobBehaviorConfig.GUN_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER.get();
        if (diff == Difficulty.NORMAL) return MobBehaviorConfig.GUN_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER.get();
        return MobBehaviorConfig.GUN_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER.get();
    }

    private static int getGunBaseCooldown(Mob mob) {
        InteractionHand hand = getGunHand(mob);
        if (hand == null) return GunSystemCommonConfig.GUN_PISTOL_SHOOT_COOLDOWN.get();
        ModGuns.GunType type = ModGuns.getGunType(mob.getItemInHand(hand));
        if (type == null) return GunSystemCommonConfig.GUN_PISTOL_SHOOT_COOLDOWN.get();
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_COOLDOWN.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_COOLDOWN.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_COOLDOWN.get();
            default -> 20;
        };
    }

    private static class State {
        long lastShootTime;
        long targetAcquiredTime;
    }
}
