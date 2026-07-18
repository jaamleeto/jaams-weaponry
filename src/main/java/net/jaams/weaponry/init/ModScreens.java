package net.jaams.weaponry.init;

import net.jaams.weaponry.client.gui.GunGUIScreen;
import net.jaams.weaponry.client.gui.PistolGUIScreen;
import net.jaams.weaponry.client.gui.RevolverGUIScreen;
import net.jaams.weaponry.client.gui.ScattergunGUIScreen;
import net.jaams.weaponry.client.gui.ShotgunGUIScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.PISTOL_GUI.get(), PistolGUIScreen::new);
            MenuScreens.register(ModMenus.SCATTERGUN_GUI.get(), ScattergunGUIScreen::new);
            MenuScreens.register(ModMenus.SHOTGUN_GUI.get(), ShotgunGUIScreen::new);
            MenuScreens.register(ModMenus.REVOLVER_GUI.get(), RevolverGUIScreen::new);
            MenuScreens.register(ModMenus.GUN_GUI.get(), GunGUIScreen::new);
        });
    }
}
