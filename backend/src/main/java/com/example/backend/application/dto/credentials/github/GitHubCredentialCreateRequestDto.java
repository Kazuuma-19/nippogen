package com.example.backend.application.dto.credentials.github;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "GitHub認証情報作成リクエスト")
public class GitHubCredentialCreateRequestDto {
    
    @NotBlank(message = "APIキーは必須です")
    @Schema(description = "GitHub APIキー", example = "ghp_xxxxxxxxxxxxxxxxxxxx", required = true)
    private String apiKey;
    
    @Schema(description = "GitHub APIベースURL", example = "https://api.github.com", defaultValue = "https://api.github.com")
    private String baseUrl;
    
    @Schema(description = "リポジトリオーナー", example = "octocat")
    private String owner;
    
    @Schema(description = "リポジトリ名", example = "Hello-World")
    private String repo;
}