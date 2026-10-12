package com.smarttaskmanager.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ IllegalArgumentException.class, org.springframework.security.authentication.BadCredentialsException.class })
    public ResponseEntity<Map<String, Object>> badRequest(RuntimeException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> forbidden(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("timestamp", new Date().toString());
        res.put("status", 400);
        res.put("errors", errors);
        return ResponseEntity.badRequest().body(res);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("timestamp", new Date().toString());
        res.put("status", s.value());
        res.put("message", msg);
        return ResponseEntity.status(s).body(res);
    }
}
