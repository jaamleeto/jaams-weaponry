package net.jaams.weaponry.client.renderer;

import org.joml.Matrix4f;

import net.minecraftforge.client.event.RenderLevelStageEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;


public class RenderUtils {
    public static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    public static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");

    
    public static void swapVanillaTexture(ResourceLocation original, ResourceLocation replacement) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        SimpleTexture newTexture = new SimpleTexture(replacement);
        newTexture.bind();
        textureManager.register(original, newTexture);
    }

    
    public static void renderCustomSun(RenderLevelStageEvent event, ResourceLocation texture) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack posestack = event.getPoseStack();
        posestack.pushPose();
        GlStateManager._enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager._depthMask(false);
        float f11 = 1.0F - mc.getInstance().player.level().getRainLevel(event.getPartialTick());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        posestack.mulPose(
                Axis.XP.rotationDegrees(mc.getInstance().player.level().getTimeOfDay(event.getPartialTick()) * 360.0F));
        Matrix4f matrix4f1 = posestack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferbuilder = new BufferBuilder(256);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F);
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F);
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F);
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._disableBlend();
        RenderSystem.defaultBlendFunc();
        GlStateManager._depthMask(true);
        posestack.popPose();
    }

    
    public static void renderCustomMoon(RenderLevelStageEvent event, ResourceLocation texture) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack posestack = event.getPoseStack();
        posestack.pushPose();
        GlStateManager._enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager._depthMask(false);
        float f11 = 1.0F - mc.getInstance().player.level().getRainLevel(event.getPartialTick());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        posestack.mulPose(
                Axis.XP.rotationDegrees(mc.getInstance().player.level().getTimeOfDay(event.getPartialTick()) * 360.0F));
        Matrix4f matrix4f1 = posestack.last().pose();
        float f12 = 20.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        int k = mc.getInstance().player.level().getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float f13 = (float) (l + 0) / 4.0F;
        float f14 = (float) (i1 + 0) / 2.0F;
        float f15 = (float) (l + 1) / 4.0F;
        float f16 = (float) (i1 + 1) / 2.0F;
        BufferBuilder bufferbuilder = new BufferBuilder(256);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, f12).uv(f15, f16);
        bufferbuilder.vertex(matrix4f1, f12, -100.0F, f12).uv(f13, f16);
        bufferbuilder.vertex(matrix4f1, f12, -100.0F, -f12).uv(f13, f14);
        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, -f12).uv(f15, f14);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._disableBlend();
        RenderSystem.defaultBlendFunc();
        GlStateManager._depthMask(true);
        posestack.popPose();
    }

    
    public static void renderCustomSkybox(RenderLevelStageEvent event, ResourceLocation texture, int color,
            float alpha) {
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        GlStateManager._enableBlend();
        RenderSystem.defaultBlendFunc();
        GlStateManager._depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        int[] rgb = getRGB(color);
        RenderSystem.setShaderColor((float) rgb[0] / 255, (float) rgb[1] / 255, (float) rgb[2] / 255, alpha);
        float distance = 100.0F;
        float size = 100.0F;
        renderSkyboxQuad(poseStack, -size, distance, -size, 1.0F / 4.0F, 1.0F / 3.0F, size, distance, -size,
                2.0F / 4.0F, 1.0F / 3.0F, size, distance, size, 2.0F / 4.0F, 0.0F, -size, distance, size, 1.0F / 4.0F,
                0.0F);
        renderSkyboxQuad(poseStack, -size, -distance, -size, 1.0F / 4.0F, 2.0F / 3.0F, -size, -distance, size,
                1.0F / 4.0F, 3.0F / 3.0F, size, -distance, size, 2.0F / 4.0F, 3.0F / 3.0F, size, -distance, -size,
                2.0F / 4.0F, 2.0F / 3.0F);
        renderSkyboxQuad(poseStack, -distance, -size, size, 0.0F, 2.0F / 3.0F, -distance, -size, -size, 1.0F / 4.0F,
                2.0F / 3.0F, -distance, size, -size, 1.0F / 4.0F, 1.0F / 3.0F, -distance, size, size, 0.0F,
                1.0F / 3.0F);
        renderSkyboxQuad(poseStack, -size, -size, -distance, 1.0F / 4.0F, 2.0F / 3.0F, size, -size, -distance,
                2.0F / 4.0F, 2.0F / 3.0F, size, size, -distance, 2.0F / 4.0F, 1.0F / 3.0F, -size, size, -distance,
                1.0F / 4.0F, 1.0F / 3.0F);
        renderSkyboxQuad(poseStack, distance, -size, -size, 2.0F / 4.0F, 2.0F / 3.0F, distance, -size, size,
                3.0F / 4.0F, 2.0F / 3.0F, distance, size, size, 3.0F / 4.0F, 1.0F / 3.0F, distance, size, -size,
                2.0F / 4.0F, 1.0F / 3.0F);
        renderSkyboxQuad(poseStack, size, -size, distance, 3.0F / 4.0F, 2.0F / 3.0F, -size, -size, distance,
                4.0F / 4.0F, 2.0F / 3.0F, -size, size, distance, 4.0F / 4.0F, 1.0F / 3.0F, size, size, distance,
                3.0F / 4.0F, 1.0F / 3.0F);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GlStateManager._depthMask(true);
        GlStateManager._disableBlend();
        poseStack.popPose();
    }

    private static void renderSkyboxQuad(PoseStack poseStack, float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3,
            float x4,
            float y4, float z4, float u4, float v4) {
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = new BufferBuilder(256);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x1, y1, z1).uv(u1, v1);
        buffer.vertex(matrix, x2, y2, z2).uv(u2, v2);
        buffer.vertex(matrix, x3, y3, z3).uv(u3, v3);
        buffer.vertex(matrix, x4, y4, z4).uv(u4, v4);
        BufferUploader.drawWithShader(buffer.end());
    }

    public static int[] getRGB(int hexColor) {
        int[] rgb = new int[3];
        rgb[0] = (hexColor >> 16) & 0xFF;
        rgb[1] = (hexColor >> 8) & 0xFF;
        rgb[2] = hexColor & 0xFF;
        return rgb;
    }
}
