package com.yanovski.load_balancer.services.impl;

import com.yanovski.load_balancer.controllers.RegisterController;
import com.yanovski.load_balancer.models.ClusterEmptyException;
import com.yanovski.load_balancer.models.KVPair;
import com.yanovski.load_balancer.models.ModificationType;
import com.yanovski.load_balancer.services.ClusterRegistrationService;
import com.yanovski.load_balancer.services.DataSyncService;
import com.yanovski.load_balancer.services.KVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class KVServiceImpl implements KVService {
    private final Logger logger = LogManager.getLogger(RegisterController.class);

    @Autowired
    private ClusterRegistrationService clusterRegistrationService;

    @Autowired
    private DataSyncService dataSyncService;

    @Override
    public void setKVPair(String key, String value) {
        validateCluster();

        //time synchronization
        while (!dataSyncService.isLockedObjectKey(key)) {
            logger.warn("Key locked: {}", key);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Error while waiting for key.");
                e.printStackTrace();
            }
        }
        dataSyncService.doLockObjectKey(key);

        String url = clusterRegistrationService.getAvailableNode() + "/set";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("k", key)
                .queryParam("v", value)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                Void.class);

        dataSyncService.releaseObjectKey(key);
        dataSyncService.handleStoreModification(ModificationType.ADDED, new KVPair(key, value));
    }

    @Override
    public void setKVPairToNode(String targetUrl, String key, String value) {
        validateCluster();

        String url = targetUrl + "/set";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("k", key)
                .queryParam("v", value)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                Void.class);
    }

    @Override
    public String getKVPair(String key) {
        validateCluster();

        String url = clusterRegistrationService.getAvailableNode() + "/get";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("k", key)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        return response.getBody();
    }

    @Override
    public void removeKVPair(String key) {
        validateCluster();

        //time synchronization
        while (!dataSyncService.isLockedObjectKey(key)) {
            logger.warn("Key locked: {}", key);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Error while waiting for key.");
                e.printStackTrace();
            }
        }
        dataSyncService.doLockObjectKey(key);

        String url = clusterRegistrationService.getAvailableNode() + "/rm";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("k", key)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                Void.class);

        dataSyncService.releaseObjectKey(key);
        dataSyncService.handleStoreModification(ModificationType.DELETED, new KVPair(key, ""));
    }

    @Override
    public void removeAllKVPairs() {
        validateCluster();

        String url = clusterRegistrationService.getAvailableNode() + "/clear";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                Void.class);

        dataSyncService.handleStoreModification(ModificationType.DELETED_ALL, new KVPair(null, null));
    }

    @Override
    public Boolean isExistingKVPair(String key) {
        validateCluster();

        String url = clusterRegistrationService.getAvailableNode() + "/is";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("k", key)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Boolean> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                Boolean.class);

        return response.getBody();
    }

    @Override
    public List<KVPair> getAll() {
        validateCluster();

        String url = clusterRegistrationService.getAvailableNode() + "/getAll";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("isMemory", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<KVPair[]> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                KVPair[].class);

        return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
    }

    private void validateCluster() {
        if (clusterRegistrationService.getAllRegisteredNodes().size() == 0) {
            throw new ClusterEmptyException();
        }
    }
}
