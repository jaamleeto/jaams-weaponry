package net.jaams.weaponry.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {

    
    public static final TagKey<Item> AXES = create("compat/axes");
    public static final TagKey<Item> HOES = create("compat/hoes");
    public static final TagKey<Item> PICKAXES = create("compat/pickaxes");
    public static final TagKey<Item> SHOVELS = create("compat/shovels");
    public static final TagKey<Item> SWORDS = create("compat/swords");
    public static final TagKey<Item> BOWS = create("compat/bows");
    public static final TagKey<Item> CROSSBOWS = create("compat/crossbows");
    
    public static final TagKey<Item> BATTLE_AXES = create("item/battle_axes");
    public static final TagKey<Item> BOKKENS = create("item/bokkens");
    public static final TagKey<Item> BROADSWORDS = create("item/broadswords");
    public static final TagKey<Item> BROOMS = create("item/brooms");
    public static final TagKey<Item> BUSTER_SWORDS = create("item/buster_swords");
    public static final TagKey<Item> BUTTERFLY_SWORDS = create("item/butterfly_swords");
    public static final TagKey<Item> CLAWS = create("item/claws");
    public static final TagKey<Item> CLEAVERS = create("item/cleavers");
    public static final TagKey<Item> DAGGERS = create("item/daggers");
    public static final TagKey<Item> GAUNTLETS = create("item/gauntlets");
    public static final TagKey<Item> GIANT_SHURIKENS = create("item/giant_shurikens");
    public static final TagKey<Item> GREATSWORDS = create("item/greatswords");
    public static final TagKey<Item> GREAT_HAMMERS = create("item/greathammers");
    public static final TagKey<Item> HAMMERS = create("item/hammers");
    public static final TagKey<Item> HOOK_SWORDS = create("item/hook_swords");
    public static final TagKey<Item> HUNTERS_BOOMERANGS = create("item/hunters_boomerangs");
    public static final TagKey<Item> KAMAS = create("item/kamas");
    public static final TagKey<Item> KATANAS = create("item/katanas");
    public static final TagKey<Item> KATARS = create("item/katars");
    public static final TagKey<Item> KNUCKLES = create("item/knuckles");
    public static final TagKey<Item> KUNAIS = create("item/kunais");
    public static final TagKey<Item> LONGSWORDS = create("item/longswords");
    public static final TagKey<Item> MACHETES = create("item/machetes");
    public static final TagKey<Item> MALLETS = create("item/mallets");
    public static final TagKey<Item> NUNCHAKUS = create("item/nunchakus");
    public static final TagKey<Item> PISTOLS = create("item/pistols");
    public static final TagKey<Item> PRONGED_KUNAIS = create("item/pronged_kunais");
    public static final TagKey<Item> REVERSE_DAGGERS = create("item/reverse_daggers");
    public static final TagKey<Item> RINGS = create("item/rings");
    public static final TagKey<Item> ROYAL_AXES = create("item/royal_axes");
    public static final TagKey<Item> ROYAL_BOWS = create("item/royal_bows");
    public static final TagKey<Item> ROYAL_CROSSBOWS = create("item/royal_crossbows");
    public static final TagKey<Item> ROYAL_RAPIER = create("item/royal_rapier");
    public static final TagKey<Item> ROYAL_SPEARS = create("item/royal_spears");
    public static final TagKey<Item> ROYAL_SWORDS = create("item/royal_swords");
    public static final TagKey<Item> RUSTIC_WHIPS = create("item/rustic_whips");
    public static final TagKey<Item> SAW_CLEAVERS = create("item/saw_cleavers");
    public static final TagKey<Item> SAW_CLEAVERS_UNFOLDED = create("item/saw_cleavers_unfolded");
    public static final TagKey<Item> SCATTERGUNS = create("item/scatterguns");
    public static final TagKey<Item> REVOLVERS = create("item/revolvers");
    public static final TagKey<Item> SCYTHES = create("item/scythes");
    public static final TagKey<Item> SHARP_STONE_BLADES = create("item/sharp_stone_blades");
    public static final TagKey<Item> SHARP_STONES = create("item/sharp_stones");
    public static final TagKey<Item> SHOTGUNS = create("item/shotguns");
    public static final TagKey<Item> SHURIKENS = create("item/shurikens");
    public static final TagKey<Item> SICKLES = create("item/sickles");
    public static final TagKey<Item> SLINGSHOTS = create("item/slingshots");
    public static final TagKey<Item> SPEARS = create("item/spears");
    public static final TagKey<Item> STAKES = create("item/stakes");
    public static final TagKey<Item> TESSENS = create("item/tessens");
    public static final TagKey<Item> TRIDENTS = create("item/tridents");
    public static final TagKey<Item> TWINBLADES = create("item/twinblades");
    public static final TagKey<Item> WAR_PICKS = create("item/war_picks");
    public static final TagKey<Item> ZWEIHANDERS = create("item/zweihanders");
    public static final TagKey<Item> THROWABLE_FIX = create("misc/throwable_fix");
    
    public static final TagKey<Item> PIERCER_STRIKE = create("trait/piercer_strike");
    public static final TagKey<Item> DUELIST = create("trait/duelist");
    public static final TagKey<Item> THREAT_RESPONSE = create("trait/threat_response");

    public static final TagKey<Item> REACH_ADVANTAGE = create("trait/reach_advantage");
    public static final TagKey<Item> AFTER_STRIKE = create("trait/after_strike");
    public static final TagKey<Item> ARMOR_BREAKER = create("trait/armor_breaker");
    public static final TagKey<Item> CLEANSING_STRIKE = create("trait/cleansing_strike");
    public static final TagKey<Item> SLASH_ASSAULT = create("trait/slash_assault");
    public static final TagKey<Item> PIERCING_ASSAULT = create("trait/piercing_assault");
    public static final TagKey<Item> DISARM = create("trait/disarm");
    public static final TagKey<Item> DISMOUNT = create("trait/dismount");
    public static final TagKey<Item> FRAGILITY = create("trait/fragility");
    public static final TagKey<Item> DISABLING_STRIKE = create("trait/disabling_strike");
    public static final TagKey<Item> ACROBATIC_LUNGE = create("trait/acrobatic_lunge");
    public static final TagKey<Item> DEXTEROUS_LUNGE = create("trait/dexterous_lunge");
    public static final TagKey<Item> PULL_LUNGE = create("trait/pull_lunge");
    public static final TagKey<Item> QUICK_CRAFTING = create("trait/quick_crafting");
    public static final TagKey<Item> AQUATIC_GRUDGE = create("trait/aquatic_grudge");
    public static final TagKey<Item> ARTHROPOD_GRUDGE = create("trait/arthropod_grudge");
    public static final TagKey<Item> UNDEAD_GRUDGE = create("trait/undead_grudge");
    public static final TagKey<Item> TRAITOR_GRUDGE = create("trait/traitor_grudge");
    public static final TagKey<Item> SNOUT_GRUDGE = create("trait/snout_grudge");
    public static final TagKey<Item> BONE_GRUDGE = create("trait/bone_grudge");
    public static final TagKey<Item> ANTI_AERIAL = create("trait/anti_aerial");
    public static final TagKey<Item> BLADE_BREAKER = create("trait/blade_breaker");
    public static final TagKey<Item> DISENGAGE = create("trait/disengage");
    public static final TagKey<Item> ROTTEN_GRUDGE = create("trait/rotten_grudge");
    public static final TagKey<Item> EXHAUSTING = create("trait/exhausting");
    public static final TagKey<Item> BRITTLE_HANDLE = create("trait/brittle_handle");
    public static final TagKey<Item> BARBED_HANDLE = create("trait/barbed_handle");
    public static final TagKey<Item> OVERSTRAIN = create("trait/overstrain");
    public static final TagKey<Item> UNSTABLE_EDGE = create("trait/unstable_edge");
    public static final TagKey<Item> DETONATING = create("trait/detonating");
    public static final TagKey<Item> DECAPITATION = create("trait/decapitation");
    public static final TagKey<Item> SPARRING_STRIKE = create("trait/sparring_strike");
    public static final TagKey<Item> HARVEST_SWEEP = create("trait/harvest_sweep");
    public static final TagKey<Item> WILD_SWEEP = create("trait/wild_sweep");
    public static final TagKey<Item> WHIRLING_STRIKE = create("trait/whirling_strike");
    public static final TagKey<Item> SLIPPERY = create("trait/slippery");
    public static final TagKey<Item> OVERWHELMING_STRIKE = create("trait/overwhelming_strike");
    public static final TagKey<Item> SUPPRESSING_STRIKE = create("trait/suppressing_strike");
    public static final TagKey<Item> THROUGH_STRIKE = create("trait/through_strike");
    public static final TagKey<Item> QUICK_SWAP = create("trait/quick_swap");
    public static final TagKey<Item> RAPID_BOOST = create("trait/rapid_boost");
    public static final TagKey<Item> POWER_BOOST = create("trait/power_boost");
    public static final TagKey<Item> SMASH_STRIKE = create("trait/smash_strike");
    public static final TagKey<Item> SHOCK_IMPACT = create("trait/shock_impact");
    public static final TagKey<Item> BACKSTAB = create("trait/backstab");
    public static final TagKey<Item> HEAVY_HANDED = create("trait/heavy_handed");
    public static final TagKey<Item> BUSTER_STRIKE = create("trait/buster_strike");
    public static final TagKey<Item> GUARD_STANCE = create("trait/guard_stance");
    public static final TagKey<Item> PARRY_GUARD = create("trait/parry_guard");
    
    public static final TagKey<EntityType<?>> AQUATIC_GRUDGE_TARGETS = createEntity("trait/aquatic_grudge_targets");
    public static final TagKey<EntityType<?>> ARTHROPOD_GRUDGE_TARGETS = createEntity("trait/arthropod_grudge_targets");
    public static final TagKey<EntityType<?>> UNDEAD_GRUDGE_TARGETS = createEntity("trait/undead_grudge_targets");
    public static final TagKey<EntityType<?>> TRAITOR_GRUDGE_TARGETS = createEntity("trait/traitor_grudge_targets");
    public static final TagKey<EntityType<?>> SNOUT_GRUDGE_TARGETS = createEntity("trait/snout_grudge_targets");
    public static final TagKey<EntityType<?>> BONE_GRUDGE_TARGETS = createEntity("trait/bone_grudge_targets");
    public static final TagKey<EntityType<?>> ROTTEN_GRUDGE_TARGETS = createEntity("trait/rotten_grudge_targets");
    
    public static final TagKey<EntityType<?>> ANTI_AERIAL_TARGETS = createEntity("trait/anti_aerial_targets");
    
    public static final TagKey<EntityType<?>> IS_ALLY = createEntity("misc/is_ally");

    public static final TagKey<Item> DYNAMITES = create("misc/dynamites");
    public static final TagKey<Item> SMOKE_BOMBS = create("misc/smoke_bombs");
    public static final TagKey<Item> BULLETS = create("misc/bullets");
    public static final TagKey<Item> SHOTSHELLS = create("misc/shotshells");
    public static final TagKey<Item> MUZZLES = create("misc/muzzles");
    public static final TagKey<Item> MAGAZINES = create("misc/magazines");
    public static final TagKey<Item> GUNS = create("misc/guns");
    public static final TagKey<Item> PRIORITIZE_SHIELD = create("misc/prioritize_shield");

    // Enchantable tags
    public static final TagKey<Item> ENCHANTABLE_AFTERMATH = create("enchantable/aftermath");
    public static final TagKey<Item> ENCHANTABLE_BACKBLAST = create("enchantable/backblast");
    public static final TagKey<Item> ENCHANTABLE_FRAMEGUARD = create("enchantable/frameguard");
    public static final TagKey<Item> ENCHANTABLE_GHOST_CLIP = create("enchantable/ghost_clip");
    public static final TagKey<Item> ENCHANTABLE_OVERDRIVE = create("enchantable/overdrive");
    public static final TagKey<Item> ENCHANTABLE_SECURE_GRIP = create("enchantable/secure_grip");

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("jaams_weaponry", path));
    }

    private static TagKey<EntityType<?>> createEntity(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("jaams_weaponry", path));
    }
}
