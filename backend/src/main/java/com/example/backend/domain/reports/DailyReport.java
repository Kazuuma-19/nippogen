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
    private final String finalContent;
    private final String additionalNotes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    
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
     * 
     * @return 表示すべきコンテンツ
     */
    public String getDisplayContent() {
        return hasFinalContent() ? finalContent : "";
    }
    
    
    
    
    
}
