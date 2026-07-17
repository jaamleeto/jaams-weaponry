package net.jaams.weaponry.handler.trait;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UnstableEdgeHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (attacker.level().isClientSide)
            return;
        if (!TraitsConfig.UNSTABLE_EDGE.get() || event.getAmount() <= 0.0F) {
            return;
        }
        ItemStack weapon = ItemStack.EMPTY;
        if (ModTraits.isUnstableEdgeItem(attacker.getMainHandItem())) {
            weapon = attacker.getMainHandItem();
        } else if (ModTraits.isUnstableEdgeItem(attacker.getOffhandItem())) {
            weapon = attacker.getOffhandItem();
        }
        if (weapon.isEmpty()) {
            return;
        }
        float min = getMinMultiplier(weapon);
        float max = getMaxMultiplier(weapon);
        if (max < min) {
            float swap = min;
            min = max;
            max = swap;
        }
        float multiplier = min + attacker.getRandom().nextFloat() * (max - min);
        event.setAmount(event.getAmount() * multiplier);
    }

    private static float getMinMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UnstableEdgeMinDamageMultiplier")) {
            return tag.getFloat("UnstableEdgeMinDamageMultiplier");
        }
        return TraitModifierData.getUnstableEdge(stack).map(entry -> entry.min_damage_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.UNSTABLE_EDGE_MIN_MULTIPLIER.get().floatValue());
    }

    private static float getMaxMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UnstableEdgeMaxDamageMultiplier")) {
            return tag.getFloat("UnstableEdgeMaxDamageMultiplier");
        }
        return TraitModifierData.getUnstableEdge(stack).map(entry -> entry.max_damage_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.UNSTABLE_EDGE_MAX_MULTIPLIER.get().floatValue());
    }
}
