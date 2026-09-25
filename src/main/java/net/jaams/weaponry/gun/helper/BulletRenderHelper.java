package net.jaams.weaponry.gun.helper;

import net.jaams.weaponry.util.ModComponents;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.entity.BulletProjectileEntity;
import net.jaams.weaponry.data.ProjectileRenderData;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;
import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class BulletRenderHelper {
	public static void render(BaseBulletProjectileEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, float partialTicks) {
		ItemStack gun = entity.getGunItem();
		ProjectileRenderData data = new ProjectileRenderData(gun);
		ItemStack renderStack = getRenderItemStack(gun);
		renderInternal(entity, matrixStack, buffer, packedLight, partialTicks, data, renderStack);
	}

	private static ItemStack getRenderItemStack(ItemStack gun) {
		if (ProjectileClientConfig.BULLET_PROJECTILE_ICON.get()) {
			return new ItemStack(ModItems.BULLET_ICON.get());
		}
		if (ModComponents.has(gun) && ModComponents.get(gun).contains("ProjectileBulletItem", Tag.TAG_COMPOUND)) {
			return ItemStack.parseOptional(net.minecraft.client.Minecraft.getInstance().level.registryAccess(), ModComponents.get(gun).getCompound("ProjectileBulletItem"));
		}
		return new ItemStack(Blocks.AIR);
	}

	private static void renderInternal(Entity entity, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, float partialTicks, ProjectileRenderData data, ItemStack renderStack) {
		float scale = data.getFloat("ProjectileScale", 1.0F);
		float yawOffset = data.getFloat("ProjectileYawOffset", 0.0F);
		float baseZRotation = data.getFloat("ProjectileBaseZRotation", 0.0F);
		float offsetX = data.getFloat("ProjectileOffsetX", 0.0F);
		float offsetY = data.getFloat("ProjectileOffsetY", 0.0F);
		float offsetZ = data.getFloat("ProjectileOffsetZ", 0.0F);
		float spinSpeed = data.getFloat("ProjectileSpin", 0.0F);
		String spinAxis = data.getString("ProjectileSpinAxis", "Z");
		float spinOffset = data.getFloat("ProjectileSpinOffset", 0.0F);
		ItemDisplayContext display = data.getDisplay("ProjectileDisplay", ItemDisplayContext.GROUND);
		matrixStack.pushPose();
		matrixStack.scale(scale, scale, scale);
		float lerpYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		matrixStack.mulPose(Axis.YP.rotationDegrees(lerpYaw + yawOffset));
		boolean impacted = (entity instanceof BulletProjectileEntity bpe) && bpe.hasImpacted();
		if (impacted) {
			Direction hitFace = (entity instanceof BulletProjectileEntity bpe) ? bpe.getHitFace() : null;
			if (hitFace == Direction.UP || hitFace == Direction.DOWN) {
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(baseZRotation));
			} else {
				applySpin(matrixStack, spinSpeed, spinAxis, baseZRotation, spinOffset, entity, partialTicks);
			}
		} else {
			applySpin(matrixStack, spinSpeed, spinAxis, baseZRotation, spinOffset, entity, partialTicks);
		}
		matrixStack.translate(offsetX, offsetY, offsetZ);
		Minecraft.getInstance().getItemRenderer().renderStatic(renderStack, display, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, entity.level(), entity.getId());
		matrixStack.popPose();
	}

	private static void applySpin(PoseStack matrixStack, float spinSpeed, String spinAxis, float baseZRotation, float spinOffset, Entity entity, float partialTicks) {
		float spinRotation = 0.0F;
		if (spinSpeed != 0) {
			float velocityFactor = (float) entity.getDeltaMovement().length() * 10.0F;
			float finalSpinSpeed = spinSpeed + velocityFactor;
			spinRotation = (entity.tickCount + partialTicks) * finalSpinSpeed + spinOffset;
		}
		float zRotation = baseZRotation;
		switch (spinAxis.toUpperCase()) {
			case "X" -> matrixStack.mulPose(Axis.XP.rotationDegrees(zRotation + spinRotation));
			case "Y" -> matrixStack.mulPose(Axis.YP.rotationDegrees(zRotation + spinRotation));
			default -> matrixStack.mulPose(Axis.ZP.rotationDegrees(zRotation + spinRotation));
		}
	}
}
