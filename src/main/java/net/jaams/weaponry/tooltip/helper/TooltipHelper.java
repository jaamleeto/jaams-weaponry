package net.jaams.weaponry.tooltip.helper;

import java.util.List;
import net.jaams.weaponry.tooltip.item.GunDefaultItemTooltip;
import net.jaams.weaponry.tooltip.item.GunItemTooltip;
import net.jaams.weaponry.tooltip.item.PistolItemTooltip;
import net.jaams.weaponry.tooltip.item.RevolverItemTooltip;
import net.jaams.weaponry.tooltip.item.ScattergunItemTooltip;
import net.jaams.weaponry.tooltip.item.ShotgunItemTooltip;
import net.jaams.weaponry.tooltip.item.SlingshotTooltip;
import net.jaams.weaponry.tooltip.item.AttachmentItemTooltip;
import net.jaams.weaponry.tooltip.item.BulletItemTooltip;
import net.jaams.weaponry.tooltip.item.FlintItemTooltip;
import net.jaams.weaponry.tooltip.item.SmokeBombItemTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableAxeTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableBroomTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableCleaverTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableDynamiteTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableGiantShurikenTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableHuntersBoomerangTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableItemTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableKunaiTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableProngedKunaiTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableRingTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableRoyalAxeTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableRoyalSpearTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableSharpStoneTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableShurikenTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableSpearTooltip;
import net.jaams.weaponry.tooltip.throwable.ThrowableTridentTooltip;
import net.jaams.weaponry.tooltip.trait.BackstabItemTooltip;
import net.jaams.weaponry.tooltip.trait.BusterStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.GuardStanceItemTooltip;
import net.jaams.weaponry.tooltip.trait.ParryGuardItemTooltip;
import net.jaams.weaponry.tooltip.trait.HeavyHandedItemTooltip;
import net.jaams.weaponry.tooltip.trait.AcrobaticLungeItemTooltip;
import net.jaams.weaponry.tooltip.trait.AntiAerialItemTooltip;
import net.jaams.weaponry.tooltip.trait.AquaticGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.ArmorBreakerItemTooltip;
import net.jaams.weaponry.tooltip.trait.ArthropodGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.BarbedHandleItemTooltip;
import net.jaams.weaponry.tooltip.trait.BladeBreakerItemTooltip;
import net.jaams.weaponry.tooltip.trait.BoneGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.BrittleHandleItemTooltip;
import net.jaams.weaponry.tooltip.trait.CleansingStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.SlashAssaultItemTooltip;
import net.jaams.weaponry.tooltip.trait.PiercingAssaultItemTooltip;
import net.jaams.weaponry.tooltip.trait.SmashStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.ShockImpactItemTooltip;
import net.jaams.weaponry.tooltip.trait.AfterStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.DecapitationItemTooltip;
import net.jaams.weaponry.tooltip.trait.DetonatingItemTooltip;
import net.jaams.weaponry.tooltip.trait.DexterousLungeItemTooltip;
import net.jaams.weaponry.tooltip.trait.DisablingStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.DisarmItemTooltip;
import net.jaams.weaponry.tooltip.trait.DisengageItemTooltip;
import net.jaams.weaponry.tooltip.trait.DismountItemTooltip;
import net.jaams.weaponry.tooltip.trait.DuelistItemTooltip;
import net.jaams.weaponry.tooltip.trait.ThreatResponseItemTooltip;
import net.jaams.weaponry.tooltip.trait.ExhaustingItemTooltip;
import net.jaams.weaponry.tooltip.trait.FragilityItemTooltip;
import net.jaams.weaponry.tooltip.trait.HarvestSweepItemTooltip;
import net.jaams.weaponry.tooltip.trait.OverstrainItemTooltip;
import net.jaams.weaponry.tooltip.trait.OverwhelmingStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.SuppressingStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.PiercerStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.PowerBoostItemTooltip;
import net.jaams.weaponry.tooltip.trait.PullLungeItemTooltip;
import net.jaams.weaponry.tooltip.trait.QuickCraftingItemTooltip;
import net.jaams.weaponry.tooltip.trait.RapidBoostItemTooltip;
import net.jaams.weaponry.tooltip.trait.QuickSwapItemTooltip;
import net.jaams.weaponry.tooltip.trait.ReachAdvantageItemTooltip;
import net.jaams.weaponry.tooltip.trait.RottenGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.SparringStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.SlipperyItemTooltip;
import net.jaams.weaponry.tooltip.trait.SnoutGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.ThroughStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.TraitorGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.UndeadGrudgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.UnstableEdgeItemTooltip;
import net.jaams.weaponry.tooltip.trait.WhirlingStrikeItemTooltip;
import net.jaams.weaponry.tooltip.trait.WildSweepItemTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.util.ModComponents;
import net.jaams.weaponry.util.ModTooltips;

public class TooltipHelper {

