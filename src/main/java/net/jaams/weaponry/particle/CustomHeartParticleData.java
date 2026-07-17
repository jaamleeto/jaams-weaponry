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

public class CustomHeartParticleData implements ParticleOptions {

    public static final Codec<CustomHeartParticleData> CODEC = RecordCodecBuilder.create((instance) ->
        instance
            .group(Codec.FLOAT.fieldOf("r").forGetter((data) -> data.r), Codec.FLOAT.fieldOf("g").forGetter((data) -> data.g), Codec.FLOAT.fieldOf("b").forGetter((data) -> data.b), Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(instance, CustomHeartParticleData::new)
    );

    @SuppressWarnings("deprecation")
    public static final Deserializer<CustomHeartParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public CustomHeartParticleData fromCommand(ParticleType<CustomHeartParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float g = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float b = Mth.clamp(reader.readFloat(), 0.0F, 1.0F);
            reader.expect(' ');
            float size = Mth.clamp(reader.readFloat(), 0.1F, 2.0F);
            return new CustomHeartParticleData(r, g, b, size);
        }

        @Override
        public CustomHeartParticleData fromNetwork(ParticleType<CustomHeartParticleData> type, FriendlyByteBuf buffer) {
            return new CustomHeartParticleData(Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Mth.clamp(buffer.readFloat(), 0.0F, 1.0F), Mth.clamp(buffer.readFloat(), 0.1F, 2.0F));
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final float size;

    public CustomHeartParticleData(float r, float g, float b, float size) {
        this.r = Mth.clamp(r, 0.0F, 1.0F);
        this.g = Mth.clamp(g, 0.0F, 1.0F);
        this.b = Mth.clamp(b, 0.0F, 1.0F);
        this.size = Mth.clamp(size, 0.1F, 2.0F);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CUSTOM_HEART_PARTICLE.get();
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
