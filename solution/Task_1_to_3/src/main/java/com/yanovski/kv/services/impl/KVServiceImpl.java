package com.yanovski.kv.services.impl;

import com.yanovski.kv.dto.KVPairDto;
import com.yanovski.kv.entities.KVPair;
import com.yanovski.kv.repository.KVPairRepository;
import com.yanovski.kv.services.KVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KVServiceImpl implements KVService {

    private final Map<String, String> kvPairsCache = new ConcurrentHashMap<>();

    private final KVPairRepository kvPairRepository;

    @Autowired
    public KVServiceImpl(KVPairRepository kvPairRepository) {
        this.kvPairRepository = kvPairRepository;
    }

    @Override
    public void setKVPair(String key, String value, boolean useCacheOnly) {
        if (useCacheOnly) {
            kvPairsCache.put(key, value);
        } else {
            KVPair kvPair = new KVPair();
            kvPair.setObjectKey(key);
            kvPair.setObjectValue(value);

            kvPairRepository.save(kvPair);
        }
    }

    @Override
    public String getKVPair(String key, boolean useCacheOnly) {
        if (useCacheOnly) {
            return kvPairsCache.get(key);
        } else {
            KVPair kvPair = kvPairRepository.findByObjectKey(key);
            return kvPair.getObjectValue();
        }
    }

    @Override
    public KVPairDto getKVPairDto(String key, boolean useCacheOnly) {
        if (useCacheOnly) {
            return new KVPairDto(key, kvPairsCache.get(key));
        } else {
            KVPair kvPair = kvPairRepository.findByObjectKey(key);
            return new KVPairDto(kvPair.getObjectKey(), kvPair.getObjectValue());
        }
    }

    @Override
    public void removeKVPair(String key, boolean useCacheOnly) {
        if (useCacheOnly) {
            kvPairsCache.remove(key);
        } else {
            kvPairRepository.delete(kvPairRepository.findByObjectKey(key));
        }
    }

    @Override
    public void removeAllKVPairs(boolean useCacheOnly) {
        if (useCacheOnly) {
            kvPairsCache.clear();
        } else {
            kvPairRepository.deleteAll();
        }
    }

    @Override
    public boolean isExistingKVPair(String key, boolean useCacheOnly) {
        if (useCacheOnly) {
            return kvPairsCache.containsKey(key);
        } else {
            return kvPairRepository.findByObjectKey(key) != null;
        }
    }

    @Override
    public List<String> getAllKeys() {
        List<KVPair> pairs = (List<KVPair>) kvPairRepository.findAll();
        return pairs.stream().map(KVPair::getObjectKey).collect(Collectors.toList());
    }

    @Override
    public Set<String> getAllValues() {
        List<KVPair> pairs = (List<KVPair>) kvPairRepository.findAll();
        Set<String> result = new HashSet<>();
        for (KVPair pair: pairs) {
            result.add(pair.getObjectValue());
        }
        return result;
    }

    @Override
    public List<KVPairDto> getAll(boolean useCacheOnly) {
        List<KVPair> pairs = (List<KVPair>) kvPairRepository.findAll();
        return pairs.stream().map(p -> new KVPairDto(p.getObjectKey(), p.getObjectValue())).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllKeysPaged(int page, int size) {
        return kvPairRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(KVPair::getObjectKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllValuesPaged(int page, int size) {
        return kvPairRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(KVPair::getObjectValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<KVPairDto> getAllPaged(int page, int size) {
        return kvPairRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(kvPair -> new KVPairDto(kvPair.getObjectKey(), kvPair.getObjectValue()))
                .collect(Collectors.toList());
    }
}
