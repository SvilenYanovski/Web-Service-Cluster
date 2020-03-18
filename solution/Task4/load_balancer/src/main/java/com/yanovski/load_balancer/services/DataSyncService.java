package com.yanovski.load_balancer.services;

import com.yanovski.load_balancer.models.KVPair;
import com.yanovski.load_balancer.models.ModificationType;

/**
 * Service for synchronization of the data between the Server Nodes
 */
public interface DataSyncService {
    /**
     * Lock the KV Pair with a certain key for modification
     *
     * @param key pair key
     */
    void doLockObjectKey(String key);

    /**
     * Release the KV Pair with a certain key for modification
     *
     * @param key pair key
     */
    void releaseObjectKey(String key);

    /**
     * Is the KV Pair with a certain key for modification
     *
     * @param key pair key
     */
    Boolean isLockedObjectKey(String key);

    /**
     * Sync data between Nodes
     *
     * @param modificationType data modification type
     * @param kvPair kv pair model
     */
    void handleStoreModification(ModificationType modificationType, KVPair kvPair);
}
