package net.jaams.weaponry.tooltip.item;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.data.RangedItemData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import java.util.List;
import java.util.ArrayList;

public class SlingshotTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null)
            return;
        if (!ModCompats.isSlingshot(stack))
            return;

        RangedItemData data = RangedItemData.getData(stack).orElse(null);

        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting",
                        ChatFormatting.YELLOW);

        addBaseDamage(stack, tooltip, data);
        addBaseKnockback(stack, tooltip, data);
        addMaxDrawTime(stack, tooltip, data);
        addMinSpeed(stack, tooltip, data);
        addMaxSpeed(stack, tooltip, data);

        if (!net.minecraft.client.gui.screens.Screen.hasControlDown())
            return;

        addAmmoInfo(stack, tooltip, data);
    }

    private static void addBaseDamage(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        double value = getFinalDouble(stack, "SlingshotBaseDamage",
                data != null && data.ranged != null && data.ranged.base_damage != null ? data.ranged.base_damage.doubleValue() : null,
                1.0);
        ModTooltips.addStat(stack, tooltip, "base_damage", value);
    }

    private static void addBaseKnockback(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        double value = getFinalDouble(stack, "SlingshotBaseKnockback",
                data != null && data.ranged != null && data.ranged.base_knockback != null ? data.ranged.base_knockback.doubleValue() : null,
                0.5);
        ModTooltips.addStat(stack, tooltip, "base_knockback", value);
    }

    private static void addMaxDrawTime(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        int drawTicks = getFinalInt(stack, "SlingshotMaxDrawDuration",
                data != null && data.ranged != null ? data.ranged.max_draw_duration : null,
                20);
        double seconds = drawTicks / 20.0;
        ModTooltips.addStat(stack, tooltip, "max_draw_time", ModTooltips.roundToTwoDecimals(seconds));
    }

    private static void addMinSpeed(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        double value = getFinalDouble(stack, "SlingshotMinSpeed",
                data != null && data.ranged != null && data.ranged.min_speed != null ? data.ranged.min_speed.doubleValue() : null,
                0.5);
        ModTooltips.addStat(stack, tooltip, "min_speed", value);
    }

    private static void addMaxSpeed(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        double value = getFinalDouble(stack, "SlingshotMaxSpeed",
                data != null && data.ranged != null && data.ranged.max_speed != null ? data.ranged.max_speed.doubleValue() : null,
                2.5);
        ModTooltips.addStat(stack, tooltip, "max_speed", value);
    }

    private static void addAmmoInfo(ItemStack stack, List<Component> tooltip, RangedItemData data) {
        if (!TooltipsConfig.TOOLTIP_SLINGSHOT_BASE_AMMO.get()) return;
        List<String> ammoIds = getFinalStringList(stack, "SlingshotAmmoItems",
                data != null && data.ammo != null ? data.ammo.ammo_items : null);

        if (ammoIds.isEmpty()) {
            ammoIds.addAll(ItemFeaturesConfig.SLINGSHOT_AMMO_ITEMS.get());
        }

        if (ammoIds.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.ammo",
                    Component.translatable("tooltip.jaams_weaponry.ammo.any")
                            .withStyle(ChatFormatting.ITALIC))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            List<Component> names = new ArrayList<>();
            int shown = 0;
            int maxShown = 5;
            int total = 0;
            for (String id : ammoIds) {
                ResourceLocation loc = ResourceLocation.tryParse(id);
                if (loc == null) continue;
                Item item = BuiltInRegistries.ITEM.get(loc);
                if (item == null || item == Items.AIR)
                    continue;
                total++;
                if (shown < maxShown) {
                    names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                    shown++;
                }
            }
            if (!names.isEmpty()) {
                Component ammoComp = names.stream()
                        .reduce((a, b) -> a.copy().append(
                                Component.literal(", ").withStyle(ChatFormatting.GRAY)).append(b))
                        .orElse(Component.empty());
                int remaining = total - maxShown;
                if (remaining > 0) {
                    ammoComp = ammoComp.copy().append(
                            Component.literal(" +" + remaining)
                                    .withStyle(ChatFormatting.DARK_GRAY));
                }
                tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.base_ammo", ammoComp)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static double getFinalDouble(ItemStack stack, String nbtKey, Double jsonValue, double defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(nbtKey, Tag.TAG_DOUBLE)) {
            return ModComponents.get(stack).getDouble(nbtKey);
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return defaultValue;
    }

    private static int getFinalInt(ItemStack stack, String nbtKey, Integer jsonValue, int defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(nbtKey, Tag.TAG_INT)) {
            return ModComponents.get(stack).getInt(nbtKey);
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return defaultValue;
    }

    private static List<String> getFinalStringList(ItemStack stack, String nbtKey, List<String> jsonValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(nbtKey, Tag.TAG_LIST)) {
            var tag = ModComponents.get(stack).getList(nbtKey, Tag.TAG_STRING);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < tag.size(); i++) {
                list.add(tag.getString(i));
            }
            if (!list.isEmpty())
                return list;
        }
        if (jsonValue != null && !jsonValue.isEmpty()) {
            return new ArrayList<>(jsonValue);
        }
        return new ArrayList<>();
    }
}
