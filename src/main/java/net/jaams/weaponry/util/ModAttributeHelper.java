package net.jaams.weaponry.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** 1.21: getDefaultAttributeModifiers overrides are gone; attributes are a component. */
public final class ModAttributeHelper {

    private ModAttributeHelper() {
    }

    /** Mainhand attack-damage/attack-speed modifiers, mirroring the old getDefaultAttributeModifiers overrides. */
    public static ItemAttributeModifiers mainhand(double attackDamage, double attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static AttributeModifier modifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation op) {
        return new AttributeModifier(id, amount, op);
    }
}
