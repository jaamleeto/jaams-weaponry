
package net.jaams.weaponry.component.projectile;

import net.jaams.weaponry.init.ModEnchantments;

import net.jaams.weaponry.util.ModComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.loader.ThrowableModifierLoader;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class BaseWeaponProjectileEntity extends AbstractArrow implements IEntityWithComplexSpawn {
    public static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ID_SPIN_TICKS = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> ID_LAST_SPIN_ROTATION = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> ID_IMPACTED = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> ID_WEAPON_ITEM = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> ID_CRITICAL = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ID_ORIGINAL_SLOT = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ID_IGNORE_HIT_TICKS = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ID_PIERCING = SynchedEntityData
            .defineId(BaseWeaponProjectileEntity.class, EntityDataSerializers.INT);
    public ItemStack weaponItem;
    protected float weaponDamage;
    protected float weaponKnockback;
    protected float lastSpinRotation = 0.0F;
    protected int life = 0;
    protected int ignoreHitTicks = 0;
    protected int pierceLevel = 0;
    protected int piercedCount = 0;
    protected int originalSlotIndex = -1;
    protected int clientSideReturnTridentTickCount = 0;
    protected int multishotGroundTicks = 0;
    protected int brokenBlocksCount = 0;
    protected int bounceCount = 0;
    protected boolean isStuck = false;
    protected int knockback = 0;
    protected boolean shotFromCrossbowCompat = false;
    @Nullable
    protected UUID lastHitEntityId;

    protected BaseWeaponProjectileEntity(EntityType<? extends BaseWeaponProjectileEntity> type, Level level) {
        super(type, level);
        initializeWeaponItem(null);
    }

    protected BaseWeaponProjectileEntity(EntityType<? extends BaseWeaponProjectileEntity> type, double x, double y,
            double z, Level level) {
        super(type, x, y, z, level, ItemStack.EMPTY, null);
        initializeWeaponItem(null);
    }

    protected BaseWeaponProjectileEntity(EntityType<? extends BaseWeaponProjectileEntity> type, LivingEntity shooter,
            Level level) {
        super(type, shooter, level, ItemStack.EMPTY, null);
        initializeWeaponItem(null);
    }

    protected BaseWeaponProjectileEntity(EntityType<? extends BaseWeaponProjectileEntity> type, LivingEntity shooter,
            Level level, ItemStack weaponItem) {
        super(type, shooter, level, weaponItem != null && !weaponItem.isEmpty() ? weaponItem.copy() : ItemStack.EMPTY,
                null);
        this.weaponItem = weaponItem != null && !weaponItem.isEmpty() ? weaponItem.copy() : getDefaultWeaponItem();
        this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
        this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
        this.weaponDamage = 0.0F;
        this.weaponKnockback = 0.0F;
        this.entityData.set(ID_CRITICAL, isCritical());
        this.entityData.set(ID_ORIGINAL_SLOT, -1);
        this.entityData.set(ID_IGNORE_HIT_TICKS, 0);
        this.entityData.set(ID_PIERCING, 0);
        this.originalSlotIndex = -1;
        this.brokenBlocksCount = 0;
        updateWeaponEnchantmentData();
        updateCustomNameFromWeapon();
    }

    public abstract ItemStack getDefaultWeaponItem();

    protected abstract float getDefaultBaseDamage(ItemStack weapon);

    protected abstract float getDefaultBaseKnockback(ItemStack weapon);

    protected abstract int getDefaultPierceLevel(ItemStack weapon);

    protected abstract SoundEvent getHitSound();

    protected abstract SoundEvent getGroundSound();

    protected abstract SoundEvent getLoyaltySound();

    protected void setDefaultProjectileStats() {
        this.setWeaponBaseDamage(1.0f);
        this.setWeaponBaseKnockback(0.5f);
        this.setPiercingLevel(0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte) 0);
        builder.define(ID_FOIL, false);
        builder.define(ID_SPIN_TICKS, 0);
        builder.define(ID_LAST_SPIN_ROTATION, 0.0F);
        builder.define(ID_IMPACTED, false);
        builder.define(ID_WEAPON_ITEM, ItemStack.EMPTY);
        builder.define(ID_CRITICAL, false);
        builder.define(ID_ORIGINAL_SLOT, -1);
        builder.define(ID_IGNORE_HIT_TICKS, 0);
        builder.define(ID_PIERCING, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("WeaponItem", 10)) {
            this.weaponItem = ItemStack.parseOptional(this.registryAccess(), tag.getCompound("WeaponItem"));
            if (this.weaponItem.isEmpty()) {
                this.weaponItem = getDefaultWeaponItem();
            }
        } else {
            this.weaponItem = getDefaultWeaponItem();
        }
        this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
        this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
        this.entityData.set(ID_IMPACTED, tag.getBoolean("HasImpacted"));
        this.entityData.set(ID_SPIN_TICKS, tag.getInt("SpinTicks"));
        this.lastSpinRotation = tag.getFloat("LastSpinRotation");
        this.entityData.set(ID_LAST_SPIN_ROTATION, this.lastSpinRotation);
        this.entityData.set(ID_CRITICAL, tag.getBoolean("IsCritical"));
        this.originalSlotIndex = tag.getInt("OriginalSlotIndex");
        this.entityData.set(ID_ORIGINAL_SLOT, this.originalSlotIndex);
        this.weaponDamage = tag.getFloat("WeaponDamage");
        this.weaponKnockback = tag.getFloat("WeaponKnockback");
        this.ignoreHitTicks = tag.getInt("IgnoreHitTicks");
        this.pierceLevel = tag.getInt("PierceLevel");
        this.piercedCount = tag.getInt("PiercedCount");
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.isStuck = tag.getBoolean("IsStuck");
        this.brokenBlocksCount = tag.getInt("BrokenBlocksCount");
        if (tag.contains("LastHitEntityId")) {
            this.lastHitEntityId = tag.getUUID("LastHitEntityId");
        } else {
            this.lastHitEntityId = null;
        }
        updateWeaponEnchantmentData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.weaponItem.isEmpty()) {
            tag.put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
        }
        tag.putBoolean("IsCritical", isCritical());
        tag.putBoolean("HasImpacted", this.entityData.get(ID_IMPACTED));
        tag.putInt("SpinTicks", this.entityData.get(ID_SPIN_TICKS));
        tag.putFloat("LastSpinRotation", this.lastSpinRotation);
        tag.putInt("OriginalSlotIndex", this.originalSlotIndex);
        tag.putFloat("WeaponDamage", this.weaponDamage);
        tag.putFloat("WeaponKnockback", this.weaponKnockback);
        tag.putInt("IgnoreHitTicks", this.ignoreHitTicks);
        tag.putInt("PierceLevel", this.pierceLevel);
        tag.putInt("PiercedCount", this.piercedCount);
        tag.putBoolean("IsStuck", this.isStuck);
        tag.putInt("BrokenBlocksCount", this.brokenBlocksCount);
        if (this.lastHitEntityId != null) {
            tag.putUUID("LastHitEntityId", this.lastHitEntityId);
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, this.weaponItem);
        buffer.writeFloat(this.weaponDamage);
        buffer.writeBoolean(isCritical());
        buffer.writeInt(this.originalSlotIndex);
        buffer.writeFloat(this.lastSpinRotation);
        buffer.writeInt(this.ignoreHitTicks);
        buffer.writeInt(this.pierceLevel);
        buffer.writeInt(this.piercedCount);
        buffer.writeInt(this.brokenBlocksCount);
        buffer.writeBoolean(this.lastHitEntityId != null);
        if (this.lastHitEntityId != null) {
            buffer.writeUUID(this.lastHitEntityId);
        }
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        this.weaponItem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
        this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
        this.weaponDamage = buf.readFloat();
        this.setCritical(buf.readBoolean());
        this.originalSlotIndex = buf.readInt();
        this.entityData.set(ID_ORIGINAL_SLOT, this.originalSlotIndex);
        this.lastSpinRotation = buf.readFloat();
        this.entityData.set(ID_LAST_SPIN_ROTATION, this.lastSpinRotation);
        this.ignoreHitTicks = buf.readInt();
        this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
        this.pierceLevel = buf.readInt();
        this.piercedCount = buf.readInt();
        this.entityData.set(ID_PIERCING, this.pierceLevel);
        this.brokenBlocksCount = buf.readInt();
        boolean hasLastHitEntityId = buf.readBoolean();
        this.lastHitEntityId = hasLastHitEntityId ? buf.readUUID() : null;
        updateWeaponEnchantmentData();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ID_WEAPON_ITEM.equals(key)) {
            ItemStack newWeaponItem = this.entityData.get(ID_WEAPON_ITEM);
            if (!ItemStack.matches(this.weaponItem, newWeaponItem)) {
                this.weaponItem = newWeaponItem.isEmpty() ? getDefaultWeaponItem() : newWeaponItem.copy();
                updateWeaponEnchantmentData();
                this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
            }
        } else if (ID_ORIGINAL_SLOT.equals(key)) {
            this.originalSlotIndex = this.entityData.get(ID_ORIGINAL_SLOT);
        } else if (ID_LAST_SPIN_ROTATION.equals(key)) {
            this.lastSpinRotation = this.entityData.get(ID_LAST_SPIN_ROTATION);
        } else if (ID_IGNORE_HIT_TICKS.equals(key)) {
            this.ignoreHitTicks = this.entityData.get(ID_IGNORE_HIT_TICKS);
        } else if (ID_PIERCING.equals(key)) {
            this.pierceLevel = this.entityData.get(ID_PIERCING);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount == 1) {
            this.refreshDimensions();
        }
        if (!this.level().isClientSide) {
            if (handleCurseOfVanishingDisappear()) {
                return;
            }
            syncPersistentDataToEntityData();
            if (!this.weaponItem.isEmpty() && this.weaponItem.isDamageableItem()
                    && this.weaponItem.getDamageValue() >= this.weaponItem.getMaxDamage()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    ModProjectiles.handleItemBreak(serverLevel, this.weaponItem, this.getX(), this.getY(), this.getZ(),
                            this);
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
                int despawnTicks = ModProjectiles.getMultishotCloneDespawnTicks(this.weaponItem);
                if (this.multishotGroundTicks >= despawnTicks && this.level() instanceof ServerLevel serverLevel) {
                    ModProjectiles.handleMultishotCloneBreak(serverLevel, this.weaponItem, this.getX(), this.getY(),
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
            handleProjectileTrail();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (this.lastHitEntityId != null && target.getUUID().equals(this.lastHitEntityId) && this.ignoreHitTicks > 0) {
            return;
        }
        boolean shouldStop = this.piercedCount >= this.pierceLevel;
        DamageSource source = this.damageSources().trident(this, this.getOwner() == null ? this : this.getOwner());
        float damage = calculateProjectileDamage(this.weaponItem, this.weaponDamage, this.isMultishotClone(),
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
        this.playSound(getHitSound(), 1.0F, 1.0F);
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
            if (!this.level().isClientSide && !this.weaponItem.isEmpty()
                    && this.getOwner() instanceof Player ownerPlayer) {
                this.weaponItem.hurtEnemy(hitLiving, ownerPlayer);
                this.weaponItem.postHurtEnemy(hitLiving, ownerPlayer);
            }
            if (hitLiving.getType() != EntityType.ENDERMAN) {
                this.doPostHurtEffects(hitLiving);
            }
            if (!this.level().isClientSide && !this.weaponItem.isEmpty()) {
                this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
                this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
                if (this.weaponItem.isDamageableItem()
                        && this.weaponItem.getDamageValue() >= this.weaponItem.getMaxDamage()) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        ModProjectiles.handleItemBreak(serverLevel, this.weaponItem, this.getX(), this.getY(),
                                this.getZ(), this);
                    }
                    return;
                }
            }
            if (!hitLiving.isAlive()) {
                this.lastHitEntityId = hitLiving.getUUID();
            }
            applyKnockback(hitLiving);
            applyCompatKnockback(hitLiving);
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
        ModProjectiles.applyWeaponEnchantmentEffects(entity, this.getOwner(), this.weaponItem);
        if (this.isOnFire()) {
            entity.igniteForSeconds(5);
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
        this.entityData.set(ID_IMPACTED, true);
        this.lastHitEntityId = null;
        this.ignoreHitTicks = 0;
        this.piercedCount = 0;
        BlockState blockstate = this.level().getBlockState(blockHitResult.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, blockHitResult, this);
        if (!this.level().isClientSide) {
            if (blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
                spawnBlockParticles(pos);
                tryBreakBlock(pos, state, hitFace);
            }
            if (ModEnchantments.level(this.weaponItem, Enchantments.FIRE_ASPECT) > 0) {
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 3,
                        0.1D, 0.1D, 0.1D, 0.0D);
            }
            float finalSpinRotation = this.entityData.get(ID_SPIN_TICKS) * 60.0F;
            this.entityData.set(ID_LAST_SPIN_ROTATION, finalSpinRotation);
            this.getPersistentData().putFloat("LastSpinRotation", finalSpinRotation);
        }
        this.applyBlockHitEffects(pos, state, blockHitResult);
        this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (shouldBreakOnBlockHit()) {
            this.playSound(getGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            handleItemBreak();
            return;
        }
        if (shouldBreakAfterMaxBlockBreaks() && getBrokenBlocksCount() >= getMaxBlockBreaks()) {
            handleItemBreak();
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
    protected boolean tryPickup(Player player) {
        Entity owner = this.getOwner();
        if (owner != null && owner != player)
            return false;
        ItemStack pickup = this.getPickupItem().copy();
        pickup.setCount(1);
        Inventory inv = player.getInventory();
        if (player.getAbilities().instabuild && this.pickup == Pickup.CREATIVE_ONLY)
            return true;
        if (this.pickup != Pickup.ALLOWED && !(this.isNoPhysics() && this.ownedBy(player)))
            return false;
        if (this.originalSlotIndex >= 0 && (this.originalSlotIndex <= 8 || this.originalSlotIndex == 40)) {
            ItemStack slot = inv.getItem(this.originalSlotIndex);
            if (slot.isEmpty()) {
                inv.setItem(this.originalSlotIndex, pickup);
                ModProjectiles.playPickupSoundPlayer(this.level(), player);
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slot, pickup) && slot.getCount() < slot.getMaxStackSize()) {
                slot.grow(1);
                ModProjectiles.playPickupSoundPlayer(this.level(), player);
                return true;
            }
        }
        for (int i = 0; i <= 8; i++) {
            if (i == this.originalSlotIndex)
                continue;
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                inv.setItem(i, pickup);
                ModProjectiles.playPickupSoundPlayer(this.level(), player);
                return true;
            }
            if (ItemStack.isSameItemSameComponents(stack, pickup) && stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(1);
                ModProjectiles.playPickupSoundPlayer(this.level(), player);
                return true;
            }
        }
        if (inv.add(pickup)) {
            ModProjectiles.playPickupSoundPlayer(this.level(), player);
            return true;
        }
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.getPersistentData().getBoolean("IsMultishotClone")) {
            return ItemStack.EMPTY;
        }
        if (this.weaponItem != null && !this.weaponItem.isEmpty()) {
            ItemStack stack = this.weaponItem.copy();
            stack.setCount(1);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return getDefaultWeaponItem();
    }

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    public int getKnockback() {
        return this.knockback;
    }

    public void setShotFromCrossbow(boolean shotFromCrossbow) {
        this.shotFromCrossbowCompat = shotFromCrossbow;
    }

    public boolean isShotFromCrossbow() {
        return this.shotFromCrossbowCompat;
    }

    protected void applyCompatKnockback(LivingEntity target) {
        if (this.knockback <= 0) {
            return;
        }
        double resistance = Math.max(0.0D, 1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 push = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize()
                .scale(this.knockback * 0.6D * resistance);
        if (push.lengthSqr() > 0.0D) {
            target.push(push.x, 0.1D, push.z);
        }
    }

    public void initializeWeaponItem(ItemStack weaponItem) {
        this.weaponItem = weaponItem != null && !weaponItem.isEmpty() ? weaponItem.copy() : getDefaultWeaponItem();
        this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
        this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
    }

    public void initializeProjectileStats(ItemStack weapon) {
        if (weapon == null || weapon.isEmpty()) {
            setDefaultProjectileStats();
            return;
        }
        ThrowableItemData.ThrowableEntry jsonData = null;
        List<ThrowableItemData> modifiers = ThrowableModifierLoader.INSTANCE.getForItem(weapon.getItem());
        if (!modifiers.isEmpty()) {
            jsonData = modifiers.get(0).throwable;
        }
        float damage = (jsonData != null && jsonData.base_damage != null && jsonData.base_damage >= 0)
                ? jsonData.base_damage
                : getDefaultBaseDamage(weapon);
        float knockback = (jsonData != null && jsonData.base_knockback != null && jsonData.base_knockback >= 0)
                ? jsonData.base_knockback
                : getDefaultBaseKnockback(weapon);
        int pierce = (jsonData != null && jsonData.pierce_level != null && jsonData.pierce_level >= 0)
                ? jsonData.pierce_level
                : getDefaultPierceLevel(weapon);
        this.setWeaponBaseDamage(damage);
        this.setWeaponBaseKnockback(knockback);
        this.setPiercingLevel(pierce);
    }

    public void setWeaponItem(ItemStack newWeaponItem) {
        this.weaponItem = newWeaponItem != null && !newWeaponItem.isEmpty() ? newWeaponItem.copy()
                : getDefaultWeaponItem();
        if (!ItemStack.matches(this.entityData.get(ID_WEAPON_ITEM), this.weaponItem)) {
            this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
            this.getPersistentData().put("WeaponItem", this.weaponItem.saveOptional(this.registryAccess()));
            updateWeaponEnchantmentData();
            updateCustomNameFromWeapon();
        }
    }

    public void updateWeaponEnchantmentData() {
        this.entityData.set(ID_LOYALTY, (byte) ModEnchantments.level(this.weaponItem, Enchantments.LOYALTY));
        this.entityData.set(ID_FOIL, this.weaponItem.hasFoil());
        int enchantPierce = ModEnchantments.level(this.weaponItem, Enchantments.PIERCING);
        this.pierceLevel = Math.max(this.pierceLevel, enchantPierce);
        this.entityData.set(ID_PIERCING, this.pierceLevel);
    }

    protected void syncPersistentDataToEntityData() {
        CompoundTag persistent = this.getPersistentData();
        if (persistent.contains("WeaponItem", 10)) {
            ItemStack persistentItem = ItemStack.parseOptional(this.registryAccess(),
                    persistent.getCompound("WeaponItem"));
            if (!ItemStack.matches(persistentItem, this.weaponItem) && !persistentItem.isEmpty()) {
                this.weaponItem = persistentItem.copy();
                this.entityData.set(ID_WEAPON_ITEM, this.weaponItem);
                updateWeaponEnchantmentData();
            }
        }
        if (persistent.contains("IsCritical")) {
            boolean nbtValue = persistent.getBoolean("IsCritical");
            if (nbtValue != this.entityData.get(ID_CRITICAL)) {
                this.entityData.set(ID_CRITICAL, nbtValue);
            }
        }
        if (persistent.contains("WeaponDamage")) {
            float nbtDmg = persistent.getFloat("WeaponDamage");
            if (nbtDmg != this.weaponDamage) {
                this.weaponDamage = nbtDmg;
            }
        }
        if (persistent.contains("WeaponKnockback")) {
            float nbtKb = persistent.getFloat("WeaponKnockback");
            if (nbtKb != this.weaponKnockback) {
                this.weaponKnockback = nbtKb;
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
        if (persistent.contains("OriginalSlotIndex")) {
            int nbtSlot = persistent.getInt("OriginalSlotIndex");
            if (nbtSlot != this.originalSlotIndex) {
                this.originalSlotIndex = nbtSlot;
                this.entityData.set(ID_ORIGINAL_SLOT, nbtSlot);
            }
        }
        if (persistent.contains("BrokenBlocksCount")) {
            int nbtCount = persistent.getInt("BrokenBlocksCount");
            if (nbtCount != this.brokenBlocksCount) {
                this.brokenBlocksCount = nbtCount;
            }
        }
    }

    protected float calculateProjectileDamage(ItemStack weaponItem, float baseWeaponDamage, boolean isMultishotClone,
            boolean isCritical, Level level, Vec3 projectilePos, Entity hitEntity) {
        float itemAttackDamage = ModProjectiles.getItemAttackDamage(weaponItem);
        float baseDamage = baseWeaponDamage + itemAttackDamage + 1.0F;
        if (isMultishotClone) {
            baseDamage *= 0.5F;
        }
        if (hitEntity instanceof LivingEntity livingEntity) {
            baseDamage += getEnchantmentDamageBonus(weaponItem, livingEntity);
            int powerLevel = ModEnchantments.level(weaponItem, Enchantments.POWER);
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

    /**
     * 1.20.1 EnchantmentHelper.getDamageBonus replacement: sharpness/smite/bane
     * bonuses with the legacy formulas, using the 1.21 sensitivity tags instead of
     * the removed MobType.
     */
    protected float getEnchantmentDamageBonus(ItemStack weapon, LivingEntity target) {
        float bonus = 0.0F;
        int sharpness = ModEnchantments.level(weapon, Enchantments.SHARPNESS);
        if (sharpness > 0) {
            bonus += 1.0F + (float) Math.max(0, sharpness - 1) * 0.5F;
        }
        int smite = ModEnchantments.level(weapon, Enchantments.SMITE);
        if (smite > 0 && target.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)) {
            bonus += smite * 2.5F;
        }
        int bane = ModEnchantments.level(weapon, Enchantments.BANE_OF_ARTHROPODS);
        if (bane > 0 && target.getType().is(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)) {
            bonus += bane * 2.5F;
        }
        return bonus;
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
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
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
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
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

    public void handleItemBreak() {
        if (!this.level().isClientSide && !this.weaponItem.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.weaponItem.copy()), this.getX(),
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

    protected void applyKnockback(LivingEntity target) {
        double knockbackResistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double knockbackFactor = 1.0 - Math.min(knockbackResistance, 1.0);
        if (knockbackFactor <= 0) {
            return;
        }
        float baseKnockback = this.weaponKnockback;
        int knockbackLevel = ModEnchantments.level(this.weaponItem, Enchantments.KNOCKBACK);
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
        if (shouldBreakOnPiercingExhausted() && this.weaponItem.isDamageableItem()
                && this.level() instanceof ServerLevel serverLevel) {
            this.weaponItem.hurtAndBreak(2, serverLevel,
                    this.getOwner() instanceof LivingEntity living ? living : null,
                    item -> ModProjectiles.handleItemBreak(serverLevel, this.weaponItem, this.getX(), this.getY(),
                            this.getZ(), this));
        }
    }

    private void handleShieldBlock(LivingEntity target, DamageSource source, float damage, Entity owner) {
        boolean canDisable = owner instanceof LivingEntity livingOwner
                && canDisableShield(target.getUseItem(), target, livingOwner);
        target.hurt(source, damage);
        if (canDisable) {
            ModProjectiles.handleBlocking(target, owner, this.weaponItem, this.level(), this,
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

    protected void handleProjectileTrail() {
        if (this.inGround) {
            return;
        }
        if (this.isCritical()) {
            spawnCriticalTrail();
        }
        ItemStack stack = this.getWeaponItem();
        if (stack.isEmpty())
            return;
        if (!shouldShowTrail()) {
            return;
        }
        double spawnRate = getTrailSpawnRate();
        if (spawnRate <= 0)
            return;
        int interval = Math.max(1, (int) (1.0 / Math.max(0.01, spawnRate)));
        if (this.tickCount % interval != 0) {
            return;
        }
        ParticleOptions particle = ModProjectiles.getTrailParticle(stack, ParticleTypes.CRIT);
        spawnTrailParticle(particle);
    }

    public ThrowableItemData.TrailEntry getTrailConfig() {
        if (this.weaponItem == null || this.weaponItem.isEmpty()) {
            return null;
        }
        return ThrowableItemData.getTrailConfig(this.weaponItem);
    }

    private void spawnCriticalTrail() {
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            Vec3 motion = this.getDeltaMovement().normalize();
            double offset = 0.45;
            double x = this.getX() - motion.x * offset;
            double y = this.getY() + 0.15 - motion.y * offset;
            double z = this.getZ() - motion.z * offset;
            this.level().addParticle(ParticleTypes.CRIT, x, y, z, 0.0D, 0.02D, 0.0D);
        }
    }

    private void spawnTrailParticle(ParticleOptions particle) {
        Vec3 motion = this.getDeltaMovement().normalize();
        double offset = 0.45;
        double x = this.getX() - motion.x * offset;
        double y = this.getY() + 0.15 - motion.y * offset;
        double z = this.getZ() - motion.z * offset;
        this.level().addParticle(particle, x, y, z, 0.0D, 0.02D, 0.0D);
    }

    protected boolean shouldShowTrail() {
        ItemStack stack = this.getWeaponItem();
        CompoundTag tag = ModComponents.getOrCreate(stack);
        if (tag.contains("ProjectileTrailEnabled", Tag.TAG_BYTE)) {
            return tag.getBoolean("ProjectileTrailEnabled");
        }
        ThrowableItemData.TrailEntry config = getTrailConfig();
        if (config != null && config.trail_enabled != null) {
            return config.trail_enabled;
        }
        return false;
    }

    protected double getTrailSpawnRate() {
        ItemStack stack = this.getWeaponItem();
        CompoundTag tag = ModComponents.getOrCreate(stack);
        if (tag.contains("ProjectileTrailSpawnRate", Tag.TAG_DOUBLE)) {
            return tag.getDouble("ProjectileTrailSpawnRate");
        }
        ThrowableItemData.TrailEntry config = getTrailConfig();
        if (config != null && config.spawn_rate != null) {
            return config.spawn_rate;
        }
        return 1.0;
    }

    protected boolean handleCurseOfVanishingDisappear() {
        if (this.level().isClientSide) {
            return false;
        }
        if (ModEnchantments.level(this.weaponItem, Enchantments.VANISHING_CURSE) <= 0) {
            return false;
        }
        Entity owner = this.getOwner();
        if (!this.hasImpacted() || (owner != null && owner.isAlive())) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel) this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.weaponItem.copy()), x, y + 0.5, z, 5,
                0.1D, 0.1D, 0.1D, 0.05D);
        this.discard();
        return true;
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

    public void setOriginalSlotIndex(int slot) {
        this.originalSlotIndex = Math.max(-1, slot);
        this.entityData.set(ID_ORIGINAL_SLOT, this.originalSlotIndex);
    }

    public void setCritical(boolean critical) {
        this.entityData.set(ID_CRITICAL, critical);
        this.getPersistentData().putBoolean("IsCritical", critical);
    }

    public void updateCustomNameFromWeapon() {
        if (this.weaponItem.isEmpty()) {
            this.setCustomName(null);
            this.setCustomNameVisible(false);
            return;
        }
        Component name = this.weaponItem.getHoverName();
        ChatFormatting color = switch (this.weaponItem.getRarity()) {
            case COMMON -> ChatFormatting.WHITE;
            case UNCOMMON -> ChatFormatting.YELLOW;
            case RARE -> ChatFormatting.AQUA;
            case EPIC -> ChatFormatting.LIGHT_PURPLE;
        };
        this.setCustomName(name.copy().withStyle(color));
        this.setCustomNameVisible(false);
    }

    public void activateIgnoreHits(UUID hitEntityId) {
        if (hitEntityId == null)
            return;
        this.lastHitEntityId = hitEntityId;
        this.ignoreHitTicks = getIgnoreHitTicks();
        this.entityData.set(ID_IGNORE_HIT_TICKS, this.ignoreHitTicks);
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

    public int getShieldDisableCooldownTicks() {
        return 100;
    }

    public boolean isCritical() {
        return this.entityData.get(ID_CRITICAL);
    }

    public boolean isMultishotClone() {
        return this.getPersistentData().getBoolean("IsMultishotClone");
    }

    public ItemStack getWeaponItem() {
        return this.weaponItem.copy();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    public boolean hasImpacted() {
        return this.entityData.get(ID_IMPACTED);
    }

    public int getSpinTicks() {
        return this.entityData.get(ID_SPIN_TICKS);
    }

    public float getLastSpinRotation() {
        return this.entityData.get(ID_LAST_SPIN_ROTATION);
    }

    public int getIgnoreHitTicks() {
        return 10;
    }

    public void setWeaponBaseDamage(float damage) {
        this.weaponDamage = damage;
        this.getPersistentData().putFloat("WeaponDamage", damage);
    }

    public void setWeaponBaseKnockback(float knockback) {
        this.weaponKnockback = knockback;
        this.getPersistentData().putFloat("WeaponKnockback", knockback);
    }

    public float getWeaponDamage() {
        return this.weaponDamage;
    }

    public float getWeaponKnockback() {
        return this.weaponKnockback;
    }

    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return false;
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
}
