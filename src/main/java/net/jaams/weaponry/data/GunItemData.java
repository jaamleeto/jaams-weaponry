package net.jaams.weaponry.data;

import net.jaams.weaponry.condition.ConditionSource;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.loader.GunModifierLoader;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.gson.JsonElement;

public class GunItemData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public transient String id;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public GunEntry gun = new GunEntry();
    public ShootEntry shoot = new ShootEntry();
    public SoundEntry sound = new SoundEntry();
    public ParticleEntry particle = new ParticleEntry();

    public static class Condition implements ConditionSource {
        public String type;
        public String mod_id;
        public String enchantment;
        public int level = 0;
        public String key;
        public String item;
        public String tag;
        public String rarity;
        public String nbt_type;
        public String nbt_key;
        public int nbt_int_value;
        public boolean nbt_boolean_value;
        public short nbt_short_value;
        public long nbt_long_value;
        public String nbt_string_value;
        public String component;
        public JsonElement component_value;

        @Override
        public String type() { return type; }

        @Override
        public String modId() { return mod_id; }

        @Override
        public String enchantment() { return enchantment; }

        @Override
        public int level() { return level; }

        @Override
        public String key() { return key; }

        @Override
        public String nbtType() { return nbt_type != null ? nbt_type : nbt_key; }

        @Override
        public String item() { return item; }

        @Override
        public String tag() { return tag; }

        @Override
        public String rarity() { return rarity; }

        @Override
        public int nbtIntValue() { return nbt_int_value; }

        @Override
        public boolean nbtBooleanValue() { return nbt_boolean_value; }

        @Override
        public short nbtShortValue() { return nbt_short_value; }

        @Override
        public long nbtLongValue() { return nbt_long_value; }

        @Override
        public String nbtStringValue() { return nbt_string_value; }

        @Override
        public String component() { return component; }

        @Override
        public JsonElement componentValue() { return component_value; }
    }

    public static class GunEntry {
        public boolean gun_enabled = true;
        public String gun_type = "GUN";
        public Boolean open_inventory = null;
        public Boolean ammo_from_gun = null;
        public Boolean ammo_from_hand = null;
        public Boolean ammo_from_player_inventory = null;
        public Map<Integer, List<String>> slot_rules = new HashMap<>();
        public Map<Integer, Integer> slot_limits = new HashMap<>();
    }

    public static class ShootEntry {
        public Double damage_modifier = null;
        public Double knockback_modifier = null;
        public Integer piercing_modifier = null;
        public Integer projectile_count = null;
        public String fire_pattern = null;
        public Double spread_angle = null;
        public Double projectile_speed = null;
        public Double inaccuracy = null;
        public Double cooldown = null;
        public Double offhand_cooldown = null;
        public Double recoil_distance = null;
        public Double crouch_recoil_reduction = null;
        public Double vertical_recoil_multiplier = null;
        public Double xrot_recoil_intensity = null;
        public Double shake_intensity = null;
        public Integer shake_reset_delay = null;
        public Integer ammo_consumption = null;
        public Integer attachment_consumption = null;
    }

    public static class SoundEntry {
        public String shoot_sound = null;
        public String after_shoot_sound = null;
        public String bullet_drop_sound = null;
        public Float sound_volume = null;
        public Float sound_pitch = null;
        public Double bullet_drop_chance = null;
        public Integer after_shot_delay = null;
        public Integer empty_cooldown = null;
    }

    public static class ParticleEntry {
        public String shot_particle = null;
        public Double shot_size = null;
        public Double shot_distance = null;
        public Integer particle_count = null;
    }


    public static Optional<GunItemData> getData(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return Optional.empty();
        List<GunItemData> entries = GunModifierLoader.INSTANCE.getForItem(stack.getItem());
        if (entries.isEmpty())
            return Optional.empty();
        for (GunItemData entry : entries) {
            if (GunModifierLoader.INSTANCE.evaluateConditions(entry, stack)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public static GunEntry getGunData(ItemStack stack) {
        return getData(stack).map(d -> d.gun).orElse(null);
    }

    public static ShootEntry getShootData(ItemStack stack) {
        return getData(stack).map(d -> d.shoot).orElse(null);
    }

    public static ParticleEntry getParticleData(ItemStack stack) {
        return getData(stack).map(d -> d.particle).orElse(null);
    }
}
