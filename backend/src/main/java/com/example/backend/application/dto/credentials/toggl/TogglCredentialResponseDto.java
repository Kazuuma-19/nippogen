package com.example.backend.application.dto.credentials.toggl;

import com.example.backend.domain.credentials.toggl.TogglCredential;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Toggl Track認証情報レスポンス")
public class TogglCredentialResponseDto {
    
    @Schema(description = "認証情報ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "ユーザーID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    
    @Schema(description = "マスクされたAPIキー", example = "1234****cdef")
    private String maskedApiKey;
    
    @Schema(description = "ワークスペースID", example = "12345")
    private Long workspaceId;
    
    @Schema(description = "プロジェクトID一覧", example = "[12345, 67890]")
    private List<Integer> projectIds;
    
    @Schema(description = "デフォルトタグ一覧", example = "[\"development\", \"backend\"]")
    private List<String> defaultTags;
    
    @Schema(description = "タイムゾーン", example = "Asia/Tokyo")
    private String timeZone;
    
    @Schema(description = "週末を含むかどうか", example = "false")
    private boolean includeWeekends;
    
    @Schema(description = "プロジェクト数", example = "2")
    private int projectCount;
    
    @Schema(description = "タグ数", example = "2")
    private int tagCount;
    
    @Schema(description = "アクティブ状態", example = "true")
    private boolean isActive;
    
    @Schema(description = "作成日時", example = "2023-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新日時", example = "2023-01-01T00:00:00")
    private LocalDateTime updatedAt;
    
    public static TogglCredentialResponseDto from(TogglCredential credential) {
        return TogglCredentialResponseDto.builder()
                .id(credential.getId())
                .userId(credential.getUserId())
                .maskedApiKey(maskApiKey(credential.getApiKey()))
                .workspaceId(credential.getWorkspaceId())
                .projectIds(credential.getProjectIds())
                .defaultTags(credential.getDefaultTags())
                .timeZone(credential.getTimeZone())
                .includeWeekends(credential.isIncludeWeekends())
                .projectCount(credential.getProjectCount())
                .tagCount(credential.getTagCount())
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
        String suffix = apiKey.substring(apiKey.length() - 4);
        int maskedLength = apiKey.length() - 8;
        
        return prefix + "*".repeat(Math.max(maskedLength, 4)) + suffix;
    }
}