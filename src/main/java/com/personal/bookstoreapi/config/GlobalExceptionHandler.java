package com.personal.bookstoreapi.config;

import com.personal.bookstoreapi.exception.ApiError;
import com.personal.bookstoreapi.exception.ConflictException;
import com.personal.bookstoreapi.exception.NotFoundException;
import com.personal.bookstoreapi.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - DTO validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult()
                               .getFieldErrors()) {
            fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }

        String traceId = newTraceId();

        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                req.getRequestURI(),
                traceId,
                fieldErrors
        );

        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String traceId = newTraceId();

        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Malformed JSON request",
                req.getRequestURI(),
                traceId
        );

        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String traceId = newTraceId();

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations()
          .forEach(v -> fieldErrors.put(v.getPropertyPath()
                                         .toString(), v.getMessage()));

        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Constraint violation",
                req.getRequestURI(),
                traceId,
                fieldErrors
        );

        return ResponseEntity.badRequest()
                             .body(body);
    }

    // 409 - email already used, etc.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        String traceId = newTraceId();

        ApiError body = ApiError.of(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                req.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(body);
    }

    // 401 - invalid credentials
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        String traceId = newTraceId();

        ApiError body = ApiError.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                req.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(body);
    }

    // 500 - orice altceva
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        String traceId = newTraceId();

        ex.printStackTrace();

        ApiError body = ApiError.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Unexpected error. Use traceId to investigate.",
                req.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        String traceId = UUID.randomUUID()
                             .toString();
        ApiError body = ApiError.of(404, "Not Found", ex.getMessage(), req.getRequestURI(), traceId);
        return ResponseEntity.status(404)
                             .body(body);
    }

    private String newTraceId() {
        return UUID.randomUUID()
                   .toString();
    }
}