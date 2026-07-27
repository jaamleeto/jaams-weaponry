package net.jaams.weaponry.mixins.mob;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.util.RandomSource;

import net.jaams.weaponry.loader.EquipmentModifierLoader;
import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;

@Mixin(Piglin.class)
public abstract class PiglinMixin {
    @Inject(method = "createSpawnWeapon", at = @At("HEAD"), cancellable = true)
    private void modifyCreateSpawnWeapon(CallbackInfoReturnable<ItemStack> cir) {
        Piglin self = (Piglin) (Object) this;
        RandomSource random = self.getRandom();
        if (EquipmentModifierLoader.INSTANCE.applySpawnEquipment(self, random)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(at = @At("HEAD"), method = "canFireProjectileWeapon(Lnet/minecraft/world/item/ProjectileWeaponItem;)Z", cancellable = true)
    public void canFireProjectileWeapon(ProjectileWeaponItem weaponItem, CallbackInfoReturnable<Boolean> callback) {
        if (weaponItem instanceof CrossbowItem) {
            callback.setReturnValue(true);
        }
    }
}
