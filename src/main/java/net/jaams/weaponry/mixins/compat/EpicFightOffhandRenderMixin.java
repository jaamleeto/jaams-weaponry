package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Makes Epic Fight render mod items held in the off-hand.
 *
 * <p>{@code LivingEntityPatch#isOffhandItemValid} gates the off-hand item
 * rendering in {@code PatchedItemInHandLayer} (third person and Epic Fight's
 * first-person model). For weapons whose natural single-wield style does not
 * allow an off-hand item (e.g. two-handed ranged weapons like the mod's
 * slingshots with a {@code epicfight:bow} capability, or the mod's bow and
 * crossbow subclasses), Epic Fight returns {@code false} and the item becomes
 * invisible while it stays fully usable. Overriding the check for mod items in
 * the off-hand keeps them rendered in Epic Fight mode.
 */
@Mixin(value = LivingEntityPatch.class, remap = false)
public abstract class EpicFightOffhandRenderMixin {

    @Inject(method = "isOffhandItemValid", at = @At("HEAD"), cancellable = true, remap = false)
    private void jaams$modOffhandItemsAlwaysValid(CallbackInfoReturnable<Boolean> cir) {
        LivingEntityPatch<?> patch = (LivingEntityPatch<?>) (Object) this;
        LivingEntity original = patch.getOriginal();
        if (original != null && jaams$isModItem(original.getOffhandItem())) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean jaams$isModItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && "jaams_weaponry".equals(key.getNamespace());
    }
}
