package net.jaams.weaponry.handler.gun;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.particle.CustomExplosionParticleData;
import net.jaams.weaponry.particle.GunSparkParticleData;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

public class GunActionsHandler {

    public static void handleGunShot(LevelAccessor level, Entity entity, ItemStack itemstack, float baseParticleSize,
            float particleDistance) {
        if (entity == null || itemstack == null || level.isClientSide() || !(entity instanceof LivingEntity living)
                || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean isMainHand = ItemStack.isSameItem(itemstack, living.getMainHandItem());
        living.swing(isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
        Vec3 lookVec = living.getLookAngle().normalize();
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        float offset = isMainHand ? 0.15f : -0.15f;
        AABB hitbox = living.getBoundingBox();
        float hitboxScale = (float) Math.sqrt((hitbox.getXsize() * hitbox.getZsize()) / 2.0);
        float adjustedParticleSize = baseParticleSize * hitboxScale;
        double startX = living.getX() + rightVec.x * offset;
        double startY = living.getY() + living.getEyeHeight();
        double startZ = living.getZ() + rightVec.z * offset;
        Vec3 startPos = new Vec3(startX, startY, startZ);
        Vec3 endPos = startPos.add(lookVec.scale(particleDistance));
        ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                living);
        BlockHitResult blockHit = level.clip(clipContext);
        EntityHitResult entityHit = ModUtils.getEntityHitResult(serverLevel, living, startPos, endPos);
        double adjustedDistance = particleDistance;
        if (blockHit.getType() != HitResult.Type.MISS) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
        }
        if (entityHit != null) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
        }
        adjustedDistance = Math.max(adjustedDistance - 0.1f, 0.1f);
        double particleX = startX + lookVec.x * adjustedDistance;
        double particleY = startY + lookVec.y * adjustedDistance;
        double particleZ = startZ + lookVec.z * adjustedDistance;
        int backblastLevel = ModEnchantments.level(itemstack, ModEnchantments.BACKBLAST);
        GunItemData.ParticleEntry particleData = GunItemData.getData(itemstack)
                .map((d) -> d.particle)
                .orElse(null);
        String shotParticleId = getFinalParticle(itemstack, "GunShotParticle",
                particleData != null ? particleData.shot_particle : "");
        double finalShotSize = getFinalDouble(itemstack, "GunShotSize",
                particleData != null ? particleData.shot_size : -1.0, baseParticleSize);
        double finalShotDistance = getFinalDouble(itemstack, "GunShotDistance",
                particleData != null ? particleData.shot_distance : -1.0, particleDistance);
        int particleCount = getFinalInt(itemstack, "GunShotParticleCount",
                particleData != null ? particleData.particle_count : -1, 1);
        float finalParticleSize = (float) (adjustedParticleSize * (finalShotSize / baseParticleSize));
        if (finalShotDistance != particleDistance) {
            adjustedDistance = Math.max(finalShotDistance - 0.1f, 0.1f);
            particleX = startX + lookVec.x * adjustedDistance;
            particleY = startY + lookVec.y * adjustedDistance;
            particleZ = startZ + lookVec.z * adjustedDistance;
        }
        if (backblastLevel > 0) {
            float colorBase = serverLevel.random.nextFloat() * 0.5f + 0.5f;
            serverLevel.sendParticles(
                    new CustomExplosionParticleData(colorBase, colorBase, colorBase, finalParticleSize), particleX,
                    particleY, particleZ, 1, 0.0, 0.0, 0.0, 0.0);
        } else if (!shotParticleId.isEmpty()) {
            ParticleOptions particleOption = getParticleFromString(shotParticleId, finalParticleSize);
            for (int i = 0; i < particleCount; i++) {
                double spreadX = (serverLevel.random.nextDouble() - 0.5) * 0.2;
                double spreadY = (serverLevel.random.nextDouble() - 0.5) * 0.2;
                double spreadZ = (serverLevel.random.nextDouble() - 0.5) * 0.2;
                serverLevel.sendParticles(particleOption, particleX + spreadX, particleY + spreadY, particleZ + spreadZ,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        } else {
            serverLevel.sendParticles(new GunSparkParticleData(finalParticleSize), particleX, particleY, particleZ, 1,
                    0.0, 0.0, 0.0, 0.0);
        }
        if (living.isUnderWater()) {
            RandomSource random = serverLevel.random;
            for (int i = 0; i < 6; i++) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE, particleX + (random.nextDouble() - 0.5) * 0.2,
                        particleY + (random.nextDouble() - 0.5) * 0.2, particleZ + (random.nextDouble() - 0.5) * 0.2, 1,
                        0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private static String getFinalParticle(ItemStack gunStack, String nbtKey, String jsonValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            String nbt = ModComponents.get(gunStack).getString(nbtKey);
            if (!nbt.isEmpty())
                return nbt;
        }
        if (jsonValue != null && !jsonValue.isEmpty()) {
            return jsonValue;
        }
        return "";
    }

    private static double getFinalDouble(ItemStack gunStack, String nbtKey, double jsonValue, double defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            return ModComponents.get(gunStack).getDouble(nbtKey);
        }
        if (jsonValue != -1.0)
            return jsonValue;
        return defaultValue;
    }

    private static int getFinalInt(ItemStack gunStack, String nbtKey, int jsonValue, int defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            return ModComponents.get(gunStack).getInt(nbtKey);
        }
        if (jsonValue != -1)
            return jsonValue;
        return defaultValue;
    }

