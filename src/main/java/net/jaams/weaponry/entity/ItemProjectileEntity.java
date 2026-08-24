package net.jaams.weaponry.entity;

import net.jaams.weaponry.handler.behavior.projectile.ItemProjectileBehaviorHandler;
import net.jaams.weaponry.component.projectile.BaseItemProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.particle.SmallWaveParticleData;
import net.jaams.weaponry.registry.WoodItems;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModProjectiles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemProjectileEntity extends BaseItemProjectileEntity {

    private final ItemProjectileBehaviorHandler behaviorHandler;
    private int tntFuse = -1;
    private int slimeBounceCount = 0;

    public ItemProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.ITEM_PROJECTILE.get(), world);
        this.behaviorHandler = new ItemProjectileBehaviorHandler(this);
        initializeProperties();
    }

    public ItemProjectileEntity(EntityType<? extends ItemProjectileEntity> type, Level world) {
        super(type, world);
        this.behaviorHandler = new ItemProjectileBehaviorHandler(this);
        initializeProperties();
    }

    public ItemProjectileEntity(EntityType<? extends ItemProjectileEntity> type, double x, double y, double z,
            Level world) {
        super(type, x, y, z, world);
        this.behaviorHandler = new ItemProjectileBehaviorHandler(this);
        initializeProperties();
    }

    public ItemProjectileEntity(EntityType<? extends ItemProjectileEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
        this.behaviorHandler = new ItemProjectileBehaviorHandler(this);
        initializeProperties();
    }

    public ItemProjectileEntity(Level world, LivingEntity shooter, ItemStack sourceItem) {
        super(ModEntities.ITEM_PROJECTILE.get(), shooter, world, sourceItem);
        this.behaviorHandler = new ItemProjectileBehaviorHandler(this);
        initializeProperties();
    }

    private void initializeProperties() {
        ItemStack preferred = getPreferredItem();
        this.setProjectileDamage(
                ModProjectiles.getBaseDamage(preferred, ProjectileCommonConfig.ITEM_PROJECTILE_BASE_DAMAGE.get()));
        this.setProjectileKnockback(ModProjectiles.getBaseKnockback(preferred,
                ProjectileCommonConfig.ITEM_PROJECTILE_BASE_KNOCKBACK.get()));
        this.setPiercingLevel(ModProjectiles.getPiercingLevel(preferred,
                ProjectileCommonConfig.ITEM_PROJECTILE_PIERCING_LEVEL.get()));
    }

    @Override
    public ItemStack getDefaultSourceItem() {
        return new ItemStack(WoodItems.WOODEN_SLINGSHOT.get());
    }

    @Override
    public ItemStack getDefaultProjectileItem() {
        return new ItemStack(Items.OAK_LOG);
    }

    @Override
    protected SoundEvent getHitSound() {
        SoundType blockSound = getProjectileBlockSoundType();
        if (blockSound != null) {
            return blockSound.getBreakSound();
        }
        return ModProjectiles.getCustomProjectileSound(getPreferredItem(), "ProjectileHitSound", "empty_sound",
                "empty_sound", SoundEvents.TRIDENT_HIT, (projectileEntry) -> projectileEntry.hit_sound);
    }

    @Override
    protected SoundEvent getGroundSound() {
        SoundType blockSound = getProjectileBlockSoundType();
        if (blockSound != null) {
            return blockSound.getPlaceSound();
        }
        return ModProjectiles.getCustomProjectileSound(getPreferredItem(), "ProjectileGroundSound", "empty_sound",
                "empty_sound", SoundEvents.TRIDENT_HIT_GROUND, (projectileEntry) -> projectileEntry.ground_sound);
    }

    @Override
    protected SoundEvent getLoyaltySound() {
        return ModProjectiles.getCustomProjectileSound(getPreferredItem(), "ProjectileReturnSound", "projectile_return",
                "projectile_return", SoundEvents.TRIDENT_RETURN, (projectileEntry) -> projectileEntry.loyalty_sound);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            behaviorHandler.onTick(serverLevel);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        behaviorHandler.onHitEntity(result);
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        behaviorHandler.onHitBlock(result);
    }

    private ItemStack getPreferredItem() {
        ItemStack proj = this.getProjectileItem();
        if (proj != null && !proj.isEmpty()) {
            return proj;
        }
        return this.getSourceItem();
    }

    @Override
    public int getMaxTicksInAir() {
        return ModProjectiles.getMaxTicksInAir(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_MAX_TICKS_IN_AIR.get());
    }

    @Override
    public int getMaxTicksInGround() {
        return ModProjectiles.getMaxTicksInGround(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_MAX_TICKS_IN_GROUND.get());
    }

    @Override
    public int getNoGravityDuration() {
        
        if (isSlingshotProjectile()) {
            return 0;
        }
        return ModProjectiles.getNoGravityDuration(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_NO_GRAVITY_DURATION.get());
    }

    @Override
    public boolean hasInitialNoGravity() {
        
        if (isSlingshotProjectile()) {
            return false;
        }
        return ModProjectiles.hasInitialNoGravity(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_INITIAL_NO_GRAVITY.get());
    }

    @Override
    protected boolean shouldMaintainNoGravity() {
        
        if (isSlingshotProjectile()) {
            return false;
        }
        return super.shouldMaintainNoGravity();
    }

    
    private boolean isSlingshotProjectile() {
        if (this.getPersistentData().getBoolean("SlingshotProjectile")) {
            return true;
        }
        ItemStack src = getSourceItem();
        return !src.isEmpty() && ModCompats.isSlingshot(src);
    }

    @Override
    protected boolean shouldBreakOnEntityHit() {
        ItemStack proj = getProjectileItem();
        if (proj.is(Items.EGG) || proj.getItem() instanceof SpawnEggItem)
            return true;
        return ModProjectiles.shouldBreakOnEntityHit(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_BREAK_ON_ENTITY_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnBlockHit() {
        ItemStack proj = getProjectileItem();
        if (proj.is(Items.EGG) || proj.getItem() instanceof SpawnEggItem)
            return true;
        return ModProjectiles.shouldBreakOnBlockHit(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_BREAK_ON_BLOCK_HIT.get());
    }

    @Override
    protected boolean shouldBreakOnPiercingExhausted() {
        return ModProjectiles.shouldBreakOnPiercingExhausted(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED.get());
    }

    @Override
    protected boolean shouldBreakAfterMaxBlockBreaks() {
        return ModProjectiles.shouldBreakAfterMaxBlockBreaks(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public int getMaxBlockBreaks() {
        return ModProjectiles.getMaxBlockBreaks(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_MAX_BLOCK_BREAKS.get());
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity shieldHolder, Entity owner) {
        return ModProjectiles.canDisableShield(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_DISABLE_SHIELD.get());
    }

    @Override
    public int getShieldDisableCooldownTicks() {
        return ModProjectiles.getShieldDisableCooldownTicks(getPreferredItem(), 100);
    }

    @Override
    public int getIgnoreHitTicks() {
        return ModProjectiles.getIgnoreHitTicks(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_IGNORE_TICKS.get());
    }

    @Override
    public boolean isCritical() {
        if (!ModProjectiles.getAllowCriticals(getPreferredItem(),
                ProjectileCommonConfig.ITEM_PROJECTILE_ALLOW_CRITICALS.get()))
            return false;
        return super.isCritical();
    }

    @Override
    protected boolean isCustomBreakableBlock(BlockState state) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return (ModProjectiles.isCustomBreakableBlock(getPreferredItem(), state,
                ResourceLocation.parse("minecraft:pointed_dripstone")) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glasses"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glass"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:glass_panes"))) ||
                state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse("jaams_weaponry:sharpstone_can_breaks")))
                ||
                blockId.getPath().contains("glass") ||
                blockId.getPath().contains("pane"));
    }

    private SoundType getProjectileBlockSoundType() {
        ItemStack proj = getProjectileItem();
        if (proj != null && !proj.isEmpty() && proj.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState().getSoundType();
        }
        return null;
    }

    @Override
    protected void handleParticleTrail() {
        if (!this.level().isClientSide || this.inGround || this.isInLava()) {
            return;
        }
        if (shouldShowWaveParticles()) {
            int interval = getWaveSpawnInterval();
            if (this.ticksInAir % interval == 0 && waveParticleCount < 5) {
                float scale = getWaveScale();
                int color = getWaveColor();
                float[] rgb = varyRGB(random, color);
                Vec3 motion = getDeltaMovement();
                double offsetScale = 0.1D;
                double offsetX = motion.x * offsetScale + (random.nextDouble() - 0.5D) * 0.05D;
                double offsetY = motion.y * offsetScale + (random.nextDouble() - 0.5D) * 0.05D + 0.2D;
                double offsetZ = motion.z * offsetScale + (random.nextDouble() - 0.5D) * 0.05D;
                SmallWaveParticleData particle = new SmallWaveParticleData(rgb[0], rgb[1], rgb[2], scale);
                level().addParticle(particle, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, 0.0D, 0.0D, 0.0D);
                waveParticleCount++;
            }
        }
        if (shouldShowTrail()) {
            double spawnRate = getTrailSpawnRate();
            if (spawnRate > 0.0) {
                int interval = Math.max(1, (int) (1.0 / Math.max(0.01, spawnRate)));
                if (this.tickCount % interval == 0) {
                    ParticleOptions particle = ModProjectiles.getTrailParticle(this.getProjectileItem(),
                            ParticleTypes.CRIT);
                    spawnTrailParticle(particle);
                }
            }
        }
    }

    protected boolean shouldShowWaveParticles() {
        CompoundTag tag = getPreferredTag("ProjectileWaveEnabled");
        if (tag.contains("ProjectileWaveEnabled", Tag.TAG_BYTE)) {
            return tag.getBoolean("ProjectileWaveEnabled");
        }
        return true;
    }

    protected int getWaveSpawnInterval() {
        CompoundTag tag = getPreferredTag("ProjectileWaveInterval");
        if (tag.contains("ProjectileWaveInterval", Tag.TAG_INT)) {
            return Math.max(1, tag.getInt("ProjectileWaveInterval"));
        }
        return 4;
    }

    protected float getWaveScale() {
        CompoundTag tag = getPreferredTag("ProjectileWaveScale");
        if (tag.contains("ProjectileWaveScale", Tag.TAG_FLOAT)) {
            return Mth.clamp(tag.getFloat("ProjectileWaveScale"), 0.05F, 2.0F);
        }
        return 0.25F;
    }

    protected int getWaveColor() {
        CompoundTag tag = getPreferredTag("ProjectileWaveColor");
        if (tag.contains("ProjectileWaveColor", Tag.TAG_INT)) {
            return tag.getInt("ProjectileWaveColor");
        }
        return this.getColor();
    }

    protected boolean shouldShowTrail() {
        CompoundTag tag = getPreferredTag("ProjectileTrailEnabled");
        if (tag.contains("ProjectileTrailEnabled", Tag.TAG_BYTE)) {
            return tag.getBoolean("ProjectileTrailEnabled");
        }
        return false;
    }

    protected double getTrailSpawnRate() {
        CompoundTag tag = getPreferredTag("ProjectileTrailSpawnRate");
        if (tag.contains("ProjectileTrailSpawnRate", Tag.TAG_DOUBLE)) {
            return tag.getDouble("ProjectileTrailSpawnRate");
        }
        return 1.0;
    }

    private CompoundTag getPreferredTag(String key) {
        ItemStack proj = this.getProjectileItem();
        if (!proj.isEmpty()) {
            CompoundTag tag = proj.getOrCreateTag();
            if (tag.contains(key))
                return tag;
        }
        ItemStack src = this.getSourceItem();
        if (!src.isEmpty()) {
            return src.getOrCreateTag();
        }
        return new CompoundTag();
    }

    protected void spawnTrailParticle(ParticleOptions particle) {
        Vec3 motion = this.getDeltaMovement().normalize();
        double offset = 0.45;
        double x = this.getX() - motion.x * offset;
        double y = this.getY() + 0.15 - motion.y * offset;
        double z = this.getZ() - motion.z * offset;
        this.level().addParticle(particle, x, y, z, 0.0D, 0.02D, 0.0D);
    }

    public SynchedEntityData getEntityData() {
        return this.entityData;
    }

    public boolean isInGround() {
        return this.inGround;
    }

    public void setInGround(boolean value) {
        this.inGround = value;
    }

    public int getTntFuse() {
        return this.tntFuse;
    }

    public void setTntFuse(int value) {
        this.tntFuse = value;
    }

    public int getSlimeBounceCount() {
        return this.slimeBounceCount;
    }

    public void incrementSlimeBounceCount() {
        this.slimeBounceCount++;
    }

    public void setNoGravityTicks(int ticks) {
        this.noGravityTicks = ticks;
    }

    public void setPlaceableBlocks(String mode) {
        if (mode == null || mode.isEmpty())
            return;
        this.getPersistentData().putString("ProjectilePlaceableBlocks", mode);
    }
}
