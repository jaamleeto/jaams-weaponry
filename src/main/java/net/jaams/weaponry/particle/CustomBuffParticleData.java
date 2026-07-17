package net.jaams.weaponry.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jaams.weaponry.init.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.g;

public class CustomBuffParticleData implements ParticleOptions {

    public static final Codec<CustomBuffParticleData> CODEC = RecordCodecBuilder.create((instance) ->
        instance
            .group(Codec.FLOAT.fieldOf("r").forGetter((data) -> data.r), Codec.FLOAT.fieldOf("g").forGetter((data) -> data.g), Codec.FLOAT.fieldOf("b").forGetter((data) -> data.b), Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(instance, CustomBuffParticleData::new)
    );

    @SuppressWarnings("deprecation")
    public static final Deserializer<CustomBuffParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public CustomBuffParticleData fromCommand(ParticleType<CustomBuffParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            return new CustomBuffParticleData(r, g, b, size);
        }

        @Override
        public CustomBuffParticleData fromNetwork(ParticleType<CustomBuffParticleData> type, FriendlyByteBuf buffer) {
            return new CustomBuffParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final float size;

    public CustomBuffParticleData(float r, float g, float b, float size) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.size = size;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CUSTOM_BUFF_PARTICLE.get();
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
