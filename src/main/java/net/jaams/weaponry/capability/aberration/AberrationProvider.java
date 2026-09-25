package net.jaams.weaponry.capability.aberration;

import java.util.Optional;

import net.jaams.weaponry.capability.ModAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class AberrationProvider {

	private AberrationProvider() {
	}

	public static Optional<IAberration> get(Entity entity) {
		return entity instanceof Player player ? Optional.of(player.getData(ModAttachments.ABERRATION)) : Optional.empty();
	}
}
