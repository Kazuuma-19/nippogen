package com.example.backend.common.util;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.domain.reports.DailyReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DailyReportエンティティとDTOの変換を担当するMapper
 * 重複する変換ロジックを統一化
 */
@Component
@RequiredArgsConstructor
public class DailyReportMapper {
    
    /**
     * DailyReportエンティティをDTOに変換
     * 
     * @param report 日報エンティティ
     * @return 日報DTO
     */
    public DailyReportDto toDto(DailyReport report) {
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
     * DailyReportエンティティのリストをDTOリストに変換
     * 
     * @param reports 日報エンティティリスト
     * @return 日報DTOリスト
     */
    public List<DailyReportDto> toDtoList(List<DailyReport> reports) {
        return reports.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}