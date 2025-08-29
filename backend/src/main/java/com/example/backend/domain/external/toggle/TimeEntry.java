package com.example.backend.domain.external.toggle;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * ToggleTrack時間記録ドメインエンティティ
 * ToggleTrackの時間記録情報を表現する
 */
@Getter
@Builder
public class TimeEntry {
    
    private final Long id;
    private final String description;
    private final String projectName;
    private final Long projectId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Long durationSeconds;
    private final boolean billable;
    private final String userId;
    private final String workspaceName;
    private final Long workspaceId;
    private final String[] tags;
    
    /**
     * 時間記録が指定日に開始されたかチェック
     * 
     * @param date 対象日
     * @return 指定日に開始された場合true
     */
    public boolean isStartedOnDate(LocalDate date) {
        if (startTime == null) {
            return false;
        }
        return startTime.toLocalDate().equals(date);
    }
    
    /**
     * 時間記録が指定日に終了したかチェック
     * 
     * @param date 対象日
     * @return 指定日に終了した場合true
     */
    public boolean isEndedOnDate(LocalDate date) {
        if (endTime == null) {
            return false;
        }
        return endTime.toLocalDate().equals(date);
    }
    
    /**
     * 時間記録が現在実行中かチェック
     * 
     * @return 実行中の場合true
     */
    public boolean isRunning() {
        return endTime == null;
    }
    
    /**
     * 時間記録の期間を取得
     * 
     * @return 期間（Duration）
     */
    public Duration getDuration() {
        if (durationSeconds != null) {
            return Duration.ofSeconds(durationSeconds);
        }
        
        if (startTime != null) {
            LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
            return Duration.between(startTime, end);
        }
        
        return Duration.ZERO;
    }
    
    /**
     * 時間記録の期間を時間単位で取得
     * 
     * @return 期間（時間）
     */
    public double getDurationInHours() {
        Duration duration = getDuration();
        return duration.toSeconds() / 3600.0;
    }
    
    /**
     * 時間記録が特定のタグを含むかチェック
     * 
     * @param tag チェックするタグ
     * @return タグが含まれている場合true
     */
    public boolean hasTag(String tag) {
        if (tags == null || tag == null) {
            return false;
        }
        
        for (String t : tags) {
            if (tag.equals(t)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * プロジェクトが設定されているかチェック
     * 
     * @return プロジェクトが設定されている場合true
     */
    public boolean hasProject() {
        return projectId != null && projectName != null && !projectName.trim().isEmpty();
    }
    
    /**
     * 説明が設定されているかチェック
     * 
     * @return 説明が設定されている場合true
     */
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }
}
