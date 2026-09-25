package net.jaams.weaponry.handler.gun;

import net.jaams.weaponry.capability.CapHelper;

import net.jaams.weaponry.util.ModComponents;

import java.util.concurrent.atomic.AtomicReference;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.core.BlockPos;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.BuiltInRegistries;

public class GunSoundHandler {

    public static void playEmptyWeaponSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack, String soundResource) {
        if (entity == null || level.isClientSide()) {
            return;
        }
        SoundEvent sound = getSoundEvent(soundResource);
        if (sound == null) {
            return;
        }
        if (level instanceof Level) {
            Level serverLevel = (Level) level;
            if (!serverLevel.isClientSide()) {
                serverLevel.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else {
                serverLevel.playLocalSound(x, y, z, sound, SoundSource.PLAYERS, 1.0f, 1.0f, false);
            }
        }
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(itemstack.getItem(), 20);
        }
    }

    public static void playPistolEmptySound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:pistol_empty");
    }

    public static void playScattergunEmptySound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:scattergun_empty");
    }

    public static void playShotgunEmptySound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:shotgun_empty");
    }

    public static void playRevolverEmptySound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:revolver_empty");
    }

    public static void playScattergunOneBulletSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:scattergun_empty");
    }

    private static ItemStack getItemStackFromSlot(int slotId, ItemStack itemStack) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        CapHelper.itemHandler(itemStack).ifPresent((capability) -> result.set(capability.getStackInSlot(slotId).copy()));
        return result.get();
    }

    private static void playSound(LevelAccessor level, double x, double y, double z, String soundResource, float volume, float pitch) {
        if (level.isClientSide() || !(level instanceof Level) || soundResource == null || soundResource.isEmpty()) {
            return;
        }
        Level serverLevel = (Level) level;
        SoundEvent sound = getSoundEvent(soundResource);
        if (sound == null) {
            return;
        }
        serverLevel.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.PLAYERS, volume, pitch);
    }

    private static SoundEvent getSoundEvent(String soundId) {
        if (soundId == null || soundId.isEmpty()) {
            return null;
        }
        ResourceLocation location;
        if (soundId.contains(":")) {
            location = ResourceLocation.parse(soundId);
        } else {
            location = ResourceLocation.fromNamespaceAndPath("jaams_weaponry", soundId);
        }
        return BuiltInRegistries.SOUND_EVENT.get(location);
    }

    public static void handleWeaponFire(
        LevelAccessor level,
        double x,
        double y,
        double z,
        Entity entity,
        ItemStack itemstack,
        Item muzzleAttachment,
        String muzzleShootSound,
        String defaultShootSound,
        Item magazineAttachment,
        String magazineAfterShootSound,
        String defaultAfterShootSound,
        String dropSound,
        double defaultShakeAmount
    ) {
        if (entity == null) return;
        GunItemData.SoundEntry soundData = GunItemData.getData(itemstack)
            .map((d) -> d.sound)
            .orElse(null);
        float attachmentVolume = GunAttachmentHelper.getAttachmentSoundVolume(itemstack);
        float attachmentPitch = GunAttachmentHelper.getAttachmentSoundPitch(itemstack);
        float volume = getFinalFloat(itemstack, "GunSoundVolume", soundData != null ? soundData.sound_volume : null, attachmentVolume, 1.0f);
        float pitch = getFinalFloat(itemstack, "GunSoundPitch", soundData != null ? soundData.sound_pitch : null, attachmentPitch, 1.0f);
        double attachmentBulletDropChance = GunAttachmentHelper.getAttachmentBulletDropChance(itemstack);
        double bulletDropChance = getFinalDouble(itemstack, "GunBulletDropChance", soundData != null ? soundData.bullet_drop_chance : null, attachmentBulletDropChance, 0.7);
        int attachmentAfterShotDelay = GunAttachmentHelper.getAttachmentAfterShotDelay(itemstack);
        int afterShotDelay = getFinalInt(itemstack, "GunAfterShotDelay", soundData != null ? soundData.after_shot_delay : null, attachmentAfterShotDelay, 10);
        int attachmentEmptyCooldown = GunAttachmentHelper.getAttachmentEmptyCooldown(itemstack);
        int emptyCooldown = getFinalInt(itemstack, "GunEmptyCooldown", soundData != null ? soundData.empty_cooldown : null, attachmentEmptyCooldown, 20);
        String jsonShootSound = soundData != null ? soundData.shoot_sound : "";
        String jsonAfterShootSound = soundData != null ? soundData.after_shoot_sound : "";
        String jsonBulletDropSound = soundData != null ? soundData.bullet_drop_sound : "";
        String attachmentShootSound = GunAttachmentHelper.getAttachmentShootSound(itemstack);
        String attachmentAfterShootSound = GunAttachmentHelper.getAttachmentAfterShootSound(itemstack);
        String attachmentBulletDropSound = GunAttachmentHelper.getAttachmentBulletDropSound(itemstack);
        int muzzleSlot = GunAttachmentHelper.getMuzzleSlot();
        String defaultShoot = attachmentShootSound != null && !attachmentShootSound.isEmpty()
                ? attachmentShootSound
                : (getItemStackFromSlot(muzzleSlot, itemstack).getItem() == muzzleAttachment ? muzzleShootSound : defaultShootSound);
        String finalShootSound = getFinalSound(itemstack, "GunShootSound", jsonShootSound, defaultShoot);
        String finalAfterShootSound;
        if (magazineAttachment != null) {
            int magazineSlot = GunAttachmentHelper.getMagazineSlot(itemstack);
            String defaultAfter = attachmentAfterShootSound != null && !attachmentAfterShootSound.isEmpty()
                    ? attachmentAfterShootSound
                    : (getItemStackFromSlot(magazineSlot, itemstack).getItem() == magazineAttachment ? magazineAfterShootSound : defaultAfterShootSound);
            finalAfterShootSound = getFinalSound(itemstack, "GunAfterShootSound", jsonAfterShootSound, defaultAfter);
        } else {
            String defaultAfter = attachmentAfterShootSound != null && !attachmentAfterShootSound.isEmpty()
                    ? attachmentAfterShootSound : defaultAfterShootSound;
            finalAfterShootSound = getFinalSound(itemstack, "GunAfterShootSound", jsonAfterShootSound, defaultAfter);
        }
        String defaultDrop = attachmentBulletDropSound != null && !attachmentBulletDropSound.isEmpty()
                ? attachmentBulletDropSound : dropSound;
        String finalDropSound = getFinalSound(itemstack, "GunBulletDropSound", jsonBulletDropSound, defaultDrop);
        playSound(level, x, y, z, finalShootSound, volume, pitch);
        JaamsWeaponryMod.queueServerWork(afterShotDelay, () -> {
            playSound(level, x, y, z, finalAfterShootSound, volume, pitch);
            if (Math.random() < bulletDropChance) {
                playSound(level, x, y, z, finalDropSound, volume, pitch);
            }
        });
    }

    private static String getFinalSound(ItemStack gunStack, String nbtKey, String jsonValue, String defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            String nbt = ModComponents.get(gunStack).getString(nbtKey);
            if (!nbt.isEmpty()) return nbt;
        }
        if (jsonValue != null && !jsonValue.isEmpty()) {
            return jsonValue;
        }
        return defaultValue != null ? defaultValue : "";
    }

    private static float getFinalFloat(ItemStack gunStack, String nbtKey, Float jsonValue, float attachmentValue, float defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            return ModComponents.get(gunStack).getFloat(nbtKey);
        }
        if (jsonValue != null && jsonValue != -1.0f) {
            return jsonValue;
        }
        if (attachmentValue != -1.0f) {
            return defaultValue * attachmentValue;
        }
        return defaultValue;
    }

    private static double getFinalDouble(ItemStack gunStack, String nbtKey, Double jsonValue, double attachmentValue, double defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            return ModComponents.get(gunStack).getDouble(nbtKey);
        }
        if (jsonValue != null && jsonValue != -1.0) {
            return jsonValue;
        }
        if (attachmentValue != -1.0) {
            return attachmentValue;
        }
        return defaultValue;
    }

    private static int getFinalInt(ItemStack gunStack, String nbtKey, Integer jsonValue, int attachmentValue, int defaultValue) {
        if (ModComponents.has(gunStack) && ModComponents.get(gunStack).contains(nbtKey)) {
            return ModComponents.get(gunStack).getInt(nbtKey);
        }
        if (jsonValue != null && jsonValue != -1) {
            return jsonValue;
        }
        if (attachmentValue != -1) {
            return attachmentValue;
        }
        return defaultValue;
    }

    public static void playPistolAttachmentSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        handleWeaponFire(
            level,
            x,
            y,
            z,
            entity,
            itemstack,
            ModItems.COPPER_MUZZLE.get(),
            "jaams_weaponry:pistol_muzzle_shoot",
            "jaams_weaponry:pistol_shoot",
            ModItems.COPPER_QUICK_DRAW_MAGAZINE.get(),
            "jaams_weaponry:pistol_magazine_after_shot",
            "jaams_weaponry:pistol_after_shoot",
            "jaams_weaponry:bullet_drop",
            1.5
        );
    }

    public static void playScattergunAttachmentSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        handleWeaponFire(
            level,
            x,
            y,
            z,
            entity,
            itemstack,
            ModItems.COPPER_CHOKE.get(),
            "jaams_weaponry:scattergun_muzzle_shoot",
            "jaams_weaponry:scattergun_shoot",
            ModItems.COPPER_QUICK_DRAW_MAGAZINE.get(),
            "jaams_weaponry:scattergun_magazine_after_shoot",
            "jaams_weaponry:scattergun_after_shoot",
            "jaams_weaponry:bullet_drop",
            2.0
        );
    }

    public static void playShotgunAttachmentSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        handleWeaponFire(
            level,
            x,
            y,
            z,
            entity,
            itemstack,
            ModItems.COPPER_CHOKE.get(),
            "jaams_weaponry:shotgun_choke_shoot",
            "jaams_weaponry:shotgun_shoot",
            ModItems.COPPER_EXTENDED_MAGAZINE.get(),
            "jaams_weaponry:shotgun_magazine_after_shot",
            "jaams_weaponry:shotgun_after_shoot",
            "jaams_weaponry:shotshell_drop",
            3.0
        );
    }

    public static void playRevolverAttachmentSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (ModGuns.getGunType(itemstack) == ModGuns.GunType.PEPPERBOX) {
            playPepperboxAttachmentSound(level, x, y, z, entity, itemstack);
            return;
        }
        handleWeaponFire(
            level,
            x,
            y,
            z,
            entity,
            itemstack,
            null,
            "jaams_weaponry:revolver_shoot",
            "jaams_weaponry:revolver_shoot",
            ModItems.COPPER_QUICK_DRAW_MAGAZINE.get(),
            "jaams_weaponry:revolver_magazine_after_shot",
            "jaams_weaponry:revolver_after_shoot",
            "jaams_weaponry:bullet_drop",
            1.8
        );
    }

    public static void playPepperboxAttachmentSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        handleWeaponFire(
            level,
            x,
            y,
            z,
            entity,
            itemstack,
            ModItems.COPPER_MUZZLE.get(),
            "jaams_weaponry:revolver_muzzle_shoot",
            "jaams_weaponry:revolver_shoot",
            null,
            "jaams_weaponry:revolver_magazine_after_shot",
            "jaams_weaponry:revolver_after_shoot",
            "jaams_weaponry:bullet_drop",
            1.8
        );
    }
}
