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

    private static void addDefaultItemsToList(List<ItemStack> list) {
        list.add(new ItemStack(ModItems.GAUNTLET.get()));
        list.add(new ItemStack(ModItems.SHARP_STONE_BLADE.get()));
        list.add(new ItemStack(ModItems.RUSTIC_WHIP.get()));
        list.add(new ItemStack(ModItems.HUNTERS_BOOMERANG.get()));
        list.add(new ItemStack(ModItems.FLINT_HAMMER.get()));
        list.add(new ItemStack(ModItems.SHORT_BOW.get()));
        list.add(new ItemStack(ModItems.NUNCHAKU.get()));
        list.add(new ItemStack(ModItems.FLAT_BOW.get()));
        list.add(new ItemStack(ModItems.TESSEN.get()));
        list.add(new ItemStack(ModItems.HUNTERS_CROSSBOW.get()));
        list.add(new ItemStack(ModItems.SHURIKEN.get()));
        list.add(new ItemStack(ModItems.HUNTERS_BOW.get()));
        list.add(new ItemStack(ModItems.GIANT_SHURIKEN.get()));
        list.add(new ItemStack(ModItems.GREAT_CROSSBOW.get()));
        list.add(new ItemStack(ModItems.PRONGED_KUNAI.get()));
        list.add(new ItemStack(ModItems.COMPOUND_BOW.get()));
        list.add(new ItemStack(ModItems.KUNAI.get()));
        list.add(new ItemStack(ModItems.STAKE_CROSSBOW.get()));
        list.add(new ItemStack(ModItems.STAKE.get()));
        list.add(new ItemStack(ModItems.SHARP_STONE.get()));
        list.add(new ItemStack(ModItems.WOODEN_DAGGER.get()));
        list.add(new ItemStack(ModItems.WOODEN_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.WOODEN_CLEAVER.get()));
        list.add(new ItemStack(ModItems.WOODEN_RING.get()));
        list.add(new ItemStack(ModItems.WOODEN_KAMA.get()));
        list.add(new ItemStack(ModItems.WOODEN_CLAW.get()));
        list.add(new ItemStack(ModItems.WOODEN_MACHETE.get()));
        list.add(new ItemStack(ModItems.WOODEN_KATAR.get()));
        list.add(new ItemStack(ModItems.WOODEN_HAMMER.get()));
        list.add(new ItemStack(ModItems.WOODEN_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.WOODEN_SICKLE.get()));
        list.add(new ItemStack(ModItems.WOODEN_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.WOODEN_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_KATANA.get()));
        list.add(new ItemStack(ModItems.WOODEN_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.WOODEN_SCYTHE.get()));
        list.add(new ItemStack(ModItems.WOODEN_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.WOODEN_SPEAR.get()));
        list.add(new ItemStack(ModItems.WOODEN_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.WOODEN_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.WOODEN_SLINGSHOT.get()));
        list.add(new ItemStack(ModItems.HEAVY_COMPRESSED_WOOD.get()));
        list.add(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get()));
        list.add(new ItemStack(ModItems.STONE_DAGGER.get()));
        list.add(new ItemStack(ModItems.STONE_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.STONE_CLEAVER.get()));
        list.add(new ItemStack(ModItems.STONE_RING.get()));
        list.add(new ItemStack(ModItems.STONE_KAMA.get()));
        list.add(new ItemStack(ModItems.STONE_CLAW.get()));
        list.add(new ItemStack(ModItems.STONE_MACHETE.get()));
        list.add(new ItemStack(ModItems.STONE_KATAR.get()));
        list.add(new ItemStack(ModItems.STONE_HAMMER.get()));
        list.add(new ItemStack(ModItems.STONE_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.STONE_SICKLE.get()));
        list.add(new ItemStack(ModItems.STONE_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.STONE_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.STONE_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.STONE_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.STONE_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.STONE_KATANA.get()));
        list.add(new ItemStack(ModItems.STONE_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.STONE_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.STONE_SCYTHE.get()));
        list.add(new ItemStack(ModItems.STONE_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.STONE_SPEAR.get()));
        list.add(new ItemStack(ModItems.STONE_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.STONE_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.STONE_SLINGSHOT.get()));
        list.add(new ItemStack(ModItems.HEAVY_COMPRESSED_STONE.get()));
        list.add(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get()));
        if (ModList.get().isLoaded("leafscopperbackport") || ModList.get().isLoaded("copperagebackport")) {
            list.add(new ItemStack(ModItems.COPPER_DAGGER.get()));
            list.add(new ItemStack(ModItems.COPPER_KNUCKLE.get()));
            list.add(new ItemStack(ModItems.COPPER_CLEAVER.get()));
            list.add(new ItemStack(ModItems.COPPER_RING.get()));
            list.add(new ItemStack(ModItems.COPPER_KAMA.get()));
            list.add(new ItemStack(ModItems.COPPER_CLAW.get()));
            list.add(new ItemStack(ModItems.COPPER_MACHETE.get()));
            list.add(new ItemStack(ModItems.COPPER_KATAR.get()));
            list.add(new ItemStack(ModItems.COPPER_HAMMER.get()));
            list.add(new ItemStack(ModItems.COPPER_BATTLE_AXE.get()));
            list.add(new ItemStack(ModItems.COPPER_SICKLE.get()));
            list.add(new ItemStack(ModItems.COPPER_LONGSWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_ZWEIHANDER.get()));
            list.add(new ItemStack(ModItems.COPPER_GREATSWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_BROADSWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_BUSTER_SWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_KATANA.get()));
            list.add(new ItemStack(ModItems.COPPER_BUTTERFLY_SWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_HOOK_SWORD.get()));
            list.add(new ItemStack(ModItems.COPPER_SCYTHE.get()));
            list.add(new ItemStack(ModItems.COPPER_GREATHAMMER.get()));
            list.add(new ItemStack(ModItems.COPPER_SPEAR.get()));
            list.add(new ItemStack(ModItems.COPPER_TWINBLADE.get()));
            list.add(new ItemStack(ModItems.COPPER_SAW_CLEAVER.get()));
            list.add(new ItemStack(ModItems.COPPER_PISTOL.get()));
            list.add(new ItemStack(ModItems.HEAVY_COPPER_INGOT.get()));
            list.add(new ItemStack(ModItems.DOUBLE_COPPER_INGOT.get()));
        }
        list.add(new ItemStack(ModItems.IRON_DAGGER.get()));
        list.add(new ItemStack(ModItems.IRON_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.IRON_CLEAVER.get()));
        list.add(new ItemStack(ModItems.IRON_RING.get()));
        list.add(new ItemStack(ModItems.IRON_KAMA.get()));
        list.add(new ItemStack(ModItems.IRON_CLAW.get()));
        list.add(new ItemStack(ModItems.IRON_MACHETE.get()));
        list.add(new ItemStack(ModItems.IRON_KATAR.get()));
        list.add(new ItemStack(ModItems.IRON_HAMMER.get()));
        list.add(new ItemStack(ModItems.IRON_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.IRON_SICKLE.get()));
        list.add(new ItemStack(ModItems.IRON_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.IRON_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.IRON_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.IRON_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.IRON_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.IRON_KATANA.get()));
        list.add(new ItemStack(ModItems.IRON_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.IRON_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.IRON_SCYTHE.get()));
        list.add(new ItemStack(ModItems.IRON_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.IRON_SPEAR.get()));
        list.add(new ItemStack(ModItems.IRON_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.IRON_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.IRON_SCATTERGUN.get()));
        list.add(new ItemStack(ModItems.HEAVY_IRON_INGOT.get()));
        list.add(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get()));
        list.add(new ItemStack(ModItems.GOLDEN_DAGGER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_CLEAVER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_RING.get()));
        list.add(new ItemStack(ModItems.GOLDEN_KAMA.get()));
        list.add(new ItemStack(ModItems.GOLDEN_CLAW.get()));
        list.add(new ItemStack(ModItems.GOLDEN_MACHETE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_KATAR.get()));
        list.add(new ItemStack(ModItems.GOLDEN_HAMMER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_SICKLE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_KATANA.get()));
        list.add(new ItemStack(ModItems.GOLDEN_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.GOLDEN_SCYTHE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_SPEAR.get()));
        list.add(new ItemStack(ModItems.GOLDEN_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.GOLDEN_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.GOLDEN_PISTOL.get()));
        list.add(new ItemStack(ModItems.HEAVY_GOLD_INGOT.get()));
        list.add(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get()));
        if (ModList.get().isLoaded("cavesanddepths") || ModList.get().isLoaded("oooh_pinky")
                || ModList.get().isLoaded("justrosegold")) {
            list.add(new ItemStack(ModItems.ROSEGOLD_DAGGER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_KNUCKLE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_RING.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_KAMA.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_CLAW.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_MACHETE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_KATAR.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_HAMMER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_BATTLE_AXE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_SICKLE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_LONGSWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_ZWEIHANDER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_GREATSWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_BROADSWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_BUSTER_SWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_KATANA.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_BUTTERFLY_SWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_HOOK_SWORD.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_SCYTHE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_GREATHAMMER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_SPEAR.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_TWINBLADE.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_SAW_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ROSEGOLD_PISTOL.get()));
            list.add(new ItemStack(ModItems.HEAVY_ROSEGOLD_INGOT.get()));
            list.add(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get()));
        }
        if (ModList.get().isLoaded("jaams_shinerite")) {
            list.add(new ItemStack(ModItems.SHINERITE_DAGGER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_KNUCKLE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_CLEAVER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_RING.get()));
            list.add(new ItemStack(ModItems.SHINERITE_KAMA.get()));
            list.add(new ItemStack(ModItems.SHINERITE_CLAW.get()));
            list.add(new ItemStack(ModItems.SHINERITE_MACHETE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_KATAR.get()));
            list.add(new ItemStack(ModItems.SHINERITE_HAMMER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_BATTLE_AXE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_SICKLE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_LONGSWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_ZWEIHANDER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_GREATSWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_BROADSWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_BUSTER_SWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_KATANA.get()));
            list.add(new ItemStack(ModItems.SHINERITE_BUTTERFLY_SWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_HOOK_SWORD.get()));
            list.add(new ItemStack(ModItems.SHINERITE_SCYTHE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_GREATHAMMER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_SPEAR.get()));
            list.add(new ItemStack(ModItems.SHINERITE_TWINBLADE.get()));
            list.add(new ItemStack(ModItems.SHINERITE_SAW_CLEAVER.get()));
            list.add(new ItemStack(ModItems.SHINERITE_PISTOL.get()));
            list.add(new ItemStack(ModItems.HEAVY_SHINERITE_INGOT.get()));
            list.add(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get()));
        }
        if (ModList.get().isLoaded("oreganized")) {
            list.add(new ItemStack(ModItems.ELECTRUM_DAGGER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_KNUCKLE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_RING.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_KAMA.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_CLAW.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_MACHETE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_KATAR.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_HAMMER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_BATTLE_AXE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_SICKLE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_LONGSWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_ZWEIHANDER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_GREATSWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_BROADSWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_BUSTER_SWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_KATANA.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_BUTTERFLY_SWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_HOOK_SWORD.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_SCYTHE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_GREATHAMMER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_SPEAR.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_TWINBLADE.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_SAW_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ELECTRUM_SHOTGUN.get()));
            list.add(new ItemStack(ModItems.HEAVY_ELECTRUM_INGOT.get()));
            list.add(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get()));
        }
        list.add(new ItemStack(ModItems.DIAMOND_DAGGER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_CLEAVER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_RING.get()));
        list.add(new ItemStack(ModItems.DIAMOND_KAMA.get()));
        list.add(new ItemStack(ModItems.DIAMOND_CLAW.get()));
        list.add(new ItemStack(ModItems.DIAMOND_MACHETE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_KATAR.get()));
        list.add(new ItemStack(ModItems.DIAMOND_HAMMER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_SICKLE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_KATANA.get()));
        list.add(new ItemStack(ModItems.DIAMOND_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.DIAMOND_SCYTHE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_SPEAR.get()));
        list.add(new ItemStack(ModItems.DIAMOND_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.DIAMOND_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.DIAMOND_SHOTGUN.get()));
        list.add(new ItemStack(ModItems.HEAVY_DIAMOND.get()));
        list.add(new ItemStack(ModItems.DOUBLE_DIAMOND.get()));
        list.add(new ItemStack(ModItems.NETHERITE_DAGGER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_KNUCKLE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_CLEAVER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_RING.get()));
        list.add(new ItemStack(ModItems.NETHERITE_KAMA.get()));
        list.add(new ItemStack(ModItems.NETHERITE_CLAW.get()));
        list.add(new ItemStack(ModItems.NETHERITE_MACHETE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_KATAR.get()));
        list.add(new ItemStack(ModItems.NETHERITE_HAMMER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_BATTLE_AXE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_SICKLE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_LONGSWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_ZWEIHANDER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_GREATSWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_BROADSWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_BUSTER_SWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_KATANA.get()));
        list.add(new ItemStack(ModItems.NETHERITE_BUTTERFLY_SWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_HOOK_SWORD.get()));
        list.add(new ItemStack(ModItems.NETHERITE_SCYTHE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_GREATHAMMER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_SPEAR.get()));
        list.add(new ItemStack(ModItems.NETHERITE_TWINBLADE.get()));
        list.add(new ItemStack(ModItems.NETHERITE_SAW_CLEAVER.get()));
        list.add(new ItemStack(ModItems.NETHERITE_SHOTGUN.get()));
        list.add(new ItemStack(ModItems.HEAVY_NETHERITE_INGOT.get()));
        list.add(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get()));
        if (ModList.get().isLoaded("majruszsdifficulty")) {
            list.add(new ItemStack(ModItems.ENDERIUM_DAGGER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_KNUCKLE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_RING.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_KAMA.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_CLAW.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_MACHETE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_KATAR.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_HAMMER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_BATTLE_AXE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_SICKLE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_LONGSWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_ZWEIHANDER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_GREATSWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_BROADSWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_BUSTER_SWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_KATANA.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_BUTTERFLY_SWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_HOOK_SWORD.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_SCYTHE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_GREATHAMMER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_SPEAR.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_TWINBLADE.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_SAW_CLEAVER.get()));
            list.add(new ItemStack(ModItems.ENDERIUM_SHOTGUN.get()));
            list.add(new ItemStack(ModItems.HEAVY_ENDERIUM_INGOT.get()));
            list.add(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get()));
        }
        list.add(new ItemStack(ModItems.ROYAL_RAPIER.get()));
        list.add(new ItemStack(ModItems.ROYAL_SWORD.get()));
        list.add(new ItemStack(ModItems.ROYAL_SPEAR.get()));
        list.add(new ItemStack(ModItems.ROYAL_AXE.get()));
        list.add(new ItemStack(ModItems.ROYAL_BOW.get()));
        list.add(new ItemStack(ModItems.ROYAL_CROSSBOW.get()));
        list.add(new ItemStack(ModItems.BROOM.get()));
        if (ModList.get().isLoaded("farmersdelight")) {
            list.add(new ItemStack(ModItems.RICE_BALE_BROOM.get()));
            list.add(new ItemStack(ModItems.STRAW_BROOM.get()));
        }
        if (ModList.get().isLoaded("supplementaries")) {
            list.add(new ItemStack(ModItems.FLAX_BALE_BROOM.get()));
        }
        list.add(new ItemStack(ModItems.BOKKEN.get()));
        list.add(new ItemStack(ModItems.WAR_PICK.get()));
        list.add(new ItemStack(ModItems.FLINT_MALLET.get()));
        list.add(new ItemStack(ModItems.SMOKE_BOMB.get()));
        list.add(new ItemStack(ModItems.DYNAMITE.get()));
        list.add(new ItemStack(ModItems.COPPER_MUZZLE.get()));
        list.add(new ItemStack(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get()));
        list.add(new ItemStack(ModItems.COPPER_CHOKE.get()));
        list.add(new ItemStack(ModItems.COPPER_EXTENDED_MAGAZINE.get()));
        list.add(new ItemStack(ModItems.BULLET.get()));
        list.add(new ItemStack(ModItems.FIRE_BULLET.get()));
        list.add(new ItemStack(ModItems.HEAVY_BULLET.get()));
        list.add(new ItemStack(ModItems.GLOWING_BULLET.get()));
        list.add(new ItemStack(ModItems.SHARP_BULLET.get()));
        list.add(new ItemStack(ModItems.ECHO_BULLET.get()));
        list.add(new ItemStack(ModItems.SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.FIRE_SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.HEAVY_SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.GLOWING_SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.SHARP_SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.ECHO_SHOTSHELL.get()));
        list.add(new ItemStack(ModItems.RAGERS_BOTTLE.get()));
        list.add(new ItemStack(ModItems.ARCHERS_BOTTLE.get()));
        list.add(new ItemStack(ModItems.WARRIORS_BOTTLE.get()));
        list.add(new ItemStack(ModItems.LONG_STICK.get()));
        list.add(new ItemStack(ModItems.SHORT_STICK.get()));
        ItemStack laEspadaPainting = new ItemStack(Items.PAINTING);
        CompoundTag laEspadaEntityTag = laEspadaPainting.getOrCreateTagElement("EntityTag");
        Painting.storeVariant(laEspadaEntityTag, ModPaintings.LA_ESPADA.getHolder().orElseThrow());
        list.add(laEspadaPainting);
        ItemStack seanamitePainting = new ItemStack(Items.PAINTING);
        CompoundTag seanamiteEntityTag = seanamitePainting.getOrCreateTagElement("EntityTag");
        Painting.storeVariant(seanamiteEntityTag, ModPaintings.SEANAMITE.getHolder().orElseThrow());
        list.add(seanamitePainting);
        if (EnchantmentsConfig.SECURE_GRIP.get()) {
            list.add(createEnchantedBook(ModEnchantments.SECURE_GRIP.get(), EnchantmentsConfig.SECURE_GRIP_MAX_LEVEL));
        }
        if (EnchantmentsConfig.OVERDRIVE.get()) {
            list.add(createEnchantedBook(ModEnchantments.OVERDRIVE.get(), EnchantmentsConfig.OVERDRIVE_MAX_LEVEL));
        }
        if (EnchantmentsConfig.AFTERMATH.get()) {
            list.add(createEnchantedBook(ModEnchantments.AFTERMATH.get(), EnchantmentsConfig.AFTERMATH_MAX_LEVEL));
        }
        if (EnchantmentsConfig.GHOST_CLIP.get()) {
            list.add(createEnchantedBook(ModEnchantments.GHOST_CLIP.get(), EnchantmentsConfig.GHOST_CLIP_MAX_LEVEL));
        }
        if (EnchantmentsConfig.FRAMEGUARD.get()) {
            list.add(createEnchantedBook(ModEnchantments.FRAMEGUARD.get(), EnchantmentsConfig.FRAMEGUARD_MAX_LEVEL));
        }
        if (EnchantmentsConfig.BACKBLAST.get()) {
            list.add(createEnchantedBook(ModEnchantments.BACKBLAST.get(), EnchantmentsConfig.BACKBLAST_MAX_LEVEL));
        }
    }
}
