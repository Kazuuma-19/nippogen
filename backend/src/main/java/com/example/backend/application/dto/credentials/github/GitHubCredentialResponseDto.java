package com.example.backend.application.dto.credentials.github;

import com.example.backend.domain.credentials.github.GitHubCredential;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "GitHub認証情報レスポンス")
public class GitHubCredentialResponseDto {
    
    @Schema(description = "認証情報ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "ユーザーID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    
    @Schema(description = "マスクされたAPIキー", example = "ghp_**********************xx")
    private String maskedApiKey;
    
    @Schema(description = "GitHub APIベースURL", example = "https://api.github.com")
    private String baseUrl;
    
    @Schema(description = "リポジトリオーナー", example = "octocat")
    private String owner;
    
    @Schema(description = "リポジトリ名", example = "Hello-World")
    private String repo;
    
    @Schema(description = "完全なリポジトリ名", example = "octocat/Hello-World")
    private String fullRepoName;
    
    @Schema(description = "アクティブ状態", example = "true")
    private boolean isActive;
    
    @Schema(description = "作成日時", example = "2023-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新日時", example = "2023-01-01T00:00:00")
    private LocalDateTime updatedAt;
    
    public static GitHubCredentialResponseDto from(GitHubCredential credential) {
        return GitHubCredentialResponseDto.builder()
                .id(credential.getId())
                .userId(credential.getUserId())
                .maskedApiKey(maskApiKey(credential.getApiKey()))
                .baseUrl(credential.getBaseUrl())
                .owner(credential.getOwner())
                .repo(credential.getRepo())
                .fullRepoName(credential.getFullRepoName())
                .isActive(credential.isActive())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
    
    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 6) {
            return "****";
        }
        
        String prefix = apiKey.substring(0, 4);
        String suffix = apiKey.substring(apiKey.length() - 2);
        int maskedLength = apiKey.length() - 6;
        
        return prefix + "*".repeat(maskedLength) + suffix;
    }
}