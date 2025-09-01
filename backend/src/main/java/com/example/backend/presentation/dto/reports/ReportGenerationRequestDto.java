package com.example.backend.presentation.dto.reports;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 日報生成リクエストDTO
 * 新規日報生成時のリクエスト情報を転送するためのDTO
 */
@Getter
@Builder
public class ReportGenerationRequestDto {
    
    private final UUID userId;
    private final LocalDate reportDate;
    private final String additionalNotes;
    
    /**
     * 必須項目の検証
     *
     * @return すべての必須項目が設定されている場合true
     */
    public boolean isValid() {
        return userId != null && reportDate != null;
    }
    
}
