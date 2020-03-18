package com.yanovski.kv.services.impl;

import com.yanovski.kv.dto.KVPairDto;
import com.yanovski.kv.entities.KVPair;
import com.yanovski.kv.repository.KVPairReactiveRepository;
import com.yanovski.kv.services.KVReactiveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class KVReactiveServiceImpl implements KVReactiveService {
    private final Logger logger = LogManager.getLogger(KVReactiveServiceImpl.class);

    private KVPairReactiveRepository kvPairReactiveRepository;

    @Autowired
    public KVReactiveServiceImpl(KVPairReactiveRepository kvPairReactiveRepository) {
        this.kvPairReactiveRepository = kvPairReactiveRepository;
    }

    @Override
    public Flux<KVPairDto> getAll() {
        return kvPairReactiveRepository.findAll().map(kvPair ->
                new KVPairDto(kvPair.getObjectKey(), kvPair.getObjectValue()));
    }

    @Override
    public Flux<String> getAllKeys() {
        return kvPairReactiveRepository.findAll().map(KVPair::getObjectKey);
    }

    @Override
    public Flux<String> getAllValues() {
        return kvPairReactiveRepository.findAll().map(KVPair::getObjectValue);
    }
}
