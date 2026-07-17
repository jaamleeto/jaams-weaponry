package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.enchantment.AftermathEnchantment;
import net.jaams.weaponry.enchantment.BackblastEnchantment;
import net.jaams.weaponry.enchantment.FrameguardEnchantment;
import net.jaams.weaponry.enchantment.GhostClipEnchantment;
import net.jaams.weaponry.enchantment.OverdriveEnchantment;
import net.jaams.weaponry.enchantment.SecureGripEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {

    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, JaamsWeaponryMod.MODID);
    public static final RegistryObject<Enchantment> SECURE_GRIP = REGISTRY.register("secure_grip", () -> new SecureGripEnchantment());
    public static final RegistryObject<Enchantment> OVERDRIVE = REGISTRY.register("overdrive", () -> new OverdriveEnchantment());
    public static final RegistryObject<Enchantment> AFTERMATH = REGISTRY.register("aftermath", () -> new AftermathEnchantment());
    public static final RegistryObject<Enchantment> GHOST_CLIP = REGISTRY.register("ghost_clip", () -> new GhostClipEnchantment());
    public static final RegistryObject<Enchantment> FRAMEGUARD = REGISTRY.register("frameguard", () -> new FrameguardEnchantment());
    public static final RegistryObject<Enchantment> BACKBLAST = REGISTRY.register("backblast", () -> new BackblastEnchantment());
}
