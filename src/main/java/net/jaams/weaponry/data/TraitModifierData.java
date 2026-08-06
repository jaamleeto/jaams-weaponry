package net.jaams.weaponry.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.jaams.weaponry.loader.TraitModifierLoader;
import net.minecraft.world.item.ItemStack;

import com.google.gson.JsonElement;

public class TraitModifierData {

    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();
    public List<String> active_traits = new ArrayList<>();
    public TraitsEntry traits = new TraitsEntry();
    public ProjectileTraitsEntry projectile_traits = new ProjectileTraitsEntry();

    public boolean hasTrait(String traitName) {
        if (active_traits == null)
            return false;
        return active_traits.stream().anyMatch((t) -> t.equalsIgnoreCase(traitName));
    }

    public static class Condition {

        public String type;
        public String mod_id;
        public String enchantment;
        public int level = 0;
        public String key;
        public String nbt_key;
        public String item;
        public String tag;
        public String rarity;
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

    public static class TraitsEntry {

        public PiercerStrikeEntry piercer_strike = null;
        public DuelistEntry duelist = null;
        public ThreatResponseEntry threat_response = null;
        public ReachAdvantageEntry reach_advantage = null;
        public AfterStrikeEntry after_strike = null;
        public QuickCraftingEntry quick_crafting = null;
        public AquaticGrudgeEntry aquatic_grudge = null;
        public ArthropodGrudgeEntry arthropod_grudge = null;
        public UndeadGrudgeEntry undead_grudge = null;
        public TraitorGrudgeEntry traitor_grudge = null;
        public SnoutGrudgeEntry snout_grudge = null;
        public BoneGrudgeEntry bone_grudge = null;
        public AntiAerialEntry anti_aerial = null;

        public RottenGrudgeEntry rotten_grudge = null;
        public ArmorBreakerEntry armor_breaker = null;
        public BladeBreakerEntry blade_breaker = null;
        public AcrobaticLungeEntry acrobatic_lunge = null;
        public DexterousLungeEntry dexterous_lunge = null;
        public PullLungeEntry pull_lunge = null;
        public DisengageEntry disengage = null;
        public DisarmEntry disarm = null;
        public DismountEntry dismount = null;
        public DisablingStrikeEntry disabling_strike = null;
        public ThroughStrikeEntry through_strike = null;
        public CleansingStrikeEntry cleansing_strike = null;
        public OverwhelmingStrikeEntry overwhelming_strike = null;
        public SuppressingStrikeEntry suppressing_strike = null;
        public SparringStrikeEntry sparring_strike = null;
        public HarvestSweepEntry harvest_sweep = null;
        public FragilityEntry fragility = null;
        public SlipperyEntry slippery = null;
        public ExhaustingEntry exhausting = null;
        public BrittleHandleEntry brittle_handle = null;
        public BarbedHandleEntry barbed_handle = null;
        public OverstrainEntry overstrain = null;
        public UnstableEdgeEntry unstable_edge = null;
        public DetonatingEntry detonating = null;
        public DecapitationEntry decapitation = null;
        public SlashAssaultEntry slash_assault = null;
        public PiercingAssaultEntry piercing_assault = null;
        public WhirlingStrikeEntry whirling_strike = null;
        public WildSweepEntry wild_sweep = null;
        public QuickSwapEntry quick_swap = null;
        public RapidBoostEntry rapid_boost = null;
        public PowerBoostEntry power_boost = null;
        public SmashStrikeEntry smash_strike = null;
        public ShockImpactEntry shock_impact = null;
        public BackstabEntry backstab = null;
        public HeavyHandedEntry heavy_handed = null;
        public BusterStrikeEntry buster_strike = null;
        public GuardStanceEntry guard_stance = null;
        public ParryGuardEntry parry_guard = null;
    }

    public static class ShockImpactEntry {

