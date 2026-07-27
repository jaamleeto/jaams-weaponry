package net.jaams.weaponry.mixins.client;

import net.jaams.weaponry.util.ModComponents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.jaams.weaponry.item.BroomItem;
import net.jaams.weaponry.item.FlatBowItem;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.client.AssortedClientConfig;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.fml.ModList;

import java.util.Optional;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private float oMainHandHeight;
    @Shadow private float oOffHandHeight;
    @Shadow private Minecraft minecraft;

    @Unique private float jaams$mainGunDrop;
    @Unique private float jaams$oMainGunDrop;
    @Unique private float jaams$offGunDrop;
    @Unique private float jaams$oOffGunDrop;

    @Shadow
    private void renderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext transformType,
            boolean leftHand,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void jaams$tickGunCooldown(CallbackInfo ci) {
        if (!GunSystemClientConfig.GUN_COOLDOWN_ANIMATION.get()) {
            return;
        }
        this.jaams$oMainGunDrop = this.jaams$mainGunDrop;
        this.jaams$oOffGunDrop = this.jaams$offGunDrop;

        LocalPlayer player = this.minecraft.player;
        if (player == null) {
            return;
        }

        float maxDrop = GunSystemClientConfig.GUN_COOLDOWN_DROP.get().floatValue();
        float step = maxDrop * 0.4F;

        ItemStack mainItem = player.getMainHandItem();
        float mainCooldown = player.getCooldowns().getCooldownPercent(mainItem.getItem(), 0.0F);
        float mainTarget = ModGuns.isGun(mainItem) ? mainCooldown * maxDrop : 0.0F;
        this.jaams$mainGunDrop += Mth.clamp(mainTarget - this.jaams$mainGunDrop, -step, step);

        ItemStack offItem = player.getOffhandItem();
        float offCooldown = player.getCooldowns().getCooldownPercent(offItem.getItem(), 0.0F);
        float offTarget = ModGuns.isGun(offItem) ? offCooldown * maxDrop : 0.0F;
        this.jaams$offGunDrop += Mth.clamp(offTarget - this.jaams$offGunDrop, -step, step);
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void jaams$onRenderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext transformType,
            boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity == null || itemStack == null || itemStack.isEmpty() || transformType == null || poseStack == null
                || bufferSource == null) {
            return;
        }
        Item item = itemStack.getItem();
        if (item == null) {
            return;
        }
        boolean isMainHand = !leftHand;
        boolean isUsingItem = entity.isUsingItem() && itemStack.equals(entity.getUseItem());
        ItemStack mainHandItem = entity.getMainHandItem();
        ItemStack offHandItem = entity.getOffhandItem();
        boolean isInHand = (mainHandItem != null && mainHandItem.equals(itemStack))
                || (offHandItem != null && offHandItem.equals(itemStack));
        boolean isItemInHand = (mainHandItem != null && mainHandItem.getItem() == item)
                || (offHandItem != null && offHandItem.getItem() == item);
        HumanoidArm mainArm = entity.getMainArm();
        HumanoidArm arm = isMainHand ? mainArm : (mainArm != null ? mainArm.getOpposite() : null);
        if (arm == null) {
            return;
        }
        int horizontal = arm == HumanoidArm.RIGHT ? 1 : -1;
        int direction = (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) ? 1 : -1;

        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            applyFirstPersonTransformations(entity, itemStack, item, transformType, isMainHand, isUsingItem, isInHand,
                    horizontal, direction, poseStack);
        }

        else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            applyThirdPersonTransformations(entity, itemStack, item, transformType, isMainHand, isUsingItem, isInHand,
                    isItemInHand, horizontal, direction, poseStack);
        }

        applyGunCooldownAnimation(entity, itemStack, item, isMainHand, transformType, poseStack);
    }

    private void applyFirstPersonTransformations(LivingEntity entity, ItemStack itemStack, Item item,
            ItemDisplayContext transformType, boolean isMainHand, boolean isUsingItem, boolean isInHand, int horizontal,
            int direction, PoseStack poseStack) {
        if (entity == null || itemStack == null || item == null || transformType == null || poseStack == null) {
            return;
        }

        
        if (jaams$applyWhirlingStrikeFirstPersonTransform(entity, itemStack, isMainHand, horizontal, poseStack)) {
            return;
        }

        applyFirstPersonItemUseTransformations(entity, itemStack, item, transformType, isUsingItem, horizontal,
                direction,
                poseStack);

        applySwordBlockingTransform(entity, itemStack, item, isUsingItem, horizontal, poseStack);

        if (isInHand && itemStack.is(ModTags.NUNCHAKUS)) {
            poseStack.translate(0.0, 0.05, -0.1);
            poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
            poseStack.scale(0.95F, 0.95F, 0.95F);
        }

        else if (isInHand && itemStack.is(ModTags.BOKKENS)) {
            poseStack.translate(0.0, 0.1, 0.0);
        }
    }

    private void applyThirdPersonTransformations(LivingEntity entity, ItemStack itemStack, Item item,
            ItemDisplayContext transformType, boolean isMainHand, boolean isUsingItem, boolean isInHand,
            boolean isItemInHand, int horizontal, int direction, PoseStack poseStack) {
        if (entity == null || itemStack == null || item == null || transformType == null || poseStack == null) {
            return;
        }

        applyThirdPersonItemUseTransformations(entity, itemStack, item, transformType, isUsingItem, horizontal,
                direction,
                poseStack);

        if (isItemInHand && itemStack.is(ModTags.BROOMS)) {
            ItemStack useItem = entity.getUseItem();
            if (!(isUsingItem && useItem != null && useItem.getItem() instanceof BroomItem)) {
                poseStack.mulPose(Axis.YP.rotationDegrees(-25.0F * direction));
                poseStack.translate(0.03 * direction, -0.01, -0.01);
            }
        }

        else if (isInHand && itemStack.is(ModTags.KATANAS)) {
            poseStack.translate(0.0, 0.01, -0.034);
        }

        else if (isItemInHand && itemStack.is(ModTags.NUNCHAKUS)) {
            poseStack.translate(0.0, 0.01, -0.025);
        }

        else if (isItemInHand && itemStack.is(ModTags.ROYAL_SPEARS)) {
            poseStack.translate(0.0, -0.067, 0.009);
        }

        else if (isItemInHand && itemStack.is(ModTags.ROYAL_SWORDS)) {
            poseStack.translate(0.0, -0.08, 0.02);
        }

        else if (isItemInHand && itemStack.is(ModTags.WAR_PICKS)) {
            poseStack.translate(0.0, 0.102, 0.009);
        }

        else if (isInHand && itemStack.is(ModTags.SPEARS)) {
            poseStack.translate(0.0, -0.23, 0.026);
        }

        else if (isInHand && itemStack.is(ModTags.TWINBLADES)) {
            poseStack.translate(0.0, -0.33, 0.02);
        }

        else if (isInHand && itemStack.is(ModTags.SCYTHES)) {
            poseStack.translate(0.0, -0.183, 0.057);
        }

        else if (isInHand && itemStack.is(ModTags.GREATSWORDS)) {
            poseStack.translate(0.0, 0.03, -0.04);
        }

        else if (isInHand && itemStack.is(ModTags.BROADSWORDS)) {
            poseStack.translate(0.0, 0.045, -0.04);
        }

        else if (isInHand && itemStack.is(ModTags.BATTLE_AXES)) {
            poseStack.translate(0.0, 0.05, 0.015);
        }

        else if (isInHand && itemStack.is(ModTags.KATARS)) {
            poseStack.translate(0.0, -0.18, 0.16);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
        }

        else if (isInHand && itemStack.is(ModTags.REVERSE_DAGGERS)) {
            poseStack.translate(0.0, -0.66, 0.1);
            poseStack.mulPose(Axis.XP.rotationDegrees(5.0F));
        }

        else if (isInHand && itemStack.is(ModTags.KNUCKLES)) {
            poseStack.translate(0.0, -0.23, 0.24);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
        }

        else if (isInHand && itemStack.is(ModTags.CLAWS)) {
            poseStack.translate(0.0, -0.26, 0.22);
            poseStack.scale(1.2F, 1.2F, 1.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
        }

        else if (isInHand && itemStack.is(ModTags.RINGS)) {
            poseStack.translate(0.0, -0.2, 0.08);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
        }

        else if (isInHand && itemStack.is(ModTags.SICKLES)) {
            poseStack.translate(0.0, -0.03, 0.045);
        }

        else if (isInHand && itemStack.is(ModTags.KAMAS)) {
            poseStack.translate(0.0, -0.118, -0.018);
        }

        else if (isItemInHand && itemStack.is(ModTags.KUNAIS)) {
            poseStack.translate(0.0, -0.02, 0.04);
        }

        else if (isItemInHand && itemStack.is(ModTags.PRONGED_KUNAIS)) {
            poseStack.translate(0.0, -0.03, 0.04);
        }

        else if (isItemInHand && itemStack.is(ModTags.SHARP_STONE_BLADES)) {
            poseStack.translate(0.0, -0.02, 0.0);
        }

        if (entity.isUsingItem() && itemStack == entity.getUseItem()) {
            if (itemStack.is(ModTags.KUNAIS)) {
                poseStack.translate(0.0, 0.1, 0.0);
            } else if (itemStack.is(ModTags.PRONGED_KUNAIS)) {
                poseStack.translate(0.0, 0.2, 0.0);
            }
        }

        if (ModList.get() != null && ModList.get().isLoaded("epicfight")) {
            if (ModUtils.isEntityInBattleMode(entity) || ModUtils.hasEpicFightAttribute(entity)) {
                applyEpicFightTransformations(entity, itemStack, item, isInHand, isItemInHand, isUsingItem, poseStack);
            }
        }
    }

    private void applyFirstPersonItemUseTransformations(LivingEntity entity, ItemStack itemStack, Item item,
            ItemDisplayContext transformType, boolean isUsingItem, int horizontal, int direction,
            PoseStack poseStack) {
        if (!isUsingItem || entity == null || itemStack == null || item == null || transformType == null
                || poseStack == null) {
            return;
        }

        if (ModCompats.isSlingshot(itemStack)) {
            poseStack.translate(0.0, 0.0, -0.1);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F * horizontal));
            poseStack.mulPose(Axis.ZP.rotationDegrees(60.0F * horizontal));
        }

        else if (item instanceof FlatBowItem) {
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-12.0F * direction));
            poseStack.mulPose(Axis.ZP.rotationDegrees(98.0F * direction));
        }

        else if (itemStack.is(ModTags.BROOMS)) {
            poseStack.translate(0.0, -0.2, -0.3);
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
        }

        else if (itemStack.is(ModTags.SHARP_STONES)) {
            poseStack.translate(0.0, 0.2, -0.2);
            poseStack.mulPose(Axis.XP.rotationDegrees(135.0F));
        }

        else if (itemStack.is(ModTags.THROWABLE_FIX) && itemStack.getUseAnimation() == UseAnim.SPEAR) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        }
    }

    private void applyThirdPersonItemUseTransformations(LivingEntity entity, ItemStack itemStack, Item item,
            ItemDisplayContext transformType, boolean isUsingItem, int horizontal, int direction,
            PoseStack poseStack) {
        if (!isUsingItem || entity == null || itemStack == null || item == null || transformType == null
                || poseStack == null) {
            return;
        }

        else if (item instanceof FlatBowItem) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-2.0F * direction));
            poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F * direction));
            poseStack.translate(0.0, 0.05, 0.0);
        }

        else if (ModCompats.isSlingshot(itemStack)) {
            poseStack.translate(0.08 * direction, 0.0, 0.06);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-80.0F * horizontal));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F * horizontal));
        }

        else if (itemStack.is(ModTags.KUNAIS)) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.translate(0.0, 0.11, 0.0);
        }

        else if (itemStack.is(ModTags.THROWABLE_FIX) && itemStack.getUseAnimation() == UseAnim.SPEAR) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    private void applyEpicFightTransformations(LivingEntity entity, ItemStack itemStack, Item item, boolean isInHand,
            boolean isItemInHand, boolean isUsingItem, PoseStack poseStack) {
        if (entity == null || itemStack == null || item == null || poseStack == null) {
            return;
        }
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
        if (itemKey == null) {
            return;
        }

        if (isUsingItem && item instanceof FlatBowItem) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-5.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-15.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(5.0F));
            poseStack.translate(0.0, 0.0, -0.05);
        }

        else if (isItemInHand && itemStack.is(ModTags.GAUNTLETS)) {
            poseStack.scale(1.05F, 1.05F, 1.05F);
            poseStack.translate(0.0, 0.01, -0.08);
        }

        else if (isInHand && itemStack.is(ModTags.SCYTHES)) {
            poseStack.translate(0.0, 0.15, -0.07);
            poseStack.mulPose(Axis.XP.rotationDegrees(17.0F));
        }

        else if (isItemInHand && itemStack.is(ModTags.WAR_PICKS)) {
            poseStack.mulPose(Axis.XP.rotationDegrees(17.0F));
        }

        else if (isItemInHand && itemStack.is(ModTags.KATARS)) {
            poseStack.translate(0.0, 0.05, 0.0);
        }

        else if (isInHand && itemStack.is(ModTags.HOOK_SWORDS)) {
            poseStack.translate(0.0, 0.07, -0.07);
            poseStack.mulPose(Axis.XP.rotationDegrees(17.0F));
        }

        else if (isInHand && itemStack.is(ModTags.GREATSWORDS)) {
            poseStack.translate(0.0, 0.07, 0.02);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
        }

        else if (isItemInHand && itemStack.is(ModTags.ROYAL_SWORDS)) {
            poseStack.translate(0.0, 0.07, 0.02);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
        }

        else if (isInHand && itemStack.is(ModTags.LONGSWORDS)) {
            poseStack.translate(0.0, 0.02, -0.01);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
        }

        else if (isInHand && itemStack.is(ModTags.ZWEIHANDERS)) {
            poseStack.translate(0.0, 0.02, -0.01);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
        }

        else if (isItemInHand && itemStack.is(ModTags.KUNAIS) && !isUsingItem) {
            poseStack.translate(0.0, -0.44, 0.0);
        }

        else if (isInHand && !isUsingItem
                && (itemStack.is(ModTags.DYNAMITES) || itemStack.is(ModTags.SMOKE_BOMBS)
                        || itemStack.is(ModTags.SHURIKENS) || itemStack.is(ModTags.GIANT_SHURIKENS)
                        || itemStack.is(ModTags.BROOMS) || itemStack.is(ModTags.SHARP_STONES))) {
            poseStack.translate(0.0, -0.34, 0.0);
        }

        else if (isInHand
                && (itemStack.is(ModTags.BUSTER_SWORDS) || itemStack.is(ModTags.GREAT_HAMMERS)
                        || itemStack.is(ModTags.WAR_PICKS))) {
            poseStack.translate(0.0, 0.0, -0.14);
        }
    }

    @Unique
    private void applyGunCooldownAnimation(LivingEntity entity, ItemStack itemStack, Item item, boolean isMainHand,
            ItemDisplayContext transformType, PoseStack poseStack) {
        if (entity == null || itemStack == null || item == null || poseStack == null) {
            return;
        }
        if (!ModGuns.isGun(itemStack)) {
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        if (transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return;
        }
        if (!GunSystemClientConfig.GUN_COOLDOWN_ANIMATION.get()) {
            return;
        }
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float drop = isMainHand
                ? Mth.lerp(partialTick, this.jaams$oMainGunDrop, this.jaams$mainGunDrop)
                : Mth.lerp(partialTick, this.jaams$oOffGunDrop, this.jaams$offGunDrop);
        if (drop > 0.001F) {
            poseStack.translate(0.0, -drop, 0.0);
        }
    }

    @Unique
    private boolean jaams$applyWhirlingStrikeFirstPersonTransform(LivingEntity entity, ItemStack itemStack,
            boolean isMainHand, int horizontal, PoseStack poseStack) {
        if (!(entity instanceof Player player))
            return false;
        if (!AssortedClientConfig.WHIRLING_STRIKE_ARM_ANIMATION.get())
            return false;
        if (!ModTraits.isWhirlingStrikeItem(itemStack))
            return false;
        if (player.getCooldowns().isOnCooldown(itemStack.getItem()))
            return false;
        if (!player.isUsingItem())
            return false;

        ItemStack offHandItem = player.getOffhandItem();
        ItemStack mainHandItem = player.getMainHandItem();
        boolean isBothHands = ModTraits.isWhirlingStrikeItem(offHandItem)
                && ModTraits.isWhirlingStrikeItem(mainHandItem)
                && player.isUsingItem();

        if (!isBothHands && player.getUseItem() != itemStack)
            return false;

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float speedFactor = 0.7F;
        float swingProgress = (player.tickCount + partialTick) * speedFactor;
        float arcMotion = (float) Math.sin(swingProgress) * 0.2F;
        float impactMotion = (float) Math.abs(Math.cos(swingProgress)) * 0.08F;

        if (isBothHands) {
            if (horizontal == 1) {
                poseStack.translate(0.4F, -0.6F + impactMotion, -0.7F + arcMotion);
                                    poseStack.scale(2.0F, 2.0F, 2.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F + arcMotion * 40.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(2.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(arcMotion * 1.2F));
            } else {
                poseStack.translate(-0.4F, -0.6F + impactMotion, -0.7F + arcMotion);
                                    poseStack.scale(2.0F, 2.0F, 2.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F + arcMotion * 40.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-2.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-arcMotion * 1.2F));
            }
        } else {
            poseStack.translate(horizontal * 0.6F, -0.8F + impactMotion, -0.9F + arcMotion);
            poseStack.scale(2.2F, 2.2F, 2.2F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-40.0F + arcMotion * 50.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(horizontal * 4.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(horizontal * arcMotion * 2.0F));
        }
        return true;
    }

    @Unique
    private static boolean shouldApplyFirstPersonTransform(ItemStack stack) {
        if (ModTraits.isParryGuardItem(stack)) {
            return true;
        }
        if (ModTraits.isGuardStanceItem(stack)) {
            if (!TraitsConfig.GUARD_STANCE_APPLY_FIRST_PERSON_TRANSFORM.get()) {
                return false;
            }
            Optional<TraitModifierData.GuardStanceEntry> entry = TraitModifierData.getGuardStance(stack);
            if (entry.isPresent() && entry.get().apply_first_person_transform != null) {
                return entry.get().apply_first_person_transform;
            }
            return true;
        }
        return false;
    }

    @Unique
    private void applySwordBlockingTransform(LivingEntity entity, ItemStack itemStack, Item item,
            boolean isUsingItem, int horizontal, PoseStack poseStack) {
        if (!isUsingItem || entity == null || itemStack == null || item == null || poseStack == null) {
            return;
        }

        if (itemStack.getUseAnimation() == UseAnim.BLOCK
                && (ModTraits.isGuardStanceItem(itemStack) || ModTraits.isParryGuardItem(itemStack))
                && shouldApplyFirstPersonTransform(itemStack)) {
            float yTranslation = (itemStack.is(ModTags.GAUNTLETS) || itemStack.is(ModTags.SWORDS)) ? 0.05F : 0.25F;
            poseStack.translate(horizontal * -0.00142136F, yTranslation, 0.14142136F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-88.25F));
            poseStack.mulPose(Axis.YP.rotationDegrees(horizontal * 13.365F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(horizontal * 88.05F));
        }
    }
}
