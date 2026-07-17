package net.jaams.weaponry.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jaams.weaponry.init.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.g;

public class SmallWaveParticleData implements ParticleOptions {

    public static final Codec<SmallWaveParticleData> CODEC = RecordCodecBuilder.create((instance) ->
        instance
            .group(Codec.FLOAT.fieldOf("r").forGetter((data) -> data.r), Codec.FLOAT.fieldOf("g").forGetter((data) -> data.g), Codec.FLOAT.fieldOf("b").forGetter((data) -> data.b), Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(instance, SmallWaveParticleData::new)
    );

    @SuppressWarnings("deprecation")
    public static final Deserializer<SmallWaveParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public SmallWaveParticleData fromCommand(ParticleType<SmallWaveParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float g = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float b = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float size = Math.max(reader.readFloat(), 0.0F);
            return new SmallWaveParticleData(r, g, b, size);
        }

        @Override
        public SmallWaveParticleData fromNetwork(ParticleType<SmallWaveParticleData> type, FriendlyByteBuf buffer) {
            return new SmallWaveParticleData(Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Math.max(buffer.readFloat(), 0.0F));
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final float size;

    public SmallWaveParticleData(float r, float g, float b, float size) {
        this.r = Mth.clamp(r, 0.0F, 1.0F);
        this.g = Mth.clamp(g, 0.0F, 1.0F);
        this.b = Mth.clamp(b, 0.0F, 1.0F);
        this.size = Math.max(size, 0.0F);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SMALL_WAVE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(r);
        buffer.writeFloat(g);
        buffer.writeFloat(b);
        buffer.writeFloat(size);
    }

    @Override
    public String writeToString() {
        return String.format("%s %.2f %.2f %.2f %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), r, g, b, size);
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
