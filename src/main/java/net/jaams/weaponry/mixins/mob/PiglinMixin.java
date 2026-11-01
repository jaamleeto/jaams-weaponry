package net.jaams.weaponry.mixins.mob;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;

@Mixin(Piglin.class)
public abstract class PiglinMixin {
    @SuppressWarnings("deprecation")
    @Inject(at = @At("HEAD"), method = "canFireProjectileWeapon(Lnet/minecraft/world/item/ProjectileWeaponItem;)Z", cancellable = true)
    public void canFireProjectileWeapon(ProjectileWeaponItem weaponItem, CallbackInfoReturnable<Boolean> callback) {
        if (weaponItem instanceof CrossbowItem) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "shootCrossbowProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/Projectile;F)V")
    private void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float velocity, CallbackInfo info) {
        if (crossbow.getItem() instanceof GreatCrossbowItem && projectile instanceof Arrow) {
            float damageMultiplier = 2.5F;
            Arrow arrow = (Arrow) projectile;
            arrow.setBaseDamage(arrow.getBaseDamage() * damageMultiplier);
        }
        if (crossbow.getItem() instanceof HuntersCrossbowItem && projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            arrow.setBaseDamage(Math.min(arrow.getBaseDamage(), 2.0));
        }
    }
}
