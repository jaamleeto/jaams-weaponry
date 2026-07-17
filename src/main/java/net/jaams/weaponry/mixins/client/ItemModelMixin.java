package net.jaams.weaponry.mixins.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.util.ModTags;

@Mixin(ItemOverrides.class)
public abstract class ItemModelMixin {

	@Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
	private void jaam$resolve(BakedModel model, ItemStack stack, ClientLevel level,
			LivingEntity entity, int seed,
			CallbackInfoReturnable<BakedModel> cir) {
		String skin = getSkinFromStack(stack);
		if (skin == null) return;

		ModelResourceLocation modelLoc = getSkinModel(skin);
		if (modelLoc == null) {
			modelLoc = getNunchakuSkinModel(skin, level, entity, stack, seed);
		}
		if (modelLoc == null) return;

		BakedModel customModel = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (customModel == null || customModel == Minecraft.getInstance().getModelManager().getMissingModel()) return;

		cir.setReturnValue(customModel);
	}

	private static String getSkinFromStack(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;

		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("ItemSkin")) {
			String raw = tag.getString("ItemSkin").toLowerCase();
				String skin = normalizeSkinName(raw);
				if (skin != null) return skin;
		}

		var tagManager = ForgeRegistries.ITEMS.tags();
		boolean isKatana = tagManager != null && tagManager.getTag(ModTags.KATANAS).contains(stack.getItem());
		boolean isBoomerang = tagManager != null && tagManager.getTag(ModTags.HUNTERS_BOOMERANGS).contains(stack.getItem());
		boolean isNunchaku = tagManager != null && tagManager.getTag(ModTags.NUNCHAKUS).contains(stack.getItem());
		boolean isBroadsword = tagManager != null && tagManager.getTag(ModTags.BROADSWORDS).contains(stack.getItem());
		boolean isButterflySword = tagManager != null && tagManager.getTag(ModTags.BUTTERFLY_SWORDS).contains(stack.getItem());
		boolean isLongsword = tagManager != null && tagManager.getTag(ModTags.LONGSWORDS).contains(stack.getItem());
		if (!isKatana && !isBoomerang && !isNunchaku && !isBroadsword && !isButterflySword && !isLongsword) return null;

		String displayName = stack.getDisplayName().getString().toLowerCase();
		if (displayName.startsWith("[") && displayName.endsWith("]")) {
			String raw = displayName.substring(1, displayName.length() - 1);
			String skin = normalizeSkinName(raw);
			if (skin != null) return skin;
		}
		String skin = normalizeSkinName(displayName);
		if (skin != null) return skin;

		return null;
	}

	private static ModelResourceLocation getSkinModel(String skin) {
		ModelResourceLocation result;
		switch (skin) {
			case "rengoku":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_rengoku"), "inventory");
				break;
			case "mitsuri":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_mitsuri"), "inventory");
				break;
			case "zenitsu":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_zenitsu"), "inventory");
				break;
			case "inosuke":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_inosuke"), "inventory");
				break;
			case "sokka":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_sokka"), "inventory");
				break;
			case "macuahuitl":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_macuahuitl"), "inventory");
				break;
			case "rita":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_rita"), "inventory");
				break;
			case "blood_sword":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "skin_blood_sword"), "inventory");
				break;
			default:
				result = null;
				break;
		}
		return result;
	}

	private static String normalizeSkinName(String input) {
		String normalized = input.replaceAll("[\\s_]+", "_").replaceAll("[^a-z0-9_]", "").toLowerCase();
		String result;
		switch (normalized) {
			case "rengoku":
				result = "rengoku";
				break;
			case "mitsuri":
				result = "mitsuri";
				break;
			case "zenitsu":
				result = "zenitsu";
				break;
			case "inosuke":
				result = "inosuke";
				break;
			case "sokka":
				result = "sokka";
				break;
			case "macuahuitl":
				result = "macuahuitl";
				break;
			case "rita":
				result = "rita";
				break;
			case "blood_sword": case "bloodsword":
				result = "blood_sword";
				break;
			case "rock_lee": case "rocklee":
				result = "rock_lee";
				break;
			case "michaelangelo":
				result = "michaelangelo";
				break;
			default:
				result = null;
				break;
		}
		return result;
	}

	private static ModelResourceLocation getNunchakuSkinModel(String skin, ClientLevel level, LivingEntity entity, ItemStack stack, int seed) {
		boolean isActive = false;
		if (entity != null && stack != null && !stack.isEmpty()) {
			ItemPropertyFunction property = ItemProperties.getProperty(stack.getItem(),
					ResourceLocation.parse("jaams_weaponry:nunchaku_pulling"));
			if (property != null) {
				isActive = property.call(stack, level, entity, seed) >= 0.5F;
			}
		}
		ModelResourceLocation result;
		switch (skin) {
			case "rock_lee":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", isActive ? "skin_rock_lee_active" : "skin_rock_lee_idle"), "inventory");
				break;
			case "michaelangelo":
				result = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", isActive ? "skin_michaelangelo_active" : "skin_michaelangelo_idle"), "inventory");
				break;
			default:
				result = null;
				break;
		}
		return result;
	}
}
