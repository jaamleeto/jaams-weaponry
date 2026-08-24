package net.jaams.weaponry.handler.trait;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.ModList;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecapitationHandler {

    public static class HeadDropConfig {
        public final String headItemId;
        public final double dropChance;

        public HeadDropConfig(String headItemId, double dropChance) {
            this.headItemId = headItemId;
            this.dropChance = dropChance;
        }
    }

    public static void handleDecapitation(Level world, double x, double y, double z, LivingEntity entity,
            LivingEntity sourceEntity, ItemStack stack) {
        if (entity.getHealth() > 0.0F) {
            return;
        }
        boolean isCritical = sourceEntity.getFallFlyingTicks() > 0 || sourceEntity.isSprinting();
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entityId == null) {
            return;
        }

        
        double generalChance = getGeneralChance(stack);
        double criticalMultiplier = getCriticalMultiplier(stack);

        
        Map<ResourceLocation, HeadDropConfig> specificHeadDrops = new HashMap<>();
        List<TraitModifierData.DecapitationDrop> customDrops = getCustomDrops(stack);

        if (customDrops != null && !customDrops.isEmpty()) {
            
            for (TraitModifierData.DecapitationDrop drop : customDrops) {
                if (drop.entity != null && drop.item != null) {
                    ResourceLocation entityRl = ResourceLocation.tryParse(drop.entity);
                    if (entityRl != null) {
                        double chance = drop.chance != null ? drop.chance : generalChance;
                        specificHeadDrops.put(entityRl, new HeadDropConfig(drop.item, chance));
                    }
                }
            }
        } else {
            
            specificHeadDrops.put(ResourceLocation.parse("minecraft:zombie"),
                    new HeadDropConfig("minecraft:zombie_head", generalChance));
            specificHeadDrops.put(ResourceLocation.parse("minecraft:skeleton"),
                    new HeadDropConfig("minecraft:skeleton_skull", generalChance));
            specificHeadDrops.put(ResourceLocation.parse("minecraft:wither_skeleton"),
                    new HeadDropConfig("minecraft:wither_skeleton_skull", generalChance));
            specificHeadDrops.put(ResourceLocation.parse("minecraft:creeper"),
                    new HeadDropConfig("minecraft:creeper_head", generalChance));
            specificHeadDrops.put(ResourceLocation.parse("minecraft:piglin"),
                    new HeadDropConfig("minecraft:piglin_head", generalChance));

            if (ModList.get().isLoaded("supplementaries")) {
                specificHeadDrops.put(ResourceLocation.parse("minecraft:enderman"),
                        new HeadDropConfig("supplementaries:enderman_head", generalChance));
            }

            if (ModList.get().isLoaded("caverns_and_chasms")) {
                specificHeadDrops.put(ResourceLocation.parse("caverns_and_chasms:mime"),
                        new HeadDropConfig("caverns_and_chasms:mime_head", generalChance));
            }
        }

        if (entity instanceof Player) {
            handlePlayerHeadDrop(world, x, y, z, entity, isCritical, generalChance, criticalMultiplier, stack);
            return;
        }

        HeadDropConfig config = specificHeadDrops.get(entityId);
        if (config != null) {
            handleHeadDrop(world, x, y, z, entity, config.headItemId, config.dropChance, isCritical,
                    criticalMultiplier, stack);
            return;
        }

        
        
        String entityName = entityId.getPath();
        Item targetItem = null;
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId != null) {
                String itemPath = itemId.getPath();
                if (itemPath.equals(entityName + "_head") || itemPath.equals(entityName + "_skull")) {
                    targetItem = item;
                    break;
                }
            }
        }

        if (targetItem != null) {
            handleHeadDrop(world, x, y, z, entity, BuiltInRegistries.ITEM.getKey(targetItem).toString(), generalChance,
                    isCritical, criticalMultiplier, stack);
        }
    }

    private static double getGeneralChance(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DecapitationGeneralChance")) {
            return Math.max(0.0, Math.min(1.0, tag.getFloat("DecapitationGeneralChance")));
        }
        return TraitModifierData.getDecapitation(stack)
                .map(entry -> entry.general_chance)
                .filter(java.util.Objects::nonNull)
                .map(Float::doubleValue)
                .orElseGet(() -> TraitsConfig.DECAPITATION_CHANCE.get());
    }

    private static double getCriticalMultiplier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DecapitationCriticalMultiplier")) {
            return Math.max(1.0, tag.getFloat("DecapitationCriticalMultiplier"));
        }
        return TraitModifierData.getDecapitation(stack)
                .map(entry -> entry.critical_multiplier)
                .filter(java.util.Objects::nonNull)
                .map(Float::doubleValue)
                .orElseGet(() -> TraitsConfig.DECAPITATION_CRITICAL_MULTIPLIER.get());
    }

    private static List<TraitModifierData.DecapitationDrop> getCustomDrops(ItemStack stack) {
        return TraitModifierData.getDecapitation(stack)
                .map(entry -> entry.drops)
                .filter(java.util.Objects::nonNull)
                .filter(drops -> !drops.isEmpty())
                .orElse(null);
    }

    private static void handlePlayerHeadDrop(Level world, double x, double y, double z, LivingEntity entity,
            boolean isCritical, double generalChance, double criticalMultiplier, ItemStack stack) {
        double effectiveChance = isCritical ? generalChance * criticalMultiplier : generalChance;
        if (Math.random() < effectiveChance) {
            if (world instanceof ServerLevel serverLevel) {
                ItemStack headItem = new ItemStack(Items.PLAYER_HEAD);
                if (entity instanceof Player player) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("SkullOwner", player.getGameProfile().getName());
                    headItem.setTag(nbt);
                }
                ItemEntity headEntity = new ItemEntity(serverLevel, x, y, z, headItem);
                headEntity.setPickUpDelay(10);
                serverLevel.addFreshEntity(headEntity);
                playSoundAndParticles(serverLevel, x, y, z, stack);
            }
        }
    }

    private static void handleHeadDrop(Level world, double x, double y, double z, LivingEntity entity,
            String headItemId, double dropChance, boolean isCritical, double criticalMultiplier, ItemStack stack) {
        double effectiveChance = isCritical ? dropChance * criticalMultiplier : dropChance;
        if (Math.random() < effectiveChance) {
            if (world instanceof ServerLevel serverLevel) {
                ResourceLocation itemId = ResourceLocation.parse(headItemId);
                Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item == Items.AIR) {
                    return;
                }
                ItemStack headItem = new ItemStack(item);
                ItemEntity headEntity = new ItemEntity(serverLevel, x, y, z, headItem);
                headEntity.setPickUpDelay(10);
                serverLevel.addFreshEntity(headEntity);
                playSoundAndParticles(serverLevel, x, y, z, stack);
            }
        }
    }

    private static String getSound(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DecapitationSound")) {
            return tag.getString("DecapitationSound");
        }
        return TraitModifierData.getDecapitation(stack)
                .map(entry -> entry.sound)
                .filter(java.util.Objects::nonNull)
                .orElse("jaams_weaponry:decapitation");
    }

    private static String getParticle(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("DecapitationParticle")) {
            return tag.getString("DecapitationParticle");
        }
        return TraitModifierData.getDecapitation(stack)
                .map(entry -> entry.particle)
                .filter(java.util.Objects::nonNull)
                .orElse("custom_sweep");
    }

    private static void playSoundAndParticles(ServerLevel serverLevel, double x, double y, double z, ItemStack stack) {
        String soundId = getSound(stack);
        ResourceLocation soundRl = ResourceLocation.tryParse(soundId);
        if (soundRl != null) {
            var soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundRl);
            if (soundEvent != null) {
                serverLevel.playSound(null, BlockPos.containing(x, y, z), soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
        String particleId = getParticle(stack);
        if ("custom_sweep".equals(particleId) || particleId == null) {
            float r = 1.0F, g = 1.0F, b = 1.0F;
            float size = 1.0F;
            double yOffset = serverLevel.random.nextFloat() * 0.5 - 0.25;
            double particleY = y + yOffset;
            serverLevel.sendParticles(new CustomSweepParticleData(r, g, b, size), x, particleY, z, 1, 0, 0, 0, 0);
        }
    }
}
