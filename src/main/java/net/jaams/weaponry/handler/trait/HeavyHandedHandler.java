package net.jaams.weaponry.handler.trait;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Holder;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.tooltip.trait.HeavyHandedItemTooltip;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.UUID;

@EventBusSubscriber
public class HeavyHandedHandler {

    private static final UUID HEAVY_HANDED_MOVEMENT_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID HEAVY_HANDED_ATTACK_SPEED_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    private static final UUID HEAVY_HANDED_ATTACK_DAMAGE_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide)
            return;
        if (!TraitsConfig.HEAVY_HANDED.get())
            return;
        if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
            removeDebuffs(player);
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean heavyHandedInMain = !mainHand.isEmpty() && ModTraits.isHeavyHandedItem(mainHand);
        boolean heavyHandedInOff = !offHand.isEmpty() && ModTraits.isHeavyHandedItem(offHand);

        boolean hasHeavyHanded = heavyHandedInMain || heavyHandedInOff;
        boolean hasDualWield = hasHeavyHanded
                && ((heavyHandedInMain && !offHand.isEmpty()) || (heavyHandedInOff && !mainHand.isEmpty()));

        if (hasDualWield) {
            ItemStack heavyStack = heavyHandedInMain ? mainHand : offHand;
            ItemStack otherStack = heavyHandedInMain ? offHand : mainHand;
            applyDebuffs(player, heavyStack, otherStack);
        } else {
            removeDebuffs(player);
        }
    }

    private static void applyDebuffs(Player player, ItemStack stack, ItemStack otherStack) {
        double movReduction = HeavyHandedItemTooltip.calculateMovementReduction(stack, otherStack);
        double atkSpeedReduction = HeavyHandedItemTooltip.calculateAttackSpeedReduction(stack, otherStack);
        double atkDmgReduction = HeavyHandedItemTooltip.calculateAttackDamageReduction(stack, otherStack);

        
        
        applyModifier(player, Attributes.MOVEMENT_SPEED, HEAVY_HANDED_MOVEMENT_UUID,
                -movReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, Attributes.ATTACK_SPEED, HEAVY_HANDED_ATTACK_SPEED_UUID,
                -atkSpeedReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, Attributes.ATTACK_DAMAGE, HEAVY_HANDED_ATTACK_DAMAGE_UUID,
                -atkDmgReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void removeDebuffs(Player player) {
        removeModifier(player, Attributes.MOVEMENT_SPEED, HEAVY_HANDED_MOVEMENT_UUID);
        removeModifier(player, Attributes.ATTACK_SPEED, HEAVY_HANDED_ATTACK_SPEED_UUID);
        removeModifier(player, Attributes.ATTACK_DAMAGE, HEAVY_HANDED_ATTACK_DAMAGE_UUID);
    }

    private static void applyModifier(Player player, Holder<Attribute> attribute, UUID uuid, double amount,
            AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null)
            return;
        ResourceLocation id = net.jaams.weaponry.util.ModUtils.modifierId(uuid);
        AttributeModifier modifier = instance.getModifier(id);
        if (modifier != null && modifier.amount() == amount && modifier.operation() == operation)
            return;
        instance.removeModifier(id);
        if (amount != 0.0) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
        }
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null)
            return;
        instance.removeModifier(net.jaams.weaponry.util.ModUtils.modifierId(uuid));
    }
}
