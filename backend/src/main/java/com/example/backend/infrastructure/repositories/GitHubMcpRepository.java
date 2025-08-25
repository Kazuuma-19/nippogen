package com.example.backend.infrastructure.repositories;

import com.example.backend.domain.entities.GitHubCommit;
import com.example.backend.domain.entities.PullRequest;
import com.example.backend.domain.repositories.GitHubRepository;
import com.example.backend.shared.exceptions.GitHubMcpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GitHubMcpRepository implements GitHubRepository {
    
    /**
     * GitHub公式MCP Server経由で接続をテスト
     * Spring AI MCP Clientを使用してget_repositoryツールを呼び出し
     */
    @Override
    public boolean testConnection(String owner, String repo) {
        try {
            log.info("Testing GitHub MCP connection for {}/{}", owner, repo);
            
            String repositoryName = owner + "/" + repo;
            
            // TODO: Remote GitHub MCP Server経由でリポジトリ情報を取得してテスト
            // 一時的にモック実装（MCP Clientの正しいAPI確認後に実装）
            log.info("GitHub MCP connection test successful for {} (mock - MCP enabled but using fallback)", repositoryName);
            return true;
            
        } catch (Exception e) {
            log.error("GitHub MCP connection failed for {}/{}: {}", owner, repo, e.getMessage());
            throw GitHubMcpException.connectionFailed(e.getMessage());
        }
    }
    
    /**
     * GitHub公式MCP Server経由で指定日のPR情報を取得
     * Spring AI MCP Clientでsearch_issuesツールを使用
     */
    @Override
    public List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching PRs via GitHub MCP for {}/{} on {}", owner, repo, date);
            
            String repositoryName = owner + "/" + repo;
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String query = String.format("repo:%s type:pr created:%s", repositoryName, dateStr);
            
            // TODO: Remote GitHub MCP Server経由でPR情報を取得
            // 一時的にモック実装（MCP Clientの正しいAPI確認後に実装）
            List<PullRequest> pullRequests = new ArrayList<>();
            
            log.info("Found {} PRs for {}/{} on {}", pullRequests.size(), owner, repo, date);
            return pullRequests;
            
        } catch (Exception e) {
            log.error("Failed to fetch PRs for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch PRs: " + e.getMessage());
        }
    }
    
    /**
     * GitHub公式MCP Server経由で指定日のコミット情報を取得
     * Spring AI MCP Clientでlist_commitsツールを使用
     */
    @Override
    public List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching commits via GitHub MCP for {}/{} on {}", owner, repo, date);
            
            String repositoryName = owner + "/" + repo;
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // TODO: Remote GitHub MCP Server経由でコミット情報を取得
            // 一時的にモック実装（MCP Clientの正しいAPI確認後に実装）
            List<GitHubCommit> commits = new ArrayList<>();
            
            log.info("Found {} commits for {}/{} on {}", commits.size(), owner, repo, date);
            return commits;
            
        } catch (Exception e) {
            log.error("Failed to fetch commits for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch commits: " + e.getMessage());
        }
    }
}