package net.jaams.weaponry.gun.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;

public class GunAttachmentHelper {

    public static final int MUZZLE_SLOT = 0;
    public static final int MAGAZINE_SLOT = 2;

    private static final double GENERIC_MUZZLE_DAMAGE_MULTIPLIER = 1.5;
    private static final double GENERIC_MUZZLE_SPEED_MULTIPLIER = 1.5;
    private static final double GENERIC_MAGAZINE_COOLDOWN_MULTIPLIER = 0.5;

    private enum Operation {
        MULTIPLY,
        ADD,
        SUBTRACT
    }

    private GunAttachmentHelper() {
    }

    public static int getMuzzleSlot() {
        return MUZZLE_SLOT;
    }

    public static int getMagazineSlot() {
        return MAGAZINE_SLOT;
    }

    public static List<ItemStack> getAttachments(ItemStack gunStack) {
        List<ItemStack> attachments = new ArrayList<>();
        if (gunStack == null || gunStack.isEmpty()) {
            return attachments;
        }
        gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int[] slots = getAttachmentSlots(gunStack);
            for (int slot : slots) {
                if (slot >= 0 && slot < handler.getSlots()) {
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        attachments.add(stack);
                    }
                }
            }
        });
        return attachments;
    }

    public static int[] getAttachmentSlots(ItemStack gunStack) {
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        if (type == ModGuns.GunType.REVOLVER || type == ModGuns.GunType.PEPPERBOX) {
            return new int[] { MUZZLE_SLOT };
        }
        if (type == ModGuns.GunType.PISTOL || type == ModGuns.GunType.SCATTERGUN || type == ModGuns.GunType.SHOTGUN) {
            return new int[] { MUZZLE_SLOT, MAGAZINE_SLOT };
        }
        return new int[] {};
    }

    public static boolean isAttachment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        if (entry.isPresent()) {
            return true;
        }
        return stack.is(ModTags.ATTACHMENTS) || isMuzzleAttachment(stack) || isMagazineAttachment(stack);
    }

    public static boolean isMuzzleAttachment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        if (entry.isPresent()) {
            String slot = entry.get().slot;
            if (slot.equals("muzzle") || slot.equals("any")) {
                return true;
            }
        }
        return stack.is(ModTags.MUZZLES);
    }

    public static boolean isMagazineAttachment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        if (entry.isPresent() && entry.get().slot.equals("magazine")) {
            return true;
        }
        return stack.is(ModTags.MAGAZINES);
    }

    public static boolean isAnyAttachment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        return entry.isPresent() && entry.get().slot.equals("any");
    }

    public static boolean isAttachmentData(ItemStack stack) {
        return GunItemData.getAttachmentData(stack).isPresent();
    }

    public static String getAttachmentSlot(ItemStack stack) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        return entry.map(e -> e.slot).orElse("");
    }

    public static boolean hasCompatibleGuns(ItemStack stack) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        return entry.isPresent() && entry.get().compatible_guns != null && !entry.get().compatible_guns.isEmpty();
    }

    public static boolean isModMuzzle(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(ModItems.COPPER_MUZZLE.get()) || stack.is(ModItems.COPPER_CHOKE.get());
    }

    public static boolean isModMagazine(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get()) || stack.is(ModItems.COPPER_EXTENDED_MAGAZINE.get());
    }

    public static boolean isModAttachment(ItemStack stack) {
        return isModMuzzle(stack) || isModMagazine(stack);
    }

    private static Operation getAttachmentOperation(ItemStack attachment, String nbtKey) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isPresent() && entry.get().modifiers != null) {
            for (GunItemData.AttachmentModifier mod : entry.get().modifiers) {
                if (mod != null && nbtKey.equals(mod.key) && mod.operation != null) {
                    Operation result;
                    switch (ModEnums.ModifierOperation.fromString(mod.operation)) {
                        case ADD:
                            result = Operation.ADD;
                            break;
                        case SUBTRACT:
                            result = Operation.SUBTRACT;
                            break;
                        default:
                            result = Operation.MULTIPLY;
                            break;
                    }
                    return result;
                }
            }
        }
        ModEnums.ModifierOperation op = ModUtils.getModifierOperation(attachment, nbtKey);
        Operation result;
        switch (op) {
            case ADD:
                result = Operation.ADD;
                break;
            case SUBTRACT:
                result = Operation.SUBTRACT;
                break;
            default:
                result = Operation.MULTIPLY;
                break;
        }
        return result;
    }

    private static Optional<Double> getAttachmentDataDouble(ItemStack attachment, String key) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isEmpty() || entry.get().modifiers == null) {
            return Optional.empty();
        }
        for (GunItemData.AttachmentModifier mod : entry.get().modifiers) {
            if (mod != null && key.equals(mod.key)) {
                return Optional.of(mod.value);
            }
        }
        return Optional.empty();
    }

    private static Optional<Integer> getAttachmentDataInt(ItemStack attachment, String key) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isEmpty() || entry.get().modifiers == null) {
            return Optional.empty();
        }
        for (GunItemData.AttachmentModifier mod : entry.get().modifiers) {
            if (mod != null && key.equals(mod.key)) {
                return Optional.of((int) mod.value);
            }
        }
        return Optional.empty();
    }

    private static double getAttachmentNbtOrDefault(ItemStack attachment, String key, DoubleSupplier defaultSupplier) {
        Optional<Double> dataValue = getAttachmentDataDouble(attachment, key);
        if (dataValue.isPresent()) {
            return dataValue.get();
        }
        if (attachment.hasTag() && attachment.getTag().contains(key)) {
            return attachment.getTag().getDouble(key);
        }
        if (defaultSupplier != null) {
            return defaultSupplier.get();
        }
        return Double.NaN;
    }

    private static int getAttachmentNbtOrDefaultInt(ItemStack attachment, String key, IntSupplier defaultSupplier) {
        Optional<Integer> dataValue = getAttachmentDataInt(attachment, key);
        if (dataValue.isPresent()) {
            return dataValue.get();
        }
        if (attachment.hasTag() && attachment.getTag().contains(key)) {
            return attachment.getTag().getInt(key);
        }
        if (defaultSupplier != null) {
            return defaultSupplier.get();
        }
        return Integer.MIN_VALUE;
    }

    private static double combineAttachmentDoubles(ItemStack gunStack, String nbtKey, Operation operation) {
        List<ItemStack> attachments = getAttachments(gunStack);
        if (attachments.isEmpty()) {
            return operation == Operation.MULTIPLY ? 1.0 : 0.0;
        }
        if (operation == Operation.MULTIPLY) {
            double result = 1.0;
            for (ItemStack attachment : attachments) {
                Operation op = getAttachmentOperation(attachment, nbtKey);
                if (op != Operation.MULTIPLY) {
                    continue;
                }
                double value = getAttachmentNbtOrDefault(attachment, nbtKey, null);
                if (Double.isNaN(value)) {
                    value = getDefaultAttachmentDouble(gunStack, attachment, nbtKey);
                }
                if (!Double.isNaN(value)) {
                    result *= value;
                }
            }
            return result;
        } else {
            double result = 0.0;
            for (ItemStack attachment : attachments) {
                Operation op = getAttachmentOperation(attachment, nbtKey);
                if (op != operation) {
                    continue;
                }
                double value = getAttachmentNbtOrDefault(attachment, nbtKey, null);
                if (Double.isNaN(value)) {
                    value = getDefaultAttachmentDouble(gunStack, attachment, nbtKey);
                }
                if (!Double.isNaN(value)) {
                    result += value;
                }
            }
            return result;
        }
    }

    private static int combineAttachmentInts(ItemStack gunStack, String nbtKey, Operation operation) {
        List<ItemStack> attachments = getAttachments(gunStack);
        if (attachments.isEmpty()) {
            return operation == Operation.MULTIPLY ? 1 : 0;
        }
        if (operation == Operation.MULTIPLY) {
            int result = 1;
            for (ItemStack attachment : attachments) {
                Operation op = getAttachmentOperation(attachment, nbtKey);
                if (op != Operation.MULTIPLY) {
                    continue;
                }
                int value = getAttachmentNbtOrDefaultInt(attachment, nbtKey, null);
                if (value == Integer.MIN_VALUE) {
                    value = getDefaultAttachmentInt(attachment, nbtKey);
                }
                if (value != Integer.MIN_VALUE) {
                    result *= value;
                }
            }
            return result;
        } else {
            int result = 0;
            for (ItemStack attachment : attachments) {
                Operation op = getAttachmentOperation(attachment, nbtKey);
                if (op != operation) {
                    continue;
                }
                int value = getAttachmentNbtOrDefaultInt(attachment, nbtKey, null);
                if (value == Integer.MIN_VALUE) {
                    value = getDefaultAttachmentInt(attachment, nbtKey);
                }
                if (value != Integer.MIN_VALUE) {
                    result += value;
                }
            }
            return result;
        }
    }

    private static double getDefaultAttachmentDouble(ItemStack gunStack, ItemStack attachment, String nbtKey) {
        if (attachment == null || attachment.isEmpty()) {
            return Double.NaN;
        }
        Operation operation = getAttachmentOperation(attachment, nbtKey);
        if (operation != Operation.MULTIPLY) {
            return Double.NaN;
        }
        ModGuns.GunType type = ModGuns.getGunType(gunStack);
        boolean muzzle = isMuzzleAttachment(attachment);
        boolean magazine = isMagazineAttachment(attachment);
        if (!muzzle && !magazine) {
            return Double.NaN;
        }
        if (nbtKey.equals("GunProjectileDamageModifier")) {
            if (muzzle) {
                return GENERIC_MUZZLE_DAMAGE_MULTIPLIER;
            }
        }
        if (nbtKey.equals("GunProjectileSpeed")) {
            if (muzzle) {
                return GENERIC_MUZZLE_SPEED_MULTIPLIER;
            }
        }
        if (nbtKey.equals("GunCooldown")) {
            if (magazine) {
                return GENERIC_MAGAZINE_COOLDOWN_MULTIPLIER;
            }
        }
        return Double.NaN;
    }

    private static int getDefaultAttachmentInt(ItemStack attachment, String nbtKey) {
        return Integer.MIN_VALUE;
    }

    public static double getMuzzleDamageMultiplier(ItemStack gunStack) {
        return getDamageMultiplier(gunStack);
    }

    public static double getMuzzleSpeedMultiplier(ItemStack gunStack) {
        return getSpeedMultiplier(gunStack);
    }

    public static double getMagazineCooldownMultiplier(ItemStack gunStack) {
        return getCooldownMultiplier(gunStack);
    }

    public static double getDamageMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileDamageModifier", Operation.MULTIPLY);
    }

    public static double getDamageAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileDamageModifier", Operation.ADD);
    }

    public static double getDamageSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileDamageModifier", Operation.SUBTRACT);
    }

    public static double getSpeedMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileSpeed", Operation.MULTIPLY);
    }

    public static double getSpeedAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileSpeed", Operation.ADD);
    }

    public static double getSpeedSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileSpeed", Operation.SUBTRACT);
    }

    public static double getKnockbackMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileKnockbackModifier", Operation.MULTIPLY);
    }

    public static double getKnockbackAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileKnockbackModifier", Operation.ADD);
    }

    public static double getKnockbackSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileKnockbackModifier", Operation.SUBTRACT);
    }

    public static double getCooldownMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCooldown", Operation.MULTIPLY);
    }

    public static double getCooldownAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCooldown", Operation.ADD);
    }

    public static double getCooldownSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCooldown", Operation.SUBTRACT);
    }

    public static double getSpreadMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunSpreadAngle", Operation.MULTIPLY);
    }

    public static double getSpreadAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunSpreadAngle", Operation.ADD);
    }

    public static double getSpreadSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunSpreadAngle", Operation.SUBTRACT);
    }

    public static double getInaccuracyMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileInaccuracy", Operation.MULTIPLY);
    }

    public static double getInaccuracyAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileInaccuracy", Operation.ADD);
    }

    public static double getInaccuracySubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunProjectileInaccuracy", Operation.SUBTRACT);
    }

    public static double getRecoilMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunRecoilDistance", Operation.MULTIPLY);
    }

    public static double getRecoilAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunRecoilDistance", Operation.ADD);
    }

    public static double getRecoilSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunRecoilDistance", Operation.SUBTRACT);
    }

    public static double getCrouchRecoilReductionMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCrouchRecoilReduction", Operation.MULTIPLY);
    }

    public static double getCrouchRecoilReductionAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCrouchRecoilReduction", Operation.ADD);
    }

    public static double getCrouchRecoilReductionSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunCrouchRecoilReduction", Operation.SUBTRACT);
    }

    public static double getVerticalRecoilMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunVerticalRecoilMultiplier", Operation.MULTIPLY);
    }

    public static double getVerticalRecoilAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunVerticalRecoilMultiplier", Operation.ADD);
    }

    public static double getVerticalRecoilSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunVerticalRecoilMultiplier", Operation.SUBTRACT);
    }

    public static double getXRotRecoilMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunXRotRecoilIntensity", Operation.MULTIPLY);
    }

    public static double getXRotRecoilAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunXRotRecoilIntensity", Operation.ADD);
    }

    public static double getXRotRecoilSubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunXRotRecoilIntensity", Operation.SUBTRACT);
    }

    public static double getShakeIntensityMultiplier(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunShakeIntensity", Operation.MULTIPLY);
    }

    public static double getShakeIntensityAddend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunShakeIntensity", Operation.ADD);
    }

    public static double getShakeIntensitySubtrahend(ItemStack gunStack) {
        return combineAttachmentDoubles(gunStack, "GunShakeIntensity", Operation.SUBTRACT);
    }

    public static int getPiercingBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectilePiercingModifier", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunProjectilePiercingModifier", Operation.SUBTRACT);
    }

    public static int getPiercingAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectilePiercingModifier", Operation.ADD);
    }

    public static int getPiercingSubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectilePiercingModifier", Operation.SUBTRACT);
    }

    public static int getProjectileCountBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectileCount", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunProjectileCount", Operation.SUBTRACT);
    }

    public static int getProjectileCountAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectileCount", Operation.ADD);
    }

    public static int getProjectileCountSubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunProjectileCount", Operation.SUBTRACT);
    }

    public static int getAmmoConsumptionBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAmmoConsumption", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunAmmoConsumption", Operation.SUBTRACT);
    }

    public static int getAmmoConsumptionAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAmmoConsumption", Operation.ADD);
    }

    public static int getAmmoConsumptionSubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAmmoConsumption", Operation.SUBTRACT);
    }

    public static int getAttachmentConsumptionBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAttachmentConsumption", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunAttachmentConsumption", Operation.SUBTRACT);
    }

    public static int getAttachmentConsumptionAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAttachmentConsumption", Operation.ADD);
    }

    public static int getAttachmentConsumptionSubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunAttachmentConsumption", Operation.SUBTRACT);
    }

    public static int getShakeResetDelayBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunShakeResetDelay", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunShakeResetDelay", Operation.SUBTRACT);
    }

    public static int getShakeResetDelayAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunShakeResetDelay", Operation.ADD);
    }

    public static int getShakeResetDelaySubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunShakeResetDelay", Operation.SUBTRACT);
    }

    public static int getOffhandCooldownBonus(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunOffhandCooldown", Operation.ADD)
                - combineAttachmentInts(gunStack, "GunOffhandCooldown", Operation.SUBTRACT);
    }

    public static int getOffhandCooldownAddend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunOffhandCooldown", Operation.ADD);
    }

    public static int getOffhandCooldownSubtrahend(ItemStack gunStack) {
        return combineAttachmentInts(gunStack, "GunOffhandCooldown", Operation.SUBTRACT);
    }

    public static ModEnums.GunFirePattern getFirePattern(ItemStack gunStack) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && !entry.get().fire_pattern.isEmpty()) {
                ModEnums.GunFirePattern pattern = ModEnums.GunFirePattern
                        .fromString(entry.get().fire_pattern.toUpperCase(Locale.ROOT));
                if (pattern != ModEnums.GunFirePattern.DEFAULT) {
                    return pattern;
                }
            }
            if (attachment.hasTag() && attachment.getTag().contains("GunFirePattern")) {
                String patternStr = attachment.getTag().getString("GunFirePattern");
                ModEnums.GunFirePattern pattern = ModEnums.GunFirePattern.fromString(patternStr);
                if (pattern != ModEnums.GunFirePattern.DEFAULT) {
                    return pattern;
                }
            }
        }
        return null;
    }

    public static String getAttachmentShootSound(ItemStack gunStack) {
        return getAttachmentSound(gunStack, "shoot_sound", GunAttachmentHelper::isMuzzleAttachment, "GunShootSound");
    }

    public static String getAttachmentAfterShootSound(ItemStack gunStack) {
        return getAttachmentSound(gunStack, "after_shoot_sound", GunAttachmentHelper::isMagazineAttachment,
                "GunAfterShootSound");
    }

    public static String getAttachmentBulletDropSound(ItemStack gunStack) {
        return getAttachmentSound(gunStack, "bullet_drop_sound", a -> true, "GunBulletDropSound");
    }

    private static String getAttachmentSound(ItemStack gunStack, String dataField, java.util.function.Predicate<ItemStack> priorityFilter,
            String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        String fallback = null;
        for (ItemStack attachment : attachments) {
            String value = getAttachmentSoundValue(attachment, dataField, nbtKey);
            if (value != null && !value.isEmpty()) {
                if (priorityFilter.test(attachment)) {
                    return value;
                }
                if (fallback == null) {
                    fallback = value;
                }
            }
        }
        return fallback;
    }

    private static String getAttachmentSoundValue(ItemStack attachment, String dataField, String nbtKey) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isPresent() && entry.get().sound != null) {
            String value;
            switch (dataField) {
                case "shoot_sound":
                    value = entry.get().sound.shoot_sound;
                    break;
                case "after_shoot_sound":
                    value = entry.get().sound.after_shoot_sound;
                    break;
                case "bullet_drop_sound":
                    value = entry.get().sound.bullet_drop_sound;
                    break;
                default:
                    value = null;
                    break;
            }
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
            String nbt = attachment.getTag().getString(nbtKey);
            if (!nbt.isEmpty()) {
                return nbt;
            }
        }
        return null;
    }

    public static float getAttachmentSoundVolume(ItemStack gunStack) {
        return getAttachmentFloatProduct(gunStack, "sound_volume", "GunSoundVolume");
    }

    public static float getAttachmentSoundPitch(ItemStack gunStack) {
        return getAttachmentFloatProduct(gunStack, "sound_pitch", "GunSoundPitch");
    }

    private static float getAttachmentFloatProduct(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        float result = -1.0f;
        for (ItemStack attachment : attachments) {
            float value = getAttachmentFloatValue(attachment, dataField, nbtKey);
            if (value != -1.0f) {
                result = result == -1.0f ? value : result * value;
            }
        }
        return result;
    }

    private static float getAttachmentFloatValue(ItemStack attachment, String dataField, String nbtKey) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isPresent() && entry.get().sound != null) {
            float value;
            switch (dataField) {
                case "sound_volume":
                    value = entry.get().sound.sound_volume;
                    break;
                case "sound_pitch":
                    value = entry.get().sound.sound_pitch;
                    break;
                default:
                    value = -1.0f;
                    break;
            }
            if (value != -1.0f) {
                return value;
            }
        }
        if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
            return attachment.getTag().getFloat(nbtKey);
        }
        return -1.0f;
    }

    public static double getAttachmentBulletDropChance(ItemStack gunStack) {
        return getAttachmentDoubleValue(gunStack, "bullet_drop_chance", "GunBulletDropChance");
    }

    public static int getAttachmentAfterShotDelay(ItemStack gunStack) {
        return getAttachmentIntValue(gunStack, "after_shot_delay", "GunAfterShotDelay");
    }

    public static int getAttachmentEmptyCooldown(ItemStack gunStack) {
        return getAttachmentIntValue(gunStack, "empty_cooldown", "GunEmptyCooldown");
    }

    private static double getAttachmentDoubleValue(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && entry.get().sound != null) {
                double value;
                switch (dataField) {
                    case "bullet_drop_chance":
                        value = entry.get().sound.bullet_drop_chance;
                        break;
                    default:
                        value = -1.0;
                        break;
                }
                if (value != -1.0) {
                    return value;
                }
            }
            if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
                return attachment.getTag().getDouble(nbtKey);
            }
        }
        return -1.0;
    }

    private static int getAttachmentIntValue(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && entry.get().sound != null) {
                int value;
                switch (dataField) {
                    case "after_shot_delay":
                        value = entry.get().sound.after_shot_delay;
                        break;
                    case "empty_cooldown":
                        value = entry.get().sound.empty_cooldown;
                        break;
                    default:
                        value = -1;
                        break;
                }
                if (value != -1) {
                    return value;
                }
            }
            if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
                return attachment.getTag().getInt(nbtKey);
            }
        }
        return -1;
    }

    public static String getAttachmentShotParticle(ItemStack gunStack) {
        return getAttachmentParticle(gunStack, "shot_particle", "GunShotParticle");
    }

    public static double getAttachmentShotSize(ItemStack gunStack) {
        return getAttachmentParticleDouble(gunStack, "shot_size", "GunShotSize");
    }

    public static double getAttachmentShotDistance(ItemStack gunStack) {
        return getAttachmentParticleDouble(gunStack, "shot_distance", "GunShotDistance");
    }

    public static int getAttachmentParticleCount(ItemStack gunStack) {
        return getAttachmentParticleInt(gunStack, "particle_count", "GunShotParticleCount");
    }

    private static String getAttachmentParticle(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && entry.get().particle != null) {
                String value;
                switch (dataField) {
                    case "shot_particle":
                        value = entry.get().particle.shot_particle;
                        break;
                    default:
                        value = null;
                        break;
                }
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
            if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
                String nbt = attachment.getTag().getString(nbtKey);
                if (!nbt.isEmpty()) {
                    return nbt;
                }
            }
        }
        return null;
    }

    private static double getAttachmentParticleDouble(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && entry.get().particle != null) {
                double value;
                switch (dataField) {
                    case "shot_size":
                        value = entry.get().particle.shot_size;
                        break;
                    case "shot_distance":
                        value = entry.get().particle.shot_distance;
                        break;
                    default:
                        value = -1.0;
                        break;
                }
                if (value != -1.0) {
                    return value;
                }
            }
            if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
                return attachment.getTag().getDouble(nbtKey);
            }
        }
        return -1.0;
    }

    private static int getAttachmentParticleInt(ItemStack gunStack, String dataField, String nbtKey) {
        List<ItemStack> attachments = getAttachments(gunStack);
        for (ItemStack attachment : attachments) {
            Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
            if (entry.isPresent() && entry.get().particle != null) {
                int value;
                switch (dataField) {
                    case "particle_count":
                        value = entry.get().particle.particle_count;
                        break;
                    default:
                        value = -1;
                        break;
                }
                if (value != -1) {
                    return value;
                }
            }
            if (nbtKey != null && attachment.hasTag() && attachment.getTag().contains(nbtKey)) {
                return attachment.getTag().getInt(nbtKey);
            }
        }
        return -1;
    }

    public static boolean isCompatibleWithGun(ItemStack attachment, ItemStack gunStack) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(attachment);
        if (entry.isEmpty() || entry.get().compatible_guns == null || entry.get().compatible_guns.isEmpty()) {
            return true;
        }
        Item gunItem = gunStack.getItem();
        ResourceLocation gunId = ForgeRegistries.ITEMS.getKey(gunItem);
        if (gunId == null) {
            return false;
        }
        boolean hasPositive = false;
        for (String pattern : entry.get().compatible_guns) {
            if (pattern == null || pattern.isEmpty()) {
                continue;
            }
            boolean negate = pattern.startsWith("!");
            String actual = negate ? pattern.substring(1) : pattern;
            boolean matches = evaluateCompatiblePattern(actual, gunId, gunItem);
            if (negate) {
                if (matches) {
                    return false;
                }
            } else {
                hasPositive = true;
                if (matches) {
                    return true;
                }
            }
        }
        return !hasPositive;
    }

    private static boolean evaluateCompatiblePattern(String pattern, ResourceLocation gunId, Item gunItem) {
        if (pattern.startsWith("regex:")) {
            return gunId.toString().matches(pattern.substring(6));
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return gunId.toString().matches(regex);
        }
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null) {
                return false;
            }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            return new ItemStack(gunItem).is(tagKey);
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(gunId);
    }

    @FunctionalInterface
    private interface DoubleSupplier {
        double get();
    }

    @FunctionalInterface
    private interface IntSupplier {
        int get();
    }
}
