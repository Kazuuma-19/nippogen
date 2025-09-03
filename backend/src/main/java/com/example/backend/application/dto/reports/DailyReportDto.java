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
    private final String finalContent;
    private final String additionalNotes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    
}
