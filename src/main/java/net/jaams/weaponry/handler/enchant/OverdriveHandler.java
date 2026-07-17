package net.jaams.weaponry.handler.enchant;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OverdriveHandler {

    private static final Map<String, UUID> ATTRIBUTE_UUID_MAP = new ConcurrentHashMap<>();
    public static final String OVERDRIVE_ATTACK_SPEED = "Overdrive Attack Speed";

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        if (!EnchantmentsConfig.OVERDRIVE.get()) {
            return;
        }
        EquipmentSlot slot = event.getSlotType();
        ItemStack itemStack = event.getItemStack();
        if (slot == EquipmentSlot.MAINHAND) {
            int overdriveLevel = itemStack.getEnchantmentLevel(ModEnchantments.OVERDRIVE.get());
            if (overdriveLevel > 0 && hasBaseAttackSpeed(event)) {
                int maxConfigLevel = EnchantmentsConfig.OVERDRIVE_MAX_LEVEL.get();
                int effectiveLevel = Math.min(overdriveLevel, maxConfigLevel);
                double speedBonusPerLevel = EnchantmentsConfig.OVERDRIVE_SPEED_BONUS_PER_LEVEL.get();
                double speedBonus = speedBonusPerLevel * effectiveLevel;
                AttributeEntry entry = createEntry(OVERDRIVE_ATTACK_SPEED, Attributes.ATTACK_SPEED, speedBonus, AttributeModifier.Operation.ADDITION);
                event.addModifier(entry.attribute, entry.modifier);
            }
        }
    }

    private static boolean hasBaseAttackSpeed(ItemAttributeModifierEvent event) {
        Multimap<Attribute, AttributeModifier> originalModifiers = event.getOriginalModifiers();
        return originalModifiers.containsKey(Attributes.ATTACK_SPEED);
    }

    private static AttributeEntry createEntry(String name, Attribute attribute, double value, AttributeModifier.Operation operation) {
        UUID uuid = ATTRIBUTE_UUID_MAP.computeIfAbsent(name, (key) -> UUID.randomUUID());
        AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
        return new AttributeEntry(attribute, modifier);
    }

    private static class AttributeEntry {

        final Attribute attribute;
        final AttributeModifier modifier;

        AttributeEntry(Attribute attribute, AttributeModifier modifier) {
            this.attribute = attribute;
            this.modifier = modifier;
        }
    }
}
