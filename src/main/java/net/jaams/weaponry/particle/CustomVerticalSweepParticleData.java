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
import net.minecraft.util.Mth;

public class CustomVerticalSweepParticleData implements ParticleOptions {

    public static final MapCodec<CustomVerticalSweepParticleData> CODEC = RecordCodecBuilder.mapCodec((instance) ->
        instance
            .group(Codec.FLOAT.fieldOf("r").forGetter((data) -> data.r), Codec.FLOAT.fieldOf("g").forGetter((data) -> data.g), Codec.FLOAT.fieldOf("b").forGetter((data) -> data.b), Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(instance, CustomVerticalSweepParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomVerticalSweepParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (data) -> data.r,
            ByteBufCodecs.FLOAT, (data) -> data.g,
            ByteBufCodecs.FLOAT, (data) -> data.b,
            ByteBufCodecs.FLOAT, (data) -> data.size,
            CustomVerticalSweepParticleData::new);

    private final float r;
    private final float g;
    private final float b;
    private final float size;

    public CustomVerticalSweepParticleData(float r, float g, float b, float size) {
        this.r = Mth.clamp(r, 0.0F, 1.0F);
        this.g = Mth.clamp(g, 0.0F, 1.0F);
        this.b = Mth.clamp(b, 0.0F, 1.0F);
        this.size = Math.max(size, 0.0F);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CUSTOM_VERTICAL_SWEEP_PARTICLE.get();
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
