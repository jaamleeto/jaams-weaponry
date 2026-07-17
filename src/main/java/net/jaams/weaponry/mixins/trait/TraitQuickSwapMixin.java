package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.data.TraitModifierData.QuickSwapEntry;
import net.jaams.weaponry.handler.trait.QuickSwapHandler;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(ItemStack.class)
public class TraitQuickSwapMixin {

    private static final String[] SUFFIXES = { "_reverse", "_unfolded", "_folded" };

    
    

    @Unique
    private boolean isQuickSwapEnabled(ItemStack stack) {
        if (!TraitsConfig.QUICK_SWAP.get()) {
            return false;
        }
        
        
        
        
        return ModTraits.isQuickSwapItem(stack);
    }

    @Unique
    private boolean hasSuffixVariant(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null)
            return false;
        String path = key.getPath();
        String ns = key.getNamespace();

        for (String suffix : SUFFIXES) {
            if (path.endsWith(suffix)) {
                
                
                String basePath = path.substring(0, path.length() - suffix.length());
                if (isValidTarget(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ns, basePath)))) {
                    return true;
                }
            } else {
                
                if (isValidTarget(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ns, path + suffix)))) {
                    return true;
                }
            }
        }
        return false;
    }

    
    

    @Unique
    private QuickSwapEntry getJsonEntry(ItemStack stack) {
        return TraitModifierData.getQuickSwap(stack).orElse(null);
    }

    

    @Unique
    private ModEnums.QuickSwapMode getQuickSwapMode(ItemStack stack) {
        
        String nbtMode = getStringNBT(stack, "QuickSwapMode");
        if (nbtMode != null && !nbtMode.isEmpty()) {
            try {
                return ModEnums.QuickSwapMode.valueOf(nbtMode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        
        QuickSwapEntry json = getJsonEntry(stack);
        if (json != null && json.activation_mode != null && !json.activation_mode.isEmpty()) {
            try {
                return ModEnums.QuickSwapMode.valueOf(json.activation_mode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        
        return TraitsConfig.QUICK_SWAP_ACTIVATION_MODE.get();
    }

    

    @Unique
    private int getUseDurationTicks(ItemStack stack) {
        
        Integer nbt = getIntNBT(stack, "QuickSwapUseDurationTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        
        QuickSwapEntry json = getJsonEntry(stack);
        if (json != null && json.use_duration_ticks != null && json.use_duration_ticks >= 0)
            return json.use_duration_ticks;
        
        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);
        
        
        if (mode == ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK) {
            return 1;
        }
        return TraitsConfig.QUICK_SWAP_CHARGE_DURATION_TICKS.get();
    }

    

    @Unique
    private UseAnim getCurrentUseAnimation(ItemStack stack) {
        
        String nbtAnim = getStringNBT(stack, "QuickSwapUseAnimation");
        if (nbtAnim != null && !nbtAnim.isEmpty())
            return parseUseAnimation(nbtAnim);
        
        QuickSwapEntry json = getJsonEntry(stack);
        if (json != null && json.use_animation != null && !json.use_animation.isEmpty())
            return parseUseAnimation(json.use_animation);
        
        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);
        if (mode == ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK) {
            return UseAnim.NONE;
        }
        return TraitsConfig.QUICK_SWAP_CHARGE_ANIMATION.get();
    }

    @Unique
    private UseAnim parseUseAnimation(String anim) {
        if (anim == null || anim.isEmpty())
            return UseAnim.NONE;
        return switch (anim.toUpperCase(Locale.ROOT).trim()) {
            case "BOW" -> UseAnim.BOW;
            case "CROSSBOW" -> UseAnim.CROSSBOW;
            case "SPEAR" -> UseAnim.SPEAR;
            case "NONE" -> UseAnim.NONE;
            case "EAT" -> UseAnim.EAT;
            case "DRINK" -> UseAnim.DRINK;
            case "BLOCK" -> UseAnim.BLOCK;
            case "SPYGLASS" -> UseAnim.SPYGLASS;
            default -> UseAnim.NONE;
        };
    }

    
    

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaam$onQuickSwapUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickSwapEnabled(stack))
            return;
        if (player == null || (TraitsConfig.QUICK_SWAP_REQUIRE_CROUCH.get() && !player.isShiftKeyDown()))
            return;

        Item targetItem = resolveTargetItem(stack);
        if (targetItem == null)
            return;

        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);

        if (mode == ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK) {
            if (!level.isClientSide()) {
                
                
                performSwap(level, player, null, stack, targetItem);
                
                
                cir.setReturnValue(InteractionResultHolder.consume(player.getItemInHand(hand)));
            } else {
                cir.setReturnValue(InteractionResultHolder.consume(stack));
            }
            return;
        }

        
        
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    
    

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaam$modifyQuickSwapUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickSwapEnabled(stack))
            return;
        
        
        
        
        if (getQuickSwapMode(stack) == ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK)
            return;
        cir.setReturnValue(getUseDurationTicks(stack));
    }

    
    

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaam$changeQuickSwapUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickSwapEnabled(stack))
            return;
        
        
        
        
        if (getQuickSwapMode(stack) == ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK)
            return;
        cir.setReturnValue(getCurrentUseAnimation(stack));
    }

    
    

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaam$onReleaseQuickSwap(Level level, LivingEntity entity, int durationUsed, CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickSwapEnabled(stack))
            return;
        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);
        if (mode != ModEnums.QuickSwapMode.CHARGE_AND_RELEASE
                && mode != ModEnums.QuickSwapMode.CHARGE_RELEASE_AND_FINISH)
            return;
        Item targetItem = resolveTargetItem(stack);
        if (targetItem == null)
            return;
        if (!level.isClientSide()) {
            performSwap(level, player, player.getUsedItemHand(), stack, targetItem);
        }
        ci.cancel();
    }

    

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaam$onFinishQuickSwap(Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        if (!(entity instanceof Player player))
            return;
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickSwapEnabled(stack))
            return;
        ModEnums.QuickSwapMode mode = getQuickSwapMode(stack);
        if (mode != ModEnums.QuickSwapMode.CHARGE_AND_FINISH_USING
                && mode != ModEnums.QuickSwapMode.CHARGE_RELEASE_AND_FINISH)
            return;
        Item targetItem = resolveTargetItem(stack);
        if (targetItem == null) {
            cir.setReturnValue(stack);
            return;
        }
        if (!level.isClientSide()) {
            performSwap(level, player, player.getUsedItemHand(), stack, targetItem);
        }
        
        
        cir.setReturnValue(player.getItemInHand(player.getUsedItemHand()));
    }

    
    

    @Unique
    private void performSwap(Level level, Player player, InteractionHand hand, ItemStack stack, Item targetItem) {
        
        if (!isValidTarget(targetItem)) {
            return;
        }
        int mainHandCooldown = getMainHandCooldown(stack);
        int offHandCooldown = getOffHandCooldown(stack);
        String soundEvent = getSoundEvent(stack);

        
        if (!hasExplicitSound(stack)) {
            ResourceLocation stackKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
            ResourceLocation targetKey = ForgeRegistries.ITEMS.getKey(targetItem);
            if (stackKey != null && targetKey != null) {
                String stackPath = stackKey.getPath().toLowerCase(Locale.ROOT);
                String targetPath = targetKey.getPath().toLowerCase(Locale.ROOT);
                if (stackPath.contains("tessen")) {
                    soundEvent = "jaams_weaponry:tessen_closed";
                } else if (targetPath.contains("tessen")) {
                    soundEvent = "jaams_weaponry:tessen_open";
                }
            }
        }

        List<Item> noCooldownItems = getNoCooldownItems(stack);
        List<ResourceLocation> noCooldownTags = getNoCooldownTags(stack);

        QuickSwapHandler.switchItem(level, player.getX(), player.getY(), player.getZ(), player,
                stack.getItem(), targetItem, mainHandCooldown, offHandCooldown, soundEvent,
                noCooldownItems, noCooldownTags, hand);
    }

    
    

    @Unique
    private Item resolveTargetItem(ItemStack stack) {
        Item target = getNbtTarget(stack);
        if (isValidTarget(target))
            return target;

        target = getJsonTarget(stack);
        if (isValidTarget(target))
            return target;

        target = resolveBySuffix(stack);
        if (isValidTarget(target))
            return target;

        return null;
    }

    @Unique
    private boolean isValidTarget(Item item) {
        return item != null && item != net.minecraft.world.item.Items.AIR;
    }

    @Unique
    private Item getNbtTarget(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapTargetItem")) {
            ResourceLocation loc = ResourceLocation.tryParse(tag.getString("QuickSwapTargetItem"));
            if (loc != null) {
                Item item = ForgeRegistries.ITEMS.getValue(loc);
                if (isValidTarget(item))
                    return item;
            }
        }
        return null;
    }

    @Unique
    private Item getJsonTarget(ItemStack stack) {
        return TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.target_item)
                .filter(java.util.Objects::nonNull)
                .map((id) -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id)))
                .filter(this::isValidTarget)
                .orElse(null);
    }

    @Unique
    private Item resolveBySuffix(ItemStack stack) {
        ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (registryKey == null)
            return null;

        String namespace = registryKey.getNamespace();
        String path = registryKey.getPath();

        
        for (String suffix : SUFFIXES) {
            if (path.endsWith(suffix)) {
                String basePath = path.substring(0, path.length() - suffix.length());
                Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, basePath));
                if (isValidTarget(baseItem))
                    return baseItem;
            }
        }

        
        for (String suffix : SUFFIXES) {
            String candidatePath = path + suffix;
            Item candidateItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, candidatePath));
            if (isValidTarget(candidateItem))
                return candidateItem;
        }

        return null;
    }

    
    

    @Unique
    private int getMainHandCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapMainHandCooldown")) {
            return Math.max(0, tag.getInt("QuickSwapMainHandCooldown"));
        }
        return TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.main_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_MAIN_HAND_COOLDOWN.get());
    }

    @Unique
    private int getOffHandCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapOffHandCooldown")) {
            return Math.max(0, tag.getInt("QuickSwapOffHandCooldown"));
        }
        return TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.off_hand_cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_SWAP_OFF_HAND_COOLDOWN.get());
    }

    @Unique
    private boolean hasExplicitSound(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapSound")) {
            return true;
        }
        return TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.sound)
                .filter(java.util.Objects::nonNull)
                .isPresent();
    }

    @Unique
    private String getSoundEvent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        
        if (tag != null && tag.contains("QuickSwapSound")) {
            return tag.getString("QuickSwapSound");
        }
        
        String jsonSound = TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.sound)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonSound != null) {
            return jsonSound;
        }
        
        ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (registryKey != null) {
            String path = registryKey.getPath().toLowerCase(java.util.Locale.ROOT);
            if (path.contains("wooden") || path.contains("stone")) {
                return "jaams_weaponry:switch_alt";
            }
        }
        
        return "jaams_weaponry:switch";
    }

    @Unique
    private List<Item> getNoCooldownItems(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapNoCooldownItems")) {
            String raw = tag.getString("QuickSwapNoCooldownItems");
            if (!raw.isEmpty()) {
                List<Item> items = new ArrayList<>();
                for (String id : raw.split(",")) {
                    ResourceLocation loc = ResourceLocation.tryParse(id.trim());
                    if (loc != null) {
                        Item item = ForgeRegistries.ITEMS.getValue(loc);
                        if (item != null)
                            items.add(item);
                    }
                }
                return items;
            }
        }
        List<String> jsonItems = TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.no_cooldown_items)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonItems != null && !jsonItems.isEmpty()) {
            List<Item> items = new ArrayList<>();
            for (String id : jsonItems) {
                ResourceLocation loc = ResourceLocation.tryParse(id);
                if (loc != null) {
                    Item item = ForgeRegistries.ITEMS.getValue(loc);
                    if (item != null)
                        items.add(item);
                }
            }
            return items;
        }
        return null;
    }

    @Unique
    private List<ResourceLocation> getNoCooldownTags(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickSwapNoCooldownTags")) {
            String raw = tag.getString("QuickSwapNoCooldownTags");
            if (!raw.isEmpty()) {
                List<ResourceLocation> tags = new ArrayList<>();
                for (String id : raw.split(",")) {
                    ResourceLocation loc = ResourceLocation.tryParse(id.trim());
                    if (loc != null)
                        tags.add(loc);
                }
                return tags;
            }
        }
        List<String> jsonTags = TraitModifierData.getQuickSwap(stack)
                .map((entry) -> entry.no_cooldown_tags)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonTags != null && !jsonTags.isEmpty()) {
            List<ResourceLocation> tags = new ArrayList<>();
            for (String id : jsonTags) {
                ResourceLocation loc = ResourceLocation.tryParse(id);
                if (loc != null)
                    tags.add(loc);
            }
            return tags;
        }
        return null;
    }

    
    

    @Unique
    private Integer getIntNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_INT)) ? tag.getInt(key) : null;
    }

    @Unique
    private String getStringNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_STRING)) ? tag.getString(key) : null;
    }
}
