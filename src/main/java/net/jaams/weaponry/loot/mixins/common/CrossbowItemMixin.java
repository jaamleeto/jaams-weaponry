package net.jaams.weaponry.mixins.common;

import java.util.List;
import javax.annotation.Nullable;

import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;
import net.jaams.weaponry.item.RoyalCrossbowItem;
import net.jaams.weaponry.item.StakeCrossbowItem;
import net.jaams.weaponry.tooltip.helper.TooltipHelper;
import net.jaams.weaponry.util.ModTooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void jaam$appendCrossbowTooltips(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip,
            TooltipFlag flag, CallbackInfo ci) {
        TooltipHelper.addAllTooltips(stack, tooltip);

        if (stack.is(Items.CROSSBOW)) {
            ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
            ModTooltips.addStat(stack, tooltip, "base_damage", 1.5);
            ModTooltips.addStat(stack, tooltip, "draw_speed", 1.0);
            ModTooltips.addStat(stack, tooltip, "load_time", 1.25);
            ModTooltips.addStat(stack, tooltip, "recoil", 0.0);
        }
    }

    @Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
    private static void modifyChargeDuration(ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof GreatCrossbowItem) {
            cir.setReturnValue(GreatCrossbowItem.getChargeDuration(stack));
        } else if (stack.getItem() instanceof HuntersCrossbowItem) {
            cir.setReturnValue(HuntersCrossbowItem.getChargeDuration(stack));
        } else if (stack.getItem() instanceof RoyalCrossbowItem) {
            cir.setReturnValue(RoyalCrossbowItem.getChargeDuration(stack));
        } else if (stack.getItem() instanceof StakeCrossbowItem) {
            cir.setReturnValue(StakeCrossbowItem.getChargeDuration(stack));
        }
    }

    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    private void modifyPerformShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float velocity, float inaccuracy, @org.jetbrains.annotations.Nullable LivingEntity target, CallbackInfo ci) {
        Item item = crossbow.getItem();
        if (item instanceof GreatCrossbowItem) {
            GreatCrossbowItem.executeShooting(level, shooter, hand, crossbow, velocity, inaccuracy);
            ci.cancel();
        } else if (item instanceof HuntersCrossbowItem) {
            HuntersCrossbowItem.executeShooting(level, shooter, hand, crossbow, velocity, inaccuracy);
            ci.cancel();
        } else if (item instanceof RoyalCrossbowItem) {
            RoyalCrossbowItem.executeShooting(level, shooter, hand, crossbow, velocity, inaccuracy);
            ci.cancel();
        } else if (item instanceof StakeCrossbowItem) {
            StakeCrossbowItem.executeShooting(level, shooter, hand, crossbow, velocity, inaccuracy);
            ci.cancel();
        }
    }
}
