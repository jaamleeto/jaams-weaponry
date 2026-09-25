package net.jaams.weaponry.handler.client;

import net.jaams.weaponry.init.ModParticles;
import net.jaams.weaponry.particle.BigWaveParticle;
import net.jaams.weaponry.particle.CustomBuffParticle;
import net.jaams.weaponry.particle.CustomCritParticle;
import net.jaams.weaponry.particle.CustomDebuffParticle;
import net.jaams.weaponry.particle.CustomExplosionParticle;
import net.jaams.weaponry.particle.CustomFlashParticle;
import net.jaams.weaponry.particle.CustomGlintParticle;
import net.jaams.weaponry.particle.CustomHeartParticle;
import net.jaams.weaponry.particle.CustomHitParticle;
import net.jaams.weaponry.particle.CustomSkullParticle;
import net.jaams.weaponry.particle.CustomSmokeParticle;
import net.jaams.weaponry.particle.CustomSoulParticle;
import net.jaams.weaponry.particle.CustomSweepParticle;
import net.jaams.weaponry.particle.CustomVerticalSweepParticle;
import net.jaams.weaponry.particle.EmptyHeartParticle;
import net.jaams.weaponry.particle.EnhancedHitParticle;
import net.jaams.weaponry.particle.GunSparkParticle;
import net.jaams.weaponry.particle.MiniSweepParticle;
import net.jaams.weaponry.particle.SmallWaveParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class ParticleRegisterHandler {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CUSTOM_CRIT_PARTICLE.get(), CustomCritParticle.CustomCritParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_SWEEP_PARTICLE.get(), CustomSweepParticle.CustomSweepParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_VERTICAL_SWEEP_PARTICLE.get(), CustomVerticalSweepParticle.CustomVerticalSweepParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_EXPLOSION_PARTICLE.get(), CustomExplosionParticle.CustomExplosionParticleProvider::new);
        event.registerSpriteSet(ModParticles.ENHANCED_HIT_PARTICLE.get(), EnhancedHitParticle.EnhancedHitParticleProvider::new);
        event.registerSpriteSet(ModParticles.EMPTY_HEART_PARTICLE.get(), EmptyHeartParticle.EmptyHeartParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_SKULL_PARTICLE.get(), CustomSkullParticle.CustomSkullParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_FLASH_PARTICLE.get(), CustomFlashParticle.CustomFlashParticleProvider::new);
        event.registerSpriteSet(ModParticles.SMALL_WAVE_PARTICLE.get(), SmallWaveParticle.SmallWaveParticleProvider::new);
        event.registerSpriteSet(ModParticles.BIG_WAVE_PARTICLE.get(), BigWaveParticle.BigWaveParticleProvider::new);
        event.registerSpriteSet(ModParticles.GUN_SPARK_PARTICLE.get(), GunSparkParticle.GunSparkParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_HIT_PARTICLE.get(), CustomHitParticle.CustomHitParticleProvider::new);
        event.registerSpriteSet(ModParticles.MINI_SWEEP_PARTICLE.get(), MiniSweepParticle.MiniSweepParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_SMOKE_PARTICLE.get(), CustomSmokeParticle.CustomSmokeParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_SOUL_PARTICLE.get(), CustomSoulParticle.CustomSoulParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_HEART_PARTICLE.get(), CustomHeartParticle.CustomHeartParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_GLINT_PARTICLE.get(), CustomGlintParticle.CustomGlintParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_BUFF_PARTICLE.get(), CustomBuffParticle.CustomBuffParticleProvider::new);
        event.registerSpriteSet(ModParticles.CUSTOM_DEBUFF_PARTICLE.get(), CustomDebuffParticle.CustomDebuffParticleProvider::new);
    }
}
