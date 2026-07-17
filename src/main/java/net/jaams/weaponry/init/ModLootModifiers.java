package net.jaams.weaponry.init;

import com.mojang.serialization.Codec;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.loot.AddItemLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JaamsWeaponryMod.MODID);

    public static final RegistryObject<Codec<AddItemLootModifier>> ADD_WEAPONRY_ITEMS =
        LOOT_MODIFIER_SERIALIZERS.register("add_weaponry_items", () -> AddItemLootModifier.CODEC.get());

    public static void register(IEventBus modEventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
    }
}
