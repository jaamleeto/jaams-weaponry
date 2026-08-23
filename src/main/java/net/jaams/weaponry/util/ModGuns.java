package net.jaams.weaponry.util;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.gun.GunInventoryCapability;
import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.entity.BulletProjectileEntity;
import net.jaams.weaponry.entity.EchoBulletProjectileEntity;
import net.jaams.weaponry.entity.FireBulletProjectileEntity;
import net.jaams.weaponry.entity.GlowingBulletProjectileEntity;
import net.jaams.weaponry.entity.HeavyBulletProjectileEntity;
import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.entity.SharpBulletProjectileEntity;
import net.jaams.weaponry.gun.shoot.DefaultShoot;
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
import net.jaams.weaponry.world.inventory.PistolGUIMenu;
import net.jaams.weaponry.world.inventory.RevolverGUIMenu;
import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.world.inventory.ShotgunGUIMenu;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.ForgeRegistries;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.h;
import org.checkerframework.checker.units.qual.s;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ModGuns {

    public static boolean isGun(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.hasTag() && stack.getTag().contains("GunType", CompoundTag.TAG_STRING)) {
        String customType = stack.getTag().getString("GunType").toUpperCase().trim();
        return switch (customType) {
            case "PISTOL", "SCATTERGUN", "SHOTGUN", "GUN", "REVOLVER" -> true;
            default -> false;
        };
    }
        if (stack.is(ModTags.GUNS) || stack.is(ModTags.PISTOLS) || stack.is(ModTags.SCATTERGUNS)
                || stack.is(ModTags.SHOTGUNS) || stack.is(ModTags.REVOLVERS)) {
            return true;
        }
        return GunItemData.getData(stack).isPresent();
    }

    public static GunType getGunType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (stack.hasTag() && stack.getTag().contains("GunType", CompoundTag.TAG_STRING)) {
        String customType = stack.getTag().getString("GunType").toUpperCase().trim();
        return switch (customType) {
            case "PISTOL" -> GunType.PISTOL;
            case "SCATTERGUN" -> GunType.SCATTERGUN;
            case "SHOTGUN" -> GunType.SHOTGUN;
            case "GUN" -> GunType.GUN;
            case "REVOLVER" -> GunType.REVOLVER;
            default -> null;
        };
    }
    if (stack.is(ModTags.PISTOLS))
        return GunType.PISTOL;
    if (stack.is(ModTags.SCATTERGUNS))
        return GunType.SCATTERGUN;
    if (stack.is(ModTags.SHOTGUNS))
        return GunType.SHOTGUN;
    if (stack.is(ModTags.REVOLVERS))
        return GunType.REVOLVER;
    if (stack.is(ModTags.GUNS))
        return GunType.GUN;
        return GunItemData.getData(stack)
                .map((data) -> getGunTypeFromString(data.gun.gun_type))
                .orElse(null);
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
            default -> null;
        };
    }

    public static boolean canOpenInventory(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
        if (gunData != null && gunData.open_inventory != null) {
            return gunData.open_inventory;
        }
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("OpenInventory", CompoundTag.TAG_BYTE)) {
                return tag.getBoolean("OpenInventory");
            }
        }
        return GunSystemCommonConfig.GUN_INVENTORY.get();
    }

    public static ICapabilityProvider createCapabilityProvider(ItemStack stack) {
        GunType type = getGunType(stack);
        if (type == null)
            return null;
        return new GunInventoryCapability(type, stack);
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
        REVOLVER
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
        int finalProjectileCount = ModUtils.getConfigOrNbtInt(gunStack, "GunProjectileCount", () -> projectileCount);
        int ammoConsumption = ModUtils.getConfigOrNbtInt(gunStack, "GunAmmoConsumption",
                GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION::get);
        double finalSpreadAngle = ModUtils.getConfigOrNbtDouble(gunStack, "GunSpreadAngle", () -> spreadAngle);
        double finalSpeed = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileSpeed", () -> projectileSpeed);
        if (ammoItem.getItem() == ModItems.HEAVY_BULLET.get()
                || ammoItem.getItem() == ModItems.HEAVY_SHOTSHELL.get()) {
            finalSpeed *= 0.5;
        }
        double finalInaccuracy = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileInaccuracy", () -> inaccuracy);
        double finalDamage = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileDamageModifier",
                () -> damageModifier);
        double finalKnockback = ModUtils.getConfigOrNbtDouble(gunStack, "GunProjectileKnockbackModifier",
                () -> knockbackModifier);
        
        finalSpeed *= getMuzzleSpeedMultiplier(gunStack);
        finalDamage *= getMuzzleDamageMultiplier(gunStack);
        int finalPiercing = Math.max(0,
                ModUtils.getConfigOrNbtInt(gunStack, "GunProjectilePiercingModifier", () -> piercingModifier));
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

    private static double getMuzzleModifierForType(GunType type) {
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_MUZZLE_MODIFIER.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_MUZZLE_MODIFIER.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_MUZZLE_MODIFIER.get();
            case REVOLVER -> GunSystemCommonConfig.GUN_REVOLVER_SHOOT_MUZZLE_MODIFIER.get();
            case GUN -> 1.0;
        };
    }

    
    public static double getMuzzleSpeedMultiplier(ItemStack gunStack) {
        GunType type = getGunType(gunStack);
        if (type == null) return 1.0;
        int muzzleSlot = type == GunType.REVOLVER ? 6 : 0;
        ItemStack muzzleAttach = gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.getStackInSlot(muzzleSlot))
                .orElse(ItemStack.EMPTY);
        if (muzzleAttach.isEmpty()) return 1.0;
        double multiplier = getMuzzleModifierForType(type);
        double attachSpeedMod = ModUtils.getConfigOrNbtDouble(muzzleAttach, "GunProjectileSpeed", () -> 1.0);
        return multiplier * attachSpeedMod;
    }


    public static double getMuzzleDamageMultiplier(ItemStack gunStack) {
        GunType type = getGunType(gunStack);
        if (type == null) return 1.0;
        int muzzleSlot = type == GunType.REVOLVER ? 6 : 0;
        ItemStack muzzleAttach = gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.getStackInSlot(muzzleSlot))
                .orElse(ItemStack.EMPTY);
        if (muzzleAttach.isEmpty()) return 1.0;
        double multiplier = getMuzzleModifierForType(type);
        double attachDamageMod = ModUtils.getConfigOrNbtDouble(muzzleAttach, "GunProjectileDamageModifier", () -> 1.0);
        return multiplier * attachDamageMod;
    }

    private static ModEnums.GunFirePattern getFirePattern(ItemStack gunStack) {
        String nbtPattern = ModUtils.getConfigOrNbtString(gunStack, "GunFirePattern", () -> null);
        if (nbtPattern != null && !nbtPattern.isEmpty()) {
            ModEnums.GunFirePattern pattern = ModEnums.GunFirePattern.fromString(nbtPattern);
            if (pattern != ModEnums.GunFirePattern.DEFAULT) {
                return pattern;
            }
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
            AbstractArrow arrow = arrowItem.createArrow(level, ammoItem, shooter);
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
            if (knockback > 0)
                arrow.setKnockback((int) knockback);
            if (piercing > 0)
                arrow.setPierceLevel((byte) Math.min(piercing, 127));
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
        if (knockbackMod != 1.0) {
            arrow.setKnockback((int) Math.round(arrow.getKnockback() * knockbackMod));
        }
        if (piercing > 0) {
            arrow.setPierceLevel((byte) piercing);
        }
        return arrow;
    }

    public static boolean canPlaceInGunSlot(ItemStack gunStack, ItemStack toPlace, GunType type, int slot) {
        if (gunStack.isEmpty() || toPlace.isEmpty()) {
            return false;
        }
        if (gunStack.hasTag() && gunStack.getTag().contains("GunSlotRules")) {
            CompoundTag rules = gunStack.getTag().getCompound("GunSlotRules");
            String key = "Slot" + slot;
            if (rules.contains(key)) {
                Tag value = rules.get(key);
                if (value instanceof ListTag listTag) {
                    List<String> ruleList = new ArrayList<>();
                    for (Tag t : listTag) {
                        if (t instanceof StringTag stringTag) {
                            ruleList.add(stringTag.getAsString());
                        }
                    }
                    if (!ruleList.isEmpty()) {
                        Boolean result = evaluateRules(ruleList, toPlace);
                        if (result != null) {
                            return result;
                        }
                    }
                } else if (value instanceof StringTag stringTag) {
                    String ruleStr = stringTag.getAsString().trim();
                    if (!ruleStr.isEmpty()) {
                        if ("any".equalsIgnoreCase(ruleStr)) {
                            return true;
                        }
                        Boolean result = evaluateRule(ruleStr, toPlace);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        GunItemData.GunEntry gunData = GunItemData.getGunData(gunStack);
        if (gunData != null && gunData.slot_rules.containsKey(slot)) {
            List<String> rulesList = gunData.slot_rules.get(slot);
            if (rulesList != null && !rulesList.isEmpty()) {
                Boolean result = evaluateRules(rulesList, toPlace);
                if (result != null) {
                    return result;
                }
            }
        }
        return getDefaultSlotRule(type, slot, toPlace);
    }

    private static Boolean evaluateRules(List<String> rulesList, ItemStack stack) {
        boolean hasAny = false;
        boolean hasPositiveMatch = false;
        for (String ruleStr : rulesList) {
            if (ruleStr == null)
                continue;
            String rule = ruleStr.trim();
            if (rule.isEmpty())
                continue;
            if ("any".equalsIgnoreCase(rule)) {
                hasAny = true;
                continue;
            }
            Boolean result = evaluateRule(rule, stack);
            if (result == null)
                continue;
            if (!result) {
                return false;
            } else {
                hasPositiveMatch = true;
            }
        }
        if (hasAny || hasPositiveMatch) {
            return true;
        }
        return null;
    }

    private static Boolean evaluateRule(String rule, ItemStack stack) {
        boolean negated = false;
        if (rule.startsWith("!")) {
            negated = true;
            rule = rule.substring(1).trim();
        }
        if (rule.startsWith("#")) {
            String tagId = rule.substring(1).trim();
            ResourceLocation location = ResourceLocation.tryParse(tagId);
            if (location == null)
                return null;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, location);
            boolean matches = stack.is(tagKey);
            return negated ? !matches : matches;
        } else {
            ResourceLocation itemId = ResourceLocation.tryParse(rule);
            if (itemId == null)
                return null;
            Item allowedItem = ForgeRegistries.ITEMS.getValue(itemId);
            boolean matches = stack.getItem() == allowedItem && allowedItem != Items.AIR;
            return negated ? !matches : matches;
        }
    }

    private static boolean getDefaultSlotRule(GunType type, int slot, ItemStack toPlace) {
        return switch (type) {
            case PISTOL -> switch (slot) {
                case 0 -> toPlace.is(ModItems.COPPER_MUZZLE.get());
                case 1 -> toPlace.is(ModTags.BULLETS);
                case 2 -> toPlace.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
                default -> false;
            };
            case SCATTERGUN -> switch (slot) {
                case 0 -> toPlace.is(ModItems.COPPER_CHOKE.get());
                case 1 -> toPlace.is(ModTags.BULLETS);
                case 2 -> toPlace.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
                default -> false;
            };
            case SHOTGUN -> switch (slot) {
                case 0 -> toPlace.is(ModItems.COPPER_CHOKE.get());
                case 1 -> toPlace.is(ModTags.SHOTSHELLS);
                case 2 -> toPlace.is(ModItems.COPPER_EXTENDED_MAGAZINE.get());
                default -> false;
            };
            case REVOLVER -> switch (slot) {
                case 0 -> toPlace.is(ModItems.COPPER_MUZZLE.get());
                case 1, 2, 3, 4, 5, 6 -> toPlace.is(ModTags.BULLETS);
                default -> false;
            };
            case GUN -> slot == 1 && toPlace.is(ModTags.BULLETS);
            default -> false;
        };
    }

    public static int getSlotStackLimit(GunType type, int slot, ItemStack gunStack) {
        if (gunStack != null && gunStack.hasTag() && gunStack.getTag().contains("GunMaxAmmo", 3)) {
            return gunStack.getTag().getInt("GunMaxAmmo");
        }
        GunItemData.GunEntry gunData = GunItemData.getGunData(gunStack);
        if (gunData != null && gunData.slot_limits.containsKey(slot)) {
            int limit = gunData.slot_limits.get(slot);
            return Math.max(1, limit);
        }
        if (type == GunType.REVOLVER) {
            if (slot >= 0 && slot <= 5) return 1;
            return 1;
        }
        if (slot == 0 || slot == 2) {
            return 1;
        }
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_MAX_AMMO.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_MAX_AMMO.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_MAX_AMMO.get();
            case GUN -> GunSystemCommonConfig.GUN_PISTOL_MAX_AMMO.get();
            case REVOLVER -> 1;
        };
    }

    public static void openGunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        GunType type = getGunType(itemStack);
        if (type == null)
            return;
        switch (type) {
            case PISTOL -> openPistolInventory(serverPlayer, itemStack, hand);
            case SCATTERGUN -> openScattergunInventory(serverPlayer, itemStack, hand);
            case SHOTGUN -> openShotgunInventory(serverPlayer, itemStack, hand);
            case REVOLVER -> openRevolverInventory(serverPlayer, itemStack, hand);
            case GUN -> openGenericGunInventory(serverPlayer, itemStack, hand);
        }
    }

    public static void updateGunInventory(ItemStack itemstack) {
        if (itemstack.isEmpty())
            return;
        itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
            CompoundTag tag = itemstack.getOrCreateTag();
            if (handler instanceof ItemStackHandler itemHandler) {
                CompoundTag inventoryTag = new CompoundTag();
                ListTag itemsList = new ListTag();
                boolean hasAnyItem = false;
                int slotCount = handler.getSlots();
                for (int i = 0; i < slotCount; i++) {
                    ItemStack slotStack = itemHandler.getStackInSlot(i);
                    if (!slotStack.isEmpty()) {
                        CompoundTag itemTag = new CompoundTag();
                        itemTag.putInt("Slot", i);
                        slotStack.save(itemTag);
                        itemsList.add(itemTag);
                        hasAnyItem = true;
                    }
                }
                inventoryTag.put("Items", itemsList);
                inventoryTag.putInt("Size", slotCount);
                if (hasAnyItem) {
                    tag.put("Inventory", inventoryTag);
                } else {
                    tag.remove("Inventory");
                }
            }
            itemstack.setTag(itemstack.getTag());
        });
    }

    public static boolean overrideStackedOnOther(ItemStack gunStack, Slot slot, ClickAction action, Player player) {
        if (!GunSystemCommonConfig.GUN_BUNDLE_INTERACTION.get())
            return false;
        if (gunStack.getCount() != 1 || action != ClickAction.SECONDARY)
            return false;
        GunType type = getGunType(gunStack);
        if (type == null)
            return false;
        return gunStack
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map((handler) -> {
                    ItemStack cursor = slot.getItem();
                    if (cursor.isEmpty()) {
                        ItemStack extracted = extractFullStackFromGun(handler);
                        if (!extracted.isEmpty()) {
                            playExtractSound(player, gunStack, extracted);
                            slot.safeInsert(extracted);
                            markGunDirty(player, gunStack);
                            return true;
                        }
                    } else {
                        int[] validSlots = getValidInsertSlots(gunStack, cursor, type);
                        for (int s : validSlots) {
                            int inserted = insertIntoSlot(handler, s, cursor);
                            if (inserted > 0) {
                                playInsertSound(player, gunStack, cursor.copyWithCount(inserted));
                                cursor.shrink(inserted);
                                markGunDirty(player, gunStack);
                                return true;
                            }
                        }
                    }
                    return false;
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
        return gunStack
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map((handler) -> {
                    if (cursorStack.isEmpty()) {
                        ItemStack extracted = extractFullStackFromGun(handler);
                        if (!extracted.isEmpty()) {
                            playExtractSound(player, gunStack, extracted);
                            access.set(extracted);
                            markGunDirty(player, gunStack);
                            return true;
                        }
                    } else {
                        int[] validSlots = getValidInsertSlots(gunStack, cursorStack, type);
                        for (int s : validSlots) {
                            int inserted = insertIntoSlot(handler, s, cursorStack);
                            if (inserted > 0) {
                                playInsertSound(player, gunStack, cursorStack.copyWithCount(inserted));
                                cursorStack.shrink(inserted);
                                markGunDirty(player, gunStack);
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    public static void markGunDirty(Player player, ItemStack gunStack) {
        if (player == null || gunStack == null || gunStack.isEmpty())
            return;
        player.getInventory().setChanged();
    }

    public static InteractionResultHolder<ItemStack> useGun(ItemStack stack, Level world, Player entity,
            InteractionHand hand) {
        if (entity.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.fail(stack);
        }
        if (hand == InteractionHand.OFF_HAND && ModUtils.isEntityInBattleMode(entity)) {
            return InteractionResultHolder.pass(stack);
        }
        if (world.isClientSide()) {
            boolean openInv = GunSystemClientConfig.GUN_INV_KEY.get().isPressed()
                    && canOpenInventory(stack);
            if (openInv) {
                JaamsWeaponryMod.PACKET_HANDLER.sendToServer(new GunInventoryPacket(hand));
                return InteractionResultHolder.sidedSuccess(stack, true);
            } else {
                int cooldown = 20;
                JaamsWeaponryMod.PACKET_HANDLER.sendToServer(new GunShootPacket(hand, cooldown));
                entity.getCooldowns().addCooldown(stack.getItem(), cooldown);
                return InteractionResultHolder.sidedSuccess(stack, true);
            }
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
        int max = 64;
        int add;
        if (ex.isEmpty())
            add = Math.min(in.getCount(), max);
        else if (ItemStack.isSameItemSameTags(ex, in))
            add = Math.min(in.getCount(), max - ex.getCount());
        else
            return 0;
        if (add <= 0)
            return 0;
        ItemStack copy = in.copyWithCount(add);
        ItemStack rem = h.insertItem(slot, copy, false);
        return add - rem.getCount();
    }

    public static int[] getValidInsertSlots(ItemStack gunStack, ItemStack item, GunType type) {
        if (item.isEmpty() || type == null) {
            return new int[] {};
        }
        int maxSlot = type == GunType.REVOLVER ? 7 : 3;
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
        if (type == GunType.REVOLVER) {
            return stack.is(ModTags.BULLETS);
        }
        if (hasCustomSlotRule(gunStack, 1)) {
            return canPlaceInGunSlot(gunStack, stack, type, 1);
        }
        return switch (type) {
            case PISTOL, SCATTERGUN, GUN -> stack.is(ModTags.BULLETS);
            case SHOTGUN -> stack.is(ModTags.SHOTSHELLS);
            case REVOLVER -> stack.is(ModTags.BULLETS);
        };
    }

    public static boolean isAttachment(ItemStack gunStack, ItemStack stack, GunType type) {
        if (stack.isEmpty() || type == null)
            return false;
        if (type == GunType.REVOLVER) {
            return canPlaceInGunSlot(gunStack, stack, type, 6);
        }
        if (hasCustomSlotRule(gunStack, 0) || hasCustomSlotRule(gunStack, 2)) {
            return canPlaceInGunSlot(gunStack, stack, type, 0) || canPlaceInGunSlot(gunStack, stack, type, 2);
        }
        return switch (type) {
            case PISTOL ->
                stack.is(ModItems.COPPER_MUZZLE.get()) || stack.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
            case SCATTERGUN ->
                stack.is(ModItems.COPPER_CHOKE.get()) || stack.is(ModItems.COPPER_QUICK_DRAW_MAGAZINE.get());
            case SHOTGUN ->
                stack.is(ModItems.COPPER_CHOKE.get()) || stack.is(ModItems.COPPER_EXTENDED_MAGAZINE.get());
            case REVOLVER -> stack.is(ModItems.COPPER_MUZZLE.get());
            case GUN -> false;
        };
    }

    private static boolean hasCustomSlotRule(ItemStack gunStack, int slot) {
        if (gunStack == null || !gunStack.hasTag() || !gunStack.getTag().contains("GunSlotRules")) {
            return false;
        }
        CompoundTag rules = gunStack.getTag().getCompound("GunSlotRules");
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
        if (type == GunType.REVOLVER) {
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
            };
        } else if (isAttachment(gunStack, item, type)) {
            return switch (type) {
                case PISTOL -> ModSounds.GUN_SYSTEM_PISTOL_ATTACHMENT.get();
                case SCATTERGUN -> ModSounds.GUN_SYSTEM_SCATTERGUN_ATTACHMENT.get();
                case SHOTGUN -> ModSounds.GUN_SYSTEM_SHOTGUN_ATTACHMENT.get();
                case REVOLVER -> ModSounds.GUN_SYSTEM_REVOLVER_ATTACHMENT.get();
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
        int backblastLevel = itemstack.getEnchantmentLevel(ModEnchantments.BACKBLAST.get());
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
            JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new GunShootPacket(player.getUsedItemHand(), cooldownTicks));
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
                    JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new GunShootPacket(InteractionHand.OFF_HAND, offhandCooldown));
                }
            }
        }
    }

    public static ItemStack getItemStack(ItemStack itemstack, int slot) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                .ifPresent((capability) -> result.set(capability.getStackInSlot(slot).copy()));
        return result.get();
    }

    public static void openGenericGunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        NetworkHooks.openScreen(
                serverPlayer,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        buffer.writeBlockPos(player.blockPosition());
                        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                        return new GunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> {
                    buf.writeBlockPos(serverPlayer.blockPosition());
                    buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                });
        serverPlayer.swing(hand, true);
    }

    public static void openPistolInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        NetworkHooks.openScreen(
                serverPlayer,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        buffer.writeBlockPos(player.blockPosition());
                        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                        return new PistolGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> {
                    buf.writeBlockPos(serverPlayer.blockPosition());
                    buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                });
        serverPlayer.swing(hand, true);
    }

    public static void openScattergunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        NetworkHooks.openScreen(
                serverPlayer,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        buffer.writeBlockPos(player.blockPosition());
                        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                        return new ScattergunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> {
                    buf.writeBlockPos(serverPlayer.blockPosition());
                    buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                });
        serverPlayer.swing(hand, true);
    }

    public static void openShotgunInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        NetworkHooks.openScreen(
                serverPlayer,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        buffer.writeBlockPos(player.blockPosition());
                        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                        return new ShotgunGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> {
                    buf.writeBlockPos(serverPlayer.blockPosition());
                    buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                });
        serverPlayer.swing(hand, true);
    }

    public static void openRevolverInventory(ServerPlayer serverPlayer, ItemStack itemStack, InteractionHand hand) {
        NetworkHooks.openScreen(
                serverPlayer,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Gun Storage");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        buffer.writeBlockPos(player.blockPosition());
                        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                        return new RevolverGUIMenu(id, inventory, buffer);
                    }
                },
                (buf) -> {
                    buf.writeBlockPos(serverPlayer.blockPosition());
                    buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                });
        serverPlayer.swing(hand, true);
    }

    public static final int REVOLVER_CHAMBER_COUNT = 6;

    public static int getRevolverCurrentChamber(ItemStack gunStack) {
        if (gunStack == null || gunStack.isEmpty()) return 0;
        CompoundTag tag = gunStack.getTag();
        if (tag != null && tag.contains("RevolverCurrentChamber")) {
            return tag.getInt("RevolverCurrentChamber") % REVOLVER_CHAMBER_COUNT;
        }
        return 0;
    }

    public static int getRevolverChamberSlot(ItemStack gunStack) {
        return getRevolverCurrentChamber(gunStack) + 1;
    }

    public static void advanceRevolverChamber(ItemStack gunStack) {
        if (gunStack == null || gunStack.isEmpty()) return;
        CompoundTag tag = gunStack.getOrCreateTag();
        int current = getRevolverCurrentChamber(gunStack);
        tag.putInt("RevolverCurrentChamber", (current + 1) % REVOLVER_CHAMBER_COUNT);
    }

    public static boolean isRevolverGun(ItemStack gunStack) {
        return getGunType(gunStack) == GunType.REVOLVER;
    }

    public static int getGunSlotCount(GunType type) {
        return type == GunType.REVOLVER ? 7 : 3;
    }
}
