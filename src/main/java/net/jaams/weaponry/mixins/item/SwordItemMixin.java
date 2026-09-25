package net.jaams.weaponry.mixins.item;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

import net.jaams.weaponry.util.ModTags;

@Mixin(value = SwordItem.class, remap = true)
public abstract class SwordItemMixin {

    @Inject(method = "canAttackBlock", at = @At("RETURN"), cancellable = true)
    private void jaam$canAttackBlock(BlockState state, Level level, BlockPos pos, Player player,
            CallbackInfoReturnable<Boolean> cir) { 
        ItemStack stack = player.getMainHandItem();
        if (stack.is(ModTags.SCYTHES)) {
            if (player.isCreative()) {
                cir.setReturnValue(state.getBlock() instanceof CropBlock);
            } else {
                cir.setReturnValue(true);
            }
        }
        if (stack.is(ModTags.SICKLES)) {
            if (player.isCreative()) {
                cir.setReturnValue(state.getBlock() instanceof CropBlock
                        || state.is(BlockTags.LEAVES)
                        || state.is(BlockTags.FLOWERS)
                        || state.is(BlockTags.SAPLINGS));
            } else {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "canPerformAction", at = @At("RETURN"), cancellable = true, remap = false)
    private void jaam$canPerformAction(ItemStack stack, ItemAbility toolAction,
            CallbackInfoReturnable<Boolean> cir) { 
        
        if (stack.is(ModTags.MACHETES) || stack.is(ModTags.SCYTHES) || stack.is(ModTags.SICKLES)) {
            cir.setReturnValue(toolAction == ItemAbilities.HOE_DIG
                    || toolAction == ItemAbilities.SWORD_DIG
                    || toolAction == ItemAbilities.SWORD_SWEEP);
            return;
        }
        
        
        if (stack.is(ModTags.CLAWS) || stack.is(ModTags.DAGGERS) || stack.is(ModTags.REVERSE_DAGGERS)
                || stack.is(ModTags.HOOK_SWORDS) || stack.is(ModTags.KATARS) || stack.is(ModTags.KNUCKLES)
                || stack.is(ModTags.SPEARS)) {
            cir.setReturnValue(toolAction == ItemAbilities.SWORD_DIG);
        }
    }

}
