package com.example.backend.application.dto.credentials.toggl;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Toggl Track認証情報作成リクエスト")
public class TogglCredentialCreateRequestDto {
    
    @NotBlank(message = "APIキーは必須です")
    @Schema(description = "Toggl Track APIキー", example = "1234567890abcdef", required = true)
    private String apiKey;
    
    @Schema(description = "ワークスペースID", example = "12345")
    private Long workspaceId;
    
    @Schema(description = "プロジェクトID一覧", example = "[12345, 67890]")
    private List<Integer> projectIds;
    
    @Schema(description = "デフォルトタグ一覧", example = "[\"development\", \"backend\"]")
    private List<String> defaultTags;
    
    @Schema(description = "タイムゾーン", example = "Asia/Tokyo", defaultValue = "UTC")
    private String timeZone;
    
    @Schema(description = "週末を含むかどうか", example = "false", defaultValue = "false")
    private Boolean includeWeekends;
}