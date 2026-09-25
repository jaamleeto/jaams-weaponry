package net.jaams.weaponry.item.tiered;

import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.jaams.weaponry.item.shinerite.ShineriteAxeItem;
import net.jaams.weaponry.item.shinerite.ShineritePickaxeItem;
import net.jaams.weaponry.item.shinerite.ShineriteSimpleItem;
import net.jaams.weaponry.item.shinerite.ShineriteSwordItem;


public class TieredWeapons {

    private TieredWeapons() {
    }

    

    private static Tier makeTier(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient) {
        return new Tier() {
            @Override
            public int getUses() {
                return uses;
            }

            @Override
            public float getSpeed() {
                return speed;
            }

            @Override
            public float getAttackDamageBonus() {
                return attackDamageBonus;
            }

            @Override
            public TagKey<Block> getIncorrectBlocksForDrops() {
                return switch (level) {
                    case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
                    case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
                    case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
                    case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
                    default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
                };
            }

            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairIngredient;
            }
        };
    }

    private static Item.Properties props(boolean fireResistant) {
        return fireResistant ? new Item.Properties().fireResistant() : new Item.Properties();
    }

    

    public static SwordItem sword(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new SwordItem(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static SwordItem sword(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed, boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new SwordItem(tier, props(fireResistant).attributes(SwordItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    

    public static AxeItem axe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new AxeItem(tier, new Item.Properties().attributes(AxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static AxeItem axe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed, boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new AxeItem(tier, props(fireResistant).attributes(AxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    

    public static PickaxeItem pickaxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new PickaxeItem(tier, new Item.Properties().attributes(PickaxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static PickaxeItem pickaxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed, boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new PickaxeItem(tier, props(fireResistant).attributes(PickaxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    

    public static Item simpleItem(int durability, int enchantmentValue) {
        return new Item(new Item.Properties().durability(durability)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }

    public static Item simpleItem(int durability, int enchantmentValue, Rarity rarity) {
        return new Item(new Item.Properties().durability(durability).rarity(rarity)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }

    public static Item simpleItem(int durability, int enchantmentValue, boolean fireResistant) {
        return new Item(props(fireResistant).durability(durability)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }

    	

    public static SwordItem shineriteSword(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineriteSwordItem(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static SwordItem shineriteSword(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed,
            boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineriteSwordItem(tier, props(fireResistant).attributes(SwordItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static AxeItem shineriteAxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineriteAxeItem(tier, new Item.Properties().attributes(AxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static AxeItem shineriteAxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed, boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineriteAxeItem(tier, props(fireResistant).attributes(AxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static PickaxeItem shineritePickaxe(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineritePickaxeItem(tier, new Item.Properties().attributes(PickaxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static PickaxeItem shineritePickaxe(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed,
            boolean fireResistant) {
        Tier tier = makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient);
        return new ShineritePickaxeItem(tier, props(fireResistant).attributes(PickaxeItem.createAttributes(tier, attackModifier, attackSpeed)));
    }

    public static Item shineriteSimpleItem(int durability, int enchantmentValue) {
        return new ShineriteSimpleItem(new Item.Properties().durability(durability)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }

    public static Item shineriteSimpleItem(int durability, int enchantmentValue, Rarity rarity) {
        return new ShineriteSimpleItem(new Item.Properties().durability(durability).rarity(rarity)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }

    public static Item shineriteSimpleItem(int durability, int enchantmentValue, boolean fireResistant) {
        return new ShineriteSimpleItem(props(fireResistant).durability(durability)) {
            @Override
            public int getEnchantmentValue() {
                return enchantmentValue;
            }
        };
    }
}
