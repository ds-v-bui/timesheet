package com.dsvn.starterkit.api.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataPayload implements BasePayload {
    private String message;
    private Object data;

    public DataPayload(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
