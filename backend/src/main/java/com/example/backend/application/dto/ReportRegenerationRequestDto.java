package com.example.backend.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

/**
 * 日報再生成リクエストDTO  
 * 既存日報の再生成時のリクエスト情報を転送するためのDTO
 */
@Getter
@Builder
public class ReportRegenerationRequestDto {
    
    private final UUID reportId;
    private final String userFeedback;
    private final String additionalNotes;
    
    /**
     * 必須項目の検証
     * 
     * @return すべての必須項目が設定されている場合true
     */
    public boolean isValid() {
        return reportId != null;
    }
    
    /**
     * ユーザーフィードバックが存在するかチェック
     * 
     * @return ユーザーフィードバックが存在する場合true
     */
    public boolean hasUserFeedback() {
        return userFeedback != null && !userFeedback.trim().isEmpty();
    }
    
    /**
     * 追加情報が存在するかチェック
     * 
     * @return 追加情報が存在する場合true
     */
    public boolean hasAdditionalNotes() {
        return additionalNotes != null && !additionalNotes.trim().isEmpty();
    }
}