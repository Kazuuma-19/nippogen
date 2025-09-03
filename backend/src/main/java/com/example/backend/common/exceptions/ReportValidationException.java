package com.example.backend.common.exceptions;

/**
 * 日報のバリデーションエラーが発生した場合にスローされる例外
 */
public class ReportValidationException extends RuntimeException {
    
    public ReportValidationException(String message) {
        super(message);
    }
    
    public ReportValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}