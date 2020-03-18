package com.yanovski.kv.repository;

import com.yanovski.kv.entities.KVPair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Blocking CRUD-only repository
 */
public interface KVPairRepository extends CrudRepository<KVPair, String> {
    /**
     * Paged list of KVPair objects
     *
     * @param page Pagination config object
     * @return list with KVPairs
     */
    List<KVPair> findAll(Pageable page);

    /**
     * Find by KVPair.key
     *
     * @param objectKey kv pair key
     * @return kv pair
     */
    KVPair findByObjectKey(String objectKey);
}
