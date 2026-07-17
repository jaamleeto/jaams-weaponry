package net.jaams.weaponry.item.tiered;

import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;

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
            public int getLevel() {
                return level;
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
        return new SwordItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, new Item.Properties());
    }

    public static SwordItem sword(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed, boolean fireResistant) {
        return new SwordItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, props(fireResistant));
    }

    

    public static AxeItem axe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed) {
        return new AxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, new Item.Properties());
    }

    public static AxeItem axe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed, boolean fireResistant) {
        return new AxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, props(fireResistant));
    }

    

    public static PickaxeItem pickaxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        return new PickaxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, new Item.Properties());
    }

    public static PickaxeItem pickaxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, int attackModifier, float attackSpeed, boolean fireResistant) {
        return new PickaxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, props(fireResistant));
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
        return new ShineriteSwordItem(
                makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, new Item.Properties());
    }

    public static SwordItem shineriteSword(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed,
            boolean fireResistant) {
        return new ShineriteSwordItem(
                makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, props(fireResistant));
    }

    public static AxeItem shineriteAxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed) {
        return new ShineriteAxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, new Item.Properties());
    }

    public static AxeItem shineriteAxe(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
            Ingredient repairIngredient, float attackModifier, float attackSpeed, boolean fireResistant) {
        return new ShineriteAxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue, repairIngredient),
                attackModifier, attackSpeed, props(fireResistant));
    }

    public static PickaxeItem shineritePickaxe(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed) {
        return new ShineritePickaxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue,
                repairIngredient), attackModifier, attackSpeed, new Item.Properties());
    }

    public static PickaxeItem shineritePickaxe(int uses, float speed, float attackDamageBonus, int level,
            int enchantmentValue, Ingredient repairIngredient, int attackModifier, float attackSpeed,
            boolean fireResistant) {
        return new ShineritePickaxeItem(makeTier(uses, speed, attackDamageBonus, level, enchantmentValue,
                repairIngredient), attackModifier, attackSpeed, props(fireResistant));
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
