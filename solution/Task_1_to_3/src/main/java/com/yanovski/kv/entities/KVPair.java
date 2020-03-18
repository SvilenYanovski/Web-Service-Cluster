package com.yanovski.kv.entities;

/**
 * DB Entity
 */
public class KVPair {
    private String objectKey;
    private String objectValue;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }
}
