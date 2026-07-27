package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.world.inventory.GunGUIMenu;
import net.jaams.weaponry.world.inventory.PistolGUIMenu;
import net.jaams.weaponry.world.inventory.RevolverGUIMenu;
import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.world.inventory.ShotgunGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, JaamsWeaponryMod.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<PistolGUIMenu>> PISTOL_GUI = REGISTRY.register("pistol_gui", () -> IMenuTypeExtension.create(PistolGUIMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ScattergunGUIMenu>> SCATTERGUN_GUI = REGISTRY.register("scattergun_gui", () -> IMenuTypeExtension.create(ScattergunGUIMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ShotgunGUIMenu>> SHOTGUN_GUI = REGISTRY.register("shotgun_gui", () -> IMenuTypeExtension.create(ShotgunGUIMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RevolverGUIMenu>> REVOLVER_GUI = REGISTRY.register("revolver_gui", () -> IMenuTypeExtension.create(RevolverGUIMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<GunGUIMenu>> GUN_GUI = REGISTRY.register("gun_gui", () -> IMenuTypeExtension.create(GunGUIMenu::new));
}
