package net.jaams.weaponry.mixins.access;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Exposes the private skin/overlay parts of {@link PlayerModel} (jacket, sleeves, pants). The hat is
 * already public on {@code HumanoidModel} and accessed directly. These overlay parts are siblings of
 * their main bones, so their transforms must be synchronised manually when the main bone is animated.
 */
@Mixin(PlayerModel.class)
public interface PlayerModelOverlayAccessorMixin {
    @Accessor("jacket")
    ModelPart jaams$getJacket();

    @Accessor("rightSleeve")
    ModelPart jaams$getRightSleeve();

    @Accessor("leftSleeve")
    ModelPart jaams$getLeftSleeve();

    @Accessor("rightPants")
    ModelPart jaams$getRightPants();

    @Accessor("leftPants")
    ModelPart jaams$getLeftPants();
}
