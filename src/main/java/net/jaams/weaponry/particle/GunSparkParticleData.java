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

public class GunSparkParticleData implements ParticleOptions {

    public static final Codec<GunSparkParticleData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size)).apply(instance, GunSparkParticleData::new));

    @SuppressWarnings("deprecation")
    public static final Deserializer<GunSparkParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public GunSparkParticleData fromCommand(ParticleType<GunSparkParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = Math.max(reader.readFloat(), 0.0F);
            return new GunSparkParticleData(size);
        }

        @Override
        public GunSparkParticleData fromNetwork(ParticleType<GunSparkParticleData> type, FriendlyByteBuf buffer) {
            return new GunSparkParticleData(Math.max(buffer.readFloat(), 0.0F));
        }
    };

    private final float size;

    public GunSparkParticleData(float size) {
        this.size = Math.max(size, 0.0F);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.GUN_SPARK_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(size);
    }

    @Override
    public String writeToString() {
        return String.format("%s %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), size);
    }

    public float getSize() {
        return size;
    }
}
