package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.world.inventory.GunGUIMenu;
import net.jaams.weaponry.world.inventory.PistolGUIMenu;
import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.world.inventory.ShotgunGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JaamsWeaponryMod.MODID);
    public static final RegistryObject<MenuType<PistolGUIMenu>> PISTOL_GUI = REGISTRY.register("pistol_gui", () -> IForgeMenuType.create(PistolGUIMenu::new));
    public static final RegistryObject<MenuType<ScattergunGUIMenu>> SCATTERGUN_GUI = REGISTRY.register("scattergun_gui", () -> IForgeMenuType.create(ScattergunGUIMenu::new));
    public static final RegistryObject<MenuType<ShotgunGUIMenu>> SHOTGUN_GUI = REGISTRY.register("shotgun_gui", () -> IForgeMenuType.create(ShotgunGUIMenu::new));
    public static final RegistryObject<MenuType<GunGUIMenu>> GUN_GUI = REGISTRY.register("gun_gui", () -> IForgeMenuType.create(GunGUIMenu::new));
}
