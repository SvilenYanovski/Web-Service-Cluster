package com.yanovski.load_balancer.models;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class KVPair {
    @NotNull
    @Max(64)
    private String key;

    @Max(256)
    private String value;

    public KVPair() {}

    public KVPair(@NotNull @Max(64) String key, @Max(256) String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
