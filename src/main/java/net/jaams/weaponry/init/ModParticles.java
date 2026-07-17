package net.jaams.weaponry.init;

import com.mojang.serialization.Codec;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "jaams_weaponry");
    public static final RegistryObject<ParticleType<CustomCritParticleData>> CUSTOM_CRIT_PARTICLE = PARTICLES.register("custom_crit", () ->
        new ParticleType<CustomCritParticleData>(false, CustomCritParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomCritParticleData> codec() {
                return CustomCritParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomSweepParticleData>> CUSTOM_SWEEP_PARTICLE = PARTICLES.register("custom_sweep", () ->
        new ParticleType<CustomSweepParticleData>(false, CustomSweepParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomSweepParticleData> codec() {
                return CustomSweepParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomVerticalSweepParticleData>> CUSTOM_VERTICAL_SWEEP_PARTICLE = PARTICLES.register("custom_vertical_sweep", () ->
        new ParticleType<CustomVerticalSweepParticleData>(false, CustomVerticalSweepParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomVerticalSweepParticleData> codec() {
                return CustomVerticalSweepParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomExplosionParticleData>> CUSTOM_EXPLOSION_PARTICLE = PARTICLES.register("custom_explosion", () ->
        new ParticleType<CustomExplosionParticleData>(false, CustomExplosionParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomExplosionParticleData> codec() {
                return CustomExplosionParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<EnhancedHitParticleData>> ENHANCED_HIT_PARTICLE = PARTICLES.register("enhanced_hit", () ->
        new ParticleType<EnhancedHitParticleData>(false, EnhancedHitParticleData.DESERIALIZER) {
            @Override
            public Codec<EnhancedHitParticleData> codec() {
                return EnhancedHitParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<EmptyHeartParticleData>> EMPTY_HEART_PARTICLE = PARTICLES.register("empty_heart", () ->
        new ParticleType<EmptyHeartParticleData>(false, EmptyHeartParticleData.DESERIALIZER) {
            @Override
            public Codec<EmptyHeartParticleData> codec() {
                return EmptyHeartParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomSkullParticleData>> CUSTOM_SKULL_PARTICLE = PARTICLES.register("custom_skull", () ->
        new ParticleType<CustomSkullParticleData>(false, CustomSkullParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomSkullParticleData> codec() {
                return CustomSkullParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomFlashParticleData>> CUSTOM_FLASH_PARTICLE = PARTICLES.register("custom_flash", () ->
        new ParticleType<CustomFlashParticleData>(false, CustomFlashParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomFlashParticleData> codec() {
                return CustomFlashParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<SmallWaveParticleData>> SMALL_WAVE_PARTICLE = PARTICLES.register("small_wave", () ->
        new ParticleType<SmallWaveParticleData>(false, SmallWaveParticleData.DESERIALIZER) {
            @Override
            public Codec<SmallWaveParticleData> codec() {
                return SmallWaveParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<BigWaveParticleData>> BIG_WAVE_PARTICLE = PARTICLES.register("big_wave", () ->
        new ParticleType<BigWaveParticleData>(false, BigWaveParticleData.DESERIALIZER) {
            @Override
            public Codec<BigWaveParticleData> codec() {
                return BigWaveParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<GunSparkParticleData>> GUN_SPARK_PARTICLE = PARTICLES.register("gun_spark", () ->
        new ParticleType<GunSparkParticleData>(false, GunSparkParticleData.DESERIALIZER) {
            @Override
            public Codec<GunSparkParticleData> codec() {
                return GunSparkParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomHitParticleData>> CUSTOM_HIT_PARTICLE = PARTICLES.register("custom_hit", () ->
        new ParticleType<CustomHitParticleData>(false, CustomHitParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomHitParticleData> codec() {
                return CustomHitParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<MiniSweepParticleData>> MINI_SWEEP_PARTICLE = PARTICLES.register("mini_sweep", () ->
        new ParticleType<MiniSweepParticleData>(false, MiniSweepParticleData.DESERIALIZER) {
            @Override
            public Codec<MiniSweepParticleData> codec() {
                return MiniSweepParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomSmokeParticleData>> CUSTOM_SMOKE_PARTICLE = PARTICLES.register("custom_smoke", () ->
        new ParticleType<CustomSmokeParticleData>(false, CustomSmokeParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomSmokeParticleData> codec() {
                return CustomSmokeParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomSoulParticleData>> CUSTOM_SOUL_PARTICLE = PARTICLES.register("custom_soul", () ->
        new ParticleType<CustomSoulParticleData>(false, CustomSoulParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomSoulParticleData> codec() {
                return CustomSoulParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomHeartParticleData>> CUSTOM_HEART_PARTICLE = PARTICLES.register("custom_heart", () ->
        new ParticleType<CustomHeartParticleData>(false, CustomHeartParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomHeartParticleData> codec() {
                return CustomHeartParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomGlintParticleData>> CUSTOM_GLINT_PARTICLE = PARTICLES.register("custom_glint", () ->
        new ParticleType<CustomGlintParticleData>(false, CustomGlintParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomGlintParticleData> codec() {
                return CustomGlintParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomBuffParticleData>> CUSTOM_BUFF_PARTICLE = PARTICLES.register("custom_buff", () ->
        new ParticleType<CustomBuffParticleData>(false, CustomBuffParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomBuffParticleData> codec() {
                return CustomBuffParticleData.CODEC;
            }
        }
    );
    public static final RegistryObject<ParticleType<CustomDebuffParticleData>> CUSTOM_DEBUFF_PARTICLE = PARTICLES.register("custom_debuff", () ->
        new ParticleType<CustomDebuffParticleData>(false, CustomDebuffParticleData.DESERIALIZER) {
            @Override
            public Codec<CustomDebuffParticleData> codec() {
                return CustomDebuffParticleData.CODEC;
            }
        }
    );

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
