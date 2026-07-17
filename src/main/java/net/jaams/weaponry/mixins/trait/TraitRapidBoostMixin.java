package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class TraitRapidBoostMixin {

    @Unique
    private static final String NBT_HITS = "RapidBoostHits";

    @Unique
    private boolean isRapidBoostEnabled(ItemStack stack) {
        if (!TraitsConfig.RAPID_BOOST.get())
            return false;
        return ModTraits.isRapidBoostItem(stack);
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onRapidBoostHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || target.level().isClientSide())
            return;
        if (!isRapidBoostEnabled(stack))
            return;
        if (!ItemStack.isSameItemSameTags(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameTags(attacker.getOffhandItem(), stack))
            return;

        CompoundTag nbt = stack.getOrCreateTag();
        int hits = nbt.getInt(NBT_HITS) + 1;
        int maxHits = getMaxHits(stack);

        if (hits >= maxHits) {
            hits = 0;
        }

        nbt.putInt(NBT_HITS, hits);
    }

    @Unique
    private int getMaxHits(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("RapidBoostMaxHits")) {
            return Math.max(1, tag.getInt("RapidBoostMaxHits"));
        }
        int value = TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_MAX_HITS.get());
        return Math.max(1, value);
    }
}
