package net.jaams.weaponry.handler.trait;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;

import java.util.Optional;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class ParryGuardHandler {

    public static float PARTICLE_OFFSET_Y = 0.6F;

    private static final String PARRY_SUCCESS_KEY = "jaams_weaponry_parry_guard_success";

    public static boolean isParryGuardEnabled() {
        return TraitsConfig.PARRY_GUARD.get();
    }

    public static ParryGuardProperties getDefaultParryGuard() {
        ParryGuardProperties.Builder builder = new ParryGuardProperties.Builder();
        builder.cooldownTicks(TraitsConfig.PARRY_GUARD_COOLDOWN_TICKS.get());
        builder.blockDamageReduction(TraitsConfig.PARRY_GUARD_BLOCK_DAMAGE_REDUCTION.get());
        builder.damagePerBlock(TraitsConfig.PARRY_GUARD_DAMAGE_PER_BLOCK.get());
        builder.damageOnStop(TraitsConfig.PARRY_GUARD_DAMAGE_ON_STOP.get());
        builder.noDurabilityBreakChance(TraitsConfig.PARRY_GUARD_NO_DURABILITY_BREAK_CHANCE.get().floatValue());
        return builder.build();
    }

    public static ParryGuardProperties getParryGuardProperties(ItemStack stack) {
        Optional<TraitModifierData.ParryGuardEntry> entry = TraitModifierData.getParryGuard(stack);
        if (entry.isEmpty()) {
            return getDefaultParryGuard();
        }
        TraitModifierData.ParryGuardEntry data = entry.get();
        ParryGuardProperties.Builder builder = new ParryGuardProperties.Builder();
        if (data.cooldown_ticks != null)
            builder.cooldownTicks(data.cooldown_ticks);
        else
            builder.cooldownTicks(TraitsConfig.PARRY_GUARD_COOLDOWN_TICKS.get());
        if (data.block_damage_reduction != null)
            builder.blockDamageReduction(data.block_damage_reduction);
        else
            builder.blockDamageReduction(TraitsConfig.PARRY_GUARD_BLOCK_DAMAGE_REDUCTION.get());
        if (data.damage_per_block != null)
            builder.damagePerBlock(data.damage_per_block);
        else
            builder.damagePerBlock(TraitsConfig.PARRY_GUARD_DAMAGE_PER_BLOCK.get());
        if (data.damage_on_stop != null)
            builder.damageOnStop(data.damage_on_stop);
        else
            builder.damageOnStop(TraitsConfig.PARRY_GUARD_DAMAGE_ON_STOP.get());
        if (data.no_durability_break_chance != null)
            builder.noDurabilityBreakChance(data.no_durability_break_chance);
        else
            builder.noDurabilityBreakChance(TraitsConfig.PARRY_GUARD_NO_DURABILITY_BREAK_CHANCE.get().floatValue());
        if (data.block_sound != null && !data.block_sound.isEmpty()) {
            ResourceLocation soundId = ResourceLocation.tryParse(data.block_sound);
            if (soundId != null) {
                SoundEvent sound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(soundId);
                if (sound != null)
                    builder.blockSound(sound);
            }
        }
        return builder.build();
    }

    @SubscribeEvent
    public static void onParryGuardLivingAttack(LivingIncomingDamageEvent event) {
        if (!isParryGuardEnabled())
            return;
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack itemStack = player.getUseItem();
        if (!ModTraits.isParryGuardItem(itemStack))
            return;
        if (!isParryGuarding(player))
            return;
        ParryGuardProperties config = getParryGuardProperties(itemStack);
        Entity source = event.getSource().getEntity();
        if (source instanceof LivingEntity attacker
                && canDisableParryGuard(attacker.getMainHandItem(), itemStack, player, attacker)) {
            disableParryGuardWeapon(player, itemStack);
            event.setCanceled(true);
            return;
        }
        if (source == null || !isInParryGuardArea(player, source))
            return;

        float originalDamage = event.getAmount();


        if (itemStack.getMaxDamage() == 0) {
            float breakChance = config.noDurabilityBreakChance;
            if (breakChance <= 0.0f || !(player.getRandom().nextFloat() < breakChance)) {

                return;
            }

            itemStack.shrink(1);
            player.stopUsingItem();
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK, player.getSoundSource(), 1.0F, 1.0F);
            return;
        }

        player.hurtTime = 0;
        player.hurtDuration = 0;
        player.invulnerableTime = 0;
        player.clearFire();
        player.refreshDimensions();
        int durabilityCost = Math.max(0, Math.round(originalDamage * config.damagePerBlock));
        itemStack.hurtAndBreak(durabilityCost, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));


        float reducedDamage = originalDamage * (1.0f - (float) config.blockDamageReduction);
        event.setCanceled(true);



        player.getPersistentData().putBoolean(PARRY_SUCCESS_KEY, true);


        if (TraitsConfig.PARRY_GUARD_GLOBAL_COOLDOWN.get()) {
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && ModTraits.isParryGuardItem(stack)) {
                    player.getCooldowns().addCooldown(stack.getItem(), config.cooldownTicks);
                }
            }
            ItemStack offHandStack = player.getOffhandItem();
            if (!offHandStack.isEmpty() && ModTraits.isParryGuardItem(offHandStack)) {
                player.getCooldowns().addCooldown(offHandStack.getItem(), config.cooldownTicks);
            }
            for (ItemStack stack : player.getInventory().armor) {
                if (!stack.isEmpty() && ModTraits.isParryGuardItem(stack)) {
                    player.getCooldowns().addCooldown(stack.getItem(), config.cooldownTicks);
                }
            }
        } else {
            player.getCooldowns().addCooldown(itemStack.getItem(), config.cooldownTicks);
        }


        player.stopUsingItem();

        if (reducedDamage > 0) {
            player.hurt(event.getSource(), reducedDamage);
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                config.blockSound != null ? config.blockSound : SoundEvents.PLAYER_ATTACK_STRONG,
                player.getSoundSource(), 1.0F, 1.0F);


        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 lookVec = player.getLookAngle().normalize();
            double px = player.getX() + lookVec.x * 1.0;
            double py = player.getY() + player.getEyeHeight() * PARTICLE_OFFSET_Y;
            double pz = player.getZ() + lookVec.z * 1.0;
            serverLevel.sendParticles(ParticleTypes.CRIT, px, py, pz, 8, 0.3, 0.3, 0.3, 0.1);
        }
    }

    @SubscribeEvent
    public static void onParryGuardStopUsing(LivingEntityUseItemEvent.Stop event) {
        if (!isParryGuardEnabled())
            return;
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack itemStack = event.getItem();
        if (!ModTraits.isParryGuardItem(itemStack))
            return;
        ParryGuardProperties config = getParryGuardProperties(itemStack);
        Level level = player.level();


        boolean parrySuccess = player.getPersistentData().getBoolean(PARRY_SUCCESS_KEY);
        player.getPersistentData().remove(PARRY_SUCCESS_KEY);

        if (parrySuccess) {

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 0.5F, 1.2F);
        } else {

            itemStack.hurtAndBreak(config.damageOnStop, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public static boolean isParryGuarding(Player player) {
        return player.isUsingItem() && player.getUseItem().getUseAnimation() == UseAnim.BLOCK;
    }

    private static boolean canDisableParryGuard(ItemStack attackerStack, ItemStack parryStack, Player player,
            LivingEntity attacker) {
        return !attackerStack.isEmpty()
                && attackerStack.getItem().canDisableShield(attackerStack, parryStack, player, attacker);
    }

    private static void disableParryGuardWeapon(Player player, ItemStack itemStack) {
        if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            player.getCooldowns().addCooldown(itemStack.getItem(), 100);
        }
        itemStack.hurtAndBreak(3, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        player.stopUsingItem();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK,
                player.getSoundSource(), 1.0F, 1.0F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BREAK,
                player.getSoundSource(), 1.0F, 1.0F);
    }

    private static boolean isInParryGuardArea(Player player, Entity source) {
        net.minecraft.core.Direction playerFront = player.getDirection();
        net.minecraft.core.Direction attackerFacing = net.minecraft.core.Direction.getNearest(
                source.getX() - player.getX(), 0, source.getZ() - player.getZ());
        net.minecraft.core.Direction leftSide = playerFront.getCounterClockWise();
        net.minecraft.core.Direction rightSide = playerFront.getClockWise();
        return attackerFacing == playerFront || attackerFacing == leftSide || attackerFacing == rightSide;
    }

    public static class ParryGuardProperties {
        public final int cooldownTicks;
        public final double blockDamageReduction;
        public final int damagePerBlock;
        public final int damageOnStop;
        public final float noDurabilityBreakChance;
        public final SoundEvent blockSound;

        private ParryGuardProperties(Builder builder) {
            this.cooldownTicks = builder.cooldownTicks;
            this.blockDamageReduction = builder.blockDamageReduction;
            this.damagePerBlock = builder.damagePerBlock;
            this.damageOnStop = builder.damageOnStop;
            this.noDurabilityBreakChance = builder.noDurabilityBreakChance;
            this.blockSound = builder.blockSound;
        }

        public static class Builder {
            private int cooldownTicks = 20;
            private double blockDamageReduction = 0.5;
            private int damagePerBlock = 1;
            private int damageOnStop = 1;
            private float noDurabilityBreakChance = 0.0f;
            private SoundEvent blockSound = null;

            public Builder cooldownTicks(int cooldownTicks) {
                this.cooldownTicks = cooldownTicks;
                return this;
            }

            public Builder blockDamageReduction(double blockDamageReduction) {
                this.blockDamageReduction = blockDamageReduction;
                return this;
            }

            public Builder damagePerBlock(int damagePerBlock) {
                this.damagePerBlock = damagePerBlock;
                return this;
            }

            public Builder damageOnStop(int damageOnStop) {
                this.damageOnStop = damageOnStop;
                return this;
            }

            public Builder noDurabilityBreakChance(float noDurabilityBreakChance) {
                this.noDurabilityBreakChance = noDurabilityBreakChance;
                return this;
            }

            public Builder blockSound(SoundEvent blockSound) {
                this.blockSound = blockSound;
                return this;
            }

            public ParryGuardProperties build() {
                return new ParryGuardProperties(this);
            }
        }
    }
}
