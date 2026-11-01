package net.jaams.weaponry.handler.trait;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

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
import net.jaams.weaponry.compat.GuardVillagersCompat;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                SoundEvent sound = net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(soundId);
                if (sound != null)
                    builder.blockSound(sound);
            }
        }
        return builder.build();
    }

    @SubscribeEvent
    public static void onParryGuardLivingAttack(LivingAttackEvent event) {
        if (!isParryGuardEnabled())
            return;
        LivingEntity defender = event.getEntity();
        ItemStack itemStack = defender.getUseItem();
        if (!ModTraits.isParryGuardItem(itemStack))
            return;
        if (!isParryGuarding(defender))
            return;
        ParryGuardProperties config = getParryGuardProperties(itemStack);
        Entity source = event.getSource().getEntity();
        if (source instanceof LivingEntity attacker
                && canDisableParryGuard(attacker.getMainHandItem(), itemStack, defender, attacker)) {
            disableParryGuardWeapon(defender, itemStack);
            event.setCanceled(true);
            return;
        }
        if (source == null || !isInParryGuardArea(defender, source))
            return;

        float originalDamage = event.getAmount();


        if (itemStack.getMaxDamage() == 0) {
            float breakChance = config.noDurabilityBreakChance;
            if (breakChance <= 0.0f || !(defender.getRandom().nextFloat() < breakChance)) {

                return;
            }

            itemStack.shrink(1);
            defender.stopUsingItem();
            defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                    SoundEvents.ITEM_BREAK, defender.getSoundSource(), 1.0F, 1.0F);
            return;
        }

        defender.hurtTime = 0;
        defender.hurtDuration = 0;
        defender.invulnerableTime = 0;
        defender.clearFire();
        defender.refreshDimensions();
        int durabilityCost = Math.max(0, Math.round(originalDamage * config.damagePerBlock));
        itemStack.hurtAndBreak(durabilityCost, defender, p -> p.broadcastBreakEvent(defender.getUsedItemHand()));


        float reducedDamage = originalDamage * (1.0f - (float) config.blockDamageReduction);
        event.setCanceled(true);



        defender.getPersistentData().putBoolean(PARRY_SUCCESS_KEY, true);


        if (defender instanceof Player player) {
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
        } else {
            GuardVillagersCompat.setGuardShieldCooldown(defender, config.cooldownTicks);
        }


        defender.stopUsingItem();

        if (reducedDamage > 0) {
            defender.hurt(event.getSource(), reducedDamage);
        }

        defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                config.blockSound != null ? config.blockSound : SoundEvents.PLAYER_ATTACK_STRONG,
                defender.getSoundSource(), 1.0F, 1.0F);


        if (defender.level() instanceof ServerLevel serverLevel) {
            Vec3 lookVec = defender.getLookAngle().normalize();
            double px = defender.getX() + lookVec.x * 1.0;
            double py = defender.getY() + defender.getEyeHeight() * PARTICLE_OFFSET_Y;
            double pz = defender.getZ() + lookVec.z * 1.0;
            serverLevel.sendParticles(ParticleTypes.CRIT, px, py, pz, 8, 0.3, 0.3, 0.3, 0.1);
        }
    }

    @SubscribeEvent
    public static void onParryGuardStopUsing(LivingEntityUseItemEvent.Stop event) {
        onParryGuardStopped(event.getEntity(), event.getItem());
    }

    /**
     * Parry guard stop logic (parry success sound / stop durability). Fired from
     * the Stop event for players and directly from the RaiseShieldGoal mixin for
     * guards, because {@code stopUsingItem} throws no use-item events for mobs.
     */
    public static void onParryGuardStopped(LivingEntity defender, ItemStack itemStack) {
        if (defender == null)
            return;
        if (!isParryGuardEnabled())
            return;
        if (!ModTraits.isParryGuardItem(itemStack))
            return;
        ParryGuardProperties config = getParryGuardProperties(itemStack);
        Level level = defender.level();


        boolean parrySuccess = defender.getPersistentData().getBoolean(PARRY_SUCCESS_KEY);
        defender.getPersistentData().remove(PARRY_SUCCESS_KEY);

        if (parrySuccess) {

            level.playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, defender.getSoundSource(), 0.5F, 1.2F);
        } else {

            itemStack.hurtAndBreak(config.damageOnStop, defender, p -> p.broadcastBreakEvent(defender.getUsedItemHand()));
            level.playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                    SoundEvents.PLAYER_ATTACK_WEAK, defender.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public static boolean isParryGuarding(LivingEntity entity) {
        return entity.isUsingItem() && entity.getUseItem().getUseAnimation() == UseAnim.BLOCK;
    }

    private static boolean canDisableParryGuard(ItemStack attackerStack, ItemStack parryStack, LivingEntity defender,
            LivingEntity attacker) {
        return !attackerStack.isEmpty()
                && attackerStack.getItem().canDisableShield(attackerStack, parryStack, defender, attacker);
    }

    private static void disableParryGuardWeapon(LivingEntity defender, ItemStack itemStack) {
        if (defender instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                player.getCooldowns().addCooldown(itemStack.getItem(), 100);
            }
        } else {
            GuardVillagersCompat.setGuardShieldCooldown(defender, 100);
        }
        itemStack.hurtAndBreak(3, defender, p -> p.broadcastBreakEvent(defender.getUsedItemHand()));
        defender.stopUsingItem();
        defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(), SoundEvents.SHIELD_BLOCK,
                defender.getSoundSource(), 1.0F, 1.0F);
        defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(), SoundEvents.SHIELD_BREAK,
                defender.getSoundSource(), 1.0F, 1.0F);
    }

    private static boolean isInParryGuardArea(LivingEntity defender, Entity source) {
        net.minecraft.core.Direction defenderFront = defender.getDirection();
        net.minecraft.core.Direction attackerFacing = net.minecraft.core.Direction.getNearest(
                source.getX() - defender.getX(), 0, source.getZ() - defender.getZ());
        net.minecraft.core.Direction leftSide = defenderFront.getCounterClockWise();
        net.minecraft.core.Direction rightSide = defenderFront.getClockWise();
        return attackerFacing == defenderFront || attackerFacing == leftSide || attackerFacing == rightSide;
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
