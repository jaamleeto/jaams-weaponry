package net.jaams.weaponry.component.projectile;

import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.particle.SmallWaveParticleData;

import javax.annotation.Nullable;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public abstract class BaseItemProjectileEntity extends AbstractArrow implements IEntityAdditionalSpawnData {
    public static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(BaseItemProjectileEntity.class,
            EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(BaseItemProjectileEntity.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ID_SPIN_TICKS = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> ID_LAST_SPIN_ROTATION = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> ID_IMPACTED = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> ID_SOURCE_ITEM = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<ItemStack> ID_PROJECTILE_ITEM = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> ID_CRITICAL = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ID_IGNORE_HIT_TICKS = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ID_PIERCING = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ID_COLOR = SynchedEntityData
            .defineId(BaseItemProjectileEntity.class, EntityDataSerializers.INT);
    protected ItemStack sourceItem;
    protected ItemStack projectileItem;
    protected float projectileDamage;
    protected float projectileKnockback;
    protected float lastSpinRotation = 0.0F;
    protected int ignoreHitTicks = 0;
    protected int pierceLevel = 0;
    protected int piercedCount = 0;
    protected int brokenBlocksCount = 0;
    protected int noGravityTicks = 0;
    protected int waveParticleCount = 0;
    protected int color = 0xFFFFFF;
    protected int multishotGroundTicks = 0;
    protected boolean isStuck = false;
    protected int clientSideReturnTridentTickCount = 0;
    @Nullable
    protected UUID lastHitEntityId;
    private Direction hitFace = Direction.NORTH;
    protected int ticksInAir = 0;
    protected int ticksInGround = 0;
    protected int bubbleTime = 0;

    protected BaseItemProjectileEntity(EntityType<? extends BaseItemProjectileEntity> type, Level level) {
        super(type, level);
        initializeSourceItem(null);
        initializeProjectileItem(null);
    }

    protected BaseItemProjectileEntity(EntityType<? extends BaseItemProjectileEntity> type, double x, double y,
            double z, Level level) {
        super(type, x, y, z, level);
        initializeSourceItem(null);
        initializeProjectileItem(null);
    }

    protected BaseItemProjectileEntity(EntityType<? extends BaseItemProjectileEntity> type, LivingEntity shooter,
            Level level) {
        super(type, shooter, level);
        initializeSourceItem(null);
        initializeProjectileItem(null);
    }

    protected BaseItemProjectileEntity(EntityType<? extends BaseItemProjectileEntity> type, LivingEntity shooter,
            Level level, @Nullable ItemStack sourceItem) {
        super(type, shooter, level);
        initializeSourceItem(sourceItem);
        initializeProjectileItem(sourceItem);
        this.projectileDamage = 0.0F;
        this.projectileKnockback = 0.0F;
        this.entityData.set(ID_CRITICAL, false);
        this.entityData.set(ID_IGNORE_HIT_TICKS, 0);
        this.entityData.set(ID_PIERCING, 0);
        this.entityData.set(ID_COLOR, 0xFFFFFF);
        this.brokenBlocksCount = 0;
        this.noGravityTicks = 0;
        updateEnchantmentData();
    }

    public abstract ItemStack getDefaultSourceItem();

    public abstract ItemStack getDefaultProjectileItem();

    protected abstract SoundEvent getHitSound();

    protected abstract SoundEvent getGroundSound();

    protected abstract SoundEvent getLoyaltySound();

    public abstract int getMaxTicksInAir();

    public abstract int getMaxTicksInGround();

    public abstract int getNoGravityDuration();

    public abstract boolean hasInitialNoGravity();

    protected abstract boolean shouldBreakOnEntityHit();

    protected abstract boolean shouldBreakOnBlockHit();

    protected abstract boolean shouldBreakOnPiercingExhausted();

    public abstract int getMaxBlockBreaks();

    protected abstract boolean shouldBreakAfterMaxBlockBreaks();

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_LOYALTY, (byte) 0);
        this.entityData.define(ID_FOIL, false);
        this.entityData.define(ID_SPIN_TICKS, 0);
        this.entityData.define(ID_LAST_SPIN_ROTATION, 0.0F);
        this.entityData.define(ID_IMPACTED, false);
        this.entityData.define(ID_SOURCE_ITEM, ItemStack.EMPTY);
        this.entityData.define(ID_PROJECTILE_ITEM, ItemStack.EMPTY);
        this.entityData.define(ID_CRITICAL, false);
        this.entityData.define(ID_IGNORE_HIT_TICKS, 0);
        this.entityData.define(ID_PIERCING, 0);
        this.entityData.define(ID_COLOR, 0xFFFFFF);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SourceItem", 10)) {
            this.sourceItem = ItemStack.of(tag.getCompound("SourceItem"));
        } else {
            this.sourceItem = getDefaultSourceItem();
        }
        this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
        if (tag.contains("ProjectileItem", 10)) {
            this.projectileItem = ItemStack.of(tag.getCompound("ProjectileItem"));
        } else {
            this.projectileItem = getDefaultProjectileItem();
        }
        this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem);
        this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
        this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
        this.entityData.set(ID_IMPACTED, tag.getBoolean("HasImpacted"));
        this.entityData.set(ID_CRITICAL, tag.getBoolean("IsCritical"));
        this.projectileDamage = tag.getFloat("ProjectileDamage");
        this.projectileKnockback = tag.getFloat("ProjectileKnockback");
        this.ignoreHitTicks = tag.getInt("IgnoreHitTicks");
        this.pierceLevel = tag.getInt("PierceLevel");
        this.piercedCount = tag.getInt("PiercedCount");
        this.brokenBlocksCount = tag.getInt("BrokenBlocksCount");
        this.color = tag.getInt("Color");
        this.lastSpinRotation = tag.getFloat("LastSpinRotation");
        this.entityData.set(ID_LAST_SPIN_ROTATION, this.lastSpinRotation);
        if (tag.contains("LastHitEntityId")) {
            this.lastHitEntityId = tag.getUUID("LastHitEntityId");
        }
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.entityData.set(ID_COLOR, this.color);
        updateEnchantmentData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.sourceItem.isEmpty()) {
            tag.put("SourceItem", this.sourceItem.save(new CompoundTag()));
        }
        if (!this.projectileItem.isEmpty()) {
            tag.put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
        }
        tag.putBoolean("HasImpacted", this.entityData.get(ID_IMPACTED));
        tag.putBoolean("IsCritical", isCritical());
        tag.putFloat("ProjectileDamage", this.projectileDamage);
        tag.putFloat("ProjectileKnockback", this.projectileKnockback);
        tag.putInt("IgnoreHitTicks", this.ignoreHitTicks);
        tag.putInt("PierceLevel", this.pierceLevel);
        tag.putInt("PiercedCount", this.piercedCount);
        tag.putInt("BrokenBlocksCount", this.brokenBlocksCount);
        tag.putInt("Color", this.color);
        tag.putFloat("LastSpinRotation", this.lastSpinRotation);
        if (this.lastHitEntityId != null) {
            tag.putUUID("LastHitEntityId", this.lastHitEntityId);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeItem(this.sourceItem);
        buffer.writeItem(this.projectileItem);
        buffer.writeFloat(this.projectileDamage);
        buffer.writeBoolean(isCritical());
        buffer.writeInt(this.ignoreHitTicks);
        buffer.writeInt(this.pierceLevel);
        buffer.writeInt(this.brokenBlocksCount);
        buffer.writeInt(this.color);
        buffer.writeFloat(this.lastSpinRotation);
        buffer.writeBoolean(this.lastHitEntityId != null);
        if (this.lastHitEntityId != null) {
            buffer.writeUUID(this.lastHitEntityId);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        this.sourceItem = buf.readItem();
        this.projectileItem = buf.readItem();
        if (this.sourceItem.isEmpty())
            this.sourceItem = getDefaultSourceItem();
        if (this.projectileItem.isEmpty())
            this.projectileItem = getDefaultProjectileItem();
        this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
        this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem);
        this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
        this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
        this.projectileDamage = buf.readFloat();
        this.setCritical(buf.readBoolean());
        this.ignoreHitTicks = buf.readInt();
        this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
        this.pierceLevel = buf.readInt();
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.brokenBlocksCount = buf.readInt();
        this.color = buf.readInt();
        this.entityData.set(ID_COLOR, this.color);
        this.lastSpinRotation = buf.readFloat();
        this.entityData.set(ID_LAST_SPIN_ROTATION, this.lastSpinRotation);
        boolean hasLastHit = buf.readBoolean();
        this.lastHitEntityId = hasLastHit ? buf.readUUID() : null;
        updateEnchantmentData();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ID_SOURCE_ITEM.equals(key)) {
            ItemStack newSource = this.entityData.get(ID_SOURCE_ITEM);
            if (!ItemStack.matches(this.sourceItem, newSource)) {
                this.sourceItem = newSource.isEmpty() ? getDefaultSourceItem() : newSource.copy();
                this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
            }
        } else if (ID_PROJECTILE_ITEM.equals(key)) {
            ItemStack newProjectile = this.entityData.get(ID_PROJECTILE_ITEM);
            if (!ItemStack.matches(this.projectileItem, newProjectile)) {
                this.projectileItem = newProjectile.isEmpty() ? getDefaultProjectileItem() : newProjectile.copy();
                this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
                updateEnchantmentData();
            }
        } else if (ID_IGNORE_HIT_TICKS.equals(key)) {
            this.ignoreHitTicks = this.entityData.get(ID_IGNORE_HIT_TICKS);
        } else if (ID_PIERCING.equals(key)) {
            this.pierceLevel = this.entityData.get(ID_PIERCING);
        } else if (ID_COLOR.equals(key)) {
            this.color = this.entityData.get(ID_COLOR);
        } else if (ID_LAST_SPIN_ROTATION.equals(key)) {
            this.lastSpinRotation = this.entityData.get(ID_LAST_SPIN_ROTATION);
        }
    }

    @Override
    public void tick() {
        if (shouldMaintainNoGravity()) {
            if (this.noGravityTicks == 0 && hasInitialNoGravity() && !this.hasImpacted()) {
                this.noGravityTicks = getNoGravityDuration();
            }
            if (this.noGravityTicks > 0) {
                this.setNoGravity(true);
                this.noGravityTicks--;
            } else {
                this.setNoGravity(false);
            }
        } else {
            this.setNoGravity(false);
        }
        super.tick();
        if (this.tickCount == 1) {
            this.refreshDimensions();
        }
        if (!this.level().isClientSide) {
            syncPersistentDataToEntityData();
            if (isInLava()) {
                handleItemBreak();
                return;
            }
            if (isInWater()) {
                if (!inGround) {
                    bubbleTime++;
                    if (bubbleTime >= 200) {
                        handleItemBreak();
                        return;
                    }
                } else {
                    bubbleTime = 0;
                    ticksInGround++;
                    if (ticksInGround >= getMaxTicksInGround() && getMaxTicksInGround() > 0) {
                        handleItemBreak();
                        return;
                    }
                }
            } else if (!inGround) {
                ticksInAir++;
                if (ticksInAir >= getMaxTicksInAir() && getMaxTicksInAir() > 0) {
                    handleItemBreak();
                    return;
                }
            } else {
                ticksInGround++;
                if (ticksInGround >= getMaxTicksInGround() && getMaxTicksInGround() > 0) {
                    handleItemBreak();
                    return;
                }
            }
        }
        if (this.inGroundTime > 1 && !this.entityData.get(ID_IMPACTED)) {
            this.entityData.set(ID_IMPACTED, true);
        }
        if (this.inGround) {
            this.entityData.set(ID_SPIN_TICKS, 0);
            if (this.getPersistentData().getBoolean("IsMultishotClone")) {
                this.multishotGroundTicks++;
                int despawnTicks = ModProjectiles.getMultishotCloneDespawnTicks(this.projectileItem);
                if (this.multishotGroundTicks >= despawnTicks && this.level() instanceof ServerLevel serverLevel) {
                    ModProjectiles.handleMultishotCloneBreak(serverLevel, this.projectileItem, this.getX(), this.getY(),
                            this.getZ(), this);
                }
            }
        } else if (this.isNoPhysics()) {
            this.entityData.set(ID_SPIN_TICKS, 0);
            this.multishotGroundTicks = 0;
        } else {
            this.entityData.set(ID_SPIN_TICKS, this.entityData.get(ID_SPIN_TICKS) + 1);
            this.multishotGroundTicks = 0;
        }
        if (this.ignoreHitTicks > 0) {
            this.ignoreHitTicks--;
            this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
            if (this.ignoreHitTicks <= 0) {
                this.lastHitEntityId = null;
            }
        }
        handleLoyaltyReturn();
        resetLoyaltyIfNoOwner();
        handleMobOwnerPickup();
        handleMobPickup();
        if (this.level().isClientSide) {
            handleParticleTrail();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (this.lastHitEntityId != null && target.getUUID().equals(this.lastHitEntityId) && this.ignoreHitTicks > 0) {
            return;
        }
        boolean shouldStop = this.piercedCount >= this.pierceLevel;
        DamageSource source = this.damageSources().arrow(this, this.getOwner() == null ? this : this.getOwner());
        float damage = calculateProjectileDamage(this.projectileItem, this.projectileDamage, this.isMultishotClone(),
                this.isCritical(), this.level(), this.position(), target);
        if (target.getType() == EntityType.ENDERMAN && target instanceof LivingEntity living) {
            living.hurt(source, damage);
            return;
        }
        if (target instanceof LivingEntity living && living.isBlocking()) {
            handleShieldBlock(living, source, damage, this.getOwner());
            activateIgnoreHits(target.getUUID());
            return;
        }
        boolean damageApplied = target.hurt(source, damage);
        this.playSound(getHitSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (!damageApplied) {
            this.entityData.set(ID_IMPACTED, true);
            this.setCritical(false);
            this.isStuck = true;
            ModProjectiles.reverseMovementAndRotation(this);
            activateIgnoreHits(target.getUUID());
            return;
        }
        if (target instanceof LivingEntity hitLiving) {
            if (shouldBreakOnEntityHit()) {
                handleItemBreak();
            }
            if (this.isCritical() && hitLiving.getType() != EntityType.ENDERMAN) {
                if (this.level() instanceof ServerLevel server) {
                    ModProjectiles.spawnCriticalEffects(server, hitLiving);
                }
            }
            if (!this.level().isClientSide && this.getOwner() instanceof Player ownerPlayer) {
                ItemStack hurtItem = !this.projectileItem.isEmpty() ? this.projectileItem : this.sourceItem;
                if (!hurtItem.isEmpty()) {
                    hurtItem.hurtEnemy(hitLiving, ownerPlayer);
                }
            }
            if (hitLiving.getType() != EntityType.ENDERMAN) {
                this.doPostHurtEffects(hitLiving);
            }
            if (!this.level().isClientSide) {
                if (!this.projectileItem.isEmpty()) {
                    this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
                    this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem);
                }
                if (!this.sourceItem.isEmpty()) {
                    this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
                    this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
                }
                if (!this.projectileItem.isEmpty() && this.projectileItem.isDamageableItem()
                        && this.projectileItem.getDamageValue() >= this.projectileItem.getMaxDamage()) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        ModProjectiles.handleItemBreak(serverLevel, this.projectileItem, this.getX(), this.getY(),
                                this.getZ(), this);
                    }
                    return;
                }
            }
            if (!hitLiving.isAlive()) {
                this.lastHitEntityId = hitLiving.getUUID();
            }
            applyKnockback(hitLiving);
            hitLiving.invulnerableTime = 0;
        }
        this.piercedCount++;
        this.activateIgnoreHits(target.getUUID());
        this.applyHitEffects(target, result);
        if (shouldStop) {
            this.setCritical(false);
            this.entityData.set(ID_IMPACTED, true);
            if (shouldBreakOnPiercingExhausted()) {
                handleItemBreak();
                return;
            }
            stopAfterPiercing();
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        ModProjectiles.applyWeaponEnchantmentEffects(entity, this.getOwner(), this.projectileItem);
        if (this.isOnFire()) {
            entity.setSecondsOnFire(5);
        }
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = this.level().getBlockState(pos);
        Direction hitFace = blockHitResult.getDirection();
        Vec3 vec3 = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.inGround = true;
        this.shakeTime = 3;
        this.hurtMarked = true;
        this.setCritArrow(false);
        this.setShotFromCrossbow(false);
        this.setCritical(false);
        this.lastHitEntityId = null;
        this.ignoreHitTicks = 0;
        this.piercedCount = 0;
        this.setNoGravity(false);
        this.noGravityTicks = 0;
        this.setHitFace(blockHitResult.getDirection());
        BlockState blockstate = this.level().getBlockState(blockHitResult.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, blockHitResult, this);
        boolean canBreakThisBlock = isCustomBreakableBlock(state);
        boolean reachedMaxBreaks = getBrokenBlocksCount() >= getMaxBlockBreaks();
        if (!this.level().isClientSide) {
            if (blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
                spawnBlockParticles(pos);
                spawnImpactParticles(blockHitResult);
                tryBreakBlock(pos, state, hitFace);
            }
            if (canBreakThisBlock) {
                this.entityData.set(ID_IMPACTED, true);
            }
            if (!canBreakThisBlock || reachedMaxBreaks) {
                this.entityData.set(ID_IMPACTED, true);
            }
            float finalSpinRotation = this.entityData.get(ID_SPIN_TICKS) * 60.0F;
            this.entityData.set(ID_LAST_SPIN_ROTATION, finalSpinRotation);
            this.getPersistentData().putFloat("LastSpinRotation", finalSpinRotation);
        }
        this.applyBlockHitEffects(pos, state, blockHitResult);
        this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (shouldBreakOnBlockHit()) {
            this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            handleProjectileBreak();
            return;
        }
        if (shouldBreakAfterMaxBlockBreaks() && reachedMaxBreaks) {
            handleProjectileBreak();
            return;
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.EMPTY;
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && (this.inGround || this.isNoPhysics())) {
            if (this.tryPickup(player)) {
                player.take(this, 1);
                this.discard();
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.getPersistentData().getBoolean("IsMultishotClone")) {
            return ItemStack.EMPTY;
        }
        if (this.projectileItem != null && !this.projectileItem.isEmpty()) {
            ItemStack pickup = this.projectileItem.copy();
            pickup.setCount(1);
            return pickup;
        }
        return ItemStack.EMPTY;
    }

    protected void syncPersistentDataToEntityData() {
        CompoundTag persistent = this.getPersistentData();
        if (persistent.contains("SourceItem", 10)) {
            ItemStack persistentSource = ItemStack.of(persistent.getCompound("SourceItem"));
            if (!ItemStack.matches(persistentSource, this.sourceItem) && !persistentSource.isEmpty()) {
                this.sourceItem = persistentSource.copy();
                this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
            }
        }
        if (persistent.contains("ProjectileItem", 10)) {
            ItemStack persistentProjectile = ItemStack.of(persistent.getCompound("ProjectileItem"));
            if (!ItemStack.matches(persistentProjectile, this.projectileItem) && !persistentProjectile.isEmpty()) {
                this.projectileItem = persistentProjectile.copy();
                this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem);
                updateEnchantmentData();
            }
        }
        if (persistent.contains("IsCritical")) {
            boolean nbtValue = persistent.getBoolean("IsCritical");
            if (nbtValue != this.entityData.get(ID_CRITICAL)) {
                this.entityData.set(ID_CRITICAL, nbtValue);
            }
        }
        if (persistent.contains("ProjectileDamage")) {
            float nbtDmg = persistent.getFloat("ProjectileDamage");
            if (nbtDmg != this.projectileDamage) {
                this.projectileDamage = nbtDmg;
            }
        }
        if (persistent.contains("ProjectileKnockback")) {
            float nbtKb = persistent.getFloat("ProjectileKnockback");
            if (nbtKb != this.projectileKnockback) {
                this.projectileKnockback = nbtKb;
            }
        }
        if (persistent.contains("IgnoreHitTicks")) {
            int nbtTicks = persistent.getInt("IgnoreHitTicks");
            if (nbtTicks != this.ignoreHitTicks) {
                this.ignoreHitTicks = nbtTicks;
                this.entityData.set(ID_IGNORE_HIT_TICKS, nbtTicks);
            }
        }
        if (persistent.contains("PierceLevel")) {
            int nbtPierce = persistent.getInt("PierceLevel");
            if (nbtPierce != this.pierceLevel) {
                this.pierceLevel = nbtPierce;
                this.entityData.set(ID_PIERCING, this.pierceLevel);
            }
        }
        if (persistent.contains("PiercedCount")) {
            int nbtPierced = persistent.getInt("PiercedCount");
            if (nbtPierced != this.piercedCount) {
                this.piercedCount = nbtPierced;
            }
        }
        if (persistent.contains("BrokenBlocksCount")) {
            int nbtCount = persistent.getInt("BrokenBlocksCount");
            if (nbtCount != this.brokenBlocksCount) {
                this.brokenBlocksCount = nbtCount;
            }
        }
        if (persistent.contains("Color")) {
            int nbtColor = persistent.getInt("Color");
            if (nbtColor != this.color) {
                this.color = nbtColor;
                this.entityData.set(ID_COLOR, this.color);
            }
        }
    }

    protected void handleParticleTrail() {
        if (level().isClientSide && !inGround && !isInLava()) {
            if (waveParticleCount < 3 && ticksInAir % 4 == 0) {
                float[] rgb = varyRGB(random, getColor());
                Vec3 motion = getDeltaMovement();
                double offsetScale = 0.1D;
                double offsetX = motion.x * offsetScale + (random.nextDouble() - 0.5D) * 0.05D;
                double offsetY = motion.y * offsetScale + (random.nextDouble() - 0.5D) * 0.05D + 0.2D;
                double offsetZ = motion.z * offsetScale + (random.nextDouble() - 0.5D) * 0.05D;
                SmallWaveParticleData particle = new SmallWaveParticleData(rgb[0], rgb[1], rgb[2], 0.25F);
                level().addParticle(particle, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, 0.0D, 0.0D, 0.0D);
                waveParticleCount++;
            }
        }
    }

    protected void spawnImpactParticles(BlockHitResult hitResult) {
        if (!(this.level() instanceof ServerLevel serverLevel))
            return;
        BlockPos pos = hitResult.getBlockPos();
        Direction face = hitResult.getDirection();
        double offset = 0.01;
        double x = pos.getX() + 0.5 + (face.getStepX() * (0.5 + offset));
        double y = pos.getY() + 0.5 + (face.getStepY() * (0.5 + offset));
        double z = pos.getZ() + 0.5 + (face.getStepZ() * (0.5 + offset));
        ItemStack projStack = this.getProjectileItem();
        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, projStack), x, y, z, 4, 0.1, 0.1, 0.1,
                0.05);
    }

    protected boolean tryBreakBlock(BlockPos pos, BlockState state, Direction hitFace) {
        if (this.level().isClientSide) {
            return false;
        }
        int max = getMaxBlockBreaks();
        int current = getBrokenBlocksCount();
        if (current >= max) {
            return false;
        }
        boolean broke = false;
        boolean canBreak = isCustomBreakableBlockData(state) || isCustomBreakableBlock(state);
        if (canBreak) {
            this.level().destroyBlock(pos, true, this);
            current++;
            broke = true;
        }
        if (current < max) {
            BlockPos adjacent = pos.relative(hitFace);
            BlockState adjState = this.level().getBlockState(adjacent);
            if (adjState.is(Blocks.COBWEB)) {
                this.level().destroyBlock(adjacent, true);
                current++;
                broke = true;
            }
        }
        if (broke) {
            this.brokenBlocksCount = current;
            this.getPersistentData().putInt("BrokenBlocksCount", current);
        }
        return broke;
    }

    protected boolean isCustomBreakableBlockData(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        CompoundTag data = this.getPersistentData();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (!data.contains("ProjectileAllowedBreakBlocks")) {
            return false;
        }
        List<String> rules = new ArrayList<>();
        if (data.contains("ProjectileAllowedBreakBlocks", Tag.TAG_LIST)) {
            ListTag list = data.getList("ProjectileAllowedBreakBlocks", Tag.TAG_STRING);
            for (Tag t : list) {
                String s = t.getAsString().trim();
                if (!s.isEmpty()) {
                    rules.add(s);
                }
            }
        } else if (data.contains("ProjectileAllowedBreakBlocks", Tag.TAG_STRING)) {
            String value = data.getString("ProjectileAllowedBreakBlocks").trim();
            if (value.isEmpty()) {
                return false;
            }
            if (value.startsWith("[") && value.endsWith("]")) {
                value = value.substring(1, value.length() - 1);
            }
            for (String s : value.split(",")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    rules.add(trimmed);
                }
            }
        }
        if (rules.isEmpty()) {
            return false;
        }
        boolean hasAny = false;
        for (String r : rules) {
            if (r.equalsIgnoreCase("any")) {
                hasAny = true;
                break;
            }
        }
        if (hasAny) {
            for (String rule : rules) {
                String r = rule.trim();
                if (!r.startsWith("!"))
                    continue;
                String clean = r.substring(1).trim();
                boolean matches = false;
                if (clean.startsWith("#")) {
                    ResourceLocation tagLoc = ResourceLocation.tryParse(clean.substring(1));
                    if (tagLoc != null) {
                        matches = state.is(TagKey.create(Registries.BLOCK, tagLoc));
                    }
                } else {
                    ResourceLocation ruleLoc = ResourceLocation.tryParse(clean);
                    matches = ruleLoc != null && ruleLoc.equals(blockId);
                }
                if (matches) {
                    return false;
                }
            }
            return true;
        } else {
            for (String rule : rules) {
                String r = rule.trim();
                boolean negated = r.startsWith("!");
                String clean = negated ? r.substring(1).trim() : r;
                boolean matches = false;
                if (clean.startsWith("#")) {
                    ResourceLocation tagLoc = ResourceLocation.tryParse(clean.substring(1));
                    if (tagLoc != null) {
                        matches = state.is(TagKey.create(Registries.BLOCK, tagLoc));
                    }
                } else {
                    ResourceLocation ruleLoc = ResourceLocation.tryParse(clean);
                    matches = ruleLoc != null && ruleLoc.equals(blockId);
                }
                if (matches) {
                    return !negated;
                }
            }
            return false;
        }
    }

    protected boolean isCustomBreakableBlock(BlockState state) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return blockId.equals(new ResourceLocation("minecraft:pointed_dripstone"));
    }

    public void setBreakableBlocks(String... rules) {
        if (rules == null || rules.length == 0) {
            this.getPersistentData().remove("ProjectileAllowedBreakBlocks");
            return;
        }
        CompoundTag data = this.getPersistentData();
        ListTag list = new ListTag();
        for (String rule : rules) {
            if (rule != null && !rule.trim().isEmpty()) {
                list.add(StringTag.valueOf(rule.trim()));
            }
        }
        data.put("ProjectileAllowedBreakBlocks", list);
    }

    public void handleProjectileBreak() {
        if (!this.level().isClientSide && !this.projectileItem.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.projectileItem.copy()),
                    this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.1D, 0.1D, 0.1D, 0.05D);
        }
        this.discard();
    }

    public void spawnBlockParticles(BlockPos pos) {
        if (this.level().isClientSide)
            return;
        BlockState state = level().getBlockState(pos);
        for (ServerPlayer player : ((ServerLevel) level()).players()) {
            if (player.distanceToSqr(this.getX(), this.getY(), this.getZ()) < 256.0D) {
                ((ServerLevel) level()).sendParticles(player,
                        new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos), true, getX(), getY(), getZ(),
                        6, 0.1D, 0.1D, 0.1D, 0.05D);
            }
        }
    }

    public void handleItemBreak() {
        if (!this.level().isClientSide && !this.projectileItem.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.projectileItem.copy()),
                    this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.1D, 0.1D, 0.1D, 0.05D);
        }
        this.discard();
    }

    protected float[] hexToRGB(int hexColor) {
        if (hexColor == 0) {
            return new float[] { 1.0F, 1.0F, 1.0F };
        }
        return new float[] { ((hexColor >> 16) & 0xFF) / 255.0F, ((hexColor >> 8) & 0xFF) / 255.0F,
                (hexColor & 0xFF) / 255.0F };
    }

    protected float[] varyRGB(RandomSource random, int hexColor) {
        float[] rgb = hexToRGB(hexColor);
        float rVariation = random.nextFloat() * 0.3F - 0.15F;
        float gVariation = random.nextFloat() * 0.3F - 0.15F;
        float bVariation = random.nextFloat() * 0.3F - 0.15F;
        return new float[] { Mth.clamp(rgb[0] + rVariation, 0.0F, 1.0F), Mth.clamp(rgb[1] + gVariation, 0.0F, 1.0F),
                Mth.clamp(rgb[2] + bVariation, 0.0F, 1.0F) };
    }

    protected boolean shouldMaintainNoGravity() {
        if (this.inGround)
            return false;
        if (this.hasImpacted())
            return false;
        if (this.piercedCount < this.pierceLevel)
            return true;
        return hasInitialNoGravity() && !this.hasImpacted();
    }

    public int getPiercingLevel() {
        return this.pierceLevel;
    }

    public int getPiercedCount() {
        return this.piercedCount;
    }

    public void setPiercedCount(int count) {
        this.piercedCount = Math.max(0, count);
        this.getPersistentData().putInt("PiercedCount", this.piercedCount);
    }

    public void resetPiercedCount() {
        this.piercedCount = 0;
        this.getPersistentData().putInt("PiercedCount", 0);
    }

    public void setPiercingLevel(int level) {
        this.pierceLevel = Math.max(0, level);
        this.getPersistentData().putInt("PierceLevel", this.pierceLevel);
        this.entityData.set(ID_PIERCING, this.pierceLevel);
    }

    protected void applyKnockback(LivingEntity target) {
        double knockbackResistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double knockbackFactor = 1.0 - Math.min(knockbackResistance, 1.0);
        if (knockbackFactor <= 0)
            return;
        float baseKnockback = this.projectileKnockback;
        int knockbackLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.KNOCKBACK, this.projectileItem);
        if (knockbackLevel > 0) {
            baseKnockback += knockbackLevel * 0.8F;
        }
        float knockbackStrength = baseKnockback * 0.5F;
        if (this.isCritical()) {
            knockbackStrength += 0.6F;
        }
        if (knockbackStrength > 0.0F) {
            target.knockback(knockbackStrength * (float) knockbackFactor, -this.getDeltaMovement().x,
                    -this.getDeltaMovement().z);
        }
    }

    private void stopAfterPiercing() {
        ModProjectiles.reverseMovementAndRotation(this);
        this.setCritical(false);
    }

    private void handleShieldBlock(LivingEntity target, DamageSource source, float damage, Entity owner) {
        boolean canDisable = owner instanceof LivingEntity livingOwner
                && canDisableShield(target.getUseItem(), target, livingOwner);
        target.hurt(source, damage);
        if (canDisable) {
            ModProjectiles.handleBlocking(target, owner, this.projectileItem, this.level(), this,
                    getShieldDisableCooldownTicks());
        } else {
            this.setCritical(false);
            this.playSound(getHitSound(), 1.0F, 1.0F);
            ModProjectiles.reverseMovementAndRotation(this);
        }
    }

    protected void applyHitEffects(Entity entity, EntityHitResult hitResult) {
    }

    protected void applyBlockHitEffects(BlockPos pos, BlockState state, BlockHitResult hitResult) {
    }

    protected float calculateProjectileDamage(ItemStack projectileItem, float baseWeaponDamage,
            boolean isMultishotClone, boolean isCritical, Level level, Vec3 projectilePos, Entity hitEntity) {
        float itemAttackDamage = ModProjectiles.getItemAttackDamage(projectileItem);
        float baseDamage = baseWeaponDamage + itemAttackDamage + 1.0F;
        if (isMultishotClone) {
            baseDamage *= 0.5F;
        }
        if (hitEntity instanceof LivingEntity livingEntity) {
            baseDamage += EnchantmentHelper.getDamageBonus(projectileItem, livingEntity.getMobType());
            int powerLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, projectileItem);
            baseDamage += powerLevel * 1.5F;
            if (isCritical) {
                baseDamage *= 1.5F;
            }
            float distanceSq = (float) projectilePos.distanceToSqr(livingEntity.position());
            if (level.getRandom().nextFloat() < 0.2F && distanceSq > 25.0F) {
                baseDamage *= 1.5F;
            }
        }
        return baseDamage;
    }

    protected void handleLoyaltyReturn() {
        if (this.getPersistentData().getBoolean("IsMultishotClone"))
            return;
        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (loyalty <= 0 || owner == null || !this.entityData.get(ID_IMPACTED)) {
            return;
        }
        if (!ModProjectiles.isAcceptableLoyaltyReturnOwner(owner)) {
            if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                ModProjectiles.dropAsItem(this.level(), this.getPickupItem(), this.getX(), this.getY(), this.getZ());
            }
            this.discard();
            return;
        }
        this.setNoPhysics(true);
        this.entityData.set(ID_SPIN_TICKS, 0);
        Vec3 toOwner = owner.getEyePosition().subtract(this.position());
        this.setPosRaw(this.getX(), this.getY() + toOwner.y * 0.015D * loyalty, this.getZ());
        if (this.level().isClientSide) {
            this.yOld = this.getY();
        }
        double pullSpeed = 0.05D * loyalty;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(toOwner.normalize().scale(pullSpeed)));
        if (this.clientSideReturnTridentTickCount == 0) {
            this.playSound(getLoyaltySound(), 10.0F, 1.0F);
        }
        this.clientSideReturnTridentTickCount++;
    }

    protected void resetLoyaltyIfNoOwner() {
        if (!this.level().isClientSide && this.entityData.get(ID_LOYALTY) > 0 && this.getOwner() == null) {
            this.setNoPhysics(false);
            this.clientSideReturnTridentTickCount = 0;
        }
    }

    protected void handleMobPickup() {
        if (!this.inGround || !(this.getOwner() instanceof Mob mob))
            return;
        if (this.position().distanceTo(mob.position()) <= 1.5D) {
            ItemStack pickup = this.getPickupItem().copy();
            pickup.setCount(1);
            if (ModProjectiles.tryMobPickup(pickup, mob, this.level())) {
                this.discard();
            }
        }
    }

    protected void handleMobOwnerPickup() {
        if (this.level().isClientSide || this.entityData.get(ID_LOYALTY) <= 0)
            return;
        if (this.getOwner() instanceof Mob mob) {
            AABB box = mob.getBoundingBox().inflate(0.3D);
            if (box.contains(this.position())) {
                ItemStack pickup = this.getPickupItem().copy();
                pickup.setCount(1);
                if (ModProjectiles.tryMobPickup(pickup, mob, this.level())) {
                    this.discard();
                }
            }
        }
    }

    protected void initializeSourceItem(@Nullable ItemStack source) {
        this.sourceItem = (source != null && !source.isEmpty()) ? source.copy() : getDefaultSourceItem();
        this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
        this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
    }

    protected void initializeProjectileItem(@Nullable ItemStack source) {
        if (source != null && !source.isEmpty()) {
            this.projectileItem = source.copy();
        } else {
            this.projectileItem = getDefaultProjectileItem();
        }
        if (this.projectileItem == null || this.projectileItem.isEmpty()) {
            this.projectileItem = new ItemStack(Items.AIR);
        }
        this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem.copy());
        this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
    }

    public void setSourceItem(@Nullable ItemStack newSource) {
        this.sourceItem = (newSource != null && !newSource.isEmpty()) ? newSource.copy() : getDefaultSourceItem();
        if (!ItemStack.matches(this.entityData.get(ID_SOURCE_ITEM), this.sourceItem)) {
            this.entityData.set(ID_SOURCE_ITEM, this.sourceItem);
            this.getPersistentData().put("SourceItem", this.sourceItem.save(new CompoundTag()));
        }
    }

    public void setProjectileItem(ItemStack newProjectileItem) {
        this.projectileItem = (newProjectileItem != null && !newProjectileItem.isEmpty()) ? newProjectileItem.copy()
                : getDefaultProjectileItem();
        if (this.projectileItem.isEmpty()) {
            this.projectileItem = new ItemStack(Items.AIR);
        }
        if (!ItemStack.matches(this.entityData.get(ID_PROJECTILE_ITEM), this.projectileItem)) {
            this.entityData.set(ID_PROJECTILE_ITEM, this.projectileItem.copy());
            this.getPersistentData().put("ProjectileItem", this.projectileItem.save(new CompoundTag()));
            updateEnchantmentData();
            updateCustomNameFromItem();
        }
    }

    public ItemStack getSourceItem() {
        return this.sourceItem != null && !this.sourceItem.isEmpty() ? this.sourceItem.copy() : getDefaultSourceItem();
    }

    public ItemStack getProjectileItem() {
        return this.projectileItem != null && !this.projectileItem.isEmpty() ? this.projectileItem.copy()
                : getDefaultProjectileItem();
    }

    public void updateEnchantmentData() {
        if (this.projectileItem.isEmpty())
            return;
        this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.projectileItem));
        this.entityData.set(ID_FOIL, this.projectileItem.hasFoil());
        int enchantPierce = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, this.projectileItem);
        this.pierceLevel = Math.max(this.pierceLevel, enchantPierce);
        this.entityData.set(ID_PIERCING, this.pierceLevel);
    }

    public void setProjectileDamage(float damage) {
        this.projectileDamage = damage;
        this.getPersistentData().putFloat("ProjectileDamage", damage);
    }

    public void setProjectileKnockback(float strength) {
        this.projectileKnockback = strength;
        this.getPersistentData().putFloat("ProjectileKnockback", strength);
    }

    public void setCritical(boolean critical) {
        this.entityData.set(ID_CRITICAL, critical);
        this.getPersistentData().putBoolean("IsCritical", critical);
    }

    public void updateCustomNameFromItem() {
        if (this.projectileItem.isEmpty()) {
            this.setCustomName(null);
            this.setCustomNameVisible(false);
            return;
        }
        Component name = this.projectileItem.getHoverName();
        ChatFormatting color = switch (this.projectileItem.getRarity()) {
            case COMMON -> ChatFormatting.WHITE;
            case UNCOMMON -> ChatFormatting.YELLOW;
            case RARE -> ChatFormatting.AQUA;
            case EPIC -> ChatFormatting.LIGHT_PURPLE;
        };
        this.setCustomName(name.copy().withStyle(color));
        this.setCustomNameVisible(false);
    }

    public boolean isCritical() {
        return this.entityData.get(ID_CRITICAL);
    }

    public boolean hasImpacted() {
        return this.entityData.get(ID_IMPACTED);
    }

    public void activateIgnoreHits(UUID hitEntityId) {
        if (hitEntityId == null)
            return;
        this.lastHitEntityId = hitEntityId;
        this.ignoreHitTicks = getIgnoreHitTicks();
        this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
        this.getPersistentData().putInt("IgnoreHitTicks", this.ignoreHitTicks);
    }

    public int getIgnoreHitTicks() {
        return 10;
    }

    public int getShieldDisableCooldownTicks() {
        return 0;
    }

    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return false;
    }

    public void setColor(int color) {
        this.color = color;
        this.entityData.set(ID_COLOR, color);
        this.getPersistentData().putInt("Color", this.color);
    }

    public int getColor() {
        return this.entityData.get(ID_COLOR);
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    public int getSpinTicks() {
        return this.entityData.get(ID_SPIN_TICKS);
    }

    public float getLastSpinRotation() {
        return this.entityData.get(ID_LAST_SPIN_ROTATION);
    }

    public boolean isMultishotClone() {
        return this.getPersistentData().getBoolean("IsMultishotClone");
    }

    public void setHitFace(Direction face) {
        this.hitFace = face;
    }

    public Direction getHitFace() {
        return hitFace;
    }

    public int getBrokenBlocksCount() {
        return this.brokenBlocksCount;
    }

    public void resetBrokenBlocksCount() {
        this.brokenBlocksCount = 0;
        this.getPersistentData().putInt("BrokenBlocksCount", 0);
    }
}
