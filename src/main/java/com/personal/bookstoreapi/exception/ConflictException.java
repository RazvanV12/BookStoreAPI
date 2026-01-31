package com.personal.bookstoreapi.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
}
