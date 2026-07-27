package net.jaams.weaponry.handler.behavior.item;

import org.joml.Vector3f;

import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobSmokeBombUseBehaviorHandler {

    private static final double MAX_USE_DISTANCE_SQ = 25.0 * 25.0;
    private static final double MELEE_DISTANCE = 2.5;
    private static final double FOV_DOT_PRODUCT = 0.5;

    private static final Map<UUID, State> STATES = new HashMap<>();

    public static boolean tryExecute(Mob mob, long tick) {
        if (!MobBehaviorConfig.SMOKE_BOMB_MOBS_USE.get()) return false;
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
        if (mob.distanceToSqr(target) > MAX_USE_DISTANCE_SQ) return false;

        if (!mob.getSensing().hasLineOfSight(target)) return false;

        if (!hasSmokeBombInEitherHand(mob)) return false;

        if (state.targetAcquiredTime == 0) state.targetAcquiredTime = tick;

        long timeSinceAcquired = tick - state.targetAcquiredTime;
        int initialCooldownMin = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS.get();
        int initialCooldownMax = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS.get();
        if (initialCooldownMax < initialCooldownMin) initialCooldownMax = initialCooldownMin;
        long initialCooldown = initialCooldownMin
                + mob.level().getRandom().nextInt(Math.max(1, initialCooldownMax - initialCooldownMin + 1));
        if (timeSinceAcquired < initialCooldown) return false;

        int cooldownMin = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS.get();
        int cooldownMax = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS.get();
        if (cooldownMax < cooldownMin) cooldownMax = cooldownMin;
        int baseCooldown = cooldownMin + mob.level().getRandom().nextInt(Math.max(1, cooldownMax - cooldownMin + 1));
        long totalCooldown = (long) (baseCooldown * getCooldownMultiplier(mob));

        if (tick - state.lastUseTime < totalCooldown) return false;

        double useProbability = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_USE_PROBABILITY.get();
        if (useProbability < 1.0 && mob.getRandom().nextDouble() > useProbability) return false;

        InteractionHand hand = getSmokeBombHand(mob);
        if (hand == null) return false;

        ItemStack smokeBombStack = mob.getItemInHand(hand);
        if (smokeBombStack.isEmpty() || !ModCompats.isSmokeBomb(smokeBombStack)) return false;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceSq = mob.distanceToSqr(target);
        double meleeDist = MELEE_DISTANCE;

        if (distanceSq <= meleeDist * meleeDist) {
            mob.doHurtTarget(target);
            mob.swing(hand);
        } else {
            if (!isTargetInFront(mob, target)) return false;
            useSmokeBomb(mob, smokeBombStack, hand);
            mob.swing(hand);
        }

        state.lastUseTime = tick;
        return true;
    }

    public static void removeState(UUID uuid) {
        STATES.remove(uuid);
    }

    private static void useSmokeBomb(Mob mob, ItemStack stack, InteractionHand hand) {
        Level level = mob.level();
        if (level.isClientSide()) return;

        applyStatusEffects(mob, stack, level);
        spawnParticlesAndSound(mob, stack, level);
        applyPush(mob, stack);
        consumeItem(mob, stack, hand);
    }

    private static void applyStatusEffects(Mob mob, ItemStack stack, Level level) {
        double radius = MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_EFFECT_RADIUS.get();
        Vec3 center = mob.position();
        AABB area = new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);

        double enemyProb = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombEnemyEffectProbability",
                ItemFeaturesConfig.SMOKE_BOMB_ENEMY_BLIND_PROBABILITY::get);

        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
            if (entity == mob) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (level.random.nextFloat() < enemyProb) {
                applyEffectToEntity(living, stack, "Enemy");
            } else if (living instanceof Mob m && m.getTarget() != null) {
                m.setTarget(null);
            }
        }
    }

    private static void applyEffectToEntity(LivingEntity entity, ItemStack stack, String targetType) {
        String effectId = ModUtils.getConfigOrNbtString(stack, "SmokeBomb" + targetType + "Effect",
                () -> "minecraft:blindness");
        int duration = ModUtils.getConfigOrNbtInt(stack, "SmokeBomb" + targetType + "Duration", () -> 40);
        int amplifier = ModUtils.getConfigOrNbtInt(stack, "SmokeBomb" + targetType + "Amplifier", () -> 1);
        BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId)).ifPresent(
                effect -> entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true)));
    }

    private static void spawnParticlesAndSound(Mob mob, ItemStack stack, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int particleCount = ModUtils.getConfigOrNbtInt(stack, "SmokeBombParticleCount",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_COUNT::get);
        double particleRange = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombParticleRange",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_RANGE::get);
        ParticleOptions particleOption = getParticleFromStack(stack);

        double x = mob.getX();
        double y = mob.getY() + mob.getEyeHeight();
        double z = mob.getZ();

        for (int i = 0; i < particleCount; i++) {
            double ox = (level.random.nextDouble() - 0.5) * particleRange;
            double oy = (level.random.nextDouble() - 0.5) * particleRange;
            double oz = (level.random.nextDouble() - 0.5) * particleRange;
            serverLevel.sendParticles(particleOption, x + ox, y + oy, z + oz, 1, 0, 0, 0, 0);
        }
        if (!mob.isUnderWater()) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, x, y - mob.getEyeHeight() + 0.5, z, 12, 0.5, 0.5, 0.5, 0.0);
        }

        String soundId = ModUtils.getConfigOrNbtString(stack, "SmokeBombSound", () -> "jaams_weaponry:smoke_bomb");
        float vol = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundVolume", () -> 1.0);
        float pitch = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundPitch", () -> 1.0);
        vol = Mth.clamp(vol * (0.85f + level.random.nextFloat() * 0.3f), 0.1f, 2.0f);
        pitch = Mth.clamp(pitch * (0.85f + level.random.nextFloat() * 0.4f), 0.5f, 2.0f);
        ResourceLocation soundLoc = ResourceLocation.tryParse(soundId);
        if (soundLoc == null) soundLoc = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "smoke_bomb");
        net.minecraft.sounds.SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundLoc);
        if (soundEvent != null) {
            serverLevel.playSound(null, x, y, z, soundEvent, SoundSource.HOSTILE, vol, pitch);
        }
    }

    private static void applyPush(Mob mob, ItemStack stack) {
        double pushForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_PUSH_FORCE::get);
        double upwardForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombUpwardPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_UPWARD_PUSH_FORCE::get);
        if (ItemFeaturesConfig.SMOKE_BOMB_RESPECT_KNOCKBACK_RESISTANCE.get()) {
            double resistance = mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            pushForce *= (1.0 - resistance);
            upwardForce *= (1.0 - resistance);
        }
        Vec3 look = mob.getLookAngle().normalize();
        double px = -look.x * pushForce;
        double pz = -look.z * pushForce;
        double py = look.y <= -0.8 ? upwardForce : 0.0;
        mob.setDeltaMovement(px, py, pz);
        mob.hurtMarked = true;
    }

    private static void consumeItem(Mob mob, ItemStack stack, InteractionHand hand) {
        if (ItemFeaturesConfig.SMOKE_BOMB_USE_DURABILITY.get() && stack.getMaxDamage() > 0) {
            stack.setDamageValue(stack.getDamageValue() + 1);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                mob.setItemInHand(hand, ItemStack.EMPTY);
            }
        } else {
            stack.shrink(1);
            if (stack.isEmpty()) mob.setItemInHand(hand, ItemStack.EMPTY);
        }
    }

    private static ParticleOptions getParticleFromStack(ItemStack stack) {
        String particleId = ModUtils.getConfigOrNbtString(stack, "SmokeBombParticle", () -> "minecraft:large_smoke");
        if (particleId.toLowerCase().contains("dust")) {
            int color = ModUtils.getConfigOrNbtInt(stack, "SmokeBombDustColor", () -> 0x333333);
            float scale = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombDustScale", () -> 1.0);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            return new DustParticleOptions(new Vector3f(r, g, b), scale);
        }
        ResourceLocation loc = ResourceLocation.parse(particleId);
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(loc);
        if (type instanceof SimpleParticleType simpleType) return simpleType;
        return ParticleTypes.LARGE_SMOKE;
    }

    private static boolean hasSmokeBombInEitherHand(Mob mob) {
        return getSmokeBombHand(mob) != null;
    }

    private static InteractionHand getSmokeBombHand(Mob mob) {
        if (isSmokeBomb(mob.getItemInHand(InteractionHand.MAIN_HAND))) return InteractionHand.MAIN_HAND;
        if (isSmokeBomb(mob.getItemInHand(InteractionHand.OFF_HAND))) return InteractionHand.OFF_HAND;
        return null;
    }

    private static boolean isSmokeBomb(ItemStack stack) {
        return !stack.isEmpty() && ModCompats.isSmokeBomb(stack);
    }

    private static boolean isTargetInFront(Mob mob, LivingEntity target) {
        Vec3 lookVec = mob.getLookAngle().normalize();
        Vec3 toTarget = new Vec3(
                target.getX() - mob.getX(),
                target.getEyeY() - mob.getEyeY(),
                target.getZ() - mob.getZ()).normalize();
        return lookVec.dot(toTarget) >= FOV_DOT_PRODUCT;
    }

    private static double getCooldownMultiplier(Mob mob) {
        Difficulty diff = mob.level().getDifficulty();
        if (diff == Difficulty.HARD) return MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER.get();
        if (diff == Difficulty.NORMAL) return MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER.get();
        return MobBehaviorConfig.SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER.get();
    }

    private static class State {
        long lastUseTime;
        long targetAcquiredTime;
    }
}
