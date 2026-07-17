package net.jaams.weaponry.mixins.common;

import java.util.List;
import javax.annotation.Nullable;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.tooltip.helper.TooltipHelper;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void jaam$addCustomTooltipsHead(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag flag, CallbackInfo ci) {
        TooltipHelper.addAllTooltips(stack, tooltip);
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void jaam$appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag,
            CallbackInfo ci) {
        if (!ModGuns.isGun(stack))
            return;
        boolean showInventory = GunSystemCommonConfig.GUN_INVENTORY.get()
                || GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get();
        if (!showInventory)
            return;
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent((handler) -> {
            for (int i = 0; i < 3; i++) {
                ItemStack slotItem = handler.getStackInSlot(i);
                if (slotItem.isEmpty())
                    continue;
                MutableComponent iconComponent;
                MutableComponent textComponent;
                Style style = Style.EMPTY.withColor(TextColor.fromRgb(0xCD7F32));
                if (i == 1) {
                    String itemName = slotItem.getHoverName().getString();
                    int count = slotItem.getCount();
                    iconComponent = Component.literal("\uFFF1")
                            .withStyle((s) -> s.withFont(new ResourceLocation("jaams_weaponry", "default")));
                    textComponent = Component.literal(" " + itemName + " x" + count);
                    style = Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700));
                } else {
                    String itemName = slotItem.getHoverName().getString();
                    int count = slotItem.getCount();
                    iconComponent = Component.literal("\uFFF2")
                            .withStyle((s) -> s.withFont(new ResourceLocation("jaams_weaponry", "default")));
                    boolean isDamageable = slotItem.isDamageableItem() && slotItem.getMaxDamage() > 0;
                    if (isDamageable) {
                        int durability = slotItem.getMaxDamage() - slotItem.getDamageValue();
                        int maxDurability = slotItem.getMaxDamage();
                        textComponent = Component.literal(" " + itemName + " " + durability + "/" + maxDurability);
                    } else {
                        textComponent = Component.literal(" " + itemName + " x" + count);
                    }
                }
                MutableComponent combined = Component.empty().append(iconComponent)
                        .append(textComponent.withStyle(style));
                tooltip.add(combined);
            }
        });
    }
}
