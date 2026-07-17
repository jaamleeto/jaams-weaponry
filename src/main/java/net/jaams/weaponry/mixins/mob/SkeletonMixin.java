package net.jaams.weaponry.mixins.mob;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.jaams.weaponry.loader.EquipmentModifierLoader;
import org.spongepowered.asm.mixin.Mixin;
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

    @SuppressWarnings("deprecation")
    @Inject(at = @At("HEAD"), method = "canFireProjectileWeapon(Lnet/minecraft/world/item/ProjectileWeaponItem;)Z", cancellable = true)
    public void canFireProjectileWeapon(ProjectileWeaponItem weaponItem, CallbackInfoReturnable<Boolean> callback) {
        if (weaponItem instanceof BowItem) {
            callback.setReturnValue(true);
        }
    }
}
