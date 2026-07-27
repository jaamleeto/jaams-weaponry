package net.jaams.weaponry.data;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonElement;

public class ItemModifierData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public ModifierEntry modifiers = new ModifierEntry();
    public List<NbtEntry> nbt = new ArrayList<>();
    /**
     * Data-component section (1.21.1+).
     * Keys are component IDs like "minecraft:custom_name", "minecraft:unbreakable", etc.
     * Values are JSON representations parsed by each component type's codec.
     */
    public Map<String, JsonElement> components = new LinkedHashMap<>();

    public static class Condition {
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
        public short nbt_short_value;
        public long nbt_long_value;
        public String nbt_string_value;
        /** Component ID for has_component / component_value conditions (1.21.1+). */
        public String component;
        /** Expected value for component_value condition, parsed via component codec. */
        public JsonElement component_value;
    }

    public static class ModifierEntry {
        public List<String> slots = new ArrayList<>();
        public List<AttributeEntry> attributes = new ArrayList<>();
    }

    public static class AttributeEntry {
        public String attribute;
        public String name;
        public double amount;
        public String operation = "addition";
        public UUID uuid;
    }

    public static class NbtEntry {
        public String key;
        public String type = "auto";
        public JsonElement value;
        public boolean replace = false;
    }

    public boolean appliesToSlot(EquipmentSlot slot) {
        if (modifiers.slots.isEmpty())
            return true;
        return modifiers.slots.contains(slot.getName());
    }
}
