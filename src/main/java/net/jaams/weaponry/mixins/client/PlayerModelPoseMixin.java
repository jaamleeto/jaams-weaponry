package net.jaams.weaponry.mixins.client;
import net.jaams.weaponry.util.ModComponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.configuration.client.AssortedClientConfig;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunShootHelper;

import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(
        value = {PlayerModel.class},
        priority = 2000
)
public abstract class PlayerModelPoseMixin {

    private static final float AIM_X_ROT = -1.4279966F; 
    private static final float WHIRL_SPEED = 0.8F;
    private static final float WHIRL_AMOUNT = 0.5F;
    private static final float WHIRL_BASE_X = -1.0F;

    @Inject(
            method = {"setupAnim"},
            at = {@At("TAIL")}
    )
    private void jaams$onSetupAnimTail(LivingEntity entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;

        @SuppressWarnings("unchecked")
        PlayerModel<?> model = (PlayerModel<?>) (Object) this;

        
        
        
        this.jaams$applyWhirlingPose(player, model);
        this.jaams$applyGunAimingPose(player, model);

        
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
    }

    

    private void jaams$applyWhirlingPose(Player player, PlayerModel<?> model) {
        if (!AssortedClientConfig.WHIRLING_STRIKE_ARM_ANIMATION.get())
            return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean isMainWhirling = ModTraits.isWhirlingStrikeItem(mainHand);
        boolean isOffWhirling = ModTraits.isWhirlingStrikeItem(offHand);

        if (!isMainWhirling && !isOffWhirling)
            return;
        if (player.getUseItemRemainingTicks() <= 0)
            return;
        if (!player.isUsingItem())
            return;

        
        if (player.getCooldowns().isOnCooldown(mainHand.getItem())
                || player.getCooldowns().isOnCooldown(offHand.getItem()))
            return;

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float smoothTick = (float) player.tickCount + partialTick;
        boolean bothHands = isMainWhirling && isOffWhirling;

        if (bothHands) {
            model.rightArm.xRot = WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED);
            model.rightArm.yRot = 0.1F;
            model.rightArm.zRot = 0.0F;
            model.leftArm.xRot = WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.cos(smoothTick * WHIRL_SPEED);
            model.leftArm.yRot = -0.1F;
            model.leftArm.zRot = 0.0F;
        } else {
            InteractionHand usedHand = player.getUsedItemHand();
            boolean whirlingInUsedHand = usedHand == InteractionHand.MAIN_HAND ? isMainWhirling : isOffWhirling;
            if (!whirlingInUsedHand)
                return;

            if (usedHand == InteractionHand.MAIN_HAND) {
                model.rightArm.xRot = WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED);
                model.rightArm.yRot = 0.1F;
                model.rightArm.zRot = 0.0F;
            } else {
                model.leftArm.xRot = WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED);
                model.leftArm.yRot = -0.1F;
                model.leftArm.zRot = 0.0F;
            }
        }
    }

    

    private void jaams$applyGunAimingPose(Player player, PlayerModel<?> model) {
        if (!GunSystemClientConfig.GUN_AIMING_ARM_ANIMATION.get())
            return;
        if (player.isUsingItem())
            return;

        this.jaams$applyHandAimingPose(player, model, player.getMainHandItem(),
                InteractionHand.MAIN_HAND, player.getMainArm());
        this.jaams$applyHandAimingPose(player, model, player.getOffhandItem(),
                InteractionHand.OFF_HAND, player.getMainArm().getOpposite());
    }

    private void jaams$applyHandAimingPose(Player player, PlayerModel<?> model,
            ItemStack stack, InteractionHand hand, HumanoidArm arm) {
        if (stack.isEmpty())
            return;

        ModGuns.GunType gunType = ModGuns.getGunType(stack);
        if (gunType == null)
            return;

        if (player.isUsingItem())
            return;

        if (stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack))
            return;

        boolean isMainHand = hand == InteractionHand.MAIN_HAND;

        
        String nbtPose = jaams$getPoseFromNBT(stack);
        String poseType = null;
        if (nbtPose != null) {
            poseType = nbtPose.toUpperCase();
        }

        if (poseType != null) {
            if ("NONE".equals(poseType))
                return;
            
        } else {
            
            if (!jaams$shouldHaveAimingPose(gunType))
                return;
        }

        if (jaams$canDisplayPose(player, stack, gunType, isMainHand, player.isCreative())) {
            ModelPart armPart = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
            float headYRotOffset = isMainHand ? -0.1F : 0.1F;
            armPart.xRot = AIM_X_ROT + model.head.xRot;
            armPart.yRot = headYRotOffset + model.head.yRot;
        }
    }

    

    private static boolean jaams$shouldHaveAimingPose(ModGuns.GunType type) {
        boolean allowedByConfig = GunSystemClientConfig.GUN_DEFAULT_AIMING_POSE.get();
        return allowedByConfig && (type == ModGuns.GunType.GUN
                || type == ModGuns.GunType.PISTOL
                || type == ModGuns.GunType.SCATTERGUN);
    }

    private static boolean jaams$canDisplayPose(Player player, ItemStack stack,
            ModGuns.GunType gunType, boolean isMainHand, boolean isCreative) {
        ItemStack handStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
        if (!ItemStack.matches(stack, handStack))
            return false;
        if (player.getCooldowns().isOnCooldown(handStack.getItem()))
            return false;
        if (isCreative)
            return true;

        int ammoConsumption = jaams$getFinalAmmoConsumption(stack);
        GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
        boolean useGunAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromGun",
                GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get,
                gunData != null ? gunData.ammo_from_gun : null);
        boolean useHandAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromHand",
                GunSystemCommonConfig.GUN_AMMO_FROM_HAND::get,
                gunData != null ? gunData.ammo_from_hand : null);
        boolean useInventoryAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromPlayerInventory",
                GunSystemCommonConfig.GUN_AMMO_FROM_PLAYER_INVENTORY::get,
                gunData != null ? gunData.ammo_from_player_inventory : null);
        GunShootHelper.SourceResult source = GunShootHelper.getPreferredSourceWithPriority(
                player, stack, useGunAmmo, useHandAmmo, useInventoryAmmo,
                ammoConsumption, gunType);
        return source.hasEnough();
    }

    private static String jaams$getPoseFromNBT(ItemStack stack) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("GunPose", 8)) {
            return ModComponents.get(stack).getString("GunPose");
        }
        return null;
    }

    private static int jaams$getFinalAmmoConsumption(ItemStack gunStack) {
        int nbtValue = ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption", () -> 0);
        if (nbtValue > 0)
            return nbtValue;
        GunItemData.ShootEntry shootData = GunItemData.getShootData(gunStack);
        if (shootData != null && shootData.ammo_consumption != null)
            return shootData.ammo_consumption;
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        if (type == null)
            type = ModGuns.GunType.GUN;
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_AMMO_CONSUMPTION.get();
            default -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
        };
    }
}
