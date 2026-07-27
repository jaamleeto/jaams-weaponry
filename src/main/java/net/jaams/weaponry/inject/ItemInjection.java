package net.jaams.weaponry.inject;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public interface ItemInjection {
	default void initializeClient(Consumer<IClientItemExtensions> consumer) {
	}
}
