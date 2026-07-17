
package net.jaams.weaponry.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;

import javax.annotation.ParametersAreNonnullByDefault;

public class CompoundBowItem extends BowItem {
    public static final float MAX_DRAW_DURATION = 40.0F;
    public static final float MAX_VELOCITY = 4.0F;
    public static final int DEFAULT_RANGE = 30;
    public static final float BASE_DAMAGE = 1.5F;
    public static final float DRAW_SPEED = 0.75F;
    public static final float RECOIL_DISTANCE = 0.7F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.6F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.3F;
    public static final float XROT_RECOIL_INTENSITY = 3.5F;

    public CompoundBowItem() {
        super(new Item.Properties().durability(584).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        ModTooltips.addStat(stack, tooltip, "base_damage", getNbtDouble(stack, "BowBaseDamage", BASE_DAMAGE));
        ModTooltips.addStat(stack, tooltip, "max_draw_time", getNbtDouble(stack, "BowMaxDrawTime", MAX_DRAW_DURATION / 20.0));
        ModTooltips.addStat(stack, tooltip, "draw_speed", getNbtDouble(stack, "BowDrawSpeed", DRAW_SPEED));
        ModTooltips.addStat(stack, tooltip, "recoil", getNbtDouble(stack, "BowRecoilDistance", RECOIL_DISTANCE));
    }

    private static double getNbtDouble(ItemStack stack, String key, double defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key, Tag.TAG_DOUBLE)) {
            return stack.getTag().getDouble(key);
        }
        return defaultValue;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public int getEnchantmentValue() {
        return 6;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack,
            net.minecraft.world.item.enchantment.Enchantment enchantment) {
        if (enchantment == Enchantments.PIERCING) {
            return true;
        }
        if (enchantment == Enchantments.INFINITY_ARROWS) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity living, int ticks) {
        if (living instanceof Player player) {
            boolean flag = player.getAbilities().instabuild
                    || EnchantmentHelper.getTagEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack) > 0;
            ItemStack itemstack = player.getProjectile(itemStack);
            int i = this.getUseDuration(itemStack) - ticks;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, level, player, i,
                    !itemstack.isEmpty() || flag);
            if (i < 0)
                return;
            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }
                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem
                            && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, itemStack, player));
                    if (!level.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem
                                ? itemstack.getItem()
                                : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F,
                                f * MAX_VELOCITY, 3.0F);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + 0.5F);
                            float recoilDistance = (float) getNbtDouble(itemStack, "BowRecoilDistance", RECOIL_DISTANCE);
                            if (recoilDistance > 0.0F) {
                                float crouchReduction = (float) getNbtDouble(itemStack, "BowRecoilCrouchReduction", CROUCH_RECOIL_REDUCTION);
                                float verticalMultiplier = (float) getNbtDouble(itemStack, "BowRecoilVerticalMultiplier", VERTICAL_RECOIL_MULTIPLIER);
                                ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMultiplier);
                            }
                        }
                        int j = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.3D + 0.3D);
                        }
                        int k = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
                        if (k > 0) {
                            abstractarrow.setKnockback(k);
                        }
                        int p = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, itemStack);
                        if (p > 0) {
                            abstractarrow.setPierceLevel((byte) p);
                        }
                        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack) > 0) {
                            abstractarrow.setSecondsOnFire(80);
                        }
                        itemStack.hurtAndBreak(1, player, (player1) -> {
                            player1.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        if (flag1 || player.getAbilities().instabuild
                                && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }
                        level.addFreshEntity(abstractarrow);
                    } else if (f == 1.0F) {
                        float xrotRecoil = (float) getNbtDouble(itemStack, "BowRecoilXROT", XROT_RECOIL_INTENSITY);
                        if (xrotRecoil != 0.0F) {
                            player.setXRot(player.getXRot() - xrotRecoil);
                        }
                    }
                    level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT,
                            SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public static float getPowerForTime(int duration) {
        float f = (float) duration / MAX_DRAW_DURATION;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return Math.min(f, 1.0F);
    }

    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
    }
}
