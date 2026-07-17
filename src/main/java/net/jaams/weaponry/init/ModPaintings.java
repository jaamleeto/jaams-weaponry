package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPaintings {

    public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, JaamsWeaponryMod.MODID);
    public static final RegistryObject<PaintingVariant> LA_ESPADA = REGISTRY.register("la_espada", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> SEANAMITE = REGISTRY.register("seanamite", () -> new PaintingVariant(32, 16));
}
