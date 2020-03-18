package com.yanovski.load_balancer.controllers;

import com.yanovski.load_balancer.services.ClusterRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to register a Service Node into a cluster.
 *
 * @author SV
 */
@RestController
public class RegisterController {
    private final Logger logger = LogManager.getLogger(RegisterController.class);

    private final ClusterRegistrationService clusterRegistrationService;

    @Autowired
    public RegisterController(ClusterRegistrationService clusterRegistrationService) {
        this.clusterRegistrationService = clusterRegistrationService;
    }

    @GetMapping("/register")
    public void setKVPair(@RequestParam(required = true) String url,
                          @RequestParam(required = true) String port) {
        logger.info("Trying to register KV Server Node with URL: {}:{}", url, port);
        clusterRegistrationService.registerServiceNode(url,port);
    }

    @GetMapping("/getServerNode")
    public String getNode() {
        return clusterRegistrationService.getAvailableNode();
    }

    @GetMapping("/getAllServerNodes")
    public List<String> getAllNodes() {
        return clusterRegistrationService.getAllRegisteredNodes();
    }
}
