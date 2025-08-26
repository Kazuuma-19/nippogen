package com.example.backend.common.exceptions;

import lombok.Getter;

@Getter
public class GitHubMcpException extends RuntimeException {
    
    private final String errorCode;
    private final int statusCode;
    
    public GitHubMcpException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public GitHubMcpException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public static GitHubMcpException connectionFailed(String reason) {
        return new GitHubMcpException("GitHub MCP connection failed: " + reason, "CONNECTION_FAILED", 500);
    }
    
    public static GitHubMcpException repositoryNotFound(String owner, String repo) {
        return new GitHubMcpException("Repository not found: " + owner + "/" + repo, "REPOSITORY_NOT_FOUND", 404);
    }
    
    public static GitHubMcpException rateLimitExceeded(String resetTime) {
        return new GitHubMcpException("Rate limit exceeded. Reset at: " + resetTime, "RATE_LIMIT_EXCEEDED", 429);
    }
    
    public static GitHubMcpException invalidParameters(String parameter) {
        return new GitHubMcpException("Invalid parameter: " + parameter, "INVALID_PARAMETERS", 400);
    }
}