package net.jaams.weaponry.handler.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.TradeModifierData;
import net.jaams.weaponry.loader.TradeModifierLoader;

import java.util.List;
import java.util.Random;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradeModifierHandler {
    private static final Logger LOGGER = LogManager.getLogger(TradeModifierHandler.class);

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        ResourceLocation profession = net.minecraftforge.registries.ForgeRegistries.VILLAGER_PROFESSIONS
                .getKey(event.getType());
        LOGGER.info("[TradeModifier] VillagerTradesEvent fired! profession={}", profession);
        if (profession == null) return;

        List<TradeModifierData> applicableRules = TradeModifierLoader.INSTANCE.getForProfession(profession);
        LOGGER.info("[TradeModifier] applicableRules count={}", applicableRules.size());
        if (applicableRules.isEmpty()) return;

        for (TradeModifierData rule : applicableRules) {
            
            if (!TradeModifierLoader.INSTANCE.evaluateConditions(rule, null)) {
                continue;
            }

            for (TradeModifierData.TradeEntry tradeEntry : rule.trades) {
                
                if (rule.chance < 1.0f && new Random().nextFloat() > rule.chance) {
                    continue;
                }

                
                ResourceLocation sellLoc = ResourceLocation.tryParse(tradeEntry.sell_item);
                if (sellLoc == null) {
                    JaamsWeaponryMod.LOGGER.warn("TradeModifier: Invalid sell item '{}'", tradeEntry.sell_item);
                    continue;
                }
                var sellItem = ForgeRegistries.ITEMS.getValue(sellLoc);
                if (sellItem == null || sellItem == Items.AIR) {
                    JaamsWeaponryMod.LOGGER.warn("TradeModifier: Unknown sell item '{}'", tradeEntry.sell_item);
                    continue;
                }

                ItemStack sellStack = new ItemStack(sellItem, tradeEntry.sell_count);

                
                if (tradeEntry.sell_nbt != null && !tradeEntry.sell_nbt.isEmpty()) {
                    try {
                        var element = com.google.gson.JsonParser.parseString(tradeEntry.sell_nbt);
                        if (element.isJsonObject()) {
                            String jsonStr = element.getAsJsonObject().toString();
                            sellStack.setTag(net.minecraft.nbt.TagParser.parseTag(jsonStr));
                        }
                    } catch (Exception e) {
                        JaamsWeaponryMod.LOGGER.warn("TradeModifier: Failed to parse NBT for '{}': {}",
                            tradeEntry.sell_item, e.getMessage());
                    }
                }

                
                if (tradeEntry.sell_enchantments != null) {
                    for (TradeModifierData.EnchantmentData ench : tradeEntry.sell_enchantments) {
                        ResourceLocation enchLoc = ResourceLocation.tryParse(ench.id);
                        if (enchLoc == null) continue;
                        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchLoc);
                        if (enchantment != null) {
                            int level = Math.min(ench.level, enchantment.getMaxLevel());
                            sellStack.enchant(enchantment, Math.max(1, level));
                        }
                    }
                }

                
                ResourceLocation costLoc = ResourceLocation.tryParse(tradeEntry.cost_item);
                if (costLoc == null) {
                    JaamsWeaponryMod.LOGGER.warn("TradeModifier: Invalid cost item '{}'", tradeEntry.cost_item);
                    continue;
                }
                var costItem = ForgeRegistries.ITEMS.getValue(costLoc);
                if (costItem == null || costItem == Items.AIR) {
                    JaamsWeaponryMod.LOGGER.warn("TradeModifier: Unknown cost item '{}'", tradeEntry.cost_item);
                    continue;
                }
                ItemStack costStack = new ItemStack(costItem, tradeEntry.cost_count);

                
                VillagerTrades.ItemListing listing = (trader, random) -> {
                    return new net.minecraft.world.item.trading.MerchantOffer(
                        costStack, sellStack,
                        tradeEntry.max_uses, tradeEntry.xp, 0.05f
                    );
                };

                Int2ObjectMap<List<VillagerTrades.ItemListing>> tradeLists = event.getTrades();
                if (tradeLists.containsKey(tradeEntry.villager_level)) {
                    tradeLists.get(tradeEntry.villager_level).add(listing);
                    LOGGER.info("[TradeModifier] SUCCESS: Added trade {} -> {} for {} at level {}",
                        tradeEntry.cost_item, tradeEntry.sell_item, profession, tradeEntry.villager_level);
                } else {
                    LOGGER.warn("[TradeModifier] FAILED: Invalid villager level {} for {}. Available keys: {}",
                        tradeEntry.villager_level, profession, tradeLists.keySet());
                }
            }
        }
    }
}
