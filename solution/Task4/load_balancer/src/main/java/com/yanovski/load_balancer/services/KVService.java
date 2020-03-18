package com.yanovski.load_balancer.services;

import com.yanovski.load_balancer.models.KVPair;

import java.util.List;

/**
 * Proxy service to route the HTTP calls to the registered cluster with Service Nodes
 */
public interface KVService {
    void setKVPair(String key, String value);
    void setKVPairToNode(String url, String key, String value);
    String getKVPair(String key);
    void removeKVPair(String key);
    void removeAllKVPairs();
    Boolean isExistingKVPair(String key);

    List<KVPair> getAll();
}
