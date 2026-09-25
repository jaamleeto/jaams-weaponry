package net.jaams.weaponry.client.renderer;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.MultiBufferSource;

import net.jaams.weaponry.gun.helper.BulletRenderHelper;
import net.jaams.weaponry.entity.EchoBulletProjectileEntity;

import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class EchoBulletProjectileRenderer extends EntityRenderer<EchoBulletProjectileEntity> {
    public EchoBulletProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EchoBulletProjectileEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        BulletRenderHelper.render(entity, matrixStack, buffer, packedLight, partialTicks);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EchoBulletProjectileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(EchoBulletProjectileEntity entity, Frustum frustum, double camX, double camY,
            double camZ) {
        return super.shouldRender(entity, frustum, camX, camY, camZ);
    }
}
