package net.jaams.weaponry.mixins.client;

import net.jaams.weaponry.capability.CapHelper;

import net.jaams.weaponry.util.ModComponents;

import com.mojang.blaze3d.systems.RenderSystem;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.client.ItemStatusBarConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Unique
    private static final int COPPER_COLOR = 0xFFB87333;

    @Unique
    private static final String NBT_HITS = "AfterStrikeHits";

    @Unique
    private static final String NBT_RAPID_BOOST_HITS = "RapidBoostHits";

    @Unique
    private static final String NBT_POWER_BOOST_HITS = "PowerBoostHits";

    @Unique
    private static final String NBT_BUSTER_STRIKE_HITS = "BusterStrikeHits";

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"), require = 0)
    public void jaam$renderItemDecorations(Font font, ItemStack itemStack, int x, int y, String customText,
            CallbackInfo ci) {
        if (GunSystemClientConfig.SHOW_GUN_BAR.get() && ModGuns.isGun(itemStack)) {
            GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
            guiGraphics.pose().pushPose();
            RenderSystem.disableDepthTest();
            boolean ammoFromGun = GunSystemCommonConfig.GUN_AMMO_FROM_GUN.get();
            boolean bundleInteraction = GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get();
            if (ammoFromGun) {
                int ammoSlot = ModGuns.isRevolverGun(itemStack)
                        ? ModGuns.getRevolverChamberSlot(itemStack)
                        : 1;
                ItemStack ammoStack = ModGuns.getItemStack(itemStack, ammoSlot);
                if (!ammoStack.isEmpty()) {
                    renderGunBar(itemStack, ammoStack, x, y);
                } else {
                    renderDurabilityBar(itemStack, x, y);
                }
            } else if (bundleInteraction) {
                renderDurabilityBar(itemStack, x, y);
            }
            RenderSystem.enableDepthTest();
            guiGraphics.pose().popPose();
        }
        if (TraitsConfig.AFTER_STRIKE.get() && ItemStatusBarConfig.SHOW_AFTER_STRIKE_BAR.get()
                && ModTraits.isAfterStrikeItem(itemStack)) {
            CompoundTag nbt = ModComponents.get(itemStack);
            if (nbt != null && nbt.contains(NBT_HITS)) {
                int hits = nbt.getInt(NBT_HITS);
                if (hits > 0) {
                    GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
                    guiGraphics.pose().pushPose();
                    RenderSystem.disableDepthTest();
                    renderAfterStrikeHitBar(itemStack, hits, x, y);
                    RenderSystem.enableDepthTest();
                    guiGraphics.pose().popPose();
                }
            }
        }
        if (TraitsConfig.RAPID_BOOST.get() && ItemStatusBarConfig.SHOW_RAPID_BOOST_BAR.get()
                && ModTraits.isRapidBoostItem(itemStack)) {
            CompoundTag nbt = ModComponents.get(itemStack);
            if (nbt != null && nbt.contains(NBT_RAPID_BOOST_HITS)) {
                int hits = nbt.getInt(NBT_RAPID_BOOST_HITS);
                if (hits > 0) {
                    GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
                    guiGraphics.pose().pushPose();
                    RenderSystem.disableDepthTest();
                    renderRapidBoostHitBar(itemStack, hits, x, y);
                    RenderSystem.enableDepthTest();
                    guiGraphics.pose().popPose();
                }
            }
        }
        if (TraitsConfig.POWER_BOOST.get() && ItemStatusBarConfig.SHOW_POWER_BOOST_BAR.get()
                && ModTraits.isPowerBoostItem(itemStack)) {
            CompoundTag nbt = ModComponents.get(itemStack);
            if (nbt != null && nbt.contains(NBT_POWER_BOOST_HITS)) {
                int hits = nbt.getInt(NBT_POWER_BOOST_HITS);
                if (hits > 0) {
                    GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
                    guiGraphics.pose().pushPose();
                    RenderSystem.disableDepthTest();
                    renderPowerBoostHitBar(itemStack, hits, x, y);
                    RenderSystem.enableDepthTest();
                    guiGraphics.pose().popPose();
                }
            }
        }
        if (TraitsConfig.BUSTER_STRIKE.get() && ItemStatusBarConfig.SHOW_BUSTER_STRIKE_BAR.get()
                && ModTraits.isBusterStrikeItem(itemStack)) {
            CompoundTag nbt = ModComponents.get(itemStack);
            if (nbt != null && nbt.contains(NBT_BUSTER_STRIKE_HITS)) {
                int hits = nbt.getInt(NBT_BUSTER_STRIKE_HITS);
                if (hits > 0) {
                    GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
                    guiGraphics.pose().pushPose();
                    RenderSystem.disableDepthTest();
                    renderBusterStrikeHitBar(itemStack, hits, x, y);
                    RenderSystem.enableDepthTest();
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    @Unique
    private void renderAfterStrikeHitBar(ItemStack itemStack, int hits, int x, int y) {
        int hitsRequired = jaam$getRequiredHits(itemStack);
        if (hitsRequired <= 0)
            return;
        float hitPercentage = (float) hits / (float) hitsRequired;
        int offsetX = ItemStatusBarConfig.AFTER_STRIKE_BAR_X.get();
        int offsetY = ItemStatusBarConfig.AFTER_STRIKE_BAR_Y.get();
        int barWidth = 13;
        int filledWidth = (int) (hitPercentage * barWidth);
        if (filledWidth == 0 && hitPercentage > 0)
            filledWidth = 1;
        int barHeight = 2;
        int barX = x + offsetX;
        int barY = y + offsetY;
        int barColor = jaam$parseHexColor(ItemStatusBarConfig.AFTER_STRIKE_BAR_COLOR.get(), 0xFFFFFFFF);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight - 1, barColor);
        guiGraphics.pose().popPose();
    }

    @Unique
    private void renderGunBar(ItemStack gunStack, ItemStack ammoStack, int x, int y) {
        float percentage = getAmmoPercentage(gunStack, ammoStack);
        if (Float.isNaN(percentage) || percentage < 0)
            percentage = 0.0F;
        int barWidth = 13;
        int filledWidth = (int) (percentage * barWidth);
        if (filledWidth == 0 && percentage > 0)
            filledWidth = 1;
        int barX = x + GunSystemClientConfig.GUN_BAR_X.get();
        int barY = y + GunSystemClientConfig.GUN_BAR_Y.get();
        int color = GunSystemClientConfig.GUN_DEFAULT_COLOR.get() ? 0xFFFF9900 : getBarColorByBulletType(ammoStack);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + 1, color);
        guiGraphics.pose().popPose();
    }

    @Unique
    private void renderDurabilityBar(ItemStack gunStack, int x, int y) {
        ItemStack durabilityStack = getDurabilityStack(gunStack);
        if (durabilityStack.isEmpty() || durabilityStack.getMaxDamage() <= 0) {
            return;
        }
        float percentage = (float) (durabilityStack.getMaxDamage() - durabilityStack.getDamageValue())
                / durabilityStack.getMaxDamage();
        int barWidth = 13;
        int filledWidth = (int) (percentage * barWidth);
        if (filledWidth == 0 && percentage > 0)
            filledWidth = 1;
        int barX = x + GunSystemClientConfig.GUN_BAR_X.get();
        int barY = y + GunSystemClientConfig.GUN_BAR_Y.get();
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + 1, COPPER_COLOR);
        guiGraphics.pose().popPose();
    }

    @Unique
    private float getAmmoPercentage(ItemStack gunStack, ItemStack ammoStack) {
        if (ammoStack.isEmpty())
            return 0.0F;
        int itemMaxStack = ammoStack.getMaxStackSize();
        int slotLimit = ModGuns.getSlotStackLimit(ModGuns.getGunType(gunStack), 1, gunStack);
        int maxForPercentage = Math.min(itemMaxStack, slotLimit);
        if (maxForPercentage <= 0)
            return 0.0F;
        return Math.min((float) ammoStack.getCount() / maxForPercentage, 1.0F);
    }

    @Unique
    private int getBarColorByBulletType(ItemStack bulletStack) {
        if (bulletStack.is(ModItems.BULLET.get()) || bulletStack.is(ModItems.SHOTSHELL.get()))
            return 0xFFFFD700;
        if (bulletStack.is(ModItems.FIRE_BULLET.get()) || bulletStack.is(ModItems.FIRE_SHOTSHELL.get()))
            return 0xFFFF4500;
        if (bulletStack.is(ModItems.HEAVY_BULLET.get()) || bulletStack.is(ModItems.HEAVY_SHOTSHELL.get()))
            return 0xFFD3D3D3;
        if (bulletStack.is(ModItems.GLOWING_BULLET.get()) || bulletStack.is(ModItems.GLOWING_SHOTSHELL.get()))
            return 0xFFEE82EE;
        if (bulletStack.is(ModItems.ECHO_BULLET.get()) || bulletStack.is(ModItems.ECHO_SHOTSHELL.get()))
            return 0xFF008B8B;
        if (bulletStack.is(ModItems.SHARP_BULLET.get()) || bulletStack.is(ModItems.SHARP_SHOTSHELL.get()))
            return 0xFF00FFFF;
        return 0xFFFF9900;
    }

    @Unique
    private ItemStack getDurabilityStack(ItemStack gunStack) {
        return CapHelper.itemHandler(gunStack)
                .map((cap) -> {
                    for (int slot : new int[] { 0, 2 }) {
                        ItemStack stack = cap.getStackInSlot(slot);
                        if (!stack.isEmpty() && stack.getMaxDamage() > 0) {
                            return stack;
                        }
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    @Unique
    private int jaam$getRequiredHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("AfterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.required_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private void renderRapidBoostHitBar(ItemStack itemStack, int hits, int x, int y) {
        int hitsRequired = getRapidBoostMaxHits(itemStack);
        if (hitsRequired <= 0)
            return;
        float hitPercentage = (float) hits / (float) hitsRequired;
        int offsetX = ItemStatusBarConfig.RAPID_BOOST_BAR_X.get();
        int offsetY = ItemStatusBarConfig.RAPID_BOOST_BAR_Y.get();
        int barWidth = 13;
        int filledWidth = (int) (hitPercentage * barWidth);
        if (filledWidth == 0 && hitPercentage > 0)
            filledWidth = 1;
        int barHeight = 2;
        int barX = x + offsetX;
        int barY = y + offsetY;
        int barColor = jaam$parseHexColor(ItemStatusBarConfig.RAPID_BOOST_BAR_COLOR.get(), 0xFF55CFFF);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight - 1, barColor);
        guiGraphics.pose().popPose();
    }

    @Unique
    private void renderPowerBoostHitBar(ItemStack itemStack, int hits, int x, int y) {
        int hitsRequired = getPowerBoostMaxHits(itemStack);
        if (hitsRequired <= 0)
            return;
        float hitPercentage = (float) hits / (float) hitsRequired;
        int offsetX = ItemStatusBarConfig.POWER_BOOST_BAR_X.get();
        int offsetY = ItemStatusBarConfig.POWER_BOOST_BAR_Y.get();
        int barWidth = 13;
        int filledWidth = (int) (hitPercentage * barWidth);
        if (filledWidth == 0 && hitPercentage > 0)
            filledWidth = 1;
        int barHeight = 2;
        int barX = x + offsetX;
        int barY = y + offsetY;
        int barColor = jaam$parseHexColor(ItemStatusBarConfig.POWER_BOOST_BAR_COLOR.get(), 0xFFFF4444);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight - 1, barColor);
        guiGraphics.pose().popPose();
    }

    @Unique
    private int getRapidBoostMaxHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("RapidBoostMaxHits")) {
            return Math.max(1, tag.getInt("RapidBoostMaxHits"));
        }
        int value = TraitModifierData.getRapidBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.RAPID_BOOST_MAX_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private void renderBusterStrikeHitBar(ItemStack itemStack, int hits, int x, int y) {
        int hitsRequired = getBusterStrikeRequiredHits(itemStack);
        if (hitsRequired <= 0)
            return;
        float hitPercentage = (float) hits / (float) hitsRequired;
        int offsetX = ItemStatusBarConfig.BUSTER_STRIKE_BAR_X.get();
        int offsetY = ItemStatusBarConfig.BUSTER_STRIKE_BAR_Y.get();
        int barWidth = 13;
        int filledWidth = (int) (hitPercentage * barWidth);
        if (filledWidth == 0 && hitPercentage > 0)
            filledWidth = 1;
        int barHeight = 2;
        int barX = x + offsetX;
        int barY = y + offsetY;
        int barColor = jaam$parseHexColor(ItemStatusBarConfig.BUSTER_STRIKE_BAR_COLOR.get(), 0xFFFF4444);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight - 1, barColor);
        guiGraphics.pose().popPose();
    }

    @Unique
    private int getBusterStrikeRequiredHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("BusterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("BusterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.required_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private int getPowerBoostMaxHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("PowerBoostMaxHits")) {
            return Math.max(1, tag.getInt("PowerBoostMaxHits"));
        }
        int value = TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_MAX_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private int jaam$parseHexColor(String hexStr, int defaultColor) {
        if (hexStr == null || hexStr.isEmpty()) {
            return defaultColor;
        }
        try {
            if (hexStr.startsWith("#")) {
                hexStr = hexStr.substring(1);
            }
            if (hexStr.length() == 6) {
                hexStr = "FF" + hexStr;
            }
            return (int) Long.parseLong(hexStr, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }
}
