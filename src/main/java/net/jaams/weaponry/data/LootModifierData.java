package net.jaams.weaponry.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

public class LootModifierData {
    public Boolean enabled = true;
    public float chance = 1.0f;
    public boolean replace_all = false;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public List<String> loot_tables = new ArrayList<>();
    public List<String> exclude_loot_tables = new ArrayList<>();
    public List<LootEntry> entries = new ArrayList<>();

    public static class Condition {
        public String type;
        public String mod_id;
        public String advancement;
        public String difficulty;
        public String dimension;
        public String gamestage;
    }

    public static class LootEntry {
        public String item;
        public int count_min = 1;
        public int count_max = 1;
        public float chance = 1.0f;
        public String nbt = "";
        public Map<String, JsonElement> components = new LinkedHashMap<>();
        public List<EnchantmentData> enchantments = new ArrayList<>();
    }

    public static class EnchantmentData {
        public String id;
        public int level = 1;
        public float chance = 1.0f;
    }
}
