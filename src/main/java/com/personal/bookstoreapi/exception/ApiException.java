package com.personal.bookstoreapi.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
