package net.jaams.weaponry.handler.gun;

import net.jaams.weaponry.capability.CapHelper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class GunGuiHandler {

    private static final int SLOT_WIDTH = 20;
    private static final int SLOT_HEIGHT = 20;
    private static final int ROW_GAP = 2;

    @SubscribeEvent
    public static void renderGameOverlayEvent(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || !GunSystemCommonConfig.GUN_INVENTORY.get() || !GunSystemClientConfig.SHOW_OVERLAY.get()) {
            return;
        }
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        boolean isRightArm = player.getMainArm() == HumanoidArm.RIGHT;
        int mainHandX = isRightArm ? screenWidth - 60 : 40;
        int mainHandY = GunSystemClientConfig.OVERLAY_POSITION.get() == ModEnums.OverlayPosition.TOP ? 20 : screenHeight - 40;
        int offHandX = isRightArm ? 40 : screenWidth - 60;
        int offHandY = GunSystemClientConfig.OVERLAY_POSITION.get() == ModEnums.OverlayPosition.TOP ? 20 : screenHeight - 40;
        try {
            mainHandX += GunSystemClientConfig.MAIN_HAND_X.get().intValue();
            mainHandY += GunSystemClientConfig.MAIN_HAND_Y.get().intValue();
            offHandX += GunSystemClientConfig.OFF_HAND_X.get().intValue();
            offHandY += GunSystemClientConfig.OFF_HAND_Y.get().intValue();
            mainHandX = Mth.clamp(mainHandX, -SLOT_WIDTH, screenWidth);
            mainHandY = Mth.clamp(mainHandY, -SLOT_HEIGHT, screenHeight);
            offHandX = Mth.clamp(offHandX, -SLOT_WIDTH, screenWidth);
            offHandY = Mth.clamp(offHandY, -SLOT_HEIGHT, screenHeight);
        } catch (Exception ignored) {}
        renderGunOverlay(mc, player.getMainHandItem(), event, mainHandX, mainHandY);
        renderGunOverlay(mc, player.getOffhandItem(), event, offHandX, offHandY);
    }

    private static void renderGunOverlay(Minecraft mc, ItemStack gunItem, RenderGuiEvent.Post event, int x, int y) {
        if (!ModGuns.isGun(gunItem)) {
            return;
        }
        renderBulletInGui(mc, gunItem, event, x, y);
    }

    private static void renderBulletInGui(Minecraft mc, ItemStack gunItem, RenderGuiEvent.Post event, int x, int y) {
        CapHelper.itemHandler(gunItem).ifPresent((cap) -> {
            int itemCount;
            int[] slots;
            boolean twoRows = false;
            ModGuns.GunType type = ModGuns.getGunType(gunItem);
            GunItemData.GunEntry gunData = GunItemData.getGunData(gunItem);
            boolean ammoFromGun = GunShootHelper.getFinalAmmoSource(gunItem, "GunAmmoFromGun",
                    GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get,
                    gunData != null ? gunData.ammo_from_gun : null);
            if (type == ModGuns.GunType.REVOLVER || type == ModGuns.GunType.PEPPERBOX) {
                boolean isPepperbox = type == ModGuns.GunType.PEPPERBOX;
                boolean extendedOverlay = isPepperbox && GunSystemClientConfig.GUN_PEPPERBOX_OVERLAY_EXTENDED.get();
                int currentChamber = ModGuns.getRevolverChamberSlot(gunItem);
                ItemStack attachment = getSlotStack(cap, 0);
                int ammoCount = 0;
                if (ammoFromGun) {
                    if (extendedOverlay) {
                        // Pepperbox fires every loaded barrel in one shot, so
                        // show all loaded ammo instead of a single chamber.
                        for (int slot = 1; slot < cap.getSlots(); slot++) {
                            if (!getSlotStack(cap, slot).isEmpty())
                                ammoCount++;
                        }
                    } else if (isPepperbox) {
                        // Single ammo slot: chamber 1 has priority, then the next loaded one.
                        if (getFirstLoadedAmmoSlot(cap) >= 0)
                            ammoCount = 1;
                    } else if (!getSlotStack(cap, currentChamber).isEmpty()) {
                        ammoCount = 1;
                    }
                }
                int attachmentCount = GunSystemClientConfig.RENDER_SLOT_0.get() && !attachment.isEmpty() ? 1 : 0;
                itemCount = ammoCount + attachmentCount;
                if (itemCount == 0) return;
                twoRows = extendedOverlay && GunSystemClientConfig.GUN_PEPPERBOX_OVERLAY_TWO_ROWS.get() && itemCount > 4;
                slots = new int[itemCount];
                int idx = 0;
                if (attachmentCount > 0) slots[idx++] = 0;
                if (ammoFromGun) {
                    if (extendedOverlay) {
                        for (int slot = 1; slot < cap.getSlots(); slot++) {
                            if (!getSlotStack(cap, slot).isEmpty())
                                slots[idx++] = slot;
                        }
                    } else if (isPepperbox) {
                        int loaded = getFirstLoadedAmmoSlot(cap);
                        if (loaded >= 0)
                            slots[idx++] = loaded;
                    } else if (!getSlotStack(cap, currentChamber).isEmpty()) {
                        slots[idx++] = currentChamber;
                    }
                }
            } else {
                ItemStack slot0 = getSlotStack(cap, 0);
                ItemStack slot1 = getSlotStack(cap, 1);
                ItemStack slot2 = getSlotStack(cap, 2);
                itemCount = countRenderSlots(slot0, slot1, slot2, ammoFromGun);
                if (itemCount == 0) return;
                slots = new int[itemCount];
                int idx = 0;
                if (GunSystemClientConfig.RENDER_SLOT_0.get() && !slot0.isEmpty()) slots[idx++] = 0;
                if (GunSystemClientConfig.RENDER_SLOT_1.get() && ammoFromGun && !slot1.isEmpty()) slots[idx++] = 1;
                if (GunSystemClientConfig.RENDER_SLOT_2.get() && !slot2.isEmpty()) slots[idx++] = 2;
            }
            int row1Count = twoRows ? (itemCount + 1) / 2 : itemCount;
            int row2Count = twoRows ? itemCount - row1Count : 0;
            int maxRowWidth = twoRows ? Math.max(row1Count, row2Count) : itemCount;
            int overlayWidth = maxRowWidth * SLOT_WIDTH;
            int overlayHeight = twoRows ? (2 * SLOT_HEIGHT) + ROW_GAP : SLOT_HEIGHT;
            int adjustedX = x - ((maxRowWidth - 1) * (SLOT_WIDTH / 2));
            int adjustedY = y;
            int margin = GunSystemClientConfig.BORDER_THICKNESS.get() + 4;
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            adjustedX = Mth.clamp(adjustedX, margin, Math.max(margin, screenWidth - overlayWidth - margin));
            adjustedY = Mth.clamp(adjustedY, margin, Math.max(margin, screenHeight - overlayHeight - margin));
            final int clampedX = adjustedX;
            final int clampedY = adjustedY;
            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0, 0.0, -300.0);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (GunSystemClientConfig.BACKGROUND_ENABLED.get()) {
                int backgroundColor = GunSystemClientConfig.BACKGROUND_COLOR.get().getColor() | GunSystemClientConfig.BACKGROUND_TRANSPARENCY.get().getAlpha();
                int borderColor = GunSystemClientConfig.BORDER_COLOR.get().getColor();
                int borderThickness = GunSystemClientConfig.BORDER_THICKNESS.get();
                renderBackgroundWithBorder(guiGraphics, clampedX - 2, clampedY - 2, overlayWidth, overlayHeight, backgroundColor, borderColor, borderThickness);
            }
            int currentX = clampedX + ((maxRowWidth - row1Count) * SLOT_WIDTH) / 2;
            int currentY = clampedY;
            int rendered = 0;
            for (int slot : slots) {
                ItemStack slotStack = getSlotStack(cap, slot);
                if (!slotStack.isEmpty()) {
                    renderItemSlot(mc, slotStack, guiGraphics, currentX, currentY);
                    currentX += SLOT_WIDTH;
                    rendered++;
                    if (twoRows && rendered == row1Count) {
                        currentX = clampedX + ((maxRowWidth - row2Count) * SLOT_WIDTH) / 2;
                        currentY += SLOT_HEIGHT + ROW_GAP;
                    }
                }
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            guiGraphics.pose().popPose();
        });
    }

    private static int countRenderSlots(ItemStack slot0, ItemStack slot1, ItemStack slot2,
            boolean ammoFromGun) {
        int count = 0;
        if (GunSystemClientConfig.RENDER_SLOT_0.get() && !slot0.isEmpty()) count++;
        if (GunSystemClientConfig.RENDER_SLOT_1.get() && ammoFromGun && !slot1.isEmpty()) count++;
        if (GunSystemClientConfig.RENDER_SLOT_2.get() && !slot2.isEmpty()) count++;
        return count;
    }

    private static ItemStack getSlotStack(IItemHandler handler, int slot) {
        return handler.getStackInSlot(slot);
    }

    private static int getFirstLoadedAmmoSlot(IItemHandler handler) {
        for (int slot = 1; slot < handler.getSlots(); slot++) {
            if (!handler.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private static void renderItemSlot(Minecraft mc, ItemStack itemStack, GuiGraphics guiGraphics, int x, int y) {
        if (!itemStack.isEmpty()) {
            guiGraphics.renderItem(itemStack, x, y);
            guiGraphics.renderItemDecorations(mc.font, itemStack, x, y);
        }
    }

    private static void renderBackgroundWithBorder(GuiGraphics guiGraphics, int x, int y, int width, int height, int backgroundColor, int borderColor, int borderThickness) {
        guiGraphics.fill(x - borderThickness, y - borderThickness, x + width + borderThickness, y, borderColor);
        guiGraphics.fill(x - borderThickness, y + height, x + width + borderThickness, y + height + borderThickness, borderColor);
        guiGraphics.fill(x - borderThickness, y, x, y + height, borderColor);
        guiGraphics.fill(x + width, y, x + width + borderThickness, y + height, borderColor);
        guiGraphics.fill(x, y, x + width, y + height, backgroundColor);
    }
}
