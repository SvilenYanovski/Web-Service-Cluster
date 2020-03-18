package com.yanovski.kv.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class KVPairDto {
    @NotNull
    @Max(64)
    private String key;

    @Max(256)
    private String value;

    public KVPairDto() {}

    public KVPairDto(@NotNull @Max(64) String key, @Max(256) String value) {
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
