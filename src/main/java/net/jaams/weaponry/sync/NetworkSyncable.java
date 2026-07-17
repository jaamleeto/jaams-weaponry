package net.jaams.weaponry.sync;

import java.util.Map;

/**
 * Implemented by data-driven reload listeners whose definitions must also exist
 * on the physical client when playing on a dedicated server. The server sends
 * snapshots of the raw JSON sources via {@code SyncModDataMessage} and the
 * client applies them through {@link #applyNetworkSync(Map)}.
 */
public interface NetworkSyncable {

    
    String getSyncId();

    
    Map<String, String> getSourcesSnapshot();

    
    void applyNetworkSync(Map<String, String> sources);
}
