package com.example.backend.presentation.controllers.reports;

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
    private final String status;
    
    /**
     * レポートが存在するかチェック
     * 
     * @return レポートが存在する場合true
     */
    public boolean hasReports() {
        return reports != null && !reports.isEmpty();
    }
    
    /**
     * 実際のレポート数を取得
     * 
     * @return レポート数
     */
    public int getActualCount() {
        return reports != null ? reports.size() : 0;
    }
    
    /**
     * ページネーション情報があるかチェック
     * 
     * @return totalCountが設定されている場合true
     */
    public boolean hasPagination() {
        return totalCount > 0;
    }
}
