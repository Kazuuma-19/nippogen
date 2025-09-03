package com.example.backend.application.usecases;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.common.util.DailyReportMapper;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.application.usecases.reports.ReportGenerationUseCase;
import com.example.backend.application.usecases.reports.ReportUseCase;
import com.example.backend.domain.reports.IReportGenerationService;
import com.example.backend.domain.reports.IDailyReportRepository;
import com.example.backend.domain.reports.DailyReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ReportUseCase reportUseCase;
    
    @Mock
    private IDailyReportRepository dailyReportRepository;
    
    @Mock
    private DailyReportMapper dailyReportMapper;
    
    private ReportGenerationUseCase reportGenerationUseCase;
    
    @BeforeEach
    void setUp() {
        reportGenerationUseCase = new ReportGenerationUseCase(
            reportGenerationService,
            reportUseCase,
            dailyReportRepository,
            dailyReportMapper
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
        when(dailyReportRepository.existsByUserIdAndDate(userId, reportDate))
            .thenReturn(false);
        
        when(reportGenerationService.generateReport(
            eq(userId), eq(reportDate), any(), any(), any(), any()
        )).thenReturn("生成された日報コンテンツ");
        
        UUID reportId = UUID.randomUUID();
        DailyReport savedReport = DailyReport.builder()
            .id(reportId)
            .userId(userId)
            .reportDate(reportDate)
            .finalContent("生成された日報コンテンツ")
            .additionalNotes("テスト用追加メモ")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
            
        when(dailyReportRepository.save(any(DailyReport.class)))
            .thenReturn(savedReport);
        
        DailyReportDto reportDto = DailyReportDto.builder()
            .id(reportId)
            .userId(userId)
            .reportDate(reportDate)
            .finalContent("生成された日報コンテンツ")
            .additionalNotes("テスト用追加メモ")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
            
        when(dailyReportMapper.toDto(savedReport))
            .thenReturn(reportDto);
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getReportId()).isEqualTo(reportId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getReportDate()).isEqualTo(reportDate);
        assertThat(response.getFinalContent()).isEqualTo("生成された日報コンテンツ");
        
        // Verify
        verify(reportGenerationService, times(1)).generateReport(
            eq(userId), eq(reportDate), any(), any(), any(), eq("テスト用追加メモ")
        );
        verify(dailyReportRepository, times(1)).save(any(DailyReport.class));
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
        when(dailyReportRepository.existsByUserIdAndDate(userId, reportDate))
            .thenReturn(true);
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("既に存在しています");
        
        // Verify - AI生成は呼ばれないはず
        verify(reportGenerationService, never()).generateReport(any(), any(), any(), any(), any(), any());
        verify(dailyReportRepository, never()).save(any(DailyReport.class));
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
        verify(dailyReportRepository, never()).existsByUserIdAndDate(any(), any());
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
        
        when(dailyReportRepository.existsByUserIdAndDate(userId, reportDate))
            .thenReturn(false);
        
        // AI生成でエラーが発生
        when(reportGenerationService.generateReport(any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("AI generation failed"));
        
        // When
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("エラーが発生しました");
        
        // Verify - saveは呼ばれないはず
        verify(dailyReportRepository, never()).save(any(DailyReport.class));
    }
}