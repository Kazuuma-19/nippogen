package com.example.backend.domain.repositories;

import com.example.backend.domain.entities.GitHubCommit;
import com.example.backend.domain.entities.PullRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * GitHub統合のためのリポジトリインターフェース
 * インフラストラクチャ層で実装される
 */
public interface GitHubRepository {
    
    /**
     * GitHub接続をテストする
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @return 接続成功時true
     */
    boolean testConnection(String owner, String repo);
    
    /**
     * 指定日に作成・更新・マージされたPRを取得
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @param date 対象日
     * @return プルリクエスト一覧
     */
    List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date);
    
    /**
     * 指定日に作成されたコミットを取得
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @param date 対象日
     * @return コミット一覧
     */
    List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date);
}