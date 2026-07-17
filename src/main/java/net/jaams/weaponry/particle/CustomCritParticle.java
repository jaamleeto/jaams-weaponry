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
public class CustomCritParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;

    public static CustomCritParticleProvider provider(SpriteSet spriteSet) {
        return new CustomCritParticleProvider(spriteSet);
    }

    public static class CustomCritParticleProvider implements ParticleProvider<CustomCritParticleData> {
        private final SpriteSet sprite;

        public CustomCritParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(CustomCritParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            CustomCritParticle particle = new CustomCritParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, data.getR(),
                    data.getG(), data.getB(), data.getSize());
            particle.pickSprite(this.sprite);
            return particle;
        }
    }

    protected CustomCritParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed, float r, float g, float b, float size) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.7F;
        this.gravity = 0.2F;
        this.xd *= 0.1D;
        this.yd *= 0.1D;
        this.zd *= 0.1D;
        this.xd += xSpeed * 0.4D;
        this.yd += ySpeed * 0.4D;
        this.zd += zSpeed * 0.4D;
        this.initialR = r;
        this.initialG = g;
        this.initialB = b;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.quadSize = size * 0.65F * 1.0F;
        this.lifetime = Math.max((int) (8.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.hasPhysics = false;
        this.tick();
    }

    @Override
    public void tick() {
        super.tick();
        this.rCol = this.initialR;
        this.gCol = this.initialG;
        this.bCol = this.initialB;
        this.alpha = 1.0F - ((float) this.age / this.lifetime);
        double friction = 0.98;
        this.xd *= friction;
        this.yd *= friction;
        this.zd *= friction;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.quadSize * Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
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
