package net.jaams.weaponry.handler.client;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegisterHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRegisterCustomModels(ModelEvent.RegisterAdditional event) {
		String[] katanaSkins = { "rengoku", "mitsuri", "zenitsu", "inosuke" };
		for (String skin : katanaSkins) {
			String base = "item/skin_" + skin;
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base)));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld")));
		}

		String[] broadswordSkins = { "macuahuitl" };
		for (String skin : broadswordSkins) {
			String base = "item/skin_" + skin;
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base)));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld")));
		}

		String[] butterflySwordSkins = { "rita" };
		for (String skin : butterflySwordSkins) {
			String base = "item/skin_" + skin;
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base)));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld")));
		}

		String[] longswordSkins = { "blood_sword" };
		for (String skin : longswordSkins) {
			String base = "item/skin_" + skin;
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base)));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld")));
		}



		String[] nunchakuSkins = { "rock_lee", "michaelangelo" };
		for (String skin : nunchakuSkins) {
			String base = "item/skin_" + skin;
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_idle")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_active")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_gui")));
			event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", base + "_handheld")));
		}

		event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_sokka")));
	}
}
