package com.example.backend.presentation.dto.reports;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

import com.example.backend.application.dto.reports.DailyReportDto;

/**
 * 日報リスト取得レスポンスDTO
 * 日報一覧取得時のレスポンス情報を転送するためのDTO
 */
@Getter
@Builder
public class DailyReportListResponseDto {
    private final List<DailyReportDto> reports;
    private final int totalCount;
    private final String dateRange;
}
