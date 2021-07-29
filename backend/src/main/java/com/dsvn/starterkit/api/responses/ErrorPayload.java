package com.dsvn.starterkit.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ErrorPayload implements BasePayload {

    private Integer errorCode;
    private String message;
    private List<Error> errors;

    public ErrorPayload(@NonNull Integer errorCode, String message, List<Error> errors) {
        this.errorCode = errorCode;
        this.message = message;
        this.errors = errors;
    }

    @Value
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Error {
        @NonNull String message;

        @NonNull String code;
    }
}
