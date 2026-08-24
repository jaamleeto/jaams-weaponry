package net.jaams.weaponry.util;

import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;
import net.jaams.weaponry.item.RoyalCrossbowItem;
import net.jaams.weaponry.item.StakeCrossbowItem;

import net.minecraft.client.renderer.item.ItemProperties;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModItemProperties {

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
            var tagManager = ForgeRegistries.ITEMS.tags();
            if (tagManager != null) {
                var nunchakuTag = tagManager.getTag(ModTags.NUNCHAKUS);
                for (Item nunchakuItem : nunchakuTag) {
                    if (nunchakuItem != ModItems.NUNCHAKU.get()) {
                        makeNunchaku(nunchakuItem, "nunchaku_pulling");
                    }
                }
            }
        });
    }

    private static void makeNunchaku(Item item, String propertyName) {
        ItemProperties.register(item, ResourceLocation.parse("jaams_weaponry:" + propertyName),
                (stack, clientLevel, living, k) -> {
                    if (living == null) {
                        return 0.0F;
                    }
                    if (living instanceof Player player) {
                        return handlePlayerNunchaku(player, stack);
                    } else if (living instanceof Mob mob) {
                        return handleMobNunchaku(mob, stack);
                    }
                    return 0.0F;
                });
    }

    private static float handlePlayerNunchaku(Player player, ItemStack stack) {
        if (ModUtils.hasRestrictedEffect(player)) {
            return 0.0F;
        }
        boolean isHoldingNunchaku = player.getMainHandItem() == stack || player.getOffhandItem() == stack;
        boolean isSwinging = player.swingTime > 0 &&
                player.swingingArm != null &&
                ((player.swingingArm == InteractionHand.MAIN_HAND && player.getMainHandItem() == stack)
                        || (player.swingingArm == InteractionHand.OFF_HAND && player.getOffhandItem() == stack))
                &&
                player.getAttackStrengthScale(0.0F) >= 0.5F &&
                player
                        .level()
                        .clip(new ClipContext(player.getEyePosition(1.0F),
                                player.getEyePosition(1.0F).add(player.getLookAngle().scale(6.5)),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
                        .getType() != HitResult.Type.BLOCK;
        boolean isRidingActive = false;
        if (player.getVehicle() instanceof LivingEntity vehicle) {
            boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
            boolean isVehicleSprinting = vehicle.isSprinting();
            isRidingActive = isHoldingNunchaku && (isVehicleMoving || isVehicleSprinting);
        }
        boolean isUsingWhirlingStrike = player.isUsingItem() && ModTraits.isWhirlingStrikeItem(player.getUseItem());
        boolean isActiveItem = player.getUseItem() == stack || isNunchakuTagItem(player.getUseItem().getItem())
                || isUsingWhirlingStrike;
        boolean isActive = isHoldingNunchaku &&
                !player.getCooldowns().isOnCooldown(stack.getItem()) &&
                (!player.isUsingItem() || isActiveItem) &&
                ((player.isUsingItem() && isActiveItem) || player.isSprinting() || isSwinging || isRidingActive);
        return isActive ? 1.0F : 0.0F;
    }

    private static float handleMobNunchaku(Mob mob, ItemStack stack) {
        if (mob.isNoAi() || ModUtils.hasRestrictedEffect(mob)) {
            return 0.0F;
        }
        boolean isHoldingNunchaku = mob.getMainHandItem() == stack || mob.getOffhandItem() == stack;
        boolean isSwinging = mob.swingTime > 0 && mob.swingingArm != null
                && ((mob.swingingArm == InteractionHand.MAIN_HAND && mob.getMainHandItem() == stack)
                        || (mob.swingingArm == InteractionHand.OFF_HAND && mob.getOffhandItem() == stack));
        boolean isMoving = mob.getDeltaMovement().length() > 0.06 && isHoldingNunchaku;
        boolean isRidingActive = false;
        if (mob.getVehicle() instanceof LivingEntity vehicle) {
            boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
            boolean isVehicleSprinting = vehicle.isSprinting();
            isRidingActive = isHoldingNunchaku && (isVehicleMoving || isVehicleSprinting);
        }
        if (isMoving || isRidingActive) {
            return 1.0F;
        }
        boolean isActive = isHoldingNunchaku && ((mob.isUsingItem()
                && (mob.getUseItem() == stack || isNunchakuTagItem(mob.getUseItem().getItem())))
                || mob.isSprinting() || isSwinging);
        return isActive ? 1.0F : 0.0F;
    }

    private static boolean isNunchakuTagItem(Item item) {
        var tagManager = ForgeRegistries.ITEMS.tags();
        return tagManager != null && tagManager.getTag(ModTags.NUNCHAKUS).contains(item);
    }

    private static void makeBow(Item item, String propertyName, String propertyPullingName, float maxPull) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (itemStack, clientWorld, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0f;
            } else {
                return livingEntity.getUseItem() != itemStack ? 0.0f
                        : (float) (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / maxPull;
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName),
                (itemStack, clientWorld, livingEntity, i) -> {
                    return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack
                            ? 1.0f
                            : 0.0f;
                });
    }

    private static void makeHuntersCrossbow(Item item, String propertyName, String propertyPullingName,
            String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return HuntersCrossbowItem.isCharged(stack) ? 0.0F
                        : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks())
                                / (float) HuntersCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName),
                (stack, clientLevel, living, k) -> living != null && living.isUsingItem()
                        && living.getUseItem() == stack && !HuntersCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName), (stack, clientLevel, living,
                k) -> living != null && HuntersCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName),
                (stack, clientLevel, living,
                        k) -> living != null && HuntersCrossbowItem.isCharged(stack)
                                && HuntersCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F
                                        : 0.0F);
    }

    private static void makeStakeCrossbow(Item item, String propertyName, String propertyPullingName,
            String propertyChargedName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return StakeCrossbowItem.isCharged(stack) ? 0.0F
                        : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks())
                                / (float) StakeCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName),
                (stack, clientLevel, living, k) -> living != null && living.isUsingItem()
                        && living.getUseItem() == stack && !StakeCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName),
                (stack, clientLevel, living, k) -> living != null && StakeCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
    }

    private static void makeGreatCrossbow(Item item, String propertyName, String propertyPullingName,
            String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return GreatCrossbowItem.isCharged(stack) ? 0.0F
                        : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks())
                                / (float) GreatCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName),
                (stack, clientLevel, living, k) -> living != null && living.isUsingItem()
                        && living.getUseItem() == stack && !GreatCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName),
                (stack, clientLevel, living, k) -> living != null && GreatCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName),
                (stack, clientLevel, living,
                        k) -> living != null && GreatCrossbowItem.isCharged(stack)
                                && GreatCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F
                                        : 0.0F);
    }

    private static void makeRoyalCrossbow(Item item, String propertyName, String propertyPullingName,
            String propertyChargedName, String propertyFireworkName) {
        ItemProperties.register(item, ResourceLocation.parse(propertyName), (stack, clientLevel, living, k) -> {
            if (living == null) {
                return 0.0F;
            } else {
                return RoyalCrossbowItem.isCharged(stack) ? 0.0F
                        : (float) (stack.getUseDuration() - living.getUseItemRemainingTicks())
                                / (float) RoyalCrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(item, ResourceLocation.parse(propertyPullingName),
                (stack, clientLevel, living, k) -> living != null && living.isUsingItem()
                        && living.getUseItem() == stack && !RoyalCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyChargedName),
                (stack, clientLevel, living, k) -> living != null && RoyalCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, ResourceLocation.parse(propertyFireworkName),
                (stack, clientLevel, living,
                        k) -> living != null && RoyalCrossbowItem.isCharged(stack)
                                && RoyalCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F
                                        : 0.0F);
    }
}
