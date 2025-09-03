package com.example.backend.common.exceptions;

/**
 * AI日報生成でエラーが発生した場合にスローされる例外
 */
public class ReportGenerationException extends RuntimeException {
    
    public ReportGenerationException(String message) {
        super(message);
    }
    
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}