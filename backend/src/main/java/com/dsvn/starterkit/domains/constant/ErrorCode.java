package com.dsvn.starterkit.domains.constant;

public enum ErrorCode {
    UNAUTHORIZED_ERROR(401),
    FORBIDDEN_ERROR(403),
    API_NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),

    INTERNAL_ERROR(500),

    VALIDATION_ERROR(700),
    PASSWORD_NOT_MATCHES_ERROR(701),
    USER_NOT_FOUND_ERROR(702),
    RESOURCE_ALREADY_EXIST(703);

    private final int error;

    ErrorCode(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }
}
