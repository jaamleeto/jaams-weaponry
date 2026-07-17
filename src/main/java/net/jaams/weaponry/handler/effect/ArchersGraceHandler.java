package net.jaams.weaponry.handler.effect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArchersGraceHandler {

    private static final Map<LivingEntity, ArchersGraceData> archersGraceEntities = new ConcurrentHashMap<>();
    private static final String ARCHERS_GRACE_NBT_KEY = "ArchersGrace";
    private static final int CLEANUP_DELAY = 5;

    private static class ArchersGraceData {

        long lastUseTick;
        ItemStack usedItem;
        int useDuration;

        ArchersGraceData(long lastUseTick, ItemStack usedItem, int useDuration) {
            this.lastUseTick = lastUseTick;
            this.usedItem = usedItem.copy();
            this.useDuration = useDuration;
        }
    }

    private static boolean isArchersGraceEnabled() {
        return EffectsConfig.ARCHERS_GRACE.get();
    }

    private static float getArchersGraceDamageMultiplier() {
        return EffectsConfig.ARCHERS_GRACE_DAMAGE_MULTIPLIER.get().floatValue();
    }

    private static int getArchersGraceMinUseTicks() {
        return EffectsConfig.ARCHERS_GRACE_MIN_USE_TICKS.get();
    }

    private static float getArchersGraceShootPitch(int level) {
        return 0.9F + (0.08F * level);
    }

    @SubscribeEvent
    public static void onArchersGraceAdded(MobEffectEvent.Added event) {
        if (!isArchersGraceEnabled()) return;
        LivingEntity entity = event.getEntity();
        if (event.getEffectInstance().getEffect() == ModMobEffects.ARCHERS_GRACE.get()) {
            archersGraceEntities.put(entity, new ArchersGraceData(entity.level().getGameTime(), ItemStack.EMPTY, 0));
        }
    }

    @SubscribeEvent
    public static void onArchersGraceRemoved(MobEffectEvent.Remove event) {
        if (!isArchersGraceEnabled()) return;
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null && effectInstance.getEffect() == ModMobEffects.ARCHERS_GRACE.get()) {
            archersGraceEntities.remove(entity);
        }
    }

    @SubscribeEvent
    public static void onArchersGraceRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!isArchersGraceEnabled()) return;
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        if (isArchersGraceValidItem(itemStack) && player.hasEffect(ModMobEffects.ARCHERS_GRACE.get())) {
            archersGraceEntities.put(player, new ArchersGraceData(player.level().getGameTime(), itemStack, 0));
        }
    }

    @SubscribeEvent
    public static void onArchersGraceCrossbow(PlayerInteractEvent.RightClickItem event) {
        if (!isArchersGraceEnabled()) return;
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        if (itemStack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemStack) && player.hasEffect(ModMobEffects.ARCHERS_GRACE.get())) {
            MobEffectInstance effect = player.getEffect(ModMobEffects.ARCHERS_GRACE.get());
            int level = effect != null ? effect.getAmplifier() + 1 : 1;
            float pitch = getArchersGraceShootPitch(level);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "archers_grace_shoot")), SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    @SubscribeEvent
    public static void onArchersGraceServerTick(TickEvent.ServerTickEvent event) {
        if (!isArchersGraceEnabled()) return;
        if (event.phase == TickEvent.Phase.END) {
            archersGraceEntities.forEach((entity, data) -> {
                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    archersGraceEntities.remove(entity);
                    return;
                }
                if (!entity.hasEffect(ModMobEffects.ARCHERS_GRACE.get())) {
                    long currentTick = entity.level().getGameTime();
                    if (currentTick - data.lastUseTick >= CLEANUP_DELAY) {
                        archersGraceEntities.remove(entity);
                    }
                } else if (entity.isUsingItem() && isArchersGraceValidItem(entity.getUseItem())) {
                    data.useDuration++;
                } else {
                    data.useDuration = 0;
                    data.usedItem = ItemStack.EMPTY;
                }
            });
        }
    }

    @SubscribeEvent
    public static void onArchersGraceEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!isArchersGraceEnabled()) return;
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof LivingEntity entity) {
            ArchersGraceData data = archersGraceEntities.get(entity);
            if (data != null && data.useDuration >= getArchersGraceMinUseTicks() && isArchersGraceValidItem(data.usedItem) && !entity.level().isClientSide()) {
                MobEffectInstance effect = entity.getEffect(ModMobEffects.ARCHERS_GRACE.get());
                int level = effect != null ? effect.getAmplifier() + 1 : 1;
                float pitch = getArchersGraceShootPitch(level);
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "archers_grace_shoot")), SoundSource.PLAYERS, 1.0F, pitch);
                arrow.getPersistentData().putBoolean(ARCHERS_GRACE_NBT_KEY, true);
            }
        }
    }

    @SubscribeEvent
    public static void onArchersGraceLivingHurt(LivingHurtEvent event) {
        if (!isArchersGraceEnabled()) return;
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && event.getEntity() instanceof LivingEntity) {
            if (arrow.getPersistentData().getBoolean(ARCHERS_GRACE_NBT_KEY)) {
                LivingEntity sourceEntity = (LivingEntity) arrow.getOwner();
                if (sourceEntity != null) {
                    MobEffectInstance effect = sourceEntity.getEffect(ModMobEffects.ARCHERS_GRACE.get());
                    if (effect != null) {
                        int level = effect.getAmplifier() + 1;
                        float damageMultiplier = 1.0F + (getArchersGraceDamageMultiplier() * level);
                        event.setAmount(event.getAmount() * damageMultiplier);
                    }
                }
                arrow.getPersistentData().remove(ARCHERS_GRACE_NBT_KEY);
            }
        }
    }

    @SubscribeEvent
    public static void onArchersGraceLivingDeath(LivingDeathEvent event) {
        if (!isArchersGraceEnabled()) return;
        archersGraceEntities.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onArchersGracePlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!isArchersGraceEnabled()) return;
        archersGraceEntities.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onArchersGraceEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!isArchersGraceEnabled()) return;
        if (event.getEntity() instanceof LivingEntity livingEntity && !(event.getEntity() instanceof Player)) {
            archersGraceEntities.remove(livingEntity);
        }
    }

    private static boolean isArchersGraceValidItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        Item item = itemStack.getItem();
        UseAnim anim = item.getUseAnimation(itemStack);
        return item instanceof BowItem || item instanceof CrossbowItem || anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR;
    }
}
