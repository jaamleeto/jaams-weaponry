package net.jaams.weaponry.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class GiantShurikenProjectileEntity extends BaseWeaponProjectileEntity {

    public GiantShurikenProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.GIANT_SHURIKEN_PROJECTILE.get(), world);
    }

    public GiantShurikenProjectileEntity(EntityType<? extends GiantShurikenProjectileEntity> type, Level world) {
        super(type, world);
    }

    public GiantShurikenProjectileEntity(EntityType<? extends GiantShurikenProjectileEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public GiantShurikenProjectileEntity(EntityType<? extends GiantShurikenProjectileEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
    }

    public GiantShurikenProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
        super(ModEntities.GIANT_SHURIKEN_PROJECTILE.get(), entity, world, weaponItem);
        initializeProjectileStats(weaponItem);
        this.refreshDimensions();
    }

    @Override
    protected float getDefaultBaseDamage(ItemStack weapon) {
        return ModProjectiles.getBaseDamage(weapon, ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_DAMAGE.get());
    }

    @Override
    protected float getDefaultBaseKnockback(ItemStack weapon) {
        return ModProjectiles.getBaseKnockback(weapon, ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_KNOCKBACK.get());
    }

    @Override
    protected int getDefaultPierceLevel(ItemStack weapon) {
        return ModProjectiles.getPiercingLevel(weapon, ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_PIERCING_LEVEL.get());
    }

    @Override
    public ItemStack getDefaultWeaponItem() {
        return new ItemStack(TopItems.GIANT_SHURIKEN.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModProjectiles.getDimensions(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_HITBOX_WIDTH.get().floatValue(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_HITBOX_HEIGHT.get().floatValue());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileHitSound", "projectile_hit", "giant_shuriken_hit", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileGroundSound", "projectile_ground", "giant_shuriken_ground", SoundEvents.TRIDENT_HIT_GROUND, (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileReturnSound", "projectile_return", "giant_shuriken_return", SoundEvents.TRIDENT_RETURN, (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.handleSweepingShot(result.getEntity());
        }
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.handleSweepingShot(null);
        }
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(this.getWeaponItem(), 100);
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        return ModProjectiles.isCustomBreakableBlock(this.getWeaponItem(), state, new ResourceLocation("minecraft:pointed_dripstone"));
    }

    @Override
    protected float getWaterInertia() {
        return ModProjectiles.getWaterInertia(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_WATER_INERTIA.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected void tickDespawn() {
        life++;
        int despawnTime = ModProjectiles.getDespawnTicks(this.getWeaponItem(), ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_DESPAWN_TIME.get());
        if (life >= despawnTime) {
            if (ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_DESPAWN_AS_ITEM.get() && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
                ModProjectiles.dropAsItem(this.level(), this.getPickupItem().copy(), this.getX(), this.getY(), this.getZ());
            } else if (!this.level().isClientSide) {
                ItemStack itemStack = this.weaponItem.copy();
                if (!itemStack.isEmpty()) {
                    ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), getX(), getY() + 0.5, getZ(), 5, 0.1d, 0.1d, 0.1d, 0.05d);
                }
            }
            discard();
        }
    }

    private void handleSweepingShot(@Nullable Entity targetHit) {
        if (!ModProjectiles.getSweepingShotEnabled(this.weaponItem, TraitsConfig.SWEEPING_SHOT.get())) {
            return;
        }
        double radius = ModProjectiles.getSweepingShotRadius(this.weaponItem, TraitsConfig.SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_RADIUS.get().doubleValue());
        double damageFactor = ModProjectiles.getSweepingShotDamageFactor(this.weaponItem, TraitsConfig.SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_DAMAGE_FACTOR.get().doubleValue());
        Level level = this.level();
        AABB area = this.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, (entity) -> entity != this.getOwner() && entity != targetHit);
        float baseDamage = this.calculateProjectileDamage(this.weaponItem, (float) this.getBaseDamage(), false, this.isCritical(), this.level(), this.position(), targetHit);
        float sweepDamage = baseDamage * (float) damageFactor;
        boolean hitAny = false;
        for (LivingEntity entity : targets) {
            if (entity.hurt(level.damageSources().arrow(this, this.getOwner()), sweepDamage)) {
                hitAny = true;
            }
        }
        if (hitAny && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
