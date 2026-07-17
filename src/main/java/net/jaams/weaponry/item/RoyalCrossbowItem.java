
package net.jaams.weaponry.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Vector3f;
import org.joml.Quaternionf;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.advancements.CriteriaTriggers;

import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class RoyalCrossbowItem extends CrossbowItem {
    
    private static final Random RANDOM = new Random();
    public static final float MAX_VELOCITY = 4.5F;
    public static final int LOAD_TIME = 15;
    public static final float BASE_DAMAGE = 1.5F;
    public static final float DRAW_SPEED = 1.0F;
    public static final float RECOIL_DISTANCE = 0.3F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.4F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.3F;
    public static final float XROT_RECOIL_INTENSITY = 2.0F;
    public boolean startSoundPlayed = false;
    public boolean midLoadSoundPlayed = false;

    public RoyalCrossbowItem() {
        super(new Item.Properties().durability(426).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        ModTooltips.addStat(stack, tooltip, "base_damage", getNbtDouble(stack, "CrossbowBaseDamage", BASE_DAMAGE));
        ModTooltips.addStat(stack, tooltip, "draw_speed", getNbtDouble(stack, "CrossbowDrawSpeed", DRAW_SPEED));
        ModTooltips.addStat(stack, tooltip, "load_time", getNbtDouble(stack, "CrossbowLoadTime", LOAD_TIME / 20.0));
        ModTooltips.addStat(stack, tooltip, "recoil", getNbtDouble(stack, "CrossbowRecoilDistance", RECOIL_DISTANCE));
    }

    private static double getNbtDouble(ItemStack stack, String key, double defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key, Tag.TAG_DOUBLE)) {
            return stack.getTag().getDouble(key);
        }
        return defaultValue;
    }

    @Override
    public int getEnchantmentValue() {
        return 16;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isCharged(stack)) {
            executeShooting(level, player, hand, stack, getShootingPower(stack), 1.0F);
            setCharged(stack, false);
            float recoilDistance = (float) getNbtDouble(stack, "CrossbowRecoilDistance", RECOIL_DISTANCE);
            if (recoilDistance > 0.0F) {
                float crouchReduction = (float) getNbtDouble(stack, "CrossbowRecoilCrouchReduction", CROUCH_RECOIL_REDUCTION);
                float verticalMultiplier = (float) getNbtDouble(stack, "CrossbowRecoilVerticalMultiplier", VERTICAL_RECOIL_MULTIPLIER);
                ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMultiplier);
            }
            float xrotRecoil = (float) getNbtDouble(stack, "CrossbowRecoilXROT", XROT_RECOIL_INTENSITY);
            if (xrotRecoil != 0.0F) {
                player.setXRot(player.getXRot() - xrotRecoil);
            }
            return InteractionResultHolder.consume(stack);
        } else if (!player.getProjectile(stack).isEmpty()) {
            if (!isCharged(stack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                player.startUsingItem(hand);
            }
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    public static float getShootingPower(ItemStack stack) {
        return containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 0.7F : MAX_VELOCITY;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack) - timeLeft;
        float f = getPowerForTime(i, stack);
        if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(entityLiving, stack)) {
            setCharged(stack, true);
            SoundSource source = entityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, source, 1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public static float getPowerForTime(int useTime, ItemStack stack) {
        float f = (float) useTime / (float) getChargeDuration(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    public static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack stack) {
        int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean flag = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
        ItemStack projectile = shooter.getProjectile(stack);
        ItemStack copy = projectile.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                projectile = copy.copy();
            }
            if (projectile.isEmpty() && flag) {
                projectile = new ItemStack(Items.ARROW);
                copy = projectile.copy();
            }
            if (!loadProjectile(shooter, stack, projectile, k > 0, flag)) {
                return false;
            }
        }
        return true;
    }

    public static boolean loadProjectile(LivingEntity shooter, ItemStack stack, ItemStack ammo, boolean hasAmmo,
            boolean isCreative) {
        if (ammo.isEmpty()) {
            return false;
        } else {
            boolean flag = isCreative && ammo.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !isCreative && !hasAmmo) {
                itemstack = ammo.split(1);
                if (ammo.isEmpty() && shooter instanceof Player) {
                    ((Player) shooter).getInventory().removeItem(ammo);
                }
            } else {
                itemstack = ammo.copy();
            }
            addChargedProjectile(stack, itemstack);
            return true;
        }
    }

    public static void addChargedProjectile(ItemStack stack, ItemStack ammo) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list;
        if (tag.contains("ChargedProjectiles", 9)) {
            list = tag.getList("ChargedProjectiles", 10);
        } else {
            list = new ListTag();
        }
        CompoundTag ammoTag = new CompoundTag();
        ammo.save(ammoTag);
        list.add(ammoTag);
        tag.put("ChargedProjectiles", list);
    }

    public static int getChargeDuration(ItemStack stack) {
        int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? LOAD_TIME : LOAD_TIME - getChargeTimeReductionPerQuickChargeLevel() * i;
    }

    public static int getChargeTimeReductionPerQuickChargeLevel() {
        return LOAD_TIME / 7;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return getChargeDuration(stack) + 3;
    }

    private static void fireProjectile(Level level, LivingEntity entity, InteractionHand hand, ItemStack crossbow,
            ItemStack projectile, float pitch, boolean isCreative, float velocity, float inaccuracy, float rotation) {
        if (!level.isClientSide) {
            boolean isFirework = projectile.is(Items.FIREWORK_ROCKET);
            Projectile proj;
            if (isFirework) {
                proj = new FireworkRocketEntity(level, projectile, entity, entity.getX(), entity.getEyeY() - 0.15,
                        entity.getZ(), true);
            } else {
                proj = createArrowProjectile(level, entity, crossbow, projectile);
                if (isCreative || rotation != 0.0F) {
                    ((AbstractArrow) proj).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            if (entity instanceof CrossbowAttackMob mob) {
                mob.shootCrossbowProjectile(mob.getTarget(), crossbow, proj, rotation);
            } else {
                Vec3 up = entity.getUpVector(1.0F);
                Quaternionf quat = new Quaternionf().setAngleAxis(rotation * (float) Math.PI / 180F, up.x, up.y, up.z);
                Vec3 view = entity.getViewVector(1.0F);
                Vector3f rotated = view.toVector3f().rotate(quat);
                proj.shoot(rotated.x(), rotated.y(), rotated.z(), velocity, inaccuracy);
            }
            crossbow.hurtAndBreak(isFirework ? 3 : 1, entity, (e) -> e.broadcastBreakEvent(hand));
            level.addFreshEntity(proj);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_SHOOT,
                    SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    private static AbstractArrow createArrowProjectile(Level level, LivingEntity shooter, ItemStack crossbow,
            ItemStack arrow) {
        ArrowItem arrowItem = (ArrowItem) (arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        AbstractArrow proj = arrowItem.createArrow(level, arrow, shooter);
        if (shooter instanceof Player) {
            proj.setCritArrow(true);
        }
        proj.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        proj.setShotFromCrossbow(true);
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FLAMING_ARROWS, crossbow) > 0) {
            proj.setSecondsOnFire(100);
        }
        int pierce = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (pierce > 0) {
            proj.setPierceLevel((byte) (pierce + 1));
        }
        int power = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, crossbow);
        if (power > 0) {
            proj.setBaseDamage(proj.getBaseDamage() + (double) power * 0.5D + 0.5D);
        }
        int punch = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PUNCH_ARROWS, crossbow);
        if (punch > 0) {
            proj.setKnockback(punch + 1);
        }
        return proj;
    }

    public static void executeShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack,
            float velocity, float inaccuracy) {
        if (shooter instanceof Player player && MinecraftForge.EVENT_BUS
                .post(new net.minecraftforge.event.entity.player.ArrowLooseEvent(player, stack, level, 1, true))) {
            return;
        }
        List<ItemStack> projectiles = getChargedProjectiles(stack);
        float[] pitches = generateShotPitches(shooter.getRandom());
        for (int i = 0; i < projectiles.size(); ++i) {
            ItemStack proj = projectiles.get(i);
            boolean isCreative = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
            if (!proj.isEmpty()) {
                if (i == 0) {
                    fireProjectile(level, shooter, hand, stack, proj, pitches[i], isCreative, velocity, inaccuracy,
                            0.0F);
                } else if (i == 1) {
                    fireProjectile(level, shooter, hand, stack, proj, pitches[i], isCreative, velocity, inaccuracy,
                            -10.0F);
                } else if (i == 2) {
                    fireProjectile(level, shooter, hand, stack, proj, pitches[i], isCreative, velocity, inaccuracy,
                            10.0F);
                }
            }
        }
        handleCrossbowShot(level, shooter, stack);
    }

    private static void handleCrossbowShot(Level level, LivingEntity shooter, ItemStack stack) {
        if (shooter instanceof ServerPlayer player) {
            if (!level.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(player, stack);
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
        clearChargedProjectiles(stack);
    }

    private static float[] generateShotPitches(RandomSource random) {
        boolean flag = random.nextBoolean();
        return new float[] { 1.0F, getRandomShotPitch(flag, random), getRandomShotPitch(!flag, random) };
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
        List<ItemStack> projectiles = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ChargedProjectiles", 9)) {
            ListTag list = tag.getList("ChargedProjectiles", 10);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag projTag = list.getCompound(i);
                projectiles.add(ItemStack.of(projTag));
            }
        }
        return projectiles;
    }

    private static void clearChargedProjectiles(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            ListTag list = tag.getList("ChargedProjectiles", 9);
            list.clear();
            tag.put("ChargedProjectiles", list);
        }
    }

    private static float getRandomShotPitch(boolean flag, RandomSource random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }
}
