package com.example.backend.application.usecases.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.domain.reports.DailyReport;
import com.example.backend.domain.reports.IDailyReportRepository;
import com.example.backend.presentation.dto.reports.DailyReportListResponseDto;
import com.example.backend.presentation.dto.reports.DailyReportUpdateRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 日報ユースケース
 * 日報に関するビジネスロジックを実装
 */
@Service
@RequiredArgsConstructor
public class ReportUseCase {
    
    private final IDailyReportRepository dailyReportRepository;
    
    
    /**
     * 日報を更新
     * 
     * @param reportId 日報ID
     * @param request 更新リクエスト
     * @return 更新された日報DTO
     * @throws IllegalArgumentException 日報が見つからない場合
     * @throws IllegalStateException 編集不可能な状態の場合
     */
    @Transactional
    public DailyReportDto updateReport(UUID reportId, DailyReportUpdateRequestDto request) {
        Optional<DailyReport> optionalReport = dailyReportRepository.findById(reportId);
        if (optionalReport.isEmpty()) {
            throw new IllegalArgumentException("Report not found: " + reportId);
        }
        
        DailyReport existingReport = optionalReport.get();
        
        DailyReport updatedReport = DailyReport.builder()
                .id(existingReport.getId())
                .userId(existingReport.getUserId())
                .reportDate(existingReport.getReportDate())
                .rawData(existingReport.getRawData())
                .finalContent(request.getFinalContent())
                .additionalNotes(request.getAdditionalNotes() != null ? request.getAdditionalNotes() : existingReport.getAdditionalNotes())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        DailyReport saved = dailyReportRepository.save(updatedReport);
        return convertToDto(saved);
    }
    
    
    /**
     * 日付範囲で日報一覧を取得
     * 
     * @param userId ユーザーID
     * @param startDate 開始日（オプション）
     * @param endDate 終了日（オプション）
     * @return 日報一覧レスポンス
     */
    @Transactional(readOnly = true)
    public DailyReportListResponseDto getReportsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<DailyReport> reports;
        
        if (startDate != null && endDate != null) {
            reports = dailyReportRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        } else {
            reports = dailyReportRepository.findByUserId(userId);
        }
        
        List<DailyReportDto> reportDtos = reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        String dateRange = buildDateRangeString(startDate, endDate);
        
        return DailyReportListResponseDto.builder()
                .reports(reportDtos)
                .totalCount(reportDtos.size())
                .dateRange(dateRange)
                .build();
    }
    
    /**
     * 特定日の日報を取得
     * 
     * @param userId ユーザーID
     * @param date 日付
     * @return 日報DTO（存在しない場合はOptional.empty()）
     */
    @Transactional(readOnly = true)
    public Optional<DailyReportDto> getReportByDate(UUID userId, LocalDate date) {
        return dailyReportRepository.findByUserIdAndDate(userId, date)
                .map(this::convertToDto);
    }
    
    
    /**
     * 日報を削除
     * 
     * @param reportId 日報ID
     * @return 削除された場合true
     */
    @Transactional
    public boolean deleteReport(UUID reportId) {
        return dailyReportRepository.deleteById(reportId);
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
                .additionalNotes(report.getAdditionalNotes())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
    
    /**
     * 日付範囲文字列を構築
     * 
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 日付範囲文字列
     */
    private String buildDateRangeString(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return startDate + " to " + endDate;
        } else if (startDate != null) {
            return "from " + startDate;
        } else if (endDate != null) {
            return "until " + endDate;
        }
        return null;
    }
}
