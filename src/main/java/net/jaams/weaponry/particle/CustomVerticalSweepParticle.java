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
public class CustomVerticalSweepParticle extends TextureSheetParticle {
	private final SpriteSet sprites;
	private static final float ROTATION_ANGLE = -1.5707964F;

	public static CustomVerticalSweepParticleProvider provider(SpriteSet spriteSet) {
		return new CustomVerticalSweepParticleProvider(spriteSet);
	}

	public static class CustomVerticalSweepParticleProvider implements ParticleProvider<CustomVerticalSweepParticleData> {
		private final SpriteSet sprite;

		public CustomVerticalSweepParticleProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(CustomVerticalSweepParticleData data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomVerticalSweepParticle particle = new CustomVerticalSweepParticle(world, x, y, z, xSpeed, data.getSize(), this.sprite);
			particle.setSpriteFromAge(this.sprite);
			return particle;
		}
	}

	protected CustomVerticalSweepParticle(ClientLevel world, double x, double y, double z, double xSpeed, float size, SpriteSet sprites) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.sprites = sprites;
		this.lifetime = 4;
		float f = this.random.nextFloat() * 0.6F + 0.4F;
		this.rCol = f;
		this.gCol = f;
		this.bCol = f;
		this.alpha = 1.0F;
		this.quadSize = 1.0F - size * 0.5F;
		this.hasPhysics = false;
		this.xd = xSpeed;
		this.yd = 0.0D;
		this.zd = 0.0D;
		this.oRoll = ROTATION_ANGLE;
		this.roll = ROTATION_ANGLE;
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
			this.alpha = 1.0F - ((float) this.age / this.lifetime);
		}
	}

	@Override
	public float getQuadSize(float partialTicks) {
		return this.quadSize;
	}

	@Override
	public int getLightColor(float partialTicks) {
		return 15728880;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}
}
