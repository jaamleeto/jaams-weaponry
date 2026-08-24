package net.jaams.weaponry.dispenser;

import org.joml.Vector3f;

import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class SmokeBombDispenser {
    public static ItemStack dispense(BlockSource source, ItemStack stack) {
        if (!ModCompats.isSmokeBomb(stack)) {
            return new DefaultDispenseItemBehavior().dispense(source, stack);
        }
        Level level = source.getLevel();
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos relativePos = source.getPos().relative(direction);
        Vec3 pos = new Vec3(relativePos.getX() + 0.5, relativePos.getY() + 0.5, relativePos.getZ() + 0.5);
        applySmokeBombEffects(level, pos, direction, stack);
        if (ItemFeaturesConfig.SMOKE_BOMB_USE_DURABILITY.get() && stack.getMaxDamage() > 0) {
            if (stack.hurt(1, level.random, null)) {
                stack.setCount(0);
            }
        } else {
            stack.shrink(1);
        }
        return stack;
    }

    private static void applySmokeBombEffects(Level level, Vec3 pos, Direction direction, ItemStack stack) {
        applyStatusEffects(level, pos, stack);
        spawnParticlesAndSound(level, pos, stack);
        applyPush(level, pos, direction, stack);
    }

    private static void applyStatusEffects(Level level, Vec3 pos, ItemStack stack) {
        AABB area = new AABB(pos.x - 3, pos.y - 3, pos.z - 3, pos.x + 3, pos.y + 3, pos.z + 3);
        double effectProbability = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombEnemyEffectProbability",
                ItemFeaturesConfig.SMOKE_BOMB_ENEMY_BLIND_PROBABILITY::get);
        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
            if (!(entity instanceof LivingEntity living))
                continue;
            if (level.random.nextFloat() < effectProbability) {
                applyEffectToEntity(living, stack);
            } else if (living instanceof Monster monster && monster.getTarget() != null) {
                monster.setTarget(null);
            }
        }
    }

    private static void applyEffectToEntity(LivingEntity living, ItemStack stack) {
        String effectId = ModUtils.getConfigOrNbtString(stack, "SmokeBombEnemyEffect", () -> "minecraft:blindness");
        int duration = ModUtils.getConfigOrNbtInt(stack, "SmokeBombEnemyDuration", () -> 40);
        int amplifier = ModUtils.getConfigOrNbtInt(stack, "SmokeBombEnemyAmplifier", () -> 1);
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(effectId));
        if (effect != null) {
            living.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
        }
    }

    private static void spawnParticlesAndSound(Level level, Vec3 pos, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        int particleCount = ModUtils.getConfigOrNbtInt(stack, "SmokeBombParticleCount",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_COUNT::get);

        ParticleOptions particleOption = getParticleFromStack(stack);
        serverLevel.sendParticles(particleOption, pos.x, pos.y, pos.z, particleCount, 0.6, 0.6, 0.6, 0.0);
        if (particleOption != ParticleTypes.LARGE_SMOKE) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.15, pos.z, 12, 0.5, 0.5, 0.5, 0.0);
        }
        playSmokeBombSound(serverLevel, pos, stack);
    }

    private static void playSmokeBombSound(ServerLevel level, Vec3 pos, ItemStack stack) {
        String soundId = ModUtils.getConfigOrNbtString(stack, "SmokeBombSound", () -> "jaams_weaponry:smoke_bomb");
        float volume = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundVolume", () -> 1.0);
        float pitch = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundPitch", () -> 1.0);
        volume = Mth.clamp(volume * (0.85f + level.random.nextFloat() * 0.3f), 0.1f, 2.0f);
        pitch = Mth.clamp(pitch * (0.85f + level.random.nextFloat() * 0.4f), 0.5f, 2.0f);
        ResourceLocation soundLoc = ResourceLocation.tryParse(soundId);
        if (soundLoc == null)
            soundLoc = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "smoke_bomb");
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLoc);
        if (soundEvent != null) {
            level.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.BLOCKS, volume, pitch);
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
        ParticleType<?> type = ForgeRegistries.PARTICLE_TYPES.getValue(loc);
        if (type instanceof SimpleParticleType simple) {
            return simple;
        }
        return ParticleTypes.LARGE_SMOKE;
    }

    private static void applyPush(Level level, Vec3 pos, Direction direction, ItemStack stack) {
        double pushForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_PUSH_FORCE::get);
        double upwardForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombUpwardPushForce",
                ItemFeaturesConfig.SMOKE_BOMB_UPWARD_PUSH_FORCE::get);
        AABB area = new AABB(pos.x - 2, pos.y - 2, pos.z - 2, pos.x + 2, pos.y + 2, pos.z + 2);
        Vec3 pushDir = Vec3.atLowerCornerOf(direction.getNormal()).normalize();
        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
            if (!(entity instanceof LivingEntity living))
                continue;
            double fx = pushDir.x * pushForce;
            double fz = pushDir.z * pushForce;
            double fy = (pushDir.y <= -0.8) ? upwardForce : 0.0;
            if (ItemFeaturesConfig.SMOKE_BOMB_RESPECT_KNOCKBACK_RESISTANCE.get()) {
                double resistance = living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                fx *= (1.0 - resistance);
                fz *= (1.0 - resistance);
                fy *= (1.0 - resistance);
            }
            entity.setDeltaMovement(fx, fy, fz);
            entity.hurtMarked = true;
        }
    }
}
