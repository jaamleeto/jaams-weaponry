package net.jaams.weaponry.entity;

import net.jaams.weaponry.component.projectile.BaseReturningProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.registry.IronItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class RingProjectileEntity extends BaseReturningProjectileEntity {

    public RingProjectileEntity(EntityType<? extends RingProjectileEntity> type, Level world) {
        super(type, world);
    }

    public RingProjectileEntity(EntityType<? extends RingProjectileEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public RingProjectileEntity(EntityType<? extends RingProjectileEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
    }

    public RingProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
        super(ModEntities.RING_PROJECTILE.get(), entity, world, weaponItem);
        initializeProjectileStats(weaponItem);
    }

    @Override
    protected float getDefaultBaseDamage(ItemStack weapon) {
        return ModProjectiles.getBaseDamage(weapon, ProjectileCommonConfig.RING_PROJECTILE_BASE_DAMAGE.get());
    }

    @Override
    protected float getDefaultBaseKnockback(ItemStack weapon) {
        return ModProjectiles.getBaseKnockback(weapon, ProjectileCommonConfig.RING_PROJECTILE_BASE_KNOCKBACK.get());
    }

    @Override
    protected int getDefaultPierceLevel(ItemStack weapon) {
        return ModProjectiles.getPiercingLevel(weapon, ProjectileCommonConfig.RING_PROJECTILE_PIERCING_LEVEL.get());
    }

    @Override
    public ItemStack getDefaultWeaponItem() {
        return new ItemStack(IronItems.IRON_RING.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModProjectiles.getDimensions(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_HITBOX_WIDTH.get().floatValue(), ProjectileCommonConfig.RING_PROJECTILE_HITBOX_HEIGHT.get().floatValue());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileHitSound", "projectile_hit", "ring_hit", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileGroundSound", "projectile_ground", "ring_ground", SoundEvents.TRIDENT_HIT_GROUND, (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileReturnSound", "projectile_return", "ring_return", SoundEvents.TRIDENT_RETURN, (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(this.getWeaponItem(), 100);
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        return ModProjectiles.isCustomBreakableBlock(this.getWeaponItem(), state, ResourceLocation.parse("minecraft:pointed_dripstone"));
    }

    @Override
    protected boolean returnOnBlockHit(BlockHitResult blockHitResult) {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnHit = ModProjectiles.getReturnOnBlockHit(weapon, TraitsConfig.THROWBACK_RING_PROJECTILE_RETURN_ON_BLOCK_HIT.get());
        return throwback && returnOnHit;
    }

    @Override
    protected boolean returnOnEntityHit(EntityHitResult entityHitResult) {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnHit = ModProjectiles.getReturnOnEntityHit(weapon, TraitsConfig.THROWBACK_RING_PROJECTILE_RETURN_ON_ENTITY_HIT.get());
        return throwback && returnOnHit;
    }

    @Override
    protected boolean returnOnMaxRange() {
        ItemStack weapon = this.getWeaponItem();
        boolean throwback = ModProjectiles.getThrowbackEnabled(weapon, TraitsConfig.THROWBACK.get());
        boolean returnOnMax = ModProjectiles.getReturnOnMaxRange(weapon, TraitsConfig.THROWBACK_RING_PROJECTILE_RETURN_ON_MAX_RANGE.get());
        return throwback && returnOnMax;
    }

    @Override
    protected float getWaterInertia() {
        return ModProjectiles.getWaterInertia(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_WATER_INERTIA.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected void tickDespawn() {
        life++;
        int despawnTime = ModProjectiles.getDespawnTicks(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_DESPAWN_TIME.get());
        if (life >= despawnTime) {
            if (ProjectileCommonConfig.RING_PROJECTILE_DESPAWN_AS_ITEM.get() && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
                ModProjectiles.dropAsItem(this.level(), this.getPickupItem().copy(), this.getX(), this.getY(), this.getZ());
            } else if (!this.level().isClientSide) {
                ItemStack itemStack = this.weaponItem.copy();
                if (!itemStack.isEmpty()) {
                    ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), getX(), getY() + 0.3, getZ(), 5, 0.1d, 0.1d, 0.1d, 0.05d);
                }
            }
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !this.inGround && !this.isNoPhysics()) {
            if (ModProjectiles.getSwooshSoundEnabled(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_SWOOSH_SOUND.get())) {
                int interval = ModProjectiles.getSwooshInterval(this.getWeaponItem(), ProjectileCommonConfig.RING_PROJECTILE_SWOOSH_INTERVAL.get());
                if (this.tickCount % interval == 0) {
                    String soundId = ModProjectiles.getSwooshSoundId(this.getWeaponItem(), "jaams_weaponry:swoosh_air");
                    ModUtils.playSound(this, soundId, SoundSource.AMBIENT, 0.3F, 1.0F);
                }
            }
        }
    }
}
