
package net.jaams.weaponry.item;

import net.jaams.weaponry.init.ModEnchantments;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Vector3f;
import org.joml.Quaternionf;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.advancements.CriteriaTriggers;

import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.entity.StakeProjectileEntity;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;

import java.util.List;
import java.util.ArrayList;

public class StakeCrossbowItem extends CrossbowItem {
    public static final float maxVelocity = 2.5F;
    public static final int loadTime = 20;
    public static final float BASE_DAMAGE = 1.0F;
    public static final float DRAW_SPEED = 1.0F;
    public static final float RECOIL_DISTANCE = 0.0F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.3F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.15F;
    public static final float XROT_RECOIL_INTENSITY = 0.0F;
    public boolean startSoundPlayed = false;
    public boolean midLoadSoundPlayed = false;

    public StakeCrossbowItem() {
        super(new Item.Properties().durability(126).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        ModTooltips.addStat(stack, tooltip, "base_damage", getNbtDouble(stack, "CrossbowBaseDamage", BASE_DAMAGE));
        ModTooltips.addStat(stack, tooltip, "draw_speed", getNbtDouble(stack, "CrossbowDrawSpeed", DRAW_SPEED));
        ModTooltips.addStat(stack, tooltip, "load_time", getNbtDouble(stack, "CrossbowLoadTime", loadTime / 20.0));
        ModTooltips.addStat(stack, tooltip, "recoil", getNbtDouble(stack, "CrossbowRecoilDistance", RECOIL_DISTANCE));
    }

    private static double getNbtDouble(ItemStack stack, String key, double defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(key, Tag.TAG_DOUBLE)) {
            return ModComponents.get(stack).getDouble(key);
        }
        return defaultValue;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        if (ItemFeaturesConfig.STAKE_CROSSBOW_ALT_SHOOT.get()) {
            return UseAnim.BOW;
        }
        return UseAnim.CROSSBOW;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.FLAME)) {
            return true;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isCharged(itemstack)) {
            executeShooting(level, player, hand, itemstack, maxVelocity, 1.0F);
            float recoilDistance = (float) getNbtDouble(itemstack, "CrossbowRecoilDistance", RECOIL_DISTANCE);
            if (recoilDistance > 0.0F) {
                float crouchReduction = (float) getNbtDouble(itemstack, "CrossbowRecoilCrouchReduction", CROUCH_RECOIL_REDUCTION);
                float verticalMultiplier = (float) getNbtDouble(itemstack, "CrossbowRecoilVerticalMultiplier", VERTICAL_RECOIL_MULTIPLIER);
                ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMultiplier);
            }
            float xrotRecoil = (float) getNbtDouble(itemstack, "CrossbowRecoilXROT", XROT_RECOIL_INTENSITY);
            if (xrotRecoil != 0.0F) {
                player.setXRot(player.getXRot() - xrotRecoil);
            }
            return InteractionResultHolder.consume(itemstack);
        } else if (hasStake(player)) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                player.startUsingItem(hand);
            }
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean isSelected) {
        if (!world.isClientSide && entity instanceof LivingEntity livingEntity
                && ItemFeaturesConfig.STAKE_CROSSBOW_ALT_SHOOT.get()) {
            boolean isBeingUsed = livingEntity.getUseItem() == itemStack;
            if (isBeingUsed && livingEntity.getUseItemRemainingTicks() <= (getUseDuration(itemStack, livingEntity) - 20)) {
                if (!isCharged(itemStack)) {
                    setCharged(itemStack, true);
                    world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                            SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    livingEntity.swing(livingEntity.getUsedItemHand(), true);
                }
            } else if (!isBeingUsed && isCharged(itemStack)) {
                setCharged(itemStack, false);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && isCharged(itemStack) && ItemFeaturesConfig.STAKE_CROSSBOW_ALT_SHOOT.get()) {
            setCharged(itemStack, false);
        }
        return super.finishUsingItem(itemStack, world, entity);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int ticks) {
        if (!(entity instanceof Player player)) {
            int i = this.getUseDuration(itemStack, entity) - ticks;
            float power = getPowerForTime(i);
            if (power >= 1.0F && !isCharged(itemStack) && tryLoadProjectiles(entity, itemStack)) {
                if (entity instanceof CrossbowAttackMob crossbowAttackMob) {
                    setCharged(itemStack, true);
                }
            }
            return;
        }
        int i = this.getUseDuration(itemStack, player) - ticks;
        float power = getPowerForTime(i);
        if (power < 0.1F) {
            return;
        }
        if (!ItemFeaturesConfig.STAKE_CROSSBOW_ALT_SHOOT.get()) {
            if (power >= 1.0F && !isCharged(itemStack) && tryLoadProjectiles(player, itemStack)) {
                setCharged(itemStack, true);
                SoundSource source = SoundSource.PLAYERS;
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_LOADING_END.value(),
                        source, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
            return;
        }
        boolean isCreative = player.getAbilities().instabuild;
        boolean hasInfinity = ModEnchantments.level(itemStack, Enchantments.INFINITY) > 0;
        boolean hasMultishot = ModEnchantments.level(itemStack, Enchantments.MULTISHOT) > 0;
        if (!level.isClientSide) {
            int projectileCount = hasMultishot ? 3 : 1;
            float[] shotPitches = generateShotPitches(level.getRandom(), projectileCount);
            for (int j = 0; j < projectileCount; j++) {
                ItemStack stakeWeaponItem = new ItemStack(ModItems.STAKE.get());
                StakeProjectileEntity projectile = new StakeProjectileEntity(level, player, stakeWeaponItem);
                projectile.setOwner(player);
                projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                int powerLevel = ModEnchantments.level(itemStack, Enchantments.POWER);
                if (powerLevel > 0) {
                    float base = projectile.getPersistentData().getFloat("BaseWeaponDamage");
                    projectile.setWeaponBaseDamage(base + powerLevel * 0.3F + 0.3F);
                }
                int punchLevel = ModEnchantments.level(itemStack, Enchantments.PUNCH);
                if (punchLevel > 0) {
                    float baseKB = projectile.getPersistentData().getFloat("BaseKnockback");
                    projectile.setWeaponBaseKnockback(baseKB + punchLevel * 0.5F);
                }
                if (ModEnchantments.level(itemStack, Enchantments.FLAME) > 0) {
                    projectile.igniteForSeconds(60);
                }
                int pierceLevel = ModEnchantments.level(itemStack, Enchantments.PIERCING);
                if (pierceLevel > 0) {
                    projectile.setPiercingLevel((byte) pierceLevel);
                }
                if (power == 1.0F) {
                    projectile.setCritArrow(true);
                }
                if (isCreative) {
                    projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                } else if (hasMultishot) {
                    projectile.pickup = (j == 0) ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
                } else {
                    projectile.pickup = AbstractArrow.Pickup.ALLOWED;
                }
                if (hasMultishot) {
                    float spreadAngle = 10.0F;
                    float rotation = (j == 0) ? 0.0F : (j == 1) ? -spreadAngle : spreadAngle;
                    Vec3 upVector = player.getUpVector(1.0F);
                    Quaternionf rotationQuaternion = new Quaternionf().setAngleAxis(rotation * (float) Math.PI / 180F,
                            upVector.x, upVector.y, upVector.z);
                    Vec3 viewVector = player.getViewVector(1.0F);
                    Vector3f rotatedVector = viewVector.toVector3f().rotate(rotationQuaternion);
                    projectile.shoot(rotatedVector.x(), rotatedVector.y(), rotatedVector.z(), power * maxVelocity,
                            1.0F);
                } else {
                    Vec3 viewVector = player.getViewVector(1.0F);
                    projectile.shoot(viewVector.x, viewVector.y, viewVector.z, power * maxVelocity, 1.0F);
                }
                level.addFreshEntity(projectile);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT,
                        SoundSource.PLAYERS, 1.0F, shotPitches[j]);
            }
            if (!isCreative) {
                itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            }
            if (!isCreative && !hasInfinity) {
                ItemStack stakeStack = findStake(player);
                if (!stakeStack.isEmpty()) {
                    stakeStack.shrink(1);
                    if (stakeStack.isEmpty()) {
                        player.getInventory().removeItem(stakeStack);
                    }
                }
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    public static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbowStack) {
        boolean isCreative = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
        boolean hasInfinity = ModEnchantments.level(crossbowStack, Enchantments.INFINITY) > 0;
        if (isCreative || hasInfinity) {
            addChargedProjectile(crossbowStack, new ItemStack(ModItems.STAKE.get()));
            return true;
        }
        ItemStack stakeStack = findStake(shooter);
        if (!stakeStack.isEmpty()) {
            addChargedProjectile(crossbowStack, new ItemStack(ModItems.STAKE.get()));
            stakeStack.shrink(1);
            if (stakeStack.isEmpty() && shooter instanceof Player player) {
                player.getInventory().removeItem(stakeStack);
            }
            return true;
        }
        return false;
    }

    private static ItemStack findStake(LivingEntity entity) {
        if (entity instanceof Player player) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == ModItems.STAKE.get()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean hasStake(Player player) {
        return !findStake(player).isEmpty() || player.getAbilities().instabuild;
    }

    private static void setCharged(ItemStack crossbowStack, boolean charged) {
        if (charged) {
            if (!isCharged(crossbowStack)) {
                crossbowStack.set(DataComponents.CHARGED_PROJECTILES,
                        ChargedProjectiles.of(new ItemStack(ModItems.STAKE.get())));
            }
        } else {
            crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }
    }

    public static void addChargedProjectile(ItemStack crossbowStack, ItemStack ammoStack) {
        List<ItemStack> projectiles = new ArrayList<>(
                crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems());
        projectiles.add(ammoStack);
        crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(projectiles));
    }

    public static void executeShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbowStack,
            float velocity, float inaccuracy) {
        List<ItemStack> projectiles = getChargedProjectiles(crossbowStack);
        float[] pitches = generateShotPitches(shooter.getRandom());
        boolean hasMultishot = ModEnchantments.level(crossbowStack, Enchantments.MULTISHOT) > 0;
        int projectileCount = hasMultishot ? 3 : 1;
        ItemStack projectileStack = projectiles.isEmpty() ? new ItemStack(ModItems.STAKE.get()) : projectiles.get(0);
        boolean isCreative = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
        for (int i = 0; i < projectileCount; i++) {
            if (!projectileStack.isEmpty()) {
                float rotation = hasMultishot ? (i == 0 ? 0.0F : (i == 1 ? -10.0F : 10.0F)) : 0.0F;
                fireProjectile(level, shooter, hand, crossbowStack, projectileStack, pitches[i], isCreative, velocity,
                        inaccuracy, rotation);
            }
        }
        handleCrossbowShot(level, shooter, crossbowStack);
    }

    private static void fireProjectile(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbowStack,
            ItemStack projectileStack, float soundPitch, boolean isCreative, float velocity, float inaccuracy,
            float rotation) {
        if (!level.isClientSide) {
            ItemStack stakeWeaponItem = new ItemStack(ModItems.STAKE.get());
            StakeProjectileEntity projectile = new StakeProjectileEntity(level, shooter, stakeWeaponItem);
            projectile.setOwner(shooter);
            projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.15, shooter.getZ());
            int powerLevel = ModEnchantments.level(crossbowStack, Enchantments.POWER);
            if (powerLevel > 0) {
                float base = projectile.getPersistentData().getFloat("BaseWeaponDamage");
                projectile.setWeaponBaseDamage(base + powerLevel * 0.3F + 0.3F);
            }
            int punchLevel = ModEnchantments.level(crossbowStack, Enchantments.PUNCH);
            if (punchLevel > 0) {
                float baseKB = projectile.getPersistentData().getFloat("BaseKnockback");
                projectile.setWeaponBaseKnockback(baseKB + punchLevel * 0.5F);
            }
            if (ModEnchantments.level(crossbowStack, Enchantments.FLAME) > 0) {
                projectile.igniteForSeconds(60);
            }
            int pierceLevel = ModEnchantments.level(crossbowStack, Enchantments.PIERCING);
            if (pierceLevel > 0) {
                projectile.setPiercingLevel((byte) pierceLevel);
            }
            if (shooter instanceof Player) {
                projectile.setCritArrow(true);
            }
            projectile.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            projectile.setShotFromCrossbow(true);
            boolean hasMultishot = ModEnchantments.level(crossbowStack, Enchantments.MULTISHOT) > 0;
            if (isCreative) {
                projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            } else if (hasMultishot) {
                projectile.pickup = (rotation == 0.0F) ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
            } else {
                projectile.pickup = AbstractArrow.Pickup.ALLOWED;
            }
            if (shooter instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
                net.minecraft.world.entity.LivingEntity aimTarget = mob.getTarget();
                double dx = aimTarget.getX() - shooter.getX();
                double dz = aimTarget.getZ() - shooter.getZ();
                double horiz = Math.sqrt(dx * dx + dz * dz);
                double dy = aimTarget.getY(0.3333333333333333) - projectile.getY() + horiz * 0.20000000298023224;
                projectile.shoot(dx, dy, dz, velocity, inaccuracy);
            } else {
                Vec3 upVector = shooter.getUpVector(1.0F);
                Quaternionf rotationQuaternion = new Quaternionf().setAngleAxis(rotation * (float) Math.PI / 180F,
                        upVector.x, upVector.y, upVector.z);
                Vec3 viewVector = shooter.getViewVector(1.0F);
                Vector3f rotatedVector = viewVector.toVector3f().rotate(rotationQuaternion);
                projectile.shoot(rotatedVector.x(), rotatedVector.y(), rotatedVector.z(), velocity, inaccuracy);
            }
            crossbowStack.hurtAndBreak(1, shooter, LivingEntity.getSlotForHand(hand));
            level.addFreshEntity(projectile);
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT,
                    SoundSource.PLAYERS, 1.0F, soundPitch);
        }
    }

    private static void handleCrossbowShot(Level level, LivingEntity shooter, ItemStack crossbowStack) {
        if (shooter instanceof ServerPlayer serverPlayer) {
            if (!level.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, crossbowStack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(crossbowStack.getItem()));
        }
        clearChargedProjectiles(crossbowStack);
    }

    private static float[] generateShotPitches(RandomSource random, int projectileCount) {
        float[] pitches = new float[Math.max(projectileCount, 1)];
        for (int i = 0; i < pitches.length; i++) {
            boolean flag = random.nextBoolean();
            pitches[i] = 1.0F / (random.nextFloat() * 0.5F + 1.8F) + (flag ? 0.63F : 0.43F);
        }
        return pitches;
    }

    private static float[] generateShotPitches(RandomSource random) {
        boolean flag = random.nextBoolean();
        return new float[] { 1.0F, getRandomShotPitch(flag, random), getRandomShotPitch(!flag, random) };
    }

    private static float getRandomShotPitch(boolean flag, RandomSource random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack crossbowStack) {
        return crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems();
    }

    private static void clearChargedProjectiles(ItemStack crossbowStack) {
        crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
    }

    public static float getPowerForTime(int useTime) {
        float f = (float) useTime / (float) loadTime;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getChargeDuration(stack) + 3;
    }

    public static int getChargeDuration(ItemStack crossbowStack) {
        int i = ModEnchantments.level(crossbowStack, Enchantments.QUICK_CHARGE);
        return i == 0 ? loadTime : loadTime - getChargeTimeReductionPerQuickChargeLevel() * i;
    }

    public static int getChargeTimeReductionPerQuickChargeLevel() {
        return loadTime / 6;
    }

    @Override
    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
    }
}
