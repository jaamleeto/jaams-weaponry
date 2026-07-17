package net.jaams.weaponry.capability;

import net.jaams.weaponry.capability.aberration.AberrationProvider;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.capability.gun.GunInventoryCapability;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityAttacher {

    public static final ResourceLocation AMOUNT_CAP = new ResourceLocation("jaams_weaponry", "amount");
    public static final ResourceLocation ABERRATION_CAP = new ResourceLocation("jaams_weaponry", "aberration");
    public static final ResourceLocation GUN_INVENTORY_CAP = new ResourceLocation("jaams_weaponry", "gun_inventory");

    @SubscribeEvent
    public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (ModGuns.isGun(stack)) {
            ModGuns.GunType type = ModGuns.getGunType(stack);
            if (type != null) {
                event.addCapability(GUN_INVENTORY_CAP, new GunInventoryCapability(type, stack));
            }
        }
    }

    @SubscribeEvent
    public static void onCapabilityAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ABERRATION_CAP, new AberrationProvider());
        }
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(AMOUNT_CAP, new AmountProvider());
        }
    }
}
