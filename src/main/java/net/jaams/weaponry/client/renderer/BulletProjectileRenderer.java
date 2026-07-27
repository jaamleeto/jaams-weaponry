package net.jaams.weaponry.client.renderer;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

import net.jaams.weaponry.gun.helper.BulletRenderHelper;
import net.jaams.weaponry.entity.BulletProjectileEntity;

import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class BulletProjectileRenderer extends EntityRenderer<BulletProjectileEntity> {
    public BulletProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BulletProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        BulletRenderHelper.render(entity, matrixStack, buffer, packedLight, partialTicks);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
