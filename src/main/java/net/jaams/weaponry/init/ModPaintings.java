package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class ModPaintings {

    public static final ResourceKey<PaintingVariant> LA_ESPADA = key("la_espada");
    public static final ResourceKey<PaintingVariant> SEANAMITE = key("seanamite");

    private static ResourceKey<PaintingVariant> key(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, name));
    }
}
