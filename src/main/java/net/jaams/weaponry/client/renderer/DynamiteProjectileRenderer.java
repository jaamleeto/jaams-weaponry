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

import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.data.ProjectileRenderData;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;

import java.util.Locale;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class DynamiteProjectileRenderer extends EntityRenderer<DynamiteProjectileEntity> {
    public DynamiteProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DynamiteProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        ItemStack weapon = entity.weaponItem;
        ProjectileRenderData data = new ProjectileRenderData(weapon);
        ThrowableItemData.RenderEntry render = ThrowableItemData.getRenderConfig(weapon);
        float scale = data.getFloat("ProjectileScale", (render != null && render.scale != null) ? render.scale
                : ProjectileClientConfig.DYNAMITE_PROJECTILE_SCALE.get().floatValue());
        float yawOffset = data.getFloat("ProjectileYawOffset",
                (render != null && render.yaw_offset != null) ? render.yaw_offset : 0.0f);
        float baseZRotation = data.getFloat("ProjectileBaseZRotation",
                (render != null && render.base_z_rotation != null) ? render.base_z_rotation : 65.0f);
        float offsetX = data.getFloat("ProjectileOffsetX",
                (render != null && render.offset_x != null) ? render.offset_x : 0.0f);
        float offsetY = data.getFloat("ProjectileOffsetY",
                (render != null && render.offset_y != null) ? render.offset_y : 0.0f);
        float offsetZ = data.getFloat("ProjectileOffsetZ",
                (render != null && render.offset_z != null) ? render.offset_z : 0.0f);
        float pitchOffset = data.getFloat("ProjectilePitchOffset", 0.0f);
        float spinSpeed = data.getFloat("ProjectileSpin",
                (render != null && render.spin_speed != null) ? render.spin_speed : 30.0f);
        String spinAxis = data.getString("ProjectileSpinAxis",
                (render != null && render.spin_axis != null) ? render.spin_axis : "X");
        float spinOffset = data.getFloat("ProjectileSpinOffset",
                (render != null && render.spin_offset != null) ? render.spin_offset : 0.0f);
        ItemDisplayContext display = data.getDisplay("ProjectileDisplay",
                ThrowableItemData.parseDisplayContext(render != null ? render.display_context : null));
        matrixStack.scale(scale, scale, scale);
        float lerpYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        matrixStack.mulPose(Axis.YP.rotationDegrees(lerpYaw + yawOffset));
        if (!entity.hasImpacted()) {
            float velocityFactor = (float) entity.getDeltaMovement().length() * 10.0F;
            float finalSpinSpeed = spinSpeed + velocityFactor;
            float spinRotation = ((float) entity.getSpinTicks() + partialTicks) * finalSpinSpeed + spinOffset;
            switch (spinAxis.toUpperCase(Locale.ROOT)) {
                case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(spinRotation));
                case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(spinRotation));
                default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(spinRotation));
            }
        } else {
            float lerpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
            float zRotation = baseZRotation + lerpPitch + pitchOffset;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(zRotation));
        }
        matrixStack.translate(offsetX, offsetY, offsetZ);
        Minecraft.getInstance().getItemRenderer().renderStatic(weapon, display, packedLight, OverlayTexture.NO_OVERLAY,
                matrixStack, buffer, entity.level(), entity.getId());
        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DynamiteProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
