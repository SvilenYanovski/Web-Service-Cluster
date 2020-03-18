package com.yanovski.kv.services;

import com.yanovski.kv.dto.KVPairDto;
import reactor.core.publisher.Flux;

/**
 * Service for the reactive methods, using Spring WebFlux and Reactive Mongo
 */
public interface KVReactiveService {
    Flux<KVPairDto> getAll();
    Flux<String> getAllKeys();
    Flux<String> getAllValues();
}
