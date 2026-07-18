package net.jaams.weaponry.gun.helper;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.packet.VisualRecoilPacket;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.handler.gun.GunSoundHandler;
import net.jaams.weaponry.handler.gun.GunActionsHandler;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.JaamsWeaponryMod;

import java.util.function.IntSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;

public class GunShootHelper {
    public static void shoot(Level level, double x, double y, double z, Entity entity, ItemStack gunStack,
            ModGuns.GunType gunType, BooleanSupplier gunAmmoFromGun, BooleanSupplier gunAmmoFromHand,
            BooleanSupplier gunAmmoFromPlayerInventory,
            IntSupplier ammoConsumption, IntSupplier attachmentConsumption, IntSupplier projectileCount,
            DoubleSupplier spreadAngle, DoubleSupplier projectileSpeed, DoubleSupplier inaccuracy,
            DoubleSupplier damageModifier,
            DoubleSupplier knockbackModifier, IntSupplier piercingModifier, DoubleSupplier shotSize,
            DoubleSupplier shotDistance, DoubleSupplier cooldown, IntSupplier offhandCooldown,
            DoubleSupplier recoilDistance,
            DoubleSupplier crouchRecoilReduction, DoubleSupplier verticalRecoilMultiplier,
            DoubleSupplier xRotRecoilIntensity, DoubleSupplier shakeIntensity, IntSupplier shakeResetDelay) {
        if (!(entity instanceof LivingEntity living))
            return;
        boolean isPlayer = living instanceof Player;
        Player player = isPlayer ? (Player) living : null;
        boolean isClientSide = level.isClientSide();
        boolean isCreative = isPlayer && player != null && player.isCreative();
        
        
        boolean mobBypassAmmo = !isPlayer && !MobBehaviorConfig.GUN_MOBS_NEED_AMMO.get();
        boolean bypassAmmo = isCreative || mobBypassAmmo;
        GunItemData.GunEntry gunData = GunItemData.getGunData(gunStack);
        boolean useGunAmmo = getFinalAmmoSource(gunStack, "GunAmmoFromGun", gunAmmoFromGun,
                gunData != null ? gunData.ammo_from_gun : null);
        boolean useHandAmmo = getFinalAmmoSource(gunStack, "GunAmmoFromHand", gunAmmoFromHand,
                gunData != null ? gunData.ammo_from_hand : null);
        boolean useInventoryAmmo = isPlayer && getFinalAmmoSource(gunStack, "GunAmmoFromPlayerInventory",
                gunAmmoFromPlayerInventory, gunData != null ? gunData.ammo_from_player_inventory : null);
        int finalAmmoConsumption = ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption", ammoConsumption);
        SourceResult source = mobBypassAmmo ? new SourceResult(ItemStack.EMPTY, false)
                : getPreferredSourceWithPriority(living, gunStack, useGunAmmo, useHandAmmo, useInventoryAmmo,
                        finalAmmoConsumption, gunType);
        if (!source.hasEnough() && !bypassAmmo) {
            if (!isClientSide) {
                playEmptySound(level, x, y, z, entity, gunStack, gunType);
            }
            return;
        }
        performShot(level, living, gunStack, source, gunType, projectileCount, spreadAngle, projectileSpeed, inaccuracy,
                damageModifier, knockbackModifier, piercingModifier, shotSize, shotDistance, cooldown, offhandCooldown,
                recoilDistance,
                crouchRecoilReduction, verticalRecoilMultiplier, xRotRecoilIntensity, shakeIntensity, shakeResetDelay);
        if (!bypassAmmo) {
            consumeResourcesAfterShot(living, gunStack, source, finalAmmoConsumption, attachmentConsumption);
        }
        ModGuns.updateGunInventory(gunStack);
    }

