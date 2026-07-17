package net.jaams.weaponry.mixins.item;

import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.stats.Stats;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;


@Mixin(ItemStack.class)
public abstract class SlingshotItemMixin {





    @Unique
    private boolean jaam$isSlingshot() {
        return ModCompats.isSlingshot((ItemStack) (Object) this);
    }


    @Unique
    private List<String> jaam$getAmmoItems() {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.hasTag() && stack.getTag().contains("SlingshotAmmoItems", 9)) {
            var tag = stack.getTag().getList("SlingshotAmmoItems", 8);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < tag.size(); i++) {
                list.add(tag.getString(i));
            }
            if (!list.isEmpty())
                return list;
        }

        net.jaams.weaponry.data.RangedItemData.AmmoEntry ammoData = net.jaams.weaponry.data.RangedItemData.getAmmoData(stack);
        if (ammoData != null && ammoData.ammo_items != null && !ammoData.ammo_items.isEmpty()) {
            return new ArrayList<>(ammoData.ammo_items);
        }

        List<? extends String> config = ItemFeaturesConfig.SLINGSHOT_AMMO_ITEMS.get();
        return List.copyOf(config);
    }


    @Unique
    private boolean jaam$isAmmoItem(ItemStack ammo) {
        if (ammo.isEmpty())
            return false;
        List<String> items = jaam$getAmmoItems();
        if (items.isEmpty())
            return true;
        ResourceLocation ammoId = ForgeRegistries.ITEMS.getKey(ammo.getItem());
        if (ammoId == null)
            return false;
        return items.contains(ammoId.toString());
    }


    @Unique
    private List<String> jaam$getPlaceableItems() {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.hasTag() && stack.getTag().contains("SlingshotPlaceableItems", 9)) {
            var tag = stack.getTag().getList("SlingshotPlaceableItems", 8);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < tag.size(); i++) {
                list.add(tag.getString(i));
            }
            if (!list.isEmpty())
                return list;
        }

        List<? extends String> config = ItemFeaturesConfig.SLINGSHOT_PLACEABLE_ITEMS.get();
        if (!config.isEmpty())
            return List.copyOf(config);

        return jaam$getAmmoItems();
    }

    @Unique
    private float jaam$getBaseDamage() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotBaseDamage", () -> 1.0);
    }

    @Unique
    private float jaam$getPowerDamageBonus() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotPowerDamageBonus", () -> 2.0);
    }

    @Unique
    private float jaam$getMinSpeed() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotMinSpeed", () -> 0.5);
    }

    @Unique
    private float jaam$getMaxSpeed() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotMaxSpeed", () -> 2.5);
    }

    @Unique
    private int jaam$getMaxDrawDuration() {
        return ModUtils.getConfigOrNbtInt((ItemStack) (Object) this, "SlingshotMaxDrawDuration", () -> 20);
    }

    @Unique
    private int jaam$getMinDrawTicks() {
        return ModUtils.getConfigOrNbtInt((ItemStack) (Object) this, "SlingshotMinDrawTicks", () -> 5);
    }

    @Unique
    private float jaam$getMultishotSpread() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotMultishotSpread", () -> 10.0);
    }

    @Unique
    private ItemStack jaam$getDefaultCreativeAmmo() {

        List<String> items = jaam$getAmmoItems();
        for (String id : items) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item != null && item != Items.AIR) {
                return new ItemStack(item);
            }
        }

        String id = ModUtils.getConfigOrNbtString((ItemStack) (Object) this, "SlingshotDefaultCreativeAmmo",
                () -> "minecraft:cobblestone");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item != null && item != Items.AIR) {
            return new ItemStack(item);
        }
        return new ItemStack(Items.COBBLESTONE);
    }

    @Unique
    private boolean jaam$getDefaultCanPlaceBlock() {




        if (!jaam$getPlaceableItems().isEmpty()) {
            return true;
        }
        return ModUtils.getConfigOrNbtBoolean((ItemStack) (Object) this, "SlingshotCanPlaceBlock", () -> false);
    }

    @Unique
    private boolean jaam$getAmmoFromInventory() {
        return ModUtils.getConfigOrNbtBoolean((ItemStack) (Object) this, "SlingshotAmmoFromInventory",
                ItemFeaturesConfig.SLINGSHOT_AMMO_FROM_INVENTORY::get);
    }

    @Unique
    private float jaam$getBaseKnockback() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotBaseKnockback", () -> 0.5);
    }

    @Unique
    private float jaam$getPowerEnchantDamagePerLevel() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotPowerEnchantDamagePerLevel",
                () -> 0.5);
    }

    @Unique
    private float jaam$getPunchEnchantKnockbackPerLevel() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this,
                "SlingshotPunchEnchantKnockbackPerLevel", () -> 0.3);
    }

    @Unique
    private float jaam$getForwardOffset() {
        return (float) ModUtils.getConfigOrNbtDouble((ItemStack) (Object) this, "SlingshotForwardOffset", () -> 1.0);
    }

    @Unique
    private int jaam$getFlameFireSeconds() {
        return ModUtils.getConfigOrNbtInt((ItemStack) (Object) this, "SlingshotFlameFireSeconds", () -> 60);
    }





    @Unique
    private float jaam$getPowerForTime(int drawTicks) {
        int max = jaam$getMaxDrawDuration();
        float f = (float) drawTicks / max;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }

    @Unique
    private float jaam$calcProjectileSpeed(float power) {
        float min = jaam$getMinSpeed();
        float max = jaam$getMaxSpeed();
        float range = max - min;
        float scaled = power * power;
        return min + range * scaled;
    }





    @Unique
    private ItemStack jaam$findAmmo(Player player, InteractionHand hand) {
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
        ItemStack handItem = player.getItemInHand(hand);
        ItemStack otherItem = player.getItemInHand(other);

        if (jaam$isAmmoItem(otherItem) && otherItem != handItem) {
            return otherItem;
        }
        if (jaam$isAmmoItem(handItem) && handItem != player.getUseItem()) {
            return handItem;
        }
        if (jaam$getAmmoFromInventory()) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (jaam$isAmmoItem(stack)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }





    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaam$slingshotUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!jaam$isSlingshot())
            return;
        boolean flag = jaam$findAmmo(player, hand).isEmpty()
                && !player.getAbilities().instabuild;
        if (flag) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
        } else {
            player.startUsingItem(hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        }
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaam$slingshotReleaseUsing(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        if (!jaam$isSlingshot())
            return;
        if (entity instanceof Player player) {
            jaam$fireSlingshot((ItemStack) (Object) this, level, player, timeLeft);
        }
        ci.cancel();
    }

    @Inject(method = "onUseTick", at = @At("HEAD"), cancellable = true)
    private void jaam$slingshotOnUseTick(Level level, LivingEntity entity, int count, CallbackInfo ci) {
        if (!jaam$isSlingshot())
            return;
        if (!level.isClientSide && entity instanceof Player player) {
            int drawn = jaam$getUseDuration() - count;
            if (drawn == jaam$getMinDrawTicks()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.SLINGSHOT_LOAD.get(), SoundSource.PLAYERS, 0.5F,
                        0.8F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaam$slingshotGetUseDuration(CallbackInfoReturnable<Integer> cir) {
        if (jaam$isSlingshot()) {
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaam$slingshotGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        if (jaam$isSlingshot()) {
            cir.setReturnValue(UseAnim.BOW);
        }
    }

    @Unique
    private int jaam$getUseDuration() {
        return 72000;
    }





    @Unique
    private void jaam$fireSlingshot(ItemStack stack, Level level, Player player, int timeLeft) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack ammo = jaam$findAmmo(player, hand);
        boolean creative = player.getAbilities().instabuild;

        if (ammo.isEmpty() && creative) {
            ammo = jaam$getDefaultCreativeAmmo();
        }
        if (ammo.isEmpty())
            return;

        int drawn = 72000 - timeLeft;
        drawn = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, level, player, drawn, !ammo.isEmpty());
        int minTicks = jaam$getMinDrawTicks();
        if (drawn < minTicks)
            return;

        float power = jaam$getPowerForTime(drawn);
        if (power < 0.1F)
            return;

        boolean infinity = stack.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
        boolean multishot = stack.getEnchantmentLevel(Enchantments.MULTISHOT) > 0;

        int count = creative || infinity ? 1 : ammo.getCount();
        if (count < 1)
            return;

        int projCount = multishot ? 3 : 1;
        float[] pitches = ModUtils.generateShotPitches(level.getRandom(), projCount);


        List<String> placeableItems = jaam$getPlaceableItems();

        for (int i = 0; i < projCount; i++) {
            ItemProjectileEntity projectile = new ItemProjectileEntity(level, player, stack);

            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            if (ammo != player.getItemInHand(InteractionHand.MAIN_HAND)
                    && ammo != player.getItemInHand(InteractionHand.OFF_HAND)) {
                slot = player.getInventory().findSlotMatchingItem(ammo);
            }
            projectile.getPersistentData().putInt("OriginalSlotIndex", slot);
            projectile.getPersistentData().putBoolean("ChargedForCrit", power >= 1.0F);

            if (ModUtils.isProjectileCritical(player, power)) {
                projectile.setCritical(true);
            }

            projectile.setProjectileItem(ammo.copy());




            projectile.getPersistentData().putBoolean("SlingshotProjectile", true);


            boolean canPlaceBlock;
            if (multishot) {
                canPlaceBlock = creative || i == 0;
                projectile.getPersistentData().putBoolean("CanPlaceBlock",
                        ModUtils.getConfigOrNbtBoolean((ItemStack) (Object) this, "SlingshotMultishotCanPlaceBlock",
                                () -> canPlaceBlock));
                projectile.pickup = creative ? AbstractArrow.Pickup.CREATIVE_ONLY
                        : (i == 0 ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED);
                if (!creative && i != 0) {
                    projectile.getPersistentData().putBoolean("BreakOnBlockHit", true);
                }
                if (!placeableItems.isEmpty() && canPlaceBlock) {
                    projectile.getPersistentData().putString("ProjectilePlaceableBlocks",
                            String.join(",", placeableItems));
                }
            } else {
                canPlaceBlock = jaam$getDefaultCanPlaceBlock();
                projectile.getPersistentData().putBoolean("CanPlaceBlock", canPlaceBlock);
                projectile.pickup = creative ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
                if (!placeableItems.isEmpty() && canPlaceBlock) {
                    projectile.getPersistentData().putString("ProjectilePlaceableBlocks",
                            String.join(",", placeableItems));
                }
            }



            float damage = jaam$getBaseDamage();
            if (power >= 1.0F) {
                damage += jaam$getPowerDamageBonus();
            }
            int pwr = stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
            if (pwr > 0) {
                damage += pwr * jaam$getPowerEnchantDamagePerLevel() + 0.5F;
            }
            projectile.setProjectileDamage(damage);

            float kb = jaam$getBaseKnockback();
            int punch = stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
            if (punch > 0) {
                kb += punch * jaam$getPunchEnchantKnockbackPerLevel();
            }
            projectile.setProjectileKnockback(kb);

            int flame = stack.getEnchantmentLevel(Enchantments.FLAMING_ARROWS);
            if (flame > 0) {
                projectile.setSecondsOnFire(jaam$getFlameFireSeconds());
            }

            int pierce = stack.getEnchantmentLevel(Enchantments.PIERCING);
            if (pierce > 0) {
                projectile.setPiercingLevel(pierce);
            }

            float speed = jaam$calcProjectileSpeed(power);


            if (multishot) {
                float spread = jaam$getMultishotSpread();
                float rot = (i == 0) ? 0.0F : (i == 1) ? -spread : spread;
                Vec3 up = player.getUpVector(1.0F);
                Quaternionf q = new Quaternionf().setAngleAxis(rot * (float) Math.PI / 180F, up.x, up.y, up.z);
                Vec3 view = player.getViewVector(1.0F);
                Vector3f dir = view.toVector3f().rotate(q);

                if (i == 0) {
                    float offset = jaam$getForwardOffset();
                    Vec3 off = view.scale(offset);
                    projectile.setPos(player.getX() + off.x, player.getEyeY() + off.y, player.getZ() + off.z);
                }
                projectile.shoot(dir.x(), dir.y(), dir.z(), speed, 1.0F);
            } else {
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, 1.0F);
            }

            level.addFreshEntity(projectile);
            level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(),
                    ModSounds.SLINGSHOT_SHOOT.get(), SoundSource.PLAYERS, 1.0F, pitches[i]);
        }


        if (!creative && !infinity) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                int s = player.getInventory().findSlotMatchingItem(ammo);
                if (s >= 0) {
                    player.getInventory().setItem(s, ItemStack.EMPTY);
                } else if (ammo == player.getItemInHand(InteractionHand.MAIN_HAND)) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                } else if (ammo == player.getItemInHand(InteractionHand.OFF_HAND)) {
                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }

        if (!creative) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        player.swing(hand, true);
    }
}
