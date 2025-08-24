package com.example.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mcp.github")
@Getter
@Setter
public class McpConfig {
    
    /**
     * GitHub MCP server executable path
     * GitHub公式MCP Server: https://github.com/github/github-mcp-server
     */
    private String serverExecutable = "npx -y @modelcontextprotocol/server-github";
    
    /**
     * GitHub Personal Access Token
     */
    private String githubToken;
    
    /**
     * MCP transport type (stdio or sse)
     */
    private String transport = "stdio";
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 10000;
    
    /**
     * Read timeout in milliseconds
     */
    private int readTimeout = 30000;
    
    /**
     * Max retry attempts
     */
    private int maxRetries = 3;
    
    /**
     * Retry delay in milliseconds
     */
    private int retryDelay = 2000;
}