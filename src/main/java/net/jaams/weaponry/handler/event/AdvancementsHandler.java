package net.jaams.weaponry.handler.event;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import net.jaams.weaponry.util.ModTags;

@Mod.EventBusSubscriber
public class AdvancementsHandler {
    @SubscribeEvent
    public static void onItemAdvancementCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getCrafting();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (itemId != null && itemId.getNamespace().equals("jaams_weaponry")) {
                grantAdvancement(player, "weaponry_ad");
            }
        }
    }

    
    private static final String ONE_HIT_GREATSWORD_TAG = "jaams_one_hit_greatsword";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        
        if (!(event.getSource().getEntity() instanceof ServerPlayer))
            return;
        ServerPlayer player = (ServerPlayer) event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty() || !weapon.is(ModTags.GREATSWORDS))
            return;
        
        float damage = event.getAmount();
        if (damage < target.getHealth())
            return;
        
        if (target instanceof Mob mob && mob.getTarget() == player) {
            
            target.getPersistentData().putBoolean(ONE_HIT_GREATSWORD_TAG, true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        CompoundTag targetData = target.getPersistentData();

        
        
        if (targetData.getBoolean(ONE_HIT_GREATSWORD_TAG)) {
            targetData.remove(ONE_HIT_GREATSWORD_TAG);
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
                grantAdvancement(player, "clavar_la_espada");
            }
        }

        
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            ItemStack weapon = player.getMainHandItem();
            if (!weapon.isEmpty() && weapon.is(ModTags.SPEARS) && player.getAttackStrengthScale(0.5F) >= 0.9F) {
                grantAdvancement(player, "spear_of_justice");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAdvancementTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            checkWeaponryAdvancement(player);
            checkHunterAdvancement(player);
        }
    }

    private static void checkWeaponryAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("jaams_weaponry", "weaponry_ad"));
        if (advancement != null && !player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                    if (itemId != null && itemId.getNamespace().equals("jaams_weaponry")) {
                        grantAdvancement(player, "weaponry_ad");
                        break;
                    }
                }
            }
        }
    }

    private static void checkHunterAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("jaams_weaponry", "the_hunter"));
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        boolean hasCrossbow = false;
        boolean hasBow = false;
        boolean hasBoomerang = false;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()) {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (itemId != null && itemId.getNamespace().equals("jaams_weaponry")) {
                    String itemPath = itemId.getPath();
                    if (itemPath.equals("hunters_crossbow"))
                        hasCrossbow = true;
                    else if (itemPath.equals("hunters_bow"))
                        hasBow = true;
                    else if (itemPath.equals("hunters_boomerang"))
                        hasBoomerang = true;
                }
            }
        }
        if (hasCrossbow && hasBow && hasBoomerang) {
            grantAdvancement(player, "the_hunter");
        }
    }

    public static boolean hasAdvancement(ServerPlayer player, String advancementId) {
        ResourceLocation id = new ResourceLocation("jaams_weaponry", advancementId);
        Advancement advancement = player.getServer().getAdvancements().getAdvancement(id);
        if (advancement != null) {
            return player.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return false;
    }

    public static void grantAdvancement(ServerPlayer player, String advancementId) {
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("jaams_weaponry", advancementId));
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    public static void incrementCounterAndCheckAdvancement(ServerPlayer player, String counterKey, String advancementId,
            int threshold) {
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("jaams_weaponry", advancementId));
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        CompoundTag persistentData = player.getPersistentData();
        ResourceLocation key = new ResourceLocation("jaams_weaponry", counterKey);
        int counter = persistentData.getInt(key.toString());
        counter++;
        persistentData.putInt(key.toString(), counter);
        if (counter >= threshold) {
            grantAdvancement(player, advancementId);
            persistentData.putInt(key.toString(), 0);
        }
    }

    public static void incrementEntityCounterAndCheckAdvancement(ServerPlayer player, LivingEntity entity,
            String counterKey, String advancementId, int threshold) {
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("jaams_weaponry", advancementId));
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        CompoundTag entityData = entity.getPersistentData();
        ResourceLocation key = new ResourceLocation("jaams_weaponry", counterKey);
        int counter = entityData.getInt(key.toString());
        counter++;
        entityData.putInt(key.toString(), counter);
        if (counter >= threshold) {
            grantAdvancement(player, advancementId);
            entityData.putInt(key.toString(), 0);
        }
    }
}
