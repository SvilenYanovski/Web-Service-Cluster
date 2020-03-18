package com.yanovski.kv.controllers;

import com.yanovski.kv.dto.KVPairDto;
import com.yanovski.kv.services.KVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Main controller.
 *
 * Contains all methods to operate over Key-Value store in a Blocking way.
 * The potentially heavy-load methods are provided with client generated pagination configuration.
 *
 * The methods provide two options to store the data - In Memory and In Database (MongoDB).
 * In Memory kv store is used for building a cluster with KV Applications for Task 4.
 *
 * @author SV
 */
@RestController
public class KVController {

    private KVService kvService;

    private final Logger logger = LogManager.getLogger(KVController.class);

    @Autowired
    public KVController(KVService kvService) {
        this.kvService = kvService;
    }

    /**
     * Home controller - opens Swagger UI
     *
     * @param response void
     * @throws IOException in generating Swagger UI
     */
    @GetMapping("/")
    public void redirect(HttpServletResponse response) throws IOException {
        logger.info("Redirecting to Swagger.");
        response.sendRedirect("/swagger-ui.html");
    }

    /**
     * Create Key-Value pair.
     *
     * @param k pair key
     * @param v pair value
     * @param inMemory flag to determine if a DB will be used or In Memory Cache
     */
    @GetMapping("/set")
    public void setKVPair(@RequestParam(required = true) @Max(64) String k,
                          @RequestParam(required = false, defaultValue = "") @Max(256) String v,
                          @RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        logger.info("Trying to create KV Pair with key: {} and value: {}", k, v);
        kvService.setKVPair(k,v, inMemory);
    }

    /**
     * Get Pair method
     *
     * @param k object key
     * @param inMemory flag to determine if a DB will be used or In Memory Cache
     * @return the pair value, if exists
     */
    @GetMapping("/get")
    public ResponseEntity<String> getKVPair(@RequestParam String k,
                                            @RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        if (k == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Trying to get KV Pair with key: {}", k);
        String result = kvService.getKVPair(k, inMemory);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    /**
     * Get all keys blocking
     *
     * @return list of all keys
     */
    @GetMapping("/getKeys")
    public ResponseEntity<List<String>> getAllKeys() {
        logger.info("Trying to get all Keys");
        List<String> result = kvService.getAllKeys();
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Paged method to get all keys
     *
     * @param page page number, starts from 0
     * @param size page size
     * @return paged result as list
     */
    @GetMapping("/getKeys/paged")
    public ResponseEntity<List<String>> getAllKeysPaged(@RequestParam Integer page,
                                                        @RequestParam Integer size) {
        logger.info("Trying to get all Keys paged");
        List<String> result = kvService.getAllKeysPaged(page, size);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Get all values blocking
     * Holds the data into a Set to ensure that only unique values are transferred via the network
     *
     * @return set of all values
     */
    @GetMapping("/getValues")
    public ResponseEntity<Set<String>> getAllValues() {
        logger.info("Trying to get all Values");
        Set<String> result = kvService.getAllValues();
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Get all pair values with pagination
     *
     * @param page page number, starts from 0
     * @param size page size
     * @return paged result as list
     */
    @GetMapping("/getValues/paged")
    public ResponseEntity<List<String>> getAllValuesPaged(@RequestParam Integer page,
                                                        @RequestParam Integer size) {
        logger.info("Trying to get all Values paged");
        List<String> result = kvService.getAllValuesPaged(page, size);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Get all pairs as DTO objects
     *
     * @return list with the pairs
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<KVPairDto>> getAll(@RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        logger.info("Trying to get all pairs");
        List<KVPairDto> result = kvService.getAll(inMemory);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Get all pairs paginated
     *
     * @param page page number, starts from 0
     * @param size page size
     * @return paged result as list
     */
    @GetMapping("/getAll/paged")
    public ResponseEntity<List<KVPairDto>> getAllPaged(@RequestParam Integer page,
                                                        @RequestParam Integer size) {
        logger.info("Trying to get all Pairs paged");
        List<KVPairDto> result = kvService.getAllPaged(page, size);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    /**
     * Remove a pair by the Key
     *
     * @param k pair key
     * @param inMemory flag to determine if a DB will be used or In Memory Cache
     */
    @GetMapping("/rm")
    public void removeKVPair(@RequestParam @NotNull String k,
                             @RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        logger.info("Trying to remove KV Pair with key: {}", k);
        kvService.removeKVPair(k, inMemory);
    }

    /**
     * Clears the stored Key-Value pairs
     *
     * @param inMemory flag to determine if a DB will be used or In Memory Cache
     */
    @GetMapping("/clear")
    public void deleteAll(@RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        logger.info("Trying to remove all KV Pairs.");
        kvService.removeAllKVPairs(inMemory);
    }

    /**
     * Validates if a pair exists by its Key
     *
     * @param k pair key
     * @param inMemory flag to determine if a DB will be used or In Memory Cache
     * @return boolean value, HTTP code OK if exists, HTTP code NOT_FOUND if missing
     */
    @GetMapping("/is")
    public ResponseEntity<Boolean> isExistingKVPair(@RequestParam String k,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean inMemory) {
        if (k == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Trying to find KV Pair with key: {}", k);
        boolean result = kvService.isExistingKVPair(k, inMemory);
        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
