package net.jaams.weaponry.mixins.mob;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.entity.projectile.Arrow;



import net.jaams.weaponry.loader.EquipmentModifierLoader;
import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;

@Mixin(Pillager.class)
public abstract class PillagerMixin {

    
    
    

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    private void modifyDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (EquipmentModifierLoader.INSTANCE.applySpawnEquipment(self, random)) {
            ci.cancel();
        }
    }

    
    
    

    @SuppressWarnings("deprecation")
    @Inject(at = @At("HEAD"), method = "canFireProjectileWeapon(Lnet/minecraft/world/item/ProjectileWeaponItem;)Z", cancellable = true)
    public void canFireProjectileWeapon(ProjectileWeaponItem weaponItem, CallbackInfoReturnable<Boolean> callback) {
        if (weaponItem instanceof CrossbowItem) {
            callback.setReturnValue(true);
        }
    }

    
    
    

    @Inject(at = @At("HEAD"), method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V", cancellable = true)
    public void performRangedAttack(LivingEntity target, float velocity, CallbackInfo callback) {
        Pillager pillager = (Pillager) (Object) this;
        ItemStack mainHandItem = pillager.getMainHandItem();

        if (mainHandItem.getItem() instanceof CrossbowItem crossbow) {
            crossbow.performShooting(pillager.level(), pillager,
                    InteractionHand.MAIN_HAND, mainHandItem, velocity, 1.0F, target);
            pillager.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F,
                    1.0F / (pillager.getRandom().nextFloat() * 0.4F + 0.8F));
            pillager.swing(InteractionHand.MAIN_HAND);
            callback.cancel();
        }
    }


}
