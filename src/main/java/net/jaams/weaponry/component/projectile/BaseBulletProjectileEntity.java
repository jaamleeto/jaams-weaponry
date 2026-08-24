package net.jaams.weaponry.component.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.jaams.weaponry.particle.SmallWaveParticleData;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public abstract class BaseBulletProjectileEntity extends AbstractArrow implements IEntityAdditionalSpawnData {

    public static final EntityDataAccessor<Boolean> ID_IMPACTED = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> ID_GUN_ITEM = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> ID_CRITICAL = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ID_IGNORE_HIT_TICKS = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ID_PIERCING = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ID_COLOR = SynchedEntityData
            .defineId(BaseBulletProjectileEntity.class, EntityDataSerializers.INT);
    protected ItemStack gunItem;
    protected ItemStack bulletItem;
    protected float bulletDamage;
    protected float bulletKnockback;
    protected int life = 0;
    protected int ignoreHitTicks = 0;
    protected int pierceLevel = 0;
    protected int piercedCount = 0;
    protected int ticksInAir = 0;
    protected int ticksInGround = 0;
    protected int bubbleTime = 0;
    protected int brokenBlocksCount = 0;
    protected int noGravityTicks = 0;
    protected int waveParticleCount = 0;
    protected int color = 0xFFFFFF;
    protected boolean isStuck = false;

    @Nullable
    protected UUID lastHitEntityId;

    private Direction hitFace = Direction.NORTH;

    protected BaseBulletProjectileEntity(EntityType<? extends BaseBulletProjectileEntity> type, Level level) {
        super(type, level);
        initializeGunItem(null);
        initializeBulletItem();
    }

    protected BaseBulletProjectileEntity(EntityType<? extends BaseBulletProjectileEntity> type, double x, double y,
            double z, Level level) {
        super(type, x, y, z, level);
        initializeGunItem(null);
        initializeBulletItem();
    }

    protected BaseBulletProjectileEntity(EntityType<? extends BaseBulletProjectileEntity> type, LivingEntity shooter,
            Level level) {
        super(type, shooter, level);
        initializeGunItem(null);
        initializeBulletItem();
    }

    protected BaseBulletProjectileEntity(EntityType<? extends BaseBulletProjectileEntity> type, LivingEntity shooter,
            Level level, @Nullable ItemStack gunItem) {
        super(type, shooter, level);
        initializeGunItem(gunItem);
        this.bulletDamage = 0.0F;
        this.bulletKnockback = 0.0F;
        this.entityData.set(ID_CRITICAL, isCritical());
        this.entityData.set(ID_IGNORE_HIT_TICKS, 0);
        this.entityData.set(ID_PIERCING, 0);
        this.entityData.set(ID_COLOR, 0xFFFFFF);
        this.brokenBlocksCount = 0;
        this.noGravityTicks = 0;
        updateGunEnchantmentData();
        initializeBulletItem();
    }

    public abstract ItemStack getDefaultGunItem();

    public abstract ItemStack getDefaultBulletItem();

    protected abstract SoundEvent getHitSound();

    protected abstract SoundEvent getGroundSound();

    public abstract int getMaxTicksInAir();

    public abstract int getMaxTicksInGround();

    public abstract int getNoGravityDuration();

    public abstract boolean hasInitialNoGravity();

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_IMPACTED, false);
        this.entityData.define(ID_GUN_ITEM, ItemStack.EMPTY);
        this.entityData.define(ID_CRITICAL, false);
        this.entityData.define(ID_IGNORE_HIT_TICKS, 0);
        this.entityData.define(ID_PIERCING, 0);
        this.entityData.define(ID_COLOR, 0xFFFFFF);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("GunItem", 10)) {
            this.gunItem = ItemStack.of(tag.getCompound("GunItem"));
            if (this.gunItem.isEmpty()) {
                this.gunItem = getDefaultGunItem();
            }
        } else {
            this.gunItem = getDefaultGunItem();
        }
        this.entityData.set(ID_GUN_ITEM, this.gunItem);
        this.getPersistentData().put("GunItem", this.gunItem.save(new CompoundTag()));
        this.entityData.set(ID_IMPACTED, tag.getBoolean("HasImpacted"));
        this.entityData.set(ID_CRITICAL, tag.getBoolean("IsCritical"));
        this.bulletDamage = tag.getFloat("BulletDamage");
        this.bulletKnockback = tag.getFloat("BulletKnockback");
        this.ignoreHitTicks = tag.getInt("IgnoreHitTicks");
        this.pierceLevel = tag.getInt("PierceLevel");
        this.piercedCount = tag.getInt("PiercedCount");
        this.brokenBlocksCount = tag.getInt("BrokenBlocksCount");
        this.color = tag.getInt("Color");
        if (tag.contains("LastHitEntityId")) {
            this.lastHitEntityId = tag.getUUID("LastHitEntityId");
        }
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.entityData.set(ID_COLOR, this.color);
        updateGunEnchantmentData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.gunItem.isEmpty()) {
            tag.put("GunItem", this.gunItem.save(new CompoundTag()));
        }
        tag.putBoolean("HasImpacted", this.entityData.get(ID_IMPACTED));
        tag.putBoolean("IsCritical", isCritical());
        tag.putFloat("BulletDamage", this.bulletDamage);
        tag.putFloat("BulletKnockback", this.bulletKnockback);
        tag.putInt("IgnoreHitTicks", this.ignoreHitTicks);
        tag.putInt("PierceLevel", this.pierceLevel);
        tag.putInt("PiercedCount", this.piercedCount);
        tag.putInt("BrokenBlocksCount", this.brokenBlocksCount);
        tag.putInt("Color", this.color);
        if (this.lastHitEntityId != null) {
            tag.putUUID("LastHitEntityId", this.lastHitEntityId);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeItem(this.gunItem);
        buffer.writeFloat(this.bulletDamage);
        buffer.writeBoolean(isCritical());
        buffer.writeInt(this.ignoreHitTicks);
        buffer.writeInt(this.pierceLevel);
        buffer.writeInt(this.brokenBlocksCount);
        buffer.writeInt(this.color);
        buffer.writeBoolean(this.lastHitEntityId != null);
        if (this.lastHitEntityId != null) {
            buffer.writeUUID(this.lastHitEntityId);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        this.gunItem = buf.readItem();
        if (this.gunItem.isEmpty()) {
            this.gunItem = getDefaultGunItem();
        }
        this.entityData.set(ID_GUN_ITEM, this.gunItem);
        this.getPersistentData().put("GunItem", this.gunItem.save(new CompoundTag()));
        this.bulletDamage = buf.readFloat();
        this.setCritical(buf.readBoolean());
        this.ignoreHitTicks = buf.readInt();
        this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
        this.pierceLevel = buf.readInt();
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.brokenBlocksCount = buf.readInt();
        this.color = buf.readInt();
        this.entityData.set(ID_COLOR, this.color);
        boolean hasLastHit = buf.readBoolean();
        this.lastHitEntityId = hasLastHit ? buf.readUUID() : null;
        updateGunEnchantmentData();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ID_GUN_ITEM.equals(key)) {
            ItemStack newGunItem = this.entityData.get(ID_GUN_ITEM);
            if (!ItemStack.matches(this.gunItem, newGunItem)) {
                this.gunItem = newGunItem.isEmpty() ? getDefaultGunItem() : newGunItem.copy();
                updateGunEnchantmentData();
                this.getPersistentData().put("GunItem", this.gunItem.save(new CompoundTag()));
            }
        } else if (ID_IGNORE_HIT_TICKS.equals(key)) {
            this.ignoreHitTicks = this.entityData.get(ID_IGNORE_HIT_TICKS);
        } else if (ID_PIERCING.equals(key)) {
            this.pierceLevel = this.entityData.get(ID_PIERCING);
        } else if (ID_COLOR.equals(key)) {
            this.color = this.entityData.get(ID_COLOR);
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
            ServerLevel serverWorld = (ServerLevel) this.level();
            if (isInLava()) {
                discardWithSmoke(serverWorld);
                return;
            }
            if (isInWater()) {
                if (!inGround) {
                    bubbleTime++;
                    if (bubbleTime >= 200) {
                        discardWithSmoke(serverWorld);
                        return;
                    }
                } else {
                    bubbleTime = 0;
                    ticksInGround++;
                    if (ticksInGround >= getMaxTicksInGround() && getMaxTicksInGround() > 0) {
                        discardWithSmoke(serverWorld);
                        return;
                    }
                }
            } else if (!inGround) {
                ticksInAir++;
                if (ticksInAir >= getMaxTicksInAir() && getMaxTicksInAir() > 0) {
                    discardWithSmoke(serverWorld);
                    return;
                }
            } else {
                ticksInGround++;
                if (ticksInGround >= getMaxTicksInGround() && getMaxTicksInGround() > 0) {
                    discardWithSmoke(serverWorld);
                    return;
                }
            }
        }
        if (this.inGroundTime > 1 && !this.entityData.get(ID_IMPACTED)) {
            this.entityData.set(ID_IMPACTED, true);
        }
        if (this.ignoreHitTicks > 0) {
            this.ignoreHitTicks--;
            this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
            if (this.ignoreHitTicks <= 0) {
                this.lastHitEntityId = null;
            }
        }
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
        float damage = this.bulletDamage;
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
                handleBulletBreak();
            }
            if (this.isCritical() && hitLiving.getType() != EntityType.ENDERMAN) {
                if (this.level() instanceof ServerLevel server) {
                    ModProjectiles.spawnCriticalEffects(server, hitLiving);
                }
            }
            if (hitLiving.getType() != EntityType.ENDERMAN) {
                if (this.level() instanceof ServerLevel server) {
                    spawnHitParticles(server);
                }
                this.doPostHurtEffects(hitLiving);
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
                handleBulletBreak();
                return;
            }
            stopAfterPiercing();
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        ModProjectiles.applyWeaponEnchantmentEffects(entity, this.getOwner(), this.gunItem);
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
        }
        this.applyBlockHitEffects(pos, state, blockHitResult);
        this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (shouldBreakOnBlockHit()) {
            this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            handleBulletBreak();
            return;
        }
        if (shouldBreakAfterMaxBlockBreaks() && reachedMaxBreaks) {
            handleBulletBreak();
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
        return ItemStack.EMPTY;
    }

    protected void syncPersistentDataToEntityData() {
        CompoundTag persistent = this.getPersistentData();
        if (persistent.contains("GunItem", 10)) {
            ItemStack persistentItem = ItemStack.of(persistent.getCompound("GunItem"));
            if (!ItemStack.matches(persistentItem, this.gunItem) && !persistentItem.isEmpty()) {
                this.gunItem = persistentItem.copy();
                this.entityData.set(ID_GUN_ITEM, this.gunItem);
                updateGunEnchantmentData();
            }
        }
        if (persistent.contains("IsCritical")) {
            boolean nbtValue = persistent.getBoolean("IsCritical");
            if (nbtValue != this.entityData.get(ID_CRITICAL)) {
                this.entityData.set(ID_CRITICAL, nbtValue);
            }
        }
        if (persistent.contains("BulletDamage")) {
            float nbtDmg = persistent.getFloat("BulletDamage");
            if (nbtDmg != this.bulletDamage) {
                this.bulletDamage = nbtDmg;
            }
        }
        if (persistent.contains("BulletKnockback")) {
            float nbtKb = persistent.getFloat("BulletKnockback");
            if (nbtKb != this.bulletKnockback) {
                this.bulletKnockback = nbtKb;
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
            if (ticksInAir <= 4) {
                Vec3 motion = getDeltaMovement().normalize();
                double offset = 0.5;
                double smokeX = getX() - motion.x * offset;
                double smokeY = getY() + 0.17D - motion.y * offset;
                double smokeZ = getZ() - motion.z * offset;
                level().addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
            }
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

    protected void spawnHitParticles(ServerLevel serverWorld) {
        float[] rgb = varyRGB(serverWorld.random, getColor());
        float maxScale = 0.20F;
        float minScale = 0.05F;
        float size = minScale + serverWorld.random.nextFloat() * (maxScale - minScale);
        Entity owner = getOwner();
        if (owner != null) {
            double distanceSqr = owner.distanceToSqr(this);
            double maxDistanceSqr = 64.0D;
            float distanceFactor = (float) Math.min(distanceSqr / maxDistanceSqr, 1.0D);
            size = minScale + (maxScale - minScale) * distanceFactor * (0.5F + serverWorld.random.nextFloat() * 0.5F);
        }
        serverWorld.sendParticles(new CustomHitParticleData(rgb[0], rgb[1], rgb[2], size), getX(), getY(), getZ(), 1,
                0.0D, 0.0D, 0.0D, 0.0D);
        serverWorld.sendParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 2, 0.1D, 0.1D, 0.1D, 0.02D);
    }

    protected void spawnImpactParticles(BlockHitResult hitResult) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = hitResult.getBlockPos();
        Direction face = hitResult.getDirection();
        double offset = 0.01;
        double x = pos.getX() + 0.5 + (face.getStepX() * (0.5 + offset));
        double y = pos.getY() + 0.5 + (face.getStepY() * (0.5 + offset));
        double z = pos.getZ() + 0.5 + (face.getStepZ() * (0.5 + offset));
        ItemStack bulletStack = this.getBulletItem();
        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, bulletStack), x, y, z, 4, 0.1, 0.1, 0.1,
                0.05);
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 6, 0.1, 0.1, 0.1, 0.05);
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
        return blockId.equals(ResourceLocation.parse("minecraft:pointed_dripstone"));
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

    public void handleBulletBreak() {
        if (!this.level().isClientSide && !this.bulletItem.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.bulletItem.copy()), this.getX(),
                    this.getY() + 0.5, this.getZ(), 5, 0.1D, 0.1D, 0.1D, 0.05D);
        }
        this.discard();
    }

    public void spawnBlockParticles(BlockPos pos) {
        if (this.level().isClientSide) {
            return;
        }
        BlockState state = level().getBlockState(pos);
        for (ServerPlayer player : ((ServerLevel) level()).players()) {
            if (player.distanceToSqr(this.getX(), this.getY(), this.getZ()) < 256.0D) {
                ((ServerLevel) level()).sendParticles(player,
                        new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos), true, getX(), getY(), getZ(),
                        6, 0.1D, 0.1D, 0.1D, 0.05D);
            }
        }
    }

    private void discardWithSmoke(ServerLevel serverWorld) {
        serverWorld.sendParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        discard();
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
        if (this.inGround) {
            return false;
        }
        if (this.hasImpacted()) {
            return false;
        }
        if (this.piercedCount < this.pierceLevel) {
            return true;
        }
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
        float baseKnockback = this.bulletKnockback;
        int knockbackLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.KNOCKBACK, this.gunItem);
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
            ModProjectiles.handleBlocking(target, owner, this.gunItem, this.level(), this,
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

    public void initializeGunItem(@Nullable ItemStack gunItem) {
        this.gunItem = gunItem != null && !gunItem.isEmpty() ? gunItem.copy() : getDefaultGunItem();
        this.entityData.set(ID_GUN_ITEM, this.gunItem);
        this.getPersistentData().put("GunItem", this.gunItem.save(new CompoundTag()));
    }

    public void setGunItem(@Nullable ItemStack newGunItem) {
        this.gunItem = newGunItem != null && !newGunItem.isEmpty() ? newGunItem.copy() : getDefaultGunItem();
        if (!ItemStack.matches(this.entityData.get(ID_GUN_ITEM), this.gunItem)) {
            this.entityData.set(ID_GUN_ITEM, this.gunItem);
            this.getPersistentData().put("GunItem", this.gunItem.save(new CompoundTag()));
            updateGunEnchantmentData();
        }
    }

    @Nullable
    public ItemStack getGunItem() {
        return this.gunItem.copy();
    }

    protected void initializeBulletItem() {
        ItemStack defaultBullet = getDefaultBulletItem();
        this.bulletItem = (defaultBullet != null && !defaultBullet.isEmpty()) ? defaultBullet.copy()
                : new ItemStack(BottomItems.BULLET.get());
    }

    public ItemStack getBulletItem() {
        return this.bulletItem.copy();
    }

    public void setBulletItem(ItemStack bulletItem) {
        this.bulletItem = (bulletItem != null && !bulletItem.isEmpty()) ? bulletItem.copy()
                : new ItemStack(BottomItems.BULLET.get());
    }

    public void updateGunEnchantmentData() {
        if (this.gunItem.isEmpty())
            return;
        int enchantPierce = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, this.gunItem);
        this.pierceLevel = Math.max(this.pierceLevel, enchantPierce);
        this.entityData.set(ID_PIERCING, this.pierceLevel);
    }

    public void setBulletDamage(float damage) {
        this.bulletDamage = damage;
        this.getPersistentData().putFloat("BulletDamage", damage);
    }

    public void setBulletKnockback(float strength) {
        this.bulletKnockback = strength;
        this.getPersistentData().putFloat("BulletKnockback", strength);
    }

    public void setCritical(boolean critical) {
        this.entityData.set(ID_CRITICAL, critical);
        this.getPersistentData().putBoolean("IsCritical", critical);
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

    protected boolean shouldBreakOnEntityHit() {
        return true;
    }

    protected boolean shouldBreakOnBlockHit() {
        return false;
    }

    protected boolean shouldBreakOnPiercingExhausted() {
        return false;
    }

    public int getMaxBlockBreaks() {
        return 1;
    }

    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return false;
    }

    public int getBrokenBlocksCount() {
        return this.brokenBlocksCount;
    }

    public void resetBrokenBlocksCount() {
        this.brokenBlocksCount = 0;
        this.getPersistentData().putInt("BrokenBlocksCount", 0);
    }

    public void setHitFace(Direction face) {
        this.hitFace = face;
    }

    public Direction getHitFace() {
        return hitFace;
    }
}
