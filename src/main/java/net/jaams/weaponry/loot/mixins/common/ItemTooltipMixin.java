package net.jaams.weaponry.mixins.common;

import net.jaams.weaponry.capability.CapHelper;

import java.util.List;
import javax.annotation.Nullable;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.tooltip.helper.TooltipHelper;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

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
        if (!ModGuns.isGun(stack))
            return;
        boolean showInventory = GunSystemCommonConfig.GUN_INVENTORY.get()
                || GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get();
        if (!showInventory)
            return;
        CapHelper.itemHandler(stack).ifPresent((handler) -> {
            int slotCount = handler.getSlots();
            for (int i = 0; i < slotCount; i++) {
                ItemStack slotItem = handler.getStackInSlot(i);
                if (slotItem.isEmpty())
                    continue;
                MutableComponent iconComponent;
                MutableComponent textComponent;
                Style style = Style.EMPTY.withColor(TextColor.fromRgb(0xCD7F32));
                if (ModGuns.isRevolverGun(stack)) {
                    if (i >= 1 && i <= 6) {
                        String itemName = slotItem.getHoverName().getString();
                        int count = slotItem.getCount();
                        iconComponent = Component.literal("\uFFF1")
                                .withStyle((s) -> s.withFont(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "default")));
                        String chamberLabel = TooltipsConfig.TOOLTIP_REVOLVER_ROMAN_NUMERALS.get()
                                ? ModTooltips.toRoman(i) : Integer.toString(i);
                        textComponent = Component.literal(" Chamber " + chamberLabel + ": " + itemName + " x" + count);
                        style = Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700));
                    } else {
                        String itemName = slotItem.getHoverName().getString();
                        int count = slotItem.getCount();
                        iconComponent = Component.literal("\uFFF2")
                                .withStyle((s) -> s.withFont(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "default")));
                        boolean isDamageable = slotItem.isDamageableItem() && slotItem.getMaxDamage() > 0;
                        if (isDamageable) {
                            int durability = slotItem.getMaxDamage() - slotItem.getDamageValue();
                            int maxDurability = slotItem.getMaxDamage();
                            textComponent = Component.literal(" " + itemName + " " + durability + "/" + maxDurability);
                        } else {
                            textComponent = Component.literal(" " + itemName + " x" + count);
                        }
                    }
                } else {
                    if (i == 1) {
                        String itemName = slotItem.getHoverName().getString();
                        int count = slotItem.getCount();
                        iconComponent = Component.literal("\uFFF1")
                                .withStyle((s) -> s.withFont(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "default")));
                        textComponent = Component.literal(" " + itemName + " x" + count);
                        style = Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700));
                    } else {
                        String itemName = slotItem.getHoverName().getString();
                        int count = slotItem.getCount();
                        iconComponent = Component.literal("\uFFF2")
                                .withStyle((s) -> s.withFont(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "default")));
                        boolean isDamageable = slotItem.isDamageableItem() && slotItem.getMaxDamage() > 0;
                        if (isDamageable) {
                            int durability = slotItem.getMaxDamage() - slotItem.getDamageValue();
                            int maxDurability = slotItem.getMaxDamage();
                            textComponent = Component.literal(" " + itemName + " " + durability + "/" + maxDurability);
                        } else {
                            textComponent = Component.literal(" " + itemName + " x" + count);
                        }
                    }
                }
                MutableComponent combined = Component.empty().append(iconComponent)
                        .append(textComponent.withStyle(style));
                tooltip.add(combined);
            }
        });
    }
}
