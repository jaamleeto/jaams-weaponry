package net.jaams.weaponry.mixins.custom;

import net.jaams.weaponry.util.ModComponents;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import org.joml.Vector3f;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;

import java.util.List;
import java.util.Comparator;

@Mixin(ItemStack.class)
public abstract class CustomSmokeBombMixin {
    @Unique
    private boolean shouldApplyCustomSmokeBombLogic() {
        ItemStack stack = (ItemStack) (Object) this;
        if (!ItemFeaturesConfig.SMOKE_BOMB_MECHANIC.get())
            return false;
        if (!ModCompats.isSmokeBomb(stack))
            return false;
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("SmokeBomb")) {
            return ModComponents.get(stack).getBoolean("SmokeBomb");
        }
        return true;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaam$onSmokeBombUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!shouldApplyCustomSmokeBombLogic())
            return;
        ItemStack stack = (ItemStack) (Object) this;
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaam$modifySmokeBombUseDuration(net.minecraft.world.entity.LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (!shouldApplyCustomSmokeBombLogic())
            return;
        cir.setReturnValue(72000);
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaam$changeSmokeBombUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        if (!shouldApplyCustomSmokeBombLogic())
            return;
        cir.setReturnValue(UseAnim.SPEAR);
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaam$onReleaseSmokeBomb(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;
        if (!shouldApplyCustomSmokeBombLogic())
            return;
        ItemStack stack = (ItemStack) (Object) this;
        if (!level.isClientSide()) {
            applySmokeBombEffects(stack, player, level, timeLeft);
        }
        ci.cancel();
    }

    @Unique
    private void applySmokeBombEffects(ItemStack stack, LivingEntity sourceEntity, Level level, int timeLeft) {
        applyStatusEffects(stack, sourceEntity, level);
        spawnParticlesAndSound(stack, sourceEntity, level, timeLeft);
        applyPushAndCooldown(stack, sourceEntity);
    }

    @Unique
    private void applyStatusEffects(ItemStack stack, LivingEntity sourceEntity, Level level) {
        double selfProb = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSelfEffectProbability",
                ItemFeaturesConfig.SMOKE_BOMB_SELF_BLIND_PROBABILITY::get);
        if (sourceEntity instanceof Player player && level.random.nextFloat() < selfProb) {
            applyEffectToEntity(player, stack, "Self");
        }
        double enemyProb = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombEnemyEffectProbability",
                ItemFeaturesConfig.SMOKE_BOMB_ENEMY_BLIND_PROBABILITY::get);
        Vec3 center = sourceEntity.position();
        List<Entity> nearby = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(2.0)).stream()
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
        for (Entity ent : nearby) {
            if (ent == sourceEntity || !(ent instanceof LivingEntity living))
                continue;
            if (level.random.nextFloat() < enemyProb) {
                applyEffectToEntity(living, stack, "Enemy");
            }
        }
    }

    @Unique
    private void applyEffectToEntity(LivingEntity entity, ItemStack stack, String targetType) {
        String effectId = ModUtils.getConfigOrNbtString(stack, "SmokeBomb" + targetType + "Effect",
                () -> "minecraft:blindness");
        int duration = ModUtils.getConfigOrNbtInt(stack, "SmokeBomb" + targetType + "Duration", () -> 40);
        int amplifier = ModUtils.getConfigOrNbtInt(stack, "SmokeBomb" + targetType + "Amplifier", () -> 1);
        net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId)).map(h -> (net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>) h).orElse(null);
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
        }
    }

    @Unique
    private void spawnParticlesAndSound(ItemStack stack, LivingEntity sourceEntity, Level level, int timeLeft) {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        int particleCount = ModUtils.getConfigOrNbtInt(stack, "SmokeBombParticleCount",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_COUNT::get);
        double particleRange = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombParticleRange",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_RANGE::get);
        ParticleOptions particleOption = getParticleFromNBT(stack, sourceEntity);
        int count = sourceEntity.isUnderWater() ? particleCount * 2 : particleCount;
        double range = sourceEntity.isUnderWater() ? particleRange * 0.75 : particleRange;
        generateParticles(serverLevel, sourceEntity, particleOption, count, range);
        if (!sourceEntity.isUnderWater()) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, sourceEntity.getX(), sourceEntity.getY() + 0.1,
                    sourceEntity.getZ(), 10, 0.5, 0.5, 0.5, 0.0);
        }
        if (sourceEntity.isUnderWater()) {
            generateParticles(serverLevel, sourceEntity, ParticleTypes.SMOKE, particleCount / 2, particleRange * 0.75);
        }
        playSmokeBombSound(stack, sourceEntity, timeLeft);
    }

    @Unique
    private void playSmokeBombSound(ItemStack stack, LivingEntity entity, int timeLeft) {
        String soundId = ModUtils.getConfigOrNbtString(stack, "SmokeBombSound", () -> "jaams_weaponry:smoke_bomb");
        float baseVolume = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundVolume", () -> 1.0);
        float basePitch = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundPitch", () -> 1.0);
        int chargeTime = 72000 - timeLeft;
        float strength = Math.min(chargeTime / 25f, 1.0f);
        float volume = baseVolume * (0.8f + strength * 0.4f);
        float pitch = basePitch * (0.9f + strength * 0.3f);
        volume += (entity.level().random.nextFloat() - 0.5f) * 0.15f;
        pitch += (entity.level().random.nextFloat() - 0.5f) * 0.2f;
        volume = Mth.clamp(volume, 0.1f, 2.0f);
        pitch = Mth.clamp(pitch, 0.5f, 2.0f);
        ModUtils.playSound(entity, soundId, SoundSource.PLAYERS, volume, pitch);
    }

    @Unique
    private ParticleOptions getParticleFromNBT(ItemStack stack, LivingEntity sourceEntity) {
        String particleId = ModUtils.getConfigOrNbtString(stack, "SmokeBombParticle", () -> "minecraft:large_smoke");
        if (particleId.toLowerCase().contains("dust")) {
            int color = ModUtils.getConfigOrNbtInt(stack, "SmokeBombDustColor", () -> 0x333333);
            float scale = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombDustScale", () -> 1.0);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            return new DustParticleOptions(new Vector3f(r, g, b), scale);
        }
        if (sourceEntity.isUnderWater())
            return ParticleTypes.BUBBLE;
        ResourceLocation loc = ResourceLocation.parse(particleId);
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(loc);
        if (type instanceof SimpleParticleType simpleType) {
            return simpleType;
        }
        return ParticleTypes.LARGE_SMOKE;
    }

    @Unique
    private void applyPushAndCooldown(ItemStack stack, LivingEntity sourceEntity) {
        double pushForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_PUSH_FORCE::get);
        double upwardForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombUpwardPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_UPWARD_PUSH_FORCE::get);
        if (ItemFeaturesConfig.SMOKE_BOMB_RESPECT_KNOCKBACK_RESISTANCE.get()) {
            double resistance = sourceEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            pushForce *= (1.0 - resistance);
            upwardForce *= (1.0 - resistance);
        }
        Vec3 look = sourceEntity.getLookAngle().normalize();
        double px = -look.x * pushForce;
        double pz = -look.z * pushForce;
        double py = look.y <= -0.8 ? upwardForce : 0.0;
        sourceEntity.setDeltaMovement(px, py, pz);
        sourceEntity.hurtMarked = true;
        if (sourceEntity instanceof Player player) {
            int cooldown = ModUtils.getConfigOrNbtInt(stack, "SmokeBombCooldown",
                    ItemFeaturesConfig.SMOKE_BOMB_COOLDOWN::get);
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
            if (!player.isCreative()) {
                if (ItemFeaturesConfig.SMOKE_BOMB_USE_DURABILITY.get() && stack.getMaxDamage() > 0) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                } else {
                    stack.shrink(1);
                }
            }
        }
    }

    @Unique
    private static void generateParticles(ServerLevel level, Entity sourceEntity, ParticleOptions particleType,
            int count, double range) {
        double x = sourceEntity.getX();
        double y = sourceEntity.getY() + sourceEntity.getEyeHeight();
        double z = sourceEntity.getZ();
        RandomSource random = level.random;
        for (int i = 0; i < count; i++) {
            double ox = (random.nextDouble() - 0.5) * range;
            double oy = (random.nextDouble() - 0.5) * range;
            double oz = (random.nextDouble() - 0.5) * range;
            level.sendParticles(particleType, x + ox, y + oy, z + oz, 1, 0, 0, 0, 0);
        }
    }
}
