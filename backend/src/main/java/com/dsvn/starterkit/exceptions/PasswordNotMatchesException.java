package com.dsvn.starterkit.exceptions;

public class PasswordNotMatchesException extends RuntimeException {
    public PasswordNotMatchesException(String message) {
        super(message);
    }
}
