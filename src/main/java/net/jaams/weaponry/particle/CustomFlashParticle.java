package net.jaams.weaponry.particle;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.Mth;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Camera;

import com.mojang.blaze3d.vertex.VertexConsumer;

@OnlyIn(Dist.CLIENT)
public class CustomFlashParticle extends TextureSheetParticle {
    public static CustomFlashParticleProvider provider(SpriteSet spriteSet) {
        return new CustomFlashParticleProvider(spriteSet);
    }

    public static class CustomFlashParticleProvider implements ParticleProvider<CustomFlashParticleData> {
        private final SpriteSet sprite;

        public CustomFlashParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomFlashParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomFlashParticle particle = new CustomFlashParticle(world, x, y, z, data.getR(), data.getG(),
                    data.getB(), data.getSize());
            particle.pickSprite(this.sprite);
            return particle;
        }
    }

    protected CustomFlashParticle(ClientLevel world, double x, double y, double z, float r, float g, float b,
            float size) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.speedUpWhenYMotionIsBlocked = false;
        this.friction = 0.0F;
        this.xd = 0.0F;
        this.yd = 0.0F;
        this.zd = 0.0F;
        this.gravity = 0.0F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 0.9F;
        this.quadSize = size;
        this.lifetime = 8;
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float ageProgress = ((float) this.age + partialTicks) / (float) this.lifetime;
        float scale = this.quadSize * (1.0F + Mth.sin(ageProgress * Mth.PI * 3.0F) * 0.3F);
        return Mth.clamp(scale, this.quadSize * 0.7F, this.quadSize * 1.3F);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        float ageProgress = ((float) this.age + partialTicks) / (float) this.lifetime;
        float alpha = Mth.sin(ageProgress * Mth.PI * 3.0F) * 0.5F + 0.3F;
        this.setAlpha(Mth.clamp(alpha, 0.0F, 0.9F));
        super.render(vertexConsumer, camera, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
