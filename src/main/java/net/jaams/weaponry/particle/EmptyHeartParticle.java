package net.jaams.weaponry.particle;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.util.Mth;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class EmptyHeartParticle extends TextureSheetParticle {
    public static EmptyHeartParticleProvider provider(SpriteSet spriteSet) {
        return new EmptyHeartParticleProvider(spriteSet);
    }

    public static class EmptyHeartParticleProvider implements ParticleProvider<EmptyHeartParticleData> {
        private final SpriteSet sprite;

        public EmptyHeartParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(EmptyHeartParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            EmptyHeartParticle particle = new EmptyHeartParticle(world, x, y, z, data.getR(), data.getG(), data.getB(),
                    data.getSize());
            particle.pickSprite(this.sprite);
            return particle;
        }
    }

    protected EmptyHeartParticle(ClientLevel world, double x, double y, double z, float r, float g, float b,
            float size) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.86F;
        this.xd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.yd = 0.1F;
        this.zd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.gravity = 0.02F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.quadSize = size * 0.75F * (0.6F + this.random.nextFloat() * 0.2F);
        this.lifetime = 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.quadSize * Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }
}