        public Float max_bonus_damage = null;
        public Float max_residual_damage = null;
        public Float smash_radius = null;
        public Float min_knockback_strength = null;
        public Float max_knockback_strength = null;
        public Float knockback_scaling_factor = null;
        public Integer particle_count = null;
        public Float shake_intensity = null;
        public Integer shake_reset_delay = null;
        public Integer durability_damage_base = null;
        public Integer max_durability_damage = null;
        public Float exhaustion = null;
        public Float depletion_chance = null;
        public Integer depletion_duration = null;
        public Integer depletion_level = null;
        public Integer depletion_max_level = null;
        public Integer depletion_max_duration = null;
        public Boolean enable_depletion = null;
        public Float player_vertical_impulse = null;
        public Float entity_vertical_impulse = null;
        public Integer cooldown_ticks = null;
        public Float base_damage_multiplier = null;
        public Float offhand_cooldown_multiplier = null;
        public Float offhand_power_multiplier = null;
        public String shock_impact_mode = null;
        public Integer use_duration_ticks = null;
        public String use_animation = null;
        public Integer min_charge_ticks = null;
    }

    public static class SparringStrikeEntry {
    }

    public static class RapidBoostEntry {

        public Integer max_hits = null;
        public Float increment = null;
    }

    public static class PowerBoostEntry {

        public Integer max_hits = null;
        public Float increment = null;
    }

    public static class SmashStrikeEntry {

        public Float damage_per_block = null;
        public Float max_bonus_damage = null;
        public Float residual_damage_base = null;
        public Float residual_damage_per_block = null;
        public Float max_residual_damage = null;
        public Float smash_radius = null;
        public Float shake_intensity = null;
        public Integer shake_reset_delay = null;
        public Float ally_damage_multiplier = null;
        public Integer durability_damage_base = null;
        public Float durability_damage_per_block = null;
        public Integer max_durability_damage = null;
        public Float min_fall_distance = null;
    }

    public static class QuickSwapEntry {

        public Boolean global_cooldown;
        public String target_item;
        public Integer main_hand_cooldown;
        public Integer off_hand_cooldown;
        public String sound;
        public List<String> no_cooldown_items;
        public List<String> no_cooldown_tags;
        public String activation_mode;
        public Integer use_duration_ticks;
        public String use_animation;
    }

    public static class DecapitationDrop {
        public String entity = null;
        public String item = null;
        public Float chance = null;
    }

    public static class DecapitationEntry {
        public Float general_chance = null;
        public Float critical_multiplier = null;
        public List<DecapitationDrop> drops = null;
        public String sound = null;
        public String particle = null;
    }

    public static class HarvestSweepEntry {
        public Integer range = null;
        public Integer till_range = null;
        public Integer max_blocks = null;
        public Boolean can_harvest = null;
        public Integer durability_cost_per_block = null;
        public Boolean durability_per_block = null;
        public Integer harvest_durability_cost = null;
        public Integer till_durability_cost = null;
    }

    public static class FragilityEntry {

        public Float break_chance = null;
        public Float min_durability_threshold = null;
        public RemainingItem remaining_item = null;

        public static class RemainingItem {
            public String item = null;
            public Float chance = null;
            public Integer count = null;
        }
    }

    public static class SlipperyEntry {

        public Float disarm_chance = null;
        public Float throw_distance = null;
    }

    public static class ExhaustingEntry {

        public Float exhaustion_amount = null;
    }

    public static class BrittleHandleEntry {

        public Integer extra_durability_cost = null;
    }

    public static class BarbedHandleEntry {

        public Float damage_return_factor = null;
    }

    public static class OverstrainEntry {

        public Float chance = null;
        public Integer effect_duration = null;
        public Integer effect_amplifier = null;
    }

    public static class UnstableEdgeEntry {

        public Float min_damage_multiplier = null;
        public Float max_damage_multiplier = null;
    }

    public static class DetonatingEntry {

        public Float explode_chance = null;
        public Float explosion_power = null;
        public Boolean break_blocks = null;
        public Boolean damage_owner = null;
    }

    public static class SlashAssaultEntry {

