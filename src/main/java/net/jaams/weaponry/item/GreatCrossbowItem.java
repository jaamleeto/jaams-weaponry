
package net.jaams.weaponry.item;

import net.jaams.weaponry.init.ModEnchantments;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Vector3f;
import org.joml.Quaternionf;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.component.ChargedProjectiles;
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
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.advancements.CriteriaTriggers;

import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;

import java.util.List;
import java.util.ArrayList;

public class GreatCrossbowItem extends CrossbowItem {
    public static final float maxVelocity = 4.0F;
    public static final int loadTime = 40;
    public static final float BASE_DAMAGE = 2.0F;
    public static final float DRAW_SPEED = 0.5F;
    public boolean startSoundPlayed = false;
    public boolean midLoadSoundPlayed = true;
    public static final float RECOIL_DISTANCE = 0.65F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.6F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.7F;
    public static final float XROT_RECOIL_INTENSITY = 4.5F;

    public GreatCrossbowItem() {
        super(new Item.Properties().durability(426).rarity(Rarity.COMMON));
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
        return UseAnim.CROSSBOW;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.POWER) || enchantment.is(Enchantments.PUNCH)) {
            return true;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isCharged(itemstack)) {
            executeShooting(level, player, hand, itemstack, getShootingPower(itemstack), 2.0F);
            player.setSprinting(false);
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
        } else if (!player.getProjectile(itemstack).isEmpty()) {
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack, entityLiving) - timeLeft;
        float f = getPowerForTime(i, stack);
        if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(entityLiving, stack)) {
            SoundSource soundsource = entityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END.value(), soundsource, 1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public static float getShootingPower(ItemStack crossbowStack) {
        return crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).contains(Items.FIREWORK_ROCKET) ? 0.7F : maxVelocity;
    }

    public static float getPowerForTime(int useTime, ItemStack crossbowStack) {
        float f = (float) useTime / (float) getChargeDuration(crossbowStack);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    public static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbowStack) {
        int i = ModEnchantments.level(crossbowStack, Enchantments.MULTISHOT);
        int j = i == 0 ? 1 : 3;
        boolean flag = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
        ItemStack itemstack = shooter.getProjectile(crossbowStack);
        ItemStack itemstack1 = itemstack.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }
            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }
            if (!loadProjectile(shooter, crossbowStack, itemstack, k > 0, flag)) {
                return false;
            }
        }
        return true;
    }

    public static boolean loadProjectile(LivingEntity shooter, ItemStack crossbowStack, ItemStack ammoStack,
            boolean hasAmmo, boolean isCreative) {
        if (ammoStack.isEmpty()) {
            return false;
        } else {
            boolean flag = isCreative && ammoStack.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !isCreative && !hasAmmo) {
                itemstack = ammoStack.split(1);
                if (ammoStack.isEmpty() && shooter instanceof Player) {
                    ((Player) shooter).getInventory().removeItem(ammoStack);
                }
            } else {
                itemstack = ammoStack.copy();
            }
            addChargedProjectile(crossbowStack, itemstack);
            return true;
        }
    }

    public static void addChargedProjectile(ItemStack crossbowStack, ItemStack ammoStack) {
        List<ItemStack> projectiles = new ArrayList<>(
                crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems());
        projectiles.add(ammoStack);
        crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(projectiles));
    }

    public static int getChargeDuration(ItemStack crossbowStack) {
        int i = ModEnchantments.level(crossbowStack, Enchantments.QUICK_CHARGE);
        return i == 0 ? loadTime : loadTime - getChargeTimeReductionPerQuickChargeLevel() * i;
    }

    public static int getChargeTimeReductionPerQuickChargeLevel() {
        return loadTime / 6;
    }

    @Override
    public int getUseDuration(ItemStack p_40938_, LivingEntity p_344898_) {
        return getChargeDuration(p_40938_) + 3;
    }

    private static void fireProjectile(Level level, LivingEntity entity, InteractionHand hand, ItemStack crossbow,
            ItemStack projectileItem, float soundPitch, boolean isCreative, float velocity, float inaccuracy,
            float rotation) {
        if (!level.isClientSide) {
            boolean isFirework = projectileItem.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (isFirework) {
                projectile = new FireworkRocketEntity(level, projectileItem, entity, entity.getX(),
                        entity.getEyeY() - 0.15, entity.getZ(), true);
            } else {
                projectile = createArrowProjectile(level, entity, crossbow, projectileItem);
                if (isCreative || rotation != 0.0F) {
                    ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            if (entity instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
                net.minecraft.world.entity.LivingEntity aimTarget = mob.getTarget();
                double dx = aimTarget.getX() - entity.getX();
                double dz = aimTarget.getZ() - entity.getZ();
                double horiz = Math.sqrt(dx * dx + dz * dz);
                double dy = aimTarget.getY(0.3333333333333333) - projectile.getY() + horiz * 0.20000000298023224;
                projectile.shoot(dx, dy, dz, velocity, inaccuracy);
            } else {
                Vec3 upVector = entity.getUpVector(1.0F);
                Quaternionf rotationQuaternion = new Quaternionf().setAngleAxis(rotation * (float) Math.PI / 180F,
                        upVector.x, upVector.y, upVector.z);
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vector3f rotatedVector = viewVector.toVector3f().rotate(rotationQuaternion);
                projectile.shoot(rotatedVector.x(), rotatedVector.y(), rotatedVector.z(), velocity, inaccuracy);
            }
            crossbow.hurtAndBreak(isFirework ? 3 : 1, entity, LivingEntity.getSlotForHand(hand));
            level.addFreshEntity(projectile);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_SHOOT,
                    SoundSource.PLAYERS, 1.0F, soundPitch);
        }
    }

    private static AbstractArrow createArrowProjectile(Level level, LivingEntity shooter, ItemStack crossbowStack,
            ItemStack arrowStack) {
        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem()
                : Items.ARROW);
        AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, shooter, crossbowStack);
        if (shooter instanceof Player) {
            arrow.setCritArrow(true);
        }
        if (shooter instanceof net.minecraft.world.entity.Mob) {
            arrow.setBaseDamage(arrow.getBaseDamage() * 2.5F);
        }
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        if (ModEnchantments.level(crossbowStack, Enchantments.FLAME) > 0) {
            arrow.igniteForSeconds(60);
        }
        int powerLevel = ModEnchantments.level(crossbowStack, Enchantments.POWER);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.3D + 0.3D);
        }
        return arrow;
    }

    public static void executeShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbowStack,
            float velocity, float inaccuracy) {
        if (shooter instanceof Player player && net.neoforged.neoforge.event.EventHooks.onArrowLoose(crossbowStack,
                shooter.level(), player, 1, true) < 0)
            return;
        List<ItemStack> chargedProjectiles = getChargedProjectiles(crossbowStack);
        float[] shotPitches = generateShotPitches(shooter.getRandom());
        for (int i = 0; i < chargedProjectiles.size(); ++i) {
            ItemStack projectileStack = chargedProjectiles.get(i);
            boolean isCreative = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
            if (!projectileStack.isEmpty()) {
                if (i == 0) {
                    fireProjectile(level, shooter, hand, crossbowStack, projectileStack, shotPitches[i], isCreative,
                            velocity, inaccuracy, 0.0F);
                } else if (i == 1) {
                    fireProjectile(level, shooter, hand, crossbowStack, projectileStack, shotPitches[i], isCreative,
                            velocity, inaccuracy, -10.0F);
                } else if (i == 2) {
                    fireProjectile(level, shooter, hand, crossbowStack, projectileStack, shotPitches[i], isCreative,
                            velocity, inaccuracy, 10.0F);
                }
            }
        }
        handleCrossbowShot(level, shooter, crossbowStack);
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

    private static float[] generateShotPitches(RandomSource randomSource) {
        boolean flag = randomSource.nextBoolean();
        return new float[] { 1.0F, getRandomShotPitch(flag, randomSource), getRandomShotPitch(!flag, randomSource) };
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack crossbowStack) {
        return crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems();
    }

    private static void clearChargedProjectiles(ItemStack p_40944_) {
        p_40944_.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
    }

    private static float getRandomShotPitch(boolean p_220026_, RandomSource p_220027_) {
        float f = p_220026_ ? 0.63F : 0.43F;
        return 1.0F / (p_220027_.nextFloat() * 0.5F + 1.8F) + f;
    }
}
