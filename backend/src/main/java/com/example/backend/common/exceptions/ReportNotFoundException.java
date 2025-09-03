package com.example.backend.common.exceptions;

/**
 * 日報が見つからない場合にスローされる例外
 */
public class ReportNotFoundException extends RuntimeException {
    
    public ReportNotFoundException(String message) {
        super(message);
    }
    
    public ReportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}