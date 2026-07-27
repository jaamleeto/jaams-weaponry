package net.jaams.weaponry.item;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class ShortBowItem extends BowItem {

    private boolean isShortbowAuto = false;
    public static final float MAX_DRAW_DURATION = 20.0F;
    public static final float MAX_VELOCITY = 1.5F;
    public static final int DEFAULT_RANGE = 10;
    public static final float BASE_DAMAGE = 0.5F;
    public static final float DRAW_SPEED = 2.0F;
    public static final float RECOIL_DISTANCE = 0.0F;
    public static final float CROUCH_RECOIL_REDUCTION = 0.3F;
    public static final float VERTICAL_RECOIL_MULTIPLIER = 0.1F;
    public static final float XROT_RECOIL_INTENSITY = 0.0F;

    public ShortBowItem() {
        super(new Item.Properties().durability(284).rarity(Rarity.COMMON));
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
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemstack, net.minecraft.world.entity.LivingEntity entityLiving) {
        this.isShortbowAuto = ItemFeaturesConfig.SHORT_BOW_AUTO_SHOOT.get();
        if (this.isShortbowAuto) {
            return 30;
        } else {
            return 72000;
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide) {
            if (entityLiving instanceof Player) {
                Player player = (Player) entityLiving;
                this.isShortbowAuto = ItemFeaturesConfig.SHORT_BOW_AUTO_SHOOT.get();
                shootArrow(stack, level, player, 1.0f);
            } else if (entityLiving instanceof AbstractSkeleton) {
                AbstractSkeleton skeleton = (AbstractSkeleton) entityLiving;
                this.isShortbowAuto = ItemFeaturesConfig.SHORT_BOW_AUTO_SHOOT.get();
                shootArrow(stack, level, skeleton, 1.0f);
            }
        }
        return stack;
    }

    private void shootArrow(ItemStack itemStack, Level level, LivingEntity living, float velocity) {
        boolean flag = (living instanceof Player && ((Player) living).getAbilities().instabuild)
                || ModEnchantments.level(itemStack, Enchantments.INFINITY) > 0;
        ItemStack arrowStack = living.getProjectile(itemStack);
        if (arrowStack.isEmpty() && !flag) {
            return;
        }
        if (!level.isClientSide) {
            AbstractArrow abstractarrow = ((ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem()
                    : Items.ARROW)).createArrow(level, arrowStack, living, itemStack);
            if (living instanceof Player) {
                Player player = (Player) living;
                abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F * 2.0F, 1.0F);
                abstractarrow.setCritArrow(true);
            } else if (living instanceof AbstractSkeleton) {
                abstractarrow.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, 1.0F * 2.0F, 1.0F);
                abstractarrow.setCritArrow(true);
                abstractarrow.setBaseDamage(0.5F);
            }
            int aftermathLevel = ModEnchantments.level(itemStack, ModEnchantments.AFTERMATH);
            if (aftermathLevel > 0) {
                abstractarrow.getPersistentData().putInt("AftermathLevel", aftermathLevel);
            }
            int powerLevel = ModEnchantments.level(itemStack, Enchantments.POWER);
            double damageModifier = 0.4D * powerLevel + 0.4D;
            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + damageModifier);
            int punchLevel = ModEnchantments.level(itemStack, Enchantments.PUNCH);
            if (punchLevel > 0) {
            }
            if (ModEnchantments.level(itemStack, Enchantments.FLAME) > 0) {
                abstractarrow.igniteForSeconds(20);
            }
            level.addFreshEntity(abstractarrow);
            itemStack.hurtAndBreak(1, living, LivingEntity.getSlotForHand(living.getUsedItemHand()));
            if (flag || ((living instanceof Player && ((Player) living).getAbilities().instabuild)
                    && (arrowStack.is(Items.SPECTRAL_ARROW) || arrowStack.is(Items.TIPPED_ARROW)))) {
                abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.ARROW_SHOOT,
                    SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 1.0F * 0.5F);
            if (!flag && (living instanceof Player && !((Player) living).getAbilities().instabuild)) {
                if (living instanceof Player) {
                    arrowStack.shrink(1);
                    if (arrowStack.isEmpty()) {
                        ((Player) living).getInventory().removeItem(arrowStack);
                    }
                }
            }
        }
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
                                f * MAX_VELOCITY, 1.0F);
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
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.2D + 0.2D);
                        }
                        if (ModEnchantments.level(itemStack, Enchantments.FLAME) > 0) {
                            abstractarrow.igniteForSeconds(20);
                        }
                        itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                        if (flag1 || (player.getAbilities().instabuild
                                && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW)))) {
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
