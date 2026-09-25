package net.jaams.weaponry.client.renderer;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class ItemProjectileRenderer extends EntityRenderer<ItemProjectileEntity> {
    public ItemProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        final float SCALE_FACTOR = ProjectileClientConfig.ITEM_PROJECTILE_SCALE.get().floatValue();
        matrixStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        if (!entity.hasImpacted()) {
            float baseSpinSpeed = 30.0F;
            float spinRotation = ((float) entity.getSpinTicks() + partialTicks) * baseSpinSpeed;
            matrixStack.mulPose(Axis.XP.rotationDegrees(spinRotation));
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getProjectileItem();
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                matrixStack, buffer, entity.level(), entity.getId());
        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ItemProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
