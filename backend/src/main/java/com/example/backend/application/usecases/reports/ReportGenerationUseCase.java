package com.example.backend.application.usecases.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.presentation.dto.reports.DailyReportUpdateRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.presentation.dto.reports.ReportRegenerationRequestDto;
import com.example.backend.domain.reports.IReportGenerationService;
import com.example.backend.domain.reports.DailyReport;
import com.example.backend.domain.reports.IDailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 日報生成ユースケース
 * AI日報生成・再生成のビジネスロジックを実装
 */
@Service
@RequiredArgsConstructor
public class ReportGenerationUseCase {
    
    private final IReportGenerationService reportGenerationService;
    private final ReportUseCase reportUseCase;
    private final IDailyReportRepository dailyReportRepository;
    
    /**
     * 新規日報を生成する
     * 
     * @param request 日報生成リクエスト
     * @return 生成結果レスポンス
     */
    @Transactional
    public ReportGenerationResponseDto generateReport(ReportGenerationRequestDto request) {
        try {
            
            if (!request.isValid()) {
                return ReportGenerationResponseDto.failure(
                    request.getUserId(),
                    request.getReportDate(),
                    "必須項目が不足しています"
                );
            }
            
            // 既存日報の重複チェック
            if (dailyReportRepository.existsByUserIdAndDate(request.getUserId(), request.getReportDate())) {
                return ReportGenerationResponseDto.failure(
                    request.getUserId(),
                    request.getReportDate(),
                    "指定された日付の日報が既に存在しています"
                );
            }
            
            // 3サービスからデータを統合取得
            String githubData = collectGitHubData(request.getUserId(), request.getReportDate());
            String togglData = collectTogglData(request.getUserId(), request.getReportDate());  
            String notionData = collectNotionData(request.getUserId(), request.getReportDate());
            
            // AI日報生成
            String generatedContent = reportGenerationService.generateReport(
                request.getUserId(),
                request.getReportDate(),
                githubData,
                togglData,
                notionData,
                request.getAdditionalNotes()
            );
            
            // 生成された日報をDBに直接保存
            DailyReport report = DailyReport.builder()
                    .id(UUID.randomUUID())
                    .userId(request.getUserId())
                    .reportDate(request.getReportDate())
                    .finalContent(generatedContent)
                    .generationCount(1)
                    .additionalNotes(request.getAdditionalNotes())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            DailyReport savedReport = dailyReportRepository.save(report);
            DailyReportDto createdReport = convertToDto(savedReport);
            
            
            return ReportGenerationResponseDto.success(
                createdReport.getId(),
                createdReport.getUserId(),
                createdReport.getReportDate(),
                createdReport.getFinalContent(),
                createdReport.getGenerationCount()
            );
            
        } catch (Exception e) {
            
            return ReportGenerationResponseDto.failure(
                request.getUserId(),
                request.getReportDate(),
                "日報生成中にエラーが発生しました: " + e.getMessage()
            );
        }
    }
    
    /**
     * 既存日報を再生成する
     * 
     * @param request 再生成リクエスト
     * @return 再生成結果レスポンス
     */
    @Transactional
    public ReportGenerationResponseDto regenerateReport(ReportRegenerationRequestDto request) {
        try {
            
            if (!request.isValid()) {
                return ReportGenerationResponseDto.failure(
                    null, null, "必須項目が不足しています"
                );
            }
            
            // 既存日報の取得
            Optional<DailyReportDto> existingReportOpt = Optional.empty();
            // TODO: reportUseCase.getReportById メソッドの実装が必要
            
            if (existingReportOpt.isEmpty()) {
                return ReportGenerationResponseDto.failure(
                    null, null, "指定された日報が見つかりません"
                );
            }
            
            DailyReportDto existingReport = existingReportOpt.get();
            
            // 3サービスからデータを再取得
            String githubData = collectGitHubData(existingReport.getUserId(), existingReport.getReportDate());
            String togglData = collectTogglData(existingReport.getUserId(), existingReport.getReportDate());
            String notionData = collectNotionData(existingReport.getUserId(), existingReport.getReportDate());
            
            // AI日報再生成
            String regeneratedContent = reportGenerationService.regenerateReport(
                existingReport.getUserId(),
                existingReport.getReportDate(),
                githubData,
                togglData,
                notionData,
                existingReport.getFinalContent(),
                request.getUserFeedback(),
                request.getAdditionalNotes()
            );
            
            // 再生成された日報を更新
            DailyReportUpdateRequestDto updateRequest = DailyReportUpdateRequestDto.builder()
                .finalContent(regeneratedContent)
                .additionalNotes(request.getAdditionalNotes())
                .build();
                
            DailyReportDto updatedReport = reportUseCase.updateReport(request.getReportId(), updateRequest);
            
            
            return ReportGenerationResponseDto.success(
                updatedReport.getId(),
                updatedReport.getUserId(),
                updatedReport.getReportDate(),
                updatedReport.getFinalContent(),
                updatedReport.getGenerationCount()
            );
            
        } catch (Exception e) {
            
            return ReportGenerationResponseDto.failure(
                null, null,
                "日報再生成中にエラーが発生しました: " + e.getMessage()
            );
        }
    }
    
    /**
     * GitHubからデータを収集
     */
    private String collectGitHubData(UUID userId, LocalDate date) {
        try {
            // TODO: GitHub API認証情報の取得とAPIコール実装
            //現在は空データを返す
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Togglからデータを収集  
     */
    private String collectTogglData(UUID userId, LocalDate date) {
        try {
            // TODO: Toggl API認証情報の取得とAPIコール実装
            // 現在は空データを返す
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Notionからデータを収集
     */
    private String collectNotionData(UUID userId, LocalDate date) {
        try {
            // TODO: Notion API認証情報の取得とAPIコール実装
            // 現在は空データを返す
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * DailyReportエンティティをDTOに変換
     * 
     * @param report 日報エンティティ
     * @return 日報DTO
     */
    private DailyReportDto convertToDto(DailyReport report) {
        return DailyReportDto.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .reportDate(report.getReportDate())
                .rawData(report.getRawData())
                .finalContent(report.getFinalContent())
                .generationCount(report.getGenerationCount())
                .additionalNotes(report.getAdditionalNotes())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
