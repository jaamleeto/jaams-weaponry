package net.jaams.weaponry.handler.event;

import net.jaams.weaponry.handler.behavior.item.MobGunShootBehaviorHandler;
import net.jaams.weaponry.handler.behavior.item.MobQuickSwapBehaviorHandler;
import net.jaams.weaponry.handler.behavior.item.MobSlingshotShootBehaviorHandler;
import net.jaams.weaponry.handler.behavior.item.MobSmokeBombUseBehaviorHandler;
import net.jaams.weaponry.handler.behavior.item.MobThrowableBehaviorHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobBehaviorHandler {

    private static final TagKey<EntityType<?>> TAG_QUICK_SWAP =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jaams_weaponry", "behavior/quick_swap"));
    private static final TagKey<EntityType<?>> TAG_GUN_SHOOT =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jaams_weaponry", "behavior/gun_shoot"));
    private static final TagKey<EntityType<?>> TAG_THROWABLE =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jaams_weaponry", "behavior/throwable"));
    private static final TagKey<EntityType<?>> TAG_SLINGSHOT =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jaams_weaponry", "behavior/slingshot_shoot"));
    private static final TagKey<EntityType<?>> TAG_SMOKE_BOMB =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jaams_weaponry", "behavior/smoke_bomb"));

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!mob.isAlive()) return;

        long tick = mob.level().getGameTime();
        EntityType<?> type = mob.getType();

        if (type.is(TAG_QUICK_SWAP) && MobQuickSwapBehaviorHandler.tryExecute(mob, tick)) return;
        if (type.is(TAG_GUN_SHOOT) && MobGunShootBehaviorHandler.tryExecute(mob, tick)) return;
        if (type.is(TAG_THROWABLE) && MobThrowableBehaviorHandler.tryExecute(mob, tick)) return;
        if (type.is(TAG_SLINGSHOT) && MobSlingshotShootBehaviorHandler.tryExecute(mob, tick)) return;
        if (type.is(TAG_SMOKE_BOMB)) MobSmokeBombUseBehaviorHandler.tryExecute(mob, tick);
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Mob)) return;
        var uuid = event.getEntity().getUUID();
        MobQuickSwapBehaviorHandler.removeState(uuid);
        MobGunShootBehaviorHandler.removeState(uuid);
        MobThrowableBehaviorHandler.removeState(uuid);
        MobSlingshotShootBehaviorHandler.removeState(uuid);
        MobSmokeBombUseBehaviorHandler.removeState(uuid);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob)) return;
        var uuid = event.getEntity().getUUID();
        MobQuickSwapBehaviorHandler.removeState(uuid);
        MobGunShootBehaviorHandler.removeState(uuid);
        MobThrowableBehaviorHandler.removeState(uuid);
        MobSlingshotShootBehaviorHandler.removeState(uuid);
        MobSmokeBombUseBehaviorHandler.removeState(uuid);
    }
}
