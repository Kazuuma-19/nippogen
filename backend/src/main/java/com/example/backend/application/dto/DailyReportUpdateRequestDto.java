package com.example.backend.application.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 日報更新リクエストDTO
 * 日報更新時のリクエスト情報を転送するためのDTO
 */
@Getter
@Builder
public class DailyReportUpdateRequestDto {
    
    private final String editedContent;
    private final String additionalNotes;
    
    /**
     * 編集されたコンテンツが存在するかチェック
     * 
     * @return 編集されたコンテンツが存在する場合true
     */
    public boolean hasEditedContent() {
        return editedContent != null && !editedContent.trim().isEmpty();
    }
    
    /**
     * 追加メモが存在するかチェック
     * 
     * @return 追加メモが存在する場合true
     */
    public boolean hasAdditionalNotes() {
        return additionalNotes != null && !additionalNotes.trim().isEmpty();
    }
    
    /**
     * 更新すべき内容があるかチェック
     * 
     * @return 更新すべき内容がある場合true
     */
    public boolean hasUpdates() {
        return hasEditedContent() || hasAdditionalNotes();
    }
}