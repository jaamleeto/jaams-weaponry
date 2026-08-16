package net.jaams.weaponry.handler.gun;

import net.jaams.weaponry.capability.CapHelper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
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
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class GunGuiHandler {

    private static final int SLOT_WIDTH = 20;
    private static final int SLOT_HEIGHT = 20;

    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
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

    private static void renderGunOverlay(Minecraft mc, ItemStack gunItem, CustomizeGuiOverlayEvent.DebugText event, int x, int y) {
        if (!ModGuns.isGun(gunItem)) {
            return;
        }
        renderBulletInGui(mc, gunItem, event, x, y);
    }

    private static void renderBulletInGui(Minecraft mc, ItemStack gunItem, CustomizeGuiOverlayEvent.DebugText event, int x, int y) {
        CapHelper.itemHandler(gunItem).ifPresent((cap) -> {
            int itemCount;
            int[] slots;
            if (ModGuns.isRevolverGun(gunItem)) {
                int currentChamber = ModGuns.getRevolverChamberSlot(gunItem);
                ItemStack chamberBullet = getSlotStack(cap, currentChamber);
                ItemStack attachment = getSlotStack(cap, 0);
                int count = 0;
                if (GunSystemClientConfig.RENDER_SLOT_0.get() && !attachment.isEmpty()) count++;
                if (!chamberBullet.isEmpty()) count++;
                itemCount = count;
                if (itemCount == 0) return;
                slots = new int[itemCount];
                int idx = 0;
                if (GunSystemClientConfig.RENDER_SLOT_0.get() && !attachment.isEmpty()) slots[idx++] = 0;
                if (!chamberBullet.isEmpty()) slots[idx++] = currentChamber;
            } else {
                ItemStack slot0 = getSlotStack(cap, 0);
                ItemStack slot1 = getSlotStack(cap, 1);
                ItemStack slot2 = getSlotStack(cap, 2);
                itemCount = countRenderSlots(slot0, slot1, slot2);
                if (itemCount == 0) return;
                slots = new int[itemCount];
                int idx = 0;
                if (GunSystemClientConfig.RENDER_SLOT_0.get() && !slot0.isEmpty()) slots[idx++] = 0;
                if (GunSystemClientConfig.RENDER_SLOT_1.get() && GunSystemCommonConfig.GUN_AMMO_FROM_GUN.get() && !slot1.isEmpty()) slots[idx++] = 1;
                if (GunSystemClientConfig.RENDER_SLOT_2.get() && !slot2.isEmpty()) slots[idx++] = 2;
            }
            final int adjustedX = x - ((itemCount - 1) * (SLOT_WIDTH / 2));
            final int adjustedY = y;
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
                renderBackgroundWithBorder(guiGraphics, adjustedX - 2, adjustedY - 2, itemCount * SLOT_WIDTH, SLOT_HEIGHT, backgroundColor, borderColor, borderThickness);
            }
            int currentX = adjustedX;
            for (int slot : slots) {
                ItemStack slotStack = getSlotStack(cap, slot);
                if (!slotStack.isEmpty()) {
                    renderItemSlot(mc, slotStack, guiGraphics, currentX, adjustedY);
                    currentX += SLOT_WIDTH;
                }
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            guiGraphics.pose().popPose();
        });
    }

    private static int countRenderSlots(ItemStack slot0, ItemStack slot1, ItemStack slot2) {
        int count = 0;
        if (GunSystemClientConfig.RENDER_SLOT_0.get() && !slot0.isEmpty()) count++;
        if (GunSystemClientConfig.RENDER_SLOT_1.get() && GunSystemCommonConfig.GUN_AMMO_FROM_GUN.get() && !slot1.isEmpty()) count++;
        if (GunSystemClientConfig.RENDER_SLOT_2.get() && !slot2.isEmpty()) count++;
        return count;
    }

    private static ItemStack getSlotStack(IItemHandler handler, int slot) {
        return handler.getStackInSlot(slot);
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
