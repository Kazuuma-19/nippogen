package com.example.backend.application.dto.credentials.toggl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Toggl Track認証情報更新リクエスト")
public class TogglCredentialUpdateRequestDto {
    
    @Schema(description = "Toggl Track APIキー", example = "1234567890abcdef")
    private String apiKey;
    
    @Schema(description = "ワークスペースID", example = "12345")
    private Long workspaceId;
    
    @Schema(description = "プロジェクトID一覧", example = "[12345, 67890]")
    private List<Integer> projectIds;
    
    @Schema(description = "デフォルトタグ一覧", example = "[\"development\", \"backend\"]")
    private List<String> defaultTags;
    
    @Schema(description = "タイムゾーン", example = "Asia/Tokyo")
    private String timeZone;
    
    @Schema(description = "週末を含むかどうか", example = "false")
    private Boolean includeWeekends;
    
    @Schema(description = "アクティブ状態", example = "true")
    private Boolean isActive;
}