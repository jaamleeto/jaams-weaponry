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

@OnlyIn(Dist.CLIENT)
public class CustomGlintParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final float size;
    private final SpriteSet sprites;

    public static CustomGlintParticleProvider provider(SpriteSet spriteSet) {
        return new CustomGlintParticleProvider(spriteSet);
    }

    public static class CustomGlintParticleProvider implements ParticleProvider<CustomGlintParticleData> {
        private final SpriteSet sprite;

        public CustomGlintParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomGlintParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomGlintParticle particle = new CustomGlintParticle(world, x, y, z, data.getR(), data.getG(),
                    data.getB(), data.getSize(), this.sprite);
            particle.setSpriteFromAge(this.sprite);
            return particle;
        }
    }

    protected CustomGlintParticle(ClientLevel world, double x, double y, double z, float r, float g, float b,
            float size, SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.size = Math.max(size, 0.1F);
        this.friction = 0.95F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.gravity = 0.0F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.quadSize = this.size * 0.5F * (0.8F + this.random.nextFloat() * 0.2F);
        this.lifetime = 8;
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
            this.rCol = this.initialR;
            this.gCol = this.initialG;
            this.bCol = this.initialB;
            float progress = (float) this.age / this.lifetime;
            this.alpha = 1.0F - (progress * progress);
        }
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.size * Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime * 1.5F, 0.0F, 1.0F);
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
