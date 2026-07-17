package net.jaams.weaponry.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.client.ItemStatusBarConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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

    @Unique
    private static final String[] ATTACHMENT_MODIFIER_KEYS = new String[] {
            "GunProjectileDamageModifier",
            "GunProjectileSpeed",
            "GunProjectileKnockbackModifier",
            "GunCooldown",
            "GunSpreadAngle",
            "GunProjectileInaccuracy",
            "GunRecoilDistance",
            "GunCrouchRecoilReduction",
            "GunVerticalRecoilMultiplier",
            "GunXRotRecoilIntensity",
            "GunShakeIntensity",
            "GunProjectilePiercingModifier",
            "GunProjectileCount",
            "GunAmmoConsumption",
            "GunAttachmentConsumption",
            "GunShakeResetDelay",
            "GunOffhandCooldown",
            "GunFirePattern" };

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"), require = 0)
    public void jaam$renderItemDecorations(Font font, ItemStack itemStack, int x, int y, String customText,
            CallbackInfo ci) {
        if (GunSystemClientConfig.SHOW_GUN_BARS.get() && ModGuns.isGun(itemStack)) {
            GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
            guiGraphics.pose().pushPose();
            RenderSystem.disableDepthTest();
            // The attachment bar is drawn first and uses its own position config,
            // so it can be placed independently from the ammo bar.
            renderAttachmentBar(itemStack, x, y);
            boolean ammoFromGun = GunSystemCommonConfig.GUN_AMMO_FROM_GUN.get();
            if (ammoFromGun) {
                renderGunBar(itemStack, x, y);
            }
            RenderSystem.enableDepthTest();
            guiGraphics.pose().popPose();
        }
        if (TraitsConfig.AFTER_STRIKE.get() && ItemStatusBarConfig.SHOW_AFTER_STRIKE_BAR.get()
                && ModTraits.isAfterStrikeItem(itemStack)) {
            CompoundTag nbt = itemStack.getTag();
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
            CompoundTag nbt = itemStack.getTag();
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
            CompoundTag nbt = itemStack.getTag();
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
            CompoundTag nbt = itemStack.getTag();
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
    private void renderGunBar(ItemStack gunStack, int x, int y) {
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        boolean isRevolverOrPepperbox = type == ModGuns.GunType.REVOLVER || type == ModGuns.GunType.PEPPERBOX;
        int barX = x + GunSystemClientConfig.AMMO_BAR_X.get();
        int barY = y + GunSystemClientConfig.AMMO_BAR_Y.get();
        int barWidth = 13;
        boolean defaultColor = GunSystemClientConfig.AMMO_BAR_DEFAULT_COLOR.get();
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        if (isRevolverOrPepperbox && GunSystemClientConfig.AMMO_BAR_SLOT_COLORS.get()) {
            renderSegmentedAmmoBar(gunStack, barX, barY, barWidth, defaultColor, guiGraphics);
        } else if (isRevolverOrPepperbox) {
            renderCurrentSlotAmmoBar(gunStack, type, barX, barY, barWidth, defaultColor, guiGraphics);
        } else {
            // Otras guns: comportamiento clásico (slot 1), sin cambios.
            ItemStack ammoStack = getAmmoStackForBar(gunStack, 1);
            if (!ammoStack.isEmpty()) {
                float percentage = getAmmoPercentage(gunStack, ammoStack, 1);
                if (!Float.isFinite(percentage))
                    percentage = 0.0F;
                percentage = Math.max(0.0F, Math.min(1.0F, percentage));
                int filledWidth = (int) (percentage * barWidth);
                if (filledWidth == 0 && percentage > 0)
                    filledWidth = 1;
                int color = defaultColor ? 0xFFFF9900 : getBarColorByBulletType(ammoStack);
                guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
                guiGraphics.fill(barX, barY, barX + filledWidth, barY + 1, color);
            }
        }
        guiGraphics.pose().popPose();
    }

    @Unique
    private void renderCurrentSlotAmmoBar(ItemStack gunStack, ModGuns.GunType type, int barX, int barY,
            int barWidth, boolean defaultColor, GuiGraphics guiGraphics) {
        int currentSlot = ModGuns.getRevolverChamberSlot(gunStack);
        // Pepperbox: si el slot en rotación está vacío, usa la primera bala cargada.
        if (type == ModGuns.GunType.PEPPERBOX && getAmmoStackForBar(gunStack, currentSlot).isEmpty()) {
            int loadedSlot = findFirstLoadedAmmoSlot(gunStack);
            if (loadedSlot >= 0)
                currentSlot = loadedSlot;
        }
        ItemStack ammoStack = getAmmoStackForBar(gunStack, currentSlot);
        // CLAVE: si el slot seleccionado está vacío, NO dibujar NADA (ya no hay barra negra vacía).
        if (ammoStack.isEmpty())
            return;
        float percentage = getAmmoPercentage(gunStack, ammoStack, currentSlot);
        if (!Float.isFinite(percentage))
            percentage = 0.0F;
        percentage = Math.max(0.0F, Math.min(1.0F, percentage));
        int filledWidth = (int) (percentage * barWidth);
        if (filledWidth == 0 && percentage > 0)
            filledWidth = 1;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
        if (filledWidth > 0) {
            int color = defaultColor ? 0xFFFF9900 : getBarColorByBulletType(ammoStack);
            guiGraphics.fill(barX, barY, barX + filledWidth, barY + 1, color);
        }
    }

    @Unique
    private int findFirstLoadedAmmoSlot(ItemStack gunStack) {
        return gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> {
                    for (int slot = 1; slot < handler.getSlots(); slot++) {
                        if (!handler.getStackInSlot(slot).isEmpty())
                            return slot;
                    }
                    return -1;
                })
                .orElse(-1);
    }

    @Unique
    private void renderSegmentedAmmoBar(ItemStack gunStack, int barX, int barY, int barWidth,
            boolean defaultColor, GuiGraphics guiGraphics) {
        gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int firstAmmoSlot = 1;
            int slotCount = Math.max(0, handler.getSlots() - firstAmmoSlot);
            if (slotCount <= 0)
                return;
            boolean hasAmmo = false;
            for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
                if (!handler.getStackInSlot(firstAmmoSlot + slotIndex).isEmpty()) {
                    hasAmmo = true;
                    break;
                }
            }
            if (!hasAmmo)
                return;   // ninguna bala -> nada
            guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
            for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
                int segStartX = barX + (int) Math.floor((double) barWidth * slotIndex / slotCount);
                int segEndX = barX + (int) Math.floor((double) barWidth * (slotIndex + 1) / slotCount);
                if (segEndX <= segStartX)
                    continue;
                ItemStack slotStack = handler.getStackInSlot(firstAmmoSlot + slotIndex);
                if (!slotStack.isEmpty()) {
                    int color = defaultColor ? 0xFFFF9900 : getBarColorByBulletType(slotStack);
                    guiGraphics.fill(segStartX, barY, segEndX, barY + 1, color);
                }
            }
        });
    }

    @Unique
    private ItemStack getAmmoStackForBar(ItemStack gunStack, int slot) {
        return gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.getSlots() > slot ? handler.getStackInSlot(slot).copy() : ItemStack.EMPTY)
                .orElse(ItemStack.EMPTY);
    }

    @Unique
    private float getAmmoPercentage(ItemStack gunStack, ItemStack ammoStack, int slot) {
        if (ammoStack.isEmpty())
            return 0.0F;
        int itemMaxStack = ammoStack.getMaxStackSize();
        int slotLimit = ModGuns.getSlotStackLimit(ModGuns.getGunType(gunStack), slot, gunStack);
        int maxForPercentage = Math.min(itemMaxStack, slotLimit);
        if (maxForPercentage <= 0)
            return 0.0F;
        return (float) ammoStack.getCount() / maxForPercentage;
    }

    @Unique
    private void renderAttachmentBar(ItemStack gunStack, int x, int y) {
        ItemStack attachmentStack = getAttachmentStack(gunStack);
        if (attachmentStack.isEmpty()) {
            return;
        }
        float percentage;
        if (attachmentStack.getMaxDamage() > 0) {
            percentage = (float) (attachmentStack.getMaxDamage() - attachmentStack.getDamageValue())
                    / attachmentStack.getMaxDamage();
        } else {
            percentage = 1.0F;
        }
        int barWidth = 13;
        int filledWidth = (int) (percentage * barWidth);
        if (filledWidth == 0 && percentage > 0)
            filledWidth = 1;
        int barX = x + GunSystemClientConfig.ATTACHMENT_BAR_X.get();
        int barY = y + GunSystemClientConfig.ATTACHMENT_BAR_Y.get();
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + 1, COPPER_COLOR);
        guiGraphics.pose().popPose();
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
    private ItemStack getAttachmentStack(ItemStack gunStack) {
        return gunStack
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map((cap) -> {
                    int[] slots = GunAttachmentHelper.getAttachmentSlots(gunStack);
                    if (slots.length == 0) {
                        slots = new int[] { 0, 2 };
                    }
                    for (int slot : slots) {
                        ItemStack stack = cap.getStackInSlot(slot);
                        if (!stack.isEmpty() && (stack.getMaxDamage() > 0 || isCustomAttachment(stack))) {
                            return stack;
                        }
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    @Unique
    private boolean isCustomAttachment(ItemStack stack) {
        if (GunAttachmentHelper.isAttachment(stack)) {
            return true;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        for (String key : ATTACHMENT_MODIFIER_KEYS) {
            if (tag.contains(key)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private int jaam$getRequiredHits(ItemStack stack) {
        CompoundTag tag = stack.getTag();
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
        CompoundTag tag = stack.getTag();
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
        int barColor = jaam$parseHexColor(ItemStatusBarConfig.BUSTER_STRIKE_BAR_COLOR.get(), 0xFFFF8800);
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight - 1, barColor);
        guiGraphics.pose().popPose();
    }

    @Unique
    private int getBusterStrikeRequiredHits(ItemStack stack) {
        CompoundTag tag = stack.getTag();
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
        CompoundTag tag = stack.getTag();
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
