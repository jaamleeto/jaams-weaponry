package net.jaams.weaponry.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.configuration.client.CreativeTabConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.data.CreativeTabData;
import net.jaams.weaponry.loader.TabModifierLoader;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModTabs {

    private static final Logger LOGGER = LogManager.getLogger(ModTabs.class);
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, JaamsWeaponryMod.MODID);
    public static final RegistryObject<CreativeModeTab> WEAPONRY = REGISTRY.register("weaponry",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.jaams_weaponry.tab_icon")
                            .withStyle((style) -> style.withFont(new ResourceLocation("jaams_weaponry", "default"))))
                    .icon(ModTabs::getTabIcon)
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS.location())
                    .displayItems(ModTabs::fillTabContents)
                    .build());

    private static void fillTabContents(CreativeModeTab.ItemDisplayParameters parameters,
            CreativeModeTab.Output output) {
        List<ItemStack> finalList = new ArrayList<>();
        processDataDrivenEntries(finalList);
        finalList.forEach(output::accept);
    }

    private static void processDataDrivenEntries(List<ItemStack> list) {
        List<CreativeTabData.Entry> entries = TabModifierLoader.INSTANCE.getAllEntries();
        boolean cleared = false;
        for (CreativeTabData.Entry entry : entries) {
            if (!TabModifierLoader.INSTANCE.evaluateConditions(entry))
                continue;
            if (entry.clear_defaults) {
                list.clear();
                cleared = true;
                break;
            }
        }
        if (!cleared) {
            addDefaultItemsToList(list);
        }
        List<CreativeTabData.Entry> toRemove = new ArrayList<>();
        List<CreativeTabData.Entry> toAdd = new ArrayList<>();
        for (CreativeTabData.Entry entry : entries) {
            if (!TabModifierLoader.INSTANCE.evaluateConditions(entry))
                continue;
            if (entry.clear_defaults)
                continue;
            if (entry.remove) {
                toRemove.add(entry);
            } else if (entry.item != null && !entry.item.isEmpty()) {
                toAdd.add(entry);
            }
        }
        for (CreativeTabData.Entry entry : toRemove) {
            removeItemFromList(list, entry.item);
        }
        toAdd.sort((a, b) -> Integer.compare(b.weight, a.weight));
        for (CreativeTabData.Entry entry : toAdd) {
            addEntryToList(list, entry);
        }
    }

    private static void addEntryToList(List<ItemStack> list, CreativeTabData.Entry entry) {
        if (entry.item == null || entry.item.isEmpty())
            return;
        try {
            List<ItemStack> stacksToAdd = new ArrayList<>();
            if (entry.item.startsWith("#")) {
                String tagId = entry.item.substring(1);
                ResourceLocation tagLocation = new ResourceLocation(tagId);
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLocation);
                for (Item item : ForgeRegistries.ITEMS) {
                    ItemStack stack = new ItemStack(item);
                    if (stack.is(tagKey)) {
                        applyNBT(stack, entry.nbt);
                        stacksToAdd.add(stack);
                    }
                }
            } else {
                ItemStack stack = createSingleItemStack(entry);
                if (stack != null) {
                    stacksToAdd.add(stack);
                }
            }
            for (ItemStack stack : stacksToAdd) {
                insertItemOrdered(list, stack, entry);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process entry: {}", entry.item, e);
        }
    }

    private static ItemStack createSingleItemStack(CreativeTabData.Entry entry) {
        try {
            ResourceLocation loc = new ResourceLocation(entry.item);
            Item item = ForgeRegistries.ITEMS.getValue(loc);
            if (item == null || item == Items.AIR)
                return null;
            ItemStack stack = new ItemStack(item);
            applyNBT(stack, entry.nbt);
            return stack;
        } catch (Exception e) {
            LOGGER.warn("Failed to create stack: {}", entry.item);
            return null;
        }
    }

    private static void applyNBT(ItemStack stack, String nbtString) {
        if (nbtString == null || nbtString.isEmpty())
            return;
        try {
            CompoundTag tag = TagParser.parseTag(nbtString);
            stack.setTag(tag);
        } catch (Exception e) {
            LOGGER.warn("Invalid NBT");
        }
    }

    private static void removeItemFromList(List<ItemStack> list, String itemId) {
        if (itemId == null || itemId.isEmpty())
            return;
        try {
            if (itemId.startsWith("#")) {
                String tagId = itemId.substring(1);
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
                Set<Item> toRemove = new HashSet<>();
                for (Item item : ForgeRegistries.ITEMS) {
                    if (new ItemStack(item).is(tagKey)) {
                        toRemove.add(item);
                    }
                }
                list.removeIf((s) -> toRemove.contains(s.getItem()));
            } else {
                ResourceLocation target = new ResourceLocation(itemId);
                list.removeIf((s) -> ForgeRegistries.ITEMS.getKey(s.getItem()).equals(target));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to remove {}", itemId, e);
        }
    }

    private static void insertItemOrdered(List<ItemStack> list, ItemStack stack, CreativeTabData.Entry entry) {
        if (entry.after != null && !entry.after.isEmpty()) {
            ResourceLocation afterLoc = new ResourceLocation(entry.after);
            for (int i = 0; i < list.size(); i++) {
                if (ForgeRegistries.ITEMS.getKey(list.get(i).getItem()).equals(afterLoc)) {
                    list.add(i + 1, stack);
                    return;
                }
            }
        }
        if (entry.before != null && !entry.before.isEmpty()) {
            ResourceLocation beforeLoc = new ResourceLocation(entry.before);
            for (int i = 0; i < list.size(); i++) {
                if (ForgeRegistries.ITEMS.getKey(list.get(i).getItem()).equals(beforeLoc)) {
                    list.add(i, stack);
                    return;
                }
            }
        }
        list.add(stack);
    }

    private static ItemStack getTabIcon() {
        try {
            String id = CreativeTabConfig.CREATIVE_TAB_ICON.get();
            if (id != null && !id.isEmpty()) {
                ResourceLocation loc = new ResourceLocation(id);
                Item item = ForgeRegistries.ITEMS.getValue(loc);
                if (item != null && item != Items.AIR) {
                    return new ItemStack(item);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load icon from CreativeTabConfig", e);
        }
        return new ItemStack(ModItems.IRON_BUSTER_SWORD.get());
    }

    private static ItemStack createEnchantedBook(Enchantment enchantment, ForgeConfigSpec.IntValue maxLevelConfig) {
        try {
            int level = maxLevelConfig.get();
            if (level < 1)
                level = 1;
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(enchantment, level), book);
            return book;
        } catch (Exception e) {
            ItemStack fallbackBook = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(enchantment, 1), fallbackBook);
            return fallbackBook;
        }
    }

    private static void safeAddItem(List<ItemStack> list, RegistryObject<Item> itemRef) {
        try {
            Item item = itemRef.get();
            if (item != null && item != Items.AIR) {
                list.add(new ItemStack(item));
            } else {
                LOGGER.warn("Skipping null/air item in creative tab");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to add item to creative tab", e);
        }
    }

    private static void addDefaultItemsToList(List<ItemStack> list) {
        safeAddItem(list, ModItems.GAUNTLET);
        safeAddItem(list, ModItems.SHARP_STONE_BLADE);
        safeAddItem(list, ModItems.RUSTIC_WHIP);
        safeAddItem(list, ModItems.HUNTERS_BOOMERANG);
        safeAddItem(list, ModItems.FLINT_HAMMER);
        safeAddItem(list, ModItems.SHORT_BOW);
        safeAddItem(list, ModItems.NUNCHAKU);
        safeAddItem(list, ModItems.FLAT_BOW);
        safeAddItem(list, ModItems.TESSEN);
        safeAddItem(list, ModItems.HUNTERS_CROSSBOW);
        safeAddItem(list, ModItems.SHURIKEN);
        safeAddItem(list, ModItems.HUNTERS_BOW);
        safeAddItem(list, ModItems.GIANT_SHURIKEN);
        safeAddItem(list, ModItems.GREAT_CROSSBOW);
        safeAddItem(list, ModItems.PRONGED_KUNAI);
        safeAddItem(list, ModItems.COMPOUND_BOW);
        safeAddItem(list, ModItems.KUNAI);
        safeAddItem(list, ModItems.STAKE_CROSSBOW);
        safeAddItem(list, ModItems.STAKE);
        safeAddItem(list, ModItems.SHARP_STONE);
        safeAddItem(list, ModItems.WOODEN_DAGGER);
        safeAddItem(list, ModItems.WOODEN_KNUCKLE);
        safeAddItem(list, ModItems.WOODEN_CLEAVER);
        safeAddItem(list, ModItems.WOODEN_RING);
        safeAddItem(list, ModItems.WOODEN_KAMA);
        safeAddItem(list, ModItems.WOODEN_CLAW);
        safeAddItem(list, ModItems.WOODEN_MACHETE);
        safeAddItem(list, ModItems.WOODEN_KATAR);
        safeAddItem(list, ModItems.WOODEN_HAMMER);
        safeAddItem(list, ModItems.WOODEN_BATTLE_AXE);
        safeAddItem(list, ModItems.WOODEN_SICKLE);
        safeAddItem(list, ModItems.WOODEN_LONGSWORD);
        safeAddItem(list, ModItems.WOODEN_ZWEIHANDER);
        safeAddItem(list, ModItems.WOODEN_GREATSWORD);
        safeAddItem(list, ModItems.WOODEN_BROADSWORD);
        safeAddItem(list, ModItems.WOODEN_BUSTER_SWORD);
        safeAddItem(list, ModItems.WOODEN_KATANA);
        safeAddItem(list, ModItems.WOODEN_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.WOODEN_HOOK_SWORD);
        safeAddItem(list, ModItems.WOODEN_SCYTHE);
        safeAddItem(list, ModItems.WOODEN_GREATHAMMER);
        safeAddItem(list, ModItems.WOODEN_SPEAR);
        safeAddItem(list, ModItems.WOODEN_TWINBLADE);
        safeAddItem(list, ModItems.WOODEN_SAW_CLEAVER);
        safeAddItem(list, ModItems.WOODEN_SLINGSHOT);
        safeAddItem(list, ModItems.HEAVY_COMPRESSED_WOOD);
        safeAddItem(list, ModItems.DOUBLE_COMPRESSED_WOOD);
        safeAddItem(list, ModItems.STONE_DAGGER);
        safeAddItem(list, ModItems.STONE_KNUCKLE);
        safeAddItem(list, ModItems.STONE_CLEAVER);
        safeAddItem(list, ModItems.STONE_RING);
        safeAddItem(list, ModItems.STONE_KAMA);
        safeAddItem(list, ModItems.STONE_CLAW);
        safeAddItem(list, ModItems.STONE_MACHETE);
        safeAddItem(list, ModItems.STONE_KATAR);
        safeAddItem(list, ModItems.STONE_HAMMER);
        safeAddItem(list, ModItems.STONE_BATTLE_AXE);
        safeAddItem(list, ModItems.STONE_SICKLE);
        safeAddItem(list, ModItems.STONE_LONGSWORD);
        safeAddItem(list, ModItems.STONE_ZWEIHANDER);
        safeAddItem(list, ModItems.STONE_GREATSWORD);
        safeAddItem(list, ModItems.STONE_BROADSWORD);
        safeAddItem(list, ModItems.STONE_BUSTER_SWORD);
        safeAddItem(list, ModItems.STONE_KATANA);
        safeAddItem(list, ModItems.STONE_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.STONE_HOOK_SWORD);
        safeAddItem(list, ModItems.STONE_SCYTHE);
        safeAddItem(list, ModItems.STONE_GREATHAMMER);
        safeAddItem(list, ModItems.STONE_SPEAR);
        safeAddItem(list, ModItems.STONE_TWINBLADE);
        safeAddItem(list, ModItems.STONE_SAW_CLEAVER);
        safeAddItem(list, ModItems.STONE_SLINGSHOT);
        safeAddItem(list, ModItems.HEAVY_COMPRESSED_STONE);
        safeAddItem(list, ModItems.DOUBLE_COMPRESSED_STONE);
        if (ModList.get().isLoaded("copperagebackport")) {
            safeAddItem(list, ModItems.COPPER_DAGGER);
            safeAddItem(list, ModItems.COPPER_KNUCKLE);
            safeAddItem(list, ModItems.COPPER_CLEAVER);
            safeAddItem(list, ModItems.COPPER_RING);
            safeAddItem(list, ModItems.COPPER_KAMA);
            safeAddItem(list, ModItems.COPPER_CLAW);
            safeAddItem(list, ModItems.COPPER_MACHETE);
            safeAddItem(list, ModItems.COPPER_KATAR);
            safeAddItem(list, ModItems.COPPER_HAMMER);
            safeAddItem(list, ModItems.COPPER_BATTLE_AXE);
            safeAddItem(list, ModItems.COPPER_SICKLE);
            safeAddItem(list, ModItems.COPPER_LONGSWORD);
            safeAddItem(list, ModItems.COPPER_ZWEIHANDER);
            safeAddItem(list, ModItems.COPPER_GREATSWORD);
            safeAddItem(list, ModItems.COPPER_BROADSWORD);
            safeAddItem(list, ModItems.COPPER_BUSTER_SWORD);
            safeAddItem(list, ModItems.COPPER_KATANA);
            safeAddItem(list, ModItems.COPPER_BUTTERFLY_SWORD);
            safeAddItem(list, ModItems.COPPER_HOOK_SWORD);
            safeAddItem(list, ModItems.COPPER_SCYTHE);
            safeAddItem(list, ModItems.COPPER_GREATHAMMER);
            safeAddItem(list, ModItems.COPPER_SPEAR);
            safeAddItem(list, ModItems.COPPER_TWINBLADE);
            safeAddItem(list, ModItems.COPPER_SAW_CLEAVER);
            safeAddItem(list, ModItems.COPPER_PISTOL);
            safeAddItem(list, ModItems.HEAVY_COPPER_INGOT);
            safeAddItem(list, ModItems.DOUBLE_COPPER_INGOT);
        }
        safeAddItem(list, ModItems.IRON_DAGGER);
        safeAddItem(list, ModItems.IRON_KNUCKLE);
        safeAddItem(list, ModItems.IRON_CLEAVER);
        safeAddItem(list, ModItems.IRON_RING);
        safeAddItem(list, ModItems.IRON_KAMA);
        safeAddItem(list, ModItems.IRON_CLAW);
        safeAddItem(list, ModItems.IRON_MACHETE);
        safeAddItem(list, ModItems.IRON_KATAR);
        safeAddItem(list, ModItems.IRON_HAMMER);
        safeAddItem(list, ModItems.IRON_BATTLE_AXE);
        safeAddItem(list, ModItems.IRON_SICKLE);
        safeAddItem(list, ModItems.IRON_LONGSWORD);
        safeAddItem(list, ModItems.IRON_ZWEIHANDER);
        safeAddItem(list, ModItems.IRON_GREATSWORD);
        safeAddItem(list, ModItems.IRON_BROADSWORD);
        safeAddItem(list, ModItems.IRON_BUSTER_SWORD);
        safeAddItem(list, ModItems.IRON_KATANA);
        safeAddItem(list, ModItems.IRON_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.IRON_HOOK_SWORD);
        safeAddItem(list, ModItems.IRON_SCYTHE);
        safeAddItem(list, ModItems.IRON_GREATHAMMER);
        safeAddItem(list, ModItems.IRON_SPEAR);
        safeAddItem(list, ModItems.IRON_TWINBLADE);
        safeAddItem(list, ModItems.IRON_SAW_CLEAVER);
        safeAddItem(list, ModItems.IRON_SCATTERGUN);
        safeAddItem(list, ModItems.HEAVY_IRON_INGOT);
        safeAddItem(list, ModItems.DOUBLE_IRON_INGOT);
        safeAddItem(list, ModItems.GOLDEN_DAGGER);
        safeAddItem(list, ModItems.GOLDEN_KNUCKLE);
        safeAddItem(list, ModItems.GOLDEN_CLEAVER);
        safeAddItem(list, ModItems.GOLDEN_RING);
        safeAddItem(list, ModItems.GOLDEN_KAMA);
        safeAddItem(list, ModItems.GOLDEN_CLAW);
        safeAddItem(list, ModItems.GOLDEN_MACHETE);
        safeAddItem(list, ModItems.GOLDEN_KATAR);
        safeAddItem(list, ModItems.GOLDEN_HAMMER);
        safeAddItem(list, ModItems.GOLDEN_BATTLE_AXE);
        safeAddItem(list, ModItems.GOLDEN_SICKLE);
        safeAddItem(list, ModItems.GOLDEN_LONGSWORD);
        safeAddItem(list, ModItems.GOLDEN_ZWEIHANDER);
        safeAddItem(list, ModItems.GOLDEN_GREATSWORD);
        safeAddItem(list, ModItems.GOLDEN_BROADSWORD);
        safeAddItem(list, ModItems.GOLDEN_BUSTER_SWORD);
        safeAddItem(list, ModItems.GOLDEN_KATANA);
        safeAddItem(list, ModItems.GOLDEN_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.GOLDEN_HOOK_SWORD);
        safeAddItem(list, ModItems.GOLDEN_SCYTHE);
        safeAddItem(list, ModItems.GOLDEN_GREATHAMMER);
        safeAddItem(list, ModItems.GOLDEN_SPEAR);
        safeAddItem(list, ModItems.GOLDEN_TWINBLADE);
        safeAddItem(list, ModItems.GOLDEN_SAW_CLEAVER);
        safeAddItem(list, ModItems.GOLDEN_PISTOL);
        safeAddItem(list, ModItems.HEAVY_GOLD_INGOT);
        safeAddItem(list, ModItems.DOUBLE_GOLD_INGOT);
        if (ModList.get().isLoaded("cavesanddepths")) {
            safeAddItem(list, ModItems.ROSEGOLD_DAGGER);
            safeAddItem(list, ModItems.ROSEGOLD_KNUCKLE);
            safeAddItem(list, ModItems.ROSEGOLD_CLEAVER);
            safeAddItem(list, ModItems.ROSEGOLD_RING);
            safeAddItem(list, ModItems.ROSEGOLD_KAMA);
            safeAddItem(list, ModItems.ROSEGOLD_CLAW);
            safeAddItem(list, ModItems.ROSEGOLD_MACHETE);
            safeAddItem(list, ModItems.ROSEGOLD_KATAR);
            safeAddItem(list, ModItems.ROSEGOLD_HAMMER);
            safeAddItem(list, ModItems.ROSEGOLD_BATTLE_AXE);
            safeAddItem(list, ModItems.ROSEGOLD_SICKLE);
            safeAddItem(list, ModItems.ROSEGOLD_LONGSWORD);
            safeAddItem(list, ModItems.ROSEGOLD_ZWEIHANDER);
            safeAddItem(list, ModItems.ROSEGOLD_GREATSWORD);
            safeAddItem(list, ModItems.ROSEGOLD_BROADSWORD);
            safeAddItem(list, ModItems.ROSEGOLD_BUSTER_SWORD);
            safeAddItem(list, ModItems.ROSEGOLD_KATANA);
            safeAddItem(list, ModItems.ROSEGOLD_BUTTERFLY_SWORD);
            safeAddItem(list, ModItems.ROSEGOLD_HOOK_SWORD);
            safeAddItem(list, ModItems.ROSEGOLD_SCYTHE);
            safeAddItem(list, ModItems.ROSEGOLD_GREATHAMMER);
            safeAddItem(list, ModItems.ROSEGOLD_SPEAR);
            safeAddItem(list, ModItems.ROSEGOLD_TWINBLADE);
            safeAddItem(list, ModItems.ROSEGOLD_SAW_CLEAVER);
            safeAddItem(list, ModItems.ROSEGOLD_PISTOL);
            safeAddItem(list, ModItems.HEAVY_ROSEGOLD_INGOT);
            safeAddItem(list, ModItems.DOUBLE_ROSEGOLD_INGOT);
        }
        if (ModList.get().isLoaded("jaams_shinerite")) {
            safeAddItem(list, ModItems.SHINERITE_DAGGER);
            safeAddItem(list, ModItems.SHINERITE_KNUCKLE);
            safeAddItem(list, ModItems.SHINERITE_CLEAVER);
            safeAddItem(list, ModItems.SHINERITE_RING);
            safeAddItem(list, ModItems.SHINERITE_KAMA);
            safeAddItem(list, ModItems.SHINERITE_CLAW);
            safeAddItem(list, ModItems.SHINERITE_MACHETE);
            safeAddItem(list, ModItems.SHINERITE_KATAR);
            safeAddItem(list, ModItems.SHINERITE_HAMMER);
            safeAddItem(list, ModItems.SHINERITE_BATTLE_AXE);
            safeAddItem(list, ModItems.SHINERITE_SICKLE);
            safeAddItem(list, ModItems.SHINERITE_LONGSWORD);
            safeAddItem(list, ModItems.SHINERITE_ZWEIHANDER);
            safeAddItem(list, ModItems.SHINERITE_GREATSWORD);
            safeAddItem(list, ModItems.SHINERITE_BROADSWORD);
            safeAddItem(list, ModItems.SHINERITE_BUSTER_SWORD);
            safeAddItem(list, ModItems.SHINERITE_KATANA);
            safeAddItem(list, ModItems.SHINERITE_BUTTERFLY_SWORD);
            safeAddItem(list, ModItems.SHINERITE_HOOK_SWORD);
            safeAddItem(list, ModItems.SHINERITE_SCYTHE);
            safeAddItem(list, ModItems.SHINERITE_GREATHAMMER);
            safeAddItem(list, ModItems.SHINERITE_SPEAR);
            safeAddItem(list, ModItems.SHINERITE_TWINBLADE);
            safeAddItem(list, ModItems.SHINERITE_SAW_CLEAVER);
            safeAddItem(list, ModItems.SHINERITE_PISTOL);
            safeAddItem(list, ModItems.HEAVY_SHINERITE_INGOT);
            safeAddItem(list, ModItems.DOUBLE_SHINERITE_INGOT);
        }
        if (ModList.get().isLoaded("oreganized")) {
            safeAddItem(list, ModItems.ELECTRUM_DAGGER);
            safeAddItem(list, ModItems.ELECTRUM_KNUCKLE);
            safeAddItem(list, ModItems.ELECTRUM_CLEAVER);
            safeAddItem(list, ModItems.ELECTRUM_RING);
            safeAddItem(list, ModItems.ELECTRUM_KAMA);
            safeAddItem(list, ModItems.ELECTRUM_CLAW);
            safeAddItem(list, ModItems.ELECTRUM_MACHETE);
            safeAddItem(list, ModItems.ELECTRUM_KATAR);
            safeAddItem(list, ModItems.ELECTRUM_HAMMER);
            safeAddItem(list, ModItems.ELECTRUM_BATTLE_AXE);
            safeAddItem(list, ModItems.ELECTRUM_SICKLE);
            safeAddItem(list, ModItems.ELECTRUM_LONGSWORD);
            safeAddItem(list, ModItems.ELECTRUM_ZWEIHANDER);
            safeAddItem(list, ModItems.ELECTRUM_GREATSWORD);
            safeAddItem(list, ModItems.ELECTRUM_BROADSWORD);
            safeAddItem(list, ModItems.ELECTRUM_BUSTER_SWORD);
            safeAddItem(list, ModItems.ELECTRUM_KATANA);
            safeAddItem(list, ModItems.ELECTRUM_BUTTERFLY_SWORD);
            safeAddItem(list, ModItems.ELECTRUM_HOOK_SWORD);
            safeAddItem(list, ModItems.ELECTRUM_SCYTHE);
            safeAddItem(list, ModItems.ELECTRUM_GREATHAMMER);
            safeAddItem(list, ModItems.ELECTRUM_SPEAR);
            safeAddItem(list, ModItems.ELECTRUM_TWINBLADE);
            safeAddItem(list, ModItems.ELECTRUM_SAW_CLEAVER);
            safeAddItem(list, ModItems.ELECTRUM_SHOTGUN);
            safeAddItem(list, ModItems.HEAVY_ELECTRUM_INGOT);
            safeAddItem(list, ModItems.DOUBLE_ELECTRUM_INGOT);
        }
        safeAddItem(list, ModItems.DIAMOND_DAGGER);
        safeAddItem(list, ModItems.DIAMOND_KNUCKLE);
        safeAddItem(list, ModItems.DIAMOND_CLEAVER);
        safeAddItem(list, ModItems.DIAMOND_RING);
        safeAddItem(list, ModItems.DIAMOND_KAMA);
        safeAddItem(list, ModItems.DIAMOND_CLAW);
        safeAddItem(list, ModItems.DIAMOND_MACHETE);
        safeAddItem(list, ModItems.DIAMOND_KATAR);
        safeAddItem(list, ModItems.DIAMOND_HAMMER);
        safeAddItem(list, ModItems.DIAMOND_BATTLE_AXE);
        safeAddItem(list, ModItems.DIAMOND_SICKLE);
        safeAddItem(list, ModItems.DIAMOND_LONGSWORD);
        safeAddItem(list, ModItems.DIAMOND_ZWEIHANDER);
        safeAddItem(list, ModItems.DIAMOND_GREATSWORD);
        safeAddItem(list, ModItems.DIAMOND_BROADSWORD);
        safeAddItem(list, ModItems.DIAMOND_BUSTER_SWORD);
        safeAddItem(list, ModItems.DIAMOND_KATANA);
        safeAddItem(list, ModItems.DIAMOND_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.DIAMOND_HOOK_SWORD);
        safeAddItem(list, ModItems.DIAMOND_SCYTHE);
        safeAddItem(list, ModItems.DIAMOND_GREATHAMMER);
        safeAddItem(list, ModItems.DIAMOND_SPEAR);
        safeAddItem(list, ModItems.DIAMOND_TWINBLADE);
        safeAddItem(list, ModItems.DIAMOND_SAW_CLEAVER);
        safeAddItem(list, ModItems.DIAMOND_SHOTGUN);
        safeAddItem(list, ModItems.HEAVY_DIAMOND);
        safeAddItem(list, ModItems.DOUBLE_DIAMOND);
        safeAddItem(list, ModItems.NETHERITE_DAGGER);
        safeAddItem(list, ModItems.NETHERITE_KNUCKLE);
        safeAddItem(list, ModItems.NETHERITE_CLEAVER);
        safeAddItem(list, ModItems.NETHERITE_RING);
        safeAddItem(list, ModItems.NETHERITE_KAMA);
        safeAddItem(list, ModItems.NETHERITE_CLAW);
        safeAddItem(list, ModItems.NETHERITE_MACHETE);
        safeAddItem(list, ModItems.NETHERITE_KATAR);
        safeAddItem(list, ModItems.NETHERITE_HAMMER);
        safeAddItem(list, ModItems.NETHERITE_BATTLE_AXE);
        safeAddItem(list, ModItems.NETHERITE_SICKLE);
        safeAddItem(list, ModItems.NETHERITE_LONGSWORD);
        safeAddItem(list, ModItems.NETHERITE_ZWEIHANDER);
        safeAddItem(list, ModItems.NETHERITE_GREATSWORD);
        safeAddItem(list, ModItems.NETHERITE_BROADSWORD);
        safeAddItem(list, ModItems.NETHERITE_BUSTER_SWORD);
        safeAddItem(list, ModItems.NETHERITE_KATANA);
        safeAddItem(list, ModItems.NETHERITE_BUTTERFLY_SWORD);
        safeAddItem(list, ModItems.NETHERITE_HOOK_SWORD);
        safeAddItem(list, ModItems.NETHERITE_SCYTHE);
        safeAddItem(list, ModItems.NETHERITE_GREATHAMMER);
        safeAddItem(list, ModItems.NETHERITE_SPEAR);
        safeAddItem(list, ModItems.NETHERITE_TWINBLADE);
        safeAddItem(list, ModItems.NETHERITE_SAW_CLEAVER);
        safeAddItem(list, ModItems.NETHERITE_SHOTGUN);
        safeAddItem(list, ModItems.HEAVY_NETHERITE_INGOT);
        safeAddItem(list, ModItems.DOUBLE_NETHERITE_INGOT);
        if (ModList.get().isLoaded("majruszsdifficulty")) {
            safeAddItem(list, ModItems.ENDERIUM_DAGGER);
            safeAddItem(list, ModItems.ENDERIUM_KNUCKLE);
            safeAddItem(list, ModItems.ENDERIUM_CLEAVER);
            safeAddItem(list, ModItems.ENDERIUM_RING);
            safeAddItem(list, ModItems.ENDERIUM_KAMA);
            safeAddItem(list, ModItems.ENDERIUM_CLAW);
            safeAddItem(list, ModItems.ENDERIUM_MACHETE);
            safeAddItem(list, ModItems.ENDERIUM_KATAR);
            safeAddItem(list, ModItems.ENDERIUM_HAMMER);
            safeAddItem(list, ModItems.ENDERIUM_BATTLE_AXE);
            safeAddItem(list, ModItems.ENDERIUM_SICKLE);
            safeAddItem(list, ModItems.ENDERIUM_LONGSWORD);
            safeAddItem(list, ModItems.ENDERIUM_ZWEIHANDER);
            safeAddItem(list, ModItems.ENDERIUM_GREATSWORD);
            safeAddItem(list, ModItems.ENDERIUM_BROADSWORD);
            safeAddItem(list, ModItems.ENDERIUM_BUSTER_SWORD);
            safeAddItem(list, ModItems.ENDERIUM_KATANA);
            safeAddItem(list, ModItems.ENDERIUM_BUTTERFLY_SWORD);
            safeAddItem(list, ModItems.ENDERIUM_HOOK_SWORD);
            safeAddItem(list, ModItems.ENDERIUM_SCYTHE);
            safeAddItem(list, ModItems.ENDERIUM_GREATHAMMER);
            safeAddItem(list, ModItems.ENDERIUM_SPEAR);
            safeAddItem(list, ModItems.ENDERIUM_TWINBLADE);
            safeAddItem(list, ModItems.ENDERIUM_SAW_CLEAVER);
            safeAddItem(list, ModItems.ENDERIUM_SHOTGUN);
            safeAddItem(list, ModItems.HEAVY_ENDERIUM_INGOT);
            safeAddItem(list, ModItems.DOUBLE_ENDERIUM_INGOT);
        }
        safeAddItem(list, ModItems.ROYAL_RAPIER);
        safeAddItem(list, ModItems.ROYAL_SWORD);
        safeAddItem(list, ModItems.ROYAL_SPEAR);
        safeAddItem(list, ModItems.ROYAL_AXE);
        safeAddItem(list, ModItems.ROYAL_BOW);
        safeAddItem(list, ModItems.ROYAL_CROSSBOW);
        safeAddItem(list, ModItems.BROOM);
        if (ModList.get().isLoaded("farmersdelight")) {
            safeAddItem(list, ModItems.RICE_BALE_BROOM);
            safeAddItem(list, ModItems.STRAW_BROOM);
        }
        if (ModList.get().isLoaded("supplementaries")) {
            safeAddItem(list, ModItems.FLAX_BALE_BROOM);
        }
        safeAddItem(list, ModItems.BOKKEN);
        safeAddItem(list, ModItems.WAR_PICK);
        safeAddItem(list, ModItems.FLINT_MALLET);
        safeAddItem(list, ModItems.SMOKE_BOMB);
        safeAddItem(list, ModItems.DYNAMITE);
        safeAddItem(list, ModItems.COPPER_MUZZLE);
        safeAddItem(list, ModItems.COPPER_QUICK_DRAW_MAGAZINE);
        safeAddItem(list, ModItems.COPPER_CHOKE);
        safeAddItem(list, ModItems.COPPER_EXTENDED_MAGAZINE);
        safeAddItem(list, ModItems.BULLET);
        safeAddItem(list, ModItems.FIRE_BULLET);
        safeAddItem(list, ModItems.HEAVY_BULLET);
        safeAddItem(list, ModItems.GLOWING_BULLET);
        safeAddItem(list, ModItems.SHARP_BULLET);
        safeAddItem(list, ModItems.ECHO_BULLET);
        safeAddItem(list, ModItems.SHOTSHELL);
        safeAddItem(list, ModItems.FIRE_SHOTSHELL);
        safeAddItem(list, ModItems.HEAVY_SHOTSHELL);
        safeAddItem(list, ModItems.GLOWING_SHOTSHELL);
        safeAddItem(list, ModItems.SHARP_SHOTSHELL);
        safeAddItem(list, ModItems.ECHO_SHOTSHELL);
        safeAddItem(list, ModItems.RAGERS_BOTTLE);
        safeAddItem(list, ModItems.ARCHERS_BOTTLE);
        safeAddItem(list, ModItems.WARRIORS_BOTTLE);
        safeAddItem(list, ModItems.LONG_STICK);
        safeAddItem(list, ModItems.SHORT_STICK);
        try {
            ItemStack laEspadaPainting = new ItemStack(Items.PAINTING);
            CompoundTag laEspadaEntityTag = laEspadaPainting.getOrCreateTagElement("EntityTag");
            Painting.storeVariant(laEspadaEntityTag, ModPaintings.LA_ESPADA.getHolder().orElseThrow());
            list.add(laEspadaPainting);
        } catch (Exception e) {
            LOGGER.warn("Failed to add La Espada painting to creative tab", e);
        }
        try {
            ItemStack seanamitePainting = new ItemStack(Items.PAINTING);
            CompoundTag seanamiteEntityTag = seanamitePainting.getOrCreateTagElement("EntityTag");
            Painting.storeVariant(seanamiteEntityTag, ModPaintings.SEANAMITE.getHolder().orElseThrow());
            list.add(seanamitePainting);
        } catch (Exception e) {
            LOGGER.warn("Failed to add Seanamite painting to creative tab", e);
        }
        if (EnchantmentsConfig.SECURE_GRIP.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.SECURE_GRIP.get(), EnchantmentsConfig.SECURE_GRIP_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Secure Grip enchanted book to creative tab", e);
            }
        }
        if (EnchantmentsConfig.OVERDRIVE.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.OVERDRIVE.get(), EnchantmentsConfig.OVERDRIVE_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Overdrive enchanted book to creative tab", e);
            }
        }
        if (EnchantmentsConfig.AFTERMATH.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.AFTERMATH.get(), EnchantmentsConfig.AFTERMATH_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Aftermath enchanted book to creative tab", e);
            }
        }
        if (EnchantmentsConfig.GHOST_CLIP.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.GHOST_CLIP.get(), EnchantmentsConfig.GHOST_CLIP_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Ghost Clip enchanted book to creative tab", e);
            }
        }
        if (EnchantmentsConfig.FRAMEGUARD.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.FRAMEGUARD.get(), EnchantmentsConfig.FRAMEGUARD_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Frameguard enchanted book to creative tab", e);
            }
        }
        if (EnchantmentsConfig.BACKBLAST.get()) {
            try {
                list.add(createEnchantedBook(ModEnchantments.BACKBLAST.get(), EnchantmentsConfig.BACKBLAST_MAX_LEVEL));
            } catch (Exception e) {
                LOGGER.warn("Failed to add Backblast enchanted book to creative tab", e);
            }
        }
    }
}
