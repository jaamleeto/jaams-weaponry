package net.jaams.weaponry.handler.projectile;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.FakePlayer;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.entity.ItemProjectileEntity;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class ProjectilePlacementHandler {
    private final ItemProjectileEntity projectile;

    public ProjectilePlacementHandler(ItemProjectileEntity projectile) {
        this.projectile = projectile;
    }

    public void onHitBlock(BlockHitResult result, ItemStack stack) {
        if (projectile.level().isClientSide)
            return;
        Item item = stack.getItem();
        if (isBucket(stack)) {
            handleBucketImpact(result, stack);
        } else if (item instanceof BlockItem) {
            if (checkPlaceableMode(stack)) {
                handleBlockPlacement(result, stack);
            }
        }
    }

    
    private boolean checkPlaceableMode(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }
        if (stack.getItem() instanceof SolidBucketItem || isBucket(stack)) {
            return true;
        }
        List<String> rules = getPlaceableRules();
        if (!rules.isEmpty()) {
            Block block = blockItem.getBlock();
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            boolean hasAny = rules.stream().anyMatch(r -> r.equalsIgnoreCase("any"));
            if (hasAny) {
                for (String rule : rules) {
                    if (isNegatedRule(rule) && matchesRule(stripNegation(rule), block, blockId, stack)) {
                        return false;
                    }
                }
                return true;
            } else {
                for (String rule : rules) {
                    if (isNegatedRule(rule)) {
                        if (matchesRule(stripNegation(rule), block, blockId, stack)) {
                            return false;
                        }
                    } else {
                        if (matchesRule(rule, block, blockId, stack)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        
        CompoundTag persistent = projectile.getPersistentData();
        if (persistent.contains("CanPlaceBlock", Tag.TAG_BYTE)) {
            return persistent.getBoolean("CanPlaceBlock");
        }
        return false;
    }

    private boolean isNegatedRule(String rule) {
        return rule.trim().startsWith("!");
    }

    private String stripNegation(String rule) {
        String r = rule.trim();
        return r.startsWith("!") ? r.substring(1).trim() : r;
    }

    private List<String> getPlaceableRules() {
        CompoundTag persistent = projectile.getPersistentData();
        List<String> rules = getRulesFromTag(persistent);
        if (!rules.isEmpty()) {
            return rules;
        }
        ItemStack source = projectile.getSourceItem();
        if (!source.isEmpty() && source.getTag() != null) {
            rules = getRulesFromTag(source.getTag());
            if (!rules.isEmpty()) {
                return rules;
            }
        }
        return List.of();
    }

    private List<String> getRulesFromTag(CompoundTag tag) {
        if (tag == null)
            return List.of();
        if (tag.contains("ProjectilePlaceableBlocks", Tag.TAG_LIST)) {
            ListTag list = tag.getList("ProjectilePlaceableBlocks", Tag.TAG_STRING);
            List<String> rules = new ArrayList<>();
            for (Tag t : list) {
                rules.add(t.getAsString());
            }
            return rules;
        }
        if (tag.contains("ProjectilePlaceableBlocks", Tag.TAG_STRING)) {
            String value = tag.getString("ProjectilePlaceableBlocks").trim();
            if (value.isEmpty())
                return List.of();
            if (value.startsWith("[") && value.endsWith("]")) {
                value = value.substring(1, value.length() - 1);
            }
            return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        }
        return List.of();
    }

    
    private boolean matchesRule(String rule, Block block, ResourceLocation blockId, ItemStack stack) {
        String r = rule.trim();
        if (r.startsWith("#")) {
            String tagId = r.substring(1);
            ResourceLocation tagLocation = ResourceLocation.tryParse(tagId);
            if (tagLocation == null)
                return false;
            
            TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, tagLocation);
            if (block.defaultBlockState().is(blockTag))
                return true;
            
            if (!stack.isEmpty()) {
                TagKey<Item> itemTag = TagKey.create(Registries.ITEM, tagLocation);
                return stack.is(itemTag);
            }
            return false;
        } else {
            ResourceLocation ruleId = ResourceLocation.tryParse(r);
            return ruleId != null && ruleId.equals(blockId);
        }
    }

    private void handleBlockPlacement(BlockHitResult hit, ItemStack stack) {
        Entity owner = projectile.getOwner();
        if (owner instanceof Player player && !player.mayBuild())
            return;
        if (projectile.isMultishotClone())
            return;
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerLevel) projectile.level());
        setupFakePlayerForOwner(fakePlayer, owner);
        BlockPlaceContext context = new BlockPlaceContext(projectile.level(), fakePlayer, InteractionHand.MAIN_HAND,
                stack, hit);
        BlockItem blockItem = (BlockItem) stack.getItem();
        InteractionResult result = blockItem.place(context);
        if (result.consumesAction()) {
            if (stack.getItem() instanceof SolidBucketItem) {
                dropCorrespondingEmptyContainer(stack);
            }
            projectile.discard();
        }
    }

    private void handleBucketImpact(BlockHitResult result, ItemStack stack) {
        if (projectile.level().isClientSide)
            return;
        Entity owner = projectile.getOwner();
        if (owner instanceof Player player && !player.mayBuild())
            return;
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerLevel) projectile.level());
        setupFakePlayerForOwner(fakePlayer, owner);
        boolean success = placeBucketContent(result, stack, fakePlayer);
        if (success) {
            projectile.discard();
        }
    }

    private boolean placeBucketContent(BlockHitResult result, ItemStack stack, FakePlayer fakePlayer) {
        Level level = projectile.level();
        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        if (level.getBlockState(result.getBlockPos()).canBeReplaced()) {
            pos = result.getBlockPos();
        }
        boolean success = false;
        Item item = stack.getItem();
        if (item instanceof BucketItem bucketItem) {
            if (item instanceof MobBucketItem mobBucket) {
                mobBucket.checkExtraContent(fakePlayer, level, stack, pos);
            }
            success = bucketItem.emptyContents(fakePlayer, level, pos, result);
        } else if (item instanceof SolidBucketItem solidBucket) {
            success = placeSolidBucket(solidBucket, level, pos, result, fakePlayer);
        }
        if (success && !projectile.isMultishotClone()) {
            dropCorrespondingEmptyContainer(stack);
        }
        return success;
    }

    private boolean placeSolidBucket(SolidBucketItem solidBucket, Level level, BlockPos pos, BlockHitResult hitResult,
            FakePlayer fakePlayer) {
        BlockPlaceContext context = new BlockPlaceContext(level, fakePlayer, InteractionHand.MAIN_HAND,
                new ItemStack(solidBucket), hitResult);
        return solidBucket.place(context).consumesAction();
    }

    private void dropCorrespondingEmptyContainer(ItemStack filledStack) {
        Entity owner = projectile.getOwner();
        if (owner instanceof Player player && player.isCreative())
            return;
        ItemStack emptyStack = filledStack.getItem().getCraftingRemainingItem(filledStack);
        if (emptyStack.isEmpty()) {
            if (filledStack.is(Items.POWDER_SNOW_BUCKET)) {
                emptyStack = new ItemStack(Items.BUCKET);
            } else {
                emptyStack = getFallbackEmptyBucket(filledStack);
            }
        }
        if (!emptyStack.isEmpty()) {
            ItemEntity entity = new ItemEntity(projectile.level(), projectile.getX(), projectile.getY() + 0.3,
                    projectile.getZ(), emptyStack.copy());
            entity.setDeltaMovement(0, 0.2, 0);
            projectile.level().addFreshEntity(entity);
        }
    }

    private ItemStack getFallbackEmptyBucket(ItemStack filledStack) {
        String fullId = ModUtils.getItemIdLowercase(filledStack);
        String namespace = "minecraft";
        String path = fullId;
        if (fullId.contains(":")) {
            String[] parts = fullId.split(":", 2);
            namespace = parts[0];
            path = parts[1];
        }
        String basePath = path;
        String[] fluidTypes = { "water", "lava", "powder_snow", "milk" };
        for (String fluid : fluidTypes) {
            if (basePath.contains(fluid)) {
                basePath = basePath.replace(fluid, "").replace("__", "_").replaceAll("^_+", "").replaceAll("_+$", "");
                break;
            }
        }
        if (basePath.equals("bucket") || basePath.isEmpty() || basePath.contains("bucket")) {
            return new ItemStack(Items.BUCKET);
        }
        ResourceLocation loc = new ResourceLocation(namespace, basePath + "_bucket");
        Item item = ForgeRegistries.ITEMS.getValue(loc);
        if (item != null && item != Items.AIR) {
            return new ItemStack(item);
        }
        return new ItemStack(Items.BUCKET);
    }

    private void setupFakePlayerForOwner(FakePlayer fakePlayer, Entity owner) {
        if (owner instanceof Player realPlayer) {
            fakePlayer.setPos(realPlayer.getX(), realPlayer.getY(), realPlayer.getZ());
            fakePlayer.setYRot(realPlayer.getYRot());
            fakePlayer.setXRot(realPlayer.getXRot());
        } else if (owner != null) {
            fakePlayer.setPos(owner.getX(), owner.getY(), owner.getZ());
            fakePlayer.setYRot(owner.getYRot());
            fakePlayer.setXRot(owner.getXRot());
        }
        fakePlayer.getInventory().clearContent();
    }

    private boolean isBucket(ItemStack stack) {
        Item item = stack.getItem();
        String id = ModUtils.getItemIdLowercase(stack);
        return item instanceof BucketItem || item instanceof SolidBucketItem || id.contains("bucket");
    }
}
