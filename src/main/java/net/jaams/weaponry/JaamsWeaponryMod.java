package net.jaams.weaponry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.jaams.weaponry.capability.ModAttachments;
import net.jaams.weaponry.init.ModDataComponents;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.init.ModLootModifiers;
import net.jaams.weaponry.init.ModMenus;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.init.ModParticles;
import net.jaams.weaponry.init.ModRecipes;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.init.ModTabs;
import net.jaams.weaponry.network.GunGUISlotMessage;
import net.jaams.weaponry.network.PistolGUISlotMessage;
import net.jaams.weaponry.network.PlayAnimationMessage;
import net.jaams.weaponry.network.RevolverGUISlotMessage;
import net.jaams.weaponry.network.PlayMobAnimationMessage;
import net.jaams.weaponry.network.ScattergunGUISlotMessage;
import net.jaams.weaponry.network.ShotgunGUISlotMessage;
import net.jaams.weaponry.packet.AberrationPacket;
import net.jaams.weaponry.packet.AmountPacket;
import net.jaams.weaponry.packet.GunInventoryPacket;
import net.jaams.weaponry.packet.GunShootPacket;
import net.jaams.weaponry.packet.VisualRecoilPacket;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.registry.CopperItems;
import net.jaams.weaponry.registry.DiamondItems;
import net.jaams.weaponry.registry.ElectrumItems;
import net.jaams.weaponry.registry.EnderiumItems;
import net.jaams.weaponry.registry.FarmersDelightItems;
import net.jaams.weaponry.registry.GoldenItems;
import net.jaams.weaponry.registry.IronItems;
import net.jaams.weaponry.registry.NetheriteItems;
import net.jaams.weaponry.registry.RosegoldItems;
import net.jaams.weaponry.registry.RoyalItems;
import net.jaams.weaponry.registry.ShineriteItems;
import net.jaams.weaponry.registry.StoneItems;
import net.jaams.weaponry.registry.SupplementariesItems;
import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.registry.WoodItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.jaams.weaponry.configuration.base.JaamsWeaponryModConfigs;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JaamsWeaponryMod.MODID)
public class JaamsWeaponryMod {

    public static final Logger LOGGER = LogManager.getLogger(JaamsWeaponryMod.class);
    public static final String MODID = "jaams_weaponry";

    public JaamsWeaponryMod(IEventBus bus, ModContainer container) {
        JaamsWeaponryModConfigs.register(container);
        NeoForge.EVENT_BUS.register(this);

        ModTabs.REGISTRY.register(bus);
        ModRecipes.REGISTRY.register(bus);
        ModParticles.register(bus);
        ModSounds.REGISTRY.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModMobEffects.REGISTRY.register(bus);
        ModMenus.REGISTRY.register(bus);
        ModLootModifiers.register(bus);
        ModAttachments.ATTACHMENT_TYPES.register(bus);
        ModDataComponents.register(bus);

        TopItems.REGISTRY.register(bus);
        WoodItems.REGISTRY.register(bus);
        StoneItems.REGISTRY.register(bus);
        IronItems.REGISTRY.register(bus);
        GoldenItems.REGISTRY.register(bus);
        DiamondItems.REGISTRY.register(bus);
        NetheriteItems.REGISTRY.register(bus);
        if (ModList.get().isLoaded("leafscopperbackport") || ModList.get().isLoaded("copperagebackport")) {
            CopperItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("cavesanddepths")) {
            RosegoldItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("jaams_shinerite")) {
            ShineriteItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("majruszsdifficulty")) {
            EnderiumItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("oreganized")) {
            ElectrumItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("farmersdelight")) {
            FarmersDelightItems.REGISTRY.register(bus);
        }
        if (ModList.get().isLoaded("supplementaries")) {
            SupplementariesItems.REGISTRY.register(bus);
        }
        RoyalItems.REGISTRY.register(bus);
        BottomItems.REGISTRY.register(bus);

        bus.addListener(this::registerPayloads);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AmountPacket.TYPE, AmountPacket.STREAM_CODEC, AmountPacket::handle);
        registrar.playToClient(AberrationPacket.TYPE, AberrationPacket.STREAM_CODEC, AberrationPacket::handle);
        registrar.playToClient(VisualRecoilPacket.TYPE, VisualRecoilPacket.STREAM_CODEC, VisualRecoilPacket::handle);
        registrar.playToClient(PlayAnimationMessage.TYPE, PlayAnimationMessage.STREAM_CODEC, PlayAnimationMessage::handle);
        registrar.playToClient(PlayMobAnimationMessage.TYPE, PlayMobAnimationMessage.STREAM_CODEC, PlayMobAnimationMessage::handle);
        registrar.playToServer(GunInventoryPacket.TYPE, GunInventoryPacket.STREAM_CODEC, GunInventoryPacket::handle);
        registrar.playBidirectional(GunShootPacket.TYPE, GunShootPacket.STREAM_CODEC, GunShootPacket::handle);
        registrar.playToServer(GunGUISlotMessage.TYPE, GunGUISlotMessage.STREAM_CODEC, GunGUISlotMessage::handle);
        registrar.playToServer(PistolGUISlotMessage.TYPE, PistolGUISlotMessage.STREAM_CODEC, PistolGUISlotMessage::handle);
        registrar.playToServer(ScattergunGUISlotMessage.TYPE, ScattergunGUISlotMessage.STREAM_CODEC, ScattergunGUISlotMessage::handle);
        registrar.playToServer(ShotgunGUISlotMessage.TYPE, ShotgunGUISlotMessage.STREAM_CODEC, ShotgunGUISlotMessage::handle);
        registrar.playToServer(RevolverGUISlotMessage.TYPE, RevolverGUISlotMessage.STREAM_CODEC, RevolverGUISlotMessage::handle);
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(ServerTickEvent.Post event) {
        List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach((work) -> {
            work.setValue(work.getValue() - 1);
            if (work.getValue() == 0)
                actions.add(work);
        });
        actions.forEach((e) -> e.getKey().run());
        workQueue.removeAll(actions);
    }
}