        public Boolean global_cooldown;
        public Float dash_distance = null;
        public Float slash_range = null;
        public Integer slash_cooldown = null;
        public Integer no_target_cooldown = null;
        public Integer durability_cost = null;
        public Float min_damage = null;
        public String slash_assault_mode = null;
        public Integer use_duration_ticks = null;
        public String use_animation = null;
        public Integer min_charge_ticks = null;
        public Float depletion_chance = null;
        public Integer depletion_duration = null;
        public Integer depletion_level = null;
        public Integer depletion_max_level = null;
        public Integer depletion_max_duration = null;
        public Boolean enable_depletion = null;
    }

    public static class PiercingAssaultEntry {

        public Boolean global_cooldown;
        public Float dash_distance = null;
        public Float pierce_range = null;
        public Integer pierce_cooldown = null;
        public Integer no_target_cooldown = null;
        public Float min_damage = null;
        public String piercing_assault_mode = null;
        public Integer use_duration_ticks = null;
        public String use_animation = null;
        public Integer min_charge_ticks = null;
        public Float depletion_chance = null;
        public Integer depletion_duration = null;
        public Integer depletion_level = null;
        public Integer depletion_max_level = null;
        public Integer depletion_max_duration = null;
        public Boolean enable_depletion = null;
    }

    public static class WildSweepEntry {

        public Boolean global_cooldown;
        public Integer break_radius = null;
        public Integer durability_cost = null;
        public Integer cooldown = null;
        public List<String> breakable_blocks = null;
    }

    public static class WhirlingStrikeEntry {

        public Float base_damage = null;
        public Float damage_multiplier = null;
        public Double base_attack_range = null;
        public Double dual_wield_range_multiplier = null;
        public Float dual_wield_damage_multiplier = null;
        public Float max_damage_cap = null;
        public Integer item_damage_interval = null;
        public Integer attack_interval = null;
        public Integer item_damage_amount = null;
        public Integer single_wield_block_damage = null;
        public Integer dual_wield_block_damage = null;
        public Double use_distance = null;
        public Integer particle_tick_interval = null;
    }

    public static class AquaticGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class ArthropodGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class UndeadGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class TraitorGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class SnoutGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class BoneGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class AntiAerialEntry {

        public Float bonus_damage = null;
    }

    public static class ArmorBreakerEntry {

        public Float chance = null;
        public Integer durability_damage = null;
        public List<String> slots = null;
        public List<String> immune_items = null;
    }

    public static class BladeBreakerEntry {

        public Float chance = null;
        public Integer durability_damage = null;
        public List<String> immune_items = null;
    }

    public static class AcrobaticLungeEntry {

        public Float strength = null;
    }

    public static class DexterousLungeEntry {

        public Float pull_strength = null;
        public Float attract_strength = null;
    }

    public static class PullLungeEntry {

        public Float strength = null;
    }

    public static class DisengageEntry {

        public Float strength = null;
    }

    public static class DisarmEntry {

        public Float chance = null;
        public List<String> non_disarmable_items = null;
        public List<String> non_disarmable_entities = null;
    }

    public static class DismountEntry {

        public Float chance = null;
        public List<String> non_dismountable_entities = null;
    }

    public static class DisablingStrikeEntry {

        public Float chance = null;
        public Integer cooldown = null;
    }

    public static class ThroughStrikeEntry {

        public Float chance = null;
    }

    public static class CleansingStrikeEntry {

        public Float chance = null;
        public List<String> blacklisted_effects = null;
    }

    public static class OverwhelmingStrikeEntry {

        public Float chance = null;
        public Integer duration = null;
    }

    public static class SuppressingStrikeEntry {

        public Float chance = null;
        public Integer duration = null;
    }

    public static class RottenGrudgeEntry {

        public Float bonus_damage = null;
    }

    public static class BackstabEntry {

