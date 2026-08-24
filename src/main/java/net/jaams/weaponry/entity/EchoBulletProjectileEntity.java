package net.jaams.weaponry.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileBulletConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.registry.GoldenItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class EchoBulletProjectileEntity extends BaseBulletProjectileEntity {

    private static final EntityDataAccessor<Boolean> ID_SCULK_SOUND = SynchedEntityData
            .defineId(EchoBulletProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> ID_TARGET = SynchedEntityData
            .defineId(EchoBulletProjectileEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> ID_HOMING_ACTIVE = SynchedEntityData
            .defineId(EchoBulletProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    public EchoBulletProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.ECHO_BULLET_PROJECTILE.get(), world);
        initializeProperties();
    }

    public EchoBulletProjectileEntity(EntityType<? extends EchoBulletProjectileEntity> type, Level world) {
        super(type, world);
        initializeProperties();
    }

    public EchoBulletProjectileEntity(EntityType<? extends EchoBulletProjectileEntity> type, double x, double y,
            double z, Level world) {
        super(type, x, y, z, world);
        initializeProperties();
    }

    public EchoBulletProjectileEntity(EntityType<? extends EchoBulletProjectileEntity> type, LivingEntity entity,
            Level world) {
        super(type, entity, world);
        initializeProperties();
    }

    public EchoBulletProjectileEntity(Level world, LivingEntity shooter, ItemStack gunItem) {
        super(ModEntities.ECHO_BULLET_PROJECTILE.get(), shooter, world, gunItem);
        initializeProperties();
    }

    private void initializeProperties() {
        ItemStack gun = this.getGunItem();
        this.setBulletDamage(
                ModProjectiles.getBaseDamage(gun, ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BASE_DAMAGE.get()));
        this.setBulletKnockback(ModProjectiles.getBaseKnockback(gun,
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BASE_KNOCKBACK.get()));
        this.setPiercingLevel(ModProjectiles.getPiercingLevel(gun,
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_PIERCING_LEVEL.get()));
        this.setColor(ModProjectiles.getColor(gun, ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_COLOR));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SCULK_SOUND, false);
        this.entityData.define(ID_TARGET, Optional.empty());
        this.entityData.define(ID_HOMING_ACTIVE, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("hasPlayedSculkSound", this.entityData.get(ID_SCULK_SOUND));
        compound.putBoolean("homingActive", this.entityData.get(ID_HOMING_ACTIVE));
        Optional<UUID> targetUUID = this.entityData.get(ID_TARGET);
        targetUUID.ifPresent((uuid) -> compound.putUUID("targetUUID", uuid));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(ID_SCULK_SOUND, compound.getBoolean("hasPlayedSculkSound"));
        this.entityData.set(ID_HOMING_ACTIVE, compound.getBoolean("homingActive"));
        if (compound.hasUUID("targetUUID")) {
            this.entityData.set(ID_TARGET, Optional.of(compound.getUUID("targetUUID")));
        } else {
            this.entityData.set(ID_TARGET, Optional.empty());
        }
    }

    @Override
    public ItemStack getDefaultGunItem() {
        return new ItemStack(GoldenItems.GOLDEN_PISTOL.get());
    }

    @Override
    public ItemStack getDefaultBulletItem() {
        return new ItemStack(BottomItems.BULLET.get());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getGunItem(), "ProjectileHitSound", "projectile_hit",
                "bullet_hit", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getGunItem(), "ProjectileGroundSound", "projectile_ground",
                "bullet_ground", SoundEvents.TRIDENT_HIT_GROUND, (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected void applyHitEffects(Entity entity, EntityHitResult hitResult) {
        super.applyHitEffects(entity, hitResult);
    }

    @Override
    protected void applyBlockHitEffects(BlockPos pos, BlockState state, BlockHitResult hitResult) {
        boolean showParticles = ModProjectiles.getShowEchoShardParticles(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_SHOW_ECHO_SHARD_PARTICLES.get());
        if (showParticles && level() instanceof ServerLevel serverLevel) {
            Direction face = hitResult.getDirection();
            double offset = 0.01;
            double x = pos.getX() + 0.5 + (face.getStepX() * (0.5 + offset));
            double y = pos.getY() + 0.5 + (face.getStepY() * (0.5 + offset));
            double z = pos.getZ() + 0.5 + (face.getStepZ() * (0.5 + offset));
            ItemStack sculkStack = new ItemStack(Items.ECHO_SHARD);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, sculkStack), x, y, z, 6, 0.12, 0.12,
                    0.12, 0.08);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide)
            return;
        if (!this.entityData.get(ID_HOMING_ACTIVE)) {
            this.setNoGravity(false);
            this.entityData.set(ID_SCULK_SOUND, false);
            this.entityData.set(ID_TARGET, Optional.empty());
            if (this.getDeltaMovement().lengthSqr() == 0) {
                this.discard();
            }
            return;
        }
        boolean enableHoming = ModProjectiles.getEchoEnableHoming(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_HOMING.get());
        if (!enableHoming) {
            this.disableHoming();
            return;
        }
        LivingEntity target = getStoredTarget();
        if (target == null || !target.isAlive()) {
            if (!this.entityData.get(ID_TARGET).isPresent()) {
                target = findNearestTarget();
                if (target != null) {
                    this.entityData.set(ID_TARGET, Optional.of(target.getUUID()));
                }
            } else {
                this.disableHoming();
                return;
            }
        }
        if (target != null && target.isAlive()) {
            boolean shouldPlaySound = ModProjectiles.getEchoPlayHomingSound(this.getGunItem(),
                    ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_PLAY_HOMING_SOUND.get());
            if (shouldPlaySound && !this.entityData.get(ID_SCULK_SOUND)) {
                SoundEvent homingSound = ModProjectiles.getEchoHomingSound(this.getGunItem(),
                        SoundEvents.SCULK_CLICKING);
                level().playSound(null, getX(), getY(), getZ(), homingSound, SoundSource.NEUTRAL, 1.0F, 1.0F);
                this.entityData.set(ID_SCULK_SOUND, true);
            }
            Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
            Vec3 direction = targetPos.subtract(this.position()).normalize();
            double homingSpeed = ModProjectiles.getEchoHomingSpeed(this.getGunItem(),
                    ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_HOMING_SPEED.get());
            if (hasClearLineOfSight(target)) {
                this.setDeltaMovement(direction.scale(homingSpeed));
                this.setNoGravity(true);
            } else {
                this.setNoGravity(false);
            }
        } else {
            this.disableHoming();
        }
        if (this.getDeltaMovement().lengthSqr() == 0) {
            this.discard();
        }
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        disableHoming();
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        disableHoming();
    }

    private void disableHoming() {
        this.entityData.set(ID_HOMING_ACTIVE, false);
        this.entityData.set(ID_TARGET, Optional.empty());
        this.entityData.set(ID_SCULK_SOUND, false);
        this.setNoGravity(false);
    }

    @Override
    public int getMaxTicksInAir() {
        return ModProjectiles.getMaxTicksInAir(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_AIR.get());
    }

    @Override
    public int getMaxTicksInGround() {
        return ModProjectiles.getMaxTicksInGround(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND.get());
    }

    @Override
    public int getNoGravityDuration() {
        return ModProjectiles.getNoGravityDuration(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_NO_GRAVITY_DURATION.get());
    }

    @Override
    public boolean hasInitialNoGravity() {
        return ModProjectiles.hasInitialNoGravity(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_INITIAL_NO_GRAVITY.get());
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(this.getGunItem(), 100);
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return (ModProjectiles.isCustomBreakableBlock(this.getGunItem(), state,
                ResourceLocation.parse("minecraft:pointed_dripstone")) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glasses"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glass"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glass_panes"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("jaams_weaponry:sharpstone_can_breaks")))
                ||
                blockId.getPath().contains("glass") ||
                blockId.getPath().contains("pane"));
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    private LivingEntity findNearestTarget() {
        double searchRange = ModProjectiles.getEchoSearchRange(this.getGunItem(),
                ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_SEARCH_RANGE.get());
        LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null;
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(searchRange), (entity) -> isValidTarget(entity, owner));
        if (entities.isEmpty()) {
            return null;
        }
        LivingEntity bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        double targetingOwnerMultiplier = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_TARGETING_OWNER_MULTIPLIER
                .get();
        double hostileMultiplier = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_HOSTILE_MULTIPLIER.get();
        double glowingMultiplier = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_GLOWING_MULTIPLIER.get();
        double losMultiplier = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_LOS_MULTIPLIER.get();
        double heightPenalty = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_HEIGHT_PENALTY.get();
        for (LivingEntity e : entities) {
            double distanceSq = this.distanceToSqr(e);
            boolean hasLineOfSight = hasClearLineOfSight(e);
            boolean hasGlowing = e.hasEffect(MobEffects.GLOWING);
            boolean targetingOwner = false;
            if (owner != null && e instanceof Mob mob) {
                LivingEntity mobTarget = mob.getTarget();
                if (mobTarget != null && mobTarget.isAlive() && mobTarget.getUUID().equals(owner.getUUID())) {
                    targetingOwner = true;
                }
            }
            boolean isHostile = e instanceof Monster
                    || (e instanceof NeutralMob neutral && owner != null && neutral.isAngryAt(owner));
            double score = distanceSq;
            if (targetingOwner) {
                score *= targetingOwnerMultiplier;
            } else if (isHostile) {
                score *= hostileMultiplier;
            }
            if (hasGlowing) {
                score *= glowingMultiplier;
            }
            if (hasLineOfSight) {
                score *= losMultiplier;
            }
            double heightDiff = Math.abs(this.getY() - (e.getY() + e.getEyeHeight()));
            score += heightDiff * heightDiff * heightPenalty;
            if (score < bestScore) {
                bestScore = score;
                bestTarget = e;
            }
        }
        return bestTarget;
    }

    private boolean isValidTarget(LivingEntity entity, LivingEntity owner) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        if (entity == this.getOwner() || entity == owner) {
            return false;
        }
        if (isTamedByOwner(entity)) {
            return false;
        }
        if (ModProjectiles.shouldEchoIgnoreEntity(this.getGunItem(), entity)) {
            return false;
        }
        return true;
    }

    private LivingEntity getStoredTarget() {
        Optional<UUID> targetUUID = this.entityData.get(ID_TARGET);
        if (targetUUID.isPresent() && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(targetUUID.get());
            if (entity instanceof LivingEntity living && living.isAlive()) {
                return living;
            }
        }
        return null;
    }

    private boolean hasClearLineOfSight(LivingEntity target) {
        Vec3 targetEyePos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
        BlockHitResult blockHitResult = this.level().clip(new ClipContext(this.position(), targetEyePos,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return blockHitResult.getType() != HitResult.Type.BLOCK
                || blockHitResult.getBlockPos().equals(target.blockPosition());
    }

    private boolean isTamedByOwner(LivingEntity entity) {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (entity.isAlliedTo(owner))
                return true;
            if (entity instanceof AbstractHorse horse) {
                UUID ownerUUID = owner.getUUID();
                UUID horseOwnerUUID = horse.getOwnerUUID();
                return ownerUUID.equals(horseOwnerUUID);
            }
        }
        return false;
    }
}
