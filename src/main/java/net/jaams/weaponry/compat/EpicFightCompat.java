package net.jaams.weaponry.compat;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Quaternionf;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import net.jaams.weaponry.configuration.client.AssortedClientConfig;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;

/**
 * Bridge class for optional Epic Fight (epicfight) integration.
 *
 * <p>Epic Fight is only a compile-time dependency. None of the methods here can
 * fail at runtime when the mod is not installed: direct references to Epic Fight
 * classes live in {@link EpicFightImpl}, which is only loaded after
 * {@link #isEpicFightLoaded()} has been checked and every access is additionally
 * guarded by a {@link Throwable} catch as a fallback for version mismatches.
 */
public final class EpicFightCompat {

    public static final String MOD_ID = "epicfight";

    public enum Mode {
        NONE, VANILLA, EPICFIGHT
    }

    private EpicFightCompat() {
    }

    public static boolean isEpicFightLoaded() {
        return ModList.get() != null && ModList.get().isLoaded(MOD_ID);
    }

    // ---------- entity patch ----------

    public static boolean hasPatch(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.getPatch(entity).isPresent();
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight currently overrides the entity's rendering with its own model. */
    public static boolean hasCustomRender(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.hasCustomRender(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Whether the entity is drawn using Epic Fight's own model, so held-item grip transforms
     * ({@code applyEpicFightTransformations}) must be applied even when the player is not in a
     * real Epic Fight battle mode. Unlike {@link #isEpicFightMode}, this is true whenever Epic
     * Fight is rendering the entity with its model (e.g. the player out of combat but with the
     * animated model enabled), which is exactly when the custom item transforms are needed.
     */
    public static boolean hasEpicFightModel(LivingEntity entity) {
        return hasCustomRender(entity);
    }

    /** Whether Epic Fight is currently playing a blocking action animation on the entity. */
    public static boolean isInaction(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.inaction(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight is currently playing an attack animation on the entity. */
    public static boolean isAttacking(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.attacking(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight is currently animating the entity (action or attack). */
    public static boolean isAnimating(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.inaction(entity) || EpicFightImpl.attacking(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean canBasicAttack(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canBasicAttack(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canUseSkill(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canUseSkill(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canUseItem(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canUseItem(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canSwitchHoldingItem(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canSwitchHoldingItem(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean isMovementLocked(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.movementLocked(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isTurnLocked(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.turningLocked(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isKnockedDown(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.knockDown(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isHurt(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.hurt(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    // ---------- player mode ----------

    /** The player's Epic Fight mode, or {@link Mode#NONE} when not applicable. */
    public static Mode getMode(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return Mode.NONE;
        }
        try {
            return EpicFightImpl.getMode(player);
        } catch (Throwable t) {
            return Mode.NONE;
        }
    }

    public static boolean isEpicFightMode(Player player) {
        return getMode(player) == Mode.EPICFIGHT;
    }

    public static boolean isVanillaMode(Player player) {
        return getMode(player) == Mode.VANILLA;
    }

    /**
     * Whether Epic Fight's animated first-person model (config {@code ingame.first_person_model})
     * is enabled. When it is, Epic Fight takes over the first-person hand rendering and the mod
     * must let its {@code RenderHandEvent} run instead of cancelling {@code renderHandsWithItems}.
     */
    public static boolean isFirstPersonModelActive() {
        if (!isEpicFightLoaded()) {
            return false;
        }
        try {
            return EpicFightImpl.firstPersonModelActive();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Whether Epic Fight uses the vanilla player model when the player is not in combat mode
     * (config {@code ingame.vanilla_model}). When it is disabled, Epic Fight's own model renders
     * the player even out of combat, so the animation API's custom first-person body must not
     * activate (it has a visual bug against Epic Fight's model).
     */
    public static boolean isVanillaModelActive() {
        if (!isEpicFightLoaded()) {
            return false;
        }
        try {
            return EpicFightImpl.vanillaModelActive();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Whether the animation API's custom first-person body can render for the given player.
     *
     * <p>It is suppressed in two situations:
     * <ul>
     *   <li>Epic Fight is showing its own first-person model (player is in combat mode and the
     *       animated first-person config is enabled).</li>
     *   <li>Epic Fight renders its own model outside of combat because the vanilla-model config
     *       is off &mdash; in this case the animation API's first-person body would clash with
     *       the Epic Fight model.</li>
     * </ul>
     */
    public static boolean canRenderAnimatedFirstPerson(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return true;
        }
        try {
            if (!EpicFightImpl.firstPersonModelActive()) {
                return true;
            }
            return !isEpicFightMode(player) && EpicFightImpl.vanillaModelActive();
        } catch (Throwable t) {
            return true;
        }
    }

    // ---------- player stamina ----------

    public static float getStamina(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return 0.0F;
        }
        try {
            return EpicFightImpl.getStamina(player);
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    public static float getMaxStamina(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return 0.0F;
        }
        try {
            return EpicFightImpl.getMaxStamina(player);
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    public static boolean hasStamina(Player player, float amount) {
        if (!isEpicFightLoaded() || player == null) {
            return true;
        }
        try {
            return EpicFightImpl.hasStamina(player, amount);
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * All direct references to Epic Fight classes are kept here so the outer class
     * stays loadable when the mod is absent. This class is only loaded on first use,
     * which happens after {@link EpicFightCompat#isEpicFightLoaded()} is true.
     */
    private static final class EpicFightImpl {

        static Optional<LivingEntityPatch<?>> getPatch(LivingEntity entity) {
            if (entity == null) {
                return Optional.empty();
            }
            return EpicFightCapabilities.getUnparameterizedEntityPatch(entity, LivingEntityPatch.class)
                    .map(patch -> (LivingEntityPatch<?>) patch);
        }

        static Optional<PlayerPatch<?>> getPlayerPatch(Player player) {
            if (player == null) {
                return Optional.empty();
            }
            return EpicFightCapabilities.getUnparameterizedEntityPatch(player, PlayerPatch.class)
                    .map(patch -> (PlayerPatch<?>) patch);
        }

        static boolean hasCustomRender(LivingEntity entity) {
            return getPatch(entity).map(EntityPatch::overrideRender).orElse(false);
        }

        static boolean inaction(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().inaction()).orElse(false);
        }

        static boolean attacking(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().attacking()).orElse(false);
        }

        static boolean canBasicAttack(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canBasicAttack()).orElse(true);
        }

        static boolean canUseSkill(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canUseSkill()).orElse(true);
        }

        static boolean canUseItem(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canUseItem()).orElse(true);
        }

        static boolean canSwitchHoldingItem(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canSwitchHoldingItem()).orElse(true);
        }

        static boolean movementLocked(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().movementLocked()).orElse(false);
        }

        static boolean turningLocked(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().turningLocked()).orElse(false);
        }

        static boolean knockDown(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().knockDown()).orElse(false);
        }

        static boolean hurt(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().hurt()).orElse(false);
        }

        static Mode getMode(Player player) {
            return getPlayerPatch(player)
                    .map(PlayerPatch::getPlayerMode)
                    .map(mode -> mode == PlayerPatch.PlayerMode.EPICFIGHT ? Mode.EPICFIGHT : Mode.VANILLA)
                    .orElse(Mode.NONE);
        }

        static boolean firstPersonModelActive() {
            return yesman.epicfight.config.ClientConfig.enableAnimatedFirstPersonModel;
        }

        static boolean vanillaModelActive() {
            return yesman.epicfight.config.ClientConfig.enableOriginalModel;
        }

        static float getStamina(Player player) {
            return getPlayerPatch(player).map(PlayerPatch::getStamina).orElse(0.0F);
        }

        static float getMaxStamina(Player player) {
            return getPlayerPatch(player).map(PlayerPatch::getMaxStamina).orElse(0.0F);
        }

        static boolean hasStamina(Player player, float amount) {
            return getPlayerPatch(player).map(patch -> patch.hasStamina(amount)).orElse(true);
        }
    }

    // ==================== Procedural pose support (gun aiming, whirling strike) ====================
    //
    // These poses animate the Epic Fight model directly (no dependency on the mod's base
    // Animation API). They are translated into Epic Fight's joint space via bind-chain conjugation,
    // exactly as the former bridge class did, but live here so the compat no longer depends on
    // the Animation API.

    private static final float AIM_X_ROT = -1.4279966F;
    private static final float WHIRL_SPEED = 0.8F;
    private static final float WHIRL_AMOUNT = 0.5F;
    private static final float WHIRL_BASE_X = -1.0F;
    private static final float AIM_BLEND_SPEED = 0.25F;
    private static final float AIM_BLEND_SPEED_SWING = 0.12F;
    private static final ConcurrentHashMap<UUID, float[]> GUN_AIM_BLEND = new ConcurrentHashMap<>();

    private static float jaams$getAimBlend(Player player, boolean isMainHand) {
        float[] arr = GUN_AIM_BLEND.get(player.getUUID());
        return arr != null ? (isMainHand ? arr[0] : arr[1]) : 0f;
    }

    private static void jaams$setAimBlend(Player player, boolean isMainHand, float value) {
        UUID uuid = player.getUUID();
        float[] arr = GUN_AIM_BLEND.getOrDefault(uuid, new float[]{0f, 0f});
        if (isMainHand) arr[0] = value; else arr[1] = value;
        GUN_AIM_BLEND.put(uuid, arr);
    }

    private static void jaams$removeAimBlend(Player player, boolean isMainHand) {
        UUID uuid = player.getUUID();
        float[] arr = GUN_AIM_BLEND.get(uuid);
        if (arr == null) return;
        if (isMainHand) arr[0] = 0f; else arr[1] = 0f;
        if (arr[0] <= 0.001f && arr[1] <= 0.001f) GUN_AIM_BLEND.remove(uuid);
        else GUN_AIM_BLEND.put(uuid, arr);
    }

    /**
     * Applies procedural poses (gun aiming, whirling strike) to Epic Fight's armature.
     * These are the same poses previously applied for the vanilla model, translated into Epic
     * Fight's joint space via bind-chain conjugation.
     */
    public static void applyProceduralPoses(Armature armature, Pose pose, Player player, float partialTicks) {
        if (armature == null || pose == null || player == null)
            return;
        jaams$applyWhirlingStrikePose(armature, pose, player, partialTicks);
        jaams$applyGunAimingPose(armature, pose, player, partialTicks);
    }

    private static void jaams$applyWhirlingStrikePose(Armature armature, Pose pose, Player player, float partialTicks) {
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

        float smoothTick = (float) player.tickCount + partialTicks;
        boolean bothHands = isMainWhirling && isOffWhirling;

        if (bothHands) {
            jaams$putProceduralArmTransform(armature, pose, "Arm_R",
                    WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED), 0.1F, 0.0F);
            jaams$putProceduralArmTransform(armature, pose, "Arm_L",
                    WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.cos(smoothTick * WHIRL_SPEED), -0.1F, 0.0F);
        } else {
            InteractionHand usedHand = player.getUsedItemHand();
            boolean whirlingInUsedHand = usedHand == InteractionHand.MAIN_HAND ? isMainWhirling : isOffWhirling;
            if (!whirlingInUsedHand)
                return;

            if (usedHand == InteractionHand.MAIN_HAND) {
                jaams$putProceduralArmTransform(armature, pose, "Arm_R",
                        WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED), 0.1F, 0.0F);
            } else {
                jaams$putProceduralArmTransform(armature, pose, "Arm_L",
                        WHIRL_BASE_X + WHIRL_AMOUNT * (float) Math.sin(smoothTick * WHIRL_SPEED), -0.1F, 0.0F);
            }
        }
    }

    private static void jaams$applyGunAimingPose(Armature armature, Pose pose, Player player, float partialTicks) {
        if (!GunSystemClientConfig.GUN_AIMING_ARM_ANIMATION.get())
            return;
        if (player.isUsingItem())
            return;

        boolean animationOverride = false;

        jaams$updateAndApplyHandBlend(armature, pose, player,
                player.getMainHandItem(), InteractionHand.MAIN_HAND, player.getMainArm(), true, animationOverride);
        jaams$updateAndApplyHandBlend(armature, pose, player,
                player.getOffhandItem(), InteractionHand.OFF_HAND, player.getMainArm().getOpposite(), false, animationOverride);
    }

    private static void jaams$updateAndApplyHandBlend(Armature armature, Pose pose, Player player,
            ItemStack stack, InteractionHand hand, HumanoidArm arm, boolean isMainHand, boolean animationOverride) {
        boolean shouldShowAim = !animationOverride
                && !player.swinging
                && jaams$canShowAimingPose(player, stack, isMainHand);

        float targetBlend = shouldShowAim ? 1f : 0f;
        float currentBlend = jaams$getAimBlend(player, isMainHand);

        if (currentBlend < targetBlend) {
            currentBlend = Math.min(targetBlend, currentBlend + AIM_BLEND_SPEED);
        } else if (currentBlend > targetBlend) {
            float speed = player.swinging ? AIM_BLEND_SPEED_SWING : AIM_BLEND_SPEED;
            currentBlend = Math.max(targetBlend, currentBlend - speed);
        }

        if (currentBlend <= 0.001f) {
            jaams$removeAimBlend(player, isMainHand);
            return;
        }
        jaams$setAimBlend(player, isMainHand, currentBlend);

        jaams$applyHandGunAimingPose(armature, pose, player, stack, hand, arm, currentBlend);
    }

    private static boolean jaams$canShowAimingPose(Player player, ItemStack stack, boolean isMainHand) {
        if (stack.isEmpty())
            return false;
        ModGuns.GunType gunType = ModGuns.getGunType(stack);
        if (gunType == null)
            return false;
        if (stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack))
            return false;
        String nbtPose = jaams$getPoseFromNBT(stack);
        if (nbtPose != null) {
            if ("NONE".equals(nbtPose.toUpperCase()))
                return false;
        } else {
            if (!jaams$shouldHaveAimingPose(gunType))
                return false;
        }
        return jaams$canDisplayPose(player, stack, gunType, isMainHand, player.isCreative());
    }

    private static void jaams$applyHandGunAimingPose(Armature armature, Pose pose, Player player,
            ItemStack stack, InteractionHand hand, HumanoidArm arm, float aimBlend) {
        if (stack.isEmpty())
            return;

        ModGuns.GunType gunType = ModGuns.getGunType(stack);
        if (gunType == null)
            return;

        boolean isMainHand = hand == InteractionHand.MAIN_HAND;

        String nbtPose = jaams$getPoseFromNBT(stack);
        String poseType = nbtPose != null ? nbtPose.toUpperCase() : null;

        if (poseType != null) {
            if ("NONE".equals(poseType))
                return;
        } else {
            if (!jaams$shouldHaveAimingPose(gunType))
                return;
        }

        if (jaams$canDisplayPose(player, stack, gunType, isMainHand, player.isCreative())) {
            String jointName = arm == HumanoidArm.RIGHT ? "Arm_R" : "Arm_L";
            float headYRotOffset = isMainHand ? -0.1F : 0.1F;
            float headXRot = (float) Math.toRadians(player.getXRot());

            Quaternionf targetRotation = jaams$buildArmRotation(armature, jointName,
                    AIM_X_ROT + headXRot, headYRotOffset, 0.0F);

            JointTransform baseTransform = pose.orElseEmpty(jointName);
            JointTransform aimTransform = new JointTransform(new Vec3f(0.0F, 0.0F, 0.0F), targetRotation,
                    new Vec3f(1.0F, 1.0F, 1.0F));
            JointTransform blended = JointTransform.interpolate(baseTransform, aimTransform, aimBlend);
            pose.putJointData(jointName, blended);
        }
    }

    /**
     * Converts vanilla ModelPart-space rotations (radians) into an Epic Fight joint transform
     * and puts it into the pose. Arm joints have their X and Z negated for frame conversion,
     * then the rotation is conjugated by the bind chain.
     */
    private static void jaams$putProceduralArmTransform(Armature armature, Pose pose, String jointName,
            float xRot, float yRot, float zRot) {
        Quaternionf rotation = jaams$buildArmRotation(armature, jointName, xRot, yRot, zRot);
        pose.putJointData(jointName, new JointTransform(new Vec3f(0.0F, 0.0F, 0.0F), rotation,
                new Vec3f(1.0F, 1.0F, 1.0F)));
    }

    private static Quaternionf jaams$buildArmRotation(Armature armature, String jointName,
            float xRot, float yRot, float zRot) {
        Quaternionf chain = jaams$computeBindChain(armature, jointName);
        Quaternionf chainInv = new Quaternionf(chain).conjugate();

        Quaternionf rotation = new Quaternionf();
        new Quaternionf(chainInv)
                .mul(new Quaternionf().rotationXYZ(-xRot, yRot, -zRot))
                .mul(chain, rotation);
        return rotation;
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
        CompoundTag tag = stack.getTag();
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
        if (shootData != null && shootData.ammo_consumption != -1)
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

    /**
     * Computes the bind rotation product along the path {@code root → joint}, i.e. the rotation
     * that takes the joint's local frame into the armature frame at rest. Returns the identity
     * quaternion when the armature has no such joint (for example non-humanoid armatures).
     */
    private static Quaternionf jaams$computeBindChain(Armature armature, String jointName) {
        if (!armature.hasJoint(jointName)) {
            return new Quaternionf();
        }
        ArrayList<String> path = new ArrayList<>();
        armature.gatherAllJointsInPathToTerminal(jointName, path);
        Quaternionf chain = new Quaternionf();
        for (String name : path) {
            Joint joint = armature.searchJointByName(name);
            if (joint != null) {
                chain.mul(joint.getLocalTransform().toQuaternion());
            }
        }
        return chain;
    }
}
