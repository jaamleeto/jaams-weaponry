package net.jaams.weaponry.component.projectile;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.util.ModProjectiles;

import net.minecraft.world.entity.Mob;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public abstract class BaseReturningProjectileEntity extends BaseWeaponProjectileEntity {
    protected static final EntityDataAccessor<Boolean> ID_RETURNING = SynchedEntityData
            .defineId(BaseReturningProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    protected float weaponRange = 30.0F;
    protected double returnSpeed = 1.5D;
    protected Vec3 initialPosition = null;
    protected int clientSideReturnTickCount = 0;
    protected int entitiesMountedCount = 0;
    protected List<UUID> mountedPassengers = new ArrayList<>();
    protected boolean hasBeenPickedUp = false;
    protected int airTimeTicks = 0;
    protected int returnTicks = 0;

    public BaseReturningProjectileEntity(EntityType<? extends BaseReturningProjectileEntity> type, Level level) {
        super(type, level);
    }

    public BaseReturningProjectileEntity(EntityType<? extends BaseReturningProjectileEntity> type, double x, double y,
            double z, Level level) {
        super(type, x, y, z, level);
    }

    public BaseReturningProjectileEntity(EntityType<? extends BaseReturningProjectileEntity> type, LivingEntity shooter,
            Level level) {
        super(type, shooter, level);
    }

    public BaseReturningProjectileEntity(EntityType<? extends BaseReturningProjectileEntity> type, LivingEntity shooter,
            Level level, ItemStack weaponItem) {
        super(type, shooter, level, weaponItem);
        this.entityData.set(ID_RETURNING, false);
    }

    protected abstract boolean returnOnBlockHit(BlockHitResult blockHitResult);

    protected abstract boolean returnOnEntityHit(EntityHitResult entityHitResult);

    protected abstract boolean returnOnMaxRange();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_RETURNING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(ID_RETURNING, tag.getBoolean("Returning"));
        this.weaponRange = tag.getFloat("WeaponRange");
        this.airTimeTicks = tag.getInt("AirTimeTicks");
        if (tag.contains("InitialPositionX")) {
            this.initialPosition = new Vec3(tag.getDouble("InitialPositionX"), tag.getDouble("InitialPositionY"),
                    tag.getDouble("InitialPositionZ"));
        }
        this.mountedPassengers.clear();
        if (tag.contains("MountedPassengers", Tag.TAG_LIST)) {
            ListTag list = tag.getList("MountedPassengers", Tag.TAG_INT_ARRAY);
            for (int i = 0; i < list.size(); i++) {
                this.mountedPassengers.add(NbtUtils.loadUUID(list.get(i)));
            }
        }
        this.hasBeenPickedUp = tag.getBoolean("HasBeenPickedUp");
        this.returnTicks = tag.getInt("ReturnTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Returning", this.entityData.get(ID_RETURNING));
        tag.putFloat("WeaponRange", this.weaponRange);
        tag.putInt("AirTimeTicks", this.airTimeTicks);
        if (this.initialPosition != null) {
            tag.putDouble("InitialPositionX", this.initialPosition.x);
            tag.putDouble("InitialPositionY", this.initialPosition.y);
            tag.putDouble("InitialPositionZ", this.initialPosition.z);
        }
        ListTag passengerList = new ListTag();
        for (UUID uuid : this.mountedPassengers) {
            passengerList.add(NbtUtils.createUUID(uuid));
        }
        tag.put("MountedPassengers", passengerList);
        tag.putBoolean("HasBeenPickedUp", this.hasBeenPickedUp);
        tag.putInt("ReturnTicks", this.returnTicks);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeBoolean(this.entityData.get(ID_RETURNING));
        buffer.writeFloat(this.weaponRange);
        buffer.writeBoolean(this.initialPosition != null);
        if (this.initialPosition != null) {
            buffer.writeDouble(this.initialPosition.x);
            buffer.writeDouble(this.initialPosition.y);
            buffer.writeDouble(this.initialPosition.z);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        super.readSpawnData(buf);
        boolean hasInitial = buf.readBoolean();
        this.entityData.set(ID_RETURNING, buf.readBoolean());
        this.weaponRange = buf.readFloat();
        this.initialPosition = hasInitial ? new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()) : null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isReturning()) {
                this.clientSideReturnTickCount++;
            }
            return;
        }
        Entity owner = this.getOwner();
        boolean ownerIsValid = owner != null && owner.isAlive() && !owner.isSpectator();
        if (!ownerIsValid) {
            if (this.isNoGravity()) {
                this.setNoGravity(false);
            }
            if (this.isReturning()) {
                this.entityData.set(ID_RETURNING, false);
                stopRidingEntities();
            }
        }
        if (this.tickCount == 1) {
            if (returnOnMaxRange()) {
                this.setNoGravity(true);
            }
        }
        if (this.isStuck && this.isNoGravity()) {
            this.setNoGravity(false);
        }
        if (this.isStuck) {
            this.entityData.set(ID_RETURNING, false);
        }
        if (this.initialPosition == null && this.tickCount >= 3) {
            this.initialPosition = this.position();
        }
        if (!this.inGround && !this.isReturning() && this.isNoGravity()) {
            this.airTimeTicks++;
            if (this.airTimeTicks > 100) {
                this.setNoGravity(false);
                this.airTimeTicks = 0;
            }
        } else {
            this.airTimeTicks = 0;
        }
        if (this.isInWater() && getWaterInertia() < 0.9F) {
            if (this.isNoGravity()) {
                this.setNoGravity(false);
            }
        }
        if (this.isNoGravity() && !this.inGround && !this.isStuck) {
            Vec3 motion = this.getDeltaMovement();
            if (motion.lengthSqr() < 0.01D) {
                this.setNoGravity(false);
            }
        }
        if (this.isNoGravity() && this.isReturning()) {
            this.returnTicks++;
            if (this.returnTicks > 200) {
                this.setNoGravity(false);
                this.entityData.set(ID_RETURNING, false);
                stopRidingEntities();
                this.returnTicks = 0;
            }
        } else if (!this.isReturning()) {
            this.returnTicks = 0;
        }
        if (this.isNoGravity() && this.tickCount > 1200) {
            this.setNoGravity(false);
        }
        if (shouldUseReturningLogic() && !this.isReturning() && this.initialPosition != null && ownerIsValid) {
            double distance = this.position().distanceTo(this.initialPosition);
            if (distance >= this.weaponRange && !this.inGround) {
                if (returnOnMaxRange() && !isStuck) {
                    this.entityData.set(ID_RETURNING, true);
                }
            }
        }
        if (this.isReturning() && ownerIsValid) {
            handleReturnMovement();
            handleCollectItemsWhileReturning();
            handleMobOwnerPickup();
        } else if (!this.isReturning()) {
            this.entitiesMountedCount = this.getPassengers().size();
        }
    }

    @Override
    public boolean canHitEntity(Entity target) {
        if (this.isReturning() && target == this.getOwner()) {
            return false;
        }
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        boolean shouldStop = this.piercedCount >= this.pierceLevel;
        if (this.isReturning() && result.getEntity() == this.getOwner()) {
            return;
        }
        super.onHitEntity(result);
        if (!returnOnEntityHit(result) && shouldStop) {
            this.isStuck = true;
            this.entityData.set(ID_RETURNING, false);
            return;
        }
        if (shouldUseReturningLogic() && shouldStop && !this.isReturning() && returnOnEntityHit(result)) {
            this.entityData.set(ID_RETURNING, true);
        }
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Entity owner = this.getOwner();
        if (!returnOnBlockHit(result)) {
            this.isStuck = true;
            return;
        }
        if (this.isReturning()) {
            this.inGround = true;
            this.setNoGravity(false);
            this.isStuck = true;
            this.entityData.set(ID_RETURNING, false);
            return;
        }
        if (!isStuck && shouldUseReturningLogic() && owner != null && owner.isAlive() && !owner.isSpectator()
                && returnOnBlockHit(result)) {
            this.entityData.set(ID_RETURNING, true);
            this.inGround = false;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        stopRidingEntities();
        this.mountedPassengers.clear();
        super.remove(reason);
    }

    @Override
    public void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        this.mountedPassengers.remove(passenger.getUUID());
        this.entitiesMountedCount = Math.max(0, this.getPassengers().size());
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.0D;
    }

    @Override
    protected void applyKnockback(LivingEntity target) {
        double knockbackResistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double knockbackFactor = 1.0 - Math.min(knockbackResistance, 1.0);
        if (knockbackFactor <= 0) {
            return;
        }
        float baseKnockback = this.weaponKnockback;
        int knockbackLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.KNOCKBACK, this.weaponItem);
        float knockbackMultiplier = this.isReturning() ? 0.3F : 0.8F;
        if (knockbackLevel > 0) {
            baseKnockback += knockbackLevel * knockbackMultiplier;
        }
        float knockbackStrength = baseKnockback * 0.5F;
        float criticalBonus = this.isCritical() ? (this.isReturning() ? 0.2F : 0.6F) : 0.0F;
        knockbackStrength += criticalBonus;
        if (knockbackStrength <= 0.0F) {
            return;
        }
        float finalStrength = knockbackStrength * (float) knockbackFactor;
        if (this.isReturning()) {
            Vec3 direction = this.getDeltaMovement().normalize().scale(-1.0);
            double horizontal = finalStrength * 2.5;
            target.setDeltaMovement(target.getDeltaMovement().x * 0.5 - direction.x * horizontal,
                    target.getDeltaMovement().y, target.getDeltaMovement().z * 0.5 - direction.z * horizontal);
        } else {
            target.knockback(finalStrength, -this.getDeltaMovement().x, -this.getDeltaMovement().z);
        }
        target.hurtMarked = true;
    }

    @Override
    protected void handleLoyaltyReturn() {
        if (!this.isStuck) {
            return;
        }
        super.handleLoyaltyReturn();
    }

    public boolean isReturning() {
        return this.entityData.get(ID_RETURNING);
    }

    protected boolean shouldUseReturningLogic() {
        return TraitsConfig.THROWBACK.get();
    }

    protected boolean canCollectItems() {
        return this.isReturning() && !this.inGround && this.entitiesMountedCount < getMaxMountedEntities();
    }

    protected void handleReturnMovement() {
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive() || owner.isSpectator() || !this.ownedBy(owner)) {
            stopReturning();
            return;
        }
        Vec3 ownerPos = owner.position().add(0, owner.getEyeHeight() * 0.85, 0);
        Vec3 toOwner = ownerPos.subtract(this.position());
        double distanceToOwner = toOwner.length();
        double pickupDistance = 1.5D;
        if (distanceToOwner <= pickupDistance) {
            if (owner instanceof Player player) {
                this.playerPickupInAir(player);
            }
            stopReturning();
            return;
        }
        double baseReturnSpeed = getReturnSpeed();
        double speed = baseReturnSpeed;
        Vec3 direction = toOwner.normalize();
        Vec3 newMotion = direction.scale(speed);
        this.setDeltaMovement(newMotion);
        this.hurtMarked = true;
    }

    protected void stopReturning() {
        this.entityData.set(ID_RETURNING, false);
        stopRidingEntities();
        this.entitiesMountedCount = 0;
        this.setDeltaMovement(Vec3.ZERO);
        this.hurtMarked = true;
    }

    protected void handleCollectItemsWhileReturning() {
        if (!canCollectItems()) {
            return;
        }
        AABB area = this.getBoundingBox().inflate(0.2D);
        List<Entity> itemsAndOrbs = this.level().getEntities(this, area,
                e -> (e instanceof ItemEntity || e instanceof ExperienceOrb) && !e.isPassenger());
        for (Entity entity : itemsAndOrbs) {
            if (this.getPassengers().size() >= getMaxMountedEntities()) {
                break;
            }
            entity.startRiding(this, true);
            if (!this.mountedPassengers.contains(entity.getUUID())) {
                this.mountedPassengers.add(entity.getUUID());
            }
            this.entitiesMountedCount = this.getPassengers().size();
        }
    }

    protected void stopRidingEntities() {
        for (Entity passenger : new ArrayList<>(this.getPassengers())) {
            passenger.stopRiding();
        }
        this.entitiesMountedCount = 0;
    }

    protected void handleMobOwnerPickup() {
        Entity owner = this.getOwner();
        if (owner != null && !(owner instanceof Player) && owner.isAlive() && owner instanceof Mob mob) {
            double distance = this.position().distanceTo(owner.position().add(0, owner.getEyeHeight() * 0.5, 0));
            if (distance <= 1.5D) {
                ItemStack pickup = this.getPickupItem().copy();
                pickup.setCount(1);
                if (ModProjectiles.tryMobPickup(pickup, mob, this.level())) {
                    this.stopReturning();
                    this.discard();
                } else {
                    
                    this.stopReturning();
                    this.spawnAtLocation(this.getPickupItem().copy(), 0.1F);
                    this.discard();
                }
            }
        }
    }

    public boolean forceMountEntity(Entity entity) {
        if (this.getPassengers().size() >= this.getMaxMountedEntities()) {
            return false;
        }
        entity.startRiding(this, true);
        if (!this.mountedPassengers.contains(entity.getUUID())) {
            this.mountedPassengers.add(entity.getUUID());
        }
        this.entitiesMountedCount = this.getPassengers().size();
        return true;
    }

    public void playerPickupInAir(Player player) {
        if (!this.level().isClientSide && this.entityData.get(ID_RETURNING) && !this.inGround
                && !this.hasBeenPickedUp) {
            if (this.tryPickup(player)) {
                player.take(this, 1);
                this.hasBeenPickedUp = true;
                for (Entity passenger : this.getPassengers()) {
                    if (passenger instanceof ItemEntity itemEntity) {
                        player.getInventory().add(itemEntity.getItem());
                        itemEntity.discard();
                    } else if (passenger instanceof ExperienceOrb orb) {
                        player.take(orb, 1);
                        player.giveExperiencePoints(orb.getValue());
                        orb.discard();
                    }
                }
                this.discard();
            } else {
                player.take(this, 1);
                this.discard();
            }
        }
    }

    public void setWeaponRange(float range) {
        this.weaponRange = Math.max(0.0F, range);
    }

    public float getWeaponRange() {
        return this.weaponRange;
    }

    public void setReturnSpeed(double speed) {
        this.returnSpeed = Math.max(0.1D, speed);
    }

    public double getReturnSpeed() {
        return this.returnSpeed;
    }

    public int getMaxMountedEntities() {
        return 0;
    }
}
