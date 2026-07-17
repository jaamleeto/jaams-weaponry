package net.jaams.weaponry.entity;

import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class ProngedKunaiProjectileEntity extends BaseWeaponProjectileEntity {

    public ProngedKunaiProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.PRONGED_KUNAI_PROJECTILE.get(), world);
    }

    public ProngedKunaiProjectileEntity(EntityType<? extends ProngedKunaiProjectileEntity> type, Level world) {
        super(type, world);
    }

    public ProngedKunaiProjectileEntity(EntityType<? extends ProngedKunaiProjectileEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public ProngedKunaiProjectileEntity(EntityType<? extends ProngedKunaiProjectileEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
    }

    public ProngedKunaiProjectileEntity(Level world, LivingEntity entity, ItemStack weaponItem) {
        super(ModEntities.PRONGED_KUNAI_PROJECTILE.get(), entity, world, weaponItem);
        initializeProjectileStats(weaponItem);
    }

    @Override
    protected float getDefaultBaseDamage(ItemStack weapon) {
        return ModProjectiles.getBaseDamage(weapon, ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_DAMAGE.get());
    }

    @Override
    protected float getDefaultBaseKnockback(ItemStack weapon) {
        return ModProjectiles.getBaseKnockback(weapon, ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_KNOCKBACK.get());
    }

    @Override
    protected int getDefaultPierceLevel(ItemStack weapon) {
        return ModProjectiles.getPiercingLevel(weapon, ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_PIERCING_LEVEL.get());
    }

    @Override
    public ItemStack getDefaultWeaponItem() {
        return new ItemStack(TopItems.PRONGED_KUNAI.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModProjectiles.getDimensions(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_HITBOX_WIDTH.get().floatValue(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_HITBOX_HEIGHT.get().floatValue());
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileHitSound", "projectile_hit", "pronged_kunai_hit", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileGroundSound", "projectile_ground", "pronged_kunai_ground", SoundEvents.TRIDENT_HIT_GROUND, (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(this.getWeaponItem(), "ProjectileReturnSound", "projectile_return", "pronged_kunai_return", SoundEvents.TRIDENT_RETURN, (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            if (ModProjectiles.getBackstabShotEnabled(this.weaponItem, TraitsConfig.BACKSTAB_SHOT.get())) {
                if (ModProjectiles.isHitFromBehind(this, target)) {
                    double multiplier = ModProjectiles.getBackstabShotDamageMultiplier(this.weaponItem, TraitsConfig.BACKSTAB_SHOT_PRONGED_KUNAI_PROJECTILE_MULTIPLIER.get().doubleValue());
                    float consistentBaseDamage = this.calculateProjectileDamage(this.weaponItem, (float) this.getBaseDamage(), false, this.isCritical(), this.level(), this.position(), target);
                    float finalDamage = (float) (consistentBaseDamage * multiplier);
                    if (this.getOwner() instanceof LivingEntity attacker && finalDamage > 0) {
                        RegistryAccess registryAccess = attacker.level().registryAccess();
                        ResourceKey<DamageType> damageKey = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jaams_weaponry:backstab"));
                        DamageSource damageSource = attacker instanceof Player player
                            ? new DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey), player)
                            : new DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey), attacker);
                        target.hurt(damageSource, finalDamage);
                    }
                    if (this.level() instanceof ServerLevel serverLevel) {
                        float entitySize = Math.max(target.getBbWidth(), target.getBbHeight());
                        float particleSize = Math.max(0.25F, entitySize * 0.5F);
                        CustomHitParticleData particleData = new CustomHitParticleData(1.0F, 1.0F, 1.0F, particleSize);
                        serverLevel.sendParticles(particleData, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.8F, 0.9F);
                    }
                }
            }
        }
        if (result.getEntity() instanceof LivingEntity livingTarget) {
            livingTarget.invulnerableTime = 0;
        }
        super.onHitEntity(result);
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_DISABLE_SHIELD.get());
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
        return ModProjectiles.getWaterInertia(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_WATER_INERTIA.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected void tickDespawn() {
        life++;
        int despawnTime = ModProjectiles.getDespawnTicks(this.getWeaponItem(), ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_DESPAWN_TIME.get());
        if (life >= despawnTime) {
            if (ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_DESPAWN_AS_ITEM.get() && this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
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
}
