package com.example.backend.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 日報作成リクエストDTO
 * 日報作成時のリクエスト情報を転送するためのDTO
 */
@Getter
@Builder
public class DailyReportCreateRequestDto {
    
    private final UUID userId;
    private final LocalDate reportDate;
    private final String rawData;
    private final String generatedContent;
    private final String additionalNotes;
    
    /**
     * 必須項目の検証
     * 
     * @return すべての必須項目が設定されている場合true
     */
    public boolean isValid() {
        return userId != null && reportDate != null;
    }
    
    /**
     * 生データが存在するかチェック
     * 
     * @return 生データが存在する場合true
     */
    public boolean hasRawData() {
        return rawData != null && !rawData.trim().isEmpty();
    }
    
    /**
     * 生成されたコンテンツが存在するかチェック
     * 
     * @return 生成されたコンテンツが存在する場合true
     */
    public boolean hasGeneratedContent() {
        return generatedContent != null && !generatedContent.trim().isEmpty();
    }
}