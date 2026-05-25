package com.freemarket.delivery_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        HttpStatus status;

        if (message != null && (
            message.contains("already cancelled") ||
            message.contains("already delivered") ||
            message.contains("final status")
        )) {
            status = HttpStatus.CONFLICT;
        } else if (message != null && (
            message.contains("cannot go back") ||
            message.contains("invalid") ||
            message.contains("Cannot cancel")
        )) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    status.value(),
            "error",     status.getReasonPhrase(),
            "message",   message != null ? message : "Unknown error"
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    404,
            "error",     "Not Found",
            "message",   ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
            "Invalid value '%s' for '%s'. Accepted values: PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO",
            ex.getValue(), ex.getName()
        );
        return ResponseEntity.badRequest().body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    400,
            "error",     "Bad Request",
            "message",   message
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    500,
            "error",     "Internal Server Error",
            "message",   ex.getMessage() != null ? ex.getMessage() : "Unexpected error"
        ));
    }
}