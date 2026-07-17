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
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
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
        int finalAmmoConsumption = Math.max(0,
                ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption", ammoConsumption)
                        + GunAttachmentHelper.getAmmoConsumptionAddend(gunStack)
                        - GunAttachmentHelper.getAmmoConsumptionSubtrahend(gunStack));
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
        } else if (type == ModGuns.GunType.REVOLVER || type == ModGuns.GunType.PEPPERBOX) {
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
        } else if (type == ModGuns.GunType.PEPPERBOX) {
            GunSoundHandler.playPepperboxAttachmentSound(level, x, y, z, entity, gunStack);
        } else {
            GunSoundHandler.playPistolAttachmentSound(level, x, y, z, entity, gunStack);
        }
    }

    public static SourceResult getPreferredSourceWithPriority(LivingEntity living, ItemStack gunStack,
            boolean useGunAmmo, boolean useHandAmmo, boolean useInventoryAmmo, int minRequired, ModGuns.GunType type) {
        boolean isCreative = living instanceof Player p && p.isCreative();
        int required = isCreative ? 1 : minRequired;
        if (useGunAmmo) {
            ItemStack ammoInGun = getAmmoFromGun(gunStack, required);
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

    public static ItemStack getAmmoFromGun(ItemStack gunStack, int minCount) {
        if (ModGuns.isRevolverGun(gunStack)) {
            int chamberSlot = ModGuns.getNextLoadedChamberSlot(gunStack, minCount);
            if (chamberSlot < 0)
                return ItemStack.EMPTY;
            final int slot = chamberSlot;
            return gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(handler -> handler.getStackInSlot(slot).copy())
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
        int cooldownTicks = (int) Math.max(1,
                getFinalDouble(gunStack, "GunCooldown", cooldown, shootData)
                        * GunAttachmentHelper.getCooldownMultiplier(gunStack)
                        + GunAttachmentHelper.getCooldownAddend(gunStack)
                        - GunAttachmentHelper.getCooldownSubtrahend(gunStack));
        if (entity instanceof Player player) {
            int offhandCd = (int) (getFinalDouble(gunStack, "GunOffhandCooldown", () -> offhandCooldown.getAsInt(),
                    shootData)
                    + GunAttachmentHelper.getOffhandCooldownAddend(gunStack)
                    - GunAttachmentHelper.getOffhandCooldownSubtrahend(gunStack));
            ModGuns.applyCooldowns(player, gunStack, cooldownTicks, offhandCd);
            ModGuns.applyPhysicalRecoil(player, gunStack,
                    (float) (getFinalDouble(gunStack, "GunRecoilDistance", recoilDistance, shootData)
                            * GunAttachmentHelper.getRecoilMultiplier(gunStack)
                            + GunAttachmentHelper.getRecoilAddend(gunStack)
                            - GunAttachmentHelper.getRecoilSubtrahend(gunStack)),
                    (float) (getFinalDouble(gunStack, "GunCrouchRecoilReduction", crouchRecoilReduction, shootData)
                            * GunAttachmentHelper.getCrouchRecoilReductionMultiplier(gunStack)
                            + GunAttachmentHelper.getCrouchRecoilReductionAddend(gunStack)
                            - GunAttachmentHelper.getCrouchRecoilReductionSubtrahend(gunStack)),
                    (float) (getFinalDouble(gunStack, "GunVerticalRecoilMultiplier", verticalRecoilMultiplier,
                            shootData) * GunAttachmentHelper.getVerticalRecoilMultiplier(gunStack)
                            + GunAttachmentHelper.getVerticalRecoilAddend(gunStack)
                            - GunAttachmentHelper.getVerticalRecoilSubtrahend(gunStack)));
            if (player instanceof ServerPlayer serverPlayer) {
                JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new VisualRecoilPacket((float) (getFinalDouble(gunStack, "GunXRotRecoilIntensity",
                                xRotRecoilIntensity, shootData) * GunAttachmentHelper.getXRotRecoilMultiplier(gunStack)
                                + GunAttachmentHelper.getXRotRecoilAddend(gunStack)
                                - GunAttachmentHelper.getXRotRecoilSubtrahend(gunStack))));
            }
            ModUtils.applyShakeEffect(player,
                    getFinalDouble(gunStack, "GunShakeIntensity", shakeIntensity, shootData)
                            * GunAttachmentHelper.getShakeIntensityMultiplier(gunStack)
                            + GunAttachmentHelper.getShakeIntensityAddend(gunStack)
                            - GunAttachmentHelper.getShakeIntensitySubtrahend(gunStack),
                    getFinalInt(gunStack, "GunShakeResetDelay", shakeResetDelay, shootData)
                            + GunAttachmentHelper.getShakeResetDelayAddend(gunStack)
                            - GunAttachmentHelper.getShakeResetDelaySubtrahend(gunStack));
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
        double value;
        switch (key) {
            case "GunCooldown":
                value = data.cooldown;
                break;
            case "GunRecoilDistance":
                value = data.recoil_distance;
                break;
            case "GunCrouchRecoilReduction":
                value = data.crouch_recoil_reduction;
                break;
            case "GunVerticalRecoilMultiplier":
                value = data.vertical_recoil_multiplier;
                break;
            case "GunXRotRecoilIntensity":
                value = data.xrot_recoil_intensity;
                break;
            case "GunShakeIntensity":
                value = data.shake_intensity;
                break;
            case "GunSpreadAngle":
                value = data.spread_angle;
                break;
            case "GunProjectileSpeed":
                value = data.projectile_speed;
                break;
            case "GunProjectileInaccuracy":
                value = data.inaccuracy;
                break;
            case "GunProjectileDamageModifier":
                value = data.damage_modifier;
                break;
            case "GunProjectileKnockbackModifier":
                value = data.knockback_modifier;
                break;
            default:
                value = 0.0;
                break;
        }
        return value == -1.0 ? 0.0 : value;
    }

    public static double getValueFromParticleEntry(GunItemData.ParticleEntry data, String key) {
        if (data == null)
            return 0.0;
        double value;
        switch (key) {
            case "GunShotSize":
                value = data.shot_size;
                break;
            case "GunShotDistance":
                value = data.shot_distance;
                break;
            default:
                value = 0.0;
                break;
        }
        return value == -1.0 ? 0.0 : value;
    }

    public static int getIntFromShootEntry(GunItemData.ShootEntry data, String key) {
        if (data == null)
            return 0;
        int value;
        switch (key) {
            case "GunProjectileCount":
                value = data.projectile_count;
                break;
            case "GunProjectilePiercingModifier":
                value = data.piercing_modifier;
                break;
            case "GunShakeResetDelay":
                value = data.shake_reset_delay;
                break;
            case "GunOffhandCooldown":
                value = (int) data.offhand_cooldown;
                break;
            case "GunAmmoConsumption":
                value = data.ammo_consumption;
                break;
            case "GunAttachmentConsumption":
                value = data.attachment_consumption;
                break;
            default:
                value = 0;
                break;
        }
        return value == -1 ? 0 : value;
    }

    public static void consumeResourcesAfterShot(LivingEntity living, ItemStack gunStack, SourceResult source,
            int ammoConsumption, IntSupplier attachmentConsumptionSupplier) {
        int attachmentConsumption = Math.max(0,
                ModUtils.getConfigOrNbtInt(gunStack, "GunAttachmentConsumption",
                        attachmentConsumptionSupplier)
                        + GunAttachmentHelper.getAttachmentConsumptionAddend(gunStack)
                        - GunAttachmentHelper.getAttachmentConsumptionSubtrahend(gunStack));
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
            if (ModGuns.isRevolverLikeGun(gunStack)) {
                // Revolver-like guns (revolver, pepperbox): only consume the muzzle attachment from slot 0
                consumeAttachment(handler, GunAttachmentHelper.getMuzzleSlot(), attachmentConsumption);
            } else {
                // Pistol, scattergun, shotgun: consume muzzle (slot 0) and magazine (slot 2)
                consumeAttachment(handler, GunAttachmentHelper.getMuzzleSlot(), attachmentConsumption);
                consumeAttachment(handler, GunAttachmentHelper.getMagazineSlot(), attachmentConsumption);
            }
        });
    }

    public static void consumeAmmoFromGun(ItemStack gunStack, int amount, LivingEntity entity) {
        gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int ammoSlot = ModGuns.isRevolverGun(gunStack) ? ModGuns.getNextLoadedChamberSlot(gunStack, amount) : 1;
            if (ammoSlot < 0)
                return;
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
