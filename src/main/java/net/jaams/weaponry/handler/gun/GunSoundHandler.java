package net.jaams.weaponry.handler.gun;

import java.util.concurrent.atomic.AtomicReference;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.init.ModItems;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static void playScattergunOneBulletSound(LevelAccessor level, double x, double y, double z, Entity entity, ItemStack itemstack) {
        playEmptyWeaponSound(level, x, y, z, entity, itemstack, "jaams_weaponry:scattergun_empty");
    }

    private static ItemStack getItemStackFromSlot(int slotId, ItemStack itemStack) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent((capability) -> result.set(capability.getStackInSlot(slotId).copy()));
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
            location = new ResourceLocation(soundId);
        } else {
            location = new ResourceLocation("jaams_weaponry", soundId);
        }
        return ForgeRegistries.SOUND_EVENTS.getValue(location);
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
        float volume = getFinalFloat(itemstack, "GunSoundVolume", soundData != null ? soundData.sound_volume : -1.0f, 1.0f);
        float pitch = getFinalFloat(itemstack, "GunSoundPitch", soundData != null ? soundData.sound_pitch : -1.0f, 1.0f);
        double bulletDropChance = getFinalDouble(itemstack, "GunBulletDropChance", soundData != null ? soundData.bullet_drop_chance : -1.0, 0.7);
        int afterShotDelay = getFinalInt(itemstack, "GunAfterShotDelay", soundData != null ? soundData.after_shot_delay : -1, 10);
        int emptyCooldown = getFinalInt(itemstack, "GunEmptyCooldown", soundData != null ? soundData.empty_cooldown : -1, 20);
        String finalShootSound = getFinalSound(itemstack, "GunShootSound", soundData != null ? soundData.shoot_sound : "", getItemStackFromSlot(0, itemstack).getItem() == muzzleAttachment ? muzzleShootSound : defaultShootSound);
        String finalAfterShootSound = getFinalSound(itemstack, "GunAfterShootSound", soundData != null ? soundData.after_shoot_sound : "", getItemStackFromSlot(2, itemstack).getItem() == magazineAttachment ? magazineAfterShootSound : defaultAfterShootSound);
        String finalDropSound = getFinalSound(itemstack, "GunBulletDropSound", soundData != null ? soundData.bullet_drop_sound : "", dropSound);
        playSound(level, x, y, z, finalShootSound, volume, pitch);
        JaamsWeaponryMod.queueServerWork(afterShotDelay, () -> {
            playSound(level, x, y, z, finalAfterShootSound, volume, pitch);
            if (Math.random() < bulletDropChance) {
                playSound(level, x, y, z, finalDropSound, volume, pitch);
            }
        });
    }

    private static String getFinalSound(ItemStack gunStack, String nbtKey, String jsonValue, String defaultValue) {
        if (gunStack.hasTag() && gunStack.getTag().contains(nbtKey)) {
            String nbt = gunStack.getTag().getString(nbtKey);
            if (!nbt.isEmpty()) return nbt;
        }
        if (jsonValue != null && !jsonValue.isEmpty()) {
            return jsonValue;
        }
        return defaultValue != null ? defaultValue : "";
    }

    private static float getFinalFloat(ItemStack gunStack, String nbtKey, float jsonValue, float defaultValue) {
        if (gunStack.hasTag() && gunStack.getTag().contains(nbtKey)) {
            return gunStack.getTag().getFloat(nbtKey);
        }
        return jsonValue != -1.0f ? jsonValue : defaultValue;
    }

    private static double getFinalDouble(ItemStack gunStack, String nbtKey, double jsonValue, double defaultValue) {
        if (gunStack.hasTag() && gunStack.getTag().contains(nbtKey)) {
            return gunStack.getTag().getDouble(nbtKey);
        }
        return jsonValue != -1.0 ? jsonValue : defaultValue;
    }

    private static int getFinalInt(ItemStack gunStack, String nbtKey, int jsonValue, int defaultValue) {
        if (gunStack.hasTag() && gunStack.getTag().contains(nbtKey)) {
            return gunStack.getTag().getInt(nbtKey);
        }
        return jsonValue != -1 ? jsonValue : defaultValue;
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
}