    private static ParticleOptions getParticleFromString(String particleId, float size) {
        try {
            ResourceLocation loc = particleId.contains(":") ? ResourceLocation.parse(particleId)
                    : ResourceLocation.fromNamespaceAndPath("jaams_weaponry", particleId);
            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(loc);
            if (type instanceof SimpleParticleType) {
                return (SimpleParticleType) type;
            }
        } catch (Exception ignored) {
        }
        return new GunSparkParticleData(size);
    }

    public static void applyBackblastRecoil(LivingEntity entity, float recoilDistance, float crouchRecoilReduction,
            float verticalRecoilMultiplier, float backblastBonus, float fireDurationBonus, float baseDamage) {
        if (entity == null || recoilDistance <= 0.0F) {
            return;
        }
        double recoilMultiplier = recoilDistance * (1.0F + backblastBonus);
        if (entity instanceof Player player && !player.isCreative()) {
            if (player.isCrouching()) {
                recoilMultiplier *= crouchRecoilReduction;
                player.getFoodData().addExhaustion(0.2F);
            } else {
                player.getFoodData().addExhaustion(0.5F);
            }
        }
        double knockbackResistance = Math.max(0.0,
                Math.min(1.0, entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 2.0);
        recoilMultiplier *= (1.0 - knockbackReduction * 0.3);
        double movementSpeed = entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double baseMovementSpeed = 0.1;
        double movementSpeedMultiplier = Math.max(0.7, Math.min(1.5, movementSpeed / baseMovementSpeed));
        recoilMultiplier *= movementSpeedMultiplier;
        double horizontalRecoil = Math.max(0.0, 1.2 * recoilMultiplier);
        double verticalRecoil = Math.max(0.0, 1.2 * verticalRecoilMultiplier * recoilMultiplier);
        if (horizontalRecoil < 0.01 && verticalRecoil < 0.01) {
            return;
        }
        float yaw = entity.getYHeadRot() * ((float) Math.PI / 180F);
        float pitch = (entity instanceof Player player) ? player.getXRot() * ((float) Math.PI / 180F) : 0.0F;
        double motionX = Math.sin(yaw) * horizontalRecoil * Math.cos(pitch);
        double motionZ = -Math.cos(yaw) * horizontalRecoil * Math.cos(pitch);
        double motionY = 0.0;
        final float MIN_PITCH_FOR_VERTICAL_DEGREES = 45.0F;
        float minPitchForVertical = (float) Math.toRadians(MIN_PITCH_FOR_VERTICAL_DEGREES);
        if (pitch > minPitchForVertical) {
            float pitchFactor = Math.min(1.0f,
                    (pitch - minPitchForVertical) / ((float) Math.PI / 2 - minPitchForVertical));
            double verticalScale = Math.cos((pitchFactor * Math.PI) / 2);
            motionY = verticalRecoil * (1.0 - verticalScale);
            double horizontalScale = Math.max(0.5, 1.0 - pitchFactor * 0.5);
            motionX *= horizontalScale;
            motionZ *= horizontalScale;
        }
        motionX = Mth.clamp(motionX, -1.5, 1.5);
        motionY = Mth.clamp(motionY, -1.5 * verticalRecoilMultiplier, 1.5 * verticalRecoilMultiplier);
        motionZ = Mth.clamp(motionZ, -1.5, 1.5);

        Vec3 currentMotion = entity.getDeltaMovement();
        double finalMotionY = currentMotion.y + motionY;

        if (motionY > 0.0 && currentMotion.y < 0.0) {
            finalMotionY = (currentMotion.y * 0.3) + motionY;
        }

        entity.setDeltaMovement(currentMotion.x + motionX, finalMotionY, currentMotion.z + motionZ);
        entity.hurtMarked = true;
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
        } else if (!entity.level().isClientSide) {
            entity
                    .level()
                    .getEntities(null, entity.getBoundingBox().inflate(10.0))
                    .stream()
                    .filter((e) -> e instanceof ServerPlayer)
                    .forEach((e) -> ((ServerPlayer) e).connection.send(new ClientboundSetEntityMotionPacket(entity)));
        }
        if (!entity.level().isClientSide) {
            int currentLevel = Math.max(1, (int) (1.0F + backblastBonus));
            if (EnchantmentsConfig.BACKBLAST_EXPLOSION.get()) {
                triggerExplosion(entity, currentLevel);
            }
            if (EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE.get()) {
                triggerFireShockwave(entity, backblastBonus, fireDurationBonus, baseDamage);
            }
        }
    }

