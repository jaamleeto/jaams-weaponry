package net.jaams.weaponry.particle;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class GunSparkParticle extends TextureSheetParticle {
	private final SpriteSet sprites;
	private final float size;

	public static GunSparkParticleProvider provider(SpriteSet spriteSet) {
		return new GunSparkParticleProvider(spriteSet);
	}

	public static class GunSparkParticleProvider implements ParticleProvider<GunSparkParticleData> {
		private final SpriteSet sprite;

		public GunSparkParticleProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(GunSparkParticleData data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			GunSparkParticle particle = new GunSparkParticle(world, x, y, z, data.getSize(), this.sprite);
			particle.setSpriteFromAge(this.sprite);
			return particle;
		}
	}

	protected GunSparkParticle(ClientLevel world, double x, double y, double z, float size, SpriteSet sprites) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.sprites = sprites;
		this.lifetime = 8;
		this.rCol = 1.0F;
		this.gCol = 0.9F;
		this.bCol = 0.1F;
		this.alpha = 1.0F;
		this.size = Math.max(size, 0.0F);
		this.hasPhysics = false;
		this.xd = 0.0D;
		this.yd = 0.0D;
		this.zd = 0.0D;
		this.setSprite(this.sprites.get(0, 6));
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
			this.alpha = 1.0F - ((float) this.age / this.lifetime);
		}
		if (isInsideBlock(this.level)) {
			this.remove();
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

	private boolean isInsideBlock(ClientLevel world) {
		BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
		BlockState state = world.getBlockState(blockPos);
		return !state.isAir();
	}
}
