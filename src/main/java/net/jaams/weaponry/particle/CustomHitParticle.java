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
public class CustomHitParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final SpriteSet sprites;
    private final float size;

    public static CustomHitParticleProvider provider(SpriteSet spriteSet) {
        return new CustomHitParticleProvider(spriteSet);
    }

    public static class CustomHitParticleProvider implements ParticleProvider<CustomHitParticleData> {
        private final SpriteSet sprite;

        public CustomHitParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomHitParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomHitParticle particle = new CustomHitParticle(world, x, y, z, data.getR(), data.getG(), data.getB(),
                    data.getSize(), this.sprite);
            particle.setSpriteFromAge(this.sprite);
            return particle;
        }
    }

    protected CustomHitParticle(ClientLevel world, double x, double y, double z, float r, float g, float b, float size,
            SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 6;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.size = Math.max(size, 0.0F);
        this.hasPhysics = false;
        this.xd = 0.0D;
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
        return this.size * (1.0F + this.age * 0.03F);
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
