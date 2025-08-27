package com.example.backend.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notionページドメインエンティティ
 * Notionのページ情報を表現する
 */
@Getter
@Builder
@RequiredArgsConstructor
public class NotionPage {
    
    private final String id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String url;
    private final List<String> tags;
    private final String status;
    
    /**
     * ページが最近更新されたかチェック
     * 
     * @param hours 時間数
     * @return 指定時間以内に更新された場合true
     */
    public boolean isRecentlyUpdated(int hours) {
        if (updatedAt == null) {
            return false;
        }
        return updatedAt.isAfter(LocalDateTime.now().minusHours(hours));
    }
    
    /**
     * ページにタグが含まれているかチェック
     * 
     * @param tag チェックするタグ
     * @return タグが含まれている場合true
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }
    
    /**
     * ページのステータスが完了状態かチェック
     * 
     * @return 完了状態の場合true
     */
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status);
    }
    
    /**
     * ページのコンテンツが空でないかチェック
     * 
     * @return コンテンツがある場合true
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
}