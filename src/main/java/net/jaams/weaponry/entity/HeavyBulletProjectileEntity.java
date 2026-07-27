package net.jaams.weaponry.entity;

import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileBulletConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.registry.GoldenItems;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.registries.BuiltInRegistries;

public class HeavyBulletProjectileEntity extends BaseBulletProjectileEntity {

    public HeavyBulletProjectileEntity(EntityType<? extends HeavyBulletProjectileEntity> type, Level world) {
        super(type, world);
        initializeProperties();
    }

    public HeavyBulletProjectileEntity(EntityType<? extends HeavyBulletProjectileEntity> type, double x, double y,
            double z, Level world) {
        super(type, x, y, z, world);
        initializeProperties();
    }

    public HeavyBulletProjectileEntity(EntityType<? extends HeavyBulletProjectileEntity> type, LivingEntity entity,
            Level world) {
        super(type, entity, world);
        initializeProperties();
    }

    public HeavyBulletProjectileEntity(Level world, LivingEntity shooter, ItemStack gunItem) {
        super(ModEntities.HEAVY_BULLET_PROJECTILE.get(), shooter, world, gunItem);
        initializeProperties();
    }

    private void initializeProperties() {
        ItemStack gun = this.getGunItem();
        this.setBulletDamage(
                ModProjectiles.getBaseDamage(gun, ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BASE_DAMAGE.get()));
        this.setBulletKnockback(ModProjectiles.getBaseKnockback(gun,
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BASE_KNOCKBACK.get()));
        this.setPiercingLevel(ModProjectiles.getPiercingLevel(gun,
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_PIERCING_LEVEL.get()));
        this.setColor(ModProjectiles.getColor(gun, ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_COLOR));
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
        double knockedOutChance = ModProjectiles.getHeavyKnockedOutChance(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_CHANCE.get());
        if (knockedOutChance > 0 && level().random.nextDouble() < knockedOutChance
                && entity instanceof LivingEntity living) {
            int duration = ModProjectiles.getHeavyKnockedOutDuration(this.getGunItem(),
                    ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_DURATION.get());
            if (duration > 0) {
                living.addEffect(
                        new MobEffectInstance(ModMobEffects.KNOCKED_OUT, duration, 0, false, false, true));
            }
        }
        boolean showParticles = ModProjectiles.getShowIronParticles(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_SHOW_IRON_PARTICLES.get());
        if (showParticles && level() instanceof ServerLevel serverLevel) {
            ItemStack particleStack = new ItemStack(Items.IRON_INGOT);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particleStack), this.getX(),
                    this.getY(), this.getZ(), 10, 0.2, 0.25, 0.2, 0.1);
        }
    }

    @Override
    protected void applyBlockHitEffects(BlockPos pos, BlockState state, BlockHitResult hitResult) {
        boolean showParticles = ModProjectiles.getShowIronParticles(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_SHOW_IRON_PARTICLES.get());
        if (showParticles && level() instanceof ServerLevel serverLevel) {
            Direction face = hitResult.getDirection();
            double offset = 0.01;
            double x = pos.getX() + 0.5 + (face.getStepX() * (0.5 + offset));
            double y = pos.getY() + 0.5 + (face.getStepY() * (0.5 + offset));
            double z = pos.getZ() + 0.5 + (face.getStepZ() * (0.5 + offset));
            ItemStack particleStack = new ItemStack(Items.IRON_INGOT);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particleStack), x, y, z, 6, 0.15, 0.15,
                    0.15, 0.08);
        }
    }

    @Override
    public int getMaxTicksInAir() {
        return ModProjectiles.getMaxTicksInAir(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_AIR.get());
    }

    @Override
    public int getMaxTicksInGround() {
        return ModProjectiles.getMaxTicksInGround(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND.get());
    }

    @Override
    public int getNoGravityDuration() {
        return ModProjectiles.getNoGravityDuration(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_NO_GRAVITY_DURATION.get());
    }

    @Override
    public boolean hasInitialNoGravity() {
        return ModProjectiles.hasInitialNoGravity(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_INITIAL_NO_GRAVITY.get());
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        return ModProjectiles.shouldBreakOnEntityHit(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        return ModProjectiles.shouldBreakOnBlockHit(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(this.getGunItem(), 100);
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(this.getGunItem(),
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
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
                ProjectileBulletConfig.HEAVY_BULLET_PROJECTILE_ALLOW_CRITICALS.get())) {
            return false;
        }
        return super.isCritical();
    }
}
