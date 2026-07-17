package net.jaams.weaponry.handler.event;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.packet.AmountPacket;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.JaamsWeaponryMod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageEventsHandler {
	@SubscribeEvent
	public static void onLivingDamageHurt(LivingHurtEvent event) {
		if (event.getSource().getEntity() instanceof Player player) {
			float damage = event.getAmount();
			player.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				amount.setDamage(damage);
				if (player instanceof ServerPlayer serverPlayer) {
					JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new AmountPacket(player.getId(), damage));
				}
			});
		} else if (event.getSource().getEntity() instanceof Mob mob) {
			float damage = event.getAmount();
			mob.getCapability(AmountProvider.AMOUNT).ifPresent(amount -> {
				amount.setDamage(damage);
			});
		}
	}
}
