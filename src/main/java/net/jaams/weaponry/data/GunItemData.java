package net.jaams.weaponry.data;

import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.loader.GunModifierLoader;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class GunItemData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public GunEntry gun = new GunEntry();
    public ShootEntry shoot = new ShootEntry();
    public SoundEntry sound = new SoundEntry();
    public ParticleEntry particle = new ParticleEntry();

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
        public String nbt_string_value;
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
        public double damage_modifier = -1.0;
        public double knockback_modifier = -1.0;
        public int piercing_modifier = -1;
        public int projectile_count = -1;
        public String fire_pattern = "";
        public double spread_angle = -1.0;
        public double projectile_speed = -1.0;
        public double inaccuracy = -1.0;
        public double cooldown = -1.0;
        public double offhand_cooldown = -1.0;
        public double recoil_distance = -1.0;
        public double crouch_recoil_reduction = -1.0;
        public double vertical_recoil_multiplier = -1.0;
        public double xrot_recoil_intensity = -1.0;
        public double shake_intensity = -1.0;
        public int shake_reset_delay = -1;
        public int ammo_consumption = -1;
        public int attachment_consumption = -1;
    }

    public static class SoundEntry {
        public String shoot_sound = "";
        public String after_shoot_sound = "";
        public String bullet_drop_sound = "";
        public float sound_volume = -1.0f;
        public float sound_pitch = -1.0f;
        public double bullet_drop_chance = -1.0;
        public int after_shot_delay = -1;
        public int empty_cooldown = -1;
    }

    public static class ParticleEntry {
        public String shot_particle = "";
        public double shot_size = -1.0;
        public double shot_distance = -1.0;
        public int particle_count = -1;
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
        GunItemData data = getData(stack).orElse(null);
        return data != null && data.gun != null ? data.gun : new GunEntry();
    }

    public static ShootEntry getShootData(ItemStack stack) {
        GunItemData data = getData(stack).orElse(null);
        return data != null && data.shoot != null ? data.shoot : new ShootEntry();
    }

    public static ParticleEntry getParticleData(ItemStack stack) {
        GunItemData data = getData(stack).orElse(null);
        return data != null && data.particle != null ? data.particle : new ParticleEntry();
    }
}
