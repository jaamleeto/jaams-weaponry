package net.jaams.weaponry.mixins.item;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import net.jaams.weaponry.util.ModTags;

@Mixin(value = PickaxeItem.class, remap = true)
public abstract class PickaxeItemMixin {

    @Inject(method = "canPerformAction", at = @At("RETURN"), cancellable = true, remap = false)
    private void jaam$canPerformAction(ItemStack stack, ToolAction toolAction,
            CallbackInfoReturnable<Boolean> cir) { 
        
        if (stack.is(ModTags.HAMMERS) || stack.is(ModTags.GREAT_HAMMERS)) {
            cir.setReturnValue(toolAction == ToolActions.PICKAXE_DIG || toolAction == ToolActions.SWORD_SWEEP);
        }
    }
}
