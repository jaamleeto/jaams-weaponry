package net.jaams.weaponry.init;

import com.mojang.serialization.MapCodec;
import net.jaams.weaponry.particle.BigWaveParticleData;
import net.jaams.weaponry.particle.CustomBuffParticleData;
import net.jaams.weaponry.particle.CustomCritParticleData;
import net.jaams.weaponry.particle.CustomDebuffParticleData;
import net.jaams.weaponry.particle.CustomExplosionParticleData;
import net.jaams.weaponry.particle.CustomFlashParticleData;
import net.jaams.weaponry.particle.CustomGlintParticleData;
import net.jaams.weaponry.particle.CustomHeartParticleData;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.jaams.weaponry.particle.CustomSkullParticleData;
import net.jaams.weaponry.particle.CustomSmokeParticleData;
import net.jaams.weaponry.particle.CustomSoulParticleData;
import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.particle.CustomVerticalSweepParticleData;
import net.jaams.weaponry.particle.EmptyHeartParticleData;
import net.jaams.weaponry.particle.EnhancedHitParticleData;
import net.jaams.weaponry.particle.GunSparkParticleData;
import net.jaams.weaponry.particle.MiniSweepParticleData;
import net.jaams.weaponry.particle.SmallWaveParticleData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "jaams_weaponry");
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomCritParticleData>> CUSTOM_CRIT_PARTICLE = PARTICLES.register("custom_crit", () ->
        new ParticleType<CustomCritParticleData>(false) {
            @Override
            public MapCodec<CustomCritParticleData> codec() {
                return CustomCritParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomCritParticleData> streamCodec() {
                return CustomCritParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomSweepParticleData>> CUSTOM_SWEEP_PARTICLE = PARTICLES.register("custom_sweep", () ->
        new ParticleType<CustomSweepParticleData>(false) {
            @Override
            public MapCodec<CustomSweepParticleData> codec() {
                return CustomSweepParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomSweepParticleData> streamCodec() {
                return CustomSweepParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomVerticalSweepParticleData>> CUSTOM_VERTICAL_SWEEP_PARTICLE = PARTICLES.register("custom_vertical_sweep", () ->
        new ParticleType<CustomVerticalSweepParticleData>(false) {
            @Override
            public MapCodec<CustomVerticalSweepParticleData> codec() {
                return CustomVerticalSweepParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomVerticalSweepParticleData> streamCodec() {
                return CustomVerticalSweepParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomExplosionParticleData>> CUSTOM_EXPLOSION_PARTICLE = PARTICLES.register("custom_explosion", () ->
        new ParticleType<CustomExplosionParticleData>(false) {
            @Override
            public MapCodec<CustomExplosionParticleData> codec() {
                return CustomExplosionParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomExplosionParticleData> streamCodec() {
                return CustomExplosionParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<EnhancedHitParticleData>> ENHANCED_HIT_PARTICLE = PARTICLES.register("enhanced_hit", () ->
        new ParticleType<EnhancedHitParticleData>(false) {
            @Override
            public MapCodec<EnhancedHitParticleData> codec() {
                return EnhancedHitParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, EnhancedHitParticleData> streamCodec() {
                return EnhancedHitParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<EmptyHeartParticleData>> EMPTY_HEART_PARTICLE = PARTICLES.register("empty_heart", () ->
        new ParticleType<EmptyHeartParticleData>(false) {
            @Override
            public MapCodec<EmptyHeartParticleData> codec() {
                return EmptyHeartParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, EmptyHeartParticleData> streamCodec() {
                return EmptyHeartParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomSkullParticleData>> CUSTOM_SKULL_PARTICLE = PARTICLES.register("custom_skull", () ->
        new ParticleType<CustomSkullParticleData>(false) {
            @Override
            public MapCodec<CustomSkullParticleData> codec() {
                return CustomSkullParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomSkullParticleData> streamCodec() {
                return CustomSkullParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomFlashParticleData>> CUSTOM_FLASH_PARTICLE = PARTICLES.register("custom_flash", () ->
        new ParticleType<CustomFlashParticleData>(false) {
            @Override
            public MapCodec<CustomFlashParticleData> codec() {
                return CustomFlashParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomFlashParticleData> streamCodec() {
                return CustomFlashParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<SmallWaveParticleData>> SMALL_WAVE_PARTICLE = PARTICLES.register("small_wave", () ->
        new ParticleType<SmallWaveParticleData>(false) {
            @Override
            public MapCodec<SmallWaveParticleData> codec() {
                return SmallWaveParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, SmallWaveParticleData> streamCodec() {
                return SmallWaveParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<BigWaveParticleData>> BIG_WAVE_PARTICLE = PARTICLES.register("big_wave", () ->
        new ParticleType<BigWaveParticleData>(false) {
            @Override
            public MapCodec<BigWaveParticleData> codec() {
                return BigWaveParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, BigWaveParticleData> streamCodec() {
                return BigWaveParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<GunSparkParticleData>> GUN_SPARK_PARTICLE = PARTICLES.register("gun_spark", () ->
        new ParticleType<GunSparkParticleData>(false) {
            @Override
            public MapCodec<GunSparkParticleData> codec() {
                return GunSparkParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, GunSparkParticleData> streamCodec() {
                return GunSparkParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomHitParticleData>> CUSTOM_HIT_PARTICLE = PARTICLES.register("custom_hit", () ->
        new ParticleType<CustomHitParticleData>(false) {
            @Override
            public MapCodec<CustomHitParticleData> codec() {
                return CustomHitParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomHitParticleData> streamCodec() {
                return CustomHitParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<MiniSweepParticleData>> MINI_SWEEP_PARTICLE = PARTICLES.register("mini_sweep", () ->
        new ParticleType<MiniSweepParticleData>(false) {
            @Override
            public MapCodec<MiniSweepParticleData> codec() {
                return MiniSweepParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, MiniSweepParticleData> streamCodec() {
                return MiniSweepParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomSmokeParticleData>> CUSTOM_SMOKE_PARTICLE = PARTICLES.register("custom_smoke", () ->
        new ParticleType<CustomSmokeParticleData>(false) {
            @Override
            public MapCodec<CustomSmokeParticleData> codec() {
                return CustomSmokeParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomSmokeParticleData> streamCodec() {
                return CustomSmokeParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomSoulParticleData>> CUSTOM_SOUL_PARTICLE = PARTICLES.register("custom_soul", () ->
        new ParticleType<CustomSoulParticleData>(false) {
            @Override
            public MapCodec<CustomSoulParticleData> codec() {
                return CustomSoulParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomSoulParticleData> streamCodec() {
                return CustomSoulParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomHeartParticleData>> CUSTOM_HEART_PARTICLE = PARTICLES.register("custom_heart", () ->
        new ParticleType<CustomHeartParticleData>(false) {
            @Override
            public MapCodec<CustomHeartParticleData> codec() {
                return CustomHeartParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomHeartParticleData> streamCodec() {
                return CustomHeartParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomGlintParticleData>> CUSTOM_GLINT_PARTICLE = PARTICLES.register("custom_glint", () ->
        new ParticleType<CustomGlintParticleData>(false) {
            @Override
            public MapCodec<CustomGlintParticleData> codec() {
                return CustomGlintParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomGlintParticleData> streamCodec() {
                return CustomGlintParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomBuffParticleData>> CUSTOM_BUFF_PARTICLE = PARTICLES.register("custom_buff", () ->
        new ParticleType<CustomBuffParticleData>(false) {
            @Override
            public MapCodec<CustomBuffParticleData> codec() {
                return CustomBuffParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomBuffParticleData> streamCodec() {
                return CustomBuffParticleData.STREAM_CODEC;
            }
        }
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<CustomDebuffParticleData>> CUSTOM_DEBUFF_PARTICLE = PARTICLES.register("custom_debuff", () ->
        new ParticleType<CustomDebuffParticleData>(false) {
            @Override
            public MapCodec<CustomDebuffParticleData> codec() {
                return CustomDebuffParticleData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, CustomDebuffParticleData> streamCodec() {
                return CustomDebuffParticleData.STREAM_CODEC;
            }
        }
    );

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