        public Boolean global_cooldown;
        public Float multiplier_normal = null;
        public Float multiplier_sneaking = null;
        public Float multiplier_invisible = null;
        public Float multiplier_sneaking_invisible = null;
        public Integer durability_penalty = null;
        public Float weakness_chance = null;
        public Integer weakness_duration = null;
        public Integer weakness_level = null;
        public Integer right_click_cooldown = null;
        public Float darkness_bonus = null;
        public Float moving_target_penalty = null;
        public Double grace_period_seconds = null;
        public Float right_click_damage_bonus = null;
        public Float right_click_durability_multiplier = null;
        public Float right_click_forward_impulse = null;
        public Double max_distance = null;
        public Double max_angle = null;
    }

    public static class BusterStrikeEntry {

        public Boolean require_fully_charged = null;
        public Integer required_hits = null;
        public Float bonus_multiplier = null;
        public Integer durability_penalty = null;
        public Float remove_chance = null;
    }

    public static class GuardStanceEntry {

        public Boolean global_cooldown;
        public Integer cooldown_ticks = null;
        public Double area_damage_multiplier = null;
        public Double knockback_force = null;
        public Double area_range = null;
        public Double block_damage_reduction = null;
        public Integer damage_per_block = null;
        public Integer damage_on_stop = null;
        public Float particle_size = null;
        public Float particle_distance = null;
        public String block_sound = null;
        public Float no_durability_break_chance = null;
        public Boolean apply_first_person_transform = null;
    }

    public static class ParryGuardEntry {

        public Boolean global_cooldown;
        public Integer cooldown_ticks = null;
        public Double block_damage_reduction = null;
        public Integer damage_per_block = null;
        public Integer damage_on_stop = null;
        public String block_sound = null;
        public Float no_durability_break_chance = null;
    }

    public static class ProjectileTraitsEntry {

        public PiercingShotEntry piercing_shot = null;
        public BackstabShotEntry backstab_shot = null;
        public SweepingShotEntry sweeping_shot = null;
        public DisarmingShotEntry disarming_shot = null;
        public DisablingShotEntry disabling_shot = null;
        public CollectorEntry collector = null;
        public ThrowbackEntry throwback = null;
    }

    public static class PiercerStrikeEntry {

        public Float bonus_damage = null;
        public Integer min_armor = null;
        public Boolean require_fully_charged = null;
    }

    public static class DuelistEntry {

        public Float bonus_damage = null;
        public Boolean require_fully_charged = null;
    }

    public static class ThreatResponseEntry {

        public Float bonus_damage = null;
        public Boolean require_fully_charged = null;
    }

    public static class ReachAdvantageEntry {

        public Float bonus_damage = null;
        public Float min_distance = null;
        public Float max_distance = null;
        public Boolean require_fully_charged = null;
    }

    public static class AfterStrikeEntry {

        public Boolean require_fully_charged = null;
        public Integer required_hits = null;
        public Boolean critical_triggers = null;
        public Integer attack_count = null;
        public Integer attack_interval = null;
        public Float initial_modifier = null;
        public Float decay_factor = null;
    }

    public static class QuickCraftingEntry {

        public Boolean global_cooldown;
        public String ingredient = null;
        public Integer ingredient_count = null;
        public String result = null;
        public Integer result_count = null;
        public Integer use_duration = null;
        public Integer durability_cost = null;
        public Integer cooldown = null;
        public String particle = null;
        public String sound = null;
    }

    public static class PiercingShotEntry {

        public Boolean enabled = null;
        public Double chance = null;
        public Double bonus_damage = null;
    }

    public static class BackstabShotEntry {

        public Boolean enabled = null;
        public Double damage_multiplier = null;
    }

    public static class SweepingShotEntry {

        public Boolean enabled = null;
        public Double radius = null;
        public Double damage_factor = null;
    }

    public static class DisarmingShotEntry {

        public Boolean enabled = null;
        public Float chance = null;
        public Boolean require_critical = null;
        public Boolean mount_item = null;
    }

    public static class DisablingShotEntry {

        public Boolean enabled = null;
        public Float chance = null;
        public Integer cooldown = null;
    }

    public static class CollectorEntry {

        public Boolean enabled = null;
        public Integer max_mounted_entities = null;
    }

    public static class ThrowbackEntry {

