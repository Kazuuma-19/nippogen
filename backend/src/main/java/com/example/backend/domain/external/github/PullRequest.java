package com.example.backend.domain.external.github;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * プルリクエストドメインエンティティ
 * GitHubのプルリクエスト情報を表現する
 */
@Getter
@Builder
public class PullRequest {
    
    public enum Status {
        OPEN,
        CLOSED,
        MERGED
    }
    
    private final Long id;
    private final Integer number;
    private final String title;
    private final String body;
    private final String state;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime mergedAt;
    private final String baseBranch;
    private final String headBranch;
    private final List<String> reviewers;
    private final Integer additions;
    private final Integer deletions;
    private final Integer changedFiles;
    private final String htmlUrl;
    
    /**
     * プルリクエストがマージ済みかチェック
     * 
     * @return マージ済みの場合true
     */
    public boolean isMerged() {
        return mergedAt != null;
    }
    
    /**
     * プルリクエストが指定日に作成されたかチェック
     * 
     * @param date 対象日
     * @return 指定日に作成された場合true
     */
    public boolean isCreatedOnDate(LocalDate date) {
        if (createdAt == null) {
            return false;
        }
        return createdAt.toLocalDate().equals(date);
    }
    
    /**
     * プルリクエストが指定日にマージされたかチェック
     * 
     * @param date 対象日
     * @return 指定日にマージされた場合true
     */
    public boolean isMergedOnDate(LocalDate date) {
        if (mergedAt == null) {
            return false;
        }
        return mergedAt.toLocalDate().equals(date);
    }
    
    /**
     * プルリクエストが指定日に更新されたかチェック
     * 
     * @param date 対象日
     * @return 指定日に更新された場合true
     */
    public boolean isUpdatedOnDate(LocalDate date) {
        if (updatedAt == null) {
            return false;
        }
        return updatedAt.toLocalDate().equals(date);
    }
    
    /**
     * プルリクエストの変更行数合計を取得
     * 
     * @return 変更行数合計
     */
    public int getTotalChanges() {
        return (additions != null ? additions : 0) + (deletions != null ? deletions : 0);
    }
}
