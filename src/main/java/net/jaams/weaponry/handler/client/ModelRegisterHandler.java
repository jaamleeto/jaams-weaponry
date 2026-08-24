package net.jaams.weaponry.handler.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegisterHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRegisterCustomModels(ModelEvent.RegisterAdditional event) {
		String[] katanaSkins = { "rengoku", "mitsuri", "zenitsu", "inosuke" };
		for (String skin : katanaSkins) {
			String base = "skin_" + skin;
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld"), "inventory"));
		}

		String[] broadswordSkins = { "macuahuitl" };
		for (String skin : broadswordSkins) {
			String base = "skin_" + skin;
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld"), "inventory"));
		}

		String[] butterflySwordSkins = { "rita" };
		for (String skin : butterflySwordSkins) {
			String base = "skin_" + skin;
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld"), "inventory"));
		}

		String[] longswordSkins = { "blood_sword" };
		for (String skin : longswordSkins) {
			String base = "skin_" + skin;
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld"), "inventory"));
		}



		String[] nunchakuSkins = { "rock_lee", "michaelangelo" };
		for (String skin : nunchakuSkins) {
			String base = "skin_" + skin;
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_idle"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_active"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui"), "inventory"));
			event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld"), "inventory"));
		}

		event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_sokka"), "inventory"));
	}
}
