package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> SECURE_GRIP = key("secure_grip");
    public static final ResourceKey<Enchantment> OVERDRIVE = key("overdrive");
    public static final ResourceKey<Enchantment> AFTERMATH = key("aftermath");
    public static final ResourceKey<Enchantment> GHOST_CLIP = key("ghost_clip");
    public static final ResourceKey<Enchantment> FRAMEGUARD = key("frameguard");
    public static final ResourceKey<Enchantment> BACKBLAST = key("backblast");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, name));
    }

    public static int level(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if (stack == null || stack.isEmpty())
            return 0;
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(enchantment))
                return entry.getIntValue();
        }
        return 0;
    }

    public static Holder<Enchantment> holder(Level level, ResourceKey<Enchantment> enchantment) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
    }

    @org.jetbrains.annotations.Nullable
    public static Holder<Enchantment> holderFromId(net.minecraft.resources.ResourceLocation id) {
        if (id == null) return null;
        net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;
        return server.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(ResourceKey.create(Registries.ENCHANTMENT, id))
                .map(h -> (Holder<Enchantment>) h)
                .orElse(null);
    }

    public static int entityLevel(LivingEntity entity, ResourceKey<Enchantment> enchantment) {
        int best = 0;
        for (ItemStack stack : entity.getAllSlots()) {
            best = Math.max(best, level(stack, enchantment));
        }
        return best;
    }

    public static float damageBonus(ItemStack stack, net.minecraft.world.entity.LivingEntity target) {
        float bonus = 0.0F;
        int sharpness = level(stack, net.minecraft.world.item.enchantment.Enchantments.SHARPNESS);
        if (sharpness > 0) {
            bonus += 0.5F * sharpness + 0.5F;
        }
        if (target != null) {
            int smite = level(stack, net.minecraft.world.item.enchantment.Enchantments.SMITE);
            if (smite > 0 && target.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                bonus += 2.5F * smite;
            }
            int bane = level(stack, net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS);
            if (bane > 0 && target.getType().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD)) {
                bonus += 2.5F * bane;
            }
        }
        return bonus;
    }

    public static int overdriveDurabilityCost(int level) {
        if (!net.jaams.weaponry.configuration.common.EnchantmentsConfig.OVERDRIVE.get()) {
            return 0;
        }
        return net.jaams.weaponry.configuration.common.EnchantmentsConfig.OVERDRIVE_DURABILITY_COST_PER_LEVEL.get() * level;
    }
}
