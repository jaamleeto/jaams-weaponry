package net.jaams.weaponry.client.renderer;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HierarchicalModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;


public class ItemArms {
    public static void renderPartWithArms(HierarchicalModel<Entity> model, PoseStack poseStack,
            VertexConsumer vertexConsumer, MultiBufferSource bufferSource, int packedLight, int packedOverlay,
            PlayerModel<?> playerModel,
            ResourceLocation skinTexture, boolean invisible) {
        ModelPart leftArm = model.root().getChild("left_arm");
        ModelPart rightArm = model.root().getChild("right_arm");
        if (leftArm != null)
            leftArm.visible = false;
        if (rightArm != null)
            rightArm.visible = false;
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        if (leftArm != null && !invisible) {
            poseStack.pushPose();
            playerModel.leftArm.x = leftArm.x - 1.5f;
            playerModel.leftArm.y = leftArm.y;
            playerModel.leftArm.z = leftArm.z - 1.5f;
            playerModel.leftArm.xRot = leftArm.xRot + (float) Math.toRadians(180f);
            playerModel.leftArm.yRot = leftArm.yRot;
            playerModel.leftArm.zRot = leftArm.zRot;
            playerModel.leftSleeve.x = leftArm.x - 1.5f;
            playerModel.leftSleeve.y = leftArm.y;
            playerModel.leftSleeve.z = leftArm.z - 1.5f;
            playerModel.leftSleeve.xRot = leftArm.xRot + (float) Math.toRadians(180f);
            playerModel.leftSleeve.yRot = leftArm.yRot;
            playerModel.leftSleeve.zRot = leftArm.zRot;
            playerModel.leftArm.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture)),
                    packedLight, OverlayTexture.NO_OVERLAY);
            playerModel.leftSleeve.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture)),
                    packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (rightArm != null && !invisible) {
            poseStack.pushPose();
            playerModel.rightArm.x = rightArm.x + 1.5f;
            playerModel.rightArm.y = rightArm.y;
            playerModel.rightArm.z = rightArm.z - 1.5f;
            playerModel.rightArm.xRot = rightArm.xRot + (float) Math.toRadians(180f);
            playerModel.rightArm.yRot = rightArm.yRot;
            playerModel.rightArm.zRot = rightArm.zRot;
            playerModel.rightSleeve.x = rightArm.x + 1.5f;
            playerModel.rightSleeve.y = rightArm.y;
            playerModel.rightSleeve.z = rightArm.z - 1.5f;
            playerModel.rightSleeve.xRot = rightArm.xRot + (float) Math.toRadians(180f);
            playerModel.rightSleeve.yRot = rightArm.yRot;
            playerModel.rightSleeve.zRot = rightArm.zRot;
            playerModel.rightArm.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture)),
                    packedLight, OverlayTexture.NO_OVERLAY);
            playerModel.rightSleeve.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture)),
                    packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
