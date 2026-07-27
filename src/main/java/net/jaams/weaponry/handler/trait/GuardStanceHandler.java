package net.jaams.weaponry.handler.trait;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.handler.event.AdvancementsHandler;

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class GuardStanceHandler {

    public static float PARTICLE_OFFSET_Y = 0.6F;

    public static boolean isGuardStanceEnabled() {
        return TraitsConfig.GUARD_STANCE.get();
    }

    public static BlockingProperties getDefaultBlocking() {
        BlockingProperties.Builder builder = new BlockingProperties.Builder();
        builder.cooldownTicks(TraitsConfig.GUARD_STANCE_COOLDOWN_TICKS.get());
        builder.areaDamageMultiplier(TraitsConfig.GUARD_STANCE_AREA_DAMAGE_MULTIPLIER.get());
        builder.knockbackForce(TraitsConfig.GUARD_STANCE_KNOCKBACK_FORCE.get());
        builder.areaRange(TraitsConfig.GUARD_STANCE_AREA_RANGE.get());
        builder.blockDamageReduction(TraitsConfig.GUARD_STANCE_BLOCK_DAMAGE_REDUCTION.get());
        builder.damagePerBlock(TraitsConfig.GUARD_STANCE_DAMAGE_PER_BLOCK.get());
        builder.damageOnStop(TraitsConfig.GUARD_STANCE_DAMAGE_ON_STOP.get());
        builder.particleSize((float) (double) TraitsConfig.GUARD_STANCE_PARTICLE_SIZE.get());
        builder.particleDistance((float) (double) TraitsConfig.GUARD_STANCE_PARTICLE_DISTANCE.get());
        return builder.build();
    }

    public static BlockingProperties getBlockingProperties(ItemStack stack) {
        Optional<TraitModifierData.GuardStanceEntry> entry = TraitModifierData.getGuardStance(stack);
        if (entry.isEmpty()) {
            return getDefaultBlocking();
        }
        TraitModifierData.GuardStanceEntry data = entry.get();
        BlockingProperties.Builder builder = new BlockingProperties.Builder();
        if (data.cooldown_ticks != null)
            builder.cooldownTicks(data.cooldown_ticks);
        else
            builder.cooldownTicks(TraitsConfig.GUARD_STANCE_COOLDOWN_TICKS.get());
        if (data.area_damage_multiplier != null)
            builder.areaDamageMultiplier(data.area_damage_multiplier);
        else
            builder.areaDamageMultiplier(TraitsConfig.GUARD_STANCE_AREA_DAMAGE_MULTIPLIER.get());
        if (data.knockback_force != null)
            builder.knockbackForce(data.knockback_force);
        else
            builder.knockbackForce(TraitsConfig.GUARD_STANCE_KNOCKBACK_FORCE.get());
        if (data.area_range != null)
            builder.areaRange(data.area_range);
        else
            builder.areaRange(TraitsConfig.GUARD_STANCE_AREA_RANGE.get());
        if (data.block_damage_reduction != null)
            builder.blockDamageReduction(data.block_damage_reduction);
        else
            builder.blockDamageReduction(TraitsConfig.GUARD_STANCE_BLOCK_DAMAGE_REDUCTION.get());
        if (data.damage_per_block != null)
            builder.damagePerBlock(data.damage_per_block);
        else
            builder.damagePerBlock(TraitsConfig.GUARD_STANCE_DAMAGE_PER_BLOCK.get());
        if (data.damage_on_stop != null)
            builder.damageOnStop(data.damage_on_stop);
        else
            builder.damageOnStop(TraitsConfig.GUARD_STANCE_DAMAGE_ON_STOP.get());
        if (data.particle_size != null)
            builder.particleSize(data.particle_size);
        else
            builder.particleSize((float) (double) TraitsConfig.GUARD_STANCE_PARTICLE_SIZE.get());
        if (data.particle_distance != null)
            builder.particleDistance(data.particle_distance);
        else
            builder.particleDistance((float) (double) TraitsConfig.GUARD_STANCE_PARTICLE_DISTANCE.get());
        if (data.block_sound != null && !data.block_sound.isEmpty()) {
            ResourceLocation soundId = ResourceLocation.tryParse(data.block_sound);
            if (soundId != null) {
                SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundId);
                if (sound != null)
                    builder.blockSound(sound);
            }
        }
        return builder.build();
    }

    public static boolean isProlongedBlocking(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        ResourceLocation prolongedKey = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "prolonged_blocking");
        return persistentData.getBoolean(prolongedKey.toString());
    }

    @SubscribeEvent
    public static void onPlayerBlockingTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CompoundTag persistentData = player.getPersistentData();
            ResourceLocation blockingTicksKey = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "blocking_ticks");
            ResourceLocation prolongedKey = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "prolonged_blocking");
            if (player.isBlocking()) {
                int blockingTicks = persistentData.getInt(blockingTicksKey.toString());
                if (blockingTicks < 20) {
                    blockingTicks++;
                    persistentData.putInt(blockingTicksKey.toString(), blockingTicks);
                    if (blockingTicks >= 20) {
                        persistentData.putBoolean(prolongedKey.toString(), true);
                    }
                }
            } else {
                persistentData.remove(blockingTicksKey.toString());
                persistentData.remove(prolongedKey.toString());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockingLivingAttack(LivingIncomingDamageEvent event) {
        if (!isGuardStanceEnabled())
            return;
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack itemStack = player.getUseItem();
        if (!ModTraits.isGuardStanceItem(itemStack))
            return;
        if (!isBlocking(player))
            return;
        BlockingProperties config = getBlockingProperties(itemStack);
        Entity source = event.getSource().getEntity();
        if (source instanceof LivingEntity attacker
                && canDisableBlocking(attacker.getMainHandItem(), itemStack, player, attacker)) {
            disableBlockingWeapon(player, itemStack);
            event.setCanceled(true);
            return;
        }
        if (source != null && isInBlockingArea(player, source)) {
            player.hurtTime = 0;
            player.hurtDuration = 0;
            player.invulnerableTime = 0;
            player.clearFire();
            player.refreshDimensions();

            SoundEvent blockSound = config.blockSound != null ? config.blockSound : SoundEvents.SHIELD_BLOCK;
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    blockSound, player.getSoundSource(), 1.0F, 1.0F);
            float originalDamage = event.getAmount();
            int damage = Math.max(0, Math.round(originalDamage * config.damageOnStop));
            itemStack.hurtAndBreak(damage, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            if (config.blockDamageReduction >= 1.0) {
                event.setCanceled(true);
            }
        }
    }


	@SubscribeEvent
	public static void onShieldBlock(LivingShieldBlockEvent event) {
	    if (!isGuardStanceEnabled())
	        return;
	    LivingEntity blocker = event.getEntity();
	    if (!(blocker instanceof Player player))
	        return;
	    ItemStack itemStack = player.getUseItem();
	    if (!ModTraits.isGuardStanceItem(itemStack))
	        return;
	    if (!isBlocking(player))
	        return;
	    BlockingProperties config = getBlockingProperties(itemStack);
	    Entity source = event.getDamageSource().getEntity();
	    if (source != null && isInBlockingArea(player, source)) {
	        float originalDamage = event.getOriginalBlockedDamage();
	        float blockedDamage = originalDamage * (float) config.blockDamageReduction;
	        event.setBlockedDamage(blockedDamage);
	        event.setShieldDamage(0.0f);
	    }
	}

    @SubscribeEvent
    public static void onBlockingStopUsing(LivingEntityUseItemEvent.Stop event) {
        if (!isGuardStanceEnabled())
            return;
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack itemStack = event.getItem();
        if (!ModTraits.isGuardStanceItem(itemStack))
            return;
        BlockingProperties config = getBlockingProperties(itemStack);
        Level level = player.level();
        if (player.isCrouching()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK,
                    player.getSoundSource(), 1.0F, 1.0F);
            return;
        }
        boolean applyCooldown = TraitsConfig.GUARD_STANCE_COOLDOWN.get();
        boolean applyAreaDamage = TraitsConfig.GUARD_STANCE_AREA_DAMAGE.get();
        if (applyCooldown) {
            boolean globalCooldown = TraitsConfig.GUARD_STANCE_GLOBAL_COOLDOWN.get();
            if (globalCooldown) {
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty() && ModTraits.isGuardStanceItem(stack)) {
                        player.getCooldowns().addCooldown(stack.getItem(), config.cooldownTicks);
                    }
                }
                ItemStack offHandStack = player.getOffhandItem();
                if (!offHandStack.isEmpty() && ModTraits.isGuardStanceItem(offHandStack)) {
                    player.getCooldowns().addCooldown(offHandStack.getItem(), config.cooldownTicks);
                }
                for (ItemStack stack : player.getInventory().armor) {
                    if (!stack.isEmpty() && ModTraits.isGuardStanceItem(stack)) {
                        player.getCooldowns().addCooldown(stack.getItem(), config.cooldownTicks);
                    }
                }
            } else {
                player.getCooldowns().addCooldown(itemStack.getItem(), config.cooldownTicks);
            }
        }
        if (applyAreaDamage) {
            double weaponDamage = net.jaams.weaponry.util.ModUtils.attackDamageModifierSum(itemStack, EquipmentSlot.MAINHAND);
            if (weaponDamage <= 0.0) {
                weaponDamage = 1.0;
            }
            float areaDamage = (float) (weaponDamage * config.areaDamageMultiplier);
            applyAreaDamage(player, areaDamage, config.knockbackForce, config.areaRange);
        }
        itemStack.hurtAndBreak(config.damageOnStop, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        SoundEvent sound = applyAreaDamage ? SoundEvents.PLAYER_ATTACK_SWEEP : SoundEvents.PLAYER_ATTACK_WEAK;
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            if (player.isUnderWater()) {
                spawnBubbleSweep(serverLevel, player, (float) config.areaRange, (float) config.particleSize);
            }
            if (applyAreaDamage) {
                spawnParticleInFront(serverLevel, player,
                        new CustomSweepParticleData(1.0F, 1.0F, 1.0F, (float) config.particleSize),
                        (float) config.particleSize, (float) config.particleDistance, itemStack);
            }
        }
    }

    private static void applyAreaDamage(Player player, float areaDamage, double knockbackForce, double areaRange) {
        if (!TraitsConfig.GUARD_STANCE_AREA_DAMAGE.get())
            return;
        if (!(player.level() instanceof ServerLevel serverLevel))
            return;
        AABB area = player.getBoundingBox().inflate(areaRange);
        DamageSource damageSource = player.damageSources().playerAttack(player);
        ItemStack mainHandItem = player.getMainHandItem();
        int smiteLevel = ModEnchantments.level(mainHandItem, Enchantments.SMITE);
        int baneLevel = ModEnchantments.level(mainHandItem, Enchantments.BANE_OF_ARTHROPODS);
        int fireAspectLevel = ModEnchantments.level(mainHandItem, Enchantments.FIRE_ASPECT);
        int knockbackLevel = ModEnchantments.level(mainHandItem, Enchantments.KNOCKBACK);
        List<LivingEntity> targets = serverLevel
                .getEntitiesOfClass(LivingEntity.class, area,
                        entity -> !ModUtils.isAlliedEntity(player, entity) && entity.isAlive() && entity != player)
                .stream().filter(target -> isInBlockingArea(player, target))
                .collect(Collectors.toList());
        int targetCount = targets.size();
        float adjustedAreaDamage = targetCount > 1 ? areaDamage / (float) Math.sqrt(targetCount) : areaDamage;
        for (LivingEntity target : targets) {
            float adjustedDamage = adjustedAreaDamage;
            if (smiteLevel > 0 && target.getType().is(EntityTypeTags.UNDEAD)) {
                adjustedDamage += smiteLevel * 1.5F;
            }
            if (baneLevel > 0 && target.getType().is(EntityTypeTags.ARTHROPOD)) {
                adjustedDamage += baneLevel * 1.5F;
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            }
            target.hurt(damageSource, adjustedDamage);
            if (fireAspectLevel > 0) {
                target.igniteForSeconds(fireAspectLevel * 4);
            }
            Vec3 knockbackDirection = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ())
                    .normalize().scale(-1);
            double knockbackX = knockbackDirection.x * (1 + knockbackLevel * 0.5);
            double knockbackZ = knockbackDirection.z * (1 + knockbackLevel * 0.5);
            target.knockback(knockbackForce, knockbackX, knockbackZ);
            if (!player.isCreative() && target instanceof Mob mobTarget) {
                mobTarget.setTarget(player);
            }
        }
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP,
                player.getSoundSource(), 1.0F, 0.9F);
    }

    private static void spawnParticleInFront(ServerLevel serverLevel, LivingEntity entity, ParticleOptions particleType,
            float particleSize, float particleDistance, ItemStack weapon) {
        if (entity == null || serverLevel.isClientSide() || particleType == null || particleSize <= 0.0F)
            return;
        if (!TraitsConfig.GUARD_STANCE_AREA_DAMAGE.get())
            return;
        Vec3 lookVec = entity.getLookAngle().normalize();
        double startX = entity.getX();
        double startY = entity.getY() + entity.getEyeHeight() * PARTICLE_OFFSET_Y;
        double startZ = entity.getZ();
        Vec3 startPos = new Vec3(startX, startY, startZ);
        Vec3 endPos = startPos.add(lookVec.scale(particleDistance));
        ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                entity);
        BlockHitResult blockHit = serverLevel.clip(clipContext);
        EntityHitResult entityHit = getEntityHitResult(serverLevel, entity, startPos, endPos);
        double adjustedDistance = Math.max(particleDistance - 0.1f, 0.1f);
        if (blockHit.getType() != HitResult.Type.MISS) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
        }
        if (entityHit != null) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
        }
        double particleX = startX + lookVec.x * adjustedDistance;
        double particleY = startY + lookVec.y * adjustedDistance;
        double particleZ = startZ + lookVec.z * adjustedDistance;
        RandomSource random = serverLevel.random;
        float r, g, b;
        int fireAspectLevel = ModEnchantments.level(weapon, Enchantments.FIRE_ASPECT);
        if (fireAspectLevel > 0) {
            r = 0.8F + random.nextFloat() * 0.2F;
            g = 0.3F + random.nextFloat() * 0.3F;
            b = random.nextFloat() * 0.2F;
        } else {
            float grayValue = 0.4F + random.nextFloat() * 0.6F;
            r = g = b = grayValue;
        }
        serverLevel.sendParticles(new CustomSweepParticleData(r, g, b, particleSize), particleX, particleY, particleZ,
                1, 0.0, 0.0, 0.0, 0.0);
    }

    private static void spawnBubbleSweep(ServerLevel serverLevel, Player player, float particleDistance,
            float particleSize) {
        if (serverLevel.isClientSide())
            return;
        if (!TraitsConfig.GUARD_STANCE_AREA_DAMAGE.get())
            return;
        double startX = player.getX();
        double startY = player.getY() + player.getEyeHeight() * 0.8;
        double startZ = player.getZ();
        Vec3 startPos = new Vec3(startX, startY, startZ);
        int particleCount = 8;
        float arcAngle = (float) Math.toRadians(60);
        float angleStep = arcAngle / (particleCount - 1);
        RandomSource random = serverLevel.random;
        float pitch = player.getXRot();
        float clampedPitch = Math.max(-80.0F, Math.min(80.0F, pitch));
        Vec3 adjustedLookVec = Vec3.directionFromRotation(clampedPitch, player.getYRot()).normalize();
        for (int i = 0; i < particleCount; i++) {
            float angle = -arcAngle / 2 + i * angleStep;
            Vec3 rightVec = adjustedLookVec.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 rotatedVec = adjustedLookVec.scale(Math.cos(angle)).add(rightVec.scale(Math.sin(angle))).normalize();
            Vec3 endPos = startPos.add(rotatedVec.scale(particleDistance));
            ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, player);
            BlockHitResult blockHit = serverLevel.clip(clipContext);
            EntityHitResult entityHit = getEntityHitResult(serverLevel, player, startPos, endPos);
            double adjustedDistance = Math.max(particleDistance - 0.1f, 0.1f);
            if (blockHit.getType() != HitResult.Type.MISS) {
                adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
            }
            if (entityHit != null) {
                adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
            }
            double particleX = startX + rotatedVec.x * adjustedDistance;
            double particleY = startY + rotatedVec.y * adjustedDistance;
            double particleZ = startZ + rotatedVec.z * adjustedDistance;
            serverLevel.sendParticles(ParticleTypes.BUBBLE, particleX + (random.nextDouble() - 0.5) * 0.1,
                    particleY + (random.nextDouble() - 0.5) * 0.1, particleZ + (random.nextDouble() - 0.5) * 0.1, 1,
                    0.0, 0.0, 0.0, 0.0);
        }
    }

    private static EntityHitResult getEntityHitResult(ServerLevel level, LivingEntity shooter, Vec3 startPos,
            Vec3 endPos) {
        AABB aabb = new AABB(startPos, endPos).inflate(1.0);
        for (Entity entity : level.getEntities(shooter, aabb, e -> e instanceof LivingEntity && e.isPickable())) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = entityBox.clip(startPos, endPos);
            if (hit.isPresent()) {
                return new EntityHitResult(entity, hit.get());
            }
        }
        return null;
    }

    private static boolean isInBlockingArea(Player player, Entity source) {
        Direction playerFront = player.getDirection();
        Direction attackerFacing = Direction.getNearest(source.getX() - player.getX(), 0,
                source.getZ() - player.getZ());
        Direction leftSide = playerFront.getCounterClockWise();
        Direction rightSide = playerFront.getClockWise();
        return attackerFacing == playerFront || attackerFacing == leftSide || attackerFacing == rightSide;
    }

    public static boolean isBlocking(Player player) {
        return player.isUsingItem() && player.getUseItem().getUseAnimation() == UseAnim.BLOCK;
    }

    private static boolean canDisableBlocking(ItemStack attackerStack, ItemStack blockingStack, Player player,
            LivingEntity attacker) {
        return !attackerStack.isEmpty()
                && attackerStack.getItem().canDisableShield(attackerStack, blockingStack, player, attacker);
    }

    private static void disableBlockingWeapon(Player player, ItemStack itemStack) {
        if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            player.getCooldowns().addCooldown(itemStack.getItem(), 100);
        }
        itemStack.hurtAndBreak(3, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        player.stopUsingItem();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK,
                player.getSoundSource(), 1.0F, 1.0F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BREAK,
                player.getSoundSource(), 1.0F, 1.0F);
    }

    public static class BlockingProperties {
        public final int cooldownTicks;
        public final double areaDamageMultiplier;
        public final double knockbackForce;
        public final double areaRange;
        public final double blockDamageReduction;
        public final int damagePerBlock;
        public final int damageOnStop;
        public final float particleSize;
        public final float particleDistance;
        public final SoundEvent blockSound;

        private BlockingProperties(Builder builder) {
            this.cooldownTicks = builder.cooldownTicks;
            this.areaDamageMultiplier = builder.areaDamageMultiplier;
            this.knockbackForce = builder.knockbackForce;
            this.areaRange = builder.areaRange;
            this.blockDamageReduction = builder.blockDamageReduction;
            this.damagePerBlock = builder.damagePerBlock;
            this.damageOnStop = builder.damageOnStop;
            this.particleSize = builder.particleSize;
            this.particleDistance = builder.particleDistance;
            this.blockSound = builder.blockSound;
        }

        public static class Builder {
            private int cooldownTicks = 20;
            private double areaDamageMultiplier = 0.35;
            private double knockbackForce = 0.4;
            private double areaRange = 1.5;
            private double blockDamageReduction = 0.5;
            private int damagePerBlock = 1;
            private int damageOnStop = 1;
            private float particleSize = 1.0F;
            private float particleDistance = 1.5F;
            private SoundEvent blockSound = null;

            public Builder cooldownTicks(int cooldownTicks) {
                this.cooldownTicks = cooldownTicks;
                return this;
            }

            public Builder areaDamageMultiplier(double areaDamageMultiplier) {
                this.areaDamageMultiplier = areaDamageMultiplier;
                return this;
            }

            public Builder knockbackForce(double knockbackForce) {
                this.knockbackForce = knockbackForce;
                return this;
            }

            public Builder areaRange(double areaRange) {
                this.areaRange = areaRange;
                return this;
            }

            public Builder blockDamageReduction(double blockDamageReduction) {
                this.blockDamageReduction = blockDamageReduction;
                return this;
            }

            public Builder damagePerBlock(int damagePerBlock) {
                this.damagePerBlock = damagePerBlock;
                return this;
            }

            public Builder damageOnStop(int damageOnStop) {
                this.damageOnStop = damageOnStop;
                return this;
            }

            public Builder particleSize(float particleSize) {
                this.particleSize = particleSize;
                return this;
            }

            public Builder particleDistance(float particleDistance) {
                this.particleDistance = particleDistance;
                return this;
            }

            public Builder blockSound(SoundEvent blockSound) {
                this.blockSound = blockSound;
                return this;
            }

            public BlockingProperties build() {
                return new BlockingProperties(this);
            }
        }
    }
}
