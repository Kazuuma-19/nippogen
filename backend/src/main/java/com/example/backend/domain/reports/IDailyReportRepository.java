package com.example.backend.domain.reports;

import com.example.backend.domain.reports.DailyReport;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 日報リポジトリインターフェース
 * 日報のデータアクセス操作を定義
 */
public interface IDailyReportRepository {
    
    /**
     * 日報を保存
     * 
     * @param report 保存する日報エンティティ
     * @return 保存された日報エンティティ
     */
    DailyReport save(DailyReport report);
    
    /**
     * IDで日報を取得
     * 
     * @param id 日報ID
     * @return 日報エンティティ（存在しない場合はOptional.empty()）
     */
    Optional<DailyReport> findById(UUID id);
    
    /**
     * ユーザーIDと日付で日報を取得
     * 
     * @param userId ユーザーID
     * @param reportDate 日報日付
     * @return 日報エンティティ（存在しない場合はOptional.empty()）
     */
    Optional<DailyReport> findByUserIdAndDate(UUID userId, LocalDate reportDate);
    
    /**
     * ユーザーIDで日報リストを取得（日付降順）
     * 
     * @param userId ユーザーID
     * @return 日報エンティティのリスト
     */
    List<DailyReport> findByUserId(UUID userId);
    
    /**
     * ユーザーIDと日付範囲で日報リストを取得
     * 
     * @param userId ユーザーID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 日報エンティティのリスト（日付降順）
     */
    List<DailyReport> findByUserIdAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate);
    
    
    /**
     * 日報を削除
     * 
     * @param id 削除する日報ID
     * @return 削除された場合true
     */
    boolean deleteById(UUID id);
    
    /**
     * 日報の存在確認
     * 
     * @param id 日報ID
     * @return 存在する場合true
     */
    boolean existsById(UUID id);
    
    /**
     * ユーザーIDと日付で日報の存在確認
     * 
     * @param userId ユーザーID
     * @param reportDate 日報日付
     * @return 存在する場合true
     */
    boolean existsByUserIdAndDate(UUID userId, LocalDate reportDate);
    
    /**
     * ユーザーの日報総数を取得
     * 
     * @param userId ユーザーID
     * @return 日報総数
     */
    long countByUserId(UUID userId);
    
}
