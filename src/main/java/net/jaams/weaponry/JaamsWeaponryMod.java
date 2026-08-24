package net.jaams.weaponry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.init.ModLootModifiers;
import net.jaams.weaponry.init.ModMenus;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.init.ModPaintings;
import net.jaams.weaponry.init.ModParticles;
import net.jaams.weaponry.init.ModRecipes;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.init.ModTabs;
import net.jaams.weaponry.network.PlayAnimationMessage;
import net.jaams.weaponry.network.PlayMobAnimationMessage;
import net.jaams.weaponry.network.SyncModDataMessage;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("jaams_weaponry")
public class JaamsWeaponryMod {

    public static final Logger LOGGER = LogManager.getLogger(JaamsWeaponryMod.class);
    public static final String MODID = "jaams_weaponry";
    public static final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder().disableHtmlEscaping().create();

    public static boolean isOwnNamespace(String location) {
        int i = location.indexOf(':');
        String ns = i >= 0 ? location.substring(0, i) : "";
        return MODID.equals(ns);
    }

    public JaamsWeaponryMod() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModTabs.REGISTRY.register(bus);
        ModRecipes.REGISTRY.register(bus);
        ModParticles.register(bus);
        ModSounds.REGISTRY.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModEnchantments.REGISTRY.register(bus);
        ModMobEffects.REGISTRY.register(bus);
        ModMenus.REGISTRY.register(bus);
        ModPaintings.REGISTRY.register(bus);
        ModLootModifiers.register(bus);

        TopItems.REGISTRY.register(bus);
        WoodItems.REGISTRY.register(bus);
        StoneItems.REGISTRY.register(bus);
        IronItems.REGISTRY.register(bus);
        GoldenItems.REGISTRY.register(bus);
        DiamondItems.REGISTRY.register(bus);
        NetheriteItems.REGISTRY.register(bus);
        if (ModList.get().isLoaded("copperagebackport")) {
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

        bus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        addNetworkMessage(AmountPacket.class, AmountPacket::encode, AmountPacket::decode, AmountPacket::handle);
        addNetworkMessage(AberrationPacket.class, AberrationPacket::encode, AberrationPacket::decode,
                AberrationPacket::handle);
        addNetworkMessage(GunInventoryPacket.class, GunInventoryPacket::encode, GunInventoryPacket::decode,
                GunInventoryPacket::handle);
        addNetworkMessage(GunShootPacket.class, GunShootPacket::encode, GunShootPacket::decode, GunShootPacket::handle);
        addNetworkMessage(VisualRecoilPacket.class, VisualRecoilPacket::encode, VisualRecoilPacket::decode,
                VisualRecoilPacket::handle);
        addNetworkMessage(PlayAnimationMessage.class, PlayAnimationMessage::encode, PlayAnimationMessage::decode,
                PlayAnimationMessage::handle);
        addNetworkMessage(PlayMobAnimationMessage.class, PlayMobAnimationMessage::encode,
                PlayMobAnimationMessage::decode,
                PlayMobAnimationMessage::handle);
        addNetworkMessage(SyncModDataMessage.class, SyncModDataMessage::encode, SyncModDataMessage::decode,
                SyncModDataMessage::handle);
    }

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
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
}
