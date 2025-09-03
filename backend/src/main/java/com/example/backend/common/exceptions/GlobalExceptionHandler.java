package com.example.backend.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReportNotFound(ReportNotFoundException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "REPORT_NOT_FOUND");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", 404);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(ReportValidationException.class)
    public ResponseEntity<Map<String, Object>> handleReportValidation(ReportValidationException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "REPORT_VALIDATION_ERROR");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", 400);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleReportGeneration(ReportGenerationException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "REPORT_GENERATION_ERROR");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", 500);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INVALID_PARAMETER_TYPE");
        errorResponse.put("message", "Invalid parameter type for: " + e.getName());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", 400);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INTERNAL_SERVER_ERROR");
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", 500);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}