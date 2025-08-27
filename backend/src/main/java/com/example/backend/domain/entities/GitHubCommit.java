package com.example.backend.domain.entities;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GitHubコミットドメインエンティティ
 * GitHubのコミット情報を表現する
 */
@Getter
@Builder
public class GitHubCommit {
    
    private final String sha;
    private final String message;
    private final String author;
    private final String authorEmail;
    private final LocalDateTime date;
    private final Integer additions;
    private final Integer deletions;
    private final Integer total;
    private final String htmlUrl;
    
    /**
     * コミットが指定日に作成されたかチェック
     * 
     * @param targetDate 対象日
     * @return 指定日に作成された場合true
     */
    public boolean isCommittedOnDate(LocalDate targetDate) {
        if (date == null) {
            return false;
        }
        return date.toLocalDate().equals(targetDate);
    }
    
    /**
     * コミットの変更行数合計を取得
     * 
     * @return 変更行数合計（追加+削除）
     */
    public int getTotalChanges() {
        return (additions != null ? additions : 0) + (deletions != null ? deletions : 0);
    }
    
    /**
     * コミットメッセージから種別を推定する
     * 
     * @return コミット種別（feat, fix, docs, etc.）
     */
    public String estimateCommitType() {
        if (message == null || message.isEmpty()) {
            return "other";
        }
        
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.startsWith("feat")) {
            return "feat";
        } else if (lowerMessage.startsWith("fix")) {
            return "fix";
        } else if (lowerMessage.startsWith("docs")) {
            return "docs";
        } else if (lowerMessage.startsWith("refactor")) {
            return "refactor";
        } else if (lowerMessage.startsWith("test")) {
            return "test";
        } else if (lowerMessage.startsWith("chore")) {
            return "chore";
        } else {
            return "other";
        }
    }
    
    /**
     * コミット作成者名を取得（authorが空の場合はemailから推定）
     * 
     * @return 作成者名
     */
    public String getDisplayAuthor() {
        if (author != null && !author.isEmpty()) {
            return author;
        }
        
        if (authorEmail != null && !authorEmail.isEmpty()) {
            // email@domain.com -> email
            return authorEmail.split("@")[0];
        }
        
        return "Unknown";
    }
}