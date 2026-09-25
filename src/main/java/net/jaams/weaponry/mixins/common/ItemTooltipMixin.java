package net.jaams.weaponry.mixins.common;

import net.jaams.weaponry.tooltip.helper.TooltipHelper;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void jaam$addCustomTooltipsHead(ItemStack stack, @Nullable net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip,
            TooltipFlag flag, CallbackInfo ci) {
        TooltipHelper.addAllTooltips(stack, tooltip);
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void jaam$appendHoverText(ItemStack stack, @Nullable net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip, TooltipFlag flag,
            CallbackInfo ci) {
        TooltipHelper.addGunInventoryTooltips(stack, tooltip);
    }
}