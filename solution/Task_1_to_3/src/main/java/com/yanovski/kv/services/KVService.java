package com.yanovski.kv.services;

import com.yanovski.kv.dto.KVPairDto;

import java.util.List;
import java.util.Set;

/**
 * Service for the Blocking methods over the KV store
 *
 * The methods provide two options to store the data - In Memory and In Database (MongoDB).
 * In Memory kv store is used for building a cluster with KV Applications for Task 4.
 *
 * @author SV
 */
public interface KVService {
    void setKVPair(String key, String value, boolean useCacheOnly);
    String getKVPair(String key, boolean useCacheOnly);
    KVPairDto getKVPairDto(String key, boolean useCacheOnly);
    void removeKVPair(String key, boolean useCacheOnly);
    void removeAllKVPairs(boolean useCacheOnly);
    boolean isExistingKVPair(String key, boolean useCacheOnly);

    List<String> getAllKeys();
    Set<String> getAllValues();
    List<KVPairDto> getAll(boolean useCacheOnly);

    List<String> getAllKeysPaged(int page, int size);
    List<String> getAllValuesPaged(int page, int size);
    List<KVPairDto> getAllPaged(int page, int size);
}
