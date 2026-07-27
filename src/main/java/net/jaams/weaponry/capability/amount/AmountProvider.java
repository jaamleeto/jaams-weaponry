package net.jaams.weaponry.capability.amount;

import java.util.Optional;

import net.jaams.weaponry.capability.ModAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Attachment accessor keeping the old capability-style Optional API. */
public final class AmountProvider {

	private AmountProvider() {
	}

	public static Optional<IAmount> get(Entity entity) {
		return entity instanceof LivingEntity living ? Optional.of(living.getData(ModAttachments.AMOUNT)) : Optional.empty();
	}
}
