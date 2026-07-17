package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import net.jaams.weaponry.util.ModTags;

@Mixin(Item.class)
public abstract class ItemRarityMixin {
    @Inject(method = "getRarity(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/Rarity;", at = @At("HEAD"), cancellable = true)
    private void jaam$modifyItemRarity(ItemStack stack, CallbackInfoReturnable<Rarity> cir) {
        Item item = stack.getItem();
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        if (itemId != null && itemId.getNamespace().equals("jaams_weaponry")) {
            String path = itemId.getPath();
            if (path.contains("enderium") || isRoyalItem(stack, path)) {
                cir.setReturnValue(Rarity.UNCOMMON);
            }
        }
    }

    private static boolean isRoyalItem(ItemStack stack, String path) {
        return path.contains("royal")
            || stack.is(ModTags.ROYAL_AXES)
            || stack.is(ModTags.ROYAL_SPEARS)
            || stack.is(ModTags.ROYAL_RAPIER)
            || stack.is(ModTags.ROYAL_SWORDS)
            || stack.is(ModTags.ROYAL_BOWS)
            || stack.is(ModTags.ROYAL_CROSSBOWS);
    }
}
