package net.jaams.weaponry.particle;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class CustomSmokeParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final float size;
    private final SpriteSet sprites;

    public static CustomSmokeParticleProvider provider(SpriteSet spriteSet) {
        return new CustomSmokeParticleProvider(spriteSet);
    }

    public static class CustomSmokeParticleProvider implements ParticleProvider<CustomSmokeParticleData> {
        private final SpriteSet sprite;

        public CustomSmokeParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomSmokeParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomSmokeParticle particle = new CustomSmokeParticle(world, x, y, z, data.getR(), data.getG(),
                    data.getB(), data.getSize(), this.sprite);
            particle.setSpriteFromAge(this.sprite); 
            return particle;
        }
    }

    protected CustomSmokeParticle(ClientLevel world, double x, double y, double z, float r, float g, float b,
            float size, SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 16;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.size = Math.max(size, 0.1F);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.86F;
        this.xd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.yd = 0.1F;
        this.zd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.gravity = 0.01F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.quadSize = this.size * 0.75F * (0.6F + this.random.nextFloat() * 0.2F);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
            this.move(this.xd, this.yd, this.zd);
            this.rCol = this.initialR;
            this.gCol = this.initialG;
            this.bCol = this.initialB;
            float progress = (float) this.age / this.lifetime;
            this.alpha = 1.0F - (progress * progress);
        }
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.size * (1.0F + this.age * 0.03F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
