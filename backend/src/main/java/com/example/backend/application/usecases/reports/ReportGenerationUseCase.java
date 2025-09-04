package com.example.backend.application.usecases.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.common.exceptions.ReportAlreadyExistsException;
import com.example.backend.common.exceptions.ReportNotFoundException;
import com.example.backend.common.exceptions.ReportValidationException;
import com.example.backend.common.util.DailyReportMapper;
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
    private final DailyReportMapper dailyReportMapper;
    
    /**
     * 新規日報を生成する
     * 
     * @param request 日報生成リクエスト
     * @return 生成結果レスポンス
     */
    @Transactional
    public ReportGenerationResponseDto generateReport(ReportGenerationRequestDto request) {
        if (!request.isValid()) {
            throw new ReportValidationException("必須項目が不足しています");
        }
        
        // 既存日報の重複チェック
        if (dailyReportRepository.existsByUserIdAndDate(request.getUserId(), request.getReportDate())) {
            throw new ReportAlreadyExistsException("指定された日付の日報が既に存在しています");
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
                .additionalNotes(request.getAdditionalNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        DailyReport savedReport = dailyReportRepository.save(report);
        DailyReportDto createdReport = dailyReportMapper.toDto(savedReport);
        
        return ReportGenerationResponseDto.builder()
            .reportId(createdReport.getId())
            .userId(createdReport.getUserId())
            .reportDate(createdReport.getReportDate())
            .finalContent(createdReport.getFinalContent())
            .generatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 既存日報を再生成する
     * 
     * @param reportId 日報ID
     * @param userFeedback ユーザーフィードバック
     * @param additionalNotes 追加メモ
     * @return 再生成結果レスポンス
     */
    @Transactional
    public ReportGenerationResponseDto regenerateReport(UUID reportId, String userFeedback, String additionalNotes) {
        // 既存日報の取得
        Optional<DailyReportDto> existingReportOpt = Optional.empty();
        // TODO: reportUseCase.getReportById メソッドの実装が必要
        
        if (existingReportOpt.isEmpty()) {
            throw new ReportNotFoundException("指定された日報が見つかりません");
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
            userFeedback,
            additionalNotes
        );
        
        // 再生成された日報を更新
        DailyReportUpdateRequestDto updateRequest = DailyReportUpdateRequestDto.builder()
            .finalContent(regeneratedContent)
            .additionalNotes(additionalNotes)
            .build();
            
        DailyReportDto updatedReport = reportUseCase.updateReport(reportId, updateRequest);
        
        return ReportGenerationResponseDto.builder()
            .reportId(updatedReport.getId())
            .userId(updatedReport.getUserId())
            .reportDate(updatedReport.getReportDate())
            .finalContent(updatedReport.getFinalContent())
            .generatedAt(LocalDateTime.now())
            .build();
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
    
}
