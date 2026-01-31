package com.personal.bookstoreapi.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path, String traceId) {
        return new ApiError(Instant.now(), status, error, message, path, traceId, null);
    }

    public static ApiError of(int status, String error, String message, String path, String traceId,
                              Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, traceId, fieldErrors);
    }
}
