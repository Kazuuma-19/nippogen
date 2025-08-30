package com.example.backend.application.dto.credentials.notion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Notion認証情報更新リクエスト")
public class NotionCredentialUpdateRequestDto {
    
    @Schema(description = "Notion APIキー", example = "secret_xxxxxxxxxxxxxxxxxxxx")
    private String apiKey;
    
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
    
    @Schema(description = "アクティブ状態", example = "true")
    private Boolean isActive;
}