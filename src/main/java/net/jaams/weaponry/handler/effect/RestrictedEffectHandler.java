package net.jaams.weaponry.handler.effect;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class RestrictedEffectHandler {

    private static boolean isKnockedOutEnabled() {
        return EffectsConfig.KNOCKED_OUT.get();
    }

    private static boolean isIncapableEnabled() {
        return EffectsConfig.INCAPABLE.get();
    }

    private static boolean restrictKnockedOutItemUse() {
        return isKnockedOutEnabled() && EffectsConfig.KNOCKED_OUT_RESTRICT_ITEM_USE.get();
    }

    private static boolean restrictKnockedOutBlockInteraction() {
        return isKnockedOutEnabled() && EffectsConfig.KNOCKED_OUT_RESTRICT_BLOCK_INTERACTION.get();
    }

    private static boolean restrictKnockedOutJump() {
        return isKnockedOutEnabled() && EffectsConfig.KNOCKED_OUT_RESTRICT_JUMP.get();
    }

    private static boolean restrictIncapableItemUse() {
        return isIncapableEnabled() && EffectsConfig.INCAPABLE_RESTRICT_ITEM_USE.get();
    }

    private static boolean restrictIncapableBlockInteraction() {
        return isIncapableEnabled() && EffectsConfig.INCAPABLE_RESTRICT_BLOCK_INTERACTION.get();
    }

    @SubscribeEvent
    public static void onRestrictedItemUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        boolean cancel = false;
        if (restrictKnockedOutItemUse() && player.hasEffect(ModMobEffects.KNOCKED_OUT)) {
            cancel = true;
        }
        if (restrictIncapableItemUse() && player.hasEffect(ModMobEffects.INCAPABLE)) {
            cancel = true;
        }
        if (cancel) {
            player.getCooldowns().addCooldown(event.getItemStack().getItem(), 20);
            event.setCanceled(true);
            ModUtils.playSound(player, "jaams_weaponry:incapable_using");
        }
    }

    @SubscribeEvent
    public static void onRestrictedPlayerTick(PlayerTickEvent.Post event) {
                Player player = event.getEntity();
        if (restrictKnockedOutItemUse() || restrictIncapableItemUse()) {
            if (ModUtils.hasRestrictedEffect(player)) {
                player.stopUsingItem();
            }
        }
    }

    @SubscribeEvent
    public static void onKnockedOutRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (restrictKnockedOutItemUse() && event.getEntity().hasEffect(ModMobEffects.KNOCKED_OUT)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKnockedOutJump(LivingEvent.LivingJumpEvent event) {
        if (!restrictKnockedOutJump()) return;
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModMobEffects.KNOCKED_OUT)) {
            entity.setOnGround(true);
            entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x, -0.1, entity.getDeltaMovement().z));
        }
    }

    @SubscribeEvent
    public static void onRestrictedBreakBlock(BlockEvent.BreakEvent event) {
        if (restrictKnockedOutBlockInteraction() || restrictIncapableBlockInteraction()) {
            if (ModUtils.hasRestrictedEffect(event.getPlayer())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRestrictedLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (restrictKnockedOutBlockInteraction() || restrictIncapableBlockInteraction()) {
            if (ModUtils.hasRestrictedEffect(event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRestrictedPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (restrictKnockedOutBlockInteraction() || restrictIncapableBlockInteraction()) {
            if (ModUtils.hasRestrictedEffect(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRestrictedRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (restrictKnockedOutBlockInteraction() || restrictIncapableBlockInteraction()) {
            if (ModUtils.hasRestrictedEffect(event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRestrictedEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (restrictKnockedOutBlockInteraction() || restrictIncapableBlockInteraction()) {
            if (ModUtils.hasRestrictedEffect(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKnockedOutAttack(LivingIncomingDamageEvent event) {
        if (!isKnockedOutEnabled()) return;
        if (!EffectsConfig.KNOCKED_OUT_RESTRICT_ATTACKING.get()) return;
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker && attacker.hasEffect(ModMobEffects.KNOCKED_OUT)) {
            event.setCanceled(true);
        }
    }
}
