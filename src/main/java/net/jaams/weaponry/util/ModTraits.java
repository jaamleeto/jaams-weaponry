package net.jaams.weaponry.util;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModTraits {

    public static boolean isPiercerStrikeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.PIERCER_STRIKE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "piercer_strike"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PiercerStrikeTrait")) {
            return tag.getBoolean("PiercerStrikeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "piercer_strike")) {
            return true;
        }
        return stack.is(ModTags.PIERCER_STRIKE);
    }

    public static boolean isDuelistItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.DUELIST.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "duelist"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DuelistTrait")) {
            return tag.getBoolean("DuelistTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "duelist")) {
            return true;
        }
        return stack.is(ModTags.DUELIST);
    }

    public static boolean isThreatResponseItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.THREAT_RESPONSE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "threat_response"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThreatResponseTrait")) {
            return tag.getBoolean("ThreatResponseTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "threat_response")) {
            return true;
        }
        return stack.is(ModTags.THREAT_RESPONSE);
    }

    public static boolean isReachAdvantageItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.REACH_ADVANTAGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "reach_advantage"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ReachAdvantageTrait")) {
            return tag.getBoolean("ReachAdvantageTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "reach_advantage")) {
            return true;
        }
        return stack.is(ModTags.REACH_ADVANTAGE);
    }

    public static boolean isAfterStrikeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.AFTER_STRIKE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "after_strike"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AfterStrikeTrait")) {
            return tag.getBoolean("AfterStrikeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "after_strike")) {
            return true;
        }
        return stack.is(ModTags.AFTER_STRIKE);
    }

    public static boolean isQuickCraftingItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.QUICK_CRAFTING.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "quick_crafting"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingTrait")) {
            return tag.getBoolean("QuickCraftingTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "quick_crafting")) {
            return true;
        }
        return stack.is(ModTags.QUICK_CRAFTING);
    }

    public static boolean isAquaticGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.AQUATIC_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "aquatic_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AquaticGrudgeTrait")) {
            return tag.getBoolean("AquaticGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "aquatic_grudge")) {
            return true;
        }
        return stack.is(ModTags.AQUATIC_GRUDGE);
    }

    public static boolean isArthropodGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.ARTHROPOD_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "arthropod_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ArthropodGrudgeTrait")) {
            return tag.getBoolean("ArthropodGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "arthropod_grudge")) {
            return true;
        }
        return stack.is(ModTags.ARTHROPOD_GRUDGE);
    }

    public static boolean isUndeadGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.UNDEAD_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "undead_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UndeadGrudgeTrait")) {
            return tag.getBoolean("UndeadGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "undead_grudge")) {
            return true;
        }
        return stack.is(ModTags.UNDEAD_GRUDGE);
    }

    public static boolean isTraitorGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.TRAITOR_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "traitor_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("TraitorGrudgeTrait")) {
            return tag.getBoolean("TraitorGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "traitor_grudge")) {
            return true;
        }
        return stack.is(ModTags.TRAITOR_GRUDGE);
    }

    public static boolean isSnoutGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.SNOUT_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "snout_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SnoutGrudgeTrait")) {
            return tag.getBoolean("SnoutGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "snout_grudge")) {
            return true;
        }
        return stack.is(ModTags.SNOUT_GRUDGE);
    }

    public static boolean isBoneGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.BONE_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "bone_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BoneGrudgeTrait")) {
            return tag.getBoolean("BoneGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "bone_grudge")) {
            return true;
        }
        if (stack.is(ModTags.BONE_GRUDGE)) {
            return true;
        }
        return false;
    }

    public static boolean isAntiAerialItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.ANTI_AERIAL.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "anti_aerial"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AntiAerialTrait")) {
            return tag.getBoolean("AntiAerialTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "anti_aerial")) {
            return true;
        }
        return stack.is(ModTags.ANTI_AERIAL);
    }

    public static boolean isRottenGrudgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.ROTTEN_GRUDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "rotten_grudge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("RottenGrudgeTrait")) {
            return tag.getBoolean("RottenGrudgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "rotten_grudge")) {
            return true;
        }
        return stack.is(ModTags.ROTTEN_GRUDGE);
    }

    public static boolean isFragilityItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.FRAGILITY.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "fragility"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("FragilityTrait")) {
            return tag.getBoolean("FragilityTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "fragility")) {
            return true;
        }
        return stack.is(ModTags.FRAGILITY);
    }

    public static boolean isSlipperyItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.SLIPPERY.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "slippery"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SlipperyTrait")) {
            return tag.getBoolean("SlipperyTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "slippery")) {
            return true;
        }
        return stack.is(ModTags.SLIPPERY);
    }

    public static boolean isExhaustingItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.EXHAUSTING.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "exhausting"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ExhaustingTrait")) {
            return tag.getBoolean("ExhaustingTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "exhausting")) {
            return true;
        }
        return stack.is(ModTags.EXHAUSTING);
    }

    public static boolean isBrittleHandleItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.BRITTLE_HANDLE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "brittle_handle"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BrittleHandleTrait")) {
            return tag.getBoolean("BrittleHandleTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "brittle_handle")) {
            return true;
        }
        return stack.is(ModTags.BRITTLE_HANDLE);
    }

    public static boolean isBarbedHandleItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.BARBED_HANDLE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "barbed_handle"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BarbedHandleTrait")) {
            return tag.getBoolean("BarbedHandleTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "barbed_handle")) {
            return true;
        }
        return stack.is(ModTags.BARBED_HANDLE);
    }

    public static boolean isOverstrainItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.OVERSTRAIN.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "overstrain"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("OverstrainTrait")) {
            return tag.getBoolean("OverstrainTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "overstrain")) {
            return true;
        }
        return stack.is(ModTags.OVERSTRAIN);
    }

    public static boolean isUnstableEdgeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.UNSTABLE_EDGE.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "unstable_edge"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UnstableEdgeTrait")) {
            return tag.getBoolean("UnstableEdgeTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "unstable_edge")) {
            return true;
        }
        return stack.is(ModTags.UNSTABLE_EDGE);
    }

    public static boolean isDetonatingItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!TraitsConfig.DETONATING.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, "detonating"))
            return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DetonatingTrait")) {
            return tag.getBoolean("DetonatingTrait");
        }
        if (TraitModifierData.isTraitActive(stack, "detonating")) {
            return true;
        }
        return stack.is(ModTags.DETONATING);
    }

    public static boolean isArmorBreakerItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.ARMOR_BREAKER, "ArmorBreakerTrait", "armor_breaker",
                ModTags.ARMOR_BREAKER);
    }

    public static boolean isBladeBreakerItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.BLADE_BREAKER, "BladeBreakerTrait", "blade_breaker",
                ModTags.BLADE_BREAKER);
    }

    public static boolean isAcrobaticLungeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.ACROBATIC_LUNGE, "AcrobaticLungeTrait", "acrobatic_lunge",
                ModTags.ACROBATIC_LUNGE);
    }

    public static boolean isDexterousLungeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DEXTEROUS_LUNGE, "DexterousLungeTrait", "dexterous_lunge",
                ModTags.DEXTEROUS_LUNGE);
    }

    public static boolean isPullLungeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.PULL_LUNGE, "PullLungeTrait", "pull_lunge", ModTags.PULL_LUNGE);
    }

    public static boolean isDisengageItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DISENGAGE, "DisengageTrait", "disengage", ModTags.DISENGAGE);
    }

    public static boolean isDisarmItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DISARM, "DisarmTrait", "disarm", ModTags.DISARM);
    }

    public static boolean isDismountItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DISMOUNT, "DismountTrait", "dismount", ModTags.DISMOUNT);
    }

    public static boolean isDisablingStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DISABLING_STRIKE, "DisablingStrikeTrait", "disabling_strike",
                ModTags.DISABLING_STRIKE);
    }

    public static boolean isThroughStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.THROUGH_STRIKE, "ThroughStrikeTrait", "through_strike",
                ModTags.THROUGH_STRIKE);
    }

    public static boolean isCleansingStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.CLEANSING_STRIKE, "CleansingStrikeTrait", "cleansing_strike",
                ModTags.CLEANSING_STRIKE);
    }

    public static boolean isOverwhelmingStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.OVERWHELMING_STRIKE, "OverwhelmingStrikeTrait", "overwhelming_strike",
                ModTags.OVERWHELMING_STRIKE);
    }

    public static boolean isSuppressingStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.SUPPRESSING_STRIKE, "SuppressingStrikeTrait", "suppressing_strike",
                ModTags.SUPPRESSING_STRIKE);
    }

    public static boolean isSlashAssaultItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.SLASH_ASSAULT, "SlashAssaultTrait", "slash_assault",
                ModTags.SLASH_ASSAULT);
    }

    public static boolean isPiercingAssaultItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.PIERCING_ASSAULT, "PiercingAssaultTrait", "piercing_assault",
                ModTags.PIERCING_ASSAULT);
    }

    public static boolean isSparringStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.SPARRING_STRIKE, "SparringStrikeTrait", "sparring_strike",
                ModTags.SPARRING_STRIKE);
    }

    public static boolean isDecapitationItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.DECAPITATION, "DecapitationTrait", "decapitation",
                ModTags.DECAPITATION);
    }

    public static boolean isHarvestSweepItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.HARVEST_SWEEP, "HarvestSweepTrait", "harvest_sweep",
                ModTags.HARVEST_SWEEP);
    }

    public static boolean isWildSweepItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.WILD_SWEEP, "WildSweepTrait", "wild_sweep", ModTags.WILD_SWEEP);
    }

    public static boolean isWhirlingStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.WHIRLING_STRIKE, "WhirlingStrikeTrait", "whirling_strike",
                ModTags.WHIRLING_STRIKE);
    }

    public static boolean isRapidBoostItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.RAPID_BOOST, "RapidBoostTrait", "rapid_boost", ModTags.RAPID_BOOST);
    }

    public static boolean isPowerBoostItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.POWER_BOOST, "PowerBoostTrait", "power_boost", ModTags.POWER_BOOST);
    }

    public static boolean isQuickSwapItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.QUICK_SWAP, "QuickSwapTrait", "quick_swap", ModTags.QUICK_SWAP);
    }

    public static boolean isSmashStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.SMASH_STRIKE, "SmashStrikeTrait", "smash_strike", ModTags.SMASH_STRIKE);
    }

    public static boolean isShockImpactItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.SHOCK_IMPACT, "ShockImpactTrait", "shock_impact", ModTags.SHOCK_IMPACT);
    }

    public static boolean isBackstabItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.BACKSTAB, "BackstabTrait", "backstab", ModTags.BACKSTAB);
    }

    public static boolean isHeavyHandedItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.HEAVY_HANDED, "HeavyHandedTrait", "heavy_handed", ModTags.HEAVY_HANDED);
    }

    public static boolean isBusterStrikeItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.BUSTER_STRIKE, "BusterStrikeTrait", "buster_strike",
                ModTags.BUSTER_STRIKE);
    }

    public static boolean isGuardStanceItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.GUARD_STANCE, "GuardStanceTrait", "guard_stance",
                ModTags.GUARD_STANCE);
    }

    public static boolean isParryGuardItem(ItemStack stack) {
        return isTraitItem(stack, TraitsConfig.PARRY_GUARD, "ParryGuardTrait", "parry_guard",
                ModTags.PARRY_GUARD);
    }

    private static boolean isTraitItem(ItemStack stack, ForgeConfigSpec.BooleanValue config, String nbtKey,
            String jsonKey, TagKey<Item> tag) {
        if (stack == null || stack.isEmpty() || !config.get())
            return false;
        if (TraitModifierData.isTraitDisabled(stack, jsonKey)) {
            return false;
        }
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains(nbtKey)) {
            return compoundTag.getBoolean(nbtKey);
        }
        if (TraitModifierData.isTraitActive(stack, jsonKey)) {
            return true;
        }
        return stack.is(tag);
    }
}
