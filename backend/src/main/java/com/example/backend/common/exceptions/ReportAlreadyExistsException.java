package com.example.backend.common.exceptions;

/**
 * レポートが既に存在する場合の例外
 */
public class ReportAlreadyExistsException extends RuntimeException {
    
    public ReportAlreadyExistsException(String message) {
        super(message);
    }
    
    public ReportAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}