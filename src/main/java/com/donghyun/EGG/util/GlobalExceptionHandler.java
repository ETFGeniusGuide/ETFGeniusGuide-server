package com.donghyun.EGG.util;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<?> handleBiz(BizException e) {
        return ResponseEntity.status(statusOf(e.getCode()))
                .body(Map.of("code", e.getCode(), "message", e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("code","BAD_REQUEST","message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknown(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code","INTERNAL_ERROR","message", e.getMessage()));
    }

    private HttpStatus statusOf(String code) {
        return switch (code) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "INVALID" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
