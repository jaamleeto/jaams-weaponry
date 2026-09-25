package net.jaams.weaponry.util;

import net.jaams.weaponry.capability.CapHelper;

import net.jaams.weaponry.util.ModComponents;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import net.jaams.weaponry.JaamsWeaponryMod;

import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.loader.GunModifierLoader;
import net.jaams.weaponry.entity.BulletProjectileEntity;
import net.jaams.weaponry.entity.EchoBulletProjectileEntity;
import net.jaams.weaponry.entity.FireBulletProjectileEntity;
import net.jaams.weaponry.entity.GlowingBulletProjectileEntity;
import net.jaams.weaponry.entity.HeavyBulletProjectileEntity;
import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.entity.SharpBulletProjectileEntity;
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
import net.jaams.weaponry.gun.shoot.DefaultShoot;
import net.jaams.weaponry.gun.shoot.PepperboxShoot;
import net.jaams.weaponry.gun.shoot.PistolShoot;
import net.jaams.weaponry.gun.shoot.RevolverShoot;
import net.jaams.weaponry.gun.shoot.ScattergunShoot;
import net.jaams.weaponry.gun.shoot.ShotgunShoot;
import net.jaams.weaponry.handler.gun.GunActionsHandler;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.init.ModSounds;

import net.jaams.weaponry.packet.GunInventoryPacket;
import net.jaams.weaponry.packet.GunShootPacket;
import net.jaams.weaponry.world.inventory.GunGUIMenu;
import net.jaams.weaponry.world.inventory.PepperboxGUIMenu;
import net.jaams.weaponry.world.inventory.PistolGUIMenu;
import net.jaams.weaponry.world.inventory.RevolverGUIMenu;
import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.world.inventory.ShotgunGUIMenu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.items.IItemHandler;

