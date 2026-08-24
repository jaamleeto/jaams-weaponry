package net.jaams.weaponry.handler.behavior.item;

import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.handler.trait.QuickSwapHandler;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MobQuickSwapBehaviorHandler {

    private static final String[] SUFFIXES = {"_reverse", "_unfolded", "_folded"};
    private static final int MIN_INTERVAL = 200;
    private static final int MAX_INTERVAL = 400;

    private static final Map<UUID, State> STATES = new HashMap<>();

    public static boolean tryExecute(Mob mob, long tick) {
        if (!MobBehaviorConfig.QUICK_SWAP_MOBS_ENABLED.get()) return false;
        if (!TraitsConfig.QUICK_SWAP.get()) return false;
        if (ModUtils.hasRestrictedEffect(mob)) return false;

        State state = STATES.get(mob.getUUID());
        if (state == null) {
            state = new State();
            int min = MobBehaviorConfig.QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS.get();
            int max = MobBehaviorConfig.QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS.get();
            state.nextSwapTick = mob.tickCount + Math.max(1,
                    min + mob.getRandom().nextInt(Math.max(1, max - min + 1)));
            STATES.put(mob.getUUID(), state);
        }

        if (mob.tickCount < state.nextSwapTick) return false;

        boolean swapped = false;
        if (hasQuickSwapInHand(mob, InteractionHand.MAIN_HAND)) {
            performSwap(mob, InteractionHand.MAIN_HAND);
            swapped = true;
        }
        if (hasQuickSwapInHand(mob, InteractionHand.OFF_HAND)) {
            performSwap(mob, InteractionHand.OFF_HAND);
            swapped = true;
        }

        if (swapped) {
            int interval = MIN_INTERVAL + mob.getRandom().nextInt(MAX_INTERVAL - MIN_INTERVAL + 1);
            state.nextSwapTick = mob.tickCount + interval;
        }
        return swapped;
    }

    public static void removeState(UUID uuid) {
        STATES.remove(uuid);
    }

    private static boolean hasQuickSwapInHand(Mob mob, InteractionHand hand) {
        ItemStack stack = mob.getItemInHand(hand);
        if (stack.isEmpty() || !ModTraits.isQuickSwapItem(stack)) return false;
        Item target = resolveTargetItem(stack);
        return isValidTarget(target) && stack.getItem() != target;
    }

    private static void performSwap(Mob mob, InteractionHand hand) {
        ItemStack stack = mob.getItemInHand(hand);
        if (stack.isEmpty()) return;

        Item targetItem = resolveTargetItem(stack);
        if (!isValidTarget(targetItem) || stack.getItem() == targetItem) return;

        mob.swing(hand);

        int mainHandCooldown = getMainHandCooldown(stack);
        int offHandCooldown = getOffHandCooldown(stack);
        String soundEvent = getSoundEvent(stack);
        List<Item> noCooldownItems = getNoCooldownItems(stack);
        List<ResourceLocation> noCooldownTags = getNoCooldownTags(stack);

        QuickSwapHandler.switchItem(
                mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob,
                stack.getItem(), targetItem,
                mainHandCooldown, offHandCooldown,
                soundEvent, noCooldownItems, noCooldownTags,
                hand);
    }

    private static Item resolveTargetItem(ItemStack stack) {
        Item target = getNbtTarget(stack);
        if (isValidTarget(target)) return target;
        target = getJsonTarget(stack);
        if (isValidTarget(target)) return target;
        return resolveBySuffix(stack);
    }

    private static boolean isValidTarget(Item item) {
        return item != null && item != net.minecraft.world.item.Items.AIR;
    }

    private static Item getNbtTarget(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapTargetItem")) {
            ResourceLocation loc = ResourceLocation.tryParse(tag.getString("QuickSwapTargetItem"));
            if (loc != null) {
                Item item = ForgeRegistries.ITEMS.getValue(loc);
                if (isValidTarget(item)) return item;
            }
        }
        return null;
    }

    private static Item getJsonTarget(ItemStack stack) {
        return TraitModifierData.getQuickSwap(stack)
                .map(entry -> entry.target_item)
                .filter(java.util.Objects::nonNull)
                .map(id -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id)))
                .filter(MobQuickSwapBehaviorHandler::isValidTarget)
                .orElse(null);
    }

    private static Item resolveBySuffix(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) return null;
        String ns = key.getNamespace();
        String path = key.getPath();
        for (String suffix : SUFFIXES) {
            if (path.endsWith(suffix)) {
                String base = path.substring(0, path.length() - suffix.length());
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(ns, base));
                if (isValidTarget(item)) return item;
            }
        }
        for (String suffix : SUFFIXES) {
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(ns, path + suffix));
            if (isValidTarget(item)) return item;
        }
        return null;
    }

    private static int getMainHandCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapMainHandCooldown"))
            return Math.max(0, tag.getInt("QuickSwapMainHandCooldown"));
        return TraitModifierData.getQuickSwap(stack)
                .map(e -> e.main_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_MAIN_HAND_COOLDOWN.get());
    }

    private static int getOffHandCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapOffHandCooldown"))
            return Math.max(0, tag.getInt("QuickSwapOffHandCooldown"));
        return TraitModifierData.getQuickSwap(stack)
                .map(e -> e.off_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_OFF_HAND_COOLDOWN.get());
    }

    private static String getSoundEvent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapSound"))
            return tag.getString("QuickSwapSound");
        String json = TraitModifierData.getQuickSwap(stack)
                .map(e -> e.sound)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (json != null) return json;
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key != null) {
            String p = key.getPath().toLowerCase(Locale.ROOT);
            if (p.contains("wooden") || p.contains("stone"))
                return "jaams_weaponry:switch_alt";
        }
        return "jaams_weaponry:switch";
    }

    private static List<Item> getNoCooldownItems(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapNoCooldownItems")) {
            String raw = tag.getString("QuickSwapNoCooldownItems");
            if (!raw.isEmpty()) {
                List<Item> items = new ArrayList<>();
                for (String id : raw.split(",")) {
                    ResourceLocation loc = ResourceLocation.tryParse(id.trim());
                    if (loc != null) {
                        Item item = ForgeRegistries.ITEMS.getValue(loc);
                        if (item != null) items.add(item);
                    }
                }
                return items;
            }
        }
        List<String> json = TraitModifierData.getQuickSwap(stack)
                .map(e -> e.no_cooldown_items)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (json != null && !json.isEmpty()) {
            List<Item> items = new ArrayList<>();
            for (String id : json) {
                ResourceLocation loc = ResourceLocation.tryParse(id);
                if (loc != null) {
                    Item item = ForgeRegistries.ITEMS.getValue(loc);
                    if (item != null) items.add(item);
                }
            }
            return items;
        }
        return null;
    }

    private static List<ResourceLocation> getNoCooldownTags(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapNoCooldownTags")) {
            String raw = tag.getString("QuickSwapNoCooldownTags");
            if (!raw.isEmpty()) {
                List<ResourceLocation> tags = new ArrayList<>();
                for (String id : raw.split(",")) {
                    ResourceLocation loc = ResourceLocation.tryParse(id.trim());
                    if (loc != null) tags.add(loc);
                }
                return tags;
            }
        }
        List<String> json = TraitModifierData.getQuickSwap(stack)
                .map(e -> e.no_cooldown_tags)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (json != null && !json.isEmpty()) {
            List<ResourceLocation> tags = new ArrayList<>();
            for (String id : json) {
                ResourceLocation loc = ResourceLocation.tryParse(id);
                if (loc != null) tags.add(loc);
            }
            return tags;
        }
        return null;
    }

    private static class State {
        int nextSwapTick;
    }
}
