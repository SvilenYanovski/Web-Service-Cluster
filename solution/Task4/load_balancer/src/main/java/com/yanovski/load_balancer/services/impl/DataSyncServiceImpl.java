package com.yanovski.load_balancer.services.impl;

import com.yanovski.load_balancer.models.KVPair;
import com.yanovski.load_balancer.models.ModificationType;
import com.yanovski.load_balancer.services.ClusterRegistrationService;
import com.yanovski.load_balancer.services.DataSyncService;
import com.yanovski.load_balancer.services.KVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class DataSyncServiceImpl implements DataSyncService {

    private final ClusterRegistrationService clusterRegistrationService;
    private final KVService kvService;

    @Value("${executor.threads}")
    private Integer numberThreads;

    private final Set<String> lockedKeys = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public DataSyncServiceImpl(ClusterRegistrationService clusterRegistrationService, KVService kvService) {
        this.clusterRegistrationService = clusterRegistrationService;
        this.kvService = kvService;
    }

    @Override
    public void doLockObjectKey(String key) {
        lockedKeys.add(key);
    }

    @Override
    public void releaseObjectKey(String key) {
        lockedKeys.remove(key);
    }

    @Override
    public Boolean isLockedObjectKey(String key) {
        return lockedKeys.contains(key);
    }

    @Override
    public void handleStoreModification(ModificationType modificationType, KVPair kvPair) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);

        switch (modificationType) {
            case ADDED:
                handleAddedKVPair(executorService, kvPair);
                break;
            case DELETED:
                handleDeletedKVPair(executorService, kvPair);
                break;
            case DELETED_ALL:
                handleDeletedAll(executorService);
                break;
            default:
                break;
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void handleDeletedAll(ExecutorService executorService) {
        for (String nodeUrl: clusterRegistrationService.getAllRegisteredNodes()) {
            executorService.execute(kvService::removeAllKVPairs);
        }
    }

    private void handleDeletedKVPair(ExecutorService executorService, KVPair kvPair) {
        for (String nodeUrl: clusterRegistrationService.getAllRegisteredNodes()) {
            executorService.execute(() -> kvService.removeKVPair(kvPair.getKey()));
        }
    }

    private void handleAddedKVPair(ExecutorService executorService, KVPair kvPair) {
        for (String nodeUrl: clusterRegistrationService.getAllRegisteredNodes()) {
            executorService.execute(() -> kvService.setKVPair(kvPair.getKey(), kvPair.getValue()));
        }
    }
}
