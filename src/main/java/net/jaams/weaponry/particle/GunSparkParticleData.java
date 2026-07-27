package net.jaams.weaponry.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jaams.weaponry.init.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GunSparkParticleData implements ParticleOptions {

    public static final MapCodec<GunSparkParticleData> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size)).apply(instance, GunSparkParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GunSparkParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (data) -> data.size,
            GunSparkParticleData::new);

    private final float size;

    public GunSparkParticleData(float size) {
        this.size = Math.max(size, 0.0F);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.GUN_SPARK_PARTICLE.get();
    }

    public float getSize() {
        return size;
    }
}
