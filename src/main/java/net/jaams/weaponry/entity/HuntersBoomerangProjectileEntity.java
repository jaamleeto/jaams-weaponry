package net.jaams.weaponry.entity;

import net.jaams.weaponry.component.projectile.BaseReturningProjectileEntity;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.handler.trait.DisarmHandler;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class HuntersBoomerangProjectileEntity extends BaseReturningProjectileEntity {

    public HuntersBoomerangProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.HUNTERS_BOOMERANG_PROJECTILE.get(), world);
    }

    public HuntersBoomerangProjectileEntity(EntityType<? extends HuntersBoomerangProjectileEntity> type, Level world) {
        super(type, world);
    }

    public HuntersBoomerangProjectileEntity(EntityType<? extends HuntersBoomerangProjectileEntity> type, double x,
            double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public HuntersBoomerangProjectileEntity(EntityType<? extends HuntersBoomerangProjectileEntity> type,
            LivingEntity entity, Level world) {
        super(type, entity, world);
    }

    public HuntersBoomerangProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
        super(ModEntities.HUNTERS_BOOMERANG_PROJECTILE.get(), entity, world, weaponItem);
        initializeProjectileStats(weaponItem);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target instanceof LivingEntity livingTarget && !this.isReturning()) {
            if (tryDisarmEnemy(livingTarget)) {
                if (this.shouldUseReturningLogic()) {
                    this.entityData.set(ID_RETURNING, true);
                }
            }
        }
        super.onHitEntity(result);
    }

    @Override
    protected float getDefaultBaseDamage(ItemStack weapon) {
        return ModProjectiles.getBaseDamage(weapon,
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_DAMAGE.get());
    }

    @Override
    protected float getDefaultBaseKnockback(ItemStack weapon) {
        return ModProjectiles.getBaseKnockback(weapon,
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_KNOCKBACK.get());
    }

    @Override
    protected int getDefaultPierceLevel(ItemStack weapon) {
        return ModProjectiles.getPiercingLevel(weapon,
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_PIERCING_LEVEL.get());
    }

    @Override
    public ItemStack getDefaultWeaponItem() {
        return new ItemStack(TopItems.HUNTERS_BOOMERANG.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModProjectiles.getDimensions(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_HITBOX_WIDTH.get().floatValue(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_HITBOX_HEIGHT.get().floatValue());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileHitSound", "projectile_hit",
                "hunters_boomerang_hit", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileGroundSound",
                "projectile_ground", "hunters_boomerang_ground", SoundEvents.TRIDENT_HIT_GROUND,
                (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileReturnSound",
                "projectile_return", "hunters_boomerang_return", SoundEvents.TRIDENT_RETURN,
                (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(this.getWeaponItem(), 100);
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        return ModProjectiles.isCustomBreakableBlock(this.getWeaponItem(), state,
                ResourceLocation.parse("minecraft:pointed_dripstone"));
    }

    @Override
    protected boolean returnOnBlockHit(BlockHitResult blockHitResult) {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnHit = ModProjectiles.getReturnOnBlockHit(weapon,
                TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_BLOCK_HIT.get());
        return throwback && returnOnHit;
    }

    @Override
    protected boolean returnOnEntityHit(EntityHitResult entityHitResult) {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnHit = ModProjectiles.getReturnOnEntityHit(weapon,
                TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_ENTITY_HIT.get());
        return throwback && returnOnHit;
    }

    @Override
    protected boolean returnOnMaxRange() {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnMax = ModProjectiles.getReturnOnMaxRange(weapon,
                TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_MAX_RANGE.get());
        return throwback && returnOnMax;
    }

    @Override
    public int getMaxMountedEntities() {
        ItemStack weapon = this.getWeaponItem();
        boolean collector = ModProjectiles.getCollectorEnabled(weapon, TraitsConfig.COLLECTOR.get());
        return ModProjectiles.getMaxMountedEntities(weapon, collector,
                TraitsConfig.COLLECTOR_HUNTERS_BOOMERANG_PROJECTILE_MAX_ITEMS.get());
    }

    @Override
    protected boolean canCollectItems() {
        return this.isReturning() && !this.inGround && this.entitiesMountedCount < getMaxMountedEntities();
    }

    @Override
    protected float getWaterInertia() {
        return ModProjectiles.getWaterInertia(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_WATER_INERTIA.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected void tickDespawn() {
        life++;
        int despawnTime = ModProjectiles.getDespawnTicks(this.getWeaponItem(),
                ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_DESPAWN_TIME.get());
        if (life >= despawnTime) {
            if (ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_DESPAWN_AS_ITEM.get()
                    && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
                ModProjectiles.dropAsItem(this.level(), this.getPickupItem().copy(), this.getX(), this.getY(),
                        this.getZ());
            } else if (!this.level().isClientSide) {
                ItemStack itemStack = this.weaponItem.copy();
                if (!itemStack.isEmpty()) {
                    ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack),
                            getX(), getY() + 0.3, getZ(), 5, 0.1d, 0.1d, 0.1d, 0.05d);
                }
            }
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.isInWater() && !this.inGround) {
            if (ProjectileClientConfig.HUNTERS_BOOMERANG_PROJECTILE_TRAIL.get()) {
                int spawnRate = ProjectileClientConfig.HUNTERS_BOOMERANG_PROJECTILE_TRAIL_SPAWN_RATE.get();
                if (this.random.nextInt(spawnRate) == 0) {
                    this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D,
                            0.0D);
                }
            }
        }
        if (!this.level().isClientSide && !this.inGround && !this.isNoPhysics()) {
            if (ModProjectiles.getSwooshSoundEnabled(this.getWeaponItem(),
                    ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_SWOOSH_SOUND.get())) {
                int interval = ModProjectiles.getSwooshInterval(this.getWeaponItem(),
                        ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_SWOOSH_INTERVAL.get());
                if (this.tickCount % interval == 0) {
                    String soundId = ModProjectiles.getSwooshSoundId(this.getWeaponItem(),
                            "jaams_weaponry:thin_swoosh_air");
                    ModUtils.playSound(this, soundId, SoundSource.AMBIENT, 0.3F, 1.0F);
                }
            }
        }
    }

    protected boolean isDisarmingShotEnabled(ItemStack weapon) {
        return ModProjectiles.getDisarmingShotEnabled(weapon, TraitsConfig.DISARMING_SHOT.get());
    }

    protected float getDisarmChance(ItemStack weapon) {
        return ModProjectiles.getDisarmingShotChance(weapon,
                TraitsConfig.DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_CHANCE.get().floatValue());
    }

    protected boolean requireCriticalToDisarm(ItemStack weapon) {
        return ModProjectiles.getDisarmingShotRequireCritical(weapon,
                TraitsConfig.DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_REQUIRE_CRITICAL.get());
    }

    protected int getDisarmDurabilityCost(ItemStack weapon) {
        return 0;
    }

    protected boolean shouldMountDisarmedItem(ItemStack weapon) {
        return ModProjectiles.getDisarmingShotMountItem(weapon,
                TraitsConfig.DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_MOUNT_ITEM.get());
    }

    protected boolean tryDisarmEnemy(LivingEntity target) {
        ItemStack weapon = this.getWeaponItem();
        if (this.level().isClientSide || !isDisarmingShotEnabled(weapon)) {
            return false;
        }
        if (requireCriticalToDisarm(weapon) && !this.isCritical()) {
            return false;
        }
        boolean mountItem = shouldMountDisarmedItem(weapon);
        if (mountItem && this.entitiesMountedCount >= getMaxMountedEntities()) {
            return false;
        }
        float disarmChance = Math.max(0.0F, Math.min(1.0F, getDisarmChance(weapon)));
        if (this.random.nextFloat() >= disarmChance) {
            return false;
        }
        ItemStack disarmedItem = ItemStack.EMPTY;
        InteractionHand handUsed = InteractionHand.MAIN_HAND;
        if (!target.getMainHandItem().isEmpty() && DisarmHandler.canBeDisarmed(target.getMainHandItem(), weapon)) {
            disarmedItem = target.getMainHandItem().copy();
            handUsed = InteractionHand.MAIN_HAND;
        } else if (!target.getOffhandItem().isEmpty() && DisarmHandler.canBeDisarmed(target.getOffhandItem(), weapon)) {
            disarmedItem = target.getOffhandItem().copy();
            handUsed = InteractionHand.OFF_HAND;
        }
        if (!disarmedItem.isEmpty()) {
            target.setItemInHand(handUsed, ItemStack.EMPTY);
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), disarmedItem);
            itemEntity.setPickUpDelay(10);
            this.level().addFreshEntity(itemEntity);
            int durabilityCost = getDisarmDurabilityCost(weapon);
            if (durabilityCost > 0 && this.getOwner() instanceof LivingEntity shooter) {
                weapon.hurtAndBreak(durabilityCost, shooter, (e) -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
            if (mountItem) {
                if (this.forceMountEntity(itemEntity)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
