
package net.jaams.weaponry.item;

import net.jaams.weaponry.init.ModEnchantments;

import net.jaams.weaponry.util.ModComponents;

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

public class RoyalBowItem extends BowItem {
    
    public static final float MAX_DRAW_DURATION = 25.0F;
    public static final float MAX_VELOCITY = 3.0F;
    public static final int DEFAULT_RANGE = 15;
    public static final float BASE_DAMAGE = 1.5F;
    public static final float DRAW_SPEED = 1.0F;
    public static final float RECOIL_DISTANCE = 0.2F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.3F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.15F;
    public static final float XROT_RECOIL_INTENSITY = 1.5F;

    public RoyalBowItem() {
        super(new Item.Properties().durability(684).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        ModTooltips.addStat(stack, tooltip, "base_damage", getNbtDouble(stack, "BowBaseDamage", BASE_DAMAGE));
        ModTooltips.addStat(stack, tooltip, "max_draw_time", getNbtDouble(stack, "BowMaxDrawTime", MAX_DRAW_DURATION / 20.0));
        ModTooltips.addStat(stack, tooltip, "draw_speed", getNbtDouble(stack, "BowDrawSpeed", DRAW_SPEED));
        ModTooltips.addStat(stack, tooltip, "recoil", getNbtDouble(stack, "BowRecoilDistance", RECOIL_DISTANCE));
    }

    private static double getNbtDouble(ItemStack stack, String key, double defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(key, Tag.TAG_DOUBLE)) {
            return ModComponents.get(stack).getDouble(key);
        }
        return defaultValue;
    }

    @Override
    public int getUseDuration(ItemStack itemstack, net.minecraft.world.entity.LivingEntity entityLiving) {
        return 72000;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity living, int ticks) {
        if (living instanceof Player player) {
            boolean flag = player.getAbilities().instabuild
                    || ModEnchantments.level(itemStack, Enchantments.INFINITY) > 0;
            ItemStack itemstack = player.getProjectile(itemStack);
            int i = this.getUseDuration(itemStack, player) - ticks;
            i = net.neoforged.neoforge.event.EventHooks.onArrowLoose(itemStack, level, player, i,
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
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player, itemStack);
                        abstractarrow = customArrow(abstractarrow, itemstack, itemStack);
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F,
                                f * MAX_VELOCITY, 1.5F);
                        abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + 1.5D);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                            float recoilDistance = (float) getNbtDouble(itemStack, "BowRecoilDistance", RECOIL_DISTANCE);
                            if (recoilDistance > 0.0F) {
                                float crouchReduction = (float) getNbtDouble(itemStack, "BowRecoilCrouchReduction", CROUCH_RECOIL_REDUCTION);
                                float verticalMultiplier = (float) getNbtDouble(itemStack, "BowRecoilVerticalMultiplier", VERTICAL_RECOIL_MULTIPLIER);
                                ModUtils.applyRecoil(player, recoilDistance, crouchReduction, verticalMultiplier);
                            }
                        }
                        int j = ModEnchantments.level(itemStack, Enchantments.POWER);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.75D + 0.5D);
                        }
                        if (ModEnchantments.level(itemStack, Enchantments.FLAME) > 0) {
                            abstractarrow.igniteForSeconds(200);
                        }
                        itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
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
