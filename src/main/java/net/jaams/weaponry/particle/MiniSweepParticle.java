package net.jaams.weaponry.particle;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class MiniSweepParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final SpriteSet sprites;
    private final float size;

    public static MiniSweepParticleProvider provider(SpriteSet spriteSet) {
        return new MiniSweepParticleProvider(spriteSet);
    }

    public static class MiniSweepParticleProvider implements ParticleProvider<MiniSweepParticleData> {
        private final SpriteSet sprite;

        public MiniSweepParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(MiniSweepParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            MiniSweepParticle particle = new MiniSweepParticle(world, x, y, z, xSpeed, data.getR(), data.getG(),
                    data.getB(), data.getSize(), this.sprite);
            particle.setSpriteFromAge(this.sprite);
            return particle;
        }
    }

    protected MiniSweepParticle(ClientLevel world, double x, double y, double z, double xSpeed, float r, float g,
            float b, float size, SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 4;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.size = Math.max(size, 0.0F);
        this.hasPhysics = false;
        this.xd = xSpeed;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.setSpriteFromAge(sprites);
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
            this.rCol = this.initialR;
            this.gCol = this.initialG;
            this.bCol = this.initialB;
            this.alpha = 1.0F - ((float) this.age / this.lifetime);
        }
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.size;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
}
