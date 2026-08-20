package net.jaams.weaponry.entity;

import java.util.List;
import java.util.stream.Collectors;
import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.init.ModParticles;
import net.jaams.weaponry.particle.CustomFlashParticleData;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class DynamiteProjectileEntity extends BaseWeaponProjectileEntity {

    protected static final EntityDataAccessor<Integer> ID_TICKS_ON_GROUND = SynchedEntityData
            .defineId(DynamiteProjectileEntity.class, EntityDataSerializers.INT);
    private static final String NBT_GROUND_SMOKE = "DynamiteGroundSmokeParticle";
    private static final String NBT_GROUND_FLAME = "DynamiteGroundFlameParticle";
    private static final String NBT_GROUND_FLASH = "DynamiteGroundFlashParticle";
    private static final String NBT_IGNITING_SOUND = "DynamiteIgnitingSound";
    private static final String NBT_BREAK_BLOCKS = "DynamiteBreakBlocks";
    private Direction lastHitDirection = null;

    public DynamiteProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.DYNAMITE_PROJECTILE.get(), world);
    }

    public DynamiteProjectileEntity(EntityType<? extends DynamiteProjectileEntity> type, Level world) {
        super(type, world);
    }

    public DynamiteProjectileEntity(EntityType<? extends DynamiteProjectileEntity> type, double x, double y, double z,
            Level world) {
        super(type, x, y, z, world);
    }

    public DynamiteProjectileEntity(EntityType<? extends DynamiteProjectileEntity> type, LivingEntity entity,
            Level world) {
        super(type, entity, world);
    }

    public DynamiteProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
        super(ModEntities.DYNAMITE_PROJECTILE.get(), entity, world, weaponItem);
        initializeProjectileStats(weaponItem);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_TICKS_ON_GROUND, 0);
    }

    @Override
    protected float getDefaultBaseDamage(ItemStack weapon) {
        return ModProjectiles.getBaseDamage(weapon, ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_DAMAGE.get());
    }

    @Override
    protected float getDefaultBaseKnockback(ItemStack weapon) {
        return ModProjectiles.getBaseKnockback(weapon, ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_KNOCKBACK.get());
    }

    @Override
    protected int getDefaultPierceLevel(ItemStack weapon) {
        return ModProjectiles.getPiercingLevel(weapon, ProjectileCommonConfig.DYNAMITE_PROJECTILE_PIERCING_LEVEL.get());
    }

    @Override
    public ItemStack getDefaultWeaponItem() {
        return new ItemStack(ModItems.DYNAMITE.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModProjectiles.getDimensions(this.getWeaponItem(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_HITBOX_WIDTH.get().floatValue(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_HITBOX_HEIGHT.get().floatValue());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileHitSound", "projectile_hit",
                "empty_sound", SoundEvents.GENERIC_EXPLODE, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileGroundSound",
                "projectile_ground", "empty_sound", SoundEvents.GENERIC_EXPLODE,
                (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileReturnSound",
                "projectile_return", "projectile_return", SoundEvents.TRIDENT_RETURN,
                (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BounceCount", bounceCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.bounceCount = compound.getInt("BounceCount");
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return false;
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return false;
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return false;
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return false;
    }

    @Override
    public int getMaxBlockBreaks() {
        return 0;
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return false;
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return 0;
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        return false;
    }

    @Override
    protected float getWaterInertia() {
        return ModProjectiles.getWaterInertia(this.getWeaponItem(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_WATER_INERTIA.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getWeaponItem(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getWeaponItem(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected void applyHitEffects(Entity target, EntityHitResult hitResult) {
        if (shouldExplodeOnEntityHit(this.getWeaponItem())) {
            explode(hitResult.getLocation());
        }
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide) {
            return;
        }
        if (!this.entityData.get(ID_IMPACTED)) {
            this.entityData.set(ID_IMPACTED, true);
        }
        Direction hitDirection = blockHitResult.getDirection();
        Vec3 motion = this.getDeltaMovement();
        boolean sameFace = (lastHitDirection != null && lastHitDirection == hitDirection);
        if (shouldExplodeOnBlockHit(this.getWeaponItem())) {
            explode(blockHitResult.getLocation());
        }
        if (bounceCount < getMaxBounces(this.getWeaponItem()) && !sameFace) {
            Vec3i normal = hitDirection.getNormal();
            double dotProduct = motion.x * normal.getX() + motion.y * normal.getY() + motion.z * normal.getZ();
            Vec3 reflection = new Vec3(motion.x - 2 * dotProduct * normal.getX(),
                    motion.y - 2 * dotProduct * normal.getY(), motion.z - 2 * dotProduct * normal.getZ());
            double elasticity = 0.68;
            Vec3 newVelocity = reflection.normalize().scale(motion.length() * elasticity);
            this.setDeltaMovement(newVelocity);
            Vec3 nudge = newVelocity.normalize().scale(0.085);
            this.setPos(getX() + nudge.x, getY() + nudge.y, getZ() + nudge.z);
            bounceCount++;
            this.entityData.set(ID_TICKS_ON_GROUND, 0);
            lastHitDirection = hitDirection;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverWorld) {
            boolean isInWater = this.isUnderWater();
            if (!this.entityData.get(ID_IMPACTED)) {
                lastHitDirection = null;
            }
            handleProjectileCollisions(serverWorld, isInWater);
            handleGroundBehavior(serverWorld, isInWater);
            handleLavaInteraction(serverWorld);
        }
    }

    @Override
    protected void tickDespawn() {
        life++;
        int despawnTime = ModProjectiles.getDespawnTicks(this.getWeaponItem(),
                ProjectileCommonConfig.DYNAMITE_PROJECTILE_DESPAWN_TIME.get());
        if (life >= despawnTime) {
            if (ProjectileCommonConfig.DYNAMITE_PROJECTILE_DESPAWN_AS_ITEM.get()
                    && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY && !hasIgnited()) {
                ModProjectiles.dropAsItem(this.level(), this.getPickupItem().copy(), this.getX(), this.getY(),
                        this.getZ());
            } else if (!this.level().isClientSide) {
                ItemStack itemStack = this.weaponItem.copy();
                if (!itemStack.isEmpty()) {
                    ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack),
                            getX(), getY() + 0.5, getZ(), 5, 0.1d, 0.1d, 0.1d, 0.05d);
                }
            }
            discard();
        }
    }

    @Override
    protected void handleProjectileTrail() {
        if (this.inGround) {
            return;
        }
        ItemStack stack = this.getWeaponItem();
        if (stack.isEmpty())
            return;
        if (!shouldShowTrail()) {
            return;
        }
        double spawnRate = getTrailSpawnRate();
        if (spawnRate <= 0) {
            return;
        }
        int interval = Math.max(1, (int) (1.0 / Math.max(0.01, spawnRate)));
        if (this.tickCount % interval != 0) {
            return;
        }
        ParticleOptions particle = ModProjectiles.getTrailParticle(stack, ParticleTypes.SMOKE);
        Vec3 motion = this.getDeltaMovement().normalize();
        double offset = 0.45;
        double x = this.getX() - motion.x * offset;
        double y = this.getY() + 0.15 - motion.y * offset;
        double z = this.getZ() - motion.z * offset;
        this.level().addParticle(particle, x, y, z, 0.0D, 0.02D, 0.0D);
    }

    @Override
    protected boolean shouldShowTrail() {
        if (super.shouldShowTrail()) {
            return true;
        }
        return ProjectileClientConfig.DYNAMITE_PROJECTILE_TRAIL.get();
    }

    @Override
    protected double getTrailSpawnRate() {
        double baseRate = super.getTrailSpawnRate();
        if (baseRate > 0) {
            return baseRate;
        }
        return ProjectileClientConfig.DYNAMITE_PROJECTILE_TRAIL_SPAWN_RATE.get();
    }

    public static boolean shouldExplodeOnEntityHit(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_ENTITY_HIT.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("DynamiteExplodeOnEntityHit")) {
            return tag.getBoolean("DynamiteExplodeOnEntityHit");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_ENTITY_HIT.get();
    }

    public static boolean shouldExplodeOnBlockHit(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_BLOCK_HIT.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("DynamiteExplodeOnBlockHit")) {
            return tag.getBoolean("DynamiteExplodeOnBlockHit");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_BLOCK_HIT.get();
    }

    public static int getMaxBounces(ItemStack stack) {
        return ModProjectiles.getMaxBounces(stack, ProjectileCommonConfig.DYNAMITE_PROJECTILE_MAX_BOUNCES);
    }

    public static float getExplosionPower(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_POWER.get().floatValue();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("DynamiteExplosionPower")) {
            return tag.getFloat("DynamiteExplosionPower");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_POWER.get().floatValue();
    }

    public static boolean causesFire(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_CAUSES_FIRE.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("DynamiteCausesFire")) {
            return tag.getBoolean("DynamiteCausesFire");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_CAUSES_FIRE.get();
    }

    public static int getFuseTicks(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_FUSE_TICKS.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("DynamiteFuseTicks")) {
            return tag.getInt("DynamiteFuseTicks");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_FUSE_TICKS.get();
    }

    public static boolean shouldBreakBlocks(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_BREAK_BLOCKS.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_BREAK_BLOCKS)) {
            return tag.getBoolean(NBT_BREAK_BLOCKS);
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_BREAK_BLOCKS.get();
    }

    public static boolean shouldExplodeOnProjectileHit(ItemStack stack) {
        if (stack == null)
            return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_PROJECTILE_HIT.get();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("ExplodeOnProjectileHit")) {
            return tag.getBoolean("ExplodeOnProjectileHit");
        }
        return ProjectileCommonConfig.DYNAMITE_PROJECTILE_EXPLODE_ON_PROJECTILE_HIT.get();
    }

    protected SoundEvent getIgnitingSound() {
        ItemStack weapon = this.getWeaponItem();
        if (weapon != null && weapon.hasTag()
                && weapon.getTag().contains(NBT_IGNITING_SOUND, net.minecraft.nbt.Tag.TAG_STRING)) {
            String soundId = weapon.getTag().getString(NBT_IGNITING_SOUND);
            SoundEvent custom = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
            if (custom != null) {
                return custom;
            }
        }
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("jaams_weaponry:dynamite_igniting"));
    }

    private Level.ExplosionInteraction getExplosionInteraction() {
        return shouldBreakBlocks(this.getWeaponItem()) ? Level.ExplosionInteraction.BLOCK
                : Level.ExplosionInteraction.NONE;
    }

    private void explode(Vec3 pos) {
        if (this.level().isClientSide)
            return;
        float power = getExplosionPower(this.getWeaponItem());
        boolean fire = causesFire(this.getWeaponItem());
        Level world = this.level();
        BlockPos center = BlockPos.containing(pos);
        Level.ExplosionInteraction interaction = getExplosionInteraction();
        BlockPos.betweenClosedStream(center.offset(-3, -3, -3), center.offset(3, 3, 3))
                .filter((p) -> world.getBlockState(p).is(Blocks.TNT))
                .forEach((p) -> world.explode(null, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, power, fire,
                        interaction));
        world.explode(this, pos.x, pos.y, pos.z, power, fire, interaction);
        this.discard();
    }

    private void handleProjectileCollisions(ServerLevel serverWorld, boolean isInWater) {
        if (!shouldExplodeOnProjectileHit(this.getWeaponItem())) {
            return;
        }
        List<Entity> collidingProjectiles = this.level()
                .getEntitiesOfClass(Entity.class, this.getBoundingBox())
                .stream()
                .filter((entity) -> entity instanceof Projectile && entity != this
                        && !(entity instanceof DynamiteProjectileEntity))
                .collect(Collectors.toList());
        for (Entity ignored : collidingProjectiles) {
            spawnItemParticles(serverWorld, 5, 0.1D, 0.05D);
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.02D, -0.2D, -0.02D));
            explode(this.position());
        }
    }

    private boolean hasIgnited() {
        if (!this.entityData.get(ID_IMPACTED)) {
            return false;
        }
        int ticksOnGround = this.entityData.get(ID_TICKS_ON_GROUND);
        int fuseTicks = getFuseTicks(this.getWeaponItem());
        return ticksOnGround >= fuseTicks - 15;
    }

    @Override
    public void playerTouch(Player player) {
        if (hasIgnited()) {
            return;
        }
        super.playerTouch(player);
    }

    private void handleGroundBehavior(ServerLevel serverWorld, boolean isInWater) {
        if (!this.entityData.get(ID_IMPACTED)) {
            return;
        }
        int ticksOnGround = this.entityData.get(ID_TICKS_ON_GROUND) + 1;
        this.entityData.set(ID_TICKS_ON_GROUND, ticksOnGround);
        int fuseTicks = getFuseTicks(this.getWeaponItem());
        float projectileSize = Math.max(getBbWidth(), getBbHeight());
        if (isInWater) {
            serverWorld.sendParticles(ParticleTypes.BUBBLE, this.getX(), this.getY() + 0.6D, this.getZ(), 4, 0.05D,
                    0.05D, 0.05D, 0.0D);
            if (ticksOnGround >= fuseTicks) {
                spawnItemEntity();
                this.discard();
            }
            return;
        }
        if (ticksOnGround >= 0 && ticksOnGround <= fuseTicks - 15 && projectileSize <= 4.0F) {
            ParticleOptions smoke = getCustomParticle(NBT_GROUND_SMOKE, ParticleTypes.SMOKE);
            serverWorld.sendParticles(smoke, this.getX(), this.getY() + 0.6D, this.getZ(), 1, 0.05D, 0.05D, 0.05D,
                    0.0D);
        }
        if (ticksOnGround == fuseTicks / 5) {
            SoundEvent sound = getIgnitingSound();
            if (sound != null) {
                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.BLOCKS, 1.0F,
                        1.0F);
            }
        }
        if (ticksOnGround == fuseTicks - 15 && projectileSize <= 1.5F) {
            ParticleOptions flame = getCustomParticle(NBT_GROUND_FLAME, ParticleTypes.FLAME);
            serverWorld.sendParticles(flame, this.getX(), this.getY() + 0.6D, this.getZ(), 1, 0.05D, 0.05D, 0.05D,
                    0.0D);
        }
        if (ticksOnGround == fuseTicks - 10) {
            ParticleOptions flash = getCustomParticle(NBT_GROUND_FLASH, ParticleTypes.FLASH);
            serverWorld.sendParticles(flash, this.getX(), this.getY() + 0.4D, this.getZ(), 1, 0.05D, 0.05D, 0.05D,
                    0.0D);
        }
        if (ticksOnGround >= fuseTicks) {
            spawnItemParticles(serverWorld, 5, 0.1D, 0.05D);
            explode(this.position());
        }
    }

    private void handleLavaInteraction(ServerLevel serverWorld) {
        if (this.isInLava()) {
            explode(this.position());
        }
    }

    private ParticleOptions getCustomParticle(String nbtKey, ParticleOptions defaultParticle) {
        ItemStack weapon = this.getWeaponItem();
        if (weapon.isEmpty())
            return defaultParticle;
        CompoundTag tag = weapon.getOrCreateTag();
        if (!tag.contains(nbtKey, Tag.TAG_STRING))
            return defaultParticle;
        String value = tag.getString(nbtKey).trim();
        if (value.isEmpty())
            return defaultParticle;
        if (value.toLowerCase().contains("custom_flash")) {
            try {
                String[] parts = value.split("\\s+");
                if (parts.length >= 5) {
                    float r = Float.parseFloat(parts[1]);
                    float g = Float.parseFloat(parts[2]);
                    float b = Float.parseFloat(parts[3]);
                    float size = Float.parseFloat(parts[4]);
                    return new CustomFlashParticleData(Mth.clamp(r, 0f, 1f), Mth.clamp(g, 0f, 1f), Mth.clamp(b, 0f, 1f),
                            size);
                }
            } catch (Exception ignored) {
            }
        }
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null)
            id = new ResourceLocation("minecraft", value);
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(id);
        if (type instanceof SimpleParticleType simpleType)
            return simpleType;
        if (type == ModParticles.CUSTOM_FLASH_PARTICLE.get()) {
            return new CustomFlashParticleData(1.0f, 1.0f, 1.0f, 1.5f);
        }
        return defaultParticle;
    }

    private void spawnItemParticles(ServerLevel serverWorld, int count, double delta, double speed) {
        if (!this.getWeaponItem().isEmpty()) {
            serverWorld.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.getWeaponItem()), this.getX(),
                    this.getY(), this.getZ(), count, delta, delta, delta, speed);
        }
    }

    private void spawnItemEntity() {
        if (!this.level().isClientSide && !hasIgnited()) {
            ItemStack itemToSpawn = this.getWeaponItem().copy();
            itemToSpawn.setCount(1);
            ItemEntity entityToSpawn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemToSpawn);
            entityToSpawn.setPickUpDelay(10);
            this.level().addFreshEntity(entityToSpawn);
        }
    }
}
