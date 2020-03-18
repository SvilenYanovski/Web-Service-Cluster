package com.yanovski.load_balancer.controllers;

import com.yanovski.load_balancer.services.KVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * Main KV Controller to proxy the requests to the KV Service Nodes via a Load Ballancer.
 * Endpoints from Task 1, only in memory use, without Mongo.
 *
 * @author SV
 */
@RestController
public class KVController {
    private final Logger logger = LogManager.getLogger(KVController.class);

    private final KVService kvService;

    @Autowired
    public KVController(KVService kvService) {
        this.kvService = kvService;
    }

    @GetMapping("/set")
    public void setKVPair(@RequestParam(required = true) @Max(64) String k,
                          @RequestParam(required = false, defaultValue = "") @Max(256) String v) {
        logger.info("Trying to create KV Pair with key: {} and value: {}", k, v);
        kvService.setKVPair(k,v);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getKVPair(@RequestParam String k) {
        if (k == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Trying to get KV Pair with key: {}", k);
        String result = kvService.getKVPair(k);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rm")
    public void removeKVPair(@RequestParam @NotNull String k) {
        logger.info("Trying to remove KV Pair with key: {}", k);
        kvService.removeKVPair(k);
    }

    @GetMapping("/clear")
    public void setKVPair() {
        logger.info("Trying to remove all KV Pairs.");
        kvService.removeAllKVPairs();
    }

    @GetMapping("/is")
    public ResponseEntity<Boolean> isExistingKVPair(@RequestParam String k) {
        if (k == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Trying to find KV Pair with key: {}", k);
        boolean result = kvService.isExistingKVPair(k);
        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
