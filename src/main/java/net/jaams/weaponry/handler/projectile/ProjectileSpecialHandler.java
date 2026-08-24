package net.jaams.weaponry.handler.projectile;

import org.joml.Vector3f;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.FakePlayer;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class ProjectileSpecialHandler {
    private final ItemProjectileEntity projectile;
    private FakePlayer fakePlayer;

    public ProjectileSpecialHandler(ItemProjectileEntity projectile) {
        this.projectile = projectile;
    }

    private FakePlayer getFakePlayer() {
        if (fakePlayer == null || fakePlayer.isRemoved()) {
            fakePlayer = FakePlayerFactory.get((ServerLevel) projectile.level(),
                    new GameProfile(UUID.randomUUID(), "ProjectileFake"));
        }
        return fakePlayer;
    }

    public void onTick(ServerLevel serverLevel, ItemStack stack) {
        if (projectile.isInGround() || projectile.isRemoved() || stack.isEmpty()) {
            return;
        }
        Item item = stack.getItem();
        if (item instanceof DyeItem) {
            handleDyeParticle(serverLevel, stack);
        } else if (stack.is(Items.INK_SAC)) {
            handleInkParticle(serverLevel);
        } else if (stack.is(Items.GLOW_INK_SAC)) {
            handleGlowInkParticle(serverLevel);
        }
    }

    public void onHitEntity(Entity target, ItemStack stack) {
        if (handleCommonImpact(stack, null)) {
            return;
        }
        if (isFireItem(stack)) {
            handleFireImpact(target, stack);
            return;
        }
        if (stack.is(Items.LEAD)) {
            handleLeadImpact(target);
            return;
        }
        if (stack.is(Items.ENDER_EYE)) {
            return;
        }
        if (getArmorSlotForItem(stack.getItem()) != null) {
            handleArmorEquipImpact(target, stack);
            return;
        }
        if (isPotion(stack) || isBottleItem(stack)) {
            return;
        }
        tryGeneralEntityInteraction(target, stack);
    }

    public void onHitBlock(BlockHitResult result, ItemStack stack) {
        if (handleCommonImpact(stack, result)) {
            return;
        }
        if (stack.is(Items.BONE_MEAL)) {
            handleBonemealImpact(result);
            return;
        }
        if (stack.is(Items.ENDER_EYE)) {
            return;
        }
        if (isPotion(stack) || isBottleItem(stack)) {
            return;
        }
        if (stack.getItem() instanceof BlockItem) {
            return;
        }
        tryGeneralBlockUse(result, stack);
    }

    private boolean handleCommonImpact(ItemStack stack, BlockHitResult blockResult) {
        if (stack.is(Items.EGG)) {
            handleEggImpact();
            return true;
        }
        if (stack.getItem() instanceof SpawnEggItem) {
            Vec3 pos = blockResult != null ? blockResult.getLocation().add(0, 0.5, 0) : projectile.position();
            handleSpawnEggImpact(pos);
            return true;
        }
        if (isDynamite(stack)) {
            handleDynamiteImpact(stack);
            return true;
        }
        if (ModCompats.isSmokeBomb(stack)) {
            handleSmokeBombImpact();
            return true;
        }
        if (stack.is(Items.ENDER_PEARL)) {
            handleEnderPearlImpact();
            return true;
        }
        if (stack.is(Items.DRAGON_BREATH)) {
            handleDragonBreathImpact();
            return true;
        }
        if (stack.is(Items.SNOWBALL)) {
            handleSnowballImpact();
            return true;
        }
        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            handleExperienceBottleImpact();
            return true;
        }
        return false;
    }

    private boolean isFireItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE || item == Items.BLAZE_POWDER) {
            return true;
        }
        return ModUtils.getItemIdLowercase(stack).contains("fire");
    }

    private boolean isDynamite(ItemStack stack) {
        if (ModCompats.isDynamiteThrowable(stack))
            return true;
        String id = ModUtils.getItemIdLowercase(stack);
        return id.contains("dynamite") || id.contains("explosive") || id.contains("tnt");
    }

    private boolean isPotion(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof PotionItem || item instanceof SplashPotionItem || item instanceof LingeringPotionItem;
    }

    private boolean isBottleItem(ItemStack stack) {
        return ModUtils.getItemIdLowercase(stack).contains("bottle");
    }

    private void tryGeneralBlockUse(BlockHitResult result, ItemStack stack) {
        if (projectile.level().isClientSide)
            return;
        Entity owner = projectile.getOwner();
        if (owner instanceof Player player && !player.mayBuild())
            return;
        ServerLevel level = (ServerLevel) projectile.level();
        FakePlayer fp = getFakePlayer();
        setupFakePlayer(fp, owner);
        InteractionResult res = stack.getItem()
                .useOn(new UseOnContext(level, fp, InteractionHand.MAIN_HAND, stack, result));
        if (!res.consumesAction()) {
            var holder = stack.getItem().use(level, fp, InteractionHand.MAIN_HAND);
            res = holder.getResult();
            if (!holder.getObject().isEmpty()) {
                projectile.setProjectileItem(holder.getObject());
            }
        }
        if (res.consumesAction()) {
            handleItemUsageResult(stack);
        }
    }

    private void tryGeneralEntityInteraction(Entity target, ItemStack stack) {
        if (projectile.level().isClientSide || !(target instanceof LivingEntity livingTarget))
            return;
        Entity owner = projectile.getOwner();
        if (!(owner instanceof Player))
            return;
        ServerLevel level = (ServerLevel) projectile.level();
        FakePlayer fp = getFakePlayer();
        setupFakePlayer(fp, owner);
        InteractionResult res = stack.getItem().interactLivingEntity(stack, fp, livingTarget,
                InteractionHand.MAIN_HAND);
        if (!res.consumesAction()) {
            var holder = stack.getItem().use(level, fp, InteractionHand.MAIN_HAND);
            res = holder.getResult();
            if (!holder.getObject().isEmpty()) {
                projectile.setProjectileItem(holder.getObject());
            }
        }
        if (res.consumesAction()) {
            handleItemUsageResult(stack);
        }
    }

    private void handleItemUsageResult(ItemStack stack) {
        if (stack.isDamageableItem() && stack.getMaxDamage() > 0) {
            stack.hurt(1, projectile.level().random, null);
            projectile.setProjectileItem(stack);
        } else {
            projectile.discard();
        }
    }

    private void handleDyeParticle(ServerLevel serverLevel, ItemStack stack) {
        int dyeColor = ModUtils.getDyeColorValue(stack);
        if (dyeColor == -1)
            return;
        float r = ((dyeColor >> 16) & 0xFF) / 255.0F;
        float g = ((dyeColor >> 8) & 0xFF) / 255.0F;
        float b = (dyeColor & 0xFF) / 255.0F;
        spawnDustTrail(serverLevel, r, g, b);
    }

    private void handleInkParticle(ServerLevel serverLevel) {
        spawnDustTrail(serverLevel, 0.1f, 0.1f, 0.1f);
    }

    private void handleGlowInkParticle(ServerLevel serverLevel) {
        spawnDustTrail(serverLevel, 0.6f, 0.9f, 0.9f);
    }

    private void spawnDustTrail(ServerLevel serverLevel, float r, float g, float b) {
        Vec3 motion = projectile.getDeltaMovement().normalize();
        double px = projectile.getX() - motion.x * 0.4;
        double py = projectile.getY() + 0.2 - motion.y * 0.4;
        double pz = projectile.getZ() - motion.z * 0.4;
        serverLevel.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), 2.5f), px, py, pz, 4, 0.15, 0.15, 0.15,
                0.0);
    }

    private void handleEggImpact() {
        if (projectile.level().isClientSide)
            return;
        Level level = projectile.level();
        level.playSound(null, projectile.blockPosition(), SoundEvents.EGG_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level.random.nextFloat() < 0.25F) {
            int count = level.random.nextInt(8) == 0 ? 4 : 1;
            for (int i = 0; i < count; i++) {
                Chicken chicken = EntityType.CHICKEN.create(level);
                if (chicken != null) {
                    chicken.setAge(-24000);
                    chicken.moveTo(projectile.getX(), projectile.getY(), projectile.getZ(), 0, 0);
                    level.addFreshEntity(chicken);
                }
            }
        }
        projectile.discard();
    }

    private void handleSpawnEggImpact(Vec3 impactPos) {
        if (projectile.level().isClientSide)
            return;
        ItemStack proj = projectile.getProjectileItem();
        if (!(proj.getItem() instanceof SpawnEggItem spawnEgg)) {
            projectile.discard();
            return;
        }
        EntityType<?> type = spawnEgg.getType(proj.getTag());
        Entity entity = type.create(projectile.level());
        if (entity != null) {
            entity.moveTo(impactPos.x, impactPos.y, impactPos.z, projectile.level().random.nextFloat() * 360.0F, 0.0F);
            if (entity instanceof AgeableMob ageable)
                ageable.setAge(0);
            if (entity instanceof Mob mob)
                mob.setDeltaMovement(0, 0.25, 0);
            projectile.level().addFreshEntity(entity);
        }
        projectile.discard();
    }

    private void handleEnderPearlImpact() {
        if (projectile.level().isClientSide)
            return;
        Entity owner = projectile.getOwner();
        Level level = projectile.level();
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 32; ++i) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, projectile.getX(),
                        projectile.getY() + level.random.nextDouble() * 2.0D, projectile.getZ(), 1,
                        level.random.nextGaussian(), 0.0D, level.random.nextGaussian(), 0.02);
            }
        }
        if (owner instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.connection.isAcceptingMessages() && serverPlayer.level() == level
                    && !serverPlayer.isSleeping()) {
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderPearl event = net.minecraftforge.event.ForgeEventFactory
                        .onEnderPearlLand(serverPlayer, projectile.getX(), projectile.getY(), projectile.getZ(), null,
                                5.0F, null);
                if (!event.isCanceled()) {
                    if (level.random.nextFloat() < 0.05F
                            && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        Endermite endermite = EntityType.ENDERMITE.create(level);
                        if (endermite != null) {
                            endermite.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(),
                                    owner.getXRot());
                            level.addFreshEntity(endermite);
                        }
                    }
                    level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (owner.isPassenger())
                        serverPlayer.dismountTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    else
                        owner.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    owner.resetFallDistance();
                    owner.hurt(projectile.damageSources().fall(), event.getAttackDamage());
                }
            }
        } else if (owner != null) {
            owner.teleportTo(projectile.getX(), projectile.getY(), projectile.getZ());
            owner.resetFallDistance();
            level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        projectile.discard();
    }

    private void handleSmokeBombImpact() {
        if (projectile.level().isClientSide || !(projectile.level() instanceof ServerLevel serverLevel))
            return;
        ItemStack stack = projectile.getProjectileItem();
        Vec3 pos = projectile.position();
        double radius = 3.0;
        double enemyProb = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombEnemyEffectProbability",
                ItemFeaturesConfig.SMOKE_BOMB_ENEMY_BLIND_PROBABILITY::get);
        AABB area = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius,
                pos.z + radius);
        for (LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class, area)) {
            if (serverLevel.random.nextFloat() < enemyProb) {
                String effectId = ModUtils.getConfigOrNbtString(stack, "SmokeBombEnemyEffect",
                        () -> "minecraft:blindness");
                int duration = ModUtils.getConfigOrNbtInt(stack, "SmokeBombEnemyDuration", () -> 60);
                int amplifier = ModUtils.getConfigOrNbtInt(stack, "SmokeBombEnemyAmplifier", () -> 0);
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(effectId));
                if (effect != null)
                    living.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
            } else if (living instanceof Mob mob) {
                mob.setTarget(null);
            }
        }
        int particleCount = ModUtils.getConfigOrNbtInt(stack, "SmokeBombParticleCount",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_COUNT::get);
        double range = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombParticleRange",
                ItemFeaturesConfig.SMOKE_BOMB_PARTICLE_RANGE::get);
        ParticleOptions particleOption = getSmokeParticle(stack);
        for (int i = 0; i < particleCount; i++) {
            double ox = (serverLevel.random.nextDouble() - 0.5) * range;
            double oy = (serverLevel.random.nextDouble() - 0.5) * range * 0.6;
            double oz = (serverLevel.random.nextDouble() - 0.5) * range;
            serverLevel.sendParticles(particleOption, pos.x + ox, pos.y + 0.8 + oy, pos.z + oz, 1, 0, 0, 0, 0);
        }
        if (particleOption != ParticleTypes.LARGE_SMOKE) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 12, 0.6, 0.6, 0.6, 0.05);
        }
        playSmokeBombSound(serverLevel, pos, stack);
        projectile.discard();
    }

    private ParticleOptions getSmokeParticle(ItemStack stack) {
        String particleId = ModUtils.getConfigOrNbtString(stack, "SmokeBombParticle", () -> "minecraft:large_smoke");
        if (particleId.toLowerCase().contains("dust")) {
            int color = ModUtils.getConfigOrNbtInt(stack, "SmokeBombDustColor", () -> 0x333333);
            float scale = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombDustScale", () -> 1.0);
            return new DustParticleOptions(new Vector3f(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f,
                    (color & 0xFF) / 255.0f), scale);
        }
        ParticleType<?> type = ForgeRegistries.PARTICLE_TYPES.getValue(ResourceLocation.parse(particleId));
        return type instanceof SimpleParticleType simple ? simple : ParticleTypes.LARGE_SMOKE;
    }

    private static void playSmokeBombSound(ServerLevel level, Vec3 pos, ItemStack stack) {
        String soundId = ModUtils.getConfigOrNbtString(stack, "SmokeBombSound", () -> "jaams_weaponry:smoke_bomb");
        float vol = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundVolume", () -> 1.0);
        float pitch = (float) ModUtils.getConfigOrNbtDouble(stack, "SmokeBombSoundPitch", () -> 1.0);
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse(soundId));
        if (sound != null) {
            level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS,
                    Mth.clamp(vol * (0.85f + level.random.nextFloat() * 0.3f), 0.1f, 2.0f),
                    Mth.clamp(pitch * (0.85f + level.random.nextFloat() * 0.4f), 0.5f, 2.0f));
        }
    }

    private void handleDragonBreathImpact() {
        if (projectile.level().isClientSide)
            return;
        Level level = projectile.level();
        AreaEffectCloud cloud = new AreaEffectCloud(level, projectile.getX(), projectile.getY(), projectile.getZ());
        cloud.setOwner(projectile.getOwner() instanceof LivingEntity living ? living : null);
        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(600);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.setParticle(ParticleTypes.DRAGON_BREATH);
        cloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
        level.addFreshEntity(cloud);
        level.playSound(null, projectile.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.levelEvent(2006, projectile.blockPosition(), 0);
        projectile.discard();
    }

    private void handleSnowballImpact() {
        if (projectile.level().isClientSide)
            return;
        Level level = projectile.level();
        level.playSound(null, projectile.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, projectile.getProjectileItem()),
                    projectile.getX(), projectile.getY(), projectile.getZ(), 8, 0.1, 0.1, 0.1, 0.05);
        }
        projectile.discard();
    }

    private void handleBonemealImpact(BlockHitResult result) {
        if (projectile.level().isClientSide)
            return;
        BlockPos pos = result.getBlockPos();
        Level level = projectile.level();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock bonemealable) {
            if (bonemealable.isValidBonemealTarget(level, pos, state, false)) {
                if (bonemealable.isBonemealSuccess(level, level.random, pos, state)) {
                    bonemealable.performBonemeal((ServerLevel) level, level.random, pos, state);
                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(2005, pos, 0);
                }
            }
        }
        projectile.discard();
    }

    private void handleDynamiteImpact(ItemStack stack) {
        if (projectile.level().isClientSide || !isDynamite(stack))
            return;
        Level level = projectile.level();
        Vec3 pos = projectile.position();
        float power = 2.5f;
        boolean causesFire = false;
        boolean breakBlocks = false;
        if (stack.is(ModItems.DYNAMITE.get())) {
            power = DynamiteProjectileEntity.getExplosionPower(stack);
            causesFire = DynamiteProjectileEntity.causesFire(stack);
            breakBlocks = DynamiteProjectileEntity.shouldBreakBlocks(stack);
        }
        Level.ExplosionInteraction interaction = breakBlocks ? Level.ExplosionInteraction.BLOCK
                : Level.ExplosionInteraction.NONE;
        level.explode(projectile.getOwner(), pos.x, pos.y, pos.z, power, causesFire, interaction);
        projectile.discard();
    }

    private void handleLeadImpact(Entity target) {
        if (projectile.level().isClientSide)
            return;
        if (projectile.isMultishotClone())
            return;
        Entity owner = projectile.getOwner();
        if (target instanceof Mob livingTarget && owner instanceof LivingEntity) {
            livingTarget.setLeashedTo(owner, true);
            projectile.level().playSound(null, target.blockPosition(), SoundEvents.LEASH_KNOT_PLACE,
                    SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        projectile.discard();
    }

    private void handleArmorEquipImpact(Entity target, ItemStack stack) {
        if (projectile.level().isClientSide)
            return;
        Item item = stack.getItem();
        if (!(target instanceof LivingEntity livingTarget))
            return;
        EquipmentSlot slot = getArmorSlotForItem(item);
        if (slot == null)
            return;
        ItemStack currentInSlot = livingTarget.getItemBySlot(slot);
        if (!currentInSlot.isEmpty()
                && !(currentInSlot.getItem() instanceof ArmorItem currentArmor && item instanceof ArmorItem newArmor
                        && newArmor.getMaterial().getDefenseForType(((ArmorItem) item).getType()) > currentArmor
                                .getMaterial().getDefenseForType(currentArmor.getType()))) {
            return;
        }
        livingTarget.setItemSlot(slot, stack.copy());
        livingTarget.level().playSound(null, livingTarget.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        projectile.discard();
    }

    private EquipmentSlot getArmorSlotForItem(Item item) {
        if (item instanceof ArmorItem armorItem) {
            return armorItem.getEquipmentSlot();
        }
        if (item == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        return null;
    }

    private void setupFakePlayer(FakePlayer fakePlayer, Entity owner) {
        if (owner instanceof Player realPlayer) {
            fakePlayer.setPos(realPlayer.getX(), realPlayer.getY(), realPlayer.getZ());
            fakePlayer.setYRot(realPlayer.getYRot());
            fakePlayer.setXRot(realPlayer.getXRot());
            fakePlayer.setUUID(realPlayer.getUUID());
            fakePlayer.getAbilities().mayBuild = realPlayer.getAbilities().mayBuild;
            fakePlayer.getAbilities().instabuild = realPlayer.getAbilities().instabuild;
        } else if (owner != null) {
            fakePlayer.setPos(owner.getX(), owner.getY(), owner.getZ());
            fakePlayer.setYRot(owner.getYRot());
            fakePlayer.setXRot(owner.getXRot());
        }
        fakePlayer.getInventory().clearContent();
    }

    private void handleFireImpact(Entity target, ItemStack stack) {
        if (!(target instanceof LivingEntity livingTarget)) {
            handleItemUsageResult(stack);
            return;
        }
        Level level = projectile.level();
        SoundEvent sound = getFireImpactSound(stack);
        livingTarget.setSecondsOnFire(6);
        livingTarget.hurt(level.damageSources().onFire(), 2.0F);
        level.playSound(null, target.blockPosition(), sound, SoundSource.PLAYERS, 1.0F,
                0.9F + level.random.nextFloat() * 0.2F);
        handleItemUsageResult(stack);
    }

    private SoundEvent getFireImpactSound(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.FLINT_AND_STEEL)
            return SoundEvents.FLINTANDSTEEL_USE;
        if (item == Items.FIRE_CHARGE)
            return SoundEvents.FIRECHARGE_USE;
        if (item == Items.BLAZE_POWDER)
            return SoundEvents.BLAZE_SHOOT;
        return SoundEvents.FIRECHARGE_USE;
    }

    private void handleExperienceBottleImpact() {
        if (projectile.level() instanceof ServerLevel serverLevel) {
            serverLevel.levelEvent(2002, projectile.blockPosition(), PotionUtils.getColor(Potions.WATER));
            int i = 3 + serverLevel.random.nextInt(5) + serverLevel.random.nextInt(5);
            ExperienceOrb.award(serverLevel, projectile.position(), i);
            projectile.discard();
        }
    }
}
