package net.jaams.weaponry.util;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;
import net.jaams.weaponry.item.NunchakuItem;
import net.jaams.weaponry.item.RoyalCrossbowItem;
import net.jaams.weaponry.item.StakeCrossbowItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModItemProperties {

    private enum WeaponType {
        KATANA,
        BOOMERANG
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            makeBow(ModItems.HUNTERS_BOW.get(), "pull", "pulling", 25.0f);
            makeBow(ModItems.COMPOUND_BOW.get(), "pull", "pulling", 40.0f);
            makeBow(ModItems.FLAT_BOW.get(), "pull", "pulling", 35.0f);
            makeBow(ModItems.SHORT_BOW.get(), "pull", "pulling", 20.0f);
            makeBow(ModItems.STONE_SLINGSHOT.get(), "pull", "pulling", 20.0f);
            makeBow(ModItems.WOODEN_SLINGSHOT.get(), "pull", "pulling", 20.0f);
            makeBow(ModItems.ROYAL_BOW.get(), "pull", "pulling", 25.0f);
            makeHuntersCrossbow(ModItems.HUNTERS_CROSSBOW.get(), "pull", "pulling", "charged", "firework");
            makeStakeCrossbow(ModItems.STAKE_CROSSBOW.get(), "pull", "pulling", "charged");
            makeGreatCrossbow(ModItems.GREAT_CROSSBOW.get(), "pull", "pulling", "charged", "firework");
            makeRoyalCrossbow(ModItems.ROYAL_CROSSBOW.get(), "pull", "pulling", "charged", "firework");
            makeNunchaku(ModItems.NUNCHAKU.get(), "nunchaku_pulling");
            makeWeaponSkin(ModItems.WOODEN_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            makeWeaponSkin(ModItems.STONE_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            makeWeaponSkin(ModItems.IRON_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            makeWeaponSkin(ModItems.GOLDEN_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            makeWeaponSkin(ModItems.DIAMOND_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            makeWeaponSkin(ModItems.NETHERITE_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            if (ModList.get().isLoaded("cavesanddepths") || ModList.get().isLoaded("oooh_pinky") || ModList.get().isLoaded("justrosegold")) {
                makeWeaponSkin(ModItems.ROSEGOLD_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            }
            if (ModList.get().isLoaded("jaams_shinerite")) {
                makeWeaponSkin(ModItems.SHINERITE_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            }
            if (ModList.get().isLoaded("majruszsdifficulty")) {
                makeWeaponSkin(ModItems.ENDERIUM_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            }
            if (ModList.get().isLoaded("oreganized")) {
                makeWeaponSkin(ModItems.ELECTRUM_KATANA.get(), "weapon_skin", WeaponType.KATANA);
            }
            makeWeaponSkin(ModItems.HUNTERS_BOOMERANG.get(), "weapon_skin", WeaponType.BOOMERANG);
        });
    }

    private static void makeNunchaku(Item item, String propertyName) {
        ItemProperties.register(item, ResourceLocation.parse("jaams_weaponry:" + propertyName), (stack, clientLevel, living, k) -> {
            CompoundTag nbt = ModComponents.getOrCreate(stack);
            String skin = nbt.getString("WeaponSkin").toLowerCase();
            String displayName = stack.getDisplayName().getString().toLowerCase();
            if (living == null) {
                return getSkinValue(skin, displayName, 2.0F, 4.0F, 0.0F);
            }
            if (living instanceof Player player) {
                return handlePlayerNunchaku(player, stack, skin, displayName);
            } else if (living instanceof Mob mob) {
                return handleMobNunchaku(mob, stack, skin, displayName);
            }
            return getSkinValue(skin, displayName, 2.0F, 4.0F, 0.0F);
        });
    }

    private static float handlePlayerNunchaku(Player player, ItemStack stack, String skin, String displayName) {
        if (ModUtils.hasRestrictedEffect(player)) {
            return getSkinValue(skin, displayName, 2.0F, 4.0F, 0.0F);
        }
        boolean isHoldingNunchaku = player.getMainHandItem() == stack || player.getOffhandItem() == stack;
        boolean isSwinging =
            player.swingTime > 0 &&
            player.swingingArm != null &&
            ((player.swingingArm == InteractionHand.MAIN_HAND && player.getMainHandItem() == stack) || (player.swingingArm == InteractionHand.OFF_HAND && player.getOffhandItem() == stack)) &&
            player.getAttackStrengthScale(0.0F) >= 0.5F &&
            player
                .level()
                .clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale(6.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
                .getType() != HitResult.Type.BLOCK;
        boolean isRidingActive = false;
        if (player.getVehicle() instanceof LivingEntity vehicle) {
            boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
            boolean isVehicleSprinting = vehicle.isSprinting();
            isRidingActive = isHoldingNunchaku && (isVehicleMoving || isVehicleSprinting);
        }
        boolean isActive =
            isHoldingNunchaku &&
            !player.getCooldowns().isOnCooldown(stack.getItem()) &&
            (!player.isUsingItem() || (player.getUseItem() == stack || player.getUseItem().getItem() instanceof NunchakuItem)) &&
            ((player.isUsingItem() && (player.getUseItem() == stack || player.getUseItem().getItem() instanceof NunchakuItem)) || player.isSprinting() || isSwinging || isRidingActive);
        return getSkinValue(skin, displayName, isActive ? 3.0F : 2.0F, isActive ? 5.0F : 4.0F, isActive ? 1.0F : 0.0F);
    }

    private static float handleMobNunchaku(Mob mob, ItemStack stack, String skin, String displayName) {
        if (mob.isNoAi() || ModUtils.hasRestrictedEffect(mob)) {
            return getSkinValue(skin, displayName, 2.0F, 4.0F, 0.0F);
        }
        boolean isHoldingNunchaku = mob.getMainHandItem() == stack || mob.getOffhandItem() == stack;
        boolean isSwinging = mob.swingTime > 0 && mob.swingingArm != null && ((mob.swingingArm == InteractionHand.MAIN_HAND && mob.getMainHandItem() == stack) || (mob.swingingArm == InteractionHand.OFF_HAND && mob.getOffhandItem() == stack));
        boolean isMoving = mob.getDeltaMovement().length() > 0.06 && isHoldingNunchaku;
        boolean isRidingActive = false;
        if (mob.getVehicle() instanceof LivingEntity vehicle) {
            boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
            boolean isVehicleSprinting = vehicle.isSprinting();
            isRidingActive = isHoldingNunchaku && (isVehicleMoving || isVehicleSprinting);
        }
        if (isMoving || isRidingActive) {
            return getSkinValue(skin, displayName, 3.0F, 5.0F, 1.0F);
        }
        boolean isActive = isHoldingNunchaku && ((mob.isUsingItem() && (mob.getUseItem() == stack || mob.getUseItem().getItem() instanceof NunchakuItem)) || mob.isSprinting() || isSwinging);
        return getSkinValue(skin, displayName, isActive ? 3.0F : 2.0F, isActive ? 5.0F : 4.0F, isActive ? 1.0F : 0.0F);
    }

    private static float getSkinValue(String skin, String displayName, float rockLeeValue, float michaelangeloValue, float defaultValue) {
        if (skin.equals("rock_lee") || displayName.equalsIgnoreCase("[rock lee]")) {
            return rockLeeValue;
        } else if (skin.equals("michaelangelo") || displayName.equalsIgnoreCase("[michaelangelo]")) {
            return michaelangeloValue;
        }
        return defaultValue;
    }

    private static void makeWeaponSkin(Item item, String propertyName, WeaponType weaponType) {
        ItemProperties.register(item, ResourceLocation.parse("jaams_weaponry:" + propertyName), (stack, clientLevel, living, k) -> {
            CompoundTag nbt = ModComponents.getOrCreate(stack);
            String skin = nbt.getString("WeaponSkin").toLowerCase();
            switch (weaponType) {
                case KATANA:
                    if (skin.equals("rengoku")) return 1.0F;
                    if (skin.equals("mitsuri")) return 2.0F;
                    if (skin.equals("zenitsu")) return 3.0F;
                    if (skin.equals("inosuke")) return 4.0F;
                    String displayName = stack.getDisplayName().getString().toLowerCase();
                    if (displayName.equalsIgnoreCase("[rengoku]")) return 1.0F;
                    if (displayName.equalsIgnoreCase("[mitsuri]")) return 2.0F;
                    if (displayName.equalsIgnoreCase("[zenitsu]")) return 3.0F;
                    if (displayName.equalsIgnoreCase("[inosuke]")) return 4.0F;
                    return 0.0F;
                case BOOMERANG:
                    if (skin.equals("sokka")) return 1.0F;
                    if (stack.getDisplayName().getString().equalsIgnoreCase("[sokka]")) return 1.0F;
                    return 0.0F;
                default:
                    return 0.0F;
            }
        });
    }

    private static void makeBow(Item item, String propertyName, String propertyPullingName, float maxPull) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (itemStack, clientWorld, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0f;
            } else {
                return livingEntity.getUseItem() != itemStack ? 0.0f : (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / maxPull;
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName), (itemStack, clientWorld, livingEntity, i) -> {
            return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f;
        });
    }

    private static void makeHuntersCrossbow(Item item, String propertyName, String propertyPullingName, String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return HuntersCrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / (float) HuntersCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName), (stack, clientLevel, living, k) -> living != null && living.isUsingItem() && living.getUseItem() == stack && !HuntersCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName), (stack, clientLevel, living, k) -> living != null && HuntersCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName), (stack, clientLevel, living, k) ->
            living != null && HuntersCrossbowItem.isCharged(stack) && stack.getOrDefault(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES, net.minecraft.world.item.component.ChargedProjectiles.EMPTY).contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
        );
    }

    private static void makeStakeCrossbow(Item item, String propertyName, String propertyPullingName, String propertyChargedName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return StakeCrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / (float) StakeCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName), (stack, clientLevel, living, k) -> living != null && living.isUsingItem() && living.getUseItem() == stack && !StakeCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName), (stack, clientLevel, living, k) -> living != null && StakeCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
    }

    private static void makeGreatCrossbow(Item item, String propertyName, String propertyPullingName, String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return GreatCrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / (float) GreatCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName), (stack, clientLevel, living, k) -> living != null && living.isUsingItem() && living.getUseItem() == stack && !GreatCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName), (stack, clientLevel, living, k) -> living != null && GreatCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName), (stack, clientLevel, living, k) ->
            living != null && GreatCrossbowItem.isCharged(stack) && stack.getOrDefault(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES, net.minecraft.world.item.component.ChargedProjectiles.EMPTY).contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
        );
    }

    private static void makeRoyalCrossbow(Item item, String propertyName, String propertyPullingName, String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return RoyalCrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / (float) RoyalCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName), (stack, clientLevel, living, k) -> living != null && living.isUsingItem() && living.getUseItem() == stack && !RoyalCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName), (stack, clientLevel, living, k) -> living != null && RoyalCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName), (stack, clientLevel, living, k) ->
            living != null && RoyalCrossbowItem.isCharged(stack) && stack.getOrDefault(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES, net.minecraft.world.item.component.ChargedProjectiles.EMPTY).contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
        );
    }
}
