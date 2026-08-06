package net.jaams.weaponry.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

public class TradeModifierData {
    public Boolean enabled = true;
    public float chance = 1.0f;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public List<String> target = new ArrayList<>();
    public List<TradeEntry> trades = new ArrayList<>();

    public static class Condition {
        public String type;
        public String mod_id;
        public String advancement;
        public String difficulty;
        public String dimension;
        public String gamestage;
    }

    public static class TradeEntry {
        public String sell_item;
        public int sell_count = 1;
        public String sell_nbt = "";
        /**
         * Data-component section (1.21.1+), applied like the {@code components}
         * section of an {@code item_modifier} file.
         */
        public Map<String, JsonElement> components = new LinkedHashMap<>();
        public List<EnchantmentData> sell_enchantments = new ArrayList<>();

        public String cost_item = "minecraft:emerald";
        public int cost_count = 1;

        public int max_uses = 12;
        public int xp = 1;
        public int villager_level = 1;
        public boolean price_multiplier_affects_price = true;
    }

    public static class EnchantmentData {
        public String id;
        public int level = 1;
    }
}
