package net.jaams.weaponry.handler.trait;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

public class MovementHandler {

    
    public static void pullEnemyTowardsPlayer(LivingEntity entity, LivingEntity sourceEntity,
            float attackStrength, ItemStack itemStack,
            float basePullStrength, float maxPullDistance, double maxPullSpeed,
            float attackStrengthPullModifier, double baseMovementSpeed,
            float maxVerticalPull, float verticalPullDampening,
            float pullDistanceScaling) {
        applyPull(entity, sourceEntity, attackStrength, itemStack,
                basePullStrength, maxPullDistance, maxPullSpeed,
                attackStrengthPullModifier, baseMovementSpeed,
                maxVerticalPull, verticalPullDampening, pullDistanceScaling,
                false, false);
    }

    
    public static void pullTowardsEnemy(LivingEntity entity, LivingEntity sourceEntity,
            float attackStrength, ItemStack itemStack,
            float basePullStrength, float baseAttractStrength,
            float maxPullDistance, double maxPullSpeed,
            float attackStrengthPullModifier, double baseMovementSpeed,
            float maxVerticalPull, float verticalPullDampening,
            float pullDistanceScaling) {
        boolean isSneaking = sourceEntity.isShiftKeyDown();
        float strength = isSneaking ? baseAttractStrength : basePullStrength;
        LivingEntity pulledEntity = isSneaking ? entity : sourceEntity;
        LivingEntity targetEntity = isSneaking ? sourceEntity : entity;
        applyPull(pulledEntity, targetEntity, attackStrength, itemStack,
                strength, maxPullDistance, maxPullSpeed,
                attackStrengthPullModifier, baseMovementSpeed,
                maxVerticalPull, verticalPullDampening, pullDistanceScaling,
                pulledEntity == sourceEntity, false);
    }

    
    public static void pushAwayFromTarget(LivingEntity entity, LivingEntity sourceEntity,
            float attackStrength, ItemStack itemStack,
            float basePushStrength, float maxPushDistance, double maxPushSpeed,
            float attackStrengthPushModifier, double baseMovementSpeed,
            float maxVerticalPush, float verticalPushDampening,
            float pushDistanceScaling) {
        applyPull(sourceEntity, entity, attackStrength, itemStack,
                basePushStrength, maxPushDistance, maxPushSpeed,
                attackStrengthPushModifier, baseMovementSpeed,
                maxVerticalPush, verticalPushDampening, pushDistanceScaling,
                true, true);
    }

    
    private static void applyPull(LivingEntity pulledEntity, LivingEntity targetEntity,
            float attackStrength, ItemStack itemStack,
            float basePullStrength, float maxPullDistance, double maxPullSpeed,
            float attackStrengthPullModifier, double baseMovementSpeed,
            float maxVerticalPull, float verticalPullDampening,
            float pullDistanceScaling,
            boolean isPullingSelf, boolean invertDirection) {
        if (pulledEntity.distanceTo(targetEntity) > maxPullDistance) {
            return;
        }
        Vec3 pulledPos = pulledEntity.position();
        Vec3 targetPos = targetEntity.position();
        double distance = pulledEntity.distanceTo(targetEntity);
        if (distance < 0.001) {
            return;
        }
        Vec3 direction = targetPos.subtract(pulledPos);
        if (invertDirection) {
            direction = direction.reverse();
        }
        direction = direction.normalize();

        float pullFactor = Math.min(1.0f, (float) distance / maxPullDistance) * pullDistanceScaling;
        double pullMultiplier = basePullStrength * pullFactor * attackStrength * attackStrengthPullModifier;

        double knockbackResistance = Math.max(0.0,
                Math.min(1.0, pulledEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double knockbackReduction;
        if (isPullingSelf) {
            knockbackReduction = knockbackResistance * 1.2;
        } else {
            knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 4.0);
        }
        pullMultiplier *= (1.0 - knockbackReduction);

        double movementSpeed = pulledEntity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double movementSpeedMultiplier = Math.max(0.1, movementSpeed / baseMovementSpeed);
        pullMultiplier *= movementSpeedMultiplier;

        double heightDiff = targetPos.y - pulledPos.y;
        double verticalDampeningFactor = 1.0 / (1.0 + Math.abs(heightDiff) * verticalPullDampening);
        double verticalPull = direction.y * verticalDampeningFactor;

        Vec3 pullMotion = new Vec3(direction.x, Math.max(-maxVerticalPull, Math.min(maxVerticalPull, verticalPull)),
                direction.z)
                .normalize().scale(pullMultiplier);

        double distanceToTarget = targetPos.subtract(pulledPos).length();
        if (pullMotion.length() > distanceToTarget) {
            pullMotion = pullMotion.normalize().scale(distanceToTarget * 0.99);
        }

        Vec3 currentMotion = pulledEntity.getDeltaMovement();
        Vec3 newMotion = currentMotion.scale(0.5).add(pullMotion);
        if (newMotion.length() > maxPullSpeed) {
            newMotion = newMotion.normalize().scale(maxPullSpeed);
        }

        pulledEntity.setDeltaMovement(newMotion);
        pulledEntity.hurtMarked = true;

        pulledEntity.level().playSound(null,
                pulledEntity.position().x, pulledEntity.position().y, pulledEntity.position().z,
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);

        updateClientMotion(pulledEntity);
    }

    private static void updateClientMotion(LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
        } else if (!entity.level().isClientSide) {
            entity.level().getEntities(null, entity.getBoundingBox().inflate(10.0))
                    .stream()
                    .filter(e -> e instanceof ServerPlayer)
                    .forEach(e -> ((ServerPlayer) e).connection
                            .send(new ClientboundSetEntityMotionPacket(entity)));
        }
    }
}
