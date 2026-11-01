package net.jaams.weaponry.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Guard Villagers compatibility helpers.
 * Only references registry lookups, so this class is safe to load when the mod is absent.
 */
public class GuardVillagersCompat {

    public static final String MOD_ID = "guardvillagers";

    private static Boolean cache = null;

    public static boolean isLoaded() {
        if (cache == null) {
            cache = ModList.get() != null && ModList.get().isLoaded(MOD_ID);
        }
        return cache;
    }

    private static EntityType<?> entityType(String name) {
        if (!isLoaded())
            return null;
        return ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
    }

    /**
     * True for the mob types whose default spawn equipment is already taken care of
     * by the Guard Villagers spawn mixin, so the generic entity event must not
     * apply equipment modifiers a second time.
     */
    public static boolean isSpawnEquipmentHandled(EntityType<?> type) {
        return isLoaded() && type == entityType("guard");
    }

    /**
     * Guards have no player-like item cooldown system; Guard Villagers already uses
     * {@code shieldCoolDown} for the same purpose (it gates {@code RaiseShieldGoal}
     * and ticks down in {@code aiStep}), so use it for guard stance / parry guard
     * cooldowns too. No-op for other mob types.
     */
    public static void setGuardShieldCooldown(LivingEntity entity, int ticks) {
        if (!isLoaded() || entity == null) {
            return;
        }
        try {
            GuardVillagersImpl.setGuardShieldCooldown(entity, ticks);
        } catch (Throwable t) {
            // Guard Villagers is optional; never let its absence break the event flow.
        }
    }

    /**
     * Same feel as vanilla shield disarm ("axe hit a raised shield"): cooldown,
     * durability damage and stop using the item.
     */
    public static void disableGuardShield(LivingEntity defender, ItemStack itemStack) {
        if (!isLoaded() || defender == null || itemStack == null) {
            return;
        }
        try {
            GuardVillagersImpl.disableGuardShield(defender, itemStack);
        } catch (Throwable t) {
            // Guard Villagers is optional; never let its absence break the event flow.
        }
    }

    // All direct references to Guard Villagers classes are kept here so this outer class
    // stays loadable when the mod is absent. This class is only loaded on first use,
    // which happens after isLoaded() has been checked.
    private static final class GuardVillagersImpl {

        static void setGuardShieldCooldown(LivingEntity entity, int ticks) {
            if (!(entity instanceof tallestegg.guardvillagers.entities.Guard guard)) {
                return;
            }
            if (guard.shieldCoolDown < ticks) {
                guard.shieldCoolDown = ticks;
            }
        }

        static void disableGuardShield(LivingEntity defender, ItemStack itemStack) {
            if (!(defender instanceof tallestegg.guardvillagers.entities.Guard guard)) {
                return;
            }
            if (guard.shieldCoolDown <= 0) {
                guard.shieldCoolDown = 100;
            }
            itemStack.hurtAndBreak(3, guard, p -> p.broadcastBreakEvent(guard.getUsedItemHand()));
            guard.stopUsingItem();
            guard.level().playSound(null, guard.getX(), guard.getY(), guard.getZ(), SoundEvents.SHIELD_BLOCK,
                    guard.getSoundSource(), 1.0F, 1.0F);
            guard.level().playSound(null, guard.getX(), guard.getY(), guard.getZ(), SoundEvents.SHIELD_BREAK,
                    guard.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
