package net.jaams.weaponry.handler.effect;

import net.jaams.weaponry.effect.RemovableAttributeEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class EffectModifierBridgeHandler {

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        handle(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        handle(event.getEntity(), event.getEffectInstance());
    }

    private static void handle(LivingEntity entity, MobEffectInstance instance) {
        if (entity == null || instance == null)
            return;
        if (instance.getEffect().value() instanceof RemovableAttributeEffect removable) {
            removable.onEffectRemoved(entity, instance.getAmplifier());
        }
    }
}
