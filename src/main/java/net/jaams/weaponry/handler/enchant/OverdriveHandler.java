package net.jaams.weaponry.handler.enchant;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
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
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class OverdriveHandler {

    private static final Map<String, UUID> ATTRIBUTE_UUID_MAP = new ConcurrentHashMap<>();
    public static final String OVERDRIVE_ATTACK_SPEED = "Overdrive Attack Speed";

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        if (!EnchantmentsConfig.OVERDRIVE.get()) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        {
            int overdriveLevel = ModEnchantments.level(itemStack, ModEnchantments.OVERDRIVE);
            if (overdriveLevel > 0 && hasBaseAttackSpeed(event)) {
                int maxConfigLevel = EnchantmentsConfig.OVERDRIVE_MAX_LEVEL.get();
                int effectiveLevel = Math.min(overdriveLevel, maxConfigLevel);
                double speedBonusPerLevel = EnchantmentsConfig.OVERDRIVE_SPEED_BONUS_PER_LEVEL.get();
                double speedBonus = speedBonusPerLevel * effectiveLevel;
                AttributeEntry entry = createEntry(OVERDRIVE_ATTACK_SPEED, Attributes.ATTACK_SPEED, speedBonus, AttributeModifier.Operation.ADD_VALUE);
                event.addModifier(entry.attribute, entry.modifier, EquipmentSlotGroup.MAINHAND);
            }
        }
    }

    private static boolean hasBaseAttackSpeed(ItemAttributeModifierEvent event) {
        return event.getDefaultModifiers().modifiers().stream().anyMatch((entry) -> entry.attribute().is(Attributes.ATTACK_SPEED.getKey()));
    }

    private static AttributeEntry createEntry(String name, Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {
        AttributeModifier modifier = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", name), value, operation);
        return new AttributeEntry(attribute, modifier);
    }

    private static class AttributeEntry {

        final Holder<Attribute> attribute;
        final AttributeModifier modifier;

        AttributeEntry(Holder<Attribute> attribute, AttributeModifier modifier) {
            this.attribute = attribute;
            this.modifier = modifier;
        }
    }
}
