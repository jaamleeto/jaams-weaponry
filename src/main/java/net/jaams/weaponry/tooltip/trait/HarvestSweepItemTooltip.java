package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class HarvestSweepItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.HARVEST_SWEEP.get()) {
            return;
        }
        if (!ModTraits.isHarvestSweepItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.harvest_sweep",
                "tooltip.jaams_weaponry.trait.harvest_sweep.desc");
        if (!TooltipsConfig.TOOLTIP_HARVEST_SWEEP_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.harvest_sweep",
                ChatFormatting.GOLD);
        addHarvestRangeLine(stack, tag, tooltip);
        addTillRangeLine(stack, tag, tooltip);
        addMaxBlocksLine(stack, tag, tooltip);
    }

    private static void addHarvestRangeLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int range = getHarvestRange(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "harvest_sweep.harvest_range", range);
    }

    private static void addTillRangeLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int range = getTillRange(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "harvest_sweep.till_range", range);
    }

    private static void addMaxBlocksLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int maxBlocks = getMaxBlocks(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "harvest_sweep.max_blocks", maxBlocks);
    }

    private static int getHarvestRange(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepRange")) {
            return Math.max(0, tag.getInt("HarvestSweepRange"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.range)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_RANGE.get());
    }

    private static int getTillRange(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepTillRange")) {
            return Math.max(0, tag.getInt("HarvestSweepTillRange"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.till_range)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_TILL_RANGE.get());
    }

    private static int getMaxBlocks(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepMaxBlocks")) {
            return Math.max(1, tag.getInt("HarvestSweepMaxBlocks"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.max_blocks)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_MAX_BLOCKS.get());
    }

    private static int getTillDurabilityCost(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepTillDurabilityCost")) {
            return Math.max(0, tag.getInt("HarvestSweepTillDurabilityCost"));
        }
        Integer jsonValue = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.till_durability_cost)
                .orElse(null);
        if (jsonValue != null) {
            return jsonValue;
        }
        Integer generalJson = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_cost_per_block)
                .orElse(null);
        if (generalJson != null) {
            return generalJson;
        }
        return TraitsConfig.HARVEST_SWEEP_TILL_DURABILITY_COST.get();
    }

    private static int getHarvestDurabilityCost(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepHarvestDurabilityCost")) {
            return Math.max(0, tag.getInt("HarvestSweepHarvestDurabilityCost"));
        }
        Integer jsonValue = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.harvest_durability_cost)
                .orElse(null);
        if (jsonValue != null) {
            return jsonValue;
        }
        Integer generalJson = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_cost_per_block)
                .orElse(null);
        if (generalJson != null) {
            return generalJson;
        }
        return TraitsConfig.HARVEST_SWEEP_HARVEST_DURABILITY_COST.get();
    }

    private static boolean isDurabilityPerBlock(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("HarvestSweepDurabilityPerBlock")) {
            return tag.getBoolean("HarvestSweepDurabilityPerBlock");
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_per_block)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_DURABILITY_PER_BLOCK.get());
    }
}
