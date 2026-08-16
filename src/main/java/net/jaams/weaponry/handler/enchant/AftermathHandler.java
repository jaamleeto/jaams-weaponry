package net.jaams.weaponry.handler.enchant;

import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.component.projectile.BaseItemProjectileEntity;
import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "jaams_weaponry")
public class AftermathHandler {

    private static final Map<Player, AftermathData> aftermathPlayers = new ConcurrentHashMap<>();
    private static final Map<LivingEntity, AftermathData> aftermathItems = new ConcurrentHashMap<>();
    private static final String AFTERMATH_NBT_KEY = "AftermathLevel";
    private static final String AFTERMATH_IMMUNE_KEY = "AftermathImmune";
    private static final int CLEANUP_DELAY = 5;

    public static int getIFrames() {
        return EnchantmentsConfig.AFTERMATH_I_FRAMES.get();
    }

    private static class AftermathData {

        int level;
        long lastUseTick;

        AftermathData(int level, long lastUseTick) {
            this.level = level;
            this.lastUseTick = lastUseTick;
        }
    }

    @SubscribeEvent
    public static void onAftermathLivingEntityUseItemStop(LivingEntityUseItemEvent.Stop event) {
        LivingEntity entity = event.getEntity();
        ItemStack item = event.getItem();
        if (entity instanceof Player player) {
            int aftermathLevel = ModEnchantments.level(item, ModEnchantments.AFTERMATH);
            if (aftermathLevel > 0) {
                aftermathPlayers.put(player, new AftermathData(aftermathLevel, player.level().getGameTime()));
            } else {
                aftermathPlayers.remove(player);
            }
        } else {
            
            int aftermathLevel = ModEnchantments.level(item, ModEnchantments.AFTERMATH);
            if (aftermathLevel > 0) {
                aftermathItems.put(entity, new AftermathData(aftermathLevel, entity.level().getGameTime()));
            } else {
                aftermathItems.remove(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onAftermathRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        int aftermathLevel = ModEnchantments.level(itemStack, ModEnchantments.AFTERMATH);
        if (aftermathLevel > 0) {
            aftermathPlayers.put(player, new AftermathData(aftermathLevel, player.level().getGameTime()));
        } else {
            aftermathPlayers.remove(player);
        }
    }

    @SubscribeEvent
    public static void onAftermathEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity entity) {
            if (entity instanceof Player player) {
                AftermathData data = aftermathPlayers.get(player);
                if (data != null && data.level > 0) {
                    projectile.getPersistentData().putInt(AFTERMATH_NBT_KEY, data.level);
                }
            } else {
                int aftermathLevel = 0;

                
                
                
                if (projectile instanceof BaseWeaponProjectileEntity weaponProj) {
                    ItemStack weaponItem = weaponProj.getWeaponItem();
                    if (!weaponItem.isEmpty()) {
                        aftermathLevel = ModEnchantments.level(weaponItem, ModEnchantments.AFTERMATH);
                    }
                }
                if (aftermathLevel <= 0 && projectile instanceof BaseItemProjectileEntity itemProj) {
                    ItemStack sourceItem = itemProj.getSourceItem();
                    if (!sourceItem.isEmpty()) {
                        aftermathLevel = ModEnchantments.level(sourceItem, ModEnchantments.AFTERMATH);
                    }
                }
                if (aftermathLevel <= 0 && projectile instanceof BaseBulletProjectileEntity bulletProj) {
                    ItemStack gunItem = bulletProj.getGunItem();
                    if (!gunItem.isEmpty()) {
                        aftermathLevel = ModEnchantments.level(gunItem, ModEnchantments.AFTERMATH);
                    }
                }

                
                if (aftermathLevel <= 0) {
                    aftermathLevel = ModEnchantments.level(entity.getMainHandItem(), ModEnchantments.AFTERMATH);
                }
                if (aftermathLevel <= 0) {
                    aftermathLevel = ModEnchantments.level(entity.getOffhandItem(), ModEnchantments.AFTERMATH);
                }

                if (aftermathLevel > 0) {
                    aftermathItems.put(entity, new AftermathData(aftermathLevel, entity.level().getGameTime()));
                    projectile.getPersistentData().putInt(AFTERMATH_NBT_KEY, aftermathLevel);
                } else {
                    aftermathItems.remove(entity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAftermathPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            AftermathData data = aftermathPlayers.get(player);
            if (data != null) {
                long currentTick = player.level().getGameTime();
                if (currentTick - data.lastUseTick >= CLEANUP_DELAY) {
                    aftermathPlayers.remove(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAftermathServerTick(ServerTickEvent.Post event) {
        if (true) {
            aftermathItems.forEach((entity, data) -> {
                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    aftermathItems.remove(entity);
                    return;
                }
                long currentTick = entity.level().getGameTime();
                if (currentTick - data.lastUseTick >= CLEANUP_DELAY) {
                    aftermathItems.remove(entity);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onAftermathLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            aftermathPlayers.remove(player);
        } else {
            aftermathItems.remove(entity);
        }
    }

    @SubscribeEvent
    public static void onAftermathPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        aftermathPlayers.remove(player);
    }

    @SubscribeEvent
    public static void onAftermathEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof Player)) {
            aftermathItems.remove(livingEntity);
        }
    }

    @SubscribeEvent
    public static void onAftermathLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (aftermathIsTargetBlocking(target)) {
            return;
        }
        if (target.getPersistentData().getBoolean(AFTERMATH_IMMUNE_KEY)) {
            return;
        }
        int aftermathLevel = projectile.getPersistentData().getInt(AFTERMATH_NBT_KEY);
        if (aftermathLevel <= 0) {
            return;
        }
        float extraDamage = aftermathCalculateExtraDamage(projectile, event.getAmount(), aftermathLevel);
        int finalDelay = aftermathCalculateDelay(aftermathLevel);
        Entity sourceEntity = projectile.getOwner() != null ? projectile.getOwner() : projectile;
        projectile.getPersistentData().remove(AFTERMATH_NBT_KEY);
        JaamsWeaponryMod.queueServerWork(finalDelay, () -> {
            aftermathProcessDelayedDamage(target, sourceEntity, extraDamage, aftermathLevel);
        });
    }

    private static float aftermathCalculateExtraDamage(Projectile projectile, float originalDamage, int level) {
        float baseExtraDamage = (float) (EnchantmentsConfig.AFTERMATH_EXTRA_DAMAGE_PER_LEVEL.get() * level);
        float multiplier = 0.1f;
        ResourceLocation projectileId = BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType());
        boolean isBullet = projectileId != null && projectileId.toString().toLowerCase().contains("bullet");
        float damage = baseExtraDamage + (originalDamage * multiplier * level);
        return isBullet ? damage * 0.5f : damage;
    }

    private static int aftermathCalculateDelay(int level) {
        int baseDelay = EnchantmentsConfig.AFTERMATH_DELAY_TICKS.get();
        double perLevelModifier = EnchantmentsConfig.AFTERMATH_DELAY_TICKS_PER_LEVEL.get();
        return (int) Math.max(1, baseDelay + (level * perLevelModifier));
    }

    private static boolean aftermathIsTargetBlocking(LivingEntity target) {
        return target.isBlocking() && EnchantmentsConfig.AFTERMATH_BLOCKING_BLOCKS_DAMAGE.get();
    }

    private static void aftermathProcessDelayedDamage(LivingEntity target, Entity sourceEntity, float extraDamage,
            int level) {
        if (target.isRemoved() || !target.isAlive() || target.getPersistentData().getBoolean(AFTERMATH_IMMUNE_KEY)) {
            return;
        }
        Level levelInstance = target.level();
        DamageSource damageSource = new DamageSource(levelInstance.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ARROW), sourceEntity);
        if (aftermathIsTargetBlocking(target)) {
            aftermathHandleBlockedDamage(target, sourceEntity, extraDamage, level);
            return;
        }
        float finalCalculatedDamage = extraDamage;
        if (EnchantmentsConfig.AFTERMATH_PROTECTION_REDUCES_DAMAGE.get()
                && target.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            finalCalculatedDamage = net.minecraft.world.item.enchantment.EnchantmentHelper.getDamageProtection(serverLevel, target, damageSource) > 0
                    ? net.minecraft.world.damagesource.CombatRules.getDamageAfterMagicAbsorb(finalCalculatedDamage,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getDamageProtection(serverLevel, target, damageSource))
                    : finalCalculatedDamage;
        }
        if (target.hurt(damageSource, finalCalculatedDamage)) {
            levelInstance.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            target.invulnerableTime = getIFrames();
            aftermathHandleThornsReflection(target, sourceEntity, finalCalculatedDamage, level, 0.7F, 0.0F, 0.0F,
                    false);
            aftermathSpawnParticles(target, level, 0.7F, 0.0F, 0.0F);
        }
    }

    private static void aftermathHandleBlockedDamage(LivingEntity target, Entity sourceEntity, float extraDamage,
            int level) {
        Level levelInstance = target.level();
        levelInstance.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        levelInstance.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_WEAK,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        aftermathSpawnParticles(target, level, 0.0F, 0.2F, 0.7F);
        aftermathHandleThornsReflection(target, sourceEntity, extraDamage, level, 0.0F, 0.2F, 0.7F, true);
    }

    private static void aftermathHandleThornsReflection(LivingEntity target, Entity sourceEntity, float damageBasis,
            int aftermathLevel, float r, float g, float b, boolean spawnAtTarget) {
        if (!EnchantmentsConfig.AFTERMATH_THORNS_REFLECT.get() || !(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }
        int thornsLevel = ModEnchantments.entityLevel(target, Enchantments.THORNS);
        if (thornsLevel <= 0) {
            return;
        }
        double reflectedPercent = EnchantmentsConfig.AFTERMATH_THORNS_PERCENTAGE.get() * thornsLevel;
        float reflectedDamage = (float) (damageBasis * reflectedPercent);
        if (reflectedDamage > 0.1F) {
            DamageSource thornsSource = target.level().damageSources().thorns(target);
            boolean isAttackerBlocking = attacker.isBlocking();
            if (isAttackerBlocking) {
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
                aftermathSpawnParticles(spawnAtTarget ? target : attacker, aftermathLevel, 0.0F, 0.2F, 0.7F);
                attacker.hurt(thornsSource, reflectedDamage);
            } else {
                if (attacker.hurt(thornsSource, reflectedDamage)) {
                    attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                            SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
                    aftermathSpawnParticles(spawnAtTarget ? target : attacker, aftermathLevel, r, g, b);
                }
            }
        }
    }

    private static void aftermathSpawnParticles(LivingEntity entity, int aftermathLevel, float baseR, float baseG,
            float baseB) {
        if (!EnchantmentsConfig.AFTERMATH_SPAWN_PARTICLES.get() || entity.level().isClientSide()) {
            return;
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 center = entity.getBoundingBox().getCenter();
            int particleCount = EnchantmentsConfig.AFTERMATH_PARTICLES_PER_LEVEL.get() * aftermathLevel;
            for (int i = 0; i < particleCount; i++) {
                float r = baseR > 0 ? baseR + (entity.getRandom().nextFloat() * (1.0F - baseR)) : 0.0F;
                float g = baseG == 0.0F && baseR == 0.0F ? entity.getRandom().nextFloat() * 0.4F : 0.0F;
                float b = baseB > 0.4F ? 0.4F + (entity.getRandom().nextFloat() * 0.6F) : 0.0F;
                float baseSize = 0.15F + (aftermathLevel * 0.05F);
                float size = baseSize + (entity.getRandom().nextFloat() * 0.05F);
                CustomHitParticleData particleData = new CustomHitParticleData(r, g, b, size);
                double spreadFactor = 0.2 + (entity.getRandom().nextFloat() * 0.15);
                double offsetX = entity.getRandom().nextGaussian() * spreadFactor;
                double offsetY = entity.getRandom().nextGaussian() * spreadFactor;
                double offsetZ = entity.getRandom().nextGaussian() * spreadFactor;
                serverLevel.sendParticles(particleData, center.x + offsetX, center.y + offsetY, center.z + offsetZ, 1,
                        0.0, 0.0, 0.0, 0.0);
            }
        }
    }
}
