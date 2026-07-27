
package net.jaams.weaponry.item;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.common.EffectsConfig;

import java.util.List;

public class ArchersBottleItem extends Item {
	public ArchersBottleItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, net.minecraft.world.entity.LivingEntity entityLiving) {
		if (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleUseDuration")) {
			return ModComponents.get(itemstack).getInt("BottleUseDuration");
		}
		return ItemFeaturesConfig.ARCHERS_BOTTLE_USE_DURATION.get();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
		return false;
	}

	
	@Override
	public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
		return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		if (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleIsFoil")) {
			return ModComponents.get(itemstack).getBoolean("BottleIsFoil");
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, net.minecraft.world.item.Item.TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		int durationTicks = (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleEffectDuration")) ? ModComponents.get(itemstack).getInt("BottleEffectDuration") : ItemFeaturesConfig.ARCHERS_BOTTLE_EFFECT_DURATION.get();
		int amp = (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleEffectAmplifier")) ? ModComponents.get(itemstack).getInt("BottleEffectAmplifier") : ItemFeaturesConfig.ARCHERS_BOTTLE_EFFECT_AMPLIFIER.get();
		int seconds = durationTicks / 20;
		String timeFormated = String.format("%02d:%02d", seconds / 60, seconds % 60);
		String romanValue = amp == 0 ? "I" : amp == 1 ? "II" : amp == 2 ? "III" : String.valueOf(amp + 1);
		if (EffectsConfig.ARCHERS_GRACE.get()) {
			Component effectName = ModMobEffects.ARCHERS_GRACE.get().getDisplayName().copy().withStyle(ChatFormatting.BLUE);
			list.add(Component.empty().append(effectName).append(Component.literal(" " + romanValue + " (" + timeFormated + ")").withStyle(ChatFormatting.BLUE)));
		} else {
			Component effectName = MobEffects.DAMAGE_BOOST.value().getDisplayName().copy().withStyle(ChatFormatting.BLUE);
			list.add(Component.empty().append(effectName).append(Component.literal(" " + romanValue + " (" + timeFormated + ")").withStyle(ChatFormatting.BLUE)));
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		if (entity instanceof Player player && itemstack.getItem() == ModItems.ARCHERS_BOTTLE.get()) {
			handleArchersBottleFinish(player, itemstack, world);
		}
		return retval;
	}

	private void handleArchersBottleFinish(Player player, ItemStack itemstack, Level world) {
		if (!world.isClientSide()) {
			ServerLevel serverWorld = (ServerLevel) world;
			if (!player.isCreative()) {
				double xOffset = player.getX() + player.getLookAngle().x * 0.5;
				double yOffset = player.getY() + player.getEyeHeight();
				double zOffset = player.getZ() + player.getLookAngle().z * 0.5;
				serverWorld.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemstack), xOffset, yOffset, zOffset, 5, 0.1, 0.1, 0.1, 0.05);
			}
			int duration = (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleEffectDuration")) ? ModComponents.get(itemstack).getInt("BottleEffectDuration") : ItemFeaturesConfig.ARCHERS_BOTTLE_EFFECT_DURATION.get();
			int amplifier = (ModComponents.has(itemstack) && ModComponents.get(itemstack).contains("BottleEffectAmplifier")) ? ModComponents.get(itemstack).getInt("BottleEffectAmplifier") : ItemFeaturesConfig.ARCHERS_BOTTLE_EFFECT_AMPLIFIER.get();
			if (EffectsConfig.ARCHERS_GRACE.get()) {
				player.addEffect(new MobEffectInstance(ModMobEffects.ARCHERS_GRACE, duration, amplifier));
			} else {
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amplifier));
			}
			world.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("jaams_weaponry:bottle_break")), SoundSource.PLAYERS, 1, 1);
		}
		if (!player.isCreative()) {
			itemstack.shrink(1);
		}
	}
}
