package com.freemarket.delivery_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {

        String message = ex.getMessage();
        HttpStatus status;

        if (message != null && (
            message.contains("ya está cancelado") ||
            message.contains("ya entregado") ||
            message.contains("Estado final")
        )) {
            status = HttpStatus.CONFLICT; 
        } else if (message != null && message.contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message != null && (
            message.contains("No puedes volver") ||
            message.contains("inválido") ||
            message.contains("No se puede")
        )) {
            status = HttpStatus.BAD_REQUEST; 
        } else {
            status = HttpStatus.BAD_REQUEST; 
        }

        return ResponseEntity.status(status).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    status.value(),
            "error",     status.getReasonPhrase(),
            "message",   message != null ? message : "Error desconocido"
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {

        String mensaje = String.format(
            "Valor '%s' no válido para '%s'. Valores aceptados: PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO",
            ex.getValue(), ex.getName()
        );

        return ResponseEntity.badRequest().body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    400,
            "error",     "Bad Request",
            "message",   mensaje
        ));
    }

   

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    ex.printStackTrace(); // ✅ para ver el error real en consola
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status",    500,
        "error",     "Internal Server Error",
        "message",   ex.getMessage() != null ? ex.getMessage() : "Error inesperado" 
    ));
}
}