    private static void triggerExplosion(LivingEntity entity, int currentLevel) {
        float basePower = EnchantmentsConfig.BACKBLAST_EXPLOSION_POWER.get().floatValue();
        float finalExplosionPower = basePower * currentLevel;
        Level.ExplosionInteraction interaction = EnchantmentsConfig.BACKBLAST_EXPLOSION_BREAKS_BLOCKS.get()
                ? Level.ExplosionInteraction.TNT
                : Level.ExplosionInteraction.NONE;
        entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), finalExplosionPower, false,
                interaction);
    }

    private static void triggerFireShockwave(LivingEntity entity, float backblastBonus, float fireDurationBonus,
            float baseDamage) {
        double maxRadius = EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE_RADIUS.get();
        AABB aoeArea = new AABB(entity.getX() - maxRadius, entity.getY() - maxRadius, entity.getZ() - maxRadius,
                entity.getX() + maxRadius, entity.getY() + maxRadius, entity.getZ() + maxRadius);
        Vec3 sourcePos = entity.position();
        entity
                .level()
                .getEntities(entity, aoeArea)
                .stream()
                .filter((e) -> e instanceof LivingEntity)
                .map((e) -> (LivingEntity) e)
                .forEach((target) -> {
                    DamageSource damageSource = entity.damageSources().inFire();
                    if (target.hurt(damageSource, baseDamage * (1.0F + backblastBonus))) {
                        int baseDurationBonus = EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE_DURATION_BONUS_PER_LEVEL
                                .get();
                        int fireDurationSeconds = Math.max(1,
                                (int) (3.0F * (1.0F + fireDurationBonus)) + baseDurationBonus);
                        target.igniteForSeconds(fireDurationSeconds);
                        target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                                SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        if (entity instanceof Player player && target instanceof Mob mob && !player.isCreative()) {
                            mob.setTarget(player);
                        }
                        Vec3 targetPos = target.position();
                        Vec3 direction = targetPos.subtract(sourcePos);
                        if (direction.lengthSqr() > 0.0) {
                            direction = direction.normalize();
                        } else {
                            direction = new Vec3(0.0, 0.0, 1.0);
                        }
                        double configKnockback = EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE_KNOCKBACK.get();
                        double knockbackStrength = configKnockback * (1.0 + backblastBonus);
                        double knockbackX = Mth.clamp(direction.x * knockbackStrength, -1.5, 1.5);
                        double knockbackY = Mth.clamp(0.2 * (1.0 + backblastBonus), 0.0, 0.8);
                        double knockbackZ = Mth.clamp(direction.z * knockbackStrength, -1.5, 1.5);
                        target.setDeltaMovement(target.getDeltaMovement().add(knockbackX, knockbackY, knockbackZ));
                        target.hurtMarked = true;
                        target.hasImpulse = true;
                        if (target instanceof ServerPlayer serverTarget) {
                            serverTarget.connection.send(new ClientboundSetEntityMotionPacket(serverTarget));
                        }
                    }
                });
    }
}
