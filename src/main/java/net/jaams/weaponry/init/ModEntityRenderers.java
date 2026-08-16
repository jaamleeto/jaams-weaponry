package net.jaams.weaponry.init;

import net.jaams.weaponry.client.renderer.AxeProjectileRenderer;
import net.jaams.weaponry.client.renderer.BroomProjectileRenderer;
import net.jaams.weaponry.client.renderer.BulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.CleaverProjectileRenderer;
import net.jaams.weaponry.client.renderer.DynamiteProjectileRenderer;
import net.jaams.weaponry.client.renderer.EchoBulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.FireBulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.GiantShurikenProjectileRenderer;
import net.jaams.weaponry.client.renderer.GlowingBulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.HeavyBulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.HuntersBoomerangProjectileRenderer;
import net.jaams.weaponry.client.renderer.ItemProjectileRenderer;
import net.jaams.weaponry.client.renderer.KunaiProjectileRenderer;
import net.jaams.weaponry.client.renderer.ProngedKunaiProjectileRenderer;
import net.jaams.weaponry.client.renderer.RingProjectileRenderer;
import net.jaams.weaponry.client.renderer.RoyalAxeProjectileRenderer;
import net.jaams.weaponry.client.renderer.RoyalSpearProjectileRenderer;
import net.jaams.weaponry.client.renderer.SharpBulletProjectileRenderer;
import net.jaams.weaponry.client.renderer.SharpStoneProjectileRenderer;
import net.jaams.weaponry.client.renderer.ShurikenProjectileRenderer;
import net.jaams.weaponry.client.renderer.SpearProjectileRenderer;
import net.jaams.weaponry.client.renderer.StakeProjectileRenderer;
import net.jaams.weaponry.client.renderer.TridentProjectileRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FIRE_BULLET_PROJECTILE.get(), FireBulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.HEAVY_BULLET_PROJECTILE.get(), HeavyBulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.GLOWING_BULLET_PROJECTILE.get(), GlowingBulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ECHO_BULLET_PROJECTILE.get(), EchoBulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHARP_BULLET_PROJECTILE.get(), SharpBulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.BULLET_PROJECTILE.get(), BulletProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.DYNAMITE_PROJECTILE.get(), DynamiteProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.HUNTERS_BOOMERANG_PROJECTILE.get(), HuntersBoomerangProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHARP_STONE_PROJECTILE.get(), SharpStoneProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.STAKE_PROJECTILE.get(), StakeProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHURIKEN_PROJECTILE.get(), ShurikenProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.KUNAI_PROJECTILE.get(), KunaiProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.GIANT_SHURIKEN_PROJECTILE.get(), GiantShurikenProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.PRONGED_KUNAI_PROJECTILE.get(), ProngedKunaiProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SPEAR_PROJECTILE.get(), SpearProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ROYAL_SPEAR_PROJECTILE.get(), RoyalSpearProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.CLEAVER_PROJECTILE.get(), CleaverProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.BROOM_PROJECTILE.get(), BroomProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.RING_PROJECTILE.get(), RingProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.TRIDENT_PROJECTILE.get(), TridentProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.AXE_PROJECTILE.get(), AxeProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ROYAL_AXE_PROJECTILE.get(), RoyalAxeProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ITEM_PROJECTILE.get(), ItemProjectileRenderer::new);
    }
}
