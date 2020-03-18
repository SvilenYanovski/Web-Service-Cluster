package com.yanovski.kv.repository;

import com.yanovski.kv.entities.KVPair;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Reactive repository default configuration
 */
public interface KVPairReactiveRepository extends ReactiveCrudRepository<KVPair, String> {
}