        public Boolean enabled = null;
        public Boolean return_on_block_hit = null;
        public Boolean return_on_entity_hit = null;
        public Boolean return_on_max_range = null;
        public Float min_range = null;
        public Float max_range = null;
        public Double return_speed = null;
    }

    public static Optional<TraitModifierData> getData(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return Optional.empty();
        List<TraitModifierData> entries = TraitModifierLoader.INSTANCE.getForItem(stack.getItem());
        if (entries.isEmpty())
            return Optional.empty();
        TraitModifierData combinedData = null;
        for (TraitModifierData entry : entries) {
            if (TraitModifierLoader.INSTANCE.evaluateConditions(entry, stack)) {
                if (combinedData == null) {
                    combinedData = new TraitModifierData();
                }
                TraitModifierLoader.INSTANCE.mergeFrom(combinedData, entry);
            }
        }
        return Optional.ofNullable(combinedData);
    }

    public static boolean isTraitActive(ItemStack stack, String traitName) {
        return getData(stack)
                .map((d) -> d.hasTrait(traitName))
                .orElse(false);
    }

    public static Optional<PiercerStrikeEntry> getPiercerStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("piercer_strike") && d.traits.piercer_strike != null)
                .map((d) -> d.traits.piercer_strike);
    }

    public static Optional<DuelistEntry> getDuelist(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("duelist") && d.traits.duelist != null)
                .map((d) -> d.traits.duelist);
    }

    public static Optional<ThreatResponseEntry> getThreatResponse(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("threat_response") && d.traits.threat_response != null)
                .map((d) -> d.traits.threat_response);
    }

    public static Optional<ReachAdvantageEntry> getReachAdvantage(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("reach_advantage") && d.traits.reach_advantage != null)
                .map((d) -> d.traits.reach_advantage);
    }

    public static Optional<AfterStrikeEntry> getAfterStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("after_strike") && d.traits.after_strike != null)
                .map((d) -> d.traits.after_strike);
    }

    public static Optional<QuickCraftingEntry> getQuickCrafting(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("quick_crafting") && d.traits.quick_crafting != null)
                .map((d) -> d.traits.quick_crafting);
    }

    public static Optional<AquaticGrudgeEntry> getAquaticGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("aquatic_grudge") && d.traits.aquatic_grudge != null)
                .map((d) -> d.traits.aquatic_grudge);
    }

    public static Optional<ArthropodGrudgeEntry> getArthropodGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("arthropod_grudge") && d.traits.arthropod_grudge != null)
                .map((d) -> d.traits.arthropod_grudge);
    }

    public static Optional<UndeadGrudgeEntry> getUndeadGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("undead_grudge") && d.traits.undead_grudge != null)
                .map((d) -> d.traits.undead_grudge);
    }

    public static Optional<TraitorGrudgeEntry> getTraitorGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("traitor_grudge") && d.traits.traitor_grudge != null)
                .map((d) -> d.traits.traitor_grudge);
    }

    public static Optional<SnoutGrudgeEntry> getSnoutGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("snout_grudge") && d.traits.snout_grudge != null)
                .map((d) -> d.traits.snout_grudge);
    }

    public static Optional<BoneGrudgeEntry> getBoneGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("bone_grudge") && d.traits.bone_grudge != null)
                .map((d) -> d.traits.bone_grudge);
    }

    public static Optional<AntiAerialEntry> getAntiAerial(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("anti_aerial") && d.traits.anti_aerial != null)
                .map((d) -> d.traits.anti_aerial);
    }

    public static Optional<ArmorBreakerEntry> getArmorBreaker(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("armor_breaker") && d.traits.armor_breaker != null)
                .map((d) -> d.traits.armor_breaker);
    }

    public static Optional<BladeBreakerEntry> getBladeBreaker(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("blade_breaker") && d.traits.blade_breaker != null)
                .map((d) -> d.traits.blade_breaker);
    }

    public static Optional<AcrobaticLungeEntry> getAcrobaticLunge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("acrobatic_lunge") && d.traits.acrobatic_lunge != null)
                .map((d) -> d.traits.acrobatic_lunge);
    }

    public static Optional<DexterousLungeEntry> getDexterousLunge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("dexterous_lunge") && d.traits.dexterous_lunge != null)
                .map((d) -> d.traits.dexterous_lunge);
    }

    public static Optional<PullLungeEntry> getPullLunge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("pull_lunge") && d.traits.pull_lunge != null)
                .map((d) -> d.traits.pull_lunge);
    }

    public static Optional<DisengageEntry> getDisengage(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("disengage") && d.traits.disengage != null)
                .map((d) -> d.traits.disengage);
    }

    public static Optional<DisarmEntry> getDisarm(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("disarm") && d.traits.disarm != null)
                .map((d) -> d.traits.disarm);
    }

    public static Optional<DismountEntry> getDismount(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("dismount") && d.traits.dismount != null)
                .map((d) -> d.traits.dismount);
    }

    public static Optional<DisablingStrikeEntry> getDisablingStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("disabling_strike") && d.traits.disabling_strike != null)
                .map((d) -> d.traits.disabling_strike);
    }

    public static Optional<ThroughStrikeEntry> getThroughStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("through_strike") && d.traits.through_strike != null)
                .map((d) -> d.traits.through_strike);
    }

    public static Optional<CleansingStrikeEntry> getCleansingStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("cleansing_strike") && d.traits.cleansing_strike != null)
                .map((d) -> d.traits.cleansing_strike);
    }

    public static Optional<OverwhelmingStrikeEntry> getOverwhelmingStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("overwhelming_strike") && d.traits.overwhelming_strike != null)
                .map((d) -> d.traits.overwhelming_strike);
    }

    public static Optional<SuppressingStrikeEntry> getSuppressingStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("suppressing_strike") && d.traits.suppressing_strike != null)
                .map((d) -> d.traits.suppressing_strike);
    }

    public static Optional<RottenGrudgeEntry> getRottenGrudge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("rotten_grudge") && d.traits.rotten_grudge != null)
                .map((d) -> d.traits.rotten_grudge);
    }

    public static Optional<BackstabEntry> getBackstab(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("backstab") && d.traits.backstab != null)
                .map((d) -> d.traits.backstab);
    }

    public static Optional<FragilityEntry> getFragility(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("fragility") && d.traits.fragility != null)
                .map((d) -> d.traits.fragility);
    }

    public static Optional<SlipperyEntry> getSlippery(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("slippery") && d.traits.slippery != null)
                .map((d) -> d.traits.slippery);
    }

    public static Optional<ExhaustingEntry> getExhausting(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("exhausting") && d.traits.exhausting != null)
                .map((d) -> d.traits.exhausting);
    }

    public static Optional<BrittleHandleEntry> getBrittleHandle(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("brittle_handle") && d.traits.brittle_handle != null)
                .map((d) -> d.traits.brittle_handle);
    }

    public static Optional<BarbedHandleEntry> getBarbedHandle(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("barbed_handle") && d.traits.barbed_handle != null)
                .map((d) -> d.traits.barbed_handle);
    }

    public static Optional<OverstrainEntry> getOverstrain(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("overstrain") && d.traits.overstrain != null)
                .map((d) -> d.traits.overstrain);
    }

    public static Optional<UnstableEdgeEntry> getUnstableEdge(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("unstable_edge") && d.traits.unstable_edge != null)
                .map((d) -> d.traits.unstable_edge);
    }

    public static Optional<DetonatingEntry> getDetonating(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("detonating") && d.traits.detonating != null)
                .map((d) -> d.traits.detonating);
    }

    public static Optional<DecapitationEntry> getDecapitation(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("decapitation") && d.traits.decapitation != null)
                .map((d) -> d.traits.decapitation);
    }

    public static Optional<PiercingShotEntry> getPiercingShot(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("piercing_shot") && d.projectile_traits.piercing_shot != null)
                .map((d) -> d.projectile_traits.piercing_shot);
    }

    public static Optional<BackstabShotEntry> getBackstabShot(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("backstab_shot") && d.projectile_traits.backstab_shot != null)
                .map((d) -> d.projectile_traits.backstab_shot);
    }

    public static Optional<SweepingShotEntry> getSweepingShot(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("sweeping_shot") && d.projectile_traits.sweeping_shot != null)
                .map((d) -> d.projectile_traits.sweeping_shot);
    }

    public static Optional<DisarmingShotEntry> getDisarmingShot(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("disarming_shot") && d.projectile_traits.disarming_shot != null)
                .map((d) -> d.projectile_traits.disarming_shot);
    }

    public static Optional<DisablingShotEntry> getDisablingShot(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("disabling_shot") && d.projectile_traits.disabling_shot != null)
                .map((d) -> d.projectile_traits.disabling_shot);
    }

    public static Optional<CollectorEntry> getCollector(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("collector") && d.projectile_traits.collector != null)
                .map((d) -> d.projectile_traits.collector);
    }

    public static Optional<ThrowbackEntry> getThrowback(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("throwback") && d.projectile_traits.throwback != null)
                .map((d) -> d.projectile_traits.throwback);
    }

    public static Optional<SlashAssaultEntry> getSlashAssault(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("slash_assault") && d.traits.slash_assault != null)
                .map((d) -> d.traits.slash_assault);
    }

    public static Optional<PiercingAssaultEntry> getPiercingAssault(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("piercing_assault") && d.traits.piercing_assault != null)
                .map((d) -> d.traits.piercing_assault);
    }

    public static Optional<WhirlingStrikeEntry> getWhirlingStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("whirling_strike") && d.traits.whirling_strike != null)
                .map((d) -> d.traits.whirling_strike);
    }

    public static Optional<HarvestSweepEntry> getHarvestSweep(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("harvest_sweep") && d.traits.harvest_sweep != null)
                .map((d) -> d.traits.harvest_sweep);
    }

    public static Optional<QuickSwapEntry> getQuickSwap(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("quick_swap") && d.traits.quick_swap != null)
                .map((d) -> d.traits.quick_swap);
    }

    public static Optional<WildSweepEntry> getWildSweep(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("wild_sweep") && d.traits.wild_sweep != null)
                .map((d) -> d.traits.wild_sweep);
    }

    public static Optional<RapidBoostEntry> getRapidBoost(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("rapid_boost") && d.traits.rapid_boost != null)
                .map((d) -> d.traits.rapid_boost);
    }

    public static Optional<PowerBoostEntry> getPowerBoost(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("power_boost") && d.traits.power_boost != null)
                .map((d) -> d.traits.power_boost);
    }

    public static Optional<ShockImpactEntry> getShockImpact(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("shock_impact") && d.traits.shock_impact != null)
                .map((d) -> d.traits.shock_impact);
    }

    public static Optional<SmashStrikeEntry> getSmashStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("smash_strike") && d.traits.smash_strike != null)
                .map((d) -> d.traits.smash_strike);
    }

    public static class HeavyHandedEntry {

        public Double movement_speed_reduction = null;
        public Double attack_speed_reduction = null;
        public Double attack_damage_reduction = null;
        public Double durability_factor = null;
        public Double damage_factor = null;
        public Double max_reduction = null;
    }

    public static Optional<BusterStrikeEntry> getBusterStrike(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("buster_strike") && d.traits.buster_strike != null)
                .map((d) -> d.traits.buster_strike);
    }

    public static Optional<HeavyHandedEntry> getHeavyHanded(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("heavy_handed") && d.traits.heavy_handed != null)
                .map((d) -> d.traits.heavy_handed);
    }

    public static Optional<GuardStanceEntry> getGuardStance(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("guard_stance") && d.traits.guard_stance != null)
                .map((d) -> d.traits.guard_stance);
    }

    public static Optional<ParryGuardEntry> getParryGuard(ItemStack stack) {
        return getData(stack)
                .filter((d) -> d.hasTrait("parry_guard") && d.traits.parry_guard != null)
                .map((d) -> d.traits.parry_guard);
    }
}
