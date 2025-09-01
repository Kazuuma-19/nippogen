package com.example.backend.application.dto.reports;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 日報DTO
 * 日報の基本情報を転送するためのDTO
 */
@Getter
@Builder
public class DailyReportDto {
    
    private final UUID id;
    private final UUID userId;
    private final LocalDate reportDate;
    private final String rawData;
    private final String generatedContent;
    private final String editedContent;
    private final String finalContent;
    private final Integer generationCount;
    private final String additionalNotes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * 表示用コンテンツを取得
     * 優先順位: finalContent > editedContent > generatedContent
     * 
     * @return 表示すべきコンテンツ
     */
    public String getDisplayContent() {
        if (finalContent != null && !finalContent.trim().isEmpty()) {
            return finalContent;
        }
        if (editedContent != null && !editedContent.trim().isEmpty()) {
            return editedContent;
        }
        if (generatedContent != null && !generatedContent.trim().isEmpty()) {
            return generatedContent;
        }
        return "";
    }
    
    /**
     * コンテンツが存在するかチェック
     * 
     * @return 何らかのコンテンツが存在する場合true
     */
    public boolean hasContent() {
        return !getDisplayContent().isEmpty();
    }
}
