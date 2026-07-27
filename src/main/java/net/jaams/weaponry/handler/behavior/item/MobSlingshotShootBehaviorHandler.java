package net.jaams.weaponry.handler.behavior.item;
import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobSlingshotShootBehaviorHandler {

    private static final double MAX_SHOOT_DISTANCE_SQ = 25.0 * 25.0;
    private static final double FOV_DOT_PRODUCT = 0.5;

    private static final Map<UUID, State> STATES = new HashMap<>();

    public static boolean tryExecute(Mob mob, long tick) {
        State state = STATES.get(mob.getUUID());

        if (state != null && state.isCharging) {
            tickCharge(mob, state);
            return true;
        }

        if (!MobBehaviorConfig.SLINGSHOT_MOBS_SHOOT.get()) return false;
        if (ModUtils.hasRestrictedEffect(mob)) return false;

        if (state == null) {
            state = new State();
            STATES.put(mob.getUUID(), state);
        }

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || target == mob) {
            state.targetAcquiredTime = 0;
            return false;
        }
        if (mob.distanceToSqr(target) > MAX_SHOOT_DISTANCE_SQ) return false;

        if (MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_CHECK_CLEAR_SHOT.get()) {
            if (!hasClearShot(mob, target)) return false;
        } else if (!mob.getSensing().hasLineOfSight(target)) {
            return false;
        }

        if (!hasSlingshotInEitherHand(mob)) return false;

        if (state.targetAcquiredTime == 0) state.targetAcquiredTime = tick;

        long timeSinceAcquired = tick - state.targetAcquiredTime;
        int initialCooldownMin = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS.get();
        int initialCooldownMax = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS.get();
        if (initialCooldownMax < initialCooldownMin) initialCooldownMax = initialCooldownMin;
        long initialCooldown = initialCooldownMin
                + mob.level().getRandom().nextInt(Math.max(1, initialCooldownMax - initialCooldownMin + 1));
        if (timeSinceAcquired < initialCooldown) return false;

        int cooldownMin = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS.get();
        int cooldownMax = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS.get();
        if (cooldownMax < cooldownMin) cooldownMax = cooldownMin;
        int baseCooldown = cooldownMin + mob.level().getRandom().nextInt(Math.max(1, cooldownMax - cooldownMin + 1));
        long totalCooldown = (long) (baseCooldown * getCooldownMultiplier(mob));

        if (tick - state.lastShootTime < totalCooldown) return false;

        double throwProbability = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_THROW_PROBABILITY.get();
        if (throwProbability < 1.0 && mob.getRandom().nextDouble() >= throwProbability) return false;

        InteractionHand hand = getSlingshotHand(mob);
        if (hand == null) return false;

        ItemStack slingshotStack = mob.getItemInHand(hand);
        if (slingshotStack.isEmpty()) return false;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceSq = mob.distanceToSqr(target);
        double meleeDist = MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_MELEE_DISTANCE.get();
        if (distanceSq <= meleeDist * meleeDist) {
            mob.doHurtTarget(target);
            mob.swing(hand);
            state.lastShootTime = tick;
            return true;
        }

        state.isCharging = true;
        state.chargingHand = hand;
        state.chargeStartTick = mob.tickCount;
        state.chargeDuration = getMaxDrawDuration(slingshotStack);
        state.loadSoundPlayed = false;
        mob.startUsingItem(hand);
        return true;
    }

    public static void removeState(UUID uuid) {
        STATES.remove(uuid);
    }

    private static void tickCharge(Mob mob, State state) {
        if (mob.level().isClientSide() || !mob.isAlive()) {
            resetCharge(state);
            return;
        }
        if (ModUtils.hasRestrictedEffect(mob)) {
            completeCharge(mob, state);
            return;
        }

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            completeCharge(mob, state);
            return;
        }
        if (mob.distanceToSqr(target) > MAX_SHOOT_DISTANCE_SQ) {
            completeCharge(mob, state);
            return;
        }
        if (!hasSlingshotInHand(mob, state.chargingHand)) {
            completeCharge(mob, state);
            return;
        }

        int elapsed = mob.tickCount - state.chargeStartTick;
        if (elapsed >= state.chargeDuration) {
            completeCharge(mob, state);
            return;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        ItemStack slingshotStack = mob.getItemInHand(state.chargingHand);
        int minDrawTicks = getMinDrawTicks(slingshotStack);

        if (!state.loadSoundPlayed && elapsed >= minDrawTicks) {
            state.loadSoundPlayed = true;
            mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    ModSounds.SLINGSHOT_LOAD.get(), SoundSource.HOSTILE, 0.5F,
                    0.8F / (mob.level().getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    private static void completeCharge(Mob mob, State state) {
        if (!state.isCharging) return;

        LivingEntity target = mob.getTarget();
        ItemStack slingshotStack = mob.getItemInHand(state.chargingHand);
        if (!slingshotStack.isEmpty() && target != null && target.isAlive()
                && mob.distanceToSqr(target) <= MAX_SHOOT_DISTANCE_SQ
                && isTargetInFront(mob, target)) {
            int drawTicks = mob.tickCount - state.chargeStartTick;
            if (drawTicks < getMinDrawTicks(slingshotStack)) {
                drawTicks = getMinDrawTicks(slingshotStack);
            }
            shoot(mob, slingshotStack, state.chargingHand, drawTicks);
            mob.swing(state.chargingHand);
        }

        mob.stopUsingItem();
        state.lastShootTime = mob.level().getGameTime();
        resetCharge(state);
    }

    private static void resetCharge(State state) {
        state.isCharging = false;
        state.chargingHand = null;
        state.loadSoundPlayed = false;
    }

    private static void shoot(Mob mob, ItemStack slingshotStack, InteractionHand hand, int drawTicks) {
        if (mob.level().isClientSide()) return;

        ItemStack ammo = resolveAmmo(mob, slingshotStack, hand);
        if (ammo.isEmpty()) return;

        float power = calcPowerForTime(drawTicks, slingshotStack);

        ItemProjectileEntity projectile = new ItemProjectileEntity(mob.level(), mob, slingshotStack);
        projectile.getPersistentData().putBoolean("SlingshotProjectile", true);
        projectile.getPersistentData().putBoolean("ChargedForCrit", power >= 1.0F);
        projectile.setProjectileItem(ammo.copy());
        projectile.pickup = ItemProjectileEntity.Pickup.CREATIVE_ONLY;

        float damage = getBaseDamage(slingshotStack);
        if (power >= 1.0F) damage += getPowerDamageBonus(slingshotStack);
        projectile.setProjectileDamage(damage);
        projectile.setProjectileKnockback(getBaseKnockback(slingshotStack));

        float speed = calcProjectileSpeed(power, slingshotStack);
        float inaccuracy = getInaccuracy(slingshotStack);

        LivingEntity target = mob.getTarget();
        Vec3 aimDir = calculateAimDirection(mob, target);
        if (aimDir != null) {
            projectile.shoot(aimDir.x, aimDir.y, aimDir.z, speed, inaccuracy);
        } else {
            projectile.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0F, speed, inaccuracy);
        }

        mob.level().addFreshEntity(projectile);
        mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                ModSounds.SLINGSHOT_SHOOT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        if (mob.getItemInHand(otherHand) == ammo) {
            ammo.shrink(1);
            if (ammo.isEmpty()) mob.setItemInHand(otherHand, ItemStack.EMPTY);
        }
    }

    private static float calcPowerForTime(int drawTicks, ItemStack slingshotStack) {
        int max = getMaxDrawDuration(slingshotStack);
        float f = (float) drawTicks / max;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }

    private static float calcProjectileSpeed(float power, ItemStack slingshotStack) {
        float min = getMinSpeed(slingshotStack);
        float max = getMaxSpeed(slingshotStack);
        return min + (max - min) * power * power;
    }

    private static ItemStack resolveAmmo(Mob mob, ItemStack slingshotStack, InteractionHand slingshotHand) {
        InteractionHand otherHand = slingshotHand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack offhandItem = mob.getItemInHand(otherHand);
        if (!offhandItem.isEmpty() && isAmmoItem(offhandItem, slingshotStack)) return offhandItem;
        return getDefaultAmmo(mob, slingshotStack);
    }

    private static ItemStack getDefaultAmmo(Mob mob, ItemStack slingshotStack) {
        String mobType = getMobAmmoOverride(mob);
        if (mobType != null) {
            ItemStack overrideAmmo = parseAmmoItem(mobType);
            if (!overrideAmmo.isEmpty()) return overrideAmmo;
        }
        List<? extends String> ammoItems = ItemFeaturesConfig.SLINGSHOT_AMMO_ITEMS.get();
        if (ammoItems != null && !ammoItems.isEmpty()) {
            for (String id : ammoItems) {
                ItemStack parsed = parseAmmoItem(id);
                if (!parsed.isEmpty()) return parsed;
            }
        }
        String defaultAmmoId = MobBehaviorConfig.SLINGSHOT_DEFAULT_AMMO.get();
        if (defaultAmmoId != null && !defaultAmmoId.isEmpty()) {
            ItemStack parsed = parseAmmoItem(defaultAmmoId);
            if (!parsed.isEmpty()) return parsed;
        }
        return new ItemStack(Items.OAK_PLANKS);
    }

    private static String getMobAmmoOverride(Mob mob) {
        List<? extends String> overrides = MobBehaviorConfig.SLINGSHOT_MOB_AMMO_OVERRIDES.get();
        if (overrides == null) return null;
        ResourceLocation mobKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (mobKey == null) return null;
        String mobId = mobKey.toString();
        for (String override : overrides) {
            String[] parts = override.split("=", 2);
            if (parts.length == 2 && parts[0].trim().equals(mobId)) return parts[1].trim();
        }
        return null;
    }

    private static boolean isAmmoItem(ItemStack stack, ItemStack slingshotStack) {
        if (stack.isEmpty()) return false;
        if (ModComponents.has(slingshotStack) && ModComponents.get(slingshotStack).contains("SlingshotAmmoItems", 9)) {
            var tag = ModComponents.get(slingshotStack).getList("SlingshotAmmoItems", 8);
            if (!tag.isEmpty()) {
                ResourceLocation ammoId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (ammoId == null) return false;
                for (int i = 0; i < tag.size(); i++) {
                    if (tag.getString(i).equals(ammoId.toString())) return true;
                }
                return false;
            }
        }
        List<? extends String> ammoItems = ItemFeaturesConfig.SLINGSHOT_AMMO_ITEMS.get();
        if (ammoItems == null || ammoItems.isEmpty()) return true;
        ResourceLocation ammoId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (ammoId == null) return false;
        return ammoItems.contains(ammoId.toString());
    }

    private static ItemStack parseAmmoItem(String id) {
        if (id == null || id.isEmpty()) return ItemStack.EMPTY;
        ResourceLocation loc = ResourceLocation.tryParse(id.trim());
        if (loc == null) return ItemStack.EMPTY;
        var item = BuiltInRegistries.ITEM.get(loc);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    private static int getMaxDrawDuration(ItemStack s) {
        return ModUtils.getConfigOrNbtInt(s, "SlingshotMaxDrawDuration", () -> 20);
    }

    private static int getMinDrawTicks(ItemStack s) {
        return ModUtils.getConfigOrNbtInt(s, "SlingshotMinDrawTicks", () -> 5);
    }

    private static float getBaseDamage(ItemStack s) {
        return (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotBaseDamage", () -> 1.0);
    }

    private static float getPowerDamageBonus(ItemStack s) {
        return (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotPowerDamageBonus", () -> 2.0);
    }

    private static float getBaseKnockback(ItemStack s) {
        return (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotBaseKnockback", () -> 0.5);
    }

    private static float getMinSpeed(ItemStack s) {
        return (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotMinSpeed", () -> 0.5);
    }

    private static float getMaxSpeed(ItemStack s) {
        return (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotMaxSpeed", () -> 2.5);
    }

    private static float getInaccuracy(ItemStack s) {
        float base = (float) ModUtils.getConfigOrNbtDouble(s, "SlingshotInaccuracy", () -> 1.0);
        return (float) (base * MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_INACCURACY_MULTIPLIER.get());
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

    private static boolean hasSlingshotInEitherHand(Mob mob) {
        return !ModUtils.getItemInEitherHand(mob, stack -> !stack.isEmpty() && ModCompats.isSlingshot(stack)).isEmpty();
    }

    private static InteractionHand getSlingshotHand(Mob mob) {
        if (hasSlingshotInHand(mob, InteractionHand.MAIN_HAND)) return InteractionHand.MAIN_HAND;
        if (hasSlingshotInHand(mob, InteractionHand.OFF_HAND)) return InteractionHand.OFF_HAND;
        return null;
    }

    private static boolean hasSlingshotInHand(Mob mob, InteractionHand hand) {
        ItemStack stack = mob.getItemInHand(hand);
        return !stack.isEmpty() && ModCompats.isSlingshot(stack);
    }

    private static double getCooldownMultiplier(Mob mob) {
        Difficulty diff = mob.level().getDifficulty();
        if (diff == Difficulty.HARD) return MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER.get();
        if (diff == Difficulty.NORMAL) return MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER.get();
        return MobBehaviorConfig.SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER.get();
    }

    private static class State {
        long lastShootTime;
        long targetAcquiredTime;
        boolean isCharging;
        InteractionHand chargingHand;
        int chargeStartTick;
        int chargeDuration;
        boolean loadSoundPlayed;
    }
}
