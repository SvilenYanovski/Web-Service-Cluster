package com.yanovski.kv.controllers;

import com.yanovski.kv.dto.KVPairDto;
import com.yanovski.kv.services.KVReactiveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Reactive controller - produces a data stream (text:event-stream)
 *
 * Used to provide GET methods over potentially huge data sets. There is no delay in the retrieval - every object is being
 * sent to the stream in the time of its' retrieval. The method takes the same time as the blocking (non-reactive) equivalent,
 * but the data can be processed without blocking delay.
 *
 * Provided two ways of retrieval:
 * 1. Not restricted flow - the stream lasts until the data is fully retrieved and the client takes care of the data handling
 * 2. With client defined Back-Pressure - the stream will serve only the desired amount of item at a time
 *
 * @author SV
 */
@RestController
@RequestMapping("/reactive")
public class KVReactiveController {
    private final Logger logger = LogManager.getLogger(KVReactiveController.class);

    private KVReactiveService kvReactiveService;

    @Autowired
    public KVReactiveController(KVReactiveService kvReactiveService) {
        this.kvReactiveService = kvReactiveService;
    }

    /**
     * Get all non-blocking
     *
     * @return stream of DTO KVPairDto objects
     */
    @GetMapping(value = "/getAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<KVPairDto> getAll() {
        logger.info("Trying to get all pairs reactive");
        return kvReactiveService.getAll();
    }

    /**
     * Get all pairs with backpressure
     *
     * @param limit backpressure stream limit
     * @return stream of DTO KVPairDto objects
     */
    @GetMapping(value = "withBackPressure/getAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<KVPairDto> getAllWithBackPressure(@RequestParam(required = true) Integer limit) {
        logger.info("Trying to get all pairs reactive");
        return kvReactiveService.getAll().limitRate(limit);
    }

    /**
     * Get all keys non-blocking
     *
     * @return stream of KVPairDto keys
     */
    @GetMapping(value = "/getAllKeys", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllKeys() {
        logger.info("Trying to get all keys reactive");
        return kvReactiveService.getAllKeys();
    }

    /**
     *Get all keys non-blocking
     *
     * @param limit backpressure stream limit
     * @return stream of DTO KVPairDto keys
     */
    @GetMapping(value = "withBackPressure/getAllKeys", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllKeysWithBackPressure(@RequestParam(required = true) Integer limit) {
        logger.info("Trying to get all keys reactive");
        return kvReactiveService.getAllKeys().limitRate(limit);
    }

    /**
     * Get all values non-blocking
     *
     * @return stream of KVPairDto values
     */
    @GetMapping(value = "/getAllValues", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllValues() {
        logger.info("Trying to get all values reactive");
        return kvReactiveService.getAllValues();
    }

    /**
     *Get all values non-blocking
     *
     * @param limit backpressure stream limit
     * @return stream of DTO KVPairDto keys
     */
    @GetMapping(value = "withBackPressure/getAllValues", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllValuesWithBackPressure(@RequestParam(required = true) Integer limit) {
        logger.info("Trying to get all values reactive");
        return kvReactiveService.getAllValues().limitRate(limit);
    }
}
