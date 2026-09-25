package net.jaams.weaponry.mixins.client;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
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

import net.jaams.weaponry.util.ModComponents;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTags;
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
    private static final float AIM_BLEND_SPEED = 0.25F;
    private static final float AIM_BLEND_SPEED_SWING = 0.12F;
    private static final double GUN_AIM_BLEND_MAX_DT = 0.05D;
    private static final ConcurrentHashMap<UUID, float[]> GUN_AIM_BLEND = new ConcurrentHashMap<>();

    private static float getAimBlend(Player player, boolean isMainHand) {
        float[] arr = GUN_AIM_BLEND.get(player.getUUID());
        return arr != null ? (isMainHand ? arr[0] : arr[1]) : 0f;
    }

    private static void setAimBlend(Player player, boolean isMainHand, float value) {
        UUID uuid = player.getUUID();
        float[] arr = GUN_AIM_BLEND.getOrDefault(uuid, new float[]{0f, 0f, 0f, 0f});
        if (isMainHand) arr[0] = value; else arr[1] = value;
        GUN_AIM_BLEND.put(uuid, arr);
    }

    private static void removeAimBlend(Player player, boolean isMainHand) {
        UUID uuid = player.getUUID();
        float[] arr = GUN_AIM_BLEND.get(uuid);
        if (arr == null) return;
        if (isMainHand) arr[0] = 0f; else arr[1] = 0f;
        if (arr[0] <= 0.001f && arr[1] <= 0.001f) GUN_AIM_BLEND.remove(uuid);
        else GUN_AIM_BLEND.put(uuid, arr);
    }

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
        if (!GunSystemClientConfig.GUN_AIMING_POSE.get())
            return;
        if (player.isUsingItem())
            return;

        boolean animationOverride = false;

        this.jaams$updateAndApplyHandBlend(player, model,
                player.getMainHandItem(), InteractionHand.MAIN_HAND, player.getMainArm(), true, animationOverride);
        this.jaams$updateAndApplyHandBlend(player, model,
                player.getOffhandItem(), InteractionHand.OFF_HAND, player.getMainArm().getOpposite(), false, animationOverride);
    }

    private void jaams$updateAndApplyHandBlend(Player player, PlayerModel<?> model,
            ItemStack stack, InteractionHand hand, HumanoidArm arm, boolean isMainHand, boolean animationOverride) {
        boolean shouldShowAim = !animationOverride
                && !player.swinging
                && jaams$canShowAimingPose(player, stack, isMainHand);
        boolean blendEnabled = GunSystemClientConfig.GUN_AIMING_POSE_BLEND.get();
        boolean cooldownEnabled = GunSystemClientConfig.GUN_COOLDOWN_THIRD_PERSON_ANIMATION.get();
        float cooldownPercent = Mth.clamp(player.getCooldowns().getCooldownPercent(stack.getItem(), 0.0F), 0.0F, 1.0F);
        float targetBlend;
        if (!shouldShowAim) {
            targetBlend = 0.0f;
        } else if (!cooldownEnabled) {
            targetBlend = (cooldownPercent > 0.001f) ? 0.0f : 1.0f;
        } else {
            targetBlend = 1.0f - cooldownPercent;
        }
        float currentBlend = getAimBlend(player, isMainHand);

        float[] state = GUN_AIM_BLEND.computeIfAbsent(player.getUUID(), p -> new float[4]);
        double now = System.nanoTime() / 1_000_000_000.0;
        float lastTime = state[2 + (isMainHand ? 0 : 1)];
        if (lastTime <= 0.0F) {
            state[2 + (isMainHand ? 0 : 1)] = (float) now;
            lastTime = (float) now;
        }
        float dt = (float) Math.min(Math.max(now - (double) lastTime, 0.0D), GUN_AIM_BLEND_MAX_DT);
        state[2 + (isMainHand ? 0 : 1)] = (float) now;

        float blended;
        if (!blendEnabled) {
            blended = targetBlend;
        } else {
            if (Math.abs(targetBlend - currentBlend) < 0.001F) {
                blended = targetBlend;
            } else {
                float rate = (targetBlend > currentBlend)
                        ? (AIM_BLEND_SPEED * 60.0F)
                        : (player.swinging ? (AIM_BLEND_SPEED_SWING * 60.0F) : (AIM_BLEND_SPEED * 60.0F));
                float factor = 1.0F - (float) Math.exp(-dt * rate);
                blended = currentBlend + (targetBlend - currentBlend) * factor;
            }
        }
        blended = Mth.clamp(blended, 0.0F, 1.0F);

        if (blended <= 0.001f) {
            removeAimBlend(player, isMainHand);
            return;
        }
        setAimBlend(player, isMainHand, blended);

        this.jaams$applyHandAimingPose(player, model, stack, hand, arm, blended);
    }

    private static boolean jaams$canShowAimingPose(Player player, ItemStack stack, boolean isMainHand) {
        if (stack.isEmpty())
            return false;
        ModGuns.GunType gunType = ModGuns.getGunType(stack);

        Boolean nbtForce = jaams$parseAimingPoseValue(jaams$getGunPoseFromNBT(stack));
        if (nbtForce != null) {
            if (!nbtForce)
                return false;
            if (gunType == null) {
                ItemStack handStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
                return ItemStack.matches(stack, handStack);
            }
        } else {
            if (stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack))
                return false;
            GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
            Boolean dataForce = jaams$parseAimingPoseValue(gunData != null ? gunData.gun_pose : null);
            if (dataForce != null) {
                if (!dataForce)
                    return false;
            } else {
                if (!stack.is(ModTags.AIM_POSE))
                    return false;
            }
            if (gunType == null) {
                ItemStack handStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
                return ItemStack.matches(stack, handStack);
            }
        }
        return jaams$canDisplayPose(player, stack, gunType, isMainHand, player.isCreative());
    }

    private static Boolean jaams$parseAimingPoseValue(String poseValue) {
        if (poseValue == null)
            return null;
        String upper = poseValue.toUpperCase();
        if ("NONE".equals(upper))
            return Boolean.FALSE;
        if ("AIM".equals(upper))
            return Boolean.TRUE;
        return null;
    }

    private void jaams$applyHandAimingPose(Player player, PlayerModel<?> model,
            ItemStack stack, InteractionHand hand, HumanoidArm arm, float aimBlend) {
        if (stack.isEmpty())
            return;

        ModGuns.GunType gunType = ModGuns.getGunType(stack);
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;

        String gunPose = jaams$getGunPoseFromNBT(stack);
        String gunPoseType = null;
        if (gunPose != null) {
            gunPoseType = gunPose.toUpperCase();
        }

        if (gunPoseType != null) {
            if ("NONE".equals(gunPoseType))
                return;
            if (!"AIM".equals(gunPoseType))
                return;
        } else {
            GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
            Boolean dataForce = jaams$parseAimingPoseValue(gunData != null ? gunData.gun_pose : null);
            if (dataForce != null) {
                if (!dataForce)
                    return;
            } else {
                if (!stack.is(ModTags.AIM_POSE))
                    return;
            }
        }

        boolean canDisplay;
        if (gunType == null) {
            ItemStack handStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
            canDisplay = ItemStack.matches(stack, handStack);
        } else {
            canDisplay = jaams$canDisplayPose(player, stack, gunType, isMainHand, player.isCreative());
        }
        if (!canDisplay)
            return;

        ModelPart armPart = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        float headYRotOffset = isMainHand ? -0.1F : 0.1F;
        float targetXRot = AIM_X_ROT + model.head.xRot;
        float targetYRot = headYRotOffset + model.head.yRot;
        armPart.xRot = (float) Math.toRadians(Mth.rotLerp(aimBlend,
                (float) Math.toDegrees(armPart.xRot), (float) Math.toDegrees(targetXRot)));
        armPart.yRot = (float) Math.toRadians(Mth.rotLerp(aimBlend,
                (float) Math.toDegrees(armPart.yRot), (float) Math.toDegrees(targetYRot)));
    }

    private static boolean jaams$canDisplayPose(Player player, ItemStack stack,
            ModGuns.GunType gunType, boolean isMainHand, boolean isCreative) {
        ItemStack handStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
        if (!ItemStack.matches(stack, handStack))
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

    private static String jaams$getGunPoseFromNBT(ItemStack stack) {
        if (!ModComponents.has(stack))
            return null;
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("GunPose", 8)) {
            return tag.getString("GunPose");
        }
        return null;
    }

    private static int jaams$getFinalAmmoConsumption(ItemStack gunStack) {
        int nbtValue = ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption", () -> 0);
        if (nbtValue > 0)
            return nbtValue;
        GunItemData.ShootEntry shootData = GunItemData.getShootData(gunStack);
        if (shootData != null && shootData.ammo_consumption != null && shootData.ammo_consumption > 0)
            return shootData.ammo_consumption;
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        if (type == null)
            type = ModGuns.GunType.GUN;
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_AMMO_CONSUMPTION.get();
            case REVOLVER -> GunSystemCommonConfig.GUN_REVOLVER_SHOOT_AMMO_CONSUMPTION.get();
            case PEPPERBOX -> 1;
            default -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
        };
    }
}
