package com.example.backend.application.usecases;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.application.usecases.external.github.GitHubUseCase;
import com.example.backend.application.usecases.external.notion.NotionUseCase;
import com.example.backend.application.usecases.external.toggle.ToggleTrackUseCase;
import com.example.backend.presentation.controllers.reports.DailyReportCreateRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.application.usecases.reports.ReportGenerationUseCase;
import com.example.backend.application.usecases.reports.ReportUseCase;
import com.example.backend.domain.reports.IReportGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportGenerationUseCase テスト")
class ReportGenerationUseCaseTest {
    
    @Mock
    private IReportGenerationService reportGenerationService;
    
    @Mock
    private GitHubUseCase gitHubUseCase;
    
    @Mock
    private ToggleTrackUseCase toggleTrackUseCase;
    
    @Mock
    private NotionUseCase notionUseCase;
    
    @Mock
    private ReportUseCase reportUseCase;
    
    private ReportGenerationUseCase reportGenerationUseCase;
    
    @BeforeEach
    void setUp() {
        reportGenerationUseCase = new ReportGenerationUseCase(
            reportGenerationService,
            gitHubUseCase,
            toggleTrackUseCase, 
            notionUseCase,
            reportUseCase
        );
    }
    
    @Test
    @DisplayName("正常な日報生成テスト")
    void generateReport_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        
        ReportGenerationRequestDto request = ReportGenerationRequestDto.builder()
            .userId(userId)
            .reportDate(reportDate)
            .additionalNotes("テスト用追加メモ")
            .build();
        
        // Mocking
        when(reportUseCase.getReportByDate(userId, reportDate))
            .thenReturn(Optional.empty());
        
        when(reportGenerationService.generateReport(
            eq(userId), eq(reportDate), any(), any(), any(), any()
        )).thenReturn("生成された日報コンテンツ");
        
        UUID reportId = UUID.randomUUID();
        DailyReportDto createdReport = DailyReportDto.builder()
            .id(reportId)
            .userId(userId)
            .reportDate(reportDate)
            .generatedContent("生成された日報コンテンツ")
            .generationCount(1)
            .build();
        
        when(reportUseCase.createReport(any(DailyReportCreateRequestDto.class)))
            .thenReturn(createdReport);
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getReportId()).isEqualTo(reportId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getReportDate()).isEqualTo(reportDate);
        assertThat(response.getGeneratedContent()).isEqualTo("生成された日報コンテンツ");
        assertThat(response.getGenerationCount()).isEqualTo(1);
        
        // Verify
        verify(reportGenerationService, times(1)).generateReport(
            eq(userId), eq(reportDate), any(), any(), any(), eq("テスト用追加メモ")
        );
        verify(reportUseCase, times(1)).createReport(any(DailyReportCreateRequestDto.class));
    }
    
    @Test
    @DisplayName("既存日報重複エラーテスト")
    void generateReport_DuplicateReport() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        
        ReportGenerationRequestDto request = ReportGenerationRequestDto.builder()
            .userId(userId)
            .reportDate(reportDate)
            .build();
        
        // 既存日報が存在する場合
        DailyReportDto existingReport = DailyReportDto.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .reportDate(reportDate)
            .build();
        
        when(reportUseCase.getReportByDate(userId, reportDate))
            .thenReturn(Optional.of(existingReport));
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("既に存在します");
        
        // Verify - AI生成は呼ばれないはず
        verify(reportGenerationService, never()).generateReport(any(), any(), any(), any(), any(), any());
        verify(reportUseCase, never()).createReport(any());
    }
    
    @Test
    @DisplayName("無効なリクエストテスト")
    void generateReport_InvalidRequest() {
        // Given - userIdがnullの無効なリクエスト
        ReportGenerationRequestDto request = ReportGenerationRequestDto.builder()
            .userId(null)
            .reportDate(LocalDate.of(2024, 1, 15))
            .build();
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("必須項目が不足");
        
        // Verify - 何も呼ばれないはず
        verify(reportUseCase, never()).getReportByDate(any(), any());
        verify(reportGenerationService, never()).generateReport(any(), any(), any(), any(), any(), any());
    }
    
    @Test 
    @DisplayName("AI生成エラーテスト")
    void generateReport_AIError() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        
        ReportGenerationRequestDto request = ReportGenerationRequestDto.builder()
            .userId(userId)
            .reportDate(reportDate)
            .build();
        
        when(reportUseCase.getReportByDate(userId, reportDate))
            .thenReturn(Optional.empty());
        
        // AI生成でエラーが発生
        when(reportGenerationService.generateReport(any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("AI generation failed"));
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("エラーが発生しました");
        
        // Verify - createReportは呼ばれないはず
        verify(reportUseCase, never()).createReport(any());
    }
}
