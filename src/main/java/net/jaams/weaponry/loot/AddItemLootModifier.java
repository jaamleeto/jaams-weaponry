package net.jaams.weaponry.loot;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.LootModifierData;
import net.jaams.weaponry.loader.LootModifierLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;


public class AddItemLootModifier extends LootModifier {

    public static final Supplier<Codec<AddItemLootModifier>> CODEC = Suppliers.memoize(() ->
        RecordCodecBuilder.create(inst -> codecStart(inst)
            .apply(inst, AddItemLootModifier::new)
        )
    );

    public AddItemLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (lootTableId == null) {
            return generatedLoot;
        }

        List<LootModifierData> candidates = LootModifierLoader.INSTANCE.getForLootTable(lootTableId);
        if (candidates.isEmpty()) {
            return generatedLoot;
        }

        RandomSource random = context.getRandom();

        for (LootModifierData rule : candidates) {
            
            if (!LootModifierLoader.INSTANCE.evaluateConditions(rule, context)) {
                continue;
            }

            
            if (random.nextFloat() > rule.chance) {
                continue;
            }

            boolean anyAdded = false;
            ObjectArrayList<ItemStack> added = new ObjectArrayList<>();

            for (LootModifierData.LootEntry entry : rule.entries) {
                if (random.nextFloat() > entry.chance) {
                    continue;
                }

                ResourceLocation itemLoc = ResourceLocation.tryParse(entry.item);
                if (itemLoc == null) {
                    JaamsWeaponryMod.LOGGER.warn("AddItemLootModifier: Invalid item '{}'", entry.item);
                    continue;
                }

                Item item = ForgeRegistries.ITEMS.getValue(itemLoc);
                if (item == null || item == Items.AIR) {
                    JaamsWeaponryMod.LOGGER.warn("AddItemLootModifier: Unknown item '{}', skipping", entry.item);
                    continue;
                }

                int count = entry.count_min == entry.count_max
                    ? entry.count_min
                    : entry.count_min + random.nextInt(entry.count_max - entry.count_min + 1);
                if (count <= 0) continue;

                ItemStack stack = new ItemStack(item, count);

                
                if (entry.nbt != null && !entry.nbt.isEmpty()) {
                    try {
                        JsonElement element = JsonParser.parseString(entry.nbt);
                        if (element.isJsonObject()) {
                            String jsonStr = element.getAsJsonObject().toString();
                            stack.setTag(net.minecraft.nbt.TagParser.parseTag(jsonStr));
                        }
                    } catch (Exception e) {
                        JaamsWeaponryMod.LOGGER.warn("AddItemLootModifier: Failed to parse NBT for '{}': {}",
                            entry.item, e.getMessage());
                    }
                }

                
                if (entry.enchantments != null) {
                    for (LootModifierData.EnchantmentData ench : entry.enchantments) {
                        if (random.nextFloat() > ench.chance) {
                            continue;
                        }
                        ResourceLocation enchLoc = ResourceLocation.tryParse(ench.id);
                        if (enchLoc == null) continue;
                        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchLoc);
                        if (enchantment != null) {
                            int level = Math.min(ench.level, enchantment.getMaxLevel());
                            stack.enchant(enchantment, Math.max(1, level));
                        } else {
                            JaamsWeaponryMod.LOGGER.warn("AddItemLootModifier: Unknown enchantment '{}'", ench.id);
                        }
                    }
                }

                added.add(stack);
                anyAdded = true;
            }

            if (anyAdded) {
                generatedLoot.addAll(added);
                if (rule.replace_all) {
                    Set<Item> addedItems = new HashSet<>();
                    for (ItemStack stack : added) {
                        addedItems.add(stack.getItem());
                    }
                    generatedLoot.removeIf(stack -> !addedItems.contains(stack.getItem()));
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
