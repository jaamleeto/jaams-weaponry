package net.jaams.weaponry.client.renderer;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

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

import net.jaams.weaponry.entity.ProngedKunaiProjectileEntity;
import net.jaams.weaponry.data.ProjectileRenderData;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;

import java.util.Locale;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class ProngedKunaiProjectileRenderer extends EntityRenderer<ProngedKunaiProjectileEntity> {
    public ProngedKunaiProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ProngedKunaiProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        ItemStack weapon = entity.weaponItem;
        ProjectileRenderData data = new ProjectileRenderData(weapon);
        ThrowableItemData.RenderEntry render = ThrowableItemData.getRenderConfig(weapon);
        float scale = data.getFloat("ProjectileScale", (render != null && render.scale != null) ? render.scale
                : ProjectileClientConfig.PRONGED_KUNAI_PROJECTILE_SCALE.get().floatValue());
        float rotX = data.getFloat("ProjectileRotX", 0.0f);
        float rotY = data.getFloat("ProjectileRotY",
                (render != null && render.yaw_offset != null) ? render.yaw_offset : -90.0f);
        float rotZ = data.getFloat("ProjectileRotZ", -135.0f);
        float offsetX = data.getFloat("ProjectileOffsetX",
                (render != null && render.offset_x != null) ? render.offset_x : -0.05f);
        float offsetY = data.getFloat("ProjectileOffsetY",
                (render != null && render.offset_y != null) ? render.offset_y : -0.22f);
        float offsetZ = data.getFloat("ProjectileOffsetZ",
                (render != null && render.offset_z != null) ? render.offset_z : 0.0f);
        float spinSpeed = data.getFloat("ProjectileSpin",
                (render != null && render.spin_speed != null) ? render.spin_speed : 0.0f);
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
        float lerpYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float lerpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        matrixStack.mulPose(Axis.YP.rotationDegrees(lerpYaw + rotY));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(lerpPitch + rotX + pitchOffset));
        float spinRotation = 0.0f;
        if (spinSpeed > 0 && !entity.hasImpacted()) {
            float velocityFactor = (float) entity.getDeltaMovement().length() * 10.0f;
            float finalSpinSpeed = spinSpeed + velocityFactor;
            spinRotation = (entity.tickCount + partialTicks) * finalSpinSpeed + spinOffset;
        }
        switch (spinAxis.toUpperCase(Locale.ROOT)) {
            case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(rotZ + spinRotation));
            case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(rotZ + spinRotation));
            default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(rotZ + spinRotation));
        }
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        matrixStack.translate(offsetX, offsetY, offsetZ);
        Minecraft.getInstance().getItemRenderer().renderStatic(weapon, display, packedLight, OverlayTexture.NO_OVERLAY,
                matrixStack, buffer, entity.level(), entity.getId());
        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ProngedKunaiProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
