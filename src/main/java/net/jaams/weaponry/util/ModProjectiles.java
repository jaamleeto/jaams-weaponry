package net.jaams.weaponry.util;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.ForgeConfigSpec;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.configuration.common.ProjectileBulletConfig;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public final class ModProjectiles {
    public static SoundEvent getCustomProjectileSound(ItemStack weapon, String nbtKey, String genericId,
            String specificId, SoundEvent vanillaFallback,
            java.util.function.Function<ThrowableItemData.ProjectileEntry, String> jsonSoundGetter) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains(nbtKey, net.minecraft.nbt.Tag.TAG_STRING)) {
            String soundId = weapon.getTag().getString(nbtKey);
            SoundEvent custom = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
            if (custom != null)
                return custom;
        }
        if (weapon != null) {
            Optional<ThrowableItemData> dataOpt = ThrowableItemData.getData(weapon);
            if (dataOpt.isPresent()) {
                ThrowableItemData.ProjectileEntry projConfig = dataOpt.get().projectile;
                if (projConfig != null) {
                    String jsonSoundId = jsonSoundGetter.apply(projConfig);
                    if (jsonSoundId != null && !jsonSoundId.isEmpty()) {
                        SoundEvent jsonSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(jsonSoundId));
                        if (jsonSound != null)
                            return jsonSound;
                    }
                }
            }
        }
        String soundId = useGenericProjectileSounds(weapon) ? genericId : specificId;
        SoundEvent modSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("jaams_weaponry:" + soundId));
        if (modSound != null)
            return modSound;
        return vanillaFallback;
    }

    public static int getMultishotCloneDespawnTicks(ItemStack weapon) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("MultishotCloneDespawnTicks", 3)) {
            return weapon.getTag().getInt("MultishotCloneDespawnTicks");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.multishot_clone_despawn_ticks != null ? config.multishot_clone_despawn_ticks : 15;
    }

    public static ParticleOptions getTrailParticle(ItemStack weapon, ParticleOptions defaultParticle) {
        if (weapon == null || weapon.isEmpty()) {
            return defaultParticle;
        }
        CompoundTag tag = weapon.getOrCreateTag();
        if (tag.contains("ProjectileTrailParticleType", Tag.TAG_STRING)) {
            String value = tag.getString("ProjectileTrailParticleType").trim();
            if (!value.isEmpty()) {
                ParticleOptions particle = parseParticle(value);
                if (particle != null) {
                    return particle;
                }
            }
        }
        ThrowableItemData.TrailEntry trailConfig = ThrowableItemData.getTrailConfig(weapon);
        if (trailConfig != null && trailConfig.particle != null && !trailConfig.particle.isEmpty()) {
            ParticleOptions particle = parseParticle(trailConfig.particle);
            if (particle != null) {
                return particle;
            }
        }
        return defaultParticle;
    }

    private static ParticleOptions parseParticle(String particleName) {
        if (particleName == null || particleName.isEmpty()) {
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(particleName);
        if (id == null) {
            try {
                id = new ResourceLocation("minecraft", particleName);
            } catch (Exception e) {
                return null;
            }
        }
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(id);
        if (type != null && type instanceof SimpleParticleType simpleType) {
            return simpleType;
        }
        return null;
    }

    public static boolean useGenericProjectileSounds(ItemStack weapon) {
        return weapon != null && weapon.hasTag() && weapon.getTag().getBoolean("ProjectileGenericSoundSet");
    }

    public static float getBaseDamage(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBaseDamage", Tag.TAG_FLOAT)) {
            return weapon.getTag().getFloat("ProjectileBaseDamage");
        }
        return (float) configDefault;
    }

    public static float getBaseKnockback(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBaseKnockback", Tag.TAG_FLOAT)) {
            return weapon.getTag().getFloat("ProjectileBaseKnockback");
        }
        return (float) configDefault;
    }

    public static int getPiercingLevel(ItemStack weapon, int configDefault) {
        int level = configDefault;
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectilePiercingLevel", Tag.TAG_INT)) {
            level = Math.max(0, weapon.getTag().getInt("ProjectilePiercingLevel"));
        }
        level += EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, weapon);
        return Math.max(0, level);
    }

    public static int getDespawnTicks(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileDespawnTicks", 99)) {
            return Math.max(1, weapon.getTag().getInt("ProjectileDespawnTicks"));
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        int def = config != null && config.despawn_ticks != null ? config.despawn_ticks : (int) configDefault;
        return Math.max(1, def);
    }

    public static float getWaterInertia(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileWaterInertia", 99)) {
            return weapon.getTag().getFloat("ProjectileWaterInertia");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.water_inertia != null ? config.water_inertia : (float) configDefault;
    }

    public static boolean getAllowCriticals(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileAllowCriticals", 1)) {
            return weapon.getTag().getBoolean("ProjectileAllowCriticals");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.allow_criticals != null ? config.allow_criticals : configDefault;
    }

    public static int getIgnoreHitTicks(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileIgnoreHitTicks", 99)) {
            return Math.max(0, weapon.getTag().getInt("ProjectileIgnoreHitTicks"));
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        int def = config != null && config.ignore_hit_ticks != null ? config.ignore_hit_ticks : configDefault;
        return Math.max(0, def);
    }

    public static boolean shouldBreakOnEntityHit(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBreakOnEntityHit", 1)) {
            return weapon.getTag().getBoolean("ProjectileBreakOnEntityHit");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.break_on_entity_hit != null ? config.break_on_entity_hit : configDefault;
    }

    public static boolean shouldBreakOnBlockHit(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBreakOnBlockHit", 1)) {
            return weapon.getTag().getBoolean("ProjectileBreakOnBlockHit");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.break_on_block_hit != null ? config.break_on_block_hit : configDefault;
    }

    public static boolean shouldBreakOnPiercingExhausted(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBreakOnPiercingExhausted", 1)) {
            return weapon.getTag().getBoolean("ProjectileBreakOnPiercingExhausted");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.break_on_piercing_exhausted != null ? config.break_on_piercing_exhausted : configDefault;
    }

    public static boolean shouldBreakAfterMaxBlockBreaks(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileBreakAfterMaxBlockBreaks", 1)) {
            return weapon.getTag().getBoolean("ProjectileBreakAfterMaxBlockBreaks");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.break_after_max_block_breaks != null ? config.break_after_max_block_breaks : configDefault;
    }

    public static int getMaxBlockBreaks(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileMaxBlockBreaks", 99)) {
            return Math.max(0, weapon.getTag().getInt("ProjectileMaxBlockBreaks"));
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        int def = config != null && config.max_block_breaks != null ? config.max_block_breaks : configDefault;
        return Math.max(0, def);
    }

    public static boolean canDisableShield(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileDisableShield", 1)) {
            return weapon.getTag().getBoolean("ProjectileDisableShield");
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        return config != null && config.disable_shield != null ? config.disable_shield : configDefault;
    }

    public static int getShieldDisableCooldownTicks(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileDisableCooldownTicks", 99)) {
            return Math.max(20, weapon.getTag().getInt("ProjectileDisableCooldownTicks"));
        }
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        int def = config != null && config.disable_cooldown_ticks != null ? config.disable_cooldown_ticks : configDefault;
        return Math.max(20, def);
    }

    public static int getMaxTicksInAir(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BulletMaxTicksInAir", Tag.TAG_INT)) {
            return Math.max(0, weapon.getTag().getInt("BulletMaxTicksInAir"));
        }
        return configDefault;
    }

    public static int getMaxTicksInGround(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BulletMaxTicksInGround", Tag.TAG_INT)) {
            return Math.max(0, weapon.getTag().getInt("BulletMaxTicksInGround"));
        }
        return configDefault;
    }

    public static int getColor(ItemStack weapon, ForgeConfigSpec.ConfigValue<String> configColor) {
        if (weapon != null && weapon.hasTag()) {
            CompoundTag tag = weapon.getTag();
            if (tag.contains("BulletColor", Tag.TAG_INT)) {
                return tag.getInt("BulletColor");
            }
            if (tag.contains("BulletColor", Tag.TAG_STRING)) {
                String colorStr = tag.getString("BulletColor");
                return parseColorString(colorStr);
            }
        }
        String colorStr = configColor.get();
        return parseColorString(colorStr);
    }

    public static int getNoGravityDuration(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BulletNoGravityDuration", Tag.TAG_INT)) {
            return Math.max(0, weapon.getTag().getInt("BulletNoGravityDuration"));
        }
        return configDefault;
    }

    public static boolean hasInitialNoGravity(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BulletInitialNoGravity", Tag.TAG_BYTE)) {
            return weapon.getTag().getBoolean("BulletInitialNoGravity");
        }
        return configDefault;
    }

    public static EntityDimensions getDimensions(ItemStack weapon, float defaultWidth, float defaultHeight) {
        float width = defaultWidth;
        float height = defaultHeight;
        ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
        if (config != null) {
            if (config.hitbox_width != null) {
                width = config.hitbox_width;
            }
            if (config.hitbox_height != null) {
                height = config.hitbox_height;
            }
        }
        if (weapon != null && weapon.hasTag()) {
            CompoundTag tag = weapon.getTag();
            if (tag.contains("ProjectileHitboxWidth", Tag.TAG_FLOAT)) {
                width = tag.getFloat("ProjectileHitboxWidth");
            }
            if (tag.contains("ProjectileHitboxHeight", Tag.TAG_FLOAT)) {
                height = tag.getFloat("ProjectileHitboxHeight");
            }
        }
        return EntityDimensions.scalable(width, height);
    }

    public static boolean isCustomBreakableBlock(ItemStack weapon, BlockState state, ResourceLocation defaultBlock) {
        if (weapon == null || state == null) {
            return false;
        }
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        Block block = state.getBlock();
        List<String> rules = new ArrayList<>();
        if (weapon.hasTag()) {
            CompoundTag tag = weapon.getTag();
            if (tag.contains("ProjectileAllowedBreakBlocks", Tag.TAG_LIST)) {
                ListTag list = tag.getList("ProjectileAllowedBreakBlocks", Tag.TAG_STRING);
                for (Tag entry : list) {
                    rules.add(entry.getAsString());
                }
            } else if (tag.contains("ProjectileAllowedBreakBlocks", Tag.TAG_STRING)) {
                String value = tag.getString("ProjectileAllowedBreakBlocks").trim();
                if (!value.isEmpty()) {
                    if (value.startsWith("[") && value.endsWith("]")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    for (String s : value.split(",")) {
                        String trimmed = s.trim();
                        if (!trimmed.isEmpty()) {
                            rules.add(trimmed);
                        }
                    }
                }
            }
        }
        if (rules.isEmpty()) {
            ThrowableItemData.ProjectileEntry config = ThrowableItemData.getProjectileConfig(weapon);
            if (config != null && config.allowed_break_blocks != null && !config.allowed_break_blocks.isEmpty()) {
                rules.addAll(config.allowed_break_blocks);
            }
        }
        if (rules.isEmpty()) {
            return blockId.equals(defaultBlock);
        }
        boolean hasAny = false;
        for (String r : rules) {
            if (r.equalsIgnoreCase("any")) {
                hasAny = true;
                break;
            }
        }
        if (hasAny) {
            for (String rule : rules) {
                String r = rule.trim();
                if (!r.startsWith("!"))
                    continue;
                String clean = r.substring(1).trim();
                boolean matches = false;
                if (clean.startsWith("#")) {
                    ResourceLocation tagLoc = ResourceLocation.tryParse(clean.substring(1));
                    if (tagLoc != null) {
                        matches = block.defaultBlockState().is(TagKey.create(Registries.BLOCK, tagLoc));
                    }
                } else {
                    ResourceLocation ruleLoc = ResourceLocation.tryParse(clean);
                    matches = ruleLoc != null && ruleLoc.equals(blockId);
                }
                if (matches)
                    return false;
            }
            return true;
        } else {
            for (String rule : rules) {
                String r = rule.trim();
                boolean negated = r.startsWith("!");
                String clean = negated ? r.substring(1).trim() : r;
                boolean matches = false;
                if (clean.startsWith("#")) {
                    ResourceLocation tagLoc = ResourceLocation.tryParse(clean.substring(1));
                    if (tagLoc != null) {
                        matches = block.defaultBlockState().is(TagKey.create(Registries.BLOCK, tagLoc));
                    }
                } else {
                    ResourceLocation ruleLoc = ResourceLocation.tryParse(clean);
                    matches = ruleLoc != null && ruleLoc.equals(blockId);
                }
                if (negated) {
                    if (matches)
                        return false;
                } else if (matches) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean getPiercingShotEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("PiercingShotTrait", 1)) {
            return weapon.getTag().getBoolean("PiercingShotTrait");
        }
        Optional<TraitModifierData.PiercingShotEntry> trait = TraitModifierData.getPiercingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getBackstabShotEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BackstabShotTrait", 1)) {
            return weapon.getTag().getBoolean("BackstabShotTrait");
        }
        Optional<TraitModifierData.BackstabShotEntry> trait = TraitModifierData.getBackstabShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getSweepingShotEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("SweepingShotTrait", 1)) {
            return weapon.getTag().getBoolean("SweepingShotTrait");
        }
        Optional<TraitModifierData.SweepingShotEntry> trait = TraitModifierData.getSweepingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getDisarmingShotEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisarmingShotTrait", 1)) {
            return weapon.getTag().getBoolean("DisarmingShotTrait");
        }
        Optional<TraitModifierData.DisarmingShotEntry> trait = TraitModifierData.getDisarmingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getDisablingShotEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisablingShotTrait", 1)) {
            return weapon.getTag().getBoolean("DisablingShotTrait");
        }
        Optional<TraitModifierData.DisablingShotEntry> trait = TraitModifierData.getDisablingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getCollectorEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("CollectorTrait", 1)) {
            return weapon.getTag().getBoolean("CollectorTrait");
        }
        Optional<TraitModifierData.CollectorEntry> trait = TraitModifierData.getCollector(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getThrowbackEnabled(ItemStack weapon, boolean configDefault) {
        if (!configDefault) {
            return false;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ThrowbackTrait", 1)) {
            return weapon.getTag().getBoolean("ThrowbackTrait");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().enabled;
        }
        return true;
    }

    public static boolean getReturnOnBlockHit(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ThrowbackReturnOnBlockHit", 1)) {
            return weapon.getTag().getBoolean("ThrowbackReturnOnBlockHit");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().return_on_block_hit;
        }
        return configDefault;
    }

    public static boolean getReturnOnEntityHit(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ThrowbackReturnOnEntityHit", 1)) {
            return weapon.getTag().getBoolean("ThrowbackReturnOnEntityHit");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().return_on_entity_hit;
        }
        return configDefault;
    }

    public static boolean getReturnOnMaxRange(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ThrowbackReturnOnMaxRange", 1)) {
            return weapon.getTag().getBoolean("ThrowbackReturnOnMaxRange");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().return_on_max_range;
        }
        return configDefault;
    }

    public static double getThrowbackMinRange(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileMinRange", Tag.TAG_DOUBLE)) {
            return weapon.getTag().getDouble("ProjectileMinRange");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().min_range;
        }
        return configDefault;
    }

    public static float getThrowbackMaxRange(ItemStack weapon, float configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileMaxRange", Tag.TAG_FLOAT)) {
            return weapon.getTag().getFloat("ProjectileMaxRange");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().max_range;
        }
        return configDefault;
    }

    public static double getThrowbackReturnSpeed(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileReturnSpeed", Tag.TAG_DOUBLE)) {
            return weapon.getTag().getDouble("ProjectileReturnSpeed");
        }
        Optional<TraitModifierData.ThrowbackEntry> trait = TraitModifierData.getThrowback(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().return_speed;
        }
        return configDefault;
    }

    public static int getMaxMountedEntities(ItemStack weapon, boolean collectorEnabled, int configDefault) {
        if (!collectorEnabled) {
            return 0;
        }
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("CollectorMaxMountedEntities", 3)) {
            return weapon.getTag().getInt("CollectorMaxMountedEntities");
        }
        Optional<TraitModifierData.CollectorEntry> trait = TraitModifierData.getCollector(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().max_mounted_entities;
        }
        return configDefault;
    }

    public static int getMaxBounces(ItemStack weapon, ForgeConfigSpec.IntValue defaultConfig) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileMaxBounces", 99)) {
            return Math.max(0, weapon.getTag().getInt("ProjectileMaxBounces"));
        }
        if (weapon != null) {
            Optional<ThrowableItemData> dataOpt = ThrowableItemData.getData(weapon);
            if (dataOpt.isPresent() && dataOpt.get().projectile != null) {
                
                Integer jsonMaxBounces = dataOpt.get().projectile.max_bounces;
                if (jsonMaxBounces != null) {
                    return Math.max(0, jsonMaxBounces);
                }
            }
        }
        return defaultConfig.get();
    }

    public static boolean getSwooshSoundEnabled(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileSwooshSound", Tag.TAG_BYTE)) {
            return weapon.getTag().getBoolean("ProjectileSwooshSound");
        }
        if (weapon != null) {
            Optional<ThrowableItemData> dataOpt = ThrowableItemData.getData(weapon);
            if (dataOpt.isPresent() && dataOpt.get().projectile != null) {
                Boolean jsonSwooshEnabled = dataOpt.get().projectile.swoosh_sound_enabled;
                if (jsonSwooshEnabled != null) {
                    return jsonSwooshEnabled;
                }
            }
        }
        return configDefault;
    }

    public static int getSwooshInterval(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("ProjectileSwooshInterval", Tag.TAG_INT)) {
            return Math.max(1, weapon.getTag().getInt("ProjectileSwooshInterval"));
        }
        if (weapon != null) {
            Optional<ThrowableItemData> dataOpt = ThrowableItemData.getData(weapon);
            if (dataOpt.isPresent() && dataOpt.get().projectile != null) {
                Integer jsonSwooshInterval = dataOpt.get().projectile.swoosh_interval;
                if (jsonSwooshInterval != null) {
                    return Math.max(1, jsonSwooshInterval);
                }
            }
        }
        return Math.max(1, configDefault);
    }

    public static String getSwooshSoundId(ItemStack weapon, String defaultSound) {
        if (weapon != null && !weapon.isEmpty() && weapon.hasTag()) {
            CompoundTag tag = weapon.getTag();
            if (tag.contains("ProjectileSwooshSoundId", Tag.TAG_STRING)) {
                return tag.getString("ProjectileSwooshSoundId");
            }
        }
        if (weapon != null) {
            Optional<ThrowableItemData> dataOpt = ThrowableItemData.getData(weapon);
            if (dataOpt.isPresent() && dataOpt.get().projectile != null) {
                String jsonSwooshSoundId = dataOpt.get().projectile.swoosh_sound_id;
                if (jsonSwooshSoundId != null && !jsonSwooshSoundId.isEmpty()) {
                    return jsonSwooshSoundId;
                }
            }
        }
        return defaultSound;
    }

    public static boolean getIgniteBlocks(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletIgniteBlocks", Tag.TAG_BYTE)) {
                return tag.getBoolean("FireBulletIgniteBlocks");
            }
        }
        return defaultValue;
    }

    public static int getIgniteBlockDuration(ItemStack gun, int defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletIgniteDuration", Tag.TAG_INT)) {
                return tag.getInt("FireBulletIgniteDuration");
            }
        }
        return defaultValue;
    }

    public static int getSetOnFireSeconds(ItemStack gun, int defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletFireSeconds", Tag.TAG_INT)) {
                return tag.getInt("FireBulletFireSeconds");
            }
        }
        return defaultValue;
    }

    public static boolean getIgniteFlammableOnly(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletIgniteFlammableOnly", Tag.TAG_BYTE)) {
                return tag.getBoolean("FireBulletIgniteFlammableOnly");
            }
        }
        return defaultValue;
    }

    public static boolean getCanLightSpecialBlocks(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletCanLightSpecial", Tag.TAG_BYTE)) {
                return tag.getBoolean("FireBulletCanLightSpecial");
            }
        }
        return defaultValue;
    }

    public static boolean getShowLavaParticles(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("FireBulletShowParticles", Tag.TAG_BYTE)) {
                return tag.getBoolean("FireBulletShowParticles");
            }
        }
        return defaultValue;
    }

    public static boolean getGlowing(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("GlowingBulletGlowing", Tag.TAG_BYTE)) {
                return tag.getBoolean("GlowingBulletGlowing");
            }
        }
        return defaultValue;
    }

    public static int getGlowingDuration(ItemStack gun, int defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("GlowingBulletDuration", Tag.TAG_INT)) {
                return tag.getInt("GlowingBulletDuration");
            }
        }
        return defaultValue;
    }

    public static double getMagicDamageChance(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("GlowingBulletMagicDamageChance", Tag.TAG_DOUBLE)) {
                return tag.getDouble("GlowingBulletMagicDamageChance");
            }
        }
        return defaultValue;
    }

    public static double getMagicDamage(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("GlowingMagicDamage", Tag.TAG_DOUBLE)) {
                return tag.getDouble("GlowingMagicDamage");
            }
        }
        return defaultValue;
    }

    public static boolean getShowAmethystParticles(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("GlowingBulletShowAmethystParticles", Tag.TAG_BYTE)) {
                return tag.getBoolean("GlowingBulletShowAmethystParticles");
            }
        }
        return defaultValue;
    }

    public static double getHeavyKnockedOutChance(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("HeavyBulletKnockedOutChance", Tag.TAG_DOUBLE)) {
                return tag.getDouble("HeavyBulletKnockedOutChance");
            }
        }
        return defaultValue;
    }

    public static int getHeavyKnockedOutDuration(ItemStack gun, int defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("HeavyBulletKnockedOutDuration", Tag.TAG_INT)) {
                return tag.getInt("HeavyBulletKnockedOutDuration");
            }
        }
        return defaultValue;
    }

    public static boolean getShowIronParticles(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("HeavyBulletShowIronParticles", Tag.TAG_BYTE)) {
                return tag.getBoolean("HeavyBulletShowIronParticles");
            }
        }
        return defaultValue;
    }

    public static double getSharpBypassArmorChance(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("SharpBulletBypassArmorChance", Tag.TAG_DOUBLE)) {
                return tag.getDouble("SharpBulletBypassArmorChance");
            }
        }
        return defaultValue;
    }

    public static double getSharpBypassArmorDamage(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("SharpBulletBypassArmorDamage", Tag.TAG_DOUBLE)) {
                return tag.getDouble("SharpBulletBypassArmorDamage");
            }
        }
        return defaultValue;
    }

    public static boolean getShowPrismarineParticles(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("SharpBulletShowPrismarineParticles", Tag.TAG_BYTE)) {
                return tag.getBoolean("SharpBulletShowPrismarineParticles");
            }
        }
        return defaultValue;
    }

    public static boolean getEchoEnableHoming(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletHoming", Tag.TAG_BYTE)) {
                return tag.getBoolean("EchoBulletHoming");
            }
        }
        return defaultValue;
    }

    public static double getEchoHomingSpeed(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletHomingSpeed", Tag.TAG_DOUBLE)) {
                return tag.getDouble("EchoBulletHomingSpeed");
            }
        }
        return defaultValue;
    }

    public static double getEchoSearchRange(ItemStack gun, double defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletSearchRange", Tag.TAG_DOUBLE)) {
                return tag.getDouble("EchoBulletSearchRange");
            }
        }
        return defaultValue;
    }

    public static boolean getShowEchoShardParticles(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletShowEchoShardParticles", Tag.TAG_BYTE)) {
                return tag.getBoolean("EchoBulletShowEchoShardParticles");
            }
        }
        return defaultValue;
    }

    public static boolean shouldEchoIgnoreEntity(ItemStack gun, Entity entity) {
        if (entity == null || gun == null || gun.isEmpty()) {
            return false;
        }
        List<? extends String> configIgnored = ProjectileBulletConfig.ECHO_BULLET_PROJECTILE_IGNORED_ENTITIES.get();
        if (isEntityInIgnoredList(entity, configIgnored)) {
            return true;
        }
        if (gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletIgnoredEntities", Tag.TAG_LIST)) {
                ListTag listTag = tag.getList("EchoBulletIgnoredEntities", Tag.TAG_STRING);
                List<String> nbtIgnored = new ArrayList<>();
                for (int i = 0; i < listTag.size(); i++) {
                    nbtIgnored.add(listTag.getString(i));
                }
                if (isEntityInIgnoredList(entity, nbtIgnored)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isEntityInIgnoredList(Entity entity, List<? extends String> ignoredList) {
        if (ignoredList == null || ignoredList.isEmpty()) {
            return false;
        }
        ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String entityId = entityKey.toString();
        String entityPath = entityKey.getPath();
        for (String ignored : ignoredList) {
            if (ignored == null || ignored.isBlank())
                continue;
            String trimmed = ignored.trim();
            if (trimmed.equalsIgnoreCase(entityId) || trimmed.equalsIgnoreCase(entityPath)) {
                return true;
            }
        }
        return false;
    }

    public static boolean getEchoPlayHomingSound(ItemStack gun, boolean defaultValue) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletPlayHomingSound", Tag.TAG_BYTE)) {
                return tag.getBoolean("EchoBulletPlayHomingSound");
            }
        }
        return defaultValue;
    }

    public static SoundEvent getEchoHomingSound(ItemStack gun, SoundEvent defaultSound) {
        if (gun != null && gun.hasTag()) {
            CompoundTag tag = gun.getTag();
            if (tag.contains("EchoBulletHomingSound", Tag.TAG_STRING)) {
                String soundId = tag.getString("EchoBulletHomingSound");
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
                if (sound != null) {
                    return sound;
                }
            }
        }
        return defaultSound;
    }

    public static float getDisarmingShotChance(ItemStack weapon, float configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisarmingShotChance", 5)) {
            return weapon.getTag().getFloat("DisarmingShotChance");
        }
        Optional<TraitModifierData.DisarmingShotEntry> trait = TraitModifierData.getDisarmingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().chance;
        }
        return configDefault;
    }

    public static boolean getDisarmingShotRequireCritical(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisarmingShotRequireCritical", 1)) {
            return weapon.getTag().getBoolean("DisarmingShotRequireCritical");
        }
        Optional<TraitModifierData.DisarmingShotEntry> trait = TraitModifierData.getDisarmingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().require_critical;
        }
        return configDefault;
    }

    public static int getDisarmingShotDurabilityCost(ItemStack weapon, int configDefault) {
        return 0;
    }

    public static boolean getDisarmingShotMountItem(ItemStack weapon, boolean configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisarmingShotMountItem", 1)) {
            return weapon.getTag().getBoolean("DisarmingShotMountItem");
        }
        Optional<TraitModifierData.DisarmingShotEntry> trait = TraitModifierData.getDisarmingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().mount_item;
        }
        return configDefault;
    }

    public static float getDisablingShotChance(ItemStack weapon, float configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisablingShotChance", 5)) {
            return weapon.getTag().getFloat("DisablingShotChance");
        }
        Optional<TraitModifierData.DisablingShotEntry> trait = TraitModifierData.getDisablingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().chance;
        }
        return configDefault;
    }

    public static int getDisablingShotCooldown(ItemStack weapon, int configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("DisablingShotCooldown", 3)) {
            return weapon.getTag().getInt("DisablingShotCooldown");
        }
        Optional<TraitModifierData.DisablingShotEntry> trait = TraitModifierData.getDisablingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().cooldown;
        }
        return configDefault;
    }

    public static double getSweepingShotRadius(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("SweepingRadius", 99)) {
            return weapon.getTag().getDouble("SweepingRadius");
        }
        Optional<TraitModifierData.SweepingShotEntry> trait = TraitModifierData.getSweepingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().radius;
        }
        return configDefault;
    }

    public static double getSweepingShotDamageFactor(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("SweepingDamageFactor", 99)) {
            return weapon.getTag().getDouble("SweepingDamageFactor");
        }
        Optional<TraitModifierData.SweepingShotEntry> trait = TraitModifierData.getSweepingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().damage_factor;
        }
        return configDefault;
    }

    public static double getBackstabShotDamageMultiplier(ItemStack weapon, double defaultValue) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("BackstabMultiplier", 99)) {
            return weapon.getTag().getDouble("BackstabMultiplier");
        }
        Optional<TraitModifierData.BackstabShotEntry> trait = TraitModifierData.getBackstabShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().damage_multiplier;
        }
        return defaultValue;
    }

    public static double getPiercingShotChance(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("PiercingShotChance", Tag.TAG_DOUBLE)) {
            return weapon.getTag().getDouble("PiercingShotChance");
        }
        Optional<TraitModifierData.PiercingShotEntry> trait = TraitModifierData.getPiercingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().chance;
        }
        return configDefault;
    }

    public static double getPiercingShotBonusDamage(ItemStack weapon, double configDefault) {
        if (weapon != null && weapon.hasTag() && weapon.getTag().contains("PiercingShotBonusDamage", Tag.TAG_DOUBLE)) {
            return weapon.getTag().getDouble("PiercingShotBonusDamage");
        }
        Optional<TraitModifierData.PiercingShotEntry> trait = TraitModifierData.getPiercingShot(weapon);
        if (trait.isPresent() && trait.get() != null) {
            return trait.get().bonus_damage;
        }
        return configDefault;
    }

    public static boolean isHitFromBehind(Entity projectile, Entity target) {
        Vec3 projectileLook = projectile.getDeltaMovement().normalize();
        Vec3 targetLook = target.getLookAngle();
        return projectileLook.dot(targetLook) > 0.4;
    }

    private static boolean hasBoolean(ItemStack stack, String key) {
        return stack.hasTag() && stack.getTag().contains(key, Tag.TAG_BYTE);
    }

    private static boolean hasInt(ItemStack stack, String key) {
        return stack.hasTag() && stack.getTag().contains(key, Tag.TAG_INT);
    }

    private static boolean hasFloat(ItemStack stack, String key) {
        return stack.hasTag() && stack.getTag().contains(key, 99);
    }

    public static void initializeWeaponItem(Entity projectile, ItemStack weaponItem, ItemStack defaultItem,
            CompoundTag persistentData) {
        ItemStack toSet = weaponItem != null && !weaponItem.isEmpty() ? weaponItem.copy() : defaultItem;
        persistentData.put("WeaponItem", toSet.save(new CompoundTag()));
    }

    public static void updateWeaponEnchantmentData(Entity projectile, ItemStack weaponItem,
            EntityDataAccessor<Byte> loyaltyAccessor, EntityDataAccessor<Boolean> foilAccessor) {
        projectile.getEntityData().set(loyaltyAccessor, (byte) EnchantmentHelper.getLoyalty(weaponItem));
        projectile.getEntityData().set(foilAccessor, weaponItem.hasFoil());
    }

    public static float getItemAttackDamage(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return 1.0F;
        }
        double total = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream()
                .mapToDouble(AttributeModifier::getAmount).sum();
        if (total <= 0.0) {
            total = itemStack.getAttributeModifiers(EquipmentSlot.OFFHAND).get(Attributes.ATTACK_DAMAGE).stream()
                    .mapToDouble(AttributeModifier::getAmount).sum();
        }
        return total > 0.0 ? (float) total : 1.0F;
    }

    public static void playPickupSoundMob(Level level, Mob mob) {
        if (!level.isClientSide) {
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL,
                    0.2F, 1.0F);
        }
    }

    public static void playPickupSoundPlayer(Level level, Player player) {
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP,
                    SoundSource.PLAYERS, 0.2F, 1.0F);
        }
    }

    public static void reverseMovementAndRotation(Entity projectile) {
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.08D));
        projectile.setYRot(projectile.getYRot() + 180.0F);
        projectile.yRotO += 180.0F;
    }

    public static void deflectProjectile(Entity projectile, SoundEvent sound) {
        reverseMovementAndRotation(projectile);
        projectile.playSound(sound, 1.0F, 1.0F);
    }

    public static void handleBlocking(LivingEntity livingEntity, Entity owner, ItemStack weaponItem, Level level,
            Entity projectile, int cooldownTicks) {
        if (livingEntity instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(player.getUseItem().getItem())) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), cooldownTicks);
            }
            player.stopUsingItem();
        }
        level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SoundEvents.SHIELD_BREAK,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        reverseMovementAndRotation(projectile);
    }

    public static void damageShield(ItemStack shield, LivingEntity holder, int damageAmount) {
        if (shield.isDamageableItem()) {
            shield.hurtAndBreak(damageAmount, holder, entity -> {
                entity.broadcastBreakEvent(holder.getUsedItemHand());
            });
        }
    }

    public static void spawnCriticalEffects(ServerLevel level, LivingEntity hitLivingEntity) {
        for (int i = 0; i < 8; i++) {
            level.sendParticles(ParticleTypes.CRIT, hitLivingEntity.getX() + level.random.nextGaussian() * 0.5D,
                    hitLivingEntity.getY() + hitLivingEntity.getBbHeight() * 0.5D + level.random.nextGaussian() * 0.5D,
                    hitLivingEntity.getZ() + level.random.nextGaussian() * 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.playSound(null, hitLivingEntity.getX(), hitLivingEntity.getY(), hitLivingEntity.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void spawnEnchantedHitEffects(ServerLevel level, LivingEntity hitLivingEntity, ItemStack weaponItem) {
        int sharpness = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SHARPNESS, weaponItem);
        int smite = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SMITE, weaponItem);
        int bane = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, weaponItem);
        if (sharpness <= 0 && smite <= 0 && bane <= 0) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            level.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    hitLivingEntity.getX() + level.random.nextGaussian() * 0.5D,
                    hitLivingEntity.getY() + hitLivingEntity.getBbHeight() * 0.5D + level.random.nextGaussian() * 0.5D,
                    hitLivingEntity.getZ() + level.random.nextGaussian() * 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void handleItemBreak(ServerLevel level, ItemStack weaponItem, double x, double y, double z,
            Entity projectile) {
        if (!weaponItem.isEmpty() && weaponItem.getDamageValue() >= weaponItem.getMaxDamage()) {
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, weaponItem.copy()), x, y + 0.5, z, 5, 0.1D,
                    0.1D, 0.1D, 0.05D);
            level.playSound(null, x, y, z, SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 0.8F,
                    0.8F + level.random.nextFloat() * 0.4F);
            projectile.discard();
        }
    }

    public static void handleMultishotCloneBreak(ServerLevel level, ItemStack weaponItem, double x, double y, double z,
            Entity projectile) {
        if (!weaponItem.isEmpty()) {
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, weaponItem.copy()), x, y + 0.5, z, 5, 0.1D,
                    0.1D, 0.1D, 0.05D);
            projectile.discard();
        }
    }

    public static void dropAsItem(Level level, ItemStack weaponItem, double x, double y, double z) {
        if (level.isClientSide)
            return;
        ItemStack copy = weaponItem.copy();
        copy.setCount(1);
        ItemEntity entity = new ItemEntity(level, x, y, z, copy);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    public static void spawnBlockParticles(ServerLevel level, BlockPos pos, BlockState state, double px, double py,
            double pz) {
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(px, py, pz) < 256.0D) {
                level.sendParticles(player, new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos), true, px,
                        py, pz, 6, 0.1D, 0.1D, 0.1D, 0.05D);
            }
        }
    }

    public static void applyWeaponEnchantmentEffects(LivingEntity target, @Nullable Entity owner,
            ItemStack weaponItem) {
        if (weaponItem.isEmpty()) {
            return;
        }
        if (owner instanceof LivingEntity ownerLiving) {
            EnchantmentHelper.doPostHurtEffects(target, ownerLiving);
            EnchantmentHelper.doPostDamageEffects(ownerLiving, target);
        }
        int fireAspectLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, weaponItem);
        if (fireAspectLevel > 0) {
            target.setSecondsOnFire(fireAspectLevel * 4);
        }
        if (target.level() instanceof ServerLevel serverLevel) {
            spawnEnchantedHitEffects(serverLevel, target, weaponItem);
        }
    }

    public static boolean tryMobPickup(ItemStack pickupItemStack, Mob mob, Level level) {
        ItemStack mainHand = mob.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = mob.getItemInHand(InteractionHand.OFF_HAND);
        if (!mainHand.isEmpty() && !offHand.isEmpty()) {
            if (!mainHand.is(pickupItemStack.getItem()) || mainHand.getCount() >= mainHand.getMaxStackSize()) {
                if (!offHand.is(pickupItemStack.getItem()) || offHand.getCount() >= offHand.getMaxStackSize()) {
                    return false;
                }
            }
        }
        if (!mainHand.isEmpty() && mainHand.is(pickupItemStack.getItem())
                && mainHand.getCount() < mainHand.getMaxStackSize()) {
            mainHand.grow(1);
            playPickupSoundMob(level, mob);
            return true;
        } else if (!offHand.isEmpty() && offHand.is(pickupItemStack.getItem())
                && offHand.getCount() < offHand.getMaxStackSize()) {
            offHand.grow(1);
            playPickupSoundMob(level, mob);
            return true;
        } else if (mainHand.isEmpty()) {
            mob.setItemInHand(InteractionHand.MAIN_HAND, pickupItemStack);
            playPickupSoundMob(level, mob);
            return true;
        } else if (offHand.isEmpty()) {
            mob.setItemInHand(InteractionHand.OFF_HAND, pickupItemStack);
            playPickupSoundMob(level, mob);
            return true;
        }
        return false;
    }

    public static boolean isAcceptableLoyaltyReturnOwner(@Nullable Entity owner) {
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayer player) || !player.isSpectator());
    }

    private static int parseColorString(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) {
            return 0xFFFFD700;
        }
        colorStr = colorStr.trim();
        try {
            if (colorStr.startsWith("0x") || colorStr.startsWith("0X") || colorStr.startsWith("#")) {
                String hex = colorStr.replace("0x", "").replace("0X", "").replace("#", "");
                long color = Long.parseLong(hex, 16);
                return (int) color;
            } else {
                return Integer.parseInt(colorStr);
            }
        } catch (NumberFormatException e) {
            return 0xFFFFD700;
        }
    }
}
