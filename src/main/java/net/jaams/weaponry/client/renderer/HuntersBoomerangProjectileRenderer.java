package net.jaams.weaponry.client.renderer;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.entity.HuntersBoomerangProjectileEntity;
import net.jaams.weaponry.data.ProjectileRenderData;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;

import java.util.Locale;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class HuntersBoomerangProjectileRenderer extends EntityRenderer<HuntersBoomerangProjectileEntity> {
    public HuntersBoomerangProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HuntersBoomerangProjectileEntity entity, float entityYaw, float partialTicks,
            PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        ItemStack weapon = entity.getWeaponItem();
        ProjectileRenderData data = new ProjectileRenderData(weapon);
        ThrowableItemData.RenderEntry render = ThrowableItemData.getRenderConfig(weapon);
        float scale = data.getFloat("ProjectileScale", (render != null && render.scale != null) ? render.scale
                : ProjectileClientConfig.HUNTERS_BOOMERANG_PROJECTILE_SCALE.get().floatValue());
        float rotX = data.getFloat("ProjectileRotX",
                (render != null && render.base_z_rotation != null) ? render.base_z_rotation : 90.0f);
        float rotY = data.getFloat("ProjectileRotY", 0.0f);
        float rotZ = data.getFloat("ProjectileRotZ", 0.0f);
        float offsetX = data.getFloat("ProjectileOffsetX",
                (render != null && render.offset_x != null) ? render.offset_x : 0.0f);
        float offsetY = data.getFloat("ProjectileOffsetY",
                (render != null && render.offset_y != null) ? render.offset_y : -0.195f);
        float offsetZ = data.getFloat("ProjectileOffsetZ",
                (render != null && render.offset_z != null) ? render.offset_z : 0.0f);
        float spinSpeed = data.getFloat("ProjectileSpin",
                (render != null && render.spin_speed != null) ? render.spin_speed : -60.0f);
        String spinAxis = data.getString("ProjectileSpinAxis",
                (render != null && render.spin_axis != null) ? render.spin_axis : "Z");
        float spinOffset = data.getFloat("ProjectileSpinOffset",
                (render != null && render.spin_offset != null) ? render.spin_offset : 0.0f);
        float pitchOffset = data.getFloat("ProjectilePitchOffset", 0.0f);
        ItemDisplayContext display = ItemDisplayContext.GROUND;
        ItemDisplayContext nbtDisplay = data.getDisplay("ProjectileDisplay", null);
        if (nbtDisplay != null) {
            display = nbtDisplay;
        } else if (render != null && render.display_context != null && !render.display_context.isEmpty()) {
            ItemDisplayContext jsonDisplay = ThrowableItemData.parseDisplayContext(render.display_context);
            if (jsonDisplay != null) {
                display = jsonDisplay;
            }
        }
        matrixStack.scale(scale, scale, scale);
        float spinRotation;
        if (entity.getSpinTicks() > 0) {
            spinRotation = (entity.getSpinTicks() + partialTicks) * spinSpeed + spinOffset;
        } else {
            spinRotation = entity.getLastSpinRotation();
        }
        matrixStack.mulPose(Axis.XP.rotationDegrees(rotX + pitchOffset));
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        if (pitch > 45 || pitch < -45) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(rotY + 90.0F));
        } else {
            matrixStack.mulPose(Axis.YP.rotationDegrees(rotY));
        }
        switch (spinAxis.toUpperCase(Locale.ROOT)) {
            case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(rotZ + spinRotation));
            case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(rotZ + spinRotation));
            default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(rotZ + spinRotation));
        }
        matrixStack.translate(offsetX, offsetY, offsetZ);
        Minecraft.getInstance().getItemRenderer().renderStatic(weapon, display, packedLight, OverlayTexture.NO_OVERLAY,
                matrixStack, buffer, entity.level(), entity.getId());
        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HuntersBoomerangProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
