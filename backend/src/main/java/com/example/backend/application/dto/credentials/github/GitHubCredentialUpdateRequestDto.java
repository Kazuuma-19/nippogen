package com.example.backend.application.dto.credentials.github;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GitHub認証情報更新リクエスト")
public class GitHubCredentialUpdateRequestDto {
    
    @Schema(description = "GitHub APIキー", example = "ghp_xxxxxxxxxxxxxxxxxxxx")
    private String apiKey;
    
    @Schema(description = "GitHub APIベースURL", example = "https://api.github.com")
    private String baseUrl;
    
    @Schema(description = "リポジトリオーナー", example = "octocat")
    private String owner;
    
    @Schema(description = "リポジトリ名", example = "Hello-World")
    private String repo;
    
    @Schema(description = "アクティブ状態", example = "true")
    private Boolean isActive;
}