    public static void addAllTooltips(ItemStack stack, List<Component> tooltip) {
        AntiAerialItemTooltip.add(stack, tooltip);
        PiercerStrikeItemTooltip.add(stack, tooltip);
        DuelistItemTooltip.add(stack, tooltip);
        AfterStrikeItemTooltip.add(stack, tooltip);
        ArmorBreakerItemTooltip.add(stack, tooltip);
        BladeBreakerItemTooltip.add(stack, tooltip);
        RapidBoostItemTooltip.add(stack, tooltip);
        PowerBoostItemTooltip.add(stack, tooltip);
        QuickCraftingItemTooltip.add(stack, tooltip);
        QuickSwapItemTooltip.add(stack, tooltip);
        ThreatResponseItemTooltip.add(stack, tooltip);
        ReachAdvantageItemTooltip.add(stack, tooltip);
        AquaticGrudgeItemTooltip.add(stack, tooltip);
        ArthropodGrudgeItemTooltip.add(stack, tooltip);
        UndeadGrudgeItemTooltip.add(stack, tooltip);
        TraitorGrudgeItemTooltip.add(stack, tooltip);
        SnoutGrudgeItemTooltip.add(stack, tooltip);
        BoneGrudgeItemTooltip.add(stack, tooltip);
        RottenGrudgeItemTooltip.add(stack, tooltip);
        AcrobaticLungeItemTooltip.add(stack, tooltip);
        DexterousLungeItemTooltip.add(stack, tooltip);
        PullLungeItemTooltip.add(stack, tooltip);
        DisengageItemTooltip.add(stack, tooltip);
        DismountItemTooltip.add(stack, tooltip);
        DisablingStrikeItemTooltip.add(stack, tooltip);
        ThroughStrikeItemTooltip.add(stack, tooltip);
        CleansingStrikeItemTooltip.add(stack, tooltip);
        OverwhelmingStrikeItemTooltip.add(stack, tooltip);
        SuppressingStrikeItemTooltip.add(stack, tooltip);
        FragilityItemTooltip.add(stack, tooltip);
        SlipperyItemTooltip.add(stack, tooltip);
        ExhaustingItemTooltip.add(stack, tooltip);
        BrittleHandleItemTooltip.add(stack, tooltip);
        BarbedHandleItemTooltip.add(stack, tooltip);
        OverstrainItemTooltip.add(stack, tooltip);
        UnstableEdgeItemTooltip.add(stack, tooltip);
        DetonatingItemTooltip.add(stack, tooltip);
        DecapitationItemTooltip.add(stack, tooltip);
        SparringStrikeItemTooltip.add(stack, tooltip);
        HarvestSweepItemTooltip.add(stack, tooltip);
        SlashAssaultItemTooltip.add(stack, tooltip);
        PiercingAssaultItemTooltip.add(stack, tooltip);
        SmashStrikeItemTooltip.add(stack, tooltip);
        ShockImpactItemTooltip.add(stack, tooltip);
        WhirlingStrikeItemTooltip.add(stack, tooltip);
        WildSweepItemTooltip.add(stack, tooltip);
        BusterStrikeItemTooltip.add(stack, tooltip);
        BackstabItemTooltip.add(stack, tooltip);
        GuardStanceItemTooltip.add(stack, tooltip);
        ParryGuardItemTooltip.add(stack, tooltip);
        HeavyHandedItemTooltip.add(stack, tooltip);
        DisarmItemTooltip.add(stack, tooltip);
        GunItemTooltip.add(stack, tooltip);
        GunDefaultItemTooltip.add(stack, tooltip);
        PistolItemTooltip.add(stack, tooltip);
        ScattergunItemTooltip.add(stack, tooltip);
        RevolverItemTooltip.add(stack, tooltip);
        ShotgunItemTooltip.add(stack, tooltip);
        SlingshotTooltip.add(stack, tooltip);
        AttachmentItemTooltip.add(stack, tooltip);
        BulletItemTooltip.add(stack, tooltip);
        FlintItemTooltip.add(stack, tooltip);
        SmokeBombItemTooltip.add(stack, tooltip);
        ThrowableItemTooltip.add(stack, tooltip);
        if (!isDataDrivenThrowable(stack)) {
            ThrowableAxeTooltip.add(stack, tooltip);
        ThrowableCleaverTooltip.add(stack, tooltip);
        ThrowableRoyalAxeTooltip.add(stack, tooltip);
        ThrowableRoyalSpearTooltip.add(stack, tooltip);
        ThrowableGiantShurikenTooltip.add(stack, tooltip);
        ThrowableShurikenTooltip.add(stack, tooltip);
        ThrowableKunaiTooltip.add(stack, tooltip);
        ThrowableProngedKunaiTooltip.add(stack, tooltip);
        ThrowableSharpStoneTooltip.add(stack, tooltip);
        ThrowableSpearTooltip.add(stack, tooltip);
        ThrowableTridentTooltip.add(stack, tooltip);
        ThrowableHuntersBoomerangTooltip.add(stack, tooltip);
        ThrowableRingTooltip.add(stack, tooltip);
        ThrowableBroomTooltip.add(stack, tooltip);
        ThrowableDynamiteTooltip.add(stack, tooltip);
        }
        if (stack.is(Items.BOW)) {
            ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting",
                    ChatFormatting.YELLOW);
            ModTooltips.addStat(stack, tooltip, "base_damage", 1.0);
            ModTooltips.addStat(stack, tooltip, "max_draw_time", 1.0);
            ModTooltips.addStat(stack, tooltip, "draw_speed", 1.0);
            ModTooltips.addStat(stack, tooltip, "recoil", 0.0);
        }
    }

    private static boolean isDataDrivenThrowable(ItemStack stack) {
        if (ThrowableItemData.getData(stack).isPresent()) {
            return true;
        }
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null) {
            for (String key : tag.getAllKeys()) {
                if (key.startsWith("Force") && key.endsWith("Throwable") && tag.getBoolean(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}
