package net.jaams.weaponry.sync;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import net.jaams.weaponry.loader.EquipmentModifierLoader;
import net.jaams.weaponry.loader.GunModifierLoader;
import net.jaams.weaponry.loader.ItemModifierLoader;
import net.jaams.weaponry.loader.LootModifierLoader;
import net.jaams.weaponry.loader.RangedModifierLoader;
import net.jaams.weaponry.loader.TabModifierLoader;
import net.jaams.weaponry.loader.ThrowableModifierLoader;
import net.jaams.weaponry.loader.TradeModifierLoader;
import net.jaams.weaponry.loader.TraitModifierLoader;

public final class ModLoaderSync {
    private static final Map<String, NetworkSyncable> SYNCABLES = new LinkedHashMap<>();

    static {
        register(ItemModifierLoader.INSTANCE);
        register(GunModifierLoader.INSTANCE);
        register(RangedModifierLoader.INSTANCE);
        register(ThrowableModifierLoader.INSTANCE);
        register(TraitModifierLoader.INSTANCE);
        register(TabModifierLoader.INSTANCE);
        register(EquipmentModifierLoader.INSTANCE);
        register(LootModifierLoader.INSTANCE);
        register(TradeModifierLoader.INSTANCE);
    }

    private ModLoaderSync() {
    }

    public static void register(NetworkSyncable syncable) {
        SYNCABLES.put(syncable.getSyncId(), syncable);
    }

    public static NetworkSyncable get(String syncId) {
        return SYNCABLES.get(syncId);
    }

    public static Collection<NetworkSyncable> all() {
        return SYNCABLES.values();
    }
}
