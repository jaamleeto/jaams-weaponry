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

public class CustomDebuffParticleData implements ParticleOptions {

    public static final MapCodec<CustomDebuffParticleData> CODEC = RecordCodecBuilder.mapCodec((instance) ->
        instance
            .group(Codec.FLOAT.fieldOf("r").forGetter((data) -> data.r), Codec.FLOAT.fieldOf("g").forGetter((data) -> data.g), Codec.FLOAT.fieldOf("b").forGetter((data) -> data.b), Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(instance, CustomDebuffParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomDebuffParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (data) -> data.r,
            ByteBufCodecs.FLOAT, (data) -> data.g,
            ByteBufCodecs.FLOAT, (data) -> data.b,
            ByteBufCodecs.FLOAT, (data) -> data.size,
            CustomDebuffParticleData::new);

    private final float r;
    private final float g;
    private final float b;
    private final float size;

    public CustomDebuffParticleData(float r, float g, float b, float size) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.size = size;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CUSTOM_DEBUFF_PARTICLE.get();
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getSize() {
        return size;
    }
}
