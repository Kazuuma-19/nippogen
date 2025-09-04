package com.example.backend.presentation.dto.reports;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 日報生成レスポンスDTO
 * 日報生成結果を返すためのDTO
 */
@Getter
@Builder
public class ReportGenerationResponseDto {
    
    private final UUID reportId;
    private final UUID userId;
    private final LocalDate reportDate;
    private final String finalContent;
    private final LocalDateTime generatedAt;
}
