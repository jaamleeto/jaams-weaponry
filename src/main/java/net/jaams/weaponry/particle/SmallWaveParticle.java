package net.jaams.weaponry.particle;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class SmallWaveParticle extends TextureSheetParticle {
    private final float initialR, initialG, initialB;
    private final SpriteSet sprites;
    private final float size;

    public static SmallWaveParticleProvider provider(SpriteSet spriteSet) {
        return new SmallWaveParticleProvider(spriteSet);
    }

    public static class SmallWaveParticleProvider implements ParticleProvider<SmallWaveParticleData> {
        private final SpriteSet sprite;

        public SmallWaveParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SmallWaveParticleData data, ClientLevel world, double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed) {
            SmallWaveParticle particle = new SmallWaveParticle(world, x, y, z, data.getR(), data.getG(), data.getB(),
                    data.getSize(), this.sprite);
            particle.setSpriteFromAge(this.sprite);
            return particle;
        }
    }

    protected SmallWaveParticle(ClientLevel world, double x, double y, double z, float r, float g, float b, float size,
            SpriteSet sprites) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 8;
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
        this.setSprite(this.sprites.get(0, 5));
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
        if (isInsideBlock(this.level)) {
            this.remove();
        }
    }

    @Override
    public void setSpriteFromAge(SpriteSet spriteSet) {
        if (!this.removed) {
            int spriteCount = 5;
            int spriteIndex = (int) ((float) this.age / this.lifetime * spriteCount);
            spriteIndex = Math.min(spriteIndex, spriteCount - 1);
            this.setSprite(spriteSet.get(spriteIndex, spriteCount));
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
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private boolean isInsideBlock(ClientLevel world) {
        BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
        BlockState state = world.getBlockState(blockPos);
        return !state.isAir();
    }
}
