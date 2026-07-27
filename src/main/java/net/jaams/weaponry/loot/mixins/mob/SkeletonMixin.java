package net.jaams.weaponry.mixins.mob;

import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.jaams.weaponry.loader.EquipmentModifierLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonMixin {

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    private void modifyDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (EquipmentModifierLoader.INSTANCE.applySpawnEquipment(self, random)) {
            ci.cancel();
        }
    }

    @Shadow
    @Final
    private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    @Shadow
    @Final
    private MeleeAttackGoal meleeGoal;

    @SuppressWarnings("deprecation")
    @Inject(at = @At("HEAD"), method = "canFireProjectileWeapon(Lnet/minecraft/world/item/ProjectileWeaponItem;)Z", cancellable = true)
    public void canFireProjectileWeapon(ProjectileWeaponItem weaponItem, CallbackInfoReturnable<Boolean> callback) {
        if (weaponItem instanceof BowItem) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "reassessWeaponGoal()V", cancellable = true)
    private void reassessWeaponGoal(CallbackInfo callback) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        if (skeleton.level() != null && !skeleton.level().isClientSide) {
            ItemStack mainHandStack = skeleton.getItemInHand(InteractionHand.MAIN_HAND);
            skeleton.goalSelector.removeGoal(bowGoal);
            skeleton.goalSelector.removeGoal(meleeGoal);
            if (mainHandStack.getItem() instanceof BowItem) {
                int attackInterval = skeleton.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
                bowGoal.setMinAttackInterval(attackInterval);
                skeleton.goalSelector.addGoal(4, bowGoal);
                callback.cancel();
            } else {
                skeleton.goalSelector.addGoal(4, meleeGoal);
            }
        }
    }
}
