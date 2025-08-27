package com.example.backend.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notionテーブル行ドメインエンティティ
 * Notionのテーブル行データを表現する
 */
@Getter
@Builder
@RequiredArgsConstructor
public class NotionTableRow {
    
    private final String id;
    private final String title;
    private final Map<String, Object> properties;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String url;
    
    /**
     * 指定されたプロパティの値を取得
     * 
     * @param propertyName プロパティ名
     * @return プロパティ値（存在しない場合はnull）
     */
    public Object getProperty(String propertyName) {
        return properties != null ? properties.get(propertyName) : null;
    }
    
    /**
     * 指定されたプロパティの値を文字列として取得
     * 
     * @param propertyName プロパティ名
     * @return プロパティ値の文字列表現
     */
    public String getPropertyAsString(String propertyName) {
        Object value = getProperty(propertyName);
        return value != null ? value.toString() : "";
    }
    
    /**
     * 指定されたプロパティの値を日付として取得
     * 
     * @param propertyName プロパティ名
     * @return プロパティ値の日付（変換できない場合はnull）
     */
    public LocalDate getPropertyAsDate(String propertyName) {
        Object value = getProperty(propertyName);
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }
        return null;
    }
    
    /**
     * 指定されたプロパティが存在するかチェック
     * 
     * @param propertyName プロパティ名
     * @return プロパティが存在する場合true
     */
    public boolean hasProperty(String propertyName) {
        return properties != null && properties.containsKey(propertyName);
    }
    
    /**
     * 行が最近作成されたかチェック
     * 
     * @param days 日数
     * @return 指定日数以内に作成された場合true
     */
    public boolean isRecentlyCreated(int days) {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(days));
    }
    
    /**
     * ステータスプロパティをチェック（完了状態かどうか）
     * 
     * @return 完了状態の場合true
     */
    public boolean isCompleted() {
        String status = getPropertyAsString("Status");
        return "completed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status);
    }
}