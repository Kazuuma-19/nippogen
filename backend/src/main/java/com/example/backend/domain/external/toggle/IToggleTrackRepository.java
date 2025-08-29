package com.example.backend.domain.external.toggle;

import com.example.backend.domain.external.toggle.TimeEntry;

import java.util.List;

/**
 * ToggleTrack統合のためのリポジトリインターフェース
 * インフラストラクチャ層で実装される
 */
public interface IToggleTrackRepository {
    
    /**
     * ToggleTrack接続をテストする
     * 
     * @return 接続成功時true
     */
    boolean testConnection();
    
    /**
     * 今日の時間記録を取得
     * 自動的に今日の日付を使用してAPIコールする
     * 
     * @return 今日の時間記録のリスト
     */
    List<TimeEntry> getTodayTimeEntries();
}
