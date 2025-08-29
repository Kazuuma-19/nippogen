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
    private final ReportStatus status;
    private final Integer generationCount;
    private final String additionalNotes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * ドラフト状態かチェック
     * 
     * @return ドラフト状態の場合true
     */
    public boolean isDraft() {
        return status != null && status.isDraft();
    }
    
    /**
     * 編集済み状態かチェック
     * 
     * @return 編集済み状態の場合true
     */
    public boolean isEdited() {
        return status != null && status.isEdited();
    }
    
    /**
     * 承認済み状態かチェック
     * 
     * @return 承認済み状態の場合true
     */
    public boolean isApproved() {
        return status != null && status.isApproved();
    }
    
    /**
     * 編集可能な状態かチェック
     * 
     * @return 編集可能な場合true（DRAFT または EDITED）
     */
    public boolean isEditable() {
        return status != null && status.isEditable();
    }
    
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
     * 生データを持っているかチェック
     * 
     * @return 生データがある場合true
     */
    public boolean hasRawData() {
        return rawData != null && !rawData.trim().isEmpty();
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
    
    /**
     * 日報が今日のものかチェック
     * 
     * @return 今日の日報の場合true
     */
    public boolean isToday() {
        return reportDate != null && reportDate.equals(LocalDate.now());
    }
    
    /**
     * 日報が指定した日付範囲内にあるかチェック
     * 
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 範囲内にある場合true
     */
    public boolean isWithinDateRange(LocalDate startDate, LocalDate endDate) {
        if (reportDate == null) {
            return false;
        }
        return !reportDate.isBefore(startDate) && !reportDate.isAfter(endDate);
    }
    
    /**
     * 追加メモを持っているかチェック
     * 
     * @return 追加メモがある場合true
     */
    public boolean hasAdditionalNotes() {
        return additionalNotes != null && !additionalNotes.trim().isEmpty();
    }
    
    /**
     * 生成回数が0より大きいかチェック
     * 
     * @return 生成回数がある場合true
     */
    public boolean hasGenerationHistory() {
        return generationCount != null && generationCount > 0;
    }
    
    /**
     * 最近更新されたかチェック
     * 
     * @param hours 時間数
     * @return 指定時間以内に更新された場合true
     */
    public boolean isRecentlyUpdated(int hours) {
        if (updatedAt == null) {
            return false;
        }
        return updatedAt.isAfter(LocalDateTime.now().minusHours(hours));
    }
}
