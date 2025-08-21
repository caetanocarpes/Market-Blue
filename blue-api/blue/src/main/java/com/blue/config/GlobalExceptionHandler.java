package com.blue.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> handleRse(ResponseStatusException ex){
        return body(ex.getStatusCode(), ex.getReason(), null);
    }

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<Map<String,Object>> handleAuth(Exception ex){
        return body(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var e : ex.getBindingResult().getAllErrors()) {
            String field = e instanceof FieldError fe ? fe.getField() : e.getObjectName();
            errors.put(field, e.getDefaultMessage());
        }
        return body(HttpStatus.BAD_REQUEST, "Erro de validação", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(String.valueOf(v.getPropertyPath()), v.getMessage())
        );
        return body(HttpStatus.BAD_REQUEST, "Erro de validação", errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return body(HttpStatus.CONFLICT, "Violação de integridade de dados", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleDenied(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "Acesso negado", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", null);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String msg, Object details) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", OffsetDateTime.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", msg);
        if (details != null) m.put("details", details);
        return ResponseEntity.status(status).body(m);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatusCode status, String msg, Object details) {
        return body(HttpStatus.valueOf(status.value()), msg, details);
    }
}