    public static boolean getFinalAmmoSource(ItemStack gunStack, String key, BooleanSupplier configDefault,
            Boolean jsonValue) {
        if (gunStack.hasTag()) {
            var tag = gunStack.getTag();
            if (tag.contains(key)) {
                return tag.getBoolean(key);
            }
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return configDefault.getAsBoolean();
    }

    public static void playEmptySound(Level level, double x, double y, double z, Entity entity, ItemStack gunStack,
            ModGuns.GunType type) {
        if (type == ModGuns.GunType.PISTOL) {
            GunSoundHandler.playPistolEmptySound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.SCATTERGUN) {
            GunSoundHandler.playScattergunEmptySound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.SHOTGUN) {
            GunSoundHandler.playShotgunEmptySound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.REVOLVER) {
            GunSoundHandler.playRevolverEmptySound(level, x, y, z, entity, gunStack);
        } else {
            GunSoundHandler.playPistolEmptySound(level, x, y, z, entity, gunStack);
        }
    }

    public static void playShootSound(Level level, double x, double y, double z, LivingEntity entity,
            ItemStack gunStack, ModGuns.GunType type) {
        if (type == ModGuns.GunType.PISTOL) {
            GunSoundHandler.playPistolAttachmentSound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.SCATTERGUN) {
            GunSoundHandler.playScattergunAttachmentSound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.SHOTGUN) {
            GunSoundHandler.playShotgunAttachmentSound(level, x, y, z, entity, gunStack);
        } else if (type == ModGuns.GunType.REVOLVER) {
            GunSoundHandler.playRevolverAttachmentSound(level, x, y, z, entity, gunStack);
        } else {
            GunSoundHandler.playPistolAttachmentSound(level, x, y, z, entity, gunStack);
        }
    }

    public static SourceResult getPreferredSourceWithPriority(LivingEntity living, ItemStack gunStack,
            boolean useGunAmmo, boolean useHandAmmo, boolean useInventoryAmmo, int minRequired, ModGuns.GunType type) {
        boolean isCreative = living instanceof Player p && p.isCreative();
        int required = isCreative ? 1 : minRequired;
        if (useGunAmmo) {
            ItemStack ammoInGun = getAmmoFromGun(gunStack);
            if (!ammoInGun.isEmpty() && ammoInGun.getCount() >= required) {
                return new SourceResult(ammoInGun, true);
            }
        }
        if (useHandAmmo) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack handStack = living.getItemInHand(hand);
                if (isSameStack(handStack, gunStack))
                    continue;
                if (!handStack.isEmpty() && handStack.getCount() >= required
                        && isValidAmmo(gunStack, handStack, type)) {
                    return new SourceResult(handStack.copy(), false);
                }
            }
        }
        if (useInventoryAmmo && living instanceof Player player) {
            for (ItemStack invStack : player.getInventory().items) {
                if (isSameStack(invStack, gunStack))
                    continue;
                if (!invStack.isEmpty() && invStack.getCount() >= required && isValidAmmo(gunStack, invStack, type)) {
                    return new SourceResult(invStack.copy(), false);
                }
            }
        }
        return new SourceResult(ItemStack.EMPTY, false);
    }

    public static boolean isSameStack(ItemStack a, ItemStack b) {
        return a == b || ItemStack.matches(a, b);
    }

    public static boolean isValidAmmo(ItemStack gunStack, ItemStack ammoStack, ModGuns.GunType type) {
        return ModGuns.canPlaceInGunSlot(gunStack, ammoStack, type, 1);
    }

    public static ItemStack getAmmoFromGun(ItemStack gunStack) {
        if (ModGuns.isRevolverGun(gunStack)) {
            int chamberSlot = ModGuns.getRevolverChamberSlot(gunStack);
            return gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(handler -> handler.getStackInSlot(chamberSlot).copy())
                    .orElse(ItemStack.EMPTY);
        }
        return gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.getStackInSlot(1).copy())
                .orElse(ItemStack.EMPTY);
    }

    public static void performShot(Level level, LivingEntity entity, ItemStack gunStack, SourceResult source,
            ModGuns.GunType gunType, IntSupplier projectileCount, DoubleSupplier spreadAngle,
            DoubleSupplier projectileSpeed, DoubleSupplier inaccuracy,
            DoubleSupplier damageModifier, DoubleSupplier knockbackModifier, IntSupplier piercingModifier,
            DoubleSupplier shotSize, DoubleSupplier shotDistance, DoubleSupplier cooldown, IntSupplier offhandCooldown,
            DoubleSupplier recoilDistance,
            DoubleSupplier crouchRecoilReduction, DoubleSupplier verticalRecoilMultiplier,
            DoubleSupplier xRotRecoilIntensity, DoubleSupplier shakeIntensity, IntSupplier shakeResetDelay) {
        if (level.isClientSide())
            return;
        GunItemData.ShootEntry shootData = GunItemData.getShootData(gunStack);
        GunItemData.ParticleEntry particleData = GunItemData.getData(gunStack).map(d -> d.particle)
                .orElse(null);
        playShootSound(level, entity.getX(), entity.getY(), entity.getZ(), entity, gunStack, gunType);
        GunActionsHandler.handleGunShot(level, entity, gunStack,
                (float) getFinalDoubleParticle(gunStack, "GunShotSize", shotSize, particleData),
                (float) getFinalDoubleParticle(gunStack, "GunShotDistance", shotDistance, particleData));
        int cooldownTicks = (int) Math.max(1, getFinalDouble(gunStack, "GunCooldown", cooldown, shootData));
        if (entity instanceof Player player) {
            int offhandCd = (int) getFinalDouble(gunStack, "GunOffhandCooldown", () -> offhandCooldown.getAsInt(),
                    shootData);
            ModGuns.applyCooldowns(player, gunStack, cooldownTicks, offhandCd);
            ModGuns.applyPhysicalRecoil(player, gunStack,
                    (float) getFinalDouble(gunStack, "GunRecoilDistance", recoilDistance, shootData),
                    (float) getFinalDouble(gunStack, "GunCrouchRecoilReduction", crouchRecoilReduction, shootData),
                    (float) getFinalDouble(gunStack, "GunVerticalRecoilMultiplier", verticalRecoilMultiplier,
                            shootData));
            if (player instanceof ServerPlayer serverPlayer) {
                JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new VisualRecoilPacket((float) getFinalDouble(gunStack, "GunXRotRecoilIntensity",
                                xRotRecoilIntensity, shootData)));
            }
            ModUtils.applyShakeEffect(player, getFinalDouble(gunStack, "GunShakeIntensity", shakeIntensity, shootData),
                    getFinalInt(gunStack, "GunShakeResetDelay", shakeResetDelay, shootData));
        }
        ModGuns.spawnProjectile(level, entity, gunStack, source.bulletItem,
                getFinalInt(gunStack, "GunProjectileCount", projectileCount, shootData),
                getFinalDouble(gunStack, "GunSpreadAngle", spreadAngle, shootData),
                getFinalDouble(gunStack, "GunProjectileSpeed", projectileSpeed, shootData),
                getFinalDouble(gunStack, "GunProjectileInaccuracy", inaccuracy, shootData),
                getFinalDouble(gunStack, "GunProjectileDamageModifier", damageModifier, shootData),
                getFinalDouble(gunStack, "GunProjectileKnockbackModifier", knockbackModifier, shootData),
                getFinalInt(gunStack, "GunProjectilePiercingModifier", piercingModifier, shootData));
    }

    public static double getFinalDouble(ItemStack gunStack, String key, DoubleSupplier configDefault,
            GunItemData.ShootEntry shootData) {
        double nbtValue = ModUtils.getConfigOrNbtDouble(gunStack, key, () -> 0.0);
        if (nbtValue != 0.0)
            return nbtValue;
        if (shootData != null) {
            double jsonValue = getValueFromShootEntry(shootData, key);
            if (jsonValue != 0.0)
                return jsonValue;
        }
        return configDefault.getAsDouble();
    }

    public static double getFinalDoubleParticle(ItemStack gunStack, String key, DoubleSupplier configDefault,
            GunItemData.ParticleEntry particleData) {
        double nbtValue = ModUtils.getConfigOrNbtDouble(gunStack, key, () -> 0.0);
        if (nbtValue != 0.0)
            return nbtValue;
        if (particleData != null) {
            double jsonValue = getValueFromParticleEntry(particleData, key);
            if (jsonValue != 0.0)
                return jsonValue;
        }
        return configDefault.getAsDouble();
    }

    public static int getFinalInt(ItemStack gunStack, String key, IntSupplier configDefault,
            GunItemData.ShootEntry shootData) {
        int nbtValue = ModUtils.getConfigOrNbtInt(gunStack, key, () -> 0);
        if (nbtValue != 0)
            return nbtValue;
        if (shootData != null) {
            int jsonValue = getIntFromShootEntry(shootData, key);
            if (jsonValue != 0)
                return jsonValue;
        }
        return configDefault.getAsInt();
    }

    public static double getValueFromShootEntry(GunItemData.ShootEntry data, String key) {
        if (data == null)
            return 0.0;
        double value = switch (key) {
            case "GunCooldown" -> data.cooldown;
            case "GunRecoilDistance" -> data.recoil_distance;
            case "GunCrouchRecoilReduction" -> data.crouch_recoil_reduction;
            case "GunVerticalRecoilMultiplier" -> data.vertical_recoil_multiplier;
            case "GunXRotRecoilIntensity" -> data.xrot_recoil_intensity;
            case "GunShakeIntensity" -> data.shake_intensity;
            case "GunSpreadAngle" -> data.spread_angle;
            case "GunProjectileSpeed" -> data.projectile_speed;
            case "GunProjectileInaccuracy" -> data.inaccuracy;
            case "GunProjectileDamageModifier" -> data.damage_modifier;
            case "GunProjectileKnockbackModifier" -> data.knockback_modifier;
            default -> 0.0;
        };
        return value == -1.0 ? 0.0 : value;
    }

    public static double getValueFromParticleEntry(GunItemData.ParticleEntry data, String key) {
        if (data == null)
            return 0.0;
        double value = switch (key) {
            case "GunShotSize" -> data.shot_size;
            case "GunShotDistance" -> data.shot_distance;
            default -> 0.0;
        };
        return value == -1.0 ? 0.0 : value;
    }

    public static int getIntFromShootEntry(GunItemData.ShootEntry data, String key) {
        if (data == null)
            return 0;
        int value = switch (key) {
            case "GunProjectileCount" -> data.projectile_count;
            case "GunProjectilePiercingModifier" -> data.piercing_modifier;
            case "GunShakeResetDelay" -> data.shake_reset_delay;
            case "GunOffhandCooldown" -> (int) data.offhand_cooldown;
            case "GunAmmoConsumption" -> data.ammo_consumption;
            case "GunAttachmentConsumption" -> data.attachment_consumption;
            default -> 0;
        };
        return value == -1 ? 0 : value;
    }

    public static void consumeResourcesAfterShot(LivingEntity living, ItemStack gunStack, SourceResult source,
            int ammoConsumption, IntSupplier attachmentConsumptionSupplier) {
        int attachmentConsumption = ModUtils.getConfigOrNbtInt(gunStack, "GunAttachmentConsumption",
                attachmentConsumptionSupplier);
        if (!source.bulletItem.isEmpty()) {
            if (source.consumedFromGun) {
                consumeAmmoFromGun(gunStack, ammoConsumption, living);
            } else if (living instanceof Player player) {
                consumeAmmoFromPlayerSource(player, source.bulletItem, ammoConsumption);
            }
        }
        if (gunStack.isDamageableItem()) {
            gunStack.hurtAndBreak(1, living, p -> {
            });
        }
        gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (ModGuns.isRevolverGun(gunStack)) {
                // Revolver: only consume attachment from slot 6
                consumeAttachment(handler, 6, attachmentConsumption);
            } else {
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    if (slot != 1) {
                        consumeAttachment(handler, slot, attachmentConsumption);
                    }
                }
            }
        });
    }

    public static void consumeAmmoFromGun(ItemStack gunStack, int amount, LivingEntity entity) {
        gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int ammoSlot = ModGuns.isRevolverGun(gunStack) ? ModGuns.getRevolverChamberSlot(gunStack) : 1;
            ItemStack ammoInSlot = handler.getStackInSlot(ammoSlot);
            if (ammoInSlot.isEmpty())
                return;
            boolean consumeAmmo = true;
            if (entity instanceof Player player) {
                int ghostClipLevel = gunStack.getEnchantmentLevel(ModEnchantments.GHOST_CLIP.get());
                if (ghostClipLevel > 0) {
                    double noConsumeChance = EnchantmentsConfig.GHOST_CLIP_CHANCE_PER_LEVEL.get() * ghostClipLevel;
                    if (player.level().getRandom().nextDouble() < noConsumeChance) {
                        consumeAmmo = false;
                    }
                }
            }
            if (consumeAmmo) {
                consumeItem(ammoInSlot, amount);
            }
        });
    }

    public static void consumeAmmoFromPlayerSource(Player player, ItemStack ammoToConsume, int amount) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack handStack = player.getItemInHand(hand);
            if (ItemStack.matches(handStack, ammoToConsume)) {
                consumeItem(handStack, amount);
                return;
            }
        }
        for (ItemStack invStack : player.getInventory().items) {
            if (ItemStack.matches(invStack, ammoToConsume)) {
                consumeItem(invStack, amount);
                return;
            }
        }
    }

    public static void consumeItem(ItemStack stack, int amount) {
        if (stack.isEmpty() || amount <= 0)
            return;
        stack.shrink(amount);
    }

    public static void consumeAttachment(IItemHandler handler, int slot, int amount) {
        ItemStack attachment = handler.getStackInSlot(slot);
        if (attachment.isEmpty())
            return;
        if (attachment.getMaxDamage() > 0) {
            int newDamage = attachment.getDamageValue() + amount;
            if (newDamage >= attachment.getMaxDamage()) {
                attachment.setCount(0);
            } else {
                attachment.setDamageValue(newDamage);
            }
        } else {
            attachment.shrink(amount);
        }
        handler.insertItem(slot, attachment, false);
    }

    public static class SourceResult {
        final ItemStack bulletItem;
        final boolean consumedFromGun;
        final boolean hasEnough;

        SourceResult(ItemStack bulletItem, boolean consumedFromGun) {
            this.bulletItem = bulletItem;
            this.consumedFromGun = consumedFromGun;
            this.hasEnough = !bulletItem.isEmpty();
        }

        public boolean hasEnough() {
            return hasEnough;
        }
    }
}
