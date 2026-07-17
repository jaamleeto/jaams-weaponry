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
public class CustomSkullParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final float size;
    private final SpriteSet sprites;

    public static CustomSkullParticleProvider provider(SpriteSet spriteSet) {
        return new CustomSkullParticleProvider(spriteSet);
    }

    public static class CustomSkullParticleProvider implements ParticleProvider<CustomSkullParticleData> {
        private final SpriteSet sprite;

        public CustomSkullParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomSkullParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomSkullParticle particle = new CustomSkullParticle(world, x, y, z, data.getR(), data.getG(),
                    data.getB(), data.getSize(), this.sprite);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }

    protected CustomSkullParticle(ClientLevel world, double x, double y, double z, float r, float g, float b,
            float size, SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.size = Math.max(size, 0.1F);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.86F;
        this.xd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.yd = 0.1F;
        this.zd = 0.01F * (this.random.nextFloat() - 0.5F);
        this.gravity = 0.02F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.quadSize = this.size * 0.75F * (0.6F + this.random.nextFloat() * 0.2F);
        this.lifetime = 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
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
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
