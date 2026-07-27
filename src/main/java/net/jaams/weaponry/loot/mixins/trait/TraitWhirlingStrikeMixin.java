package net.jaams.weaponry.mixins.trait;
import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.init.ModEnchantments;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public class TraitWhirlingStrikeMixin {

    @Unique
    private int whirlingParticleTickCounter = 0;

    @Unique
    private boolean isWhirlingStrikeEnabled(ItemStack stack) {
        if (!TraitsConfig.WHIRLING_STRIKE.get())
            return false;
        return ModTraits.isWhirlingStrikeItem(stack);
    }

    @Unique
    private boolean isNunchakuItem(ItemStack stack) {
        return stack.is(ModTags.NUNCHAKUS);
    }

    @Unique
    private float getDamageMultiplier(ItemStack stack) {
        
        var tag = ModComponents.get(stack);
        if (tag != null && tag.contains("WhirlingStrikeDamageMultiplier")) {
            return tag.getFloat("WhirlingStrikeDamageMultiplier");
        }
        
        return TraitModifierData.getWhirlingStrike(stack)
                .map(e -> e.damage_multiplier)
                .filter(Objects::nonNull)
                .orElse(0.5f);
    }

    @Unique
    private float getWeaponAttackDamage(ItemStack stack) {
        var modifiers = stack.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY);
        if (modifiers.modifiers().isEmpty()) {
            modifiers = stack.getItem().getDefaultAttributeModifiers(stack);
        }
        double damage = 1.0;
        for (var entry : modifiers.modifiers()) {
            if (entry.attribute().is(Attributes.ATTACK_DAMAGE)
                    && entry.slot().test(EquipmentSlot.MAINHAND)
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                damage += entry.modifier().amount();
            }
        }
        return (float) damage;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onWhirlingStrikeUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isWhirlingStrikeEnabled(stack)) {
            return;
        }
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaams$onWhirlingStrikeGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isWhirlingStrikeEnabled(stack)) {
            cir.setReturnValue(UseAnim.NONE);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaams$onWhirlingStrikeGetUseDuration(net.minecraft.world.entity.LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isWhirlingStrikeEnabled(stack)) {
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onWhirlingStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || target.level().isClientSide()) {
            return;
        }
        if (!isWhirlingStrikeEnabled(stack)) {
            return;
        }
        if (isNunchakuItem(stack)) {
            ModUtils.playSound(attacker, "jaams_weaponry:nunchaku_chain", SoundSource.PLAYERS, 0.35F, 1.0F);
            ModUtils.playSound(attacker, "jaams_weaponry:nunchaku_hit", SoundSource.PLAYERS, 0.35F, 1.0F);
        } else {
            ModUtils.playSound(attacker, "minecraft:entity.player.attack.weak", SoundSource.PLAYERS, 0.35F, 1.0F);
            ModUtils.playSound(attacker, "minecraft:entity.player.attack.crit", SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void jaams$onWhirlingStrikeUseTick(Level world, LivingEntity entityLiving, int count, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isWhirlingStrikeEnabled(stack)) {
            return;
        }
        if (!(entityLiving instanceof Player player) || player.isDeadOrDying() || player.isSpectator()) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            player.stopUsingItem();
            return;
        }
        HitResult hitResult = calculateWhirlingHitResult(entityLiving);
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();
        boolean isMainHandWhirling = ModTraits.isWhirlingStrikeItem(mainHandStack);
        boolean isOffHandWhirling = ModTraits.isWhirlingStrikeItem(offHandStack);
        if (!isMainHandWhirling && !isOffHandWhirling) {
            return;
        }
        if (!world.isClientSide && count % 10 == 0) {
            float volume = (isMainHandWhirling && isOffHandWhirling) ? 0.45F : 0.35F;
            boolean anyNunchaku = (isMainHandWhirling && isNunchakuItem(mainHandStack))
                    || (isOffHandWhirling && isNunchakuItem(offHandStack));
            if (anyNunchaku) {
                ModUtils.playSound(player, "jaams_weaponry:nunchaku_chain", SoundSource.AMBIENT, volume, 1.0F);
            } else {
                ModUtils.playSound(player, "minecraft:entity.player.attack.weak", SoundSource.AMBIENT, volume, 1.0F);
            }
        }
        if (hitResult instanceof BlockHitResult blockHitResult && !world.isClientSide) {
            if (whirlingParticleTickCounter++ % getParticleTickInterval() == 0) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                Vec3 hitVec = blockHitResult.getLocation();
                Direction direction = blockHitResult.getDirection();
                spawnWhirlingParticles((ServerLevel) world, blockPos, hitVec, direction);
            }
        }
        if (count % getItemDamageInterval() == 0 && player instanceof ServerPlayer) {
            int itemDamageAmount = getItemDamageAmount();
            if (isMainHandWhirling) {
                mainHandStack.hurtAndBreak(itemDamageAmount, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
            }
            if (isOffHandWhirling) {
                offHandStack.hurtAndBreak(itemDamageAmount, player, LivingEntity.getSlotForHand(InteractionHand.OFF_HAND));
            }
        }
        if (count % getAttackInterval() == 0) {
            double range = getBaseAttackRange()
                    * (isMainHandWhirling && isOffHandWhirling ? getDualWieldRangeMultiplier() : 1.0);
            Vec3 lookDirection = player.getLookAngle();
            Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 end = start.add(lookDirection.scale(range));
            AABB areaOfEffect = new AABB(start, end).inflate(0.5, 1.5, 0.5);
            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, areaOfEffect, e -> e != player
                    && e.isAlive() && player.hasLineOfSight(e) && isWhirlingEntityInFront(player, e, 90.0F));
            for (LivingEntity target : targets) {
                applyWhirlingEffectsAndDamage(player, target, mainHandStack, offHandStack, isMainHandWhirling,
                        isOffHandWhirling);
            }
        }
    }

    @Unique
    private void spawnWhirlingParticles(ServerLevel serverLevel, BlockPos blockPos, Vec3 hitVec, Direction direction) {
        BlockState state = serverLevel.getBlockState(blockPos);
        boolean isInWater = serverLevel.getBlockState(blockPos).is(Blocks.WATER)
                || serverLevel.getFluidState(blockPos).is(Fluids.WATER);
        ParticleOptions particle = isInWater ? ParticleTypes.BUBBLE
                : new BlockParticleOption(ParticleTypes.BLOCK, state);
        int particleCount = isInWater ? 2 : 4;
        double offsetX = direction.getStepX() * 0.1;
        double offsetY = direction.getStepY() * 0.1;
        double offsetZ = direction.getStepZ() * 0.1;
        double particleX = hitVec.x + offsetX;
        double particleY = hitVec.y + offsetY;
        double particleZ = hitVec.z + offsetZ;
        serverLevel.sendParticles(particle, particleX, particleY, particleZ, particleCount, 0.01D, 0.01D, 0.01D,
                0.002D);
    }

    @Unique
    private boolean isWhirlingEntityInFront(LivingEntity attacker, LivingEntity target, float fovDegrees) {
        Vec3 attackerEyePos = attacker.getEyePosition();
        Vec3 targetEyePos = target.getEyePosition();
        Vec3 targetDirection = targetEyePos.subtract(attackerEyePos).normalize();
        Vec3 lookDirection = attacker.getLookAngle();
        double dotProduct = lookDirection.dot(targetDirection);
        double fovRad = Math.toRadians(fovDegrees / 2.0);
        double cosFov = Math.cos(fovRad);
        Vec3 targetDirectionXZ = new Vec3(targetDirection.x, 0, targetDirection.z).normalize();
        Vec3 lookDirectionXZ = new Vec3(lookDirection.x, 0, lookDirection.z).normalize();
        double dotProductXZ = lookDirectionXZ.dot(targetDirectionXZ);
        return dotProduct >= cosFov && dotProductXZ > 0;
    }

    @Unique
    private void applyWhirlingEffectsAndDamage(LivingEntity attacker, LivingEntity target, ItemStack mainHandStack,
            ItemStack offHandStack, boolean isMainHandWhirling, boolean isOffHandWhirling) {
        int fireAspectLevel = Math.max(
                ModEnchantments.level(mainHandStack, Enchantments.FIRE_ASPECT),
                ModEnchantments.level(offHandStack, Enchantments.FIRE_ASPECT));
        if (fireAspectLevel > 0) {
            target.igniteForSeconds(4 * fireAspectLevel);
        }
        int knockbackLevel = Math.max(ModEnchantments.level(mainHandStack, Enchantments.KNOCKBACK),
                ModEnchantments.level(offHandStack, Enchantments.KNOCKBACK));
        if (knockbackLevel > 0) {
            double knockbackStrength = knockbackLevel * 0.5;
            double yRotRad = Math.toRadians(attacker.getYRot());
            target.push(-Math.sin(yRotRad) * knockbackStrength, 0.1, Math.cos(yRotRad) * knockbackStrength);
        }
        if (attacker instanceof Player player && ModUtils.isAlliedEntity(player, target)) {
            return;
        }
        float enchantmentBonusMain = isMainHandWhirling
                ? ModEnchantments.damageBonus(mainHandStack, target)
                : 0;
        float enchantmentBonusOff = isOffHandWhirling
                ? ModEnchantments.damageBonus(offHandStack, target)
                : 0;
        boolean anyNunchaku = (isMainHandWhirling && isNunchakuItem(mainHandStack))
                || (isOffHandWhirling && isNunchakuItem(offHandStack));
        float totalDamage = getBaseDamage(mainHandStack) + enchantmentBonusMain + enchantmentBonusOff;
        if (isMainHandWhirling && isOffHandWhirling) {
            totalDamage *= getDualWieldDamageMultiplier();
        }
        totalDamage = Math.min(totalDamage, getMaxDamageCap());
        SoundEvent attackSound = anyNunchaku
                ? ((isMainHandWhirling && isOffHandWhirling) ? SoundEvents.PLAYER_ATTACK_STRONG
                        : SoundEvents.PLAYER_ATTACK_WEAK)
                : SoundEvents.PLAYER_ATTACK_WEAK;
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), attackSound,
                SoundSource.HOSTILE, 0.5F, 1.0F);
        if (target
                .hurt(new DamageSource(
                        attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(
                                attacker instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK),
                        attacker), totalDamage)) {
            float volume = (isMainHandWhirling && isOffHandWhirling) ? 0.45F : 0.35F;
            if (anyNunchaku) {
                ModUtils.playSound(attacker, "jaams_weaponry:nunchaku_hit", SoundSource.HOSTILE, volume, 1.0F);
            } else {
                ModUtils.playSound(attacker, "minecraft:entity.player.attack.crit", SoundSource.HOSTILE, volume, 1.0F);
            }
            ModUtils.playSound(target, "minecraft:entity.player.attack.weak", SoundSource.HOSTILE, 0.35F, 1.0F);
        }
        if (target.isBlocking() && target.getUseItem().isDamageableItem()) {
            int blockDamage = (isMainHandWhirling && isOffHandWhirling) ? getDualWieldBlockDamage()
                    : getSingleWieldBlockDamage();
            target.getUseItem().hurtAndBreak(blockDamage, target, LivingEntity.getSlotForHand(target.getUsedItemHand()));
        }
    }

    @Unique
    private HitResult calculateWhirlingHitResult(LivingEntity entity) {
        return ProjectileUtil.getHitResultOnViewVector(entity, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, getUseDistance());
    }



    @Unique
    private int getItemDamageInterval() {
        return TraitsConfig.WHIRLING_STRIKE_ITEM_DAMAGE_INTERVAL.get();
    }

    @Unique
    private int getAttackInterval() {
        return TraitsConfig.WHIRLING_STRIKE_ATTACK_INTERVAL.get();
    }

    @Unique
    private float getBaseDamage(ItemStack stack) {
        
        var tag = ModComponents.get(stack);
        if (tag != null && tag.contains("WhirlingStrikeBaseDamage")) {
            return tag.getFloat("WhirlingStrikeBaseDamage");
        }
        
        var jsonDamage = TraitModifierData.getWhirlingStrike(stack)
                .map(e -> e.base_damage)
                .filter(Objects::nonNull);
        if (jsonDamage.isPresent()) {
            return jsonDamage.get();
        }
        
        return getWeaponAttackDamage(stack) * getDamageMultiplier(stack);
    }

    @Unique
    private double getBaseAttackRange() {
        return TraitsConfig.WHIRLING_STRIKE_BASE_ATTACK_RANGE.get();
    }

    @Unique
    private double getDualWieldRangeMultiplier() {
        return TraitsConfig.WHIRLING_STRIKE_DUAL_WIELD_RANGE_MULTIPLIER.get();
    }

    @Unique
    private int getItemDamageAmount() {
        return TraitsConfig.WHIRLING_STRIKE_ITEM_DAMAGE_AMOUNT.get();
    }

    @Unique
    private float getDualWieldDamageMultiplier() {
        return TraitsConfig.WHIRLING_STRIKE_DUAL_WIELD_DAMAGE_MULTIPLIER.get().floatValue();
    }

    @Unique
    private float getMaxDamageCap() {
        return TraitsConfig.WHIRLING_STRIKE_MAX_DAMAGE_CAP.get().floatValue();
    }

    @Unique
    private int getSingleWieldBlockDamage() {
        return TraitsConfig.WHIRLING_STRIKE_SINGLE_WIELD_BLOCK_DAMAGE.get();
    }

    @Unique
    private int getDualWieldBlockDamage() {
        return TraitsConfig.WHIRLING_STRIKE_DUAL_WIELD_BLOCK_DAMAGE.get();
    }

    @Unique
    private double getUseDistance() {
        return TraitsConfig.WHIRLING_STRIKE_USE_DISTANCE.get();
    }

    @Unique
    private int getParticleTickInterval() {
        return TraitsConfig.WHIRLING_STRIKE_PARTICLE_TICK_INTERVAL.get();
    }
}
