package net.jaams.weaponry.data;

import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.loader.RangedModifierLoader;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class RangedItemData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public RangedEntry ranged = new RangedEntry();
    public AmmoEntry ammo = new AmmoEntry();
    public EnchantEntry enchant = new EnchantEntry();
    public ProjectileEntry projectile = new ProjectileEntry();

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
    }

    public static class RangedEntry {
        public String ranged_type = "SLINGSHOT";
        public Boolean ranged_enabled = null;
        public Float base_damage = null;
        public Float power_damage_bonus = null;
        public Float base_knockback = null;
        public Float min_speed = null;
        public Float max_speed = null;
        public Integer max_draw_duration = null;
        public Integer min_draw_ticks = null;
        public Float multishot_spread = null;
        public Float forward_offset = null;
        public String use_animation = null;
        public String load_sound = null;
        public String shoot_sound = null;
        public Float shoot_sound_volume = null;
        public Float shoot_sound_pitch = null;
    }

    public static class AmmoEntry {
        public List<String> ammo_items = null;
        public Boolean ammo_from_inventory = null;
        public String default_creative_ammo = null;
        public Boolean can_place_block = null;
    }

    public static class EnchantEntry {
        public Float power_enchant_damage_per_level = null;
        public Float punch_enchant_knockback_per_level = null;
        public Integer flame_fire_seconds = null;
    }

    public static class ProjectileEntry {
        public Integer despawn_ticks = null;
        public Float water_inertia = null;
        public Boolean allow_criticals = null;
        public Boolean break_on_entity_hit = null;
        public Boolean break_on_block_hit = null;
        public Float hitbox_width = null;
        public Float hitbox_height = null;
        public Boolean multishot_can_place_block = null;
        public List<String> placeable_blocks = null;
    }

    public static Optional<RangedItemData> getData(ItemStack stack) {
        return RangedModifierLoader.INSTANCE.getDataForStack(stack);
    }

    public static boolean isRangedType(ItemStack stack, String type) {
        if (stack == null || stack.isEmpty() || type == null)
            return false;
        Optional<RangedItemData> data = getData(stack);
        return data.isPresent() && data.get().ranged != null
                && type.equalsIgnoreCase(data.get().ranged.ranged_type);
    }

    public static boolean isSlingshot(ItemStack stack) {
        return isRangedType(stack, "SLINGSHOT");
    }

    public static RangedEntry getRangedData(ItemStack stack) {
        RangedItemData data = getData(stack).orElse(null);
        return data != null && data.ranged != null ? data.ranged : new RangedEntry();
    }

    public static AmmoEntry getAmmoData(ItemStack stack) {
        RangedItemData data = getData(stack).orElse(null);
        return data != null && data.ammo != null ? data.ammo : new AmmoEntry();
    }

    public static EnchantEntry getEnchantData(ItemStack stack) {
        RangedItemData data = getData(stack).orElse(null);
        return data != null && data.enchant != null ? data.enchant : new EnchantEntry();
    }

    public static ProjectileEntry getProjectileData(ItemStack stack) {
        RangedItemData data = getData(stack).orElse(null);
        return data != null && data.projectile != null ? data.projectile : new ProjectileEntry();
    }
}
