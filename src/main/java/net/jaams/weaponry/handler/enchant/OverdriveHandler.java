package net.jaams.weaponry.handler.enchant;

import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.init.ModEnchantments;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber
public class OverdriveHandler {

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        if (!EnchantmentsConfig.OVERDRIVE.get()) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        {
            int overdriveLevel = ModEnchantments.level(itemStack, ModEnchantments.OVERDRIVE);
            if (overdriveLevel > 0) {
                int maxConfigLevel = EnchantmentsConfig.OVERDRIVE_MAX_LEVEL.get();
                int effectiveLevel = Math.min(overdriveLevel, maxConfigLevel);
                double speedBonusPerLevel = EnchantmentsConfig.OVERDRIVE_SPEED_BONUS_PER_LEVEL.get();
                double speedBonus = speedBonusPerLevel * effectiveLevel;

                for (ItemAttributeModifiers.Entry entry : event.getDefaultModifiers().modifiers()) {
                    if (entry.attribute().is(Attributes.ATTACK_SPEED.getKey())) {
                        AttributeModifier oldMod = entry.modifier();
                        AttributeModifier newMod = new AttributeModifier(
                                oldMod.id(),
                                oldMod.amount() + speedBonus,
                                oldMod.operation());
                        event.replaceModifier(entry.attribute(), newMod, entry.slot());
                    }
                }
            }
        }
    }
}
