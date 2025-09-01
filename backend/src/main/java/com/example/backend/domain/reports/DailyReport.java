package com.example.backend.domain.reports;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 日報ドメインエンティティ
 * 日報の情報とビジネスロジックを表現する
 */
@Getter
@Builder(toBuilder = true)
public class DailyReport {
    
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
     * 生成されたコンテンツを持っているかチェック
     * 
     * @return 生成されたコンテンツがある場合true
     */
    public boolean hasGeneratedContent() {
        return generatedContent != null && !generatedContent.trim().isEmpty();
    }
    
    /**
     * 編集されたコンテンツを持っているかチェック
     * 
     * @return 編集されたコンテンツがある場合true
     */
    public boolean hasEditedContent() {
        return editedContent != null && !editedContent.trim().isEmpty();
    }
    
    /**
     * 最終コンテンツを持っているかチェック
     * 
     * @return 最終コンテンツがある場合true
     */
    public boolean hasFinalContent() {
        return finalContent != null && !finalContent.trim().isEmpty();
    }
    
    
    /**
     * 表示用のコンテンツを取得
     * 優先順位: finalContent > editedContent > generatedContent
     * 
     * @return 表示すべきコンテンツ
     */
    public String getDisplayContent() {
        if (hasFinalContent()) {
            return finalContent;
        }
        if (hasEditedContent()) {
            return editedContent;
        }
        if (hasGeneratedContent()) {
            return generatedContent;
        }
        return "";
    }
    
    
    
    
    
}
