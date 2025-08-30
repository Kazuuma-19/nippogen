package com.example.backend.application.dto.credentials.notion;

import com.example.backend.domain.credentials.notion.NotionCredential;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Notion認証情報レスポンス")
public class NotionCredentialResponseDto {
    
    @Schema(description = "認証情報ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "ユーザーID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    
    @Schema(description = "マスクされたAPIキー", example = "secret_**********************xx")
    private String maskedApiKey;
    
    @Schema(description = "データベースID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String databaseId;
    
    @Schema(description = "タイトルプロパティ名", example = "Name")
    private String titleProperty;
    
    @Schema(description = "ステータスプロパティ名", example = "Status")
    private String statusProperty;
    
    @Schema(description = "日付プロパティ名", example = "Date")
    private String dateProperty;
    
    @Schema(description = "フィルター条件", example = "{\"status\": [\"In Progress\", \"Done\"]}")
    private Map<String, Object> filterConditions;
    
    @Schema(description = "完全設定済みかどうか", example = "true")
    private boolean isFullyConfigured;
    
    @Schema(description = "アクティブ状態", example = "true")
    private boolean isActive;
    
    @Schema(description = "作成日時", example = "2023-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新日時", example = "2023-01-01T00:00:00")
    private LocalDateTime updatedAt;
    
    public static NotionCredentialResponseDto from(NotionCredential credential) {
        return NotionCredentialResponseDto.builder()
                .id(credential.getId())
                .userId(credential.getUserId())
                .maskedApiKey(maskApiKey(credential.getApiKey()))
                .databaseId(credential.getDatabaseId())
                .titleProperty(credential.getTitleProperty())
                .statusProperty(credential.getStatusProperty())
                .dateProperty(credential.getDateProperty())
                .filterConditions(credential.getFilterConditions())
                .isFullyConfigured(credential.isFullyConfigured())
                .isActive(credential.isActive())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
    
    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "****";
        }
        
        String prefix = apiKey.substring(0, 7);  // "secret_"
        String suffix = apiKey.substring(apiKey.length() - 2);
        int maskedLength = apiKey.length() - 9;
        
        return prefix + "*".repeat(Math.max(maskedLength, 4)) + suffix;
    }
}