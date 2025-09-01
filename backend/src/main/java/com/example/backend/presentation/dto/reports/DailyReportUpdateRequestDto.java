package com.example.backend.presentation.dto.reports;

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
    
    
    
}
