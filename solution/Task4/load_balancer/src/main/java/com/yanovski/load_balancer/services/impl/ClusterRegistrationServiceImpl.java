package com.yanovski.load_balancer.services.impl;

import com.yanovski.load_balancer.models.KVPair;
import com.yanovski.load_balancer.services.ClusterRegistrationService;
import com.yanovski.load_balancer.services.KVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ClusterRegistrationServiceImpl implements ClusterRegistrationService {
    private final Logger logger = LogManager.getLogger(ClusterRegistrationServiceImpl.class);

    @Autowired
    private KVService kvService;

    //thread safe
    private final List<String> nodes = Collections.synchronizedList(new ArrayList<>());

    //thread safe
    private static AtomicInteger nodePosition = new AtomicInteger();

    @Override
    public void registerServiceNode(String url, String port) {
        String constructedUrl = String.format("http://%s:%s", url, port);
        logger.info("Started Registering Server Node URL: {}", constructedUrl);

        if (nodes.size() > 0) {
            List<KVPair> pairs = kvService.getAll();
            logger.info("Retrieved existing KVPairs for synchronization: {}", pairs.size());
            int errors = 0;

            for (KVPair pair: pairs) {
                try {
                    kvService.setKVPairToNode(constructedUrl, pair.getKey(), pair.getValue());
                } catch(Exception ex) {
                    errors++;
                    logger.error("ERROR saving data row: {}, {}", ex.getCause(), ex.getMessage());
                }
            }
            logger.info("Sync completed - {} rows of data, {} errors.", pairs.size() - errors, errors);
        } else {
            logger.info("First Server Node - no data for synchronization.");
        }

        nodes.add(constructedUrl);
        logger.info("Completed Registering Server Node URL: {}", constructedUrl);
    }

    @Override
    public void removeServiceNode(String url, String port) {
        nodes.remove(String.format("http://%s:%s", url, port));
    }

    @Override
    public String getAvailableNode() {
        String server = "";
        if (nodePosition.get() >= nodes.size()) {
            nodePosition.set(0);
        }

        server = nodes.get(nodePosition.getAndIncrement());
        logger.info("Retrieved Node URL: {}", server);
        return server;
    }

    @Override
    public List<String> getAllRegisteredNodes() {
        return Collections.unmodifiableList(nodes);
    }
}
