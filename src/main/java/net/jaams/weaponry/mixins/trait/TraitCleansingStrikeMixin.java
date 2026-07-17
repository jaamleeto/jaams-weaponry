package net.jaams.weaponry.mixins.trait;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.handler.event.AdvancementsHandler;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class TraitCleansingStrikeMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onCleansingStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide
                || !ModTraits.isCleansingStrikeItem(stack)) {
            return;
        }

        Level level = target.level();

        
        float chance = getChance(stack, stack.getTag());
        if (attacker instanceof ServerPlayer player) {
            if (AdvancementsHandler.hasAdvancement(player, "brooms_and_potions")) {
                chance = Math.min(1.0F, chance * 2.0F);
            }
        }
        if (attacker.getRandom().nextFloat() >= chance || target.getActiveEffects().isEmpty()) {
            return;
        }

        
        int totalEffects = target.getActiveEffects().size();

        
        
        Set<String> blacklisted = getBlacklistedEffects(stack, stack.getTag());
        MobEffectInstance slowness = null;
        boolean hasBaneOfArthropods = stack.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS) > 0;
        if (hasBaneOfArthropods && target.getMobType() == MobType.ARTHROPOD) {
            slowness = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }

        
        List<MobEffectInstance> effectsToKeep = new ArrayList<>();
        for (MobEffectInstance effect : new ArrayList<>(target.getActiveEffects())) {
            String effectId = getEffectId(effect);
            if (blacklisted.contains(effectId)) {
                effectsToKeep.add(effect);
            } else if (slowness != null && effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN) {
                effectsToKeep.add(effect);
            }
        }

        
        target.removeAllEffects();
        for (MobEffectInstance effect : effectsToKeep) {
            target.addEffect(new MobEffectInstance(effect));
        }

        
        
        if (attacker instanceof ServerPlayer player && totalEffects >= 6 && stack.is(ModTags.BROOMS)) {
            AdvancementsHandler.grantAdvancement(player, "brooms_and_potions");
        }

        
        if (stack.is(ModTags.BROOMS)) {
            ModUtils.playSound(target, "jaams_weaponry:broom_clean");
        } else {
            ModUtils.playSound(target, "jaams_weaponry:swoosh_air");
        }

        
        if (!level.isClientSide()) {
            spawnEffectParticles((ServerLevel) level, target, ParticleTypes.CLOUD, 5);
        }
    }

    @Unique
    private float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("CleansingStrikeChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("CleansingStrikeChance")));
        }
        return TraitModifierData.getCleansingStrike(stack)
                .map((entry) -> entry.chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.CLEANSING_STRIKE_CHANCE.get().floatValue());
    }

    @Unique
    private Set<String> getBlacklistedEffects(ItemStack stack, CompoundTag tag) {
        
        if (tag != null && tag.contains("CleansingStrikeBlacklist")) {
            net.minecraft.nbt.ListTag list = tag.getList("CleansingStrikeBlacklist", net.minecraft.nbt.Tag.TAG_STRING);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
            return result;
        }
        
        Set<String> fromData = TraitModifierData.getCleansingStrike(stack)
                .map((entry) -> entry.blacklisted_effects)
                .filter(java.util.Objects::nonNull)
                .map(HashSet::new)
                .orElse(null);
        if (fromData != null) {
            return fromData;
        }
        
        return new HashSet<>(TraitsConfig.CLEANSING_STRIKE_BLACKLISTED_EFFECTS.get());
    }

    @Unique
    private static String getEffectId(MobEffectInstance effect) {
        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect());
        return key != null ? key.toString() : "";
    }

    @Unique
    private void spawnEffectParticles(ServerLevel serverLevel, LivingEntity entity, ParticleOptions particleType,
            int particleCount) {
        RandomSource random = serverLevel.random;
        for (int i = 0; i < particleCount; i++) {
            double xOffset = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            double yOffset = entity.getY() + entity.getBbHeight() * 0.5
                    + (random.nextDouble() - 0.5) * entity.getBbHeight() * 0.5;
            double zOffset = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            serverLevel.sendParticles(particleType, xOffset, yOffset, zOffset, 1, 0.1, 0.1, 0.1, 0.05);
        }
    }
}
