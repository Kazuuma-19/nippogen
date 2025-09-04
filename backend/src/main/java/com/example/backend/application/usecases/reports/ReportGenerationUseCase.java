package com.example.backend.application.usecases.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.common.util.DailyReportMapper;
import com.example.backend.presentation.dto.reports.DailyReportUpdateRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.presentation.dto.reports.ReportRegenerationRequestDto;
import com.example.backend.domain.reports.IReportGenerationService;
import com.example.backend.domain.reports.DailyReport;
import com.example.backend.domain.reports.IDailyReportRepository;
import com.example.backend.infrastructure.github.GitHubApiService;
import com.example.backend.infrastructure.toggl.TogglApiService;
import com.example.backend.infrastructure.notion.NotionApiService;
import com.example.backend.domain.credentials.github.IGitHubCredentialRepository;
import com.example.backend.domain.credentials.toggl.ITogglCredentialRepository;
import com.example.backend.domain.credentials.notion.INotionCredentialRepository;
import com.example.backend.infrastructure.github.dto.GitHubCommitDto;
import com.example.backend.infrastructure.toggl.dto.TogglTimeEntryDto;
import com.example.backend.infrastructure.notion.dto.NotionPageDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    
    // 外部API統合のための依存関係
    private final GitHubApiService gitHubApiService;
    private final TogglApiService togglApiService;
    private final NotionApiService notionApiService;
    private final IGitHubCredentialRepository gitHubCredentialRepository;
    private final ITogglCredentialRepository togglCredentialRepository;
    private final INotionCredentialRepository notionCredentialRepository;
    private final ObjectMapper objectMapper;
    
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
                    .additionalNotes(request.getAdditionalNotes())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            DailyReport savedReport = dailyReportRepository.save(report);
            DailyReportDto createdReport = dailyReportMapper.toDto(savedReport);
            
            
            return ReportGenerationResponseDto.success(
                createdReport.getId(),
                createdReport.getUserId(),
                createdReport.getReportDate(),
                createdReport.getFinalContent()
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
                updatedReport.getFinalContent()
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
            // アクティブなGitHub認証情報を取得
            var activeCredentials = gitHubCredentialRepository.findActiveByUserId(userId);
            
            if (activeCredentials.isEmpty()) {
                return "{\"error\": \"アクティブなGitHub認証情報がありません\"}";
            }
            
            var credential = activeCredentials.get(0);
            
            // GitHub APIから指定日のコミット履歴を取得
            List<GitHubCommitDto> commits = gitHubApiService.getCommitsByDate(credential, date);
            
            // データをJSON形式で構造化
            var githubData = Map.of(
                "source", "GitHub",
                "date", date.toString(),
                "repository", Map.of(
                    "owner", credential.getOwner(),
                    "name", credential.getRepo()
                ),
                "commits", commits.stream().map(commit -> Map.of(
                    "sha", commit.getShortSha(),
                    "message", commit.getCommitMessage(),
                    "author", commit.getAuthorName(),
                    "date", commit.getCommitDate(),
                    "additions", commit.getStats() != null ? commit.getStats().getAdditions() : 0,
                    "deletions", commit.getStats() != null ? commit.getStats().getDeletions() : 0
                )).toList(),
                "summary", Map.of(
                    "totalCommits", commits.size(),
                    "totalAdditions", commits.stream().mapToInt(c -> c.getStats() != null ? c.getStats().getAdditions() : 0).sum(),
                    "totalDeletions", commits.stream().mapToInt(c -> c.getStats() != null ? c.getStats().getDeletions() : 0).sum()
                )
            );
            
            return objectMapper.writeValueAsString(githubData);
            
        } catch (Exception e) {
            return "{\"error\": \"GitHubデータの取得に失敗しました: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Togglからデータを収集  
     */
    private String collectTogglData(UUID userId, LocalDate date) {
        try {
            // アクティブなToggl認証情報を取得
            var activeCredentials = togglCredentialRepository.findActiveByUserId(userId);
            
            if (activeCredentials.isEmpty()) {
                return "{\"error\": \"アクティブなToggl認証情報がありません\"}";
            }
            
            var credential = activeCredentials.get(0);
            
            // Toggl APIから指定日の時間記録を取得
            List<TogglTimeEntryDto> timeEntries = togglApiService.getTimeEntriesByDate(credential, date);
            
            // データをJSON形式で構造化
            var togglData = Map.of(
                "source", "Toggl Track",
                "date", date.toString(),
                "workspaceId", credential.getWorkspaceId() != null ? credential.getWorkspaceId() : 0,
                "timeEntries", timeEntries.stream().map(entry -> Map.of(
                    "id", entry.getId() != null ? entry.getId() : 0,
                    "description", entry.getDescription() != null ? entry.getDescription() : "",
                    "start", entry.getStart() != null ? entry.getStart().toString() : "",
                    "stop", entry.getStop() != null ? entry.getStop().toString() : "",
                    "duration", entry.getDuration() != null ? entry.getDuration() : 0,
                    "durationHours", entry.getDurationHours(),
                    "formattedDuration", entry.getFormattedDuration(),
                    "projectId", entry.getProjectId() != null ? entry.getProjectId() : 0,
                    "tags", entry.getTags() != null ? entry.getTags() : List.of(),
                    "billable", entry.getBillable() != null ? entry.getBillable() : false
                )).toList(),
                "summary", Map.of(
                    "totalEntries", timeEntries.size(),
                    "totalDurationSeconds", timeEntries.stream().mapToLong(e -> e.getDuration() != null ? e.getDuration() : 0L).sum(),
                    "totalHours", timeEntries.stream().mapToDouble(TogglTimeEntryDto::getDurationHours).sum(),
                    "billableEntries", (int) timeEntries.stream().filter(e -> Boolean.TRUE.equals(e.getBillable())).count()
                )
            );
            
            return objectMapper.writeValueAsString(togglData);
            
        } catch (Exception e) {
            return "{\"error\": \"Togglデータの取得に失敗しました: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Notionからデータを収集
     */
    private String collectNotionData(UUID userId, LocalDate date) {
        try {
            // アクティブなNotion認証情報を取得
            var activeCredentials = notionCredentialRepository.findActiveByUserId(userId);
            
            if (activeCredentials.isEmpty()) {
                return "{\"error\": \"アクティブなNotion認証情報がありません\"}";
            }
            
            var credential = activeCredentials.get(0);
            
            // Notion APIから指定日のページ情報を取得
            // データベースIDが設定されている場合はそのデータベースから取得
            List<NotionPageDto> pages;
            if (credential.getDatabaseIds() != null && !credential.getDatabaseIds().isEmpty()) {
                // 設定されたデータベースから取得
                String databaseId = credential.getDatabaseIds().get(0);
                pages = notionApiService.getPagesByDatabase(credential, databaseId, date);
            } else {
                // 全体検索で日付に関連するページを取得
                String query = date.toString(); // 日付をクエリとして使用
                pages = notionApiService.searchPages(credential, query);
            }
            
            // データをJSON形式で構造化
            var notionData = Map.of(
                "source", "Notion",
                "date", date.toString(),
                "workspaceId", credential.getWorkspaceId() != null ? credential.getWorkspaceId() : "",
                "databaseIds", credential.getDatabaseIds() != null ? credential.getDatabaseIds() : List.of(),
                "pages", pages.stream().map(page -> Map.of(
                    "id", page.getId() != null ? page.getId() : "",
                    "title", page.getTitle() != null ? page.getTitle() : "",
                    "url", page.getUrl() != null ? page.getUrl() : "",
                    "createdTime", page.getCreatedTime() != null ? page.getCreatedTime().toString() : "",
                    "lastEditedTime", page.getLastEditedTime() != null ? page.getLastEditedTime().toString() : "",
                    "archived", page.getArchived() != null ? page.getArchived() : false,
                    "createdBy", page.getCreatedBy() != null && page.getCreatedBy().getName() != null ? 
                        page.getCreatedBy().getName() : "",
                    "parentType", page.getParent() != null && page.getParent().getType() != null ? 
                        page.getParent().getType() : ""
                )).toList(),
                "summary", Map.of(
                    "totalPages", pages.size(),
                    "createdToday", (int) pages.stream()
                        .filter(p -> p.getCreatedTime() != null && 
                            p.getCreatedTime().toLocalDate().equals(date))
                        .count(),
                    "editedToday", (int) pages.stream()
                        .filter(p -> p.getLastEditedTime() != null && 
                            p.getLastEditedTime().toLocalDate().equals(date))
                        .count()
                )
            );
            
            return objectMapper.writeValueAsString(notionData);
            
        } catch (Exception e) {
            return "{\"error\": \"Notionデータの取得に失敗しました: " + e.getMessage() + "\"}";
        }
    }
    
}
