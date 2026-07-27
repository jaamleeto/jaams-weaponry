package net.jaams.weaponry.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class EquipmentData {

    
    public List<String> entity = new ArrayList<>();

    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";

    
    public double global_chance = 1.0;

    
    public List<EntityCondition> conditions = new ArrayList<>();

    
    public List<ItemEntry> items = new ArrayList<>();

    
    
    

    public static class ItemEntry {
        
        public String item = "";

        
        public String slot = "mainhand";

        
        public int count = 1;

        
        public double chance = 1.0;

        
        public boolean replace_existing = true;

        
        public Map<String, Integer> enchantments = new HashMap<>();

        
        public boolean random_enchantments = false;

        
        public Map<String, Object> nbt = new HashMap<>();

        
        public List<ItemCondition> conditions = new ArrayList<>();

        
        public List<EquipEntry> equipment = new ArrayList<>();

        public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot() {
            return switch (slot.toLowerCase(java.util.Locale.ROOT)) {
                case "offhand" -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                case "head" -> net.minecraft.world.entity.EquipmentSlot.HEAD;
                case "chest" -> net.minecraft.world.entity.EquipmentSlot.CHEST;
                case "legs" -> net.minecraft.world.entity.EquipmentSlot.LEGS;
                case "feet" -> net.minecraft.world.entity.EquipmentSlot.FEET;
                default -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
            };
        }
    }

    
    
    

    public static class EquipEntry {
        
        public String item = "";

        
        public String slot = "offhand";

        
        public int count = 1;

        
        public boolean replace_existing = true;

        
        public Map<String, Integer> enchantments = new HashMap<>();

        
        public boolean random_enchantments = false;

        
        public Map<String, Object> nbt = new HashMap<>();

        
        public List<ItemCondition> conditions = new ArrayList<>();

        public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot() {
            return switch (slot.toLowerCase(java.util.Locale.ROOT)) {
                case "offhand" -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                case "head" -> net.minecraft.world.entity.EquipmentSlot.HEAD;
                case "chest" -> net.minecraft.world.entity.EquipmentSlot.CHEST;
                case "legs" -> net.minecraft.world.entity.EquipmentSlot.LEGS;
                case "feet" -> net.minecraft.world.entity.EquipmentSlot.FEET;
                default -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
            };
        }
    }

    
    
    

    public static class ItemCondition {
        public String type;
        public String mod_id;
        public String enchantment;
        public int level = 0;
        public String key;
        public String item;
        public String tag;
        public String rarity;
        public String nbt_key;
        public int nbt_int_value;
        public boolean nbt_boolean_value;
        public String nbt_string_value;
    }

    public static class EntityCondition {
        
        public String type;

        public String difficulty;
        public String biome;
        public int time_min = 0;
        public int time_max = 24000;
        public int light_level_min = 0;
        public int light_level_max = 15;
        public String mod_id;
        public String entity_type;
        public float health_value;
        public String effect;
        public String gamerule;
        public String gamerule_value;
        public double distance_value;
        public String structure;
        public int moon_phase_min = 0;
        public int moon_phase_max = 7;

        
        public boolean negate = false;
    }
}
