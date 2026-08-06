package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.handler.event.AdvancementsHandler;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitBusterStrikeMixin {

    @Unique
    private static final String NBT_HITS = "BusterStrikeHits";
    @Unique
    private static final float ARMOR_DAMAGE_CHANCE = 0.25F;
    @Unique
    private static final int ARMOR_DAMAGE_AMOUNT = 1;
    @Unique
    private static final float HELD_ITEM_DAMAGE_CHANCE = 0.25F;
    @Unique
    private static final int HELD_ITEM_DAMAGE_AMOUNT = 1;
    @Unique
    private static final boolean ENABLE_ATTACK_PARTICLES = true;
    @Unique
    private static final int PARTICLE_AREA_SIZE = 3;
    @Unique
    private static final int PARTICLES_PER_BLOCK = 2;
    @Unique
    private static final float PARTICLE_OFFSET_SCALE = 0.3F;
    @Unique
    private static final float PARTICLE_SPEED = 0.0F;
    @Unique
    private static final boolean ENABLE_DEPLETION = true;
    @Unique
    private static final int DEPLETION_DURATION = 40;
    @Unique
    private static final int DEPLETION_LEVEL = 0;

    @Unique
    private boolean isBusterStrikeEnabled(ItemStack stack) {
        if (!TraitsConfig.BUSTER_STRIKE.get())
            return false;
        return ModTraits.isBusterStrikeItem(stack);
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onBusterStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || target.level().isClientSide())
            return;
        if (!isBusterStrikeEnabled(stack))
            return;
        if (!ItemStack.isSameItemSameComponents(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameComponents(attacker.getOffhandItem(), stack))
            return;
        float attackScale = attacker.getAttackStrengthScale(0.5F);
        if (requiresFullyCharged(stack) && attackScale < 0.9F) {
            return;
        }
        Level level = target.level();

        performBusterStrikeEffects(stack, target, attacker, level);
    }

    @Unique
    private void performBusterStrikeEffects(ItemStack stack, LivingEntity entity, LivingEntity sourceentity,
            Level level) {
        
        if (level.random.nextFloat() < ARMOR_DAMAGE_CHANCE) {
            for (EquipmentSlot slot : new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
                ItemStack armor = entity.getItemBySlot(slot);
                if (!armor.isEmpty() && armor.isDamageableItem()) {
                    ModUtils.applyTraitDurabilityCost(armor, entity, ARMOR_DAMAGE_AMOUNT, slot);
                }
            }
        }
        
        if (level.random.nextFloat() < HELD_ITEM_DAMAGE_CHANCE) {
            for (EquipmentSlot slot : new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }) {
                ItemStack held = entity.getItemBySlot(slot);
                if (!held.isEmpty() && held.isDamageableItem()) {
                    ModUtils.applyTraitDurabilityCost(held, entity, HELD_ITEM_DAMAGE_AMOUNT, slot);
                }
            }
        }

        
        CompoundTag nbt = ModComponents.getOrCreate(stack);
        int hits = nbt.getInt(NBT_HITS) + 1;
        int hitsRequired = getRequiredHits(stack);
        boolean appliedStrike = false;
        if (hits >= hitsRequired) {
            hits = 0;
            applyBusterStrikeDamage(sourceentity, entity, stack, level);
            appliedStrike = true;
        }
        nbt.putInt(NBT_HITS, hits);
        ModComponents.set(stack, nbt);

        
        if (sourceentity instanceof Player player) {
            performSweepAttack(stack, entity, player);
        }
    }

    @Unique
    private void applyBusterStrikeDamage(LivingEntity sourceentity, LivingEntity entity, ItemStack stack, Level level) {
        float baseDamage = AmountProvider.get(sourceentity)
                .map(amount -> amount.getDamage())
                .orElse(4.0F);
        float bonusMultiplier = getBonusMultiplier(stack);
        float bonusDamage = baseDamage * bonusMultiplier;
        ModUtils.applyBonusDamage(sourceentity, entity, stack, bonusDamage);

        if (sourceentity instanceof ServerPlayer player) {
            boolean shouldIncrementCounter = false;
            boolean isCreative = player.getAbilities().instabuild;
            if (isCreative) {
                shouldIncrementCounter = true;
            } else {
                if (entity instanceof Player) {
                    shouldIncrementCounter = true;
                } else if (entity instanceof Mob mob) {
                    shouldIncrementCounter = mob.getTarget() == player;
                }
            }
            if (shouldIncrementCounter) {
                AdvancementsHandler.incrementEntityCounterAndCheckAdvancement(player, entity,
                        "buster_strike_count", "ruder_buster", 3);
            }
            int durabilityPenalty = getDurabilityPenalty(stack);
            if (durabilityPenalty > 0) {
                stack.hurtAndBreak(durabilityPenalty, sourceentity, EquipmentSlot.MAINHAND);
            }
        }

        
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    sourceentity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE,
                    1.0F, 1.0F);
            spawnAttackBlockParticles(serverLevel, entity.blockPosition(), entity);
        }

        
        applyDepletionEffect(level, sourceentity);
    }

    @Unique
    private void spawnAttackBlockParticles(ServerLevel serverLevel, BlockPos impactPos, LivingEntity entity) {
        if (!ENABLE_ATTACK_PARTICLES) {
            return;
        }
        boolean isInWater = entity.isUnderWater();
        BlockPos basePos = impactPos.below();
        int halfSize = PARTICLE_AREA_SIZE / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                BlockPos pos = basePos.offset(x, 0, z);
                BlockState state = serverLevel.getBlockState(pos);
                if (!state.isAir() && serverLevel.getBlockState(pos.above()).isAir()) {
                    ParticleOptions particle = isInWater ? ParticleTypes.BUBBLE
                            : new BlockParticleOption(ParticleTypes.BLOCK, state);
                    int particleCount = isInWater ? PARTICLES_PER_BLOCK * 2 : PARTICLES_PER_BLOCK;
                    for (int i = 0; i < particleCount; i++) {
                        double offsetX = serverLevel.random.nextGaussian() * PARTICLE_OFFSET_SCALE;
                        double offsetY = serverLevel.random.nextDouble() * 0.5 + 0.5;
                        double offsetZ = serverLevel.random.nextGaussian() * PARTICLE_OFFSET_SCALE;
                        serverLevel.sendParticles(particle,
                                pos.getX() + 0.5 + offsetX,
                                pos.getY() + 1.0 + offsetY,
                                pos.getZ() + 0.5 + offsetZ,
                                1, PARTICLE_SPEED, PARTICLE_SPEED, PARTICLE_SPEED, 0.0);
                    }
                }
            }
        }
    }

    @Unique
    private void applyDepletionEffect(Level level, LivingEntity sourceentity) {
        if (!ENABLE_DEPLETION || !EffectsConfig.DEPLETION.get() || level.isClientSide()) {
            return;
        }
        sourceentity.addEffect(new MobEffectInstance(
                ModMobEffects.DEPLETION,
                DEPLETION_DURATION, DEPLETION_LEVEL, false, false, true));
    }

    @Unique
    private void performSweepAttack(ItemStack stack, LivingEntity entity, Player player) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float sweepDamage = 1.0F + baseDamage * 0.5F;
        double range = 1.0F;
        double knockback = 0.4F;
        for (LivingEntity nearby : entity.level().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(range, 0.25D, range))) {
            if (nearby == player || nearby == entity || player.isAlliedTo(nearby)) {
                continue;
            }
            if (player.distanceToSqr(nearby) > range * range) {
                continue;
            }
            nearby.knockback(knockback, player.getX() - nearby.getX(), player.getZ() - nearby.getZ());
            nearby.hurt(player.damageSources().playerAttack(player), sweepDamage);
            net.jaams.weaponry.util.ModUtils.applyAttackEnchantEffects(nearby, player);
        }
        serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Unique
    private boolean requiresFullyCharged(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BusterStrikeRequireFullyCharged"))
            return tag.getBoolean("BusterStrikeRequireFullyCharged");
        return TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_REQUIRES_CHARGED.get());
    }

    @Unique
    private int getRequiredHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BusterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("BusterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.required_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private float getBonusMultiplier(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BusterStrikeBonusMultiplier")) {
            return Math.max(0.0F, tag.getFloat("BusterStrikeBonusMultiplier"));
        }
        float value = TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.bonus_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_BONUS_MULTIPLIER.get().floatValue());
        return Math.max(0.0F, value);
    }

    @Unique
    private int getDurabilityPenalty(ItemStack stack) {
        return 0;
    }

}
