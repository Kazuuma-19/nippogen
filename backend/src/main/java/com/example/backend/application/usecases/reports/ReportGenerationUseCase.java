package com.example.backend.application.usecases.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.presentation.controllers.reports.DailyReportCreateRequestDto;
import com.example.backend.presentation.controllers.reports.DailyReportUpdateRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.presentation.dto.reports.ReportRegenerationRequestDto;
import com.example.backend.application.usecases.external.github.GitHubUseCase;
import com.example.backend.application.usecases.external.notion.NotionUseCase;
import com.example.backend.application.usecases.external.toggle.ToggleTrackUseCase;
import com.example.backend.domain.reports.IReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * 日報生成ユースケース
 * AI日報生成・再生成のビジネスロジックを実装
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationUseCase {
    
    private final IReportGenerationService reportGenerationService;
    private final GitHubUseCase gitHubUseCase;
    private final ToggleTrackUseCase toggleTrackUseCase;
    private final NotionUseCase notionUseCase;
    private final ReportUseCase reportUseCase;
    
    /**
     * 新規日報を生成する
     * 
     * @param request 日報生成リクエスト
     * @return 生成結果レスポンス
     */
    @Transactional
    public ReportGenerationResponseDto generateReport(ReportGenerationRequestDto request) {
        try {
            log.info("日報生成開始 - ユーザーID: {}, 対象日: {}", request.getUserId(), request.getReportDate());
            
            if (!request.isValid()) {
                return ReportGenerationResponseDto.failure(
                    request.getUserId(),
                    request.getReportDate(),
                    "必須項目が不足しています"
                );
            }
            
            // 既存日報の重複チェック
            Optional<DailyReportDto> existingReport = reportUseCase.getReportByDate(
                request.getUserId(), 
                request.getReportDate()
            );
            
            if (existingReport.isPresent()) {
                return ReportGenerationResponseDto.failure(
                    request.getUserId(),
                    request.getReportDate(),
                    "指定日の日報は既に存在します"
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
            
            // 生成された日報をDBに保存
            DailyReportCreateRequestDto createRequest = DailyReportCreateRequestDto.builder()
                .userId(request.getUserId())
                .reportDate(request.getReportDate())
                .generatedContent(generatedContent)
                .additionalNotes(request.getAdditionalNotes())
                .build();
                
            DailyReportDto createdReport = reportUseCase.createReport(createRequest);
            
            log.info("日報生成完了 - 日報ID: {}", createdReport.getId());
            
            return ReportGenerationResponseDto.success(
                createdReport.getId(),
                createdReport.getUserId(),
                createdReport.getReportDate(),
                createdReport.getGeneratedContent(),
                createdReport.getGenerationCount()
            );
            
        } catch (Exception e) {
            log.error("日報生成エラー - ユーザーID: {}, 対象日: {}", 
                request.getUserId(), request.getReportDate(), e);
            
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
            log.info("日報再生成開始 - 日報ID: {}", request.getReportId());
            
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
                existingReport.getGeneratedContent(),
                request.getUserFeedback(),
                request.getAdditionalNotes()
            );
            
            // 再生成された日報を更新
            DailyReportUpdateRequestDto updateRequest = DailyReportUpdateRequestDto.builder()
                .editedContent(regeneratedContent)
                .additionalNotes(request.getAdditionalNotes())
                .build();
                
            DailyReportDto updatedReport = reportUseCase.updateReport(request.getReportId(), updateRequest);
            
            log.info("日報再生成完了 - 日報ID: {}", updatedReport.getId());
            
            return ReportGenerationResponseDto.success(
                updatedReport.getId(),
                updatedReport.getUserId(),
                updatedReport.getReportDate(),
                updatedReport.getEditedContent(),
                updatedReport.getGenerationCount()
            );
            
        } catch (Exception e) {
            log.error("日報再生成エラー - 日報ID: {}", request.getReportId(), e);
            
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
            log.warn("GitHubデータ取得エラー - ユーザーID: {}, 日付: {}", userId, date, e);
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
            log.warn("Togglデータ取得エラー - ユーザーID: {}, 日付: {}", userId, date, e);
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
            log.warn("Notionデータ取得エラー - ユーザーID: {}, 日付: {}", userId, date, e);
            return "{}";
        }
    }
}
