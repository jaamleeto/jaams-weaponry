package net.jaams.weaponry.loader;

import net.jaams.weaponry.animation.AnimationSyncables;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModLoaderSync {
    private static final Map<String, NetworkSyncable> SYNCABLES = new LinkedHashMap<>();

    static {
        register(new AnimationSyncables.Animations());
        register(new AnimationSyncables.RandomGroups());
        register(HeldPoseModifierLoader.INSTANCE);
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