import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ModGuns {
    /** Version marker for the server-authoritative gun menu extra data. */
    public static final int GUN_MENU_DATA_VERSION = 0x7F;

    public static boolean isGun(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("GunType", CompoundTag.TAG_STRING)) {
            String customType = ModComponents.get(stack).getString("GunType").toUpperCase().trim();
            boolean hasCustomGunType = switch (customType) {
                case "PISTOL", "SCATTERGUN", "SHOTGUN", "GUN", "REVOLVER", "PEPPERBOX" -> true;
                default -> false;
            };
            return hasCustomGunType && !GunModifierLoader.INSTANCE.isGunExplicitlyDisabled(stack);
        }
        if (inAnyGunTags(stack)) {
            return !GunModifierLoader.INSTANCE.isGunExplicitlyDisabled(stack);
        }
        return GunItemData.getData(stack).isPresent();
    }

    public static GunType getGunType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("GunType", CompoundTag.TAG_STRING)) {
            String customType = ModComponents.get(stack).getString("GunType").toUpperCase().trim();
            return switch (customType) {
                case "PISTOL" -> GunType.PISTOL;
                case "SCATTERGUN" -> GunType.SCATTERGUN;
                case "SHOTGUN" -> GunType.SHOTGUN;
                case "GUN" -> GunType.GUN;
                case "REVOLVER" -> GunType.REVOLVER;
                case "PEPPERBOX" -> GunType.PEPPERBOX;
                default -> null;
            };
        }
        GunType tagType = gunTypeFromTags(stack);
        if (tagType != null) {
            return tagType;
        }
        return GunItemData.getData(stack)
                .map((data) -> getGunTypeFromString(data.gun.gun_type))
                .orElse(null);
    }

    private static boolean inAnyGunTags(ItemStack stack) {
        return gunTypeFromTags(stack) != null;
    }

    private static GunType gunTypeFromTags(ItemStack stack) {
        if (stack.is(ModTags.PISTOLS))
            return GunType.PISTOL;
        if (stack.is(ModTags.SCATTERGUNS))
            return GunType.SCATTERGUN;
        if (stack.is(ModTags.SHOTGUNS))
            return GunType.SHOTGUN;
        if (stack.is(ModTags.REVOLVERS))
            return GunType.REVOLVER;
        if (stack.is(ModTags.PEPPERBOXES))
            return GunType.PEPPERBOX;
        if (stack.is(ModTags.GUNS))
            return GunType.GUN;
        return null;
    }

    private static GunType getGunTypeFromString(String type) {
        if (type == null)
            return null;
        return switch (type.toUpperCase().trim()) {
            case "PISTOL" -> GunType.PISTOL;
            case "SCATTERGUN" -> GunType.SCATTERGUN;
            case "SHOTGUN" -> GunType.SHOTGUN;
            case "GUN" -> GunType.GUN;
            case "REVOLVER" -> GunType.REVOLVER;
            case "PEPPERBOX" -> GunType.PEPPERBOX;
            default -> null;
        };
    }

    public static boolean canOpenInventory(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !isGun(stack)
                || !GunSystemCommonConfig.GUN_INVENTORY.get())
            return false;
        GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
        if (gunData != null && gunData.open_inventory != null) {
            return gunData.open_inventory;
        }
        if (ModComponents.has(stack)) {
            CompoundTag tag = ModComponents.get(stack);
            if (tag != null && tag.contains("OpenInventory", CompoundTag.TAG_BYTE)) {
                return tag.getBoolean("OpenInventory");
            }
        }
        return true;
    }

    /**
     * Compares a held gun with the stack used to create its inventory menu. The
     * container component is deliberately ignored because it changes on every
     * insertion/extraction. The server additionally checks the original stack
     * reference in the menu, so replacing the gun in the hand cannot retarget an
     * existing menu.
     */
    public static boolean isSameGunForInventory(ItemStack current, ItemStack bound) {
        if (current == null || bound == null || current.isEmpty() || bound.isEmpty())
            return false;
        if (current.getItem() != bound.getItem() || current.getCount() != bound.getCount())
            return false;
        return ItemStack.isSameItemSameComponents(withoutGunInventory(current), withoutGunInventory(bound));
    }

    private static ItemStack withoutGunInventory(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.remove(DataComponents.CONTAINER);
        CompoundTag tag = ModComponents.get(copy);
        if (tag != null) {
            tag.remove("Inventory");
            // Chamber rotation is runtime state, not the identity of the gun being
            // edited. It may change while a menu is open.
            tag.remove("RevolverCurrentChamber");
            ModComponents.set(copy, tag.isEmpty() ? null : tag);
        }
        return copy;
    }

    public static void shoot(Level world, ServerPlayer player, ItemStack itemStack) {
        GunType type = getGunType(itemStack);
        if (type == null) {
            return;
        }
        switch (type) {
            case PISTOL:
                PistolShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            case SCATTERGUN:
                ScattergunShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            case SHOTGUN:
                ShotgunShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            case REVOLVER:
                RevolverShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            case PEPPERBOX:
                PepperboxShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            case GUN:
                DefaultShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
            default:
                DefaultShoot.shoot(world, player.getX(), player.getY(), player.getZ(), player, itemStack);
                break;
        }
    }

    public enum GunType {
        GUN,
        PISTOL,
        SCATTERGUN,
        SHOTGUN,
        REVOLVER,
        PEPPERBOX
    }

    public static void spawnProjectile(
            Level level,
            LivingEntity shooter,
            ItemStack gunStack,
            ItemStack ammoItem,
            int projectileCount,
            double spreadAngle,
            double projectileSpeed,
            double inaccuracy,
            double damageModifier,
            double knockbackModifier,
            int piercingModifier) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel))
            return;
        int ammoConsumption = Math.max(0,
                ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption",
                        GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION::get)
                        + GunAttachmentHelper.getAmmoConsumptionAddend(gunStack)
                        - GunAttachmentHelper.getAmmoConsumptionSubtrahend(gunStack));
        double finalSpreadAngle = ModUtils.getConfigOrNbtDouble(gunStack, "GunSpreadAngle", () -> spreadAngle)
                * GunAttachmentHelper.getSpreadMultiplier(gunStack)
                + GunAttachmentHelper.getSpreadAddend(gunStack)
                - GunAttachmentHelper.getSpreadSubtrahend(gunStack);
        double finalSpeed = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileSpeed", () -> projectileSpeed)
                * GunAttachmentHelper.getSpeedMultiplier(gunStack)
                + GunAttachmentHelper.getSpeedAddend(gunStack)
                - GunAttachmentHelper.getSpeedSubtrahend(gunStack);
        if (ammoItem.getItem() == ModItems.HEAVY_BULLET.get()
                || ammoItem.getItem() == ModItems.HEAVY_SHOTSHELL.get()) {
            finalSpeed *= 0.5;
        }
        double finalInaccuracy = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileInaccuracy", () -> inaccuracy)
                * GunAttachmentHelper.getInaccuracyMultiplier(gunStack)
                + GunAttachmentHelper.getInaccuracyAddend(gunStack)
                - GunAttachmentHelper.getInaccuracySubtrahend(gunStack);
        double finalDamage = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileDamageModifier",
                () -> damageModifier) * GunAttachmentHelper.getDamageMultiplier(gunStack)
                + GunAttachmentHelper.getDamageAddend(gunStack)
                - GunAttachmentHelper.getDamageSubtrahend(gunStack);
        double finalKnockback = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileKnockbackModifier",
                () -> knockbackModifier) * GunAttachmentHelper.getKnockbackMultiplier(gunStack)
                + GunAttachmentHelper.getKnockbackAddend(gunStack)
                - GunAttachmentHelper.getKnockbackSubtrahend(gunStack);
        int finalPiercing = Math.max(0,
                ModUtils.getConfigOrNbtInt(gunStack, "GunProjectilePiercingModifier", () -> piercingModifier)
                        + GunAttachmentHelper.getPiercingAddend(gunStack)
                        - GunAttachmentHelper.getPiercingSubtrahend(gunStack));
        int finalProjectileCount = Math.max(1,
                ModUtils.getConfigOrNbtInt(gunStack, "GunProjectileCount", () -> projectileCount)
                        + GunAttachmentHelper.getProjectileCountAddend(gunStack)
                        - GunAttachmentHelper.getProjectileCountSubtrahend(gunStack));
        ModEnums.GunFirePattern pattern = getFirePattern(gunStack);
        Vec3 eyePos = shooter.getEyePosition(1.0F);
        Vec3 viewDir = shooter.getViewVector(1.0F);
        Vec3 upVector = shooter.getUpVector(1.0F);
        Vec3 rightVector = viewDir.cross(upVector).normalize();
        Vec3 spawnPos = eyePos.add(viewDir.scale(0.6)).add(0, -0.08, 0);
        boolean isCreative = shooter instanceof Player p && p.isCreative();
        if (ammoConsumption >= finalProjectileCount) {
            ammoConsumption = finalProjectileCount;
        }
        for (int i = 0; i < finalProjectileCount; i++) {
            Entity projectile = createProjectileForAmmo(level, shooter, gunStack, ammoItem);
            if (projectile == null)
                continue;
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            Vec3 direction = getDirectionForPattern(viewDir, upVector, rightVector, pattern, finalSpreadAngle, i,
                    finalProjectileCount, serverLevel);
            if (finalInaccuracy > 0.0) {
                Vec3 inaccRight = direction.cross(upVector).normalize();
                Vec3 inaccUp = direction.cross(inaccRight).normalize();
                double inaccX = serverLevel.random.nextGaussian() * finalInaccuracy;
                double inaccY = serverLevel.random.nextGaussian() * finalInaccuracy;
                direction = direction.add(inaccRight.scale(inaccX)).add(inaccUp.scale(inaccY)).normalize();
            }
            direction = direction.scale(finalSpeed);
            boolean isMultishotClone = !isCreative && i >= ammoConsumption;
            setupProjectile(projectile, shooter, gunStack, ammoItem, finalDamage, finalKnockback, finalPiercing,
                    direction, isMultishotClone);
            serverLevel.addFreshEntity(projectile);
        }
    }

    public static boolean isRevolverLikeGun(ItemStack gunStack) {
        GunType type = getGunType(gunStack);
        return type == GunType.REVOLVER || type == GunType.PEPPERBOX;
    }

    public static boolean isPepperboxGun(ItemStack gunStack) {
        return getGunType(gunStack) == GunType.PEPPERBOX;
    }

    public static double getMuzzleSpeedMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getMuzzleSpeedMultiplier(gunStack);
    }

    public static double getMuzzleDamageMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getMuzzleDamageMultiplier(gunStack);
    }

    public static double getMagazineCooldownMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getMagazineCooldownMultiplier(gunStack);
    }

    public static double getAttachmentDamageMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getDamageMultiplier(gunStack);
    }

    public static double getAttachmentDamageAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getDamageAddend(gunStack);
    }

    public static double getAttachmentDamageSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getDamageSubtrahend(gunStack);
    }

    public static double getAttachmentSpeedMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getSpeedMultiplier(gunStack);
    }

    public static double getAttachmentSpeedAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getSpeedAddend(gunStack);
    }

    public static double getAttachmentSpeedSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getSpeedSubtrahend(gunStack);
    }

    public static double getAttachmentKnockbackMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getKnockbackMultiplier(gunStack);
    }

    public static double getAttachmentKnockbackAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getKnockbackAddend(gunStack);
    }

    public static double getAttachmentKnockbackSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getKnockbackSubtrahend(gunStack);
    }

    public static double getAttachmentCooldownMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getCooldownMultiplier(gunStack);
    }

    public static double getAttachmentCooldownAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getCooldownAddend(gunStack);
    }

    public static double getAttachmentCooldownSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getCooldownSubtrahend(gunStack);
    }

    public static double getAttachmentSpreadMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getSpreadMultiplier(gunStack);
    }

    public static double getAttachmentSpreadAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getSpreadAddend(gunStack);
    }

    public static double getAttachmentSpreadSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getSpreadSubtrahend(gunStack);
    }

    public static double getAttachmentInaccuracyMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getInaccuracyMultiplier(gunStack);
    }

    public static double getAttachmentInaccuracyAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getInaccuracyAddend(gunStack);
    }

    public static double getAttachmentInaccuracySubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getInaccuracySubtrahend(gunStack);
    }

    public static double getAttachmentRecoilMultiplier(ItemStack gunStack) {
        return GunAttachmentHelper.getRecoilMultiplier(gunStack);
    }

    public static double getAttachmentRecoilAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getRecoilAddend(gunStack);
    }

    public static double getAttachmentRecoilSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getRecoilSubtrahend(gunStack);
    }

    public static int getAttachmentPiercingBonus(ItemStack gunStack) {
        return GunAttachmentHelper.getPiercingBonus(gunStack);
    }

    public static int getAttachmentPiercingAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getPiercingAddend(gunStack);
    }

    public static int getAttachmentPiercingSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getPiercingSubtrahend(gunStack);
    }

    public static int getAttachmentProjectileCountBonus(ItemStack gunStack) {
        return GunAttachmentHelper.getProjectileCountBonus(gunStack);
    }

    public static int getAttachmentProjectileCountAddend(ItemStack gunStack) {
        return GunAttachmentHelper.getProjectileCountAddend(gunStack);
    }

    public static int getAttachmentProjectileCountSubtrahend(ItemStack gunStack) {
        return GunAttachmentHelper.getProjectileCountSubtrahend(gunStack);
    }

    private static ModEnums.GunFirePattern getFirePattern(ItemStack gunStack) {
        String nbtPattern = ModUtils.getConfigOrNbtString(gunStack, "GunFirePattern", () -> null);
        if (nbtPattern != null && !nbtPattern.isEmpty()) {
            ModEnums.GunFirePattern pattern = ModEnums.GunFirePattern.fromString(nbtPattern);
            if (pattern != ModEnums.GunFirePattern.DEFAULT) {
                return pattern;
            }
        }
        ModEnums.GunFirePattern attachmentPattern = GunAttachmentHelper.getFirePattern(gunStack);
        if (attachmentPattern != null) {
            return attachmentPattern;
        }
        GunItemData.ShootEntry shootData = GunItemData.getShootData(gunStack);
        if (shootData != null && shootData.fire_pattern != null && !shootData.fire_pattern.isEmpty()) {
            ModEnums.GunFirePattern pattern = ModEnums.GunFirePattern.fromString(shootData.fire_pattern);
            if (pattern != ModEnums.GunFirePattern.DEFAULT) {
                return pattern;
            }
        }
        ModGuns.GunType gunType = ModGuns.getGunType(gunStack);
        return getDefaultFirePatternForType(gunType);
    }

    private static ModEnums.GunFirePattern getDefaultFirePatternForType(ModGuns.GunType gunType) {
        return switch (gunType) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_FIRE_PATTERN.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_FIRE_PATTERN.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_FIRE_PATTERN.get();
            case REVOLVER -> GunSystemCommonConfig.GUN_REVOLVER_SHOOT_FIRE_PATTERN.get();
            case PEPPERBOX -> GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_FIRE_PATTERN.get();
            case GUN -> ModEnums.GunFirePattern.DEFAULT;
            default -> ModEnums.GunFirePattern.DEFAULT;
        };
    }

    private static Vec3 getDirectionForPattern(Vec3 viewDir, Vec3 upVector, Vec3 rightVector,
            ModEnums.GunFirePattern pattern, double spreadAngle, int index, int total, ServerLevel serverLevel) {
        if (total <= 1 || spreadAngle <= 0) {
            return viewDir;
        }
        double rad = Math.toRadians(spreadAngle);
        switch (pattern) {
            case CIRCLE:
                double angle = ((double) index / total) * 2 * Math.PI;
                Vec3 circleOffset = rightVector.scale(Math.cos(angle) * rad).add(upVector.scale(Math.sin(angle) * rad));
                return viewDir.add(circleOffset).normalize();
            case HEART:
                double heartAngle = (index * 2 * Math.PI) / total;
                double heartX = 16 * Math.pow(Math.sin(heartAngle), 3);
                double heartY = 13 * Math.cos(heartAngle) - 5 * Math.cos(2 * heartAngle) - 2 * Math.cos(3 * heartAngle)
                        - Math.cos(4 * heartAngle);
                double scale = rad / 20.0;
                Vec3 heartOffset = rightVector.scale(heartX * scale).add(upVector.scale(heartY * scale));
                return viewDir.add(heartOffset).normalize();
            case HORIZONTAL:
                double horRotation = (spreadAngle / (total - 1)) * (index - (total - 1) / 2.0);
                Quaternionf horQuat = new Quaternionf().setAngleAxis(Math.toRadians(horRotation), (float) upVector.x,
                        (float) upVector.y, (float) upVector.z);
                Vector3f horVec = viewDir.toVector3f().rotate(horQuat);
                return new Vec3(horVec.x(), horVec.y(), horVec.z());
            case VERTICAL:
                double vertRotation = (spreadAngle / (total - 1)) * (index - (total - 1) / 2.0);
                Quaternionf vertQuat = new Quaternionf().setAngleAxis(Math.toRadians(vertRotation),
                        (float) rightVector.x, (float) rightVector.y, (float) rightVector.z);
                Vector3f vertVec = viewDir.toVector3f().rotate(vertQuat);
                return new Vec3(vertVec.x(), vertVec.y(), vertVec.z());
            case DEFAULT:
            default:
                double defaultRotation = (spreadAngle / (total - 1)) * (index - (total - 1) / 2.0);
                Quaternionf defQuat = new Quaternionf().setAngleAxis(Math.toRadians(defaultRotation),
                        (float) rightVector.x, (float) rightVector.y, (float) rightVector.z);
                Vector3f defVec = viewDir.toVector3f().rotate(defQuat);
                return new Vec3(defVec.x(), defVec.y(), defVec.z());
        }
    }

    private static Entity createProjectileForAmmo(Level level, LivingEntity shooter, ItemStack gunStack,
            ItemStack ammoItem) {
        if (ammoItem.isEmpty()) {
            return new BulletProjectileEntity(level, shooter, gunStack);
        }
        Item item = ammoItem.getItem();
        if (item instanceof ArrowItem arrowItem) {
            AbstractArrow arrow = arrowItem.createArrow(level, ammoItem, shooter, gunStack);
            return customArrow(arrow, gunStack);
        }
        if (item == Items.FIREWORK_ROCKET) {
            return new FireworkRocketEntity(level, ammoItem.copy(), shooter, shooter.getX(), shooter.getEyeY() - 0.15D,
                    shooter.getZ(), true);
        }
        if (item == ModItems.BULLET.get() || item == ModItems.SHOTSHELL.get()) {
            return new BulletProjectileEntity(level, shooter, gunStack);
        }
        if (item == ModItems.FIRE_BULLET.get() || item == ModItems.FIRE_SHOTSHELL.get()) {
            return new FireBulletProjectileEntity(level, shooter, gunStack);
        }
        if (item == ModItems.HEAVY_BULLET.get() || item == ModItems.HEAVY_SHOTSHELL.get()) {
            return new HeavyBulletProjectileEntity(level, shooter, gunStack);
        }
        if (item == ModItems.GLOWING_BULLET.get() || item == ModItems.GLOWING_SHOTSHELL.get()) {
            return new GlowingBulletProjectileEntity(level, shooter, gunStack);
        }
        if (item == ModItems.ECHO_BULLET.get() || item == ModItems.ECHO_SHOTSHELL.get()) {
            return new EchoBulletProjectileEntity(level, shooter, gunStack);
        }
        if (item == ModItems.SHARP_BULLET.get() || item == ModItems.SHARP_SHOTSHELL.get()) {
            return new SharpBulletProjectileEntity(level, shooter, gunStack);
        }
        return new ItemProjectileEntity(level, shooter, gunStack);
    }

    private static void setupProjectile(Entity proj, LivingEntity owner, ItemStack gunStack, ItemStack ammoItem,
            double damage, double knockback, int piercing, Vec3 velocity, boolean isMultishotClone) {
        if (proj instanceof Projectile projectile) {
            projectile.setOwner(owner);
        }
        if (proj instanceof FireworkRocketEntity firework) {
            firework.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0.0F);
            firework.getPersistentData().putBoolean("ShotAtAngle", true);
            if (isMultishotClone)
                firework.getPersistentData().putBoolean("IsMultishotClone", true);
            return;
        }
        if (proj instanceof ItemProjectileEntity itemProj) {
            itemProj.setSourceItem(gunStack);
            itemProj.setProjectileItem(ammoItem.isEmpty() ? ItemStack.EMPTY : ammoItem.copy());
            itemProj.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0.0F);
            if (damage > 0)
                itemProj.setProjectileDamage((float) damage);
            if (knockback > 0)
                itemProj.setProjectileKnockback((float) knockback);
            if (piercing > 0)
                itemProj.setPiercingLevel(piercing);
            if (isMultishotClone)
                itemProj.getPersistentData().putBoolean("IsMultishotClone", true);
            return;
        }
        if (proj instanceof BaseBulletProjectileEntity bullet) {
            bullet.setGunItem(gunStack);
            bullet.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0.0F);
            if (damage > 0)
                bullet.setBulletDamage((float) damage);
            if (knockback > 0)
                bullet.setBulletKnockback((float) knockback);
            if (piercing > 0)
                bullet.setPiercingLevel((int) piercing);
            if (isMultishotClone)
                bullet.getPersistentData().putBoolean("IsMultishotClone", true);
            return;
        }
        if (proj instanceof AbstractArrow arrow) {
            arrow.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0.0F);
            if (damage > 0)
                arrow.setBaseDamage(damage);
            if (piercing > 0)
                ((net.jaams.weaponry.mixins.access.AbstractArrowAccessorMixin) (Object) arrow).invokeSetPierceLevel((byte) Math.min(piercing, 127));
            if (isMultishotClone)
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            return;
        }
    }

    protected static AbstractArrow customArrow(AbstractArrow arrow, ItemStack gunStack) {
        double damageMod = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileDamageModifier", () -> 1.0);
        double knockbackMod = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileKnockbackModifier", () -> 1.0);
        int piercing = ModUtils.getConfigOrNbtInt(gunStack, "GunProjectilePiercingModifier", () -> 0);
        if (damageMod != 1.0) {
            arrow.setBaseDamage(arrow.getBaseDamage() * damageMod);
        }
        if (piercing > 0) {
            ((net.jaams.weaponry.mixins.access.AbstractArrowAccessorMixin) (Object) arrow).invokeSetPierceLevel((byte) piercing);
        }
        return arrow;
    }

    public static boolean canPlaceInGunSlot(ItemStack gunStack, ItemStack toPlace, GunType type, int slot) {
        if (gunStack == null || toPlace == null || gunStack.isEmpty() || toPlace.isEmpty()
                || type == null || slot < 0 || slot >= getGunSlotCount(type)) {
            return false;
        }
        // 1) Per-stack GunSlotRules are authoritative: when a slot has explicit
        //    rules they decide the answer and never fall through to defaults.
        if (ModComponents.has(gunStack)) {
            CompoundTag tag = ModComponents.get(gunStack);
            if (tag != null && tag.contains("GunSlotRules")) {
                List<String> ruleList = readSlotRuleList(tag.getCompound("GunSlotRules"), slot);
                if (!ruleList.isEmpty()) {
                    return evaluateRules(ruleList, toPlace);
                }
            }
        }
        // 2) gun_modifier datapack rules are authoritative as well.
        GunItemData.GunEntry gunData = GunItemData.getGunData(gunStack);
        if (gunData != null && gunData.slot_rules != null && gunData.slot_rules.containsKey(slot)) {
            List<String> rulesList = gunData.slot_rules.get(slot);
            if (rulesList != null && !rulesList.isEmpty()) {
                return evaluateRules(rulesList, toPlace);
            }
        }
        // 3) Built-in per-slot defaults.
        return getDefaultSlotRule(gunStack, type, slot, toPlace);
    }

    private static List<String> readSlotRuleList(CompoundTag rules, int slot) {
        List<String> ruleList = new ArrayList<>();
        if (rules == null || !rules.contains("Slot" + slot)) {
            return ruleList;
        }
        Tag value = rules.get("Slot" + slot);
        if (value instanceof ListTag listTag) {
            for (Tag t : listTag) {
                if (t instanceof StringTag stringTag) {
                    String rule = stringTag.getAsString();
                    if (rule != null && !rule.trim().isEmpty()) {
                        ruleList.add(rule.trim());
                    }
                }
            }
        } else if (value instanceof StringTag stringTag) {
            String rule = stringTag.getAsString();
            if (rule != null && !rule.trim().isEmpty()) {
                ruleList.add(rule.trim());
            }
        }
        return ruleList;
    }

    /**
     * Evaluates a per-slot rule list. The list enumerates the acceptable items/tags
     * (OR semantics); "any" allows everything and an entry prefixed with "!" acts as
     * a deny rule that overrides everything else. Returns {@code null} only when the
     * list contains no usable rules, so callers can fall through to the next source.
     */
    private static Boolean evaluateRules(List<String> rulesList, ItemStack stack) {
        boolean hasAny = false;
        boolean hasPositiveMatch = false;
        boolean hasUsableRule = false;
        for (String ruleStr : rulesList) {
            if (ruleStr == null)
                continue;
            String rule = ruleStr.trim();
            if (rule.isEmpty())
                continue;
            boolean negated = rule.startsWith("!");
            if (negated)
                rule = rule.substring(1).trim();
            if (rule.isEmpty())
                continue;
            if ("any".equalsIgnoreCase(rule)) {
                hasUsableRule = true;
                if (!negated) {
                    hasAny = true;
                }
                continue;
            }
            if (rule.startsWith("#")) {
                ResourceLocation location = ResourceLocation.tryParse(rule.substring(1).trim());
                if (location == null)
                    continue;
                hasUsableRule = true;
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, location);
                boolean matches = stack.is(tagKey);
                if (negated) {
                    if (matches)
                        return false;
                } else if (matches) {
                    hasPositiveMatch = true;
                }
                continue;
            }
            ResourceLocation itemId = ResourceLocation.tryParse(rule);
            if (itemId == null)
                continue;
            Item allowedItem = BuiltInRegistries.ITEM.get(itemId);
            if (allowedItem == null || allowedItem == Items.AIR)
                continue;
            hasUsableRule = true;
            boolean matches = stack.getItem() == allowedItem;
            if (negated) {
                if (matches)
                    return false;
            } else if (matches) {
                hasPositiveMatch = true;
            }
        }
        if (!hasUsableRule)
            return null;
        return hasAny || hasPositiveMatch;
    }

    private static boolean getDefaultSlotRule(ItemStack gunStack, GunType type, int slot, ItemStack toPlace) {
        return switch (type) {
            case PISTOL -> switch (slot) {
                case 0 -> isMuzzleAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_MUZZLE.get());
                case 1 -> toPlace.is(ModTags.BULLETS);
                case 2 -> isMagazineAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
                default -> false;
            };
            case SCATTERGUN -> switch (slot) {
                case 0 -> isMuzzleAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_CHOKE.get());
                case 1 -> toPlace.is(ModTags.BULLETS);
                case 2 -> isMagazineAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
                default -> false;
            };
            case SHOTGUN -> switch (slot) {
                case 0 -> isMuzzleAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_CHOKE.get());
                case 1 -> toPlace.is(ModTags.SHOTSHELLS);
                case 2 -> isMagazineAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_EXTENDED_MAGAZINE.get());
                default -> false;
            };
            case REVOLVER -> switch (slot) {
                case 0 -> isMagazineAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
                case 1, 2, 3, 4, 5, 6 -> toPlace.is(ModTags.BULLETS);
                default -> false;
            };
            case PEPPERBOX -> switch (slot) {
                case 0 -> isMuzzleAttachmentForGun(toPlace, gunStack, type) || toPlace.is(ModItems.COPPER_MUZZLE.get());
                case 1, 2, 3, 4, 5, 6 -> toPlace.is(ModTags.BULLETS);
                default -> false;
            };
            case GUN -> slot == 1 && toPlace.is(ModTags.BULLETS);
            default -> false;
        };
    }

    private static boolean isMuzzleAttachmentForGun(ItemStack toPlace, ItemStack gunStack, GunType type) {
        if (!GunAttachmentHelper.isMuzzleAttachment(toPlace)
                && !GunAttachmentHelper.isAnyAttachment(toPlace)
                && !isNbtCustomAttachment(toPlace)) {
            return false;
        }
        return GunAttachmentHelper.isCompatibleWithGun(toPlace, gunStack);
    }

    private static boolean isMagazineAttachmentForGun(ItemStack toPlace, ItemStack gunStack, GunType type) {
        if (!GunAttachmentHelper.isMagazineAttachment(toPlace)
                && !GunAttachmentHelper.isAnyAttachment(toPlace)
                && !isNbtCustomAttachment(toPlace)) {
            return false;
        }
        return GunAttachmentHelper.isCompatibleWithGun(toPlace, gunStack);
    }

    /**
     * Recognized gun-modifier NBT keys. An arbitrary item only qualifies as a
     * "custom attachment" when it actually carries one of these markers, so a plain
     * block/tool cannot be stuffed into an attachment slot.
     */
    private static final Set<String> ATTACHMENT_MODIFIER_KEYS = Set.of(
            "GunProjectileDamageModifier",
            "GunProjectileSpeed",
            "GunProjectileInaccuracy",
            "GunProjectileKnockbackModifier",
            "GunProjectilePiercingModifier",
            "GunProjectileCount",
            "GunCooldown",
            "GunOffhandCooldown",
            "GunRecoilDistance",
            "GunCrouchRecoilReduction",
            "GunVerticalRecoilMultiplier",
            "GunXRotRecoilIntensity",
            "GunShakeIntensity",
            "GunShakeResetDelay",
            "GunSpreadAngle",
            "GunAmmoConsumption",
            "GunAttachmentConsumption",
            "GunFirePattern",
            "GunShootSound",
            "GunAfterShootSound",
            "GunBulletDropSound",
            "GunSoundVolume",
            "GunSoundPitch",
            "GunBulletDropChance",
            "GunAfterShotDelay",
            "GunEmptyCooldown",
            "GunShotParticle",
            "GunShotSize",
            "GunShotDistance",
            "GunShotParticleCount");

    private static boolean isNbtCustomAttachment(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !ModComponents.has(stack)) {
            return false;
        }
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null) {
            return false;
        }
        for (String key : ATTACHMENT_MODIFIER_KEYS) {
            if (tag.contains(key)) {
                return true;
            }
        }
        return tag.contains("GunAttachmentSlot");
    }

    public static int getSlotStackLimit(GunType type, int slot, ItemStack gunStack) {
        if (type == null || slot < 0 || slot >= getGunSlotCount(type))
            return 0;
        if (slot == 1 && gunStack != null && ModComponents.has(gunStack)) {
            CompoundTag tag = ModComponents.get(gunStack);
            if (tag != null && tag.contains("GunMaxAmmo", CompoundTag.TAG_INT)) {
                return Math.max(1, tag.getInt("GunMaxAmmo"));
            }
        }
        GunItemData.GunEntry gunData = GunItemData.getGunData(gunStack);
        if (gunData != null && gunData.slot_limits != null && gunData.slot_limits.containsKey(slot)) {
            Integer configuredLimit = gunData.slot_limits.get(slot);
            int limit = configuredLimit != null ? configuredLimit : 1;
            return Math.max(1, limit);
        }
        if (type == GunType.REVOLVER || type == GunType.PEPPERBOX) {
            // Slot 0 is the attachment/muzzle slot; slots 1..REVOLVER_CHAMBER_COUNT are the
            // chamber/barrel slots. The per-chamber capacity is configurable, so a stack larger
            // than the configured maximum is cut down when inserted or saved (reload uses insertItem).
            if (slot == 0) {
                return 1;
            }
            if (slot >= 1 && slot <= REVOLVER_CHAMBER_COUNT) {
                return type == GunType.REVOLVER
                        ? Math.max(1, GunSystemCommonConfig.GUN_REVOLVER_MAX_AMMO.get())
                        : Math.max(1, GunSystemCommonConfig.GUN_PEPPERBOX_MAX_AMMO.get());
            }
            return 1;
        }
        if (slot == 0 || slot == 2) {
            return 1;
        }
        return switch (type) {
            case PISTOL -> Math.max(1, GunSystemCommonConfig.GUN_PISTOL_MAX_AMMO.get());
            case SCATTERGUN -> Math.max(1, GunSystemCommonConfig.GUN_SCATTERGUN_MAX_AMMO.get());
            case SHOTGUN -> Math.max(1, GunSystemCommonConfig.GUN_SHOTGUN_MAX_AMMO.get());
            case GUN -> Math.max(1, GunSystemCommonConfig.GUN_PISTOL_MAX_AMMO.get());
            case REVOLVER, PEPPERBOX -> 1;
        };
    }

    public static void openGunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        if (serverPlayer == null || hand == null || itemStack == null
                || itemStack != serverPlayer.getItemInHand(hand)
                || !isGun(itemStack) || !canOpenInventory(itemStack)) {
            return;
        }
        GunType type = getGunType(itemStack);
        JaamsWeaponryMod.LOGGER.debug("[GunInventory] Opening gun inventory: player={}, type={}, hand={}",
                serverPlayer.getName().getString(), type, hand);
        if (type == null)
            return;
        switch (type) {
            case PISTOL -> openPistolInventory(serverPlayer, itemStack, hand);
            case SCATTERGUN -> openScattergunInventory(serverPlayer, itemStack, hand);
            case SHOTGUN -> openShotgunInventory(serverPlayer, itemStack, hand);
            case REVOLVER -> openRevolverInventory(serverPlayer, itemStack, hand);
            case PEPPERBOX -> openPepperboxInventory(serverPlayer, itemStack, hand);
            case GUN -> openGenericGunInventory(serverPlayer, itemStack, hand);
        }
    }

    public static void updateGunInventory(ItemStack itemstack) {
        if (itemstack.isEmpty())
            return;
        CompoundTag tag = ModComponents.get(itemstack);
        if (tag != null && tag.contains("Inventory")) {
            tag.remove("Inventory");
            ModComponents.set(itemstack, tag.isEmpty() ? null : tag);
        }
    }

    public static boolean overrideStackedOnOther(ItemStack gunStack, Slot slot, ClickAction action, Player player) {
        if (!GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get())
            return false;
        if (gunStack.getCount() != 1 || action != ClickAction.SECONDARY)
            return false;
        if (slot instanceof net.minecraft.world.inventory.ResultSlot)
            return false;
        GunType type = getGunType(gunStack);
        if (type == null)
            return false;
        return CapHelper.itemHandler(gunStack)
                .map((handler) -> {
                    // Never auto-insert into a slot backed by this same gun's handler: the slot would
                    // hand out copies of the very contents we are mutating, so writing the remainder
                    // back could void items. Let vanilla handle that click instead.
                    if (slot instanceof SlotItemHandler slotHandler && slotHandler.getItemHandler() == handler)
                        return false;
                    ItemStack cursor = slot.getItem();
                    if (cursor.isEmpty()) {
                        // Unload: move a full stack out of the gun into the empty slot.
                        ItemStack extracted = extractFullStackFromGun(handler);
                        if (extracted.isEmpty())
                            return false;
                        playExtractSound(player, gunStack, extracted);
                        ItemStack leftover = slot.safeInsert(extracted);
                        if (!leftover.isEmpty()) {
                            // Target slot could not hold everything: put it back into the gun.
                            ItemStack rejected = insertIntoGun(handler, gunStack, leftover, type);
                            if (!rejected.isEmpty() && !player.level().isClientSide())
                                player.drop(rejected, false);
                        }
                        return true;
                    }
                    // Load: insert the hovered stack into the gun, keeping the remainder in the slot.
                    ItemStack remaining = insertIntoGun(handler, gunStack, cursor, type);
                    int inserted = cursor.getCount() - remaining.getCount();
                    if (inserted <= 0)
                        return false;
                    playInsertSound(player, gunStack, cursor.copyWithCount(inserted));
                    if (slot.getItem() == cursor) {
                        // The slot hands out a live reference (vanilla inventories, ItemStackHandler):
                        // shrinking the stack persists directly.
                        cursor.shrink(inserted);
                        slot.setChanged();
                    } else if (!remaining.isEmpty()) {
                        // The slot hands out copies (e.g. component-backed handlers): write the
                        // remainder back explicitly so items are neither lost nor duplicated.
                        slot.set(remaining);
                    } else if (slot instanceof SlotItemHandler slotItemHandler) {
                        // Everything was inserted and the slot hands out copies: empty it safely
                        // through the handler (setStackInSlot rejects empty stacks on some handlers).
                        slotItemHandler.getItemHandler().extractItem(slotItemHandler.getSlotIndex(), Integer.MAX_VALUE, false);
                    } else {
                        slot.set(ItemStack.EMPTY);
                    }
                    return true;
                })
                .orElse(false);
    }

    public static boolean overrideOtherStackedOnMe(ItemStack gunStack, ItemStack cursorStack, Slot slot,
            ClickAction action, Player player, SlotAccess access) {
        if (!GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get())
            return false;
        if (gunStack.getCount() != 1 || action != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;
        GunType type = getGunType(gunStack);
        if (type == null)
            return false;
        return CapHelper.itemHandler(gunStack)
                .map((handler) -> {
                    if (cursorStack.isEmpty()) {
                        // Right-click the gun with an empty cursor: move a full stack out of the gun's
                        // inventory directly onto the cursor. Extraction goes through the handler, so the
                        // gun's components are always written back (no loss or duplication).
                        ItemStack extracted = extractFullStackFromGun(handler);
                        if (extracted.isEmpty())
                            return false;
                        playExtractSound(player, gunStack, extracted);
                        access.set(extracted);
                        return true;
                    }
                    // Load: insert the carried stack into the gun, keeping the remainder on the cursor.
                    ItemStack remaining = insertIntoGun(handler, gunStack, cursorStack, type);
                    int inserted = cursorStack.getCount() - remaining.getCount();
                    if (inserted > 0) {
                        playInsertSound(player, gunStack, cursorStack.copyWithCount(inserted));
                        cursorStack.shrink(inserted);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public static InteractionResultHolder<ItemStack> useGun(ItemStack stack, Level world, Player entity,
            InteractionHand hand) {
        if (world.isClientSide()) {
            boolean modifierPressed = GunSystemClientConfig.GUN_INV_KEY.get() != null
                    && GunSystemClientConfig.GUN_INV_KEY.get().isPressed();
            boolean openInventory = modifierPressed && canOpenInventory(stack);
            JaamsWeaponryMod.LOGGER.debug("[GunInventory] Client useGun: hand={}, modifier={}, open={}", hand,
                    modifierPressed, openInventory);
            if (modifierPressed) {
                // Let the server make the final decision. If its data differs from
                // the client, the packet can fall back to a normal shot.
                PacketDistributor.sendToServer(new GunInventoryPacket(hand, true));
                return InteractionResultHolder.sidedSuccess(stack, true);
            }
            if (entity.getCooldowns().isOnCooldown(stack.getItem())) {
                return InteractionResultHolder.fail(stack);
            }
            // Cooldown is applied and echoed by the server only after a valid
            // shot. This avoids a false local cooldown when the server rejects
            // the shot for lack of ammunition.
            PacketDistributor.sendToServer(new GunShootPacket(hand, 0));
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static ItemStack extractFullStackFromGun(IItemHandler h) {
        int[] p = { 1, 0, 2 };
        int slotCount = h.getSlots();
        if (slotCount > 3) {
            p = new int[slotCount];
            for (int i = 0; i < slotCount; i++) p[i] = i;
        }
        for (int s : p) {
            ItemStack st = h.getStackInSlot(s);
            if (!st.isEmpty()) {
                return h.extractItem(s, st.getCount(), false);
            }
        }
        return ItemStack.EMPTY;
    }

    public static int insertIntoSlot(IItemHandler h, int slot, ItemStack in) {
        ItemStack ex = h.getStackInSlot(slot);
        int max = Math.min(h.getSlotLimit(slot), in.getMaxStackSize());
        int add;
        if (ex.isEmpty())
            add = Math.min(in.getCount(), max);
        else if (ItemStack.isSameItemSameComponents(ex, in))
            add = Math.min(in.getCount(), max - ex.getCount());
        else
            return 0;
        if (add <= 0)
            return 0;
        ItemStack copy = in.copyWithCount(add);
        ItemStack rem = h.insertItem(slot, copy, false);
        return add - rem.getCount();
    }

    /**
     * Inserts as much of {@code stack} as possible into the gun's valid slots, respecting slot rules
     * and per-slot stack limits. Returns the remainder that could not be inserted (never loses items).
     */
    public static ItemStack insertIntoGun(IItemHandler handler, ItemStack gunStack, ItemStack stack, GunType type) {
        if (stack.isEmpty() || type == null || handler == null)
            return stack;
        ItemStack remainder = stack;
        for (int slot : getValidInsertSlots(gunStack, remainder, type)) {
            if (remainder.isEmpty())
                break;
            remainder = handler.insertItem(slot, remainder, false);
        }
        return remainder;
    }

    public static int[] getValidInsertSlots(ItemStack gunStack, ItemStack item, GunType type) {
        if (gunStack == null || item == null || item.isEmpty() || type == null) {
            return new int[] {};
        }
        int maxSlot = getGunSlotCount(type);
        List<Integer> valid = new ArrayList<>();
        for (int slot = 0; slot < maxSlot; slot++) {
            if (canPlaceInGunSlot(gunStack, item, type, slot)) {
                valid.add(slot);
            }
        }
        return valid
                .stream()
                .mapToInt((i) -> i)
                .toArray();
    }

    public static boolean isAmmo(ItemStack gunStack, ItemStack stack, GunType type) {
        if (stack.isEmpty() || type == null)
            return false;
        if (hasCustomSlotRule(gunStack, 1)) {
            return canPlaceInGunSlot(gunStack, stack, type, 1);
        }
        return switch (type) {
            case PISTOL, SCATTERGUN, GUN, REVOLVER, PEPPERBOX -> stack.is(ModTags.BULLETS);
            case SHOTGUN -> stack.is(ModTags.SHOTSHELLS);
        };
    }

    public static boolean isAttachment(ItemStack gunStack, ItemStack stack, GunType type) {
        if (stack.isEmpty() || type == null)
            return false;
        return canPlaceInGunSlot(gunStack, stack, type, 0) || canPlaceInGunSlot(gunStack, stack, type, 2);
    }

    private static boolean hasCustomSlotRule(ItemStack gunStack, int slot) {
        if (gunStack == null || !ModComponents.has(gunStack) || !ModComponents.get(gunStack).contains("GunSlotRules")) {
            return false;
        }
        CompoundTag rules = ModComponents.get(gunStack).getCompound("GunSlotRules");
        return rules.contains("Slot" + slot);
    }

    public static SoundEvent getItemSound(ItemStack gunStack, ItemStack item) {
        if (gunStack == null || item.isEmpty()) {
            return null;
        }
        GunType type = getGunType(gunStack);
        if (type == null) {
            return null;
        }
        if (type == GunType.REVOLVER || type == GunType.PEPPERBOX) {
            if (isAmmo(gunStack, item, type)) {
                return ModSounds.GUN_SYSTEM_REVOLVER_BULLET.get();
            } else if (isAttachment(gunStack, item, type)) {
                return ModSounds.GUN_SYSTEM_REVOLVER_ATTACHMENT.get();
            }
            return null;
        }
        if (isAmmo(gunStack, item, type)) {
            return switch (type) {
                case PISTOL, GUN -> ModSounds.GUN_SYSTEM_PISTOL_BULLET.get();
                case SCATTERGUN -> ModSounds.GUN_SYSTEM_SCATTERGUN_BULLET.get();
                case SHOTGUN -> ModSounds.GUN_SYSTEM_SHOTGUN_SHELL.get();
                case REVOLVER -> ModSounds.GUN_SYSTEM_REVOLVER_BULLET.get();
                case PEPPERBOX -> ModSounds.GUN_SYSTEM_REVOLVER_BULLET.get();
            };
        } else if (isAttachment(gunStack, item, type)) {
            return switch (type) {
                case PISTOL -> ModSounds.GUN_SYSTEM_PISTOL_ATTACHMENT.get();
                case SCATTERGUN -> ModSounds.GUN_SYSTEM_SCATTERGUN_ATTACHMENT.get();
                case SHOTGUN -> ModSounds.GUN_SYSTEM_SHOTGUN_ATTACHMENT.get();
                case REVOLVER -> ModSounds.GUN_SYSTEM_REVOLVER_ATTACHMENT.get();
                case PEPPERBOX -> ModSounds.GUN_SYSTEM_REVOLVER_ATTACHMENT.get();
                case GUN -> null;
            };
        }
        return null;
    }

    public static void playInsertSound(Player player, ItemStack gunStack, ItemStack item) {
        if (player == null || gunStack == null || item.isEmpty()) {
            return;
        }
        SoundEvent sound = getItemSound(gunStack, item);
        if (sound == null) {
            sound = SoundEvents.BUNDLE_INSERT;
        }
        player.playSound(sound, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    public static void playExtractSound(Player player, ItemStack gunStack, ItemStack item) {
        if (player == null || gunStack == null || item.isEmpty()) {
            return;
        }
        SoundEvent sound = getItemSound(gunStack, item);
        if (sound == null) {
            sound = SoundEvents.BUNDLE_REMOVE_ONE;
        }
        player.playSound(sound, 0.8F, 0.9F + player.level().getRandom().nextFloat() * 0.3F);
    }

    public static void applyPhysicalRecoil(LivingEntity entity, ItemStack itemstack, float recoilDistance,
            float crouchRecoilReduction, float verticalRecoilMultiplier) {
        int backblastLevel = ModEnchantments.level(itemstack, ModEnchantments.BACKBLAST);
        if (backblastLevel > 0) {
            float backblastBonus = (float) (EnchantmentsConfig.BACKBLAST_RECOIL_BONUS_PER_LEVEL.get() * backblastLevel);
            float fireDurationBonus = (float) (EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE_DURATION_BONUS_PER_LEVEL
                    .get() * backblastLevel);
            float enhancedVerticalRecoil = verticalRecoilMultiplier
                    + (float) (EnchantmentsConfig.BACKBLAST_VERTICAL_RECOIL_BONUS_PER_LEVEL.get() * backblastLevel);
            float baseDamage = (float) (EnchantmentsConfig.BACKBLAST_FIRE_SHOCKWAVE_BASE_DAMAGE_PER_LEVEL.get()
                    * backblastLevel);
            GunActionsHandler.applyBackblastRecoil(entity, recoilDistance, crouchRecoilReduction,
                    enhancedVerticalRecoil, backblastBonus, fireDurationBonus, baseDamage);
        } else {
            ModUtils.applyRecoil(entity, recoilDistance, crouchRecoilReduction, verticalRecoilMultiplier);
        }
    }

    public static void applyVisualRecoil(Player player, float xRotRecoilIntensity) {
        player.setXRot(player.getXRot() - xRotRecoilIntensity);
    }

    public static void applyCooldowns(Player player, ItemStack itemstack, int cooldownTicks, int offhandCooldown) {
        if (cooldownTicks <= 0)
            return;
        if (GunSystemCommonConfig.GUN_COOLDOWN_GLOBAL.get()) {
            for (ItemStack invStack : player.getInventory().items) {
                if (isGun(invStack))
                    player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
            }
            for (ItemStack invStack : player.getInventory().armor) {
                if (isGun(invStack))
                    player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
            }
            for (ItemStack invStack : player.getInventory().offhand) {
                if (isGun(invStack))
                    player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
            }
        } else if (GunSystemCommonConfig.GUN_COOLDOWN_BY_TYPE.get()) {
            GunType currentType = getGunType(itemstack);
            if (currentType != null) {
                for (ItemStack invStack : player.getInventory().items) {
                    if (isGun(invStack) && getGunType(invStack) == currentType)
                        player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
                }
                for (ItemStack invStack : player.getInventory().armor) {
                    if (isGun(invStack) && getGunType(invStack) == currentType)
                        player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
                }
                for (ItemStack invStack : player.getInventory().offhand) {
                    if (isGun(invStack) && getGunType(invStack) == currentType)
                        player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
                }
            } else {
                player.getCooldowns().addCooldown(itemstack.getItem(), cooldownTicks);
            }
        } else {
            player.getCooldowns().addCooldown(itemstack.getItem(), cooldownTicks);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            InteractionHand usedHand = player.getMainHandItem() == itemstack
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
            PacketDistributor.sendToPlayer(serverPlayer, new GunShootPacket(usedHand, cooldownTicks));
        }
        ItemStack offhandItem = player.getOffhandItem();
        if (!offhandItem.isEmpty() && !offhandItem.equals(itemstack) && offhandCooldown > 0) {
            if (player.getCooldowns().getCooldownPercent(offhandItem.getItem(), 0.0F) == 0.0F) {
                if (GunSystemCommonConfig.GUN_COOLDOWN_GLOBAL.get()) {
                    for (ItemStack invStack : player.getInventory().items) {
                        if (isGun(invStack))
                            player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                    }
                    for (ItemStack invStack : player.getInventory().armor) {
                        if (isGun(invStack))
                            player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                    }
                    for (ItemStack invStack : player.getInventory().offhand) {
                        if (isGun(invStack))
                            player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                    }
                } else if (GunSystemCommonConfig.GUN_COOLDOWN_BY_TYPE.get()) {
                    GunType offhandType = getGunType(offhandItem);
                    if (offhandType != null) {
                        for (ItemStack invStack : player.getInventory().items) {
                            if (isGun(invStack) && getGunType(invStack) == offhandType)
                                player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                        }
                        for (ItemStack invStack : player.getInventory().armor) {
                            if (isGun(invStack) && getGunType(invStack) == offhandType)
                                player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                        }
                        for (ItemStack invStack : player.getInventory().offhand) {
                            if (isGun(invStack) && getGunType(invStack) == offhandType)
                                player.getCooldowns().addCooldown(invStack.getItem(), offhandCooldown);
                        }
                    } else {
                        player.getCooldowns().addCooldown(offhandItem.getItem(), offhandCooldown);
                    }
                } else {
                    player.getCooldowns().addCooldown(offhandItem.getItem(), offhandCooldown);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new GunShootPacket(InteractionHand.OFF_HAND, offhandCooldown));
                }
            }
        }
    }

    public static ItemStack getItemStack(ItemStack itemstack, int slot) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        CapHelper.itemHandler(itemstack)
                .ifPresent((capability) -> result.set(capability.getStackInSlot(slot).copy()));
        return result.get();
    }

    private static void writeGunMenuData(FriendlyByteBuf buffer, Player player, ItemStack gunStack,
            InteractionHand hand, GunType type) {
        buffer.writeBlockPos(player.blockPosition());
        buffer.writeByte(GUN_MENU_DATA_VERSION);
        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buffer.writeNbt(gunStack.copy().saveOptional(player.level().registryAccess()));
        buffer.writeEnum(type);
        for (int slot = 0; slot < getGunSlotCount(type); slot++) {
            int limit = getSlotStackLimit(type, slot, gunStack);
            // Ensure attachment slots always have at least limit 1 to prevent
            // ComponentItemHandler from rejecting empty (air) stacks in those slots
            if (isAttachmentSlot(type, slot)) {
                limit = Math.max(1, limit);
            }
            buffer.writeVarInt(Math.max(0, limit));
        }
    }

    private static boolean isAttachmentSlot(GunType type, int slot) {
        return switch (type) {
            case PISTOL, SCATTERGUN, SHOTGUN -> slot == 0 || slot == 2;
            case REVOLVER -> slot == 0;
            case PEPPERBOX -> slot == 0;
            default -> false;
        };
    }

    public static void openGenericGunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.GUN);
                        return new GunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.GUN));
        serverPlayer.swing(hand, true);
    }

    public static void openPistolInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.PISTOL);
                        return new PistolGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.PISTOL));
        serverPlayer.swing(hand, true);
    }

    public static void openScattergunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.SCATTERGUN);
                        return new ScattergunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.SCATTERGUN));
        serverPlayer.swing(hand, true);
    }

    public static void openShotgunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.SHOTGUN);
                        return new ShotgunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.SHOTGUN));
        serverPlayer.swing(hand, true);
    }

    public static void openRevolverInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.REVOLVER);
                        return new RevolverGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.REVOLVER));
        serverPlayer.swing(hand, true);
    }

    public static void openPepperboxInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        serverPlayer.openMenu(
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        writeGunMenuData(buffer, player, player.getItemInHand(hand), hand, GunType.PEPPERBOX);
                        return new PepperboxGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> writeGunMenuData(buf, serverPlayer, serverPlayer.getItemInHand(hand), hand, GunType.PEPPERBOX));
        serverPlayer.swing(hand, true);
    }

    public static final int REVOLVER_CHAMBER_COUNT = 6;
    public static final int PEPPERBOX_CHAMBER_COUNT = 6;

    public static int getRevolverCurrentChamber(ItemStack gunStack) {
        if (gunStack == null || gunStack.isEmpty())
            return 0;
        CompoundTag tag = ModComponents.get(gunStack);
        if (tag != null && tag.contains("RevolverCurrentChamber", CompoundTag.TAG_INT)) {
            return Math.floorMod(tag.getInt("RevolverCurrentChamber"), REVOLVER_CHAMBER_COUNT);
        }
        return 0;
    }

    public static int getRevolverChamberSlot(ItemStack gunStack) {
        return getRevolverCurrentChamber(gunStack) + 1;
    }

    public static int getNextLoadedChamberSlot(ItemStack gunStack) {
        return getNextLoadedChamberSlot(gunStack, 1);
    }

    /**
     * Finds the next loaded chamber/barrel slot starting from the current chamber, iterating all
     * chamber slots 1..REVOLVER_CHAMBER_COUNT. Returns the first slot whose stack has at least
     * {@code minCount} items, or -1 if no chamber qualifies.
     */
    public static int getNextLoadedChamberSlot(ItemStack gunStack, int minCount) {
        if (gunStack == null || gunStack.isEmpty())
            return -1;
        int start = getRevolverChamberSlot(gunStack);
        final int slotCount = REVOLVER_CHAMBER_COUNT;
        return CapHelper.itemHandler(gunStack)
                .map(handler -> {
                    for (int i = 0; i < slotCount; i++) {
                        int slot = ((start - 1 + i) % slotCount) + 1;
                        ItemStack stackInSlot = handler.getStackInSlot(slot);
                        if (!stackInSlot.isEmpty() && stackInSlot.getCount() >= minCount) {
                            return slot;
                        }
                    }
                    return -1;
                })
                .orElse(-1);
    }

    public static void advanceRevolverChamber(ItemStack gunStack) {
        if (gunStack == null || gunStack.isEmpty()) return;
        int current = getRevolverCurrentChamber(gunStack);
        ModComponents.update(gunStack, tag -> tag.putInt("RevolverCurrentChamber", (current + 1) % REVOLVER_CHAMBER_COUNT));
    }

    public static boolean isRevolverGun(ItemStack gunStack) {
        return getGunType(gunStack) == GunType.REVOLVER;
    }

    public static int getGunSlotCount(GunType type) {
        if (type == null)
            return 0;
        return type == GunType.REVOLVER || type == GunType.PEPPERBOX ? 7 : 3;
    }
}
