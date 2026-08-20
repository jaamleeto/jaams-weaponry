package net.jaams.weaponry.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;

import net.jaams.weaponry.loader.ThrowableModifierLoader;

import java.util.Optional;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

public class ThrowableItemData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public ThrowableEntry throwable = new ThrowableEntry();
    public RenderEntry render = new RenderEntry();
    public TrailEntry trail = new TrailEntry();
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
        public String nbt_type;
        public int nbt_int_value;
        public boolean nbt_boolean_value;
        public short nbt_short_value;
        public long nbt_long_value;
        public String nbt_string_value;
    }

    public static class ThrowableEntry {
        public Boolean throw_enabled = null;
        public Float base_damage = null;
        public Float base_knockback = null;
        public Integer pierce_level = null;
        public String projectile = null;
        public String throw_mode = null;
        public String use_animation = null;
        public String throw_sound = null;
        public Integer use_duration_ticks = null;
        public Integer min_charge_ticks = null;
        public Integer max_charge_ticks = null;
        public Integer instant_cooldown_ticks = null;
        public Float min_speed = null;
        public Float max_speed = null;
        public Float inaccuracy = null;
        public Float critical_power_threshold = null;
        public Integer damage_on_throw = null;
        public Integer min_remaining_durability = null;
        public Boolean consume_on_throw = null;
        public Float recoil = null;
        public Float recoil_crouch_reduction = null;
        public Float recoil_vertical_multiplier = null;
        public Boolean recoil_only_fully_charged = null;
        public Float recoil_pitch_kick = null;
        public Boolean allow_multishot = null;
        public Float multishot_spread_angle = null;
        public Boolean remember_slot = null;
    }

    public static class RenderEntry {
        public Float scale = null;
        public Float yaw_offset = null;
        public Float base_z_rotation = null;
        public Float offset_x = null;
        public Float offset_y = null;
        public Float offset_z = null;
        public Float spin_speed = null;
        public String spin_axis = null;
        public Float spin_offset = null;
        public String display_context = null;
    }

    public static class TrailEntry {
        public Boolean trail_enabled = null;
        public Double spawn_rate = null;
        public String particle = null;
        public float r = 1.0f;
        public float g = 1.0f;
        public float b = 1.0f;
        public float alpha = 1.0f;
        public double speed_multiplier = 1.0;
    }

    public static class ProjectileEntry {
        public Integer despawn_ticks = null;
        public Float water_inertia = null;
        public Boolean allow_criticals = null;
        public Integer ignore_hit_ticks = null;
        public Boolean break_on_entity_hit = null;
        public Boolean break_on_block_hit = null;
        public Boolean break_on_piercing_exhausted = null;
        public Boolean break_after_max_block_breaks = null;
        public Integer max_block_breaks = null;
        public Boolean disable_shield = null;
        public Integer disable_cooldown_ticks = null;
        public Float hitbox_width = null;
        public Float hitbox_height = null;
        public Integer multishot_clone_despawn_ticks = null;
        public List<String> allowed_break_blocks = null;
        public String hit_sound = null;
        public String ground_sound = null;
        public String loyalty_sound = null;
        public Integer max_bounces = null;
        public Boolean swoosh_sound_enabled = null;
        public Integer swoosh_interval = null;
        public String swoosh_sound_id = null;
    }

    public static Optional<ThrowableItemData> getData(ItemStack stack) {
        return ThrowableModifierLoader.INSTANCE.getDataForStack(stack);
    }

    public static ThrowableEntry getThrowableData(ItemStack stack) {
        ThrowableItemData data = getData(stack).orElse(null);
        return data != null && data.throwable != null ? data.throwable : new ThrowableEntry();
    }

    public static RenderEntry getRenderConfig(ItemStack stack) {
        ThrowableItemData data = getData(stack).orElse(null);
        return data != null && data.render != null ? data.render : new RenderEntry();
    }

    public static TrailEntry getTrailConfig(ItemStack stack) {
        ThrowableItemData data = getData(stack).orElse(null);
        return data != null && data.trail != null ? data.trail : new TrailEntry();
    }

    public static ProjectileEntry getProjectileConfig(ItemStack stack) {
        ThrowableItemData data = getData(stack).orElse(null);
        return data != null && data.projectile != null ? data.projectile : new ProjectileEntry();
    }

    public static ItemDisplayContext parseDisplayContext(String context) {
        if (context == null || context.isEmpty()) {
            return ItemDisplayContext.GROUND;
        }
        try {
            return ItemDisplayContext.valueOf(context.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return ItemDisplayContext.GROUND;
        }
    }
}
