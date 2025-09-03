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
    private final String status;
    private final Integer generationCount;
    private final LocalDateTime generatedAt;
    private final boolean success;
    private final String errorMessage;
    
    /**
     * 生成成功レスポンスを作成
     * 
     * @param reportId 日報ID
     * @param userId ユーザーID
     * @param reportDate 日報対象日
     * @param finalContent 最終コンテンツ
     * @param generationCount 生成回数
     * @return 成功レスポンス
     */
    public static ReportGenerationResponseDto success(
        UUID reportId,
        UUID userId, 
        LocalDate reportDate,
        String finalContent,
        Integer generationCount
    ) {
        return ReportGenerationResponseDto.builder()
            .reportId(reportId)
            .userId(userId)
            .reportDate(reportDate)
            .finalContent(finalContent)
            .status("DRAFT")
            .generationCount(generationCount)
            .generatedAt(LocalDateTime.now())
            .success(true)
            .build();
    }
    
    /**
     * 生成失敗レスポンスを作成
     * 
     * @param userId ユーザーID
     * @param reportDate 日報対象日
     * @param errorMessage エラーメッセージ
     * @return 失敗レスポンス
     */
    public static ReportGenerationResponseDto failure(
        UUID userId,
        LocalDate reportDate, 
        String errorMessage
    ) {
        return ReportGenerationResponseDto.builder()
            .userId(userId)
            .reportDate(reportDate)
            .success(false)
            .errorMessage(errorMessage)
            .generatedAt(LocalDateTime.now())
            .build();
    }
}
