package net.jaams.weaponry.compat;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PlayerAnimatorCompat {

    
    public static boolean isPlayerAnimatorActive(Player player) {
        if (player == null || !player.level().isClientSide)
            return false;
        if (!(player instanceof AbstractClientPlayer))
            return false;

        try {
            var layer = PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayer) player);
            return layer != null && layer.isActive();
        } catch (Throwable t) {
            return false;
        }
    }
}
