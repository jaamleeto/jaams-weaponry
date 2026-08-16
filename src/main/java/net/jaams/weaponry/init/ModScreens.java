package net.jaams.weaponry.init;

import net.jaams.weaponry.client.gui.GunGUIScreen;
import net.jaams.weaponry.client.gui.PistolGUIScreen;
import net.jaams.weaponry.client.gui.RevolverGUIScreen;
import net.jaams.weaponry.client.gui.ScattergunGUIScreen;
import net.jaams.weaponry.client.gui.ShotgunGUIScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PISTOL_GUI.get(), PistolGUIScreen::new);
        event.register(ModMenus.SCATTERGUN_GUI.get(), ScattergunGUIScreen::new);
        event.register(ModMenus.SHOTGUN_GUI.get(), ShotgunGUIScreen::new);
        event.register(ModMenus.REVOLVER_GUI.get(), RevolverGUIScreen::new);
        event.register(ModMenus.GUN_GUI.get(), GunGUIScreen::new);
    }
}
