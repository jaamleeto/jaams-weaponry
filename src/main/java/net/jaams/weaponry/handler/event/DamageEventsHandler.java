package net.jaams.weaponry.handler.event;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.packet.AmountPacket;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.JaamsWeaponryMod;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class DamageEventsHandler {
	@SubscribeEvent
	public static void onLivingDamageHurt(LivingIncomingDamageEvent event) {
		if (event.getSource().getEntity() instanceof Player player) {
			float damage = event.getAmount();
			AmountProvider.get(player).ifPresent(amount -> {
				amount.setDamage(damage);
				if (player instanceof ServerPlayer serverPlayer) {
					PacketDistributor.sendToPlayer(serverPlayer, new AmountPacket(player.getId(), damage));
				}
			});
		} else if (event.getSource().getEntity() instanceof Mob mob) {
			float damage = event.getAmount();
			AmountProvider.get(mob).ifPresent(amount -> {
				amount.setDamage(damage);
			});
		}
	}
}
