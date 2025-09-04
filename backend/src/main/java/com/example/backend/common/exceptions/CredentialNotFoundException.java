package com.example.backend.common.exceptions;

/**
 * 認証情報が見つからない場合の例外
 */
public class CredentialNotFoundException extends RuntimeException {
    
    public CredentialNotFoundException(String message) {
        super(message);
    }
    
    public CredentialